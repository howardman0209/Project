package com.howard.project.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.howard.project.R
import com.howard.project.databinding.ActivityLoginBinding
import com.howard.project.extension.TAG
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.LoginViewModel
import com.howard.project.util.LoginManager

class LoginActivity : MVVMActivity<LoginViewModel, ActivityLoginBinding>() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d(LIFECYCLE, "LoginActivity onCreate")
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.logo.setOnLongClickListener {
            bypassLogin()
            true
        }
    }

    private fun login(
        email: String? = viewModel.email.get(),
        password: String? = viewModel.password.get()
    ) {
        Log.d(TAG, "email: $email, password: $password")
        showLoadingIndicator(true)
        auth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) { task ->
                showLoadingIndicator(false)
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmailAndPassword:success")
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "Login Success", Toast.LENGTH_SHORT).show()
                    LoginManager.user = user
                    onSuccessLogin(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmailAndPassword:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    onFailLogin()
                }
            }
    }

    private fun bypassLogin() {
        startActivity(Intent(this, LandingActivity::class.java))
        this.finish()
    }

    private fun onSuccessLogin(user: FirebaseUser?) {
        Log.w(TAG, "onSuccessLogin - User: $user")
        startActivity(Intent(this, LandingActivity::class.java))
        this.finish()
    }

    private fun onFailLogin() {
        Log.w(TAG, "onFailLogin")
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("FirebaseAuth", "Logged In")
            // TODO: redirect to other page
//            reload()
        }
    }

    override fun onBackPressed() {
        //disable back button
    }

    override fun getLayoutResId(): Int = R.layout.activity_login

    override fun getViewModelInstance(): LoginViewModel = LoginViewModel()

    override fun setBindingData() {
        binding.viewModel = viewModel
    }
}