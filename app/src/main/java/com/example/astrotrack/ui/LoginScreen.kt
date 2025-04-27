package com.example.astrotrack.ui

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.astrotrack.R
import com.example.astrotrack.viewmodel.ApodViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavController, viewModel: ApodViewModel) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        val context = LocalContext.current
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        var showResetDialog by remember { mutableStateOf(false) }
        var resetEmail by remember { mutableStateOf("") }

        val gso = remember {
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("163607543046-hvi7975m4ilqifbfbnjemmlp51f1hnqj.apps.googleusercontent.com")
                .requestEmail()
                .build()
        }
        val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { authResult ->
                        if (authResult.isSuccessful) {
                            val firebaseUser = auth.currentUser
                            val uid = firebaseUser?.uid

                            uid?.let {
                                val userRef = firestore.collection("users").document(it)

                                userRef.get().addOnSuccessListener { documentSnapshot ->
                                    val userExists = documentSnapshot.exists()

                                    val displayName = firebaseUser?.displayName ?: ""
                                    val nameParts = displayName.split(" ")
                                    val firstName = nameParts.getOrNull(0) ?: ""
                                    val lastName = nameParts.getOrNull(1) ?: ""
                                    val email = firebaseUser?.email
                                    val googlePhotoUrl = firebaseUser?.photoUrl?.toString() ?: ""

                                    // ⚡ If Firestore user already has profilePicUrl, keep it. Otherwise use Google's photo.
                                    val existingPhotoUrl = documentSnapshot.getString("profilePicUrl") ?: googlePhotoUrl

                                    val userMap = mapOf(
                                        "firstName" to firstName,
                                        "lastName" to lastName,
                                        "email" to email,
                                        "profilePicUrl" to existingPhotoUrl
                                    )

                                    userRef.set(userMap)
                                        .addOnSuccessListener {
                                            Log.d("LoginScreen", "Google user data saved or updated.")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("LoginScreen", "Failed to save Google user data.", e)
                                        }

                                    viewModel.refreshFavorites()
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(context, "Google sign-in failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google sign-in error", Toast.LENGTH_SHORT).show()
                Log.e("LoginScreen", "Google Sign-In failed", e)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Login", fontSize = 28.sp, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Password Visibility"
                            )
                        }
                    }
                )

                TextButton(onClick = { showResetDialog = true }) {
                    Text("Forgot Password?", color = MaterialTheme.colorScheme.onBackground)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    viewModel.refreshFavorites()
                                    navController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(context, "Login failed!", Toast.LENGTH_SHORT).show()
                                }
                            }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "or login with",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Sign in with Google",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            val signInIntent = googleSignInClient.signInIntent
                            launcher.launch(signInIntent)
                        }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = {
                    navController.navigate("signup")
                }) {
                    Text(
                        "Don't have an account? Sign Up",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showResetDialog = false
                        resetEmail = ""
                    },
                    title = { Text("Reset Password") },
                    text = {
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Enter your email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (resetEmail.isNotEmpty()) {
                                auth.sendPasswordResetEmail(resetEmail)
                                    .addOnCompleteListener { task ->
                                        Toast.makeText(
                                            context,
                                            if (task.isSuccessful) "Reset email sent!" else "Failed to send reset email",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showResetDialog = false
                                        resetEmail = ""
                                    }
                            }
                        }) {
                            Text("Send")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showResetDialog = false
                            resetEmail = ""
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
