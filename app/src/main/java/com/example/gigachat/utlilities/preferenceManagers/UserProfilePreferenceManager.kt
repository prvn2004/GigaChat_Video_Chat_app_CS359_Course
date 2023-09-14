package com.example.gigachat.utlilities.preferenceManagers

import android.content.Context
import android.content.SharedPreferences

class UserProfilePreferenceManager(val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "Authentication",
        Context.MODE_PRIVATE
    )
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    fun savePhoneNumber(phoneNumber: String) {
        editor.putString("User_Phone_Number", phoneNumber)
        editor.apply()
    }

    fun getPhoneNumber(): String {
        return sharedPreferences.getString("User_Phone_Number", "").toString()
    }

    fun saveUserName(UserName: String) {
        editor.putString("User_Name", UserName)
        editor.apply()
    }

    fun getUserName(): String {
        return sharedPreferences.getString("User_Name", "").toString() ?: ""
    }

    fun saveUserBio(UserBio: String) {
        editor.putString("User_Bio", UserBio)
        editor.apply()
    }

    fun getUserBio(): String {
        return sharedPreferences.getString("User_Bio", "").toString() ?: ""
    }

    fun saveUserImage(UserImage : String){
        editor.putString("User_Image", UserImage)
        editor.apply()
    }

    fun getUserImage() : String{
        return sharedPreferences.getString("User_Image", "").toString() ?: ""
    }

    fun clearAllPref() {
        editor.remove("User_Phone_Number")
        editor.remove("User_Name")
        editor.remove("User_Bio")
        editor.remove("User_Image")
        editor.apply()
    }
}