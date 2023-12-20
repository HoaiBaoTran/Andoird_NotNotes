package com.example.notnotes.userservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityProfileBinding
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.model.User

class ProfileActivity :
    AppCompatActivity(),
    FirebaseReadUserListener {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var database: FirebaseService
    private lateinit var user: User

    private lateinit var NO_INFORMATION: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        NO_INFORMATION = getString(R.string.no_information)

        database = FirebaseService(this, this)
        val firebaseUser = database.auth.currentUser
        if (firebaseUser != null) {
            database.getUserFromFirebaseUser(firebaseUser)
        }

        binding.progressBar.visibility = View.VISIBLE

        binding.btnImage.setOnClickListener {
            openEditProfileActivity()
        }
    }

    private fun loadUserInfo() {
        binding.apply {
            tvNameInfo.text = user.fullName
            tvName.text = user.fullName
            tvEmailInfo.text = user.email
        }

        binding.tvPhoneNumberInfo.text = if (user.phoneNumber == null) {
            NO_INFORMATION
        } else {
            user.phoneNumber
        }

        binding.tvAddressInfo.text = if(user.address == null) {
            NO_INFORMATION
        }
        else {
            user.address
        }

        binding.tvJobInfo.text = if (user.job == null) {
            NO_INFORMATION
        }
        else {
            user.job
        }

        binding.tvHomepageInfo.text = if (user.homepage == null) {
            NO_INFORMATION
        }
        else {
            user.homepage
        }
    }

    private fun openEditProfileActivity () {
        val editProfileIntent = Intent(this, EditProfileActivity::class.java)
        startActivity(editProfileIntent)

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

}