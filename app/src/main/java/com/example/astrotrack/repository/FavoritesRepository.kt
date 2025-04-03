package com.example.astrotrack.repository

import com.example.astrotrack.model.ApodItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects

class FavoritesRepository {
    private val db = FirebaseFirestore.getInstance()

    private fun getUserFavoritesRef() =
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).collection("favorites")
        }

    fun addToFavorites(item: ApodItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        getUserFavoritesRef()?.document(item.date)?.set(item)
            ?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { onFailure(it) }
    }

    fun removeFromFavorites(item: ApodItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        getUserFavoritesRef()?.document(item.date)?.delete()
            ?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { onFailure(it) }
    }

    fun getFavorites(onResult: (List<ApodItem>) -> Unit) {
        getUserFavoritesRef()?.get()?.addOnSuccessListener { snapshot ->
            val list = snapshot.toObjects(ApodItem::class.java)
            onResult(list)
        }
    }
}
