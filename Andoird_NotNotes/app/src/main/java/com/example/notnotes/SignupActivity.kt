package com.example.notnotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.databinding.ActivitySignupBinding
import com.example.notnotes.model.User
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater);
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        binding.btnSignup.setOnClickListener {
            if (!validField(binding.etNameSignup)
                || !validField(binding.etEmailSingup)
                || !validField(binding.etPasswordSignup)
                || !validField(binding.etConfirmPasswordSignup))
            {
                val title = "Lỗi thông tin trống"
                val message = "Xin vui lòng điền hết thông tin"
                showDialog(title, message)
            } else {
                val name = binding.etNameSignup.text.toString()
                val email = binding.etEmailSingup.text.toString()
                val password = binding.etPasswordSignup.text.toString()
                val confirmPassword = binding.etConfirmPasswordSignup.text.toString()

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
                    val user = User(name, email, password)
                    addUser(user)
                }


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

    private fun validField(editText: EditText) : Boolean {
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

    private fun addUser(user: User) {
        val url = "http://10.0.2.2:8081/api/users"
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("name", user.name)
            .add("email", user.email)
            .add("password", user.password)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .post(formBody)
            .build();

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("MAIN_ACTIVITY_FAIL", e.message.toString());
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                try {
                    val responseData = response.body?.string();
                    val json  = JSONObject(responseData!!)
                    val jsonMessage = json.getString("message")
                    runOnUiThread {
                        showDialog("Thông báo", jsonMessage + ". Sau 5s tự động chuyển sang màn hình đăng nhập")
                        Handler().postDelayed({
                            openLoginActivity()
                        }, 5000)
                    }
                } catch (e: Exception) {
                    Log.d("MAIN_ACTIVITY_EXCEPTION", e.message.toString())
                }
            }
        })
    }

    private fun openLoginActivity () {
        val loginActivity = Intent(this, LoginActivity::class.java)
        startActivity(loginActivity)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}