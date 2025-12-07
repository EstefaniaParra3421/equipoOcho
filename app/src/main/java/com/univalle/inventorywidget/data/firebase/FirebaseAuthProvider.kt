package com.tuapp.firebase

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthProvider {
    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
}
