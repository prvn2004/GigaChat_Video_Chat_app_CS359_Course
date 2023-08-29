package com.example.gigachat.Utlilities.permissionManagers

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf

class PermissionChecker(val context: Context) {
    fun CheckContactPermission() : Boolean{
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }
}