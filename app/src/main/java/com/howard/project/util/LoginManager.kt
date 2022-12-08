package com.howard.project.util

import com.google.firebase.auth.FirebaseUser
import com.howard.project.R

object LoginManager {
    var user: FirebaseUser? = null

    fun clearUserSession() {
        user = null
    }
}