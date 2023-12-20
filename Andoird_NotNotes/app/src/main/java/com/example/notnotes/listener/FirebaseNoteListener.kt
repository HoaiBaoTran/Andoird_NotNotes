package com.example.notnotes.listener

import com.example.notnotes.model.Note

interface FirebaseNoteListener {
    fun onAddNoteSuccess()
    fun onAddNoteFailure()
    fun onUpdateNoteSuccess()
    fun onUpdateNoteFailure()
}