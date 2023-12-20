package com.example.notnotes.listener

import com.example.notnotes.model.Note

interface FirebaseReadNoteListener {
    fun onReadNoteListComplete(notes: List<Note>)
    fun onReadNoteListFailure()
}