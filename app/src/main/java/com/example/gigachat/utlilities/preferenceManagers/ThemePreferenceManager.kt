package com.example.gigachat.utlilities.preferenceManagers

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class ThemePreferenceManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)

    fun getCurrentTheme(): Int {
        val themeValue = sharedPreferences.getInt("theme", 0)

        return when (themeValue) {
            2 -> AppCompatDelegate.MODE_NIGHT_YES // Dark Theme
            1 -> AppCompatDelegate.MODE_NIGHT_NO // Light Theme
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM // System Default
        }
    }

    fun setTheme(themeValue: Int) {
        // Save the selected theme to shared preferences
        sharedPreferences.edit().putInt("theme", themeValue).apply()
    }
}
