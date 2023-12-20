package com.example.notnotes.userservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.MainActivity
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseConnection
import com.example.notnotes.databinding.ActivityLoginBinding
import com.example.notnotes.listener.FirebaseListener
import com.example.notnotes.model.Note
import com.example.notnotes.model.User

class LoginActivity : AppCompatActivity(), FirebaseListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: FirebaseConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseConnection(this, this)

        binding.tvSignup.setOnClickListener {
            openSignupActivity()
        }

        binding.btnLogin.setOnClickListener {
            if (!isValidField(binding.etEmailLogin)
                || !isValidField(binding.etPasswordLogin)) {
                val title = getString(R.string.empty_field_error)
                val message = getString(R.string.please_fill_all_the_field)
                showDialog(title, message)
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmailLogin.text.toString()).matches()) {
                val title = getString(R.string.invalid_email_error)
                val message = getString(R.string.your_email_is_invalid_message)
                showDialog(title, message)
            }
            else {
                val user = getUserFromField()
                database.loginUser(user)
            }
        }

        binding.tvForgetPassword.setOnClickListener {
            val title = getString(R.string.forgot_password)
            val message = getString(R.string.relax_and_try_to_remember_your_password)
            showDialog(title, message)
        }
    }

    private fun isValidField(editText: EditText) : Boolean {
        if (editText.text.isEmpty()) {
            editText.requestFocus()
            return false
        }
        return true
    }

    private fun getUserFromField() : User {
        val email = binding.etEmailLogin.text.toString()
        val password = binding.etPasswordLogin.text.toString()
        return User(email, password)
    }

    private fun checkUserData(user: User) {
//        database.checkUsernameExist(user.userName)
    }

    private fun showDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


//    private fun saveUserSession(user: User) {
//        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        editor.apply {
//            putString("fullName", user.fullName)
//            putString("email", user.email)
//            putString("userName", user.userName)
//            putString("password", "")
//            putString("phoneNumber", user.phoneNumber)
//            putString("address", user.address)
//            putString("job", user.job)
//            putString("homepage", user.homepage)
//            apply()
//        }
//    }

    private fun openMainActivity () {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }


    private fun openSignupActivity() {
        val signupIntent = Intent(this, SignupActivity::class.java)
        startActivity(signupIntent)
    }


//    override fun onUsernameExist(user: User) {
//        val userField = getUserFromField()
//        if (userField.userName != user.userName ||
//            userField.password != user.password) {
//            val title = getString(R.string.user_account_error)
//            val message = getString(R.string.wrong_username_or_password)
//            showDialog(title, message)
//        }
//        else {
//            saveUserSession(user)
//            openMainActivity()
//        }
//    }

    override fun onUserNotExist() {
        val title = getString(R.string.user_account_error)
        val message = getString(R.string.wrong_username_or_password)
        showDialog(title, message)
    }

    override fun onUsernameExist(user: User) {

    }

    override fun onStartAccess() {

    }

    override fun onFailure() {

    }

    override fun onReadNoteListComplete(notes: List<Note>) {

    }
}