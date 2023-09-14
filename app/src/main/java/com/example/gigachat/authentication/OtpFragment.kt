package com.example.gigachat.authentication

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gigachat.MainActivity
import com.example.gigachat.R
import com.example.gigachat.utlilities.userManagers.UserGetters
import com.example.gigachat.utlilities.permissionManagers.PermissionChecker
import com.example.gigachat.utlilities.permissionManagers.PermissionRequester
import com.example.gigachat.utlilities.preferenceManagers.AuthenticationPreferenceManager
import com.example.gigachat.databinding.FragmentOtpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class OtpFragment : Fragment() {

    private lateinit var binding: FragmentOtpBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    private lateinit var progressDialog: ProgressDialog
    private lateinit var AuthPreferenceManager: AuthenticationPreferenceManager
    private lateinit var PermissionChecker: PermissionChecker
    private lateinit var PermissionRequester: PermissionRequester


    private lateinit var userGetter: UserGetters
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(requireActivity())
        AuthPreferenceManager = AuthenticationPreferenceManager(requireContext())
        userGetter = UserGetters(requireContext())
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
                        AuthPreferenceManager.saveCurrentAuthStatus(true)
                        CheckCurrentUser(userGetter.getPhone())
                    } else {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        binding.otp.error = "Invalid OTP"
                    }
                }
        }
        return view
    }

    private fun NavigateToNextFragment() {
        val fragment = UserprofileInitialisationFragment()
//        val bundle = Bundle()
//        bundle.putString("verificationId", verificationId)
//        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun CheckCurrentUser(personPhone: String) {
        GlobalScope.launch(Dispatchers.Main) {
            if (userGetter.fetchCurrentProfileInitializationStatus() == true) {
                userGetter.fetchUserDetails()
                if (progressDialog.isShowing) progressDialog.dismiss()
                AuthPreferenceManager.saveCurrentProfileStatus(true)
                val intent = Intent(requireActivity(), MainActivity::class.java)
                requireActivity().startActivity(intent)
                requireActivity().finish()
            } else {
                UpdateUser(personPhone)
            }
        }
    }

    private fun UpdateUser(personPhone: String) {
        AuthPreferenceManager.saveCurrentAuthStatus(true)
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        WriteNewUser(personPhone, uid)
    }

    private fun WriteNewUser(personPhone: String, uid: String) {
        val fireStoreDatabase = FirebaseFirestore.getInstance()

        val hashMap = hashMapOf<String, Any>(
            "Phone" to personPhone,
            "ProfileUrl" to ""
        )

        fireStoreDatabase.collection("Users").document(uid)
            .set(hashMap)
            .addOnSuccessListener {
                if (progressDialog.isShowing) progressDialog.dismiss()
                NavigateToNextFragment()
            }
            .addOnFailureListener { exception ->
                if (progressDialog.isShowing) progressDialog.dismiss()
                Log.w(ContentValues.TAG, "Error adding User in Database $exception")
            }
    }

    companion object {

    }
}