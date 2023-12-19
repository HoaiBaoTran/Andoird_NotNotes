package com.example.notnotes.database

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.listener.FirebaseListener
import com.example.notnotes.model.Note
import com.example.notnotes.model.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.Timer
import kotlin.concurrent.schedule

class FirebaseConnection(
    private val context: Context,
    private val firebaseListener: FirebaseListener,
    ) {

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

    fun addNote(note: Note, userName: String) {
        connectNoteRef(userName)
        val key = reference.push().key
        note.id = key!!
        reference.child(note.id).setValue(note)
            .addOnCompleteListener {
                val title = context.applicationContext.getString(R.string.Annoucement)
                val message = "Thêm note thành công "
                showDialog(title, message)
            }
    }

    fun editNote(note: Note, userName: String) {
        connectNoteRef(userName)
        reference.child(note.id).setValue(note)
            .addOnCompleteListener {
                val title = context.applicationContext.getString(R.string.Annoucement)
                val message = "Sửa note thành công"
                showDialog(title, message)
            }
    }

    fun deleteNote(note: Note, userName: String) {
        connectNoteRef(userName)
        reference.child(note.id).removeValue()
            .addOnCompleteListener {
                val title = context.applicationContext.getString(R.string.Annoucement)
                val message = "Xóa note thành công"
                showDialog(title, message)
            }
            .addOnFailureListener {
                val title = context.applicationContext.getString(R.string.Annoucement)
                val message = "Xóa note thất bại"
                showDialog(title, message)
            }
    }

    fun getNotes(userName: String) {
        connectNoteRef(userName)
        val notes = ArrayList<Note>()
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    if (note != null) {
                        notes.add(note)
                    }
                }
                firebaseListener.onReadNoteListComplete(notes)
            }
            override fun onCancelled(error: DatabaseError) {
                firebaseListener.onFailure()
            }
        })
    }

    fun changePasswordUser(user: User) {
        connectUserRef()
        reference.child(user.userName).setValue(user)
            .addOnCompleteListener {
                val title = context.applicationContext.getString(R.string.Annoucement)
                val message = context.applicationContext.getString(R.string.change_password_success)
                showDialog(title, message)
                Timer().schedule(3000) {
                    (context as Activity).finish()
                }
            }
    }

    fun updateUserData(user: User) {
        connectUserRef()
        reference.child(user.userName).setValue(user)
            .addOnCompleteListener {
                val title = context.applicationContext.getString(R.string.Annoucement)
                val message = context.getString(R.string.update_user_success)
                showDialog(title, message)
                Timer().schedule(3000) {
                    (context as Activity).finish()
                }
            }
    }

    fun registerUser(user: User) {
        connectUserRef()
        reference.child(user.userName).setValue(user)
            .addOnCompleteListener {
                val title = context.applicationContext.getString(R.string.Annoucement)
                val message = context.applicationContext.getString(R.string.signup_success_message)
                showDialog(title, message)
                Timer().schedule(3000) {
                    (context as Activity).finish()
                }
        }
    }

    fun checkUsernameExist (userName: String) {
        connectUserRef()
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var isExist = false
                var userRes = User()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user?.userName == userName) {
                        userRes = user
                        isExist = true
                    }
                }
                if (isExist) {
                    firebaseListener.onUsernameExist(userRes)
                }
                else {
                    firebaseListener.onUserNotExist()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                firebaseListener.onFailure()
            }
        })
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