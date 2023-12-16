package com.example.notnotes.database

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.model.UserTemp
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.Timer
import kotlin.concurrent.schedule

class FirebaseConnection(private val context: Context) {

    private val db = Firebase.database
    private lateinit var reference: DatabaseReference
    private val USER_TABLE = "User"
    private val NOTE_TABLE = "Note"

    private fun connectUserRef () {
        reference = db.getReference(USER_TABLE)
    }

    private fun connectNoteRef () {
        reference = db.getReference(NOTE_TABLE)
    }

    fun registerUser(user: UserTemp) {
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

    fun isUsernameExist (userName: String) : Boolean {
        connectUserRef()
        var isExist = false
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(UserTemp::class.java)
                    if (user?.userName == userName) {
                        Log.e("Signup_Activity", user.userName.toString())
                        Log.e("Signup_Activity", "Change isExist")
                        isExist = true
                        Log.e("Signup_Activity", isExist.toString())
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Signup_Activity", "Error reading data", error.toException())
            }
        })
//        Log.e("Signup_Activity", isExist.toString())
//        if (isExist) {
//            val title = context.applicationContext.getString(R.string.username_error)
//            val message = context.applicationContext.getString(R.string.username_has_been_used)
//            showDialog(title, message)
//            Log.e("Signup_Activity", isExist.toString())
//        }
        return isExist
    }

    private fun showDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context.applicationContext)
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


}