package com.example.astrotrack.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.astrotrack.ui.theme.LocalCustomColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerColor = LocalCustomColors.current.drawerBackground

    var userInitials by remember { mutableStateOf("U") }
    var profilePicUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val first = doc.getString("firstName") ?: ""
                    val last = doc.getString("lastName") ?: ""
                    userInitials = "${first.firstOrNull() ?: ""}${last.firstOrNull() ?: ""}".uppercase()
                    profilePicUrl = doc.getString("profilePicUrl")
                }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(260.dp),
                drawerContainerColor = drawerColor,
                drawerContentColor = Color.White
            ) {
                Spacer(Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!profilePicUrl.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(profilePicUrl),
                            contentDescription = "Profile Pic",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userInitials,
                                color = Color.White,
                                fontSize = 24.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text("Welcome!", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }

                Spacer(Modifier.height(20.dp))
                Divider(color = Color.White.copy(alpha = 0.3f))

                DrawerMenuItem("Home", Icons.Default.Home) { navController.navigate("main") }
                DrawerMenuItem("Favorites", Icons.Default.Favorite) { navController.navigate("favorites") }
                DrawerMenuItem("Profile", Icons.Default.Person) { navController.navigate("profile") }
                DrawerMenuItem("Settings", Icons.Default.Settings) { navController.navigate("settings") }
                DrawerMenuItem("About", Icons.Default.Info) { navController.navigate("about") }

                Spacer(modifier = Modifier.weight(1f))
                Divider(color = Color.White.copy(alpha = 0.3f))

                DrawerMenuItem("Logout", Icons.Default.ExitToApp) {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(
                            text = "AstroTrack",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                BottomNavigationBar(navController)
            },
            containerColor = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}

@Composable
fun DrawerMenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(
                title,
                fontSize = 16.sp,
                color = Color.White
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White
            )
        },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = Color.White.copy(alpha = 0.15f),
            selectedIconColor = Color.White,
            selectedTextColor = Color.White,
            unselectedIconColor = Color.White,
            unselectedTextColor = Color.White
        )
    )
}
