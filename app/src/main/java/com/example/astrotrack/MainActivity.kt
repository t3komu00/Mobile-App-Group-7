package com.example.astrotrack

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private val TAG = "FirebaseTest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val email = "test@example.com"
        val password = "password123"

        val auth = FirebaseAuth.getInstance()

        // First try to create user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                Log.d(TAG, "✅ Email sign-up successful: ${authResult.user?.uid}")

                // Write to Firestore
                val db = FirebaseFirestore.getInstance()
                val data = hashMapOf(
                    "name" to "AstroTrack User",
                    "status" to "Signed up and connected to Firestore",
                    "timestamp" to System.currentTimeMillis()
                )

                db.collection("users").add(data)
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ Firestore write successful: ${it.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "❌ Firestore write failed", e)
                    }
            }
            .addOnFailureListener { signUpError ->
                Log.e(TAG, "❌ Email sign-up failed", signUpError)

                // If already registered, try sign-in instead
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ Signed in existing user: ${it.user?.uid}")
                    }
                    .addOnFailureListener { signInError ->
                        Log.e(TAG, "❌ Sign-in also failed", signInError)
                    }
            }

        // UI content
        setContent {
            Text("Signing in with Email/Password...")
        }
    }
}
