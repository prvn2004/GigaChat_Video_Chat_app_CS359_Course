package com.example.gigachat.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.example.gigachat.R
import com.example.gigachat.settings.account.AccountFragment
import com.example.gigachat.utlilities.userManagers.UserGetters
import com.example.gigachat.utlilities.preferenceManagers.UserProfilePreferenceManager
import com.example.gigachat.databinding.FragmentSettingsBinding
import com.example.gigachat.settings.profile.ProfileFragment
import com.example.gigachat.utlilities.preferenceManagers.ThemePreferenceManager

class SettingsFragment : Fragment() {

    private lateinit var userProfilePreferenceManager: UserProfilePreferenceManager
    private lateinit var userGetter : UserGetters
    private lateinit var themePreferenceManager: ThemePreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        userProfilePreferenceManager = UserProfilePreferenceManager(requireContext())
        userGetter = UserGetters(requireContext())
        themePreferenceManager = ThemePreferenceManager(requireContext())
        fetchProfile()
        setCurrentTheme()

        binding.profileOpen.setOnClickListener {
            NavigateToNextFragment(ProfileFragment())
        }

        binding.themeOpen.setOnClickListener {
            showThemeDialog()
        }

        binding.accountOpen.setOnClickListener {
            NavigateToNextFragment(AccountFragment())
        }

        return view
    }

    private fun setCurrentTheme(){
        when (themePreferenceManager.getCurrentTheme()) {
            AppCompatDelegate.MODE_NIGHT_YES -> binding.appTheme.setText("Dark")
            AppCompatDelegate.MODE_NIGHT_NO -> binding.appTheme.setText("Light")
            else -> binding.appTheme.setText("System default")
        }
    }

    private fun showThemeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_theme, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)

        // Initialize the selected radio button based on the current theme
        when (themePreferenceManager.getCurrentTheme()) {
            AppCompatDelegate.MODE_NIGHT_YES -> radioGroup.check(R.id.radioDark)
            AppCompatDelegate.MODE_NIGHT_NO -> radioGroup.check(R.id.radioLight)
            else -> radioGroup.check(R.id.radioSystemDefault)
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val selectedRadioButton = dialogView.findViewById<RadioButton>(
                    radioGroup.checkedRadioButtonId
                )
                when (selectedRadioButton.id) {
                    R.id.radioDark -> themePreferenceManager.setTheme(AppCompatDelegate.MODE_NIGHT_YES)
                    R.id.radioLight -> themePreferenceManager.setTheme(AppCompatDelegate.MODE_NIGHT_NO)
                    else -> themePreferenceManager.setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                setCurrentTheme()
//                recreate() // Recreate the activity to apply the new theme
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.window?.setBackgroundDrawableResource(R.drawable.theme_dialog_bg)

        alertDialog.show()
    }

    private fun fetchProfile(){
        binding.profileName.text = userGetter.getName().toString()
        binding.profileBio.text = userGetter.getBio().toString()

        Glide.with(requireContext())
            .load(userGetter.getImage())
            .placeholder(R.drawable.camera_logo)
            .into(binding.profileImage)
    }

    private fun NavigateToNextFragment(fragment: Fragment) {
        val bundle = Bundle()
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.settings_container, fragment).addToBackStack(null)
            .commit()
    }


}