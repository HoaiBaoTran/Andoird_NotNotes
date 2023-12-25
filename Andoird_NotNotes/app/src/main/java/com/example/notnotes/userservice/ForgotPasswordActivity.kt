package com.example.notnotes.userservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityForgotPasswordBinding
import com.example.notnotes.listener.FirebaseResetPasswordListener
import java.util.Timer
import java.util.regex.Pattern
import kotlin.concurrent.schedule

class ForgotPasswordActivity :
    AppCompatActivity(),
    FirebaseResetPasswordListener {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var database: FirebaseService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseService(this, this)

        binding.btnReset.setOnClickListener {
            val email = binding.etEmailForgot.text.toString()
            if (email.isEmpty()) {
                val message = getString(R.string.email_is_not_empty)
                binding.etEmailForgot.error = message
                binding.etEmailForgot.requestFocus()
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                val message = getString(R.string.invalid_email_error)
                binding.etEmailForgot.error = message
                binding.etEmailForgot.requestFocus()
            }
            else {
                binding.progressBar.visibility = View.VISIBLE
                database.resetPassword(email)
            }
        }
    }

    override fun onResetPasswordSuccess() {
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.please_check_your_email_for_password_reset_link)
        showDialog(title, message)

        Timer().schedule(3000) {
            val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        binding.progressBar.visibility = View.GONE
    }

    override fun onResetPasswordFailure() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
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