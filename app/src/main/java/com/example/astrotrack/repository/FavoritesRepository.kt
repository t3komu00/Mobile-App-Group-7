package com.example.astrotrack.repository

import com.example.astrotrack.model.ApodItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects

class FavoritesRepository {
    private val db = FirebaseFirestore.getInstance()
    private val favoritesRef = db.collection("favorites")

    fun addToFavorites(item: ApodItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        favoritesRef.document(item.date).set(item)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun removeFromFavorites(item: ApodItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        favoritesRef.document(item.date).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getFavorites(onResult: (List<ApodItem>) -> Unit) {
        favoritesRef.get().addOnSuccessListener { snapshot ->
            val list = snapshot.toObjects(ApodItem::class.java)
            onResult(list)
        }
    }
}
