package com.example.notnotes.database

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.listener.FirebaseListener
import com.example.notnotes.listener.FirebaseLoginUserListener
import com.example.notnotes.listener.FirebaseNoteListener
import com.example.notnotes.listener.FirebaseReadNoteListener
import com.example.notnotes.listener.FirebaseRegisterUserListener
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.FirebaseUpdateUserListener
import com.example.notnotes.model.Note
import com.example.notnotes.model.User
import com.example.notnotes.userservice.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.util.Timer
import kotlin.concurrent.schedule

class FirebaseService(
    private val context: Context) {

    private lateinit var firebaseListener: FirebaseListener
    private lateinit var firebaseRegisterUserListener: FirebaseRegisterUserListener
    private lateinit var firebaseLoginUserListener: FirebaseLoginUserListener
    private lateinit var firebaseReadUserListener: FirebaseReadUserListener
    private lateinit var firebaseUpdateUserListener: FirebaseUpdateUserListener
    private lateinit var firebaseNoteListener: FirebaseNoteListener
    var firebaseReadNoteListener: FirebaseReadNoteListener? = null
        set(value) {
            field = value
            firebaseRepository.firebaseReadNoteListener = value
        }
    private val db = Firebase.database
    private lateinit var reference: DatabaseReference

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseRepository = FirebaseRepository(context)

    private val USER_TABLE = "User"
    private val NOTE_TABLE = "Note"

    constructor(context: Context, firebaseRegisterUserListener: FirebaseRegisterUserListener) : this(context) {
        this.firebaseRegisterUserListener = firebaseRegisterUserListener
    }

    constructor(context: Context, firebaseLoginUserListener: FirebaseLoginUserListener) : this(context) {
        this.firebaseLoginUserListener = firebaseLoginUserListener
    }

    constructor(context: Context, firebaseReadUserListener: FirebaseReadUserListener) : this(context) {
        this.firebaseReadUserListener = firebaseReadUserListener
        firebaseRepository.firebaseReadUserListener = firebaseReadUserListener
    }

    constructor(context: Context, firebaseUpdateUserListener: FirebaseUpdateUserListener) : this(context) {
        this.firebaseUpdateUserListener = firebaseUpdateUserListener
        firebaseRepository.firebaseUpdateUserListener = firebaseUpdateUserListener
    }

    constructor(context: Context,
                firebaseReadUserListener: FirebaseReadUserListener,
                firebaseUpdateUserListener: FirebaseUpdateUserListener) : this (context) {
        this.firebaseReadUserListener = firebaseReadUserListener
        this.firebaseUpdateUserListener = firebaseUpdateUserListener
        firebaseRepository.firebaseReadUserListener = firebaseReadUserListener
        firebaseRepository.firebaseUpdateUserListener = firebaseUpdateUserListener
                }

    constructor(context: Context, firebaseNoteListener: FirebaseNoteListener) : this(context) {
        this.firebaseNoteListener = firebaseNoteListener
        firebaseRepository.firebaseNoteListener = firebaseNoteListener
    }


    private fun connectUserRef () {
        reference = db.getReference(USER_TABLE)
    }

    private fun connectNoteRef (userId: String) {
        reference = db.getReference(NOTE_TABLE).child(userId)
    }

    // -- NOTE --

    fun addNote(note: Note) {
        val firebaseUser = auth.currentUser
        val userId = firebaseUser!!.uid
        firebaseRepository.addNote(note, userId)
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

    fun getNotes() {
        val userId = auth.currentUser!!.uid
        firebaseRepository.getNotes(userId)
    }

    // -- USER --

    fun changePasswordUser(oldPassword: String, newPassword: String) {
        val firebaseUser = auth.currentUser
        val email = firebaseUser!!.email!!
        val credential = EmailAuthProvider.getCredential(email, oldPassword)

        firebaseUser?.reauthenticate(credential)
            ?.addOnCompleteListener {
                task ->
                    if (task.isSuccessful) {
                        firebaseUser.updatePassword(newPassword).addOnCompleteListener {
                            updateTask ->
                                if (updateTask.isSuccessful) {
                                    firebaseUpdateUserListener.onUpdateUserSuccess()
                                }
                                else {
                                    firebaseUpdateUserListener.onUpdateUserFailure()
                                }
                        }
                    }
            }
//        connectUserRef()
//        reference.child(user.userName).setValue(user)
//            .addOnCompleteListener {
//                val title = context.applicationContext.getString(R.string.Annoucement)
//                val message = context.applicationContext.getString(R.string.change_password_success)
//                showDialog(title, message)
//                Timer().schedule(3000) {
//                    (context as Activity).finish()
//                }
//            }
    }

    fun updateUser(user: User) {
        val firebaseUser: FirebaseUser? = auth.currentUser
        val id = firebaseUser?.uid
        val email = firebaseUser?.email
        user.email = email!!
        firebaseRepository.updateUser(user, id)
    }

    fun registerUser(user: User) {
        auth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
                task ->
            if (task.isSuccessful) {
                val firebaseUser: FirebaseUser? = auth.currentUser
                firebaseUser?.sendEmailVerification()
                val id = firebaseUser?.uid

                firebaseRepository.writeUserData(user, id)

                firebaseRegisterUserListener!!.onRegisterUserSuccess()

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
                    firebaseRegisterUserListener.onFailure()

                } catch (e: FirebaseAuthUserCollisionException) {
                    val title = context.applicationContext.getString(R.string.invalid_email_error)
                    val message = context.applicationContext
                        .getString(R.string.email_has_been_used_please_choose_another_email)
                    showDialog(title, message)
                    firebaseRegisterUserListener.onFailure()

                } catch (e: Exception) {
                    Log.e("FirebaseAuthentication", e.message!!)
                    firebaseRegisterUserListener.onFailure()
                }
            }
        }
    }

    fun loginUser(user: User) {
        auth.signInWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
                task ->
            if (task.isSuccessful) {

                // Get instance current user
                val firebaseUser = auth.currentUser

                if (firebaseUser!!.isEmailVerified) {
                    val message = context.getString(R.string.login_success)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    firebaseLoginUserListener.onLoginUserSuccess(auth.currentUser)
                }
                else {
                    firebaseUser.sendEmailVerification()
                    auth.signOut()
                    showAlertDialog()
                }
            }
            else {
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidUserException) {
                    val message = context.getString(R.string.user_not_exist_or_wrong_password)
                    val title = context.getString(R.string.login_error)
                    showDialog(title, message)
                    firebaseLoginUserListener.onFailure()
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    val message = context.getString(R.string.user_not_exist_or_wrong_password)
                    val title = context.getString(R.string.login_error)
                    showDialog(title, message)
                    firebaseLoginUserListener.onFailure()
                }
                catch (e: Exception) {
                    Log.e("FirebaseAuthentication", e.message!!)
                    firebaseLoginUserListener.onFailure()
                }
            }

        }
    }

    private fun showAlertDialog() {
        val alertDialog = android.app.AlertDialog.Builder(context)
        val title = context.getString(R.string.email_not_verified)
        val message = context.getString(R.string.please_verify_your_email)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)

        alertDialog.setPositiveButton("Continue") { dialog, _ ->
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        alertDialog.create()
        alertDialog.show()
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

    fun getUserFromFirebaseUser(firebaseUser: FirebaseUser) {
        val id = firebaseUser.uid
        firebaseRepository.getUserFromId(id)
    }


}