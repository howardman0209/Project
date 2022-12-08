package com.howard.project.extension

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import androidx.core.content.PermissionChecker
import com.howard.project.R


fun Context.getSoepayAccountList(): Array<Account> {
    val accountManager = AccountManager.get(this)
    return accountManager.getAccountsByType(getString(R.string.account_type))
}

fun Context.isGranted(permission: String): Boolean = PermissionChecker.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED