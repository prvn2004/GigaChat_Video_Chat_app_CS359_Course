package com.example.gigachat.Utlilities.preferenceManagers

import android.content.Context
import android.content.SharedPreferences

class UserProfilePreferenceManager(context: Context) {
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

    fun clearPhoneNumber() {
        editor.remove("User_Phone_Number")
        editor.apply()
    }

}