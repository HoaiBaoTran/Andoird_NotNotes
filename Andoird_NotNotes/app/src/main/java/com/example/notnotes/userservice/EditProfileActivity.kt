package com.example.notnotes.userservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseConnection
import com.example.notnotes.databinding.ActivityEditProfileBinding
import com.example.notnotes.listener.FirebaseListener
import com.example.notnotes.model.User

class EditProfileActivity : AppCompatActivity(), FirebaseListener {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var database: FirebaseConnection
    private lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        database = FirebaseConnection(this, this)

        user = getUserSession()
        loadUserInfo()

        binding.btnSave.setOnClickListener {
            if (!isValidField(binding.etName))
            {
                val title = getString(R.string.name_error)
                val message = getString(R.string.user_fullname_not_empty)
                showDialog(title, message)
            }
            else {
                val user = getUserFromField()
                updateUserSession(user)
                val userName = getUserNameUserSession()
                database.checkUsernameExist(userName!!)
            }


        }
    }

    private fun getUserNameUserSession(): String? {
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        return sharedPreferences.getString("userName", "")
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

    private fun getUserFromField() : User {

        val user = User()
        user.fullName = binding.etName.text.toString()

        val email = binding.etEmail.text.toString()
        val phoneNumber = binding.etPhoneNumber.text.toString()
        val address = binding.etAddress.text.toString()
        val job = binding.etJob.text.toString()
        val homepage = binding.etHomepage.text.toString()

        if (email.isNotEmpty()) {
            user.email = email
        }

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
            putString("fullName", user.fullName)
            putString("email", user.email)
            putString("phoneNumber", user.phoneNumber)
            putString("address", user.address)
            putString("job", user.job)
            putString("homepage", user.homepage)
            apply()
        }
    }

    private fun getUserSession(): User {
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val fullName = sharedPreferences.getString("fullName", "")
        val userName = sharedPreferences.getString("userName", "")
        val password = ""
        val email = sharedPreferences.getString("email", null)
        val phoneNumber = sharedPreferences.getString("phoneNumber", null)
        val address = sharedPreferences.getString("address", null)
        val job  = sharedPreferences.getString("job", null)
        val homepage = sharedPreferences.getString("homepage", null)
        return User(fullName!!, userName!!, password, email, phoneNumber, address, job, homepage)
    }

    private fun loadUserInfo() {
        binding.apply {
            etName.setText(user.fullName)
            tvNameEditProfile.text = user.fullName
        }

        if (user.address != null) {
            binding.etAddress.setText(user.address)
        }
        else {
            binding.etAddress.setText("")
        }

        if (user.homepage != null) {
            binding.etHomepage.setText(user.homepage)
        }
        else {
            binding.etHomepage.setText("")
        }

        if (user.phoneNumber != null) {
            binding.etPhoneNumber.setText(user.phoneNumber)
        }
        else {
            binding.etPhoneNumber.setText("")
        }

        if (user.job != null) {
            binding.etJob.setText(user.job)
        }
        else {
            binding.etJob.setText("")
        }

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

    override fun onUsernameExist(user: User) {
        val userField = getUserFromField()
        user.email = userField.email
        user.fullName = userField.fullName
        user.job = userField.job
        user.address = userField.address
        user.homepage = userField.homepage
        user.phoneNumber = userField.phoneNumber
        database.updateUserData(user)
    }

    override fun onStartAccess() {

    }

    override fun onUserNotExist() {

    }

    override fun onFailure() {

    }


}