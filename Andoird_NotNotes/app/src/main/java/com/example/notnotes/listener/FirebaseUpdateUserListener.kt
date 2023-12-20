package com.example.notnotes.listener

import com.example.notnotes.model.User

interface FirebaseUpdateUserListener {
    fun onUpdateUserSuccess()
    fun onUpdateUserFailure()
}