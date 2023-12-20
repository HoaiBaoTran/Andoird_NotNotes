package com.example.notnotes.userservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityEditProfileBinding
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.FirebaseUpdateUserListener
import com.example.notnotes.model.User
import java.util.Timer
import kotlin.concurrent.schedule

class EditProfileActivity :
    AppCompatActivity(),
    FirebaseReadUserListener,
    FirebaseUpdateUserListener {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var database: FirebaseService
    private lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.progressBar.visibility = View.GONE
        database = FirebaseService(this, this, this)
        val firebaseUser = database.auth.currentUser
        if (firebaseUser != null) {
            database.getUserFromFirebaseUser(firebaseUser)
        }

        binding.btnSave.setOnClickListener {
            if (!isValidField(binding.etName))
            {
                val title = getString(R.string.name_error)
                val message = getString(R.string.user_fullname_not_empty)
                showDialog(title, message)
            }
            else {
                val user = getUserFromField()
                database.updateUser(user)
                binding.progressBar.visibility = View.VISIBLE
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

    private fun getUserFromField() : User {

        val user = User()
        user.fullName = binding.etName.text.toString()

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


    override fun onReadUserSuccess(user: User) {
        this.user = user
        loadUserInfo()
        binding.progressBar.visibility = View.GONE
    }

    override fun onReadUserFailure() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onUpdateUserSuccess() {
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.update_user_success)
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