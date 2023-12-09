package com.example.notnotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.databinding.ActivityLoginBinding
import com.example.notnotes.model.User
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val SUCCESS_CODE = 200
    private val NOT_FOUND_CODE = 404
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignup.setOnClickListener {
            openSignupActivity()
        }

        binding.btnLogin.setOnClickListener {
            if (!isValidField(binding.etEmailLogin)
                || !isValidField(binding.etPasswordLogin)) {
                val title = "Lỗi thông tin trống"
                val message = "Xin vui lòng điền hết thông tin"
                showDialog(title, message)
            }
            else {
                val email = binding.etEmailLogin.text.toString()
                val password = binding.etPasswordLogin.text.toString()
                val user = User(email, password)
                isMailExist(user)
            }
        }
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

    private fun isMailExist(user: User) {
        val url = "http://10.0.2.2:8081/api/users/email"
        val body = FormBody.Builder()
            .add("email", user.email)
            .build()

        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build();

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("MAIN_ACTIVITY_FAIL", e.message.toString());
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                try {
                    val responseData = response.body?.string();
                    val json  = JSONObject(responseData!!)
                    val jsonCode = json.getInt("code")
                    runOnUiThread {
                        if (jsonCode == NOT_FOUND_CODE) {
                            showDialog("Lỗi", "Email hoặc mật khẩu sai!!!")
                        }
                        else if (jsonCode == SUCCESS_CODE){
                            val userRes = json.getJSONArray("data").getJSONObject(0)
                            val password = userRes.getString("password")
                            if (user.password != password) {
                                showDialog("Lỗi", "Email hoặc mật khẩu sai!!!")
                            }
                            else {
                                showDialog("Thông báo", "Đăng nhập thành công. Sau 5s tự động chuyển sang màn hình đăng nhập")
                                Handler().postDelayed({
                                    openMainActivity()
                                }, 5000)
                            }
                        }
                    }

                } catch (e: Exception) {
                    Log.d("MAIN_ACTIVITY_EXCEPTION", e.message.toString())
                }
            }
        })
    }

    private fun openMainActivity () {
        val mainIntent = Intent(this, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }

    private fun openSignupActivity() {
        val signupIntent = Intent(this, SignupActivity::class.java)
        startActivity(signupIntent)
    }

    private fun isValidField(editText: EditText) : Boolean {
        if (editText.text.isEmpty()) {
            editText.requestFocus()
            return false
        }
        return true
    }
}