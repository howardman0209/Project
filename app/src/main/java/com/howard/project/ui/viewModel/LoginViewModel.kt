package com.howard.project.ui.viewModel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.howard.project.extension.ValidPasswordLength
import com.howard.project.extension.isEmailValid
import com.howard.project.ui.base.BaseViewModel

class LoginViewModel : BaseViewModel() {
    val password = ObservableField<String>()
    val email = ObservableField<String>()
    val enableLogin = ObservableBoolean()
    val isSavePassword = ObservableBoolean(false)

    @Suppress("UNUSED_PARAMETER")
    fun onPasswordChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val input = s.toString()
        password.set(input)
        enableLogin.set(email.get().orEmpty().isEmailValid() && input.length >= ValidPasswordLength)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onEmailChanged(emailInput: CharSequence, start: Int, before: Int, count: Int) {
        val input = emailInput.toString()
        email.set(input)
        enableLogin.set(
            input.isEmailValid() && password.get().orEmpty().length >= ValidPasswordLength
        )
    }
}