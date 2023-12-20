package com.example.notnotes.userservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseConnection
import com.example.notnotes.databinding.ActivitySignupBinding
import com.example.notnotes.listener.FirebaseRegisterUserListener
import com.example.notnotes.model.User
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity(), FirebaseRegisterUserListener {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var database: FirebaseConnection


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater);
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        database = FirebaseConnection(this, this)

        binding.btnSignup.setOnClickListener {
            if (!isValidField(binding.etNameSignup)
                || !isValidField(binding.etEmailSignup)
                || !isValidField(binding.etPasswordSignup)
                || !isValidField(binding.etConfirmPasswordSignup))
            {
                val title = getString(R.string.empty_field_error)
                val message = getString(R.string.please_fill_all_the_field)
                showDialog(title, message)
            } else {
                val email = binding.etEmailSignup.text.toString()
                val password = binding.etPasswordSignup.text.toString()
                val confirmPassword = binding.etConfirmPasswordSignup.text.toString()

                if (!isValidString(password)) {
                    val title = getString(R.string.empty_field_error)
                    val message = getString(R.string.field_not_contain_blank_space)
                    showDialog(title, message)
                }

                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    val title = getString(R.string.invalid_email_error)
                    val message = getString(R.string.your_email_is_invalid_message)
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
                    val user = getUserFromField()
                    database.registerUser(user)
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getUserFromField() : User {
        val fullName = binding.etNameSignup.text.toString()
        val email = binding.etEmailSignup.text.toString()
        val password = binding.etPasswordSignup.text.toString()
        return User(fullName, email, password)
    }

    private fun isValidString (text: String) : Boolean {
        val regexPattern = "^\\w*\$"
        val pattern = Pattern.compile(regexPattern)
        val matcher = pattern.matcher(text)
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

    override fun onFailure() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onRegisterUserSuccess() {
        binding.progressBar.visibility = View.GONE
    }
}