package com.example.advan.presentation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val user = auth.currentUser
    val userId = user?.uid

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var gender by remember { mutableStateOf("") }
    var profilePicUrl by remember { mutableStateOf<String?>(user?.photoUrl?.toString()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadProfilePicture(userId, it) { downloadUrl ->
                profilePicUrl = downloadUrl
            }
        }
    }

    // Load Firestore info if exists
    LaunchedEffect(userId) {
        userId?.let {
            firestore.collection("users").document(it).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    firstName = doc.getString("firstName") ?: ""
                    lastName = doc.getString("lastName") ?: ""
                    phoneNumber = doc.getString("phoneNumber") ?: ""
                    gender = doc.getString("gender") ?: ""
                    profilePicUrl = doc.getString("profilePicUrl") ?: profilePicUrl
                } else {
                    // Fallback to FirebaseAuth data
                    val nameParts = user?.displayName?.split(" ") ?: listOf()
                    firstName = nameParts.getOrNull(0) ?: ""
                    lastName = nameParts.getOrNull(1) ?: ""
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Edit Profile", fontSize = 28.sp, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))

        // 👤 Profile Picture
        Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(120.dp)) {
            if (!profilePicUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(profilePicUrl),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(120.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                val initials = "${firstName.firstOrNull()?.uppercaseChar() ?: ""}${lastName.firstOrNull()?.uppercaseChar() ?: ""}"
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Text(text = initials, color = MaterialTheme.colorScheme.onPrimary, fontSize = 28.sp)
                }
            }

            IconButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Upload", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            userId?.let {
                val userMap = mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "phoneNumber" to phoneNumber,
                    "email" to email,
                    "gender" to gender,
                    "profilePicUrl" to profilePicUrl
                )
                firestore.collection("users").document(it).set(userMap)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                        Log.e("EditProfile", "Update Error", it)
                    }
            }
        }) {
            Text("Save Changes")
        }
    }
}

fun uploadProfilePicture(userId: String?, imageUri: Uri, onSuccess: (String) -> Unit) {
    userId?.let { uid ->
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$uid.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    FirebaseFirestore.getInstance().collection("users").document(uid)
                        .update("profilePicUrl", downloadUrl)
                        .addOnSuccessListener {
                            onSuccess(downloadUrl)
                        }
                }
            }
    }
}
