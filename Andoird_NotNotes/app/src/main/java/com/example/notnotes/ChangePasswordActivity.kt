package com.example.notnotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.databinding.ActivityChangePasswordBinding
import com.example.notnotes.model.User
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val SUCCESS_CODE = 200
    private val NOT_FOUND_CODE = 404

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

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
                    // CODE CHANGEPASSWORD HERE
                    val id = getIdUserSession()
                    isOldPassword(id, password)
                }

            }
        }

    }

    private fun getIdUserSession(): Int {
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val id = sharedPreferences.getInt("id", -1)
        return id
    }

    private fun isOldPassword(id: Int, newPassword: String) {
        val url = "http://10.0.2.2:8081/api/users/$id"

        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
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
                            showDialog("Lỗi", "Email đã được sử dụng")
                        }
                        else if (jsonCode == SUCCESS_CODE){
                            val item = json.getJSONArray("data").getJSONObject(0)
                            val password = item.getString("password")
                            if (password != binding.etOldPassword.text.toString()) {
                                showDialog("Lỗi", "Sai mật khẩu hiện tại")
                            }
                            else {
                                changePasswordDatabase(id, newPassword)
                            }
                        }
                    }

                } catch (e: Exception) {
                    Log.d("MAIN_ACTIVITY_EXCEPTION", e.message.toString())
                }
            }
        })
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

    private fun changePasswordDatabase(id: Int, newPassword: String) {
        val url = "http://10.0.2.2:8081/api/users/password/$id"
        val body = FormBody.Builder()
            .add("password", newPassword)
            .build()

        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .put(body)
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
                            showDialog("Lỗi", "Cập nhật mật khẩu thất bại")
                        }
                        else if (jsonCode == SUCCESS_CODE){
                            showDialog("Thông báo", "Cập nhật mật khẩu thành công")
                            Handler().postDelayed({
                                finish()
                            }, 3000)
                        }
                    }

                } catch (e: Exception) {
                    Log.d("MAIN_ACTIVITY_EXCEPTION", e.message.toString())
                }
            }
        })
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