package com.example.notnotes.database

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.listener.FirebaseNoteListener
import com.example.notnotes.listener.FirebaseReadNoteListener
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.FirebaseUpdateUserListener
import com.example.notnotes.model.Note
import com.example.notnotes.model.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class FirebaseRepository(private val context: Context) {
    private val db = Firebase.database
    private lateinit var reference: DatabaseReference

    var firebaseReadUserListener: FirebaseReadUserListener? = null
    var firebaseUpdateUserListener: FirebaseUpdateUserListener? = null
    var firebaseNoteListener: FirebaseNoteListener? = null
    var firebaseReadNoteListener: FirebaseReadNoteListener? = null

    private val USER_TABLE = "User"
    private val NOTE_TABLE = "Note"

    private fun connectUserRef () {
        reference = db.getReference(USER_TABLE)
    }

    private fun connectNoteRef (userId: String) {
        reference = db.getReference(NOTE_TABLE).child(userId)
    }

    // -- Note --
    fun addNote(note: Note, userId: String) {
        connectNoteRef(userId)

        val key = reference.push().key
        reference.child(key!!).setValue(note)
            .addOnCompleteListener {
                task ->
                    if (task.isSuccessful) {
                        firebaseNoteListener!!.onAddNoteSuccess()
                    }
                    else {
                        firebaseNoteListener!!.onAddNoteFailure()
                    }

            }
    }

    fun getNotes(userId: String) {
        connectNoteRef(userId)
        val notes = ArrayList<Note>()
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    note?.id = noteSnapshot.key.toString()
                    if (note != null) {
                        notes.add(note)
                    }
                }
                firebaseReadNoteListener!!.onReadNoteListComplete(notes)
            }
            override fun onCancelled(error: DatabaseError) {
                firebaseReadNoteListener!!.onReadNoteListFailure()
            }
        })
    }

    // -- User --

    fun writeUserData(user: User, id: String?) {
        connectUserRef()
        reference.child(id!!).setValue(user)
    }

    fun updateUser(user: User, id: String?) {
        connectUserRef()
        reference.child(id!!).setValue(user).addOnCompleteListener {
            task ->
                if (task.isSuccessful) {
                    firebaseUpdateUserListener!!.onUpdateUserSuccess()
                }
                else {
                    firebaseUpdateUserListener!!.onUpdateUserFailure()
                }
        }
    }

    fun getUserFromId(userId: String) {
        connectUserRef()
        val userQuery: Query = reference.child(userId?: "")
        userQuery.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    firebaseReadUserListener!!.onReadUserSuccess(user!!)
                }
                else {
                    firebaseReadUserListener!!.onReadUserFailure()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                firebaseReadUserListener!!.onReadUserFailure()
            }

        })
    }

}