package com.howard.project.util

import com.google.firebase.auth.FirebaseUser

object LoginManager {
    var user: FirebaseUser? = null

    fun clearUserSession() {
        user = null
    }
}