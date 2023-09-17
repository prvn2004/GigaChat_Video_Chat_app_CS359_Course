package com.example.gigachat.settings.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.example.gigachat.R
import com.example.gigachat.utlilities.userManagers.UserGetters
import com.example.gigachat.utlilities.preferenceManagers.UserProfilePreferenceManager
import com.example.gigachat.databinding.FragmentProfileBinding
import com.example.gigachat.settings.SettingsFragment
import com.example.gigachat.utlilities.imageUtilities.ImageEditors
import com.example.gigachat.utlilities.imageUtilities.ImageTransitManager
import com.example.gigachat.utlilities.userManagers.UserSetters

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var userGetter: UserGetters
    private lateinit var userSetters: UserSetters
    private lateinit var userProfilePreferenceManager: UserProfilePreferenceManager
    private lateinit var ImageTransitManager: ImageTransitManager
    private lateinit var imageEditors: ImageEditors

    private lateinit var progressDialog: ProgressDialog

    val PICK_IMAGE_REQUEST = 101
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        userGetter = UserGetters(requireContext())
        userSetters = UserSetters(requireContext())
        userProfilePreferenceManager = UserProfilePreferenceManager(requireContext())
        ImageTransitManager = ImageTransitManager(requireContext())
        imageEditors = ImageEditors(requireContext())

        progressDialog = ProgressDialog(requireActivity())

        fetchProfile()

        binding.goBackButton.setOnClickListener {
            NavigateToPrevFragment(SettingsFragment())
        }

        binding.EditNameConst.setOnClickListener {
            binding.editName.setText(userGetter.getName())
            binding.eventBottomSheet.visibility = View.VISIBLE
        }

        binding.EditBioConst.setOnClickListener {
            binding.editBio.setText(userGetter.getBio())
            binding.BioBottomSheet.visibility = View.VISIBLE
        }

        binding.editNameCancel.setOnClickListener {
            binding.eventBottomSheet.visibility = View.GONE
        }

        binding.editBioCancel.setOnClickListener {
            binding.BioBottomSheet.visibility = View.GONE
        }

        binding.editNameSave.setOnClickListener {
            val editedName = binding.editName.text.toString().trim()
            binding.eventBottomSheet.visibility = View.GONE
            userSetters.setUserName(
                editedName,
                onSuccess = {
                    Toast.makeText(
                        requireContext(),
                        "SuccesFully Updated the Name",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.UserName.text = editedName
                    userProfilePreferenceManager.saveUserName(editedName)
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.editBioSave.setOnClickListener {
            val editedBio = binding.editBio.text.toString().trim()
            binding.BioBottomSheet.visibility = View.GONE
            userSetters.setUserBio(
                editedBio,
                onSuccess = {
                    Toast.makeText(
                        requireContext(),
                        "SuccesFully Updated the Bio",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.UserBio.text = editedBio
                    userProfilePreferenceManager.saveUserBio(editedBio)
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Failure", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.profileImage.setOnClickListener {
            progressDialog.setMessage("Uploading Image")
            progressDialog.setCancelable(false)
            progressDialog.show()

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

            ImageTransitManager.uploadImageToStorage(selectedImageUri!!,
                imageEditors.resizeImage(selectedImageUri!!),
                onSuccess = {
                    Log.d("1234", "Image uploaded successfully")
                    val newImageUrl = it.toString()
                    ImageTransitManager.UpdateProfileImageInfo(it, onSuccess = {
                        Log.d("1234", "Image url update successfully")
                        ImageTransitManager.deleteImageFromStorage(
                            userGetter.getImage(),
                            onSuccess = {
                                Log.d("1234", "Image deletion successfully")
                                userProfilePreferenceManager.saveUserImage(newImageUrl)
                                Toast.makeText(requireContext(), userGetter.getImage(), Toast.LENGTH_SHORT).show()
                                fetchProfile()
                                if (progressDialog.isShowing) progressDialog.dismiss()
                            },
                            onFailure = {
                                Log.d("1234", "Image deletion failure")
                                userProfilePreferenceManager.saveUserImage(newImageUrl)
                                fetchProfile()
                                if (progressDialog.isShowing) progressDialog.dismiss()
                            })
                    }, onFailure = {
                        Log.d("1234", "Image url update failure")
                        if (progressDialog.isShowing) progressDialog.dismiss()
                    })
                },
                onFailure = {
                    Log.d("1234", "Image uploaded failure")
                    Log.e(ContentValues.TAG, "Error uploading resized image: ${it.message}")
                    if (progressDialog.isShowing) progressDialog.dismiss()
                })
        }else{
            if (progressDialog.isShowing) progressDialog.dismiss()
        }
    }

    private fun fetchProfile() {
        binding.UserName.text = userGetter.getName().toString()
        binding.UserBio.text = userGetter.getBio().toString()
        binding.UserPhone.text = userGetter.getPhone().toString()

//        Glide.with(requireContext())
//            .load(userGetter.getImage())
//            .placeholder(R.drawable.camera_logo)
//            .into(binding.profileImage)

        binding.profileImage.load(userGetter.getImage()) {
            crossfade(true)
            placeholder(R.drawable.camera_logo)
            transformations(CircleCropTransformation())
        }

    }

    private fun setBottomSheetData() {

    }

    private fun NavigateToPrevFragment(fragment: Fragment) {
        val bundle = Bundle()
        fragment.arguments = bundle

        parentFragmentManager.popBackStack()
    }
}