package com.example.notnotes.userservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityChangePasswordBinding
import com.example.notnotes.listener.FirebaseListener
import com.example.notnotes.listener.FirebaseUpdateUserListener
import com.example.notnotes.model.Note
import com.example.notnotes.model.User
import java.util.Timer
import java.util.regex.Pattern
import kotlin.concurrent.schedule

class ChangePasswordActivity :
    AppCompatActivity(),
    FirebaseUpdateUserListener {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var database: FirebaseService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initShowHidePasswordFeature()

        database = FirebaseService(this, this)

        binding.btnChangePassword.setOnClickListener {
            if (!isValidField(binding.etOldPassword)
                || !isValidField(binding.etNewPassword)
                || !isValidField(binding.etConfirmNewPassword))
            {
                val title = getString(R.string.empty_field_error)
                val message = getString(R.string.please_fill_all_the_field)
                showDialog(title, message)
            }
            else {
                val password = binding.etNewPassword.text.toString()
                val confirmPassword = binding.etConfirmNewPassword.text.toString()
                if (!validPassword(password)) {
                    val message = getString(R.string.password_pattern)
                    binding.etNewPassword.error = message
                    binding.etNewPassword.requestFocus()
                }
                else if (!comparePassword(password, confirmPassword)) {
                    val message = getString(R.string.password_unfit)
                    binding.etNewPassword.error = message
                    binding.etConfirmNewPassword.error = message
                    binding.etConfirmNewPassword.requestFocus()
                }
                else {
                    binding.progressBar.visibility = View.VISIBLE
                    val oldPassword = binding.etOldPassword.text.toString()
                    val newPassword = binding.etNewPassword.text.toString()
                    database.changePasswordUser(oldPassword, newPassword)
                }
            }
        }

    }

    private fun initShowHidePasswordFeature() {
        binding.imgOldPassword.setOnClickListener {
            if (binding.etOldPassword.transformationMethod
                    .equals(HideReturnsTransformationMethod.getInstance())) {
                binding.etOldPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgOldPassword.setImageResource(R.drawable.hide)
            }
            else {
                binding.etOldPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgOldPassword.setImageResource(R.drawable.view)
            }
        }

        binding.imgNewPassword.setOnClickListener {
            if (binding.etNewPassword.transformationMethod
                    .equals(HideReturnsTransformationMethod.getInstance())) {
                binding.etNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgNewPassword.setImageResource(R.drawable.hide)
            }
            else {
                binding.etNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgNewPassword.setImageResource(R.drawable.view)
            }
        }

        binding.imgConfirmNewPassword.setOnClickListener {
            if (binding.etConfirmNewPassword.transformationMethod
                    .equals(HideReturnsTransformationMethod.getInstance())) {
                binding.etConfirmNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgConfirmNewPassword.setImageResource(R.drawable.hide)
            }
            else {
                binding.etConfirmNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgConfirmNewPassword.setImageResource(R.drawable.view)
            }
        }
    }


    private fun comparePassword(
        password: String,
        confirmPassword: String) : Boolean {
        return password == confirmPassword
    }

    private fun validPassword (password: String) : Boolean {
        val regexPattern =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}"
        val pattern = Pattern.compile(regexPattern)
        val matcher = pattern.matcher(password)
        return matcher.find()
    }

    private fun isValidField(editText: EditText) : Boolean {
        if (editText.text.isEmpty()) {
            val message = getString(R.string.please_fill_all_the_field)
            editText.error = message
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

    override fun onUpdateUserSuccess() {
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.change_password_success)
        showDialog(title, message)
        binding.progressBar.visibility = View.GONE
        Timer().schedule(3000) {
            finish()
        }
    }

    override fun onUpdateUserFailure() {
        binding.progressBar.visibility = View.GONE
    }

}