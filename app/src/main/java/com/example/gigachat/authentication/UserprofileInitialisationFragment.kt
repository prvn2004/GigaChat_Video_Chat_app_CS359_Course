package com.example.gigachat.authentication

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gigachat.MainActivity
import com.example.gigachat.utlilities.imageUtilities.ImageEditors
import com.example.gigachat.utlilities.permissionManagers.PermissionChecker
import com.example.gigachat.utlilities.permissionManagers.PermissionRequester
import com.example.gigachat.utlilities.preferenceManagers.AuthenticationPreferenceManager
import com.example.gigachat.utlilities.preferenceManagers.UserProfilePreferenceManager
import com.example.gigachat.databinding.FragmentUserprofileInitialisationBinding
import com.example.gigachat.utlilities.imageUtilities.ImageTransitManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

class UserprofileInitialisationFragment : Fragment() {

    private lateinit var PermissionChecker: PermissionChecker
    private lateinit var PermissionRequester: PermissionRequester
    private lateinit var UserProfilePreferenceManager: UserProfilePreferenceManager
    private lateinit var AuthenticationPreferenceManager: AuthenticationPreferenceManager
    private lateinit var imageEditors: ImageEditors
    private lateinit var ImageTransitManager: ImageTransitManager

    private val contactPermissionCode = 101

    private lateinit var binding: FragmentUserprofileInitialisationBinding


    private var firestore = FirebaseFirestore.getInstance()
    private var firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var progressDialog: ProgressDialog

    val PICK_IMAGE_REQUEST = 101


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserprofileInitialisationBinding.inflate(inflater, container, false)

        val view = binding.root

        progressDialog = ProgressDialog(requireActivity())

        PermissionChecker = PermissionChecker(requireContext())
        PermissionRequester = PermissionRequester(requireContext(), requireActivity())
        UserProfilePreferenceManager = UserProfilePreferenceManager(requireContext())
        AuthenticationPreferenceManager = AuthenticationPreferenceManager(requireContext())
        imageEditors = ImageEditors(requireContext())
        ImageTransitManager = ImageTransitManager(requireContext())

        CheckContactPermission()

        binding.complete.setOnClickListener {
            progressDialog.setMessage("Wait for some time")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val UserName = binding.EnterName.text.toString()
            val UserBio = binding.enterBio.text.toString()
            UpdateUserInfo(UserName, UserBio)
        }

        binding.profileImage.setOnClickListener {
            openImagePicker()
        }

        return view
    }

    fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data

            binding.profileImage.setImageURI(selectedImageUri)

            // Upload the selected image to Firebase Storage
            ImageTransitManager.uploadImageToStorage(selectedImageUri!!,
                imageEditors.resizeImage(selectedImageUri!!),
                onSuccess = {
                    UserProfilePreferenceManager.saveUserImage(it)
                    ImageTransitManager.UpdateProfileImageInfo(it, onSuccess = {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                    }, onFailure = {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                    })
                },
                onFailure = {
                    Log.e(ContentValues.TAG, "Error uploading resized image: ${it.message}")
                })
        }
    }

    fun CheckContactPermission() {
        if (!PermissionChecker.CheckContactPermission()) {
            showPermissionExplanationDialog()
        }
    }


    private fun UpdateUserInfo(userName: String, userBio: String) {
        val data = HashMap<String, Any>()
        data["UserName"] = userName
        data["UserBio"] = userBio
        data["AccountStatus"] = true

        val userDocRef = firestore.collection("Users").document(firebaseAuth.currentUser?.uid ?: "")
        userDocRef.update(data).addOnSuccessListener {
            UserProfilePreferenceManager.saveUserName(userName)
            UserProfilePreferenceManager.saveUserBio(userBio)
            AuthenticationPreferenceManager.saveCurrentProfileStatus(true)

            MoveToActivity()
            Log.d(TAG, "Document updated successfully!")
            if (progressDialog.isShowing) progressDialog.dismiss()
        }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
                if (progressDialog.isShowing) progressDialog.dismiss()
            }
    }


    private fun showPermissionExplanationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Contact Permission")
        alertDialogBuilder.setMessage(
            "This app requires access to your contacts to function properly. " +
                    "Do you want to allow access to your contacts?"
        )
        alertDialogBuilder.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            PermissionRequester.requestContactPermission(contactPermissionCode)
        }
        alertDialogBuilder.setNegativeButton("Decline") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        val dialog = alertDialogBuilder.create()
        dialog.show()
    }

    fun MoveToActivity() {
        val intent = Intent(requireActivity(), MainActivity()::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}