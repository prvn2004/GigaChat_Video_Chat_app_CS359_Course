package com.example.gigachat.Utlilities

import android.content.Context
import com.example.gigachat.Utlilities.preferenceManagers.AuthenticationPreferenceManager
import com.example.gigachat.Utlilities.preferenceManagers.UserProfilePreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UserInfo(context: Context) {
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

    fun getEmail(): String {
        return currentUser?.email?.toString() ?: ""
    }

//    fun fetchName(){
//        firestore.collection("Users").document(firebaseAuth.currentUser?.uid ?: "").get().addOnSuccessListener {
//            UserProfilePreferenceManager.saveUserName(it.getString("UserName").toString() ?: "")
//        }
//    }

}