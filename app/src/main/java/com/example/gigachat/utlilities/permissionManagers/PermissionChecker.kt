package com.example.gigachat.utlilities.permissionManagers

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class PermissionChecker(val context: Context) {
    fun CheckContactPermission() : Boolean{
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
    }
}