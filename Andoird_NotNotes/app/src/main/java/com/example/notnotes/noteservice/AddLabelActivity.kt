package com.example.notnotes.noteservice

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
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notnotes.MainActivity
import com.example.notnotes.R
import com.example.notnotes.appservice.SettingActivity
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityAddLabelBinding
import com.example.notnotes.listener.FirebaseLabelListener
import com.example.notnotes.listener.FirebaseReadLabelListener
import com.example.notnotes.listener.FirebaseReadNoteListener
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.LabelClickListener
import com.example.notnotes.model.Label
import com.example.notnotes.model.Note
import com.example.notnotes.model.User
import com.example.notnotes.userservice.ChangePasswordActivity
import com.example.notnotes.userservice.LoginActivity
import com.example.notnotes.userservice.ProfileActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class AddLabelActivity :
    AppCompatActivity(),
    LabelClickListener,
    FirebaseReadUserListener,
    FirebaseReadLabelListener,
    FirebaseLabelListener,
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityAddLabelBinding
    private lateinit var database: FirebaseService
    private lateinit var labelAdapter: MyLabelAdapter
    private lateinit var labelList: ArrayList<Label>
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLabelBinding.inflate(layoutInflater)
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

        binding.progressBar.visibility = View.VISIBLE
        database = FirebaseService(this, this, this, this)
        getUserFromDatabase()

        binding.addLabel.setOnClickListener {
            if (binding.labelName.text.toString().isEmpty()) {
                Toast.makeText(this, "Please fill in", Toast.LENGTH_SHORT).show()
            }
            else {
                addLabelToDatabase(binding.labelName.text.toString())
                Toast.makeText(this, "Add label successfully!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getUserFromDatabase() {
        val firebaseUser = database.auth.currentUser
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

    private fun addLabelToDatabase(labelName: String) {
        database.addLabel(Label(labelName))
    }

    private fun initRecyclerView() {
        labelList = ArrayList()
        labelAdapter = MyLabelAdapter(this, labelList, this)
        binding.recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(
                this.context,
                LinearLayoutManager.VERTICAL,
                false
            )

            addItemDecoration(
                DividerItemDecoration(
                    this.context,
                    RecyclerView.VERTICAL
                )
            )
            adapter = labelAdapter
        }
        database.getLabels()
    }

    override fun onItemClick(label: Label) {

    }

    override fun onDeleteItemClick(label: Label) {
        val title = getString(R.string.delete_label)
        val message = getString(R.string.are_you_sure_want_to_delete_label)
        showDialogDeleteConfirm(title, message, label)
    }

    private fun showDialogDeleteConfirm(title: String, message: String, label: Label) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                database.deleteLabel(label)
                database.getLabels()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onReadUserSuccess(user: User) {
        this.user = user
        binding.progressBar.visibility = View.GONE
        initRecyclerView()
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

    }

    override fun onReadLabelListComplete(labels: List<Label>) {
        labelList.clear()
        labelList.addAll(labels)
        labelAdapter.notifyDataSetChanged()
    }

    override fun onReadLabelListFailure() {

    }

    override fun onDeleteLabelSuccess() {
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.delete_label_success)
        showDialog(title, message)
    }

    private fun showDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onDeleteLabelFailure() {
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.delete_label_fail)
        showDialog(title, message)
    }

    override fun onAddLabelSuccess() {
        database.getLabels()
    }

    override fun onAddLabelFailure() {

    }

    override fun onUpdateLabelSuccess() {

    }

    override fun onUpdateLabelFailure() {

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