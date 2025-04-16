package com.example.astrotrack.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.WorkManager
import com.example.astrotrack.MainActivity
import com.example.astrotrack.datastore.NotificationPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val notificationPrefs = remember { NotificationPreferences(context) }
    val notificationsEnabled by notificationPrefs.notificationsEnabledFlow.collectAsState(initial = true)

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Brightness6, contentDescription = "Dark Mode")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Dark Mode", modifier = Modifier.weight(1f))
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onThemeToggle() }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Notifications", modifier = Modifier.weight(1f))
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = {
                        scope.launch {
                            notificationPrefs.setNotificationsEnabled(it)
                            if (it) {
                                Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show()
                                (context as? MainActivity)?.scheduleDailyReminder(8, 0)
                            } else {
                                Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
                                WorkManager.getInstance(context).cancelUniqueWork("daily_reminder")
                            }
                        }
                    }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Account")
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = Color.Red,
                        shape = MaterialTheme.shapes.medium
                    )
                ) {
                    Text("Delete Account", color = Color.Red)
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Confirm Deletion") },
                    text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                            val user = FirebaseAuth.getInstance().currentUser
                            user?.let {
                                FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(it.uid)
                                    .delete()
                                    .addOnSuccessListener {
                                        user.delete().addOnCompleteListener { task ->
                                            val message = if (task.isSuccessful)
                                                "Account deleted"
                                            else
                                                "Failed to delete account"
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }) {
                            Text("Delete", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
