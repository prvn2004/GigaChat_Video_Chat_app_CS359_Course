package com.example.gigachat.authentication

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gigachat.MainActivity
import com.example.gigachat.R
import com.example.gigachat.Utlilities.Constants
import com.example.gigachat.Utlilities.preferenceManagers.AuthenticationPreferenceManager
import com.example.gigachat.Utlilities.preferenceManagers.UserProfilePreferenceManager
import com.example.gigachat.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    private lateinit var AuthPreferenceManager: AuthenticationPreferenceManager
    private lateinit var UserProfilePreferenceManager : UserProfilePreferenceManager
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        auth = FirebaseAuth.getInstance()
        AuthPreferenceManager = AuthenticationPreferenceManager(requireContext())
        UserProfilePreferenceManager = UserProfilePreferenceManager(requireContext())

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(requireActivity())

        binding.sendOtpButton.setOnClickListener {
            if (binding.acceptCheckBox.isChecked) {
                progressDialog.setMessage("Sending OTP")
                progressDialog.setCancelable(false)
                progressDialog.show()

                val phoneNumber = "+91" + binding.phoneNumberEditText.text.toString()
                UserProfilePreferenceManager.savePhoneNumber(phoneNumber)
                sendOTP(phoneNumber)
            } else {
                binding.acceptCheckBox.error = ""
            }
        }

        return view
    }

    private fun sendOTP(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60, // Timeout duration
            TimeUnit.SECONDS,
            requireActivity(),
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Automatically verify the phone number when possible
                    signInWithPhoneAuthCredential(credential, phoneNumber)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    binding.phoneNumberEditText.error = "Enter correct Number"
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    if (progressDialog.isShowing) progressDialog.dismiss()

                    // Save verification ID for later use
                    this@LoginFragment.verificationId = verificationId

                    // Navigate to the OTP confirmation fragment
                    NavigateToNextFragment()
                }
            }
        )
    }

    private fun NavigateToNextFragment() {
        val fragment = OtpFragment()
        val bundle = Bundle()
        bundle.putString("verificationId", verificationId)
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential,
        personPhone: String
    ) {
        // Sign in with the provided phoneAuthCredential
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    AuthPreferenceManager.saveCurrentAuthStatus(true)
                    UpdateUser(personPhone)
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                } else {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    Toast.makeText(activity, "Failure", Toast.LENGTH_SHORT).show()
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
}