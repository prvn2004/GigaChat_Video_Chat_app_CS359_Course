package com.example.gigachat.Utlilities.permissionManagers

import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.ktx.actionCodeSettings

class PermissionRequester(val context: Context, val activity: Activity) {
     fun requestContactPermission(contactPermissionCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            contactPermissionCode
        )
    }
}