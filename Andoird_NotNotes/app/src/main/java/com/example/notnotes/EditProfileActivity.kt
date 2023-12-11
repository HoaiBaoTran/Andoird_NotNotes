package com.example.notnotes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.databinding.ActivityEditProfileBinding
import com.example.notnotes.model.User
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var user: User

    private val SUCCESS_CODE = 200
    private val NOT_FOUND_CODE = 404

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        user = getUserSession()
        loadUserInfo()

        binding.btnSave.setOnClickListener {
            if (!isValidField(binding.etName) || !isValidField(binding.etEmail))
            {
                showDialog("Lỗi thông tin trống", "Email và tên không được để trống")
            }
            else {
                updateUserInfo()
                updateUserSession(user)
                updateUserDatabase(user)
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

    private fun isValidField(editText: EditText) : Boolean {
        if (editText.text.isEmpty()) {
            editText.requestFocus()
            return false
        }
        return true
    }

    private fun updateUserInfo() : User {
        user.email = binding.etEmail.text.toString()
        user.name = binding.etName.text.toString()

        val phoneNumber = binding.etPhoneNumber.text.toString()
        val address = binding.etAddress.text.toString()
        val job = binding.etJob.text.toString()
        val homepage = binding.etHomepage.text.toString()
        if (phoneNumber.isNotEmpty()) {
            user.phoneNumber = phoneNumber
        }

        if (address.isNotEmpty()) {
            user.address = address
        }

        if (job.isNotEmpty()) {
            user.job = job
        }

        if (homepage.isNotEmpty()) {
            user.homepage = homepage
        }

        return user

    }

    private fun updateUserSession(user: User) {
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("name", user.name)
            putString("email", user.email)
            putString("password", user.password)
            putString("phoneNumber", user.phoneNumber)
            putString("address", user.address)
            putString("job", user.job)
            putString("homepage", user.homepage)
            apply()
        }
    }

    private fun getUserSession(): User {
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val id = sharedPreferences.getInt("id", -1)
        val name = sharedPreferences.getString("name", "")
        val email = sharedPreferences.getString("email", "")
        val password  = sharedPreferences.getString("password", "")
        val phoneNumber = sharedPreferences.getString("phoneNumber", null)
        val address = sharedPreferences.getString("address", null)
        val job  = sharedPreferences.getString("job", null)
        val homepage = sharedPreferences.getString("homepage", null)
        return User(id, name!!, email!!, password!!, phoneNumber, address, job, homepage)
    }

    private fun loadUserInfo() {
        binding.apply {
            etName.setText(user.name)
            etEmail.setText(user.email)
            tvNameEditProfile.text = user.name
        }

        if (user.address != "null") {
            binding.etAddress.setText(user.address)
        }
        else {
            binding.etAddress.setText("")
        }

        if (user.homepage != "null") {
            binding.etHomepage.setText(user.homepage)
        }
        else {
            binding.etHomepage.setText("")
        }

        if (user.phoneNumber != "null") {
            binding.etPhoneNumber.setText(user.phoneNumber)
        }
        else {
            binding.etPhoneNumber.setText("")
        }

        if (user.job != "null") {
            binding.etJob.setText(user.job)
        }
        else {
            binding.etJob.setText("")
        }

    }

    private fun updateUserDatabase(user: User) {
        val url = "http://10.0.2.2:8081/api/users/${user.id}"
        val body = FormBody.Builder()
            .add("name", user.name)
            .add("email", user.email)
            .add("phoneNumber", user.phoneNumber.toString())
            .add("address", user.address.toString())
            .add("job", user.job.toString())
            .add("homepage", user.homepage.toString())
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
                            showDialog("Lỗi", "Email đã được sử dụng")
                        }
                        else if (jsonCode == SUCCESS_CODE){
                            showDialog("Thông báo", "Cập nhật thông tin người dùng thành công")
                            Handler().postDelayed({
                                Log.d("MAIN_ACTIVITY", user.toString())
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.profile_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.menu_item_back -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}