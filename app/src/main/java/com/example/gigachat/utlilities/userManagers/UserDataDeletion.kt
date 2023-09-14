package com.example.gigachat.utlilities.userManagers

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserDataDeletion {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    interface DeleteCallback {
        fun onSuccess()
        fun onFailure(exception: Exception)
    }

    suspend fun deleteUserData(uid: String, callback: DeleteCallback) {
        try {
            firestore.collection("Users")
                .document(uid)
                .delete()
                .await()

            auth.currentUser?.delete()?.await()

            callback.onSuccess()
        } catch (e: Exception) {
            callback.onFailure(e)
        }
    }
}

