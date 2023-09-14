package com.example.gigachat.settings.account

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.gigachat.R
import com.example.gigachat.authentication.AuthenticationActivity
import com.example.gigachat.databinding.FragmentAccountBinding
import com.example.gigachat.settings.SettingsFragment
import com.example.gigachat.utlilities.preferenceManagers.AuthenticationPreferenceManager
import com.example.gigachat.utlilities.preferenceManagers.UserProfilePreferenceManager
import com.example.gigachat.utlilities.userManagers.UserDataDeletion
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var authenticationPreferenceManager: AuthenticationPreferenceManager
    private lateinit var userProfilePreferenceManager: UserProfilePreferenceManager
    private val auth = FirebaseAuth.getInstance()

    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        authenticationPreferenceManager = AuthenticationPreferenceManager(requireContext())
        userProfilePreferenceManager = UserProfilePreferenceManager(requireContext())
        progressDialog = ProgressDialog(requireContext())

        binding.goBackButton.setOnClickListener {
            NavigateToPrevFragment(SettingsFragment())
        }

        binding.logout.setOnClickListener {
            showThemeDialogLogout("DO you want to logout from this device", "logout", view)
        }

        binding.deleteAccount.setOnClickListener {
            showThemeDialogDelete("Do you want to delete your Account data and credentials from our server", "Delete", view)
        }

        return view
    }

    private fun showThemeDialogDelete(message : String, confirmText : String, view: View) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(confirmText) { _, _ ->
                deleteAccount(view)
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.window?.setBackgroundDrawableResource(R.drawable.theme_dialog_bg)

        alertDialog.show()
    }

    private fun showThemeDialogLogout(message : String, confirmText : String, view: View) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(confirmText) { _, _ ->
                progressDialog.setMessage("Signing out")
                progressDialog.setCancelable(false)
                progressDialog.show()

                logoutAndDeletePref(view)
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.window?.setBackgroundDrawableResource(R.drawable.theme_dialog_bg)

        alertDialog.show()
    }

    private fun deleteAccount(view: View){
        progressDialog.setMessage("Deleting Account")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val uid = auth.currentUser?.uid

        val userDataDeletion = UserDataDeletion()
        val deleteCallback = object : UserDataDeletion.DeleteCallback {
            override fun onSuccess() {
                logoutAndDeletePref(view)
            }

            override fun onFailure(exception: Exception) {
                progressDialog.dismiss()
            }
        }

        GlobalScope.launch {
            try {
                userDataDeletion.deleteUserData(uid.toString(), deleteCallback)
            }catch (e: Exception) {
                progressDialog.dismiss()
            } finally {
                // Dismiss the progress dialog regardless of success or failure
                progressDialog.dismiss()
            }
        }
    }


    private fun logoutAndDeletePref(view: View){
        authenticationPreferenceManager.clearAllAuthPreferences(onSuccess = {
            auth.signOut()
            userProfilePreferenceManager.clearAllPref()
//            if(progressDialog.isShowing)progressDialog.dismiss()
            val intent = Intent(requireActivity(), AuthenticationActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }, onFailure = {
            if(progressDialog.isShowing)progressDialog.dismiss()
            val snackbar = Snackbar.make(view, "Logout Failure", Snackbar.LENGTH_SHORT)
            snackbar.show()
        })
    }

    private fun NavigateToPrevFragment(fragment: Fragment) {
        val bundle = Bundle()
        fragment.arguments = bundle

        parentFragmentManager.popBackStack()
    }
}