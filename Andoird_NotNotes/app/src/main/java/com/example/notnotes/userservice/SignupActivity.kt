package com.example.notnotes.userservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseConnection
import com.example.notnotes.databinding.ActivitySignupBinding
import com.example.notnotes.model.UserTemp
import java.util.Timer
import java.util.regex.Pattern
import kotlin.concurrent.schedule

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var database: FirebaseConnection

    private val SUCCESS_CODE = 200
    private val NOT_FOUND_CODE = 404

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater);
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        binding.btnSignup.setOnClickListener {
            if (!isValidField(binding.etNameSignup)
                || !isValidField(binding.etUsernameSignup)
                || !isValidField(binding.etPasswordSignup)
                || !isValidField(binding.etConfirmPasswordSignup))
            {
                val title = getString(R.string.empty_field_error)
                val message = getString(R.string.please_fill_all_the_field)
                showDialog(title, message)
            } else {
                val name = binding.etNameSignup.text.toString()
                val userName = binding.etUsernameSignup.text.toString()
                val password = binding.etPasswordSignup.text.toString()
                val confirmPassword = binding.etConfirmPasswordSignup.text.toString()

                if (!isValidUsername(userName)) {
                    val title = getString(R.string.username_error)
                    val message = getString(R.string.username_not_contain_blank_space)
                    showDialog(title, message)
                }
                else if (!isValidPassword(password)) {
                    val title = getString(R.string.password_error)
                    val message = getString(R.string.password_pattern)
                    showDialog(title, message)
                }
                else if (!comparePassword(password, confirmPassword)) {
                    val title = getString(R.string.password_error)
                    val message = getString(R.string.password_unfit)
                    showDialog(title, message)
                }
                else {
                    val user = UserTemp(name, userName, password)
                    val database = FirebaseConnection(this)
                    if (!database.isUsernameExist(userName)) {
//                        database.registerUser(user)
//                        Log.e("Signup_Activity", "Register")
                    }
//                    Log.e("Signup_Activity", database.isUsernameExist(userName).toString())
                }
            }
        }
    }

    private fun isValidUsername (username: String) : Boolean {
        val regexPattern = "^\\w*\$"
        val pattern = Pattern.compile(regexPattern)
        val matcher = pattern.matcher(username)
        return matcher.find()
    }

    private fun comparePassword(
        password: String,
        confirmPassword: String) : Boolean {
        return password == confirmPassword
    }

    private fun isValidPassword (password: String) : Boolean {
        val regexPattern =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}"
        val pattern = Pattern.compile(regexPattern)
        val matcher = pattern.matcher(password)
        return matcher.find()
    }

    private fun isValidField(editText: EditText) : Boolean {
        if (editText.text.isEmpty()) {
            editText.requestFocus()
            return false
        }
        return true
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}