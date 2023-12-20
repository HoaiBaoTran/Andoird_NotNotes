package com.example.notnotes.listener

import com.example.notnotes.model.User

interface FirebaseReadUserListener {
    fun onReadUserSuccess(user: User)
    fun onReadUserFailure()
}