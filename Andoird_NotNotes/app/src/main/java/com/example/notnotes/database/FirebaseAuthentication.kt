package com.example.notnotes.database

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.listener.FirebaseRegisterUserListener
import com.example.notnotes.model.User
import com.example.notnotes.userservice.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import java.util.Timer
import kotlin.concurrent.schedule


class FirebaseAuthentication (
    private val context: Context
) {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDataWriter = FirebaseDataWriter(context)
    var firebaseRegisterUserListener: FirebaseRegisterUserListener? = null

    fun registerUser(user: User) {
        auth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
            task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser? = auth.currentUser
                    firebaseUser?.sendEmailVerification()
                    val id = firebaseUser?.uid

                    // Write user data to db
                    firebaseDataWriter.writeUserData(user, id)

                    val title = context.applicationContext.getString(R.string.Annoucement)
                    val message = context.applicationContext.getString(R.string.signup_success_message)
                    showDialog(title, message)

                    Timer().schedule(3000) {
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                        (context as Activity).finish()
                    }

                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        val title = context.applicationContext.getString(R.string.invalid_email_error)
                        val message = context.applicationContext
                            .getString(R.string.your_email_is_invalid_or_already_in_use)
                        showDialog(title, message)
                        firebaseRegisterUserListener!!.onFailure()

                    } catch (e: FirebaseAuthUserCollisionException) {
                        val title = context.applicationContext.getString(R.string.invalid_email_error)
                        val message = context.applicationContext
                            .getString(R.string.email_has_been_used_please_choose_another_email)
                        showDialog(title, message)
                        firebaseRegisterUserListener!!.onFailure()

                    } catch (e: Exception) {
                        Log.e("FirebaseAuthentication", e.message!!)
                        firebaseRegisterUserListener!!.onFailure()
                    }
                }
        }
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