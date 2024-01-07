package com.example.notnotes.database

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.listener.FirebaseLoginUserListener
import com.example.notnotes.listener.FirebaseNoteListener
import com.example.notnotes.listener.FirebaseReadLabelListener
import com.example.notnotes.listener.FirebaseReadNoteListener
import com.example.notnotes.listener.FirebaseRegisterUserListener
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.FirebaseResetPasswordListener
import com.example.notnotes.listener.FirebaseUpdateUserListener
import com.example.notnotes.listener.FirebaseUploadImageListener
import com.example.notnotes.model.Label
import com.example.notnotes.model.Note
import com.example.notnotes.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

class FirebaseService(
    private val context: Context) {
    private lateinit var firebaseRegisterUserListener: FirebaseRegisterUserListener
    private lateinit var firebaseLoginUserListener: FirebaseLoginUserListener
    private lateinit var firebaseReadUserListener: FirebaseReadUserListener
    private lateinit var firebaseUpdateUserListener: FirebaseUpdateUserListener
    private lateinit var firebaseNoteListener: FirebaseNoteListener
    private lateinit var firebaseResetPasswordListener: FirebaseResetPasswordListener
    private lateinit var firebaseUploadImageListener: FirebaseUploadImageListener
    private lateinit var firebaseReadLabelListener: FirebaseReadLabelListener

    var firebaseReadNoteListener: FirebaseReadNoteListener? = null
        set(value) {
            field = value
            firebaseRepository.firebaseReadNoteListener = value
        }

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val storageRef= FirebaseStorage.getInstance().getReference("DisplayPics")
    private val firebaseRepository = FirebaseRepository(context)


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
                firebaseUpdateUserListener: FirebaseUpdateUserListener) :
            this (context) {
                this.firebaseReadUserListener = firebaseReadUserListener
                this.firebaseUpdateUserListener = firebaseUpdateUserListener
                firebaseRepository.firebaseReadUserListener = firebaseReadUserListener
                firebaseRepository.firebaseUpdateUserListener = firebaseUpdateUserListener
            }

    constructor(context: Context, firebaseNoteListener: FirebaseNoteListener) : this(context) {
        this.firebaseNoteListener = firebaseNoteListener
        firebaseRepository.firebaseNoteListener = firebaseNoteListener
    }

    constructor(context: Context, firebaseResetPasswordListener: FirebaseResetPasswordListener)
            : this(context) {
        this.firebaseResetPasswordListener = firebaseResetPasswordListener

    }

    constructor(context: Context, firebaseUploadImageListener: FirebaseUploadImageListener) : this(context) {
        this.firebaseUploadImageListener = firebaseUploadImageListener
    }

    constructor(
        context: Context,
        fireNoteListener: FirebaseNoteListener,
        fireReadNoteListener: FirebaseReadNoteListener,
        readUserListener: FirebaseReadUserListener) : this (context) {
        this.firebaseNoteListener = fireNoteListener
        this.firebaseReadNoteListener = fireReadNoteListener
        this.firebaseReadUserListener = readUserListener
        firebaseRepository.firebaseNoteListener = fireNoteListener
        firebaseRepository.firebaseReadNoteListener = fireReadNoteListener
        firebaseRepository.firebaseReadUserListener = readUserListener
    }

    constructor(
        context: Context,
        firebaseReadLabelListener: FirebaseReadLabelListener,
        firebaseReadUserListener: FirebaseReadUserListener
    ) : this(context) {
        this.firebaseReadLabelListener = firebaseReadLabelListener
        this.firebaseReadUserListener = firebaseReadUserListener
    }

    // -- NOTE --

    fun addNote(note: Note) {
        val firebaseUser = auth.currentUser
        val userId = firebaseUser!!.uid
        firebaseRepository.addNote(note, userId)
    }

    fun editNote(note: Note) {
        val firebaseUser = auth.currentUser
        val userId = firebaseUser!!.uid
        firebaseRepository.editNote(note, userId)
    }

    fun deleteNote(note: Note) {
        val firebaseUser = auth.currentUser
        val userId = firebaseUser!!.uid
        firebaseRepository.deleteNote(note, userId)
    }

    fun getNotes() {
        val userId = auth.currentUser!!.uid
        firebaseRepository.getNotes(userId)
    }

    fun getNotesByLabel(label: String) {
        val userId = auth.currentUser!!.uid
        firebaseRepository.getNotesByLabel(userId, label)
    }

    fun getNotesByTitle(title: String) {
        val userId = auth.currentUser!!.uid
        firebaseRepository.getNotesByTitle(userId, title)
    }

    fun getNotesByContent(content: String) {
        val userId = auth.currentUser!!.uid
        firebaseRepository.getNotesByContent(userId, content)
    }

    // -- USER --
    fun changeProfilePic(uriImage: Uri) {
        val firebaseUser = auth.currentUser!!
        val fileReference = storageRef.child(firebaseUser.uid
                + "." + getFileExtension(uriImage))
//        val uri = firebaseUser.photoUrl
//        Picasso.with(context).load(uri).into(imgView)

        fileReference.putFile(uriImage)
            .addOnSuccessListener {
            fileReference.downloadUrl.addOnSuccessListener { uri ->
                val downloadUri: Uri = uri

                val userProfileChangeRequest: UserProfileChangeRequest = UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUri).build()

                firebaseUser.updateProfile(userProfileChangeRequest)
                firebaseUploadImageListener.onUploadImageSuccess()
            }
        }
            .addOnFailureListener {
                Log.d("FirebaseService", it.message.toString())
                firebaseUploadImageListener.onUploadImageFailure()
            }
    }

    private fun getFileExtension(uriImage: Uri): String? {
        val contentResolver = context.applicationContext.contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uriImage))
    }

    fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {task ->
            if (task.isSuccessful) {
                firebaseResetPasswordListener.onResetPasswordSuccess()
            }
            else {
                try {
                    throw task.exception!!
                }
                catch (e: FirebaseAuthInvalidUserException) {
                    val message = context.getString(R.string.user_not_exist_or_wrong_password)
                    val title = context.getString(R.string.user_account_error)
                    showDialog(title, message)
                    firebaseUpdateUserListener.onUpdateUserFailure()
                }
                catch (e: Exception) {
                    Log.e("FirebaseService", e.message!!)
                    firebaseUpdateUserListener.onUpdateUserFailure()
                }
            }
        }
    }

    fun changePasswordUser(oldPassword: String, newPassword: String) {
        val firebaseUser = auth.currentUser
        val email = firebaseUser!!.email!!
        val credential = EmailAuthProvider.getCredential(email, oldPassword)

        firebaseUser.reauthenticate(credential)
            .addOnCompleteListener {
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
                firebaseRegisterUserListener.onRegisterUserSuccess()

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
                    Log.e("FirebaseService", e.message!!)
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

        alertDialog.setPositiveButton("Continue") { _, _ ->
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

    fun getLabels() {
        val userId = auth.currentUser!!.uid
        firebaseRepository.getLabels(userId)
    }

    fun addLabel(label: Label) {
        val firebaseUser = auth.currentUser
        val userId = firebaseUser!!.uid
        firebaseRepository.addLabel(label, userId)
    }
}