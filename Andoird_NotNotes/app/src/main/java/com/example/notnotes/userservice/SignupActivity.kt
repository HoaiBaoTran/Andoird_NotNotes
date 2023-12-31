package com.example.notnotes.userservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivitySignupBinding
import com.example.notnotes.listener.FirebaseRegisterUserListener
import com.example.notnotes.model.User
import java.util.Timer
import java.util.regex.Pattern
import kotlin.concurrent.schedule

class SignupActivity : AppCompatActivity(), FirebaseRegisterUserListener {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var database: FirebaseService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater);
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        database = FirebaseService(this, this)

        binding.imgPassword.setOnClickListener {
            if (binding.etPasswordSignup.transformationMethod
                    .equals(HideReturnsTransformationMethod.getInstance())) {
                binding.etPasswordSignup.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgPassword.setImageResource(R.drawable.hide)
            }
            else {
                binding.etPasswordSignup.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgPassword.setImageResource(R.drawable.view)
            }
        }

        binding.imgConfirmPassword.setOnClickListener {
            if (binding.etConfirmPasswordSignup.transformationMethod
                    .equals(HideReturnsTransformationMethod.getInstance())) {
                binding.etConfirmPasswordSignup.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgConfirmPassword.setImageResource(R.drawable.hide)
            }
            else {
                binding.etConfirmPasswordSignup.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgConfirmPassword.setImageResource(R.drawable.view)
            }
        }


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
                    val message = getString(R.string.your_email_is_invalid_message)
                    binding.etEmailSignup.requestFocus()
                    binding.etEmailSignup.error = message
                }

                else if (!isValidPassword(password)) {
                    val message = getString(R.string.password_pattern)
                    binding.etPasswordSignup.requestFocus()
                    binding.etPasswordSignup.error = message
                }
                else if (!comparePassword(password, confirmPassword)) {
                    val message = getString(R.string.password_unfit)
                    binding.etConfirmPasswordSignup.requestFocus()
                    binding.etPasswordSignup.error = message
                    binding.etConfirmPasswordSignup.error = message
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
            val message = getString(R.string.please_fill_all_the_field)
            editText.error = message
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
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.signup_success_message)
        showDialog(title, message)

        Timer().schedule(3000) {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}