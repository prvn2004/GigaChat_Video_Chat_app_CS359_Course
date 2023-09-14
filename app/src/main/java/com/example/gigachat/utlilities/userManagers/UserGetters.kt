package com.example.gigachat.utlilities.userManagers

import android.content.Context
import com.example.gigachat.utlilities.preferenceManagers.UserProfilePreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserGetters(val context: Context) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    var currentUser = firebaseAuth.currentUser
    val UserProfilePreferenceManager = UserProfilePreferenceManager(context)
    private val firestore = FirebaseFirestore.getInstance()

    fun getName(): String {
        return UserProfilePreferenceManager.getUserName() ?: ""
    }

    fun getPhone(): String {
        return UserProfilePreferenceManager.getPhoneNumber() ?: ""
    }

    fun getBio(): String {
        return UserProfilePreferenceManager.getUserBio() ?: ""
    }

    fun getEmail(): String {
        return currentUser?.email?.toString() ?: ""
    }

    fun getImage(): String{
        return  UserProfilePreferenceManager.getUserImage().toString()
    }

    fun fetchUserDetails(){
        firestore.collection("Users").document(firebaseAuth.currentUser?.uid ?: "").get().addOnSuccessListener {
            UserProfilePreferenceManager.saveUserName(it.getString("UserName").toString() ?: "")
            UserProfilePreferenceManager.saveUserBio(it.getString("UserBio").toString() ?: "")
            UserProfilePreferenceManager.saveUserImage(it.getString("ProfileUrl").toString() ?: "")
        }
    }


    suspend fun fetchCurrentProfileInitializationStatus(): Boolean {
        var fValue: Boolean = false
        withContext(Dispatchers.IO) {
            val document = firestore.collection("Users").document(firebaseAuth.currentUser?.uid.toString()).get().await()

            if (document.exists()) {
                fValue = document.getBoolean("AccountStatus") ?: false
            }
        }

        return fValue
    }

}