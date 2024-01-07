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
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import com.example.notnotes.MainActivity
import com.example.notnotes.R
import com.example.notnotes.appservice.SettingActivity
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityChangePasswordBinding
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.FirebaseUpdateUserListener
import com.example.notnotes.model.User
import com.example.notnotes.noteservice.AddLabelActivity
import com.example.notnotes.noteservice.TrashActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import java.util.Timer
import java.util.regex.Pattern
import kotlin.concurrent.schedule

class ChangePasswordActivity :
    AppCompatActivity(),
    FirebaseUpdateUserListener,
    FirebaseReadUserListener,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var database: FirebaseService
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
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

        initShowHidePasswordFeature()

        database = FirebaseService(this, this, this)
        getUserFromDatabase()


        binding.btnChangePassword.setOnClickListener {
            if (!isValidField(binding.etOldPassword)
                || !isValidField(binding.etNewPassword)
                || !isValidField(binding.etConfirmNewPassword))
            {
                val title = getString(R.string.empty_field_error)
                val message = getString(R.string.please_fill_all_the_field)
                showDialog(title, message)
            }
            else {
                val password = binding.etNewPassword.text.toString()
                val confirmPassword = binding.etConfirmNewPassword.text.toString()
                if (!validPassword(password)) {
                    val message = getString(R.string.password_pattern)
                    binding.etNewPassword.error = message
                    binding.etNewPassword.requestFocus()
                }
                else if (!comparePassword(password, confirmPassword)) {
                    val message = getString(R.string.password_unfit)
                    binding.etNewPassword.error = message
                    binding.etConfirmNewPassword.error = message
                    binding.etConfirmNewPassword.requestFocus()
                }
                else {
                    binding.progressBar.visibility = View.VISIBLE
                    val oldPassword = binding.etOldPassword.text.toString()
                    val newPassword = binding.etNewPassword.text.toString()
                    database.changePasswordUser(oldPassword, newPassword)
                }
            }
        }

    }

    private fun getUserFromDatabase() {
        val firebaseUser = database.auth.currentUser
        Log.d("TrashActivity", "getUserFromDatabase: ${firebaseUser?.uid}")
        if (firebaseUser != null) {
            database.getUserFromFirebaseUser(firebaseUser)
            checkIfEmailVerified(firebaseUser)
        }
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

    private fun initShowHidePasswordFeature() {
        binding.imgOldPassword.setOnClickListener {
            if (binding.etOldPassword.transformationMethod
                    .equals(HideReturnsTransformationMethod.getInstance())) {
                binding.etOldPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgOldPassword.setImageResource(R.drawable.hide)
            }
            else {
                binding.etOldPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgOldPassword.setImageResource(R.drawable.view)
            }
        }

        binding.imgNewPassword.setOnClickListener {
            if (binding.etNewPassword.transformationMethod
                    .equals(HideReturnsTransformationMethod.getInstance())) {
                binding.etNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgNewPassword.setImageResource(R.drawable.hide)
            }
            else {
                binding.etNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgNewPassword.setImageResource(R.drawable.view)
            }
        }

        binding.imgConfirmNewPassword.setOnClickListener {
            if (binding.etConfirmNewPassword.transformationMethod
                    .equals(HideReturnsTransformationMethod.getInstance())) {
                binding.etConfirmNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.imgConfirmNewPassword.setImageResource(R.drawable.hide)
            }
            else {
                binding.etConfirmNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.imgConfirmNewPassword.setImageResource(R.drawable.view)
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

    private fun isValidField(editText: EditText) : Boolean {
        if (editText.text.isEmpty()) {
            val message = getString(R.string.please_fill_all_the_field)
            editText.error = message
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onUpdateUserSuccess() {
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.change_password_success)
        showDialog(title, message)
        binding.progressBar.visibility = View.GONE
        Timer().schedule(3000) {
            finish()
        }
    }

    override fun onUpdateUserFailure() {
        binding.progressBar.visibility = View.GONE
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

    override fun onReadUserSuccess(user: User) {
        this.user = user
        binding.progressBar.visibility = View.GONE
        initNavHeaderView()
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
        TODO("Not yet implemented")
    }

}