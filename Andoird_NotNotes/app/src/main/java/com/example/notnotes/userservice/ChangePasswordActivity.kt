package com.example.notnotes.userservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseConnection
import com.example.notnotes.databinding.ActivityChangePasswordBinding
import com.example.notnotes.listener.FirebaseListener
import com.example.notnotes.model.UserTemp
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity(), FirebaseListener {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var database: FirebaseConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        database = FirebaseConnection(this, this)

        binding.btnChangePassword.setOnClickListener {
            if (!isValidField(binding.etOldPassword)
                || !isValidField(binding.etNewPassword)
                || !isValidField(binding.etConfirmNewPassword))
            {
                val title = "Lỗi thông tin trống"
                val message = "Xin vui lòng điền hết thông tin"
                showDialog(title, message)
            }
            else {
                val password = binding.etNewPassword.text.toString()
                val confirmPassword = binding.etConfirmNewPassword.text.toString()
                if (!validPassword(password)) {
                    val title = "Lỗi mật khẩu"
                    val message = "Mật khẩu ít nhất 8 ký tự và gồm 1 ký tự in hoa và 1 chữ số"
                    showDialog(title, message)
                }
                else if (!comparePassword(password, confirmPassword)) {
                    val title = "Lỗi mật khẩu"
                    val message = "Mật khẩu không khớp"
                    showDialog(title, message)
                }
                else {
                    val userName = getUserNameUserSession()
                    database.checkUsernameExist(userName!!)
                }
            }
        }

    }

    private fun getUserNameUserSession(): String? {
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        return sharedPreferences.getString("userName", "")
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

    override fun onUsernameExist(user: UserTemp) {
        val oldPassword = binding.etOldPassword.text.toString()
        if (oldPassword != user.password) {
            val title = getString(R.string.password_error)
            val message = getString(R.string.wrong_password)
            showDialog(title, message)
        }
        else {
            user.password = binding.etNewPassword.text.toString()
            database.changePasswordUser(user)
        }
    }

    override fun onStartAccess() {
    }

    override fun onUserNotExist() {
    }

    override fun onFailure() {
    }
}