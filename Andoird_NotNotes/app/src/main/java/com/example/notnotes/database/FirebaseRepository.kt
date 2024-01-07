package com.example.notnotes.database

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.listener.FirebaseLabelListener
import com.example.notnotes.listener.FirebaseNoteListener
import com.example.notnotes.listener.FirebaseReadLabelListener
import com.example.notnotes.listener.FirebaseReadNoteListener
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.FirebaseUpdateUserListener
import com.example.notnotes.model.Label
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
    var firebaseLabelListener: FirebaseLabelListener? = null
    var firebaseReadLabelListener: FirebaseReadLabelListener? = null

    private val USER_TABLE = "User"
    private val NOTE_TABLE = "Note"
    private val LABEL_TABLE = "Label"

    private fun connectUserRef () {
        reference = db.getReference(USER_TABLE)
    }

    private fun connectNoteRef(userId: String) {
        reference = db.getReference(NOTE_TABLE).child(userId)
    }

    private fun connectLabelRef(userId: String) {
        reference = db.getReference(LABEL_TABLE).child(userId)
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

    fun editNote(note: Note, userId: String) {
        connectNoteRef(userId)
        Log.d("FirebaseRepository", note.id)
        reference.child(note.id).setValue(note)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseNoteListener!!.onUpdateNoteSuccess()
                }
                else {
                    firebaseNoteListener!!.onUpdateNoteFailure()
                }
            }
    }

    fun deleteNote(note: Note, userId: String) {
        connectNoteRef(userId)
        reference.child(note.id).removeValue()
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    firebaseReadNoteListener!!.onDeleteNoteSuccess()
                }
                else {
                    firebaseReadNoteListener!!.onDeleteNoteFailure()
                }
            }
            .addOnFailureListener {
                firebaseReadNoteListener!!.onDeleteNoteFailure()
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
                    note?.deleted = note?.let { noteSnapshot.child("deleted").getValue(Boolean::class.java) } == true
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

    fun getNotesByLabel(userId: String, label: String) {
        connectNoteRef(userId)
        val notes = ArrayList<Note>()
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    note?.id = noteSnapshot.key.toString()
                    note?.deleted = note?.let { noteSnapshot.child("deleted").getValue(Boolean::class.java) } == true
                    if (note != null && note.label?.lowercase()?.contains(label.lowercase()) == true) {
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

    fun getNotesByTitle(userId: String, title: String) {
        connectNoteRef(userId)
        val notes = ArrayList<Note>()
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    note?.id = noteSnapshot.key.toString()
                    note?.deleted = note?.let { noteSnapshot.child("deleted").getValue(Boolean::class.java) } == true
                    if (note != null && note.title.lowercase().contains(title.lowercase())) {
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

    fun getNotesByContent(userId: String, content: String) {
        connectNoteRef(userId)
        val notes = ArrayList<Note>()
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (noteSnapshot in snapshot.children) {
                    val note = noteSnapshot.getValue(Note::class.java)
                    note?.id = noteSnapshot.key.toString()
                    note?.deleted = note?.let { noteSnapshot.child("deleted").getValue(Boolean::class.java) } == true
                    if (note != null && note.content?.lowercase()?.contains(content.lowercase()) == true) {
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
                    firebaseReadUserListener?.onReadUserSuccess(user!!)
                }
                else {
                    firebaseReadUserListener?.onReadUserFailure()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                firebaseReadUserListener!!.onReadUserFailure()
            }

        })
    }

    fun getLabels(userId: String) {
        connectLabelRef(userId)
        val labels = ArrayList<Label>()
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (labelSnapshot in snapshot.children) {
                    val label = labelSnapshot.getValue(Label::class.java)
//                    label?.id = labelSnapshot.key.toString()
                    if (label != null) {
                        labels.add(label)
                    }
                }
                firebaseReadLabelListener!!.onReadLabelListComplete(labels)
            }
            override fun onCancelled(error: DatabaseError) {
                firebaseReadLabelListener!!.onReadLabelListFailure()
            }
        })
    }

    fun addLabel(label: Label, userId: String) {
        connectLabelRef(userId)
//        val key = reference.push().key
        reference.child(label.name).setValue(label)
            .addOnCompleteListener {
                    task ->
                if (task.isSuccessful) {
                    firebaseLabelListener!!.onAddLabelSuccess()
                }
                else {
                    firebaseLabelListener!!.onAddLabelFailure()
                }

            }
    }

    fun deleteLabel(label: Label, userId: String) {
        connectLabelRef(userId)
        reference.child(label.name).removeValue()
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    firebaseReadLabelListener!!.onDeleteLabelSuccess()
                }
                else {
                    firebaseReadLabelListener!!.onDeleteLabelFailure()
                }
            }
            .addOnFailureListener {
                firebaseReadLabelListener!!.onDeleteLabelFailure()
            }
    }

}