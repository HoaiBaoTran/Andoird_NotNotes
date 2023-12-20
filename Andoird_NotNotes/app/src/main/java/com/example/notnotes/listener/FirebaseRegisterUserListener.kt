package com.example.notnotes.listener

import com.example.notnotes.model.User

interface FirebaseRegisterUserListener {
    fun onFailure()
    fun onRegisterUserSuccess()
}