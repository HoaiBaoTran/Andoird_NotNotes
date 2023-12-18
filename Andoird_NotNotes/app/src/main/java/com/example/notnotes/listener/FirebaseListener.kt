package com.example.notnotes.listener

import com.example.notnotes.model.UserTemp
import com.google.firebase.database.DataSnapshot

interface FirebaseListener {

    fun onUsernameExist(user: UserTemp)
    fun onStartAccess()
    fun onUserNotExist()
    fun onFailure()
}