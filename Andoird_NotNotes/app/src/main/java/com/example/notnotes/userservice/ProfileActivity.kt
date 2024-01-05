package com.example.notnotes.userservice

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityProfileBinding
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.FirebaseUpdateUserListener
import com.example.notnotes.model.User
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class ProfileActivity :
    AppCompatActivity(),
    FirebaseReadUserListener,
    FirebaseUpdateUserListener {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var database: FirebaseService
    private lateinit var user: User

    private lateinit var NO_INFORMATION: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        NO_INFORMATION = getString(R.string.no_information)

        database = FirebaseService(this, this, this)
        getUserFromDatabase()

        binding.progressBar.visibility = View.VISIBLE

        binding.btnImgProfile.setOnClickListener {
            openUploadImageProfileActivity()
        }

        binding.btnImage.setOnClickListener {
            openEditProfileActivity()
        }
    }

    private fun getUserFromDatabase() {
        val firebaseUser = database.auth.currentUser
        if (firebaseUser != null) {
            database.getUserFromFirebaseUser(firebaseUser)
            checkIfEmailVerified(firebaseUser)
        }
    }

    private fun openUploadImageProfileActivity() {
        val uploadImageProfileIntent = Intent(this, UploadImageProfileActivity::class.java)
        startActivity(uploadImageProfileIntent)
    }

    private fun checkIfEmailVerified(firebaseUser: FirebaseUser) {
        if (!firebaseUser.isEmailVerified) {
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val alertDialog = android.app.AlertDialog.Builder(this)
        val title = getString(R.string.email_not_verified)
        val message = getString(R.string.please_verify_your_email)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)

        alertDialog.setPositiveButton("Continue") { dialog, _ ->
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_APP_EMAIL)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        alertDialog.create()
        alertDialog.show()
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

    private fun loadUserImage() {
        val uri: Uri? = database.auth.currentUser?.photoUrl

        if (uri != null) {
            Picasso.with(this).load(uri)
                .fit()
                .centerCrop()
                .into(binding.imgProfile)
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
        loadUserImage()
        binding.progressBar.visibility = View.GONE
    }

    override fun onReadUserFailure() {
        binding.progressBar.visibility = View.GONE
    }


    override fun onResume() {
        super.onResume()
        getUserFromDatabase()
    }

    override fun onUpdateUserSuccess() {
        loadUserInfo()
        loadUserImage()
    }

    override fun onUpdateUserFailure() {

    }
}