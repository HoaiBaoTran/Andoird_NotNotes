package com.example.notnotes.listener

import com.example.notnotes.model.UserTemp
import com.google.firebase.database.DataSnapshot

interface FirebaseListener {

    fun onUsernameExist()
    fun onStartAccess()
    fun onRegisterUser()
    fun onFailure()
}