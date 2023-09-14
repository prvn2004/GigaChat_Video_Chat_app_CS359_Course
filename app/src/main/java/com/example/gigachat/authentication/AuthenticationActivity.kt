package com.example.gigachat.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.gigachat.MainActivity
import com.example.gigachat.R
import com.example.gigachat.utlilities.preferenceManagers.AuthenticationPreferenceManager
import com.example.gigachat.databinding.ActivityLoginBinding

class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var AuthPreferenceManager: AuthenticationPreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        AuthPreferenceManager = AuthenticationPreferenceManager(this)

        if(!AuthPreferenceManager.getCurrentAuthStatus()){
            showFragment(LoginFragment())
        }else if(AuthPreferenceManager.getCurrentAuthStatus() && AuthPreferenceManager.getCurrentProfileStatus()){
            moveToActivity()
        }else{
            showFragment(UserprofileInitialisationFragment())
        }


//        showFragment(LoginFragment())

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    fun moveToActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun showFragment(fragment: Fragment) {
        val fram = supportFragmentManager.beginTransaction()
        fram.replace(R.id.container, fragment)
        fram.commit()
    }
}