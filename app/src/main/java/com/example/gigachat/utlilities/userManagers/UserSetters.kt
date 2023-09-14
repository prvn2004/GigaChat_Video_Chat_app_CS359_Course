package com.example.gigachat.utlilities.userManagers

import android.content.Context
import com.example.gigachat.utlilities.preferenceManagers.UserProfilePreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserSetters(val context: Context)  {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userProfilePreferenceManager = UserProfilePreferenceManager(context)

    fun setUserName(userName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userDocRef = firestore.collection("Users").document(auth.currentUser?.uid ?: "")
        firestore.runTransaction { transaction ->
            val userDoc = transaction.get(userDocRef)
            if (userDoc.exists()) {
                transaction.update(userDocRef, "UserName", userName)
                userProfilePreferenceManager.saveUserName(userName)
            }
            null
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

    fun setUserBio(userBio: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userDocRef = firestore.collection("Users").document(auth.currentUser?.uid ?: "")
        firestore.runTransaction { transaction ->
            val userDoc = transaction.get(userDocRef)
            if (userDoc.exists()) {
                transaction.update(userDocRef, "UserBio", userBio)
                userProfilePreferenceManager.saveUserBio(userBio)
            }
            null
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

    fun setProfileUrl(profileUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userDocRef = firestore.collection("Users").document(auth.currentUser?.uid ?: "")
        firestore.runTransaction { transaction ->
            val userDoc = transaction.get(userDocRef)
            if (userDoc.exists()) {
                transaction.update(userDocRef, "ProfileUrl", profileUrl)
                userProfilePreferenceManager.saveUserImage(profileUrl)
            }
            null
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }
}