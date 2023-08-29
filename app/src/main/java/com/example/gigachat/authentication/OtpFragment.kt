package com.example.gigachat.authentication

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gigachat.MainActivity
import com.example.gigachat.R
import com.example.gigachat.Utlilities.UserInfo
import com.example.gigachat.Utlilities.permissionManagers.PermissionChecker
import com.example.gigachat.Utlilities.permissionManagers.PermissionRequester
import com.example.gigachat.Utlilities.preferenceManagers.AuthenticationPreferenceManager
import com.example.gigachat.databinding.FragmentOtpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import java.util.jar.Manifest

class OtpFragment : Fragment() {

    private lateinit var binding: FragmentOtpBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    private lateinit var progressDialog: ProgressDialog
    private lateinit var AuthPreferenceManager: AuthenticationPreferenceManager
    private lateinit var PermissionChecker : PermissionChecker
    private lateinit var PermissionRequester : PermissionRequester


    private lateinit var UserInfo : UserInfo
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(requireActivity())
        AuthPreferenceManager = AuthenticationPreferenceManager(requireContext())
        UserInfo = UserInfo(requireContext())
        PermissionChecker = PermissionChecker(requireContext())
        PermissionRequester = PermissionRequester(requireContext(), requireActivity())

        verificationId = arguments?.getString("verificationId")

        binding.verifyOtp.setOnClickListener {
            progressDialog.setMessage("Verifying OTP")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val otp = binding.otp.text.toString()

            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)

            auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        AuthPreferenceManager.saveCurrentAuthStatus(true)
                        UpdateUser(UserInfo.getPhone())
                        NavigateToNextFragment()
                    } else {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        binding.otp.error = "Invalid OTP"
                    }
                }
        }
        return view
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == contactPermissionCode) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Contact permission granted, you can proceed with your logic
//            }
//        }
//    }



    private fun NavigateToNextFragment(){
        val fragment = UserprofileInitialisationFragment()
//        val bundle = Bundle()
//        bundle.putString("verificationId", verificationId)
//        fragment.arguments = bundle

        parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun UpdateUser(personPhone: String) {
        AuthPreferenceManager.saveCurrentAuthStatus(true)
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        WriteNewUser(personPhone, uid)
    }

    private fun WriteNewUser(personPhone: String, uid: String) {
        val fireStoreDatabase = FirebaseFirestore.getInstance()

        val hashMap = hashMapOf<String, Any>(
            "Phone" to personPhone
        )

        fireStoreDatabase.collection("Users").document(uid)
            .set(hashMap)
            .addOnSuccessListener {

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error adding User in Database $exception")
            }
    }

    companion object {

    }
}