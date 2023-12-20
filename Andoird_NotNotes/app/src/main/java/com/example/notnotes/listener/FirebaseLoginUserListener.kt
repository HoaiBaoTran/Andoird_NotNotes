package com.example.notnotes.listener

import com.google.firebase.auth.FirebaseUser

interface FirebaseLoginUserListener {
    fun onLoginUserSuccess(currentUser: FirebaseUser?)
    fun onFailure()
}