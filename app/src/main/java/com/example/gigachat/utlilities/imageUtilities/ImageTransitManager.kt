package com.example.gigachat.utlilities.imageUtilities

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.example.gigachat.utlilities.preferenceManagers.UserProfilePreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class ImageTransitManager(val context: Context) {

    private val UserProfilePreferenceManager = UserProfilePreferenceManager(context)
    fun uploadImageToStorage(
        imageUri: Uri,
        resizedBitmap: Bitmap,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("Profile/${UUID.randomUUID()}")

        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val data = outputStream.toByteArray()

        imageRef.putBytes(data)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()

                    onSuccess(imageUrl)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun deleteImageFromStorage(
        imageUri: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri.toString())

        storageRef.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

     fun UpdateProfileImageInfo(
        imageUrl: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val data = HashMap<String, Any>()
        data["ProfileUrl"] = imageUrl

        val userDocRef =
            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid.toString())
        userDocRef.update(data)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }



}