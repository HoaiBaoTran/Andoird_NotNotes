package com.example.notnotes.listener

import com.example.notnotes.model.Note
import com.example.notnotes.model.User

interface FirebaseListener {

    fun onUsernameExist(user: User)
    fun onStartAccess()
    fun onUserNotExist()
    fun onFailure()
    fun onReadNoteListComplete(notes: List<Note>)
}