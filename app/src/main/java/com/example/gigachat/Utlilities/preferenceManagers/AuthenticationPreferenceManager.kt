package com.example.gigachat.Utlilities.preferenceManagers

import android.content.Context
import android.content.SharedPreferences

class AuthenticationPreferenceManager(context: Context){
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "Authentication",
        Context.MODE_PRIVATE
    )
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    fun saveCurrentAuthStatus(status: Boolean) {
        editor.putBoolean("Current_Auth_Status", status)
        editor.apply()
    }

    fun getCurrentAuthStatus(): Boolean {
        return sharedPreferences.getBoolean("Current_Auth_Status", false)
    }

    fun saveCurrentProfileStatus(status: Boolean) {
        editor.putBoolean("Current_Profile_Status", status)
        editor.apply()
    }

    fun getCurrentProfileStatus(): Boolean {
        return sharedPreferences.getBoolean("Current_Profile_Status", false)
    }

    fun clearCurrentAuthStatus() {
        editor.remove("Current_Auth_Status")
        editor.apply()
    }

    fun clearCurrentProfileStatus() {
        editor.remove("Current_Profile_Status")
        editor.apply()
    }

    fun clearAllAuthPreferences(){
        editor.remove("Current_Auth_Status")
        editor.remove("Current_Profile_Status")
        editor.remove("User_Phone_Number")
        editor.apply()
    }
}