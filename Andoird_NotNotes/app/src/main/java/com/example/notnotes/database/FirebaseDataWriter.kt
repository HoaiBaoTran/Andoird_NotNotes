package com.example.notnotes.database

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.model.User
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.util.Timer
import kotlin.concurrent.schedule

class FirebaseDataWriter(private val context: Context) {
    private val db = Firebase.database
    private lateinit var reference: DatabaseReference

    private val USER_TABLE = "User"
    private val NOTE_TABLE = "Note"

    private fun connectUserRef () {
        reference = db.getReference(USER_TABLE)
    }

    private fun connectNoteRef (userName: String) {
        reference = db.getReference(NOTE_TABLE).child(userName)
    }

    fun writeUserData(user: User, id: String?) {
        connectUserRef()
        reference.child(id!!).setValue(user)
    }

    private fun showDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}