package com.tuapp.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreDataSource(
    private val firestore: FirebaseFirestore = FirebaseModule.firestore
) {

    suspend fun saveUserData(uid: String, data: Map<String, Any>) {
        firestore.collection("users")
            .document(uid)
            .set(data)
            .await()
    }

    suspend fun getUserData(uid: String): Map<String, Any>? {
        val snapshot = firestore.collection("users")
            .document(uid)
            .get()
            .await()

        return snapshot.data
    }

    suspend fun updateField(uid: String, field: String, value: Any) {
        firestore.collection("users")
            .document(uid)
            .update(field, value)
            .await()
    }

    suspend fun userExists(uid: String): Boolean {
        val doc = firestore.collection("users")
            .document(uid)
            .get()
            .await()

        return doc.exists()
    }
}
