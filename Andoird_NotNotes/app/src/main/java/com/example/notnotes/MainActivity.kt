package com.example.notnotes

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.notnotes.databinding.ActivityMainBinding
import com.example.notnotes.model.User
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = getUserSession()

        binding.tvHello.text = "Hello $user"

        callApi()
    }

    private fun getUserSession(): User {
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val id = sharedPreferences.getInt("id", -1)
        val name = sharedPreferences.getString("name", "")
        val password = ""
        val email = sharedPreferences.getString("email", "")
        val phoneNumber = sharedPreferences.getString("phoneNumber", null)
        val address = sharedPreferences.getString("address", null)
        val job  = sharedPreferences.getString("job", null)
        val homepage = sharedPreferences.getString("homepage", null)
        return User(id, name!!, email!!, password, phoneNumber, address, job, homepage)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_profile -> openProfileActivity()
            R.id.menu_item_logout -> logoutAccount()
            R.id.menu_item_change_password -> openChangePasswordActivity()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun logoutAccount() {
        deleteSharedPreferences("MyPreferences")
        openLoginActivity()
    }

    override fun deleteSharedPreferences(prefName: String): Boolean {
        val sharedPreferences: SharedPreferences = getSharedPreferences(prefName, MODE_PRIVATE)

        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        return true

    }

    private fun openChangePasswordActivity() {
        val changePasswordIntent = Intent(this, ChangePasswordActivity::class.java)
        startActivity(changePasswordIntent)
    }

    private fun openLoginActivity() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    private fun openProfileActivity() {
        val profileIntent = Intent(this, ProfileActivity::class.java)
        startActivity(profileIntent)
    }

    private fun callApi() {
        val url = "http://10.0.2.2:8081/api/users"
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(url).build();
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.d("MAIN_ACTIVITY_FAIL", e.message.toString());
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                try {
                    val responseData = response.body?.string();
                    val json  = JSONObject(responseData!!)
                    val jsonArray = json.getJSONArray("data")
                    runOnUiThread {
                        for (index in 0 until jsonArray.length()) {
                            Log.d("MAIN_ACTIVITY_SUCCESS", jsonArray.getJSONObject(index).toString())
                        }
                    }
                } catch (e: Exception) {
                    Log.d("MAIN_ACTIVITY_EXCEPTION", e.message.toString())
                }
            }
        })
    }


}