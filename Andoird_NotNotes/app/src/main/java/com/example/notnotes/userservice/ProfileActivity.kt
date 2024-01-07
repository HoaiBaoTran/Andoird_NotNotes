package com.example.notnotes.userservice

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.notnotes.MainActivity
import com.example.notnotes.R
import com.example.notnotes.appservice.SettingActivity
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityProfileBinding
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.FirebaseUpdateUserListener
import com.example.notnotes.model.User
import com.example.notnotes.noteservice.AddLabelActivity
import com.example.notnotes.noteservice.TrashActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class ProfileActivity :
    AppCompatActivity(),
    FirebaseReadUserListener,
    FirebaseUpdateUserListener,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var database: FirebaseService
    private lateinit var user: User

    private lateinit var NO_INFORMATION: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolBar,
            R.string.nav_drawer_open,
            R.string.nav_drawer_close)
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
        initNavHeaderView()
        loadUserInfo()
        loadUserImage()
        binding.progressBar.visibility = View.GONE
    }

    private fun initNavHeaderView() {
        val userName = user.fullName
//        val formattedString = getString(R.string.formatted_string, userName)
        val spannableString = SpannableString(userName)

//        val start = formattedString.indexOf(userName)
        val start = 0
        val end = start + userName.length
        val styleSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(styleSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val colorSpan = ForegroundColorSpan(resources.getColor(R.color.lightgray))
        spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val headerView = binding.navigationView.getHeaderView(0)
        val headerText: TextView = headerView.findViewById(R.id.tvNavHeader)
        val imgProfile: ImageView = headerView.findViewById(R.id.imgProfile)
        headerText.text = spannableString

        val uri: Uri? = database.auth.currentUser?.photoUrl

        if (uri != null) {
            Picasso.with(this).load(uri)
                .fit()
                .centerCrop()
                .into(imgProfile)
        }
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_notes -> {
                openMainActivity()
            }
            R.id.nav_storage -> {

            }
            R.id.nav_label -> {
                openAddLabelActivity()
            }
            R.id.nav_recycle_bin -> {
                openTrashActivity()
            }
            R.id.nav_profile -> {
                openProfileActivity()
            }
            R.id.nav_change_pass -> {
                openChangePasswordActivity()
            }
            R.id.nav_logout -> {
                logoutAccount()
            }
            R.id.nav_setting -> {
                openSettingActivity()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        if (isActivityRunning(this, MainActivity::class.java)) {
            mainIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        else {
            mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(mainIntent)
    }

    private fun openAddLabelActivity() {
        val labelIntent = Intent(this, AddLabelActivity::class.java)
        if (isActivityRunning(this, AddLabelActivity::class.java)) {
            labelIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        else {
            labelIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(labelIntent)
    }

    private fun openTrashActivity() {
        val trashIntent = Intent(this, TrashActivity::class.java)
        if (isActivityRunning(this, TrashActivity::class.java)) {
            trashIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        else {
            trashIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(trashIntent)
    }

    private fun openSettingActivity() {
        val settingIntent = Intent(this, SettingActivity::class.java)
        if (isActivityRunning(this, SettingActivity::class.java)) {
            settingIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        else {
            settingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(settingIntent)
    }

    private fun logoutAccount() {
        deleteSharedPreferences("MyPreferences")
        openLoginActivity()
    }

    private fun openChangePasswordActivity() {
        val changePasswordIntent = Intent(this, ChangePasswordActivity::class.java)
        if (isActivityRunning(this, ChangePasswordActivity::class.java)) {
            changePasswordIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        else {
            changePasswordIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(changePasswordIntent)
    }

    private fun openLoginActivity() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(loginIntent)
        finish()
    }

    private fun openProfileActivity() {
        val profileIntent = Intent(this, ProfileActivity::class.java)
        if (isActivityRunning(this, ChangePasswordActivity::class.java)) {
            profileIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        else {
            profileIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(profileIntent)
    }

    private fun isActivityRunning(context: Context, activityClass: Class<*>): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val taskInfo = am.getRunningTasks(1)[0]
        val componentName = taskInfo.topActivity
        return componentName?.className == activityClass.name
    }
}