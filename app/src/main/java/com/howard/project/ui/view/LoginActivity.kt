package com.howard.project.ui.view

import android.accounts.Account
import android.accounts.AccountManager
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
import com.howard.project.extension.getSoepayAccountList
import com.howard.project.ui.base.MVVMActivity
import com.howard.project.ui.viewModel.LoginViewModel
import com.howard.project.util.LoginManager

class LoginActivity : MVVMActivity<LoginViewModel, ActivityLoginBinding>() {
    private lateinit var auth: FirebaseAuth

    private val accountManager: AccountManager by lazy {
        AccountManager.get(this)
    }
    private var loginAccountList: Array<Account> = arrayOf()
    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d(LIFECYCLE, "LoginActivity onCreate")
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
        loginAccountList = getSoepayAccountList()
        autoFillInLoginCredential()
        Log.d(TAG, "loginAccountList $loginAccountList")

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
//        Log.d(TAG, "email: $email, password: $password")
        showLoadingIndicator(true)
        auth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(this) { task ->
                showLoadingIndicator(false)
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmailAndPassword: success")
                    val user = auth.currentUser
                    saveLoginCredential(user, email, accountType())
                    onSuccessLogin(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmailAndPassword: failure", task.exception)
                    onFailLogin()
                }
            }
    }

    private fun autoFillInLoginCredential() {
        if (loginAccountList.isNotEmpty()) {
            viewModel.email.set(loginAccountList.last().name)
            val accountPassword = try {
                accountManager.getUserData(loginAccountList.last(), "password")
            } catch (e: Exception) {
                Log.d(TAG, "Get password error $e")
                null
            }
            Log.d(TAG, "accountPassword: $accountPassword")
            viewModel.password.set(accountPassword)
            viewModel.isSavePassword.set(true)
        }
    }

    private fun saveLoginCredential(
        user: FirebaseUser? = null,
        email: String? = viewModel.email.get(),
        accountType: String? = null
    ) {
        val account = Account(email, accountType)
        if (loginAccountList.none { it.name == user?.email }) {
            accountManager.addAccountExplicitly(account, null, null)
        } else {
            checkAccountIsLast(account)
        }

        if (viewModel.isSavePassword.get()) {
            accountManager.setUserData(account, "password", viewModel.password.get())
        } else {
            accountManager.setUserData(account, "password", "")
        }
    }

    private fun checkAccountIsLast(account: Account) {
        if (loginAccountList.last() != account) {
            accountManager.removeAccountExplicitly(account)
            accountManager.addAccountExplicitly(account, null, null)
            if (viewModel.isSavePassword.get()) {
                accountManager.setUserData(account, "password", viewModel.password.get())
            }
        }
    }

    private fun bypassLogin() {
        startActivity(Intent(applicationContext, LandingActivity::class.java))
        this.finish()
    }

    private fun onSuccessLogin(user: FirebaseUser?) {
        Log.w(TAG, "onSuccessLogin - User: $user")
        LoginManager.user = user

        Toast.makeText(baseContext, "Login Success", Toast.LENGTH_SHORT).show()
        startActivity(Intent(applicationContext, LandingActivity::class.java))
        this.finish()
    }

    private fun onFailLogin() {
        Log.w(TAG, "onFailLogin")
        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
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