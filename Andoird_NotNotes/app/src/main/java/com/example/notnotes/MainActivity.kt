package com.example.notnotes

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
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
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notnotes.appservice.SettingActivity
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.userservice.ChangePasswordActivity
import com.example.notnotes.userservice.LoginActivity
import com.example.notnotes.userservice.ProfileActivity
import com.example.notnotes.databinding.ActivityMainBinding
import com.example.notnotes.listener.FirebaseReadNoteListener
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.FragmentListener
import com.example.notnotes.listener.ItemClickListener
import com.example.notnotes.model.Note
import com.example.notnotes.model.User
import com.example.notnotes.noteservice.MyNoteAdapter
import com.example.notnotes.noteservice.NoteDetailFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class MainActivity :
    SettingActivity(),
    FragmentListener,
    FirebaseReadUserListener,
    ItemClickListener,
    FirebaseReadNoteListener,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: MyNoteAdapter
    private lateinit var noteList: ArrayList<Note>
    private lateinit var database: FirebaseService
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        // supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.progressBar.visibility = View.VISIBLE

        database = FirebaseService(this, this)
        database.firebaseReadNoteListener = this
        getUserFromDatabase()

        binding.fab.setOnClickListener {
            openNoteDetailFragment(null)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            database.getNotes()
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

    private fun openNoteDetailFragment(bundle: Bundle?) {
        hideComponents()

        val fragment = NoteDetailFragment()
        fragment.arguments = bundle
        fragment.setFragmentListener(this)

        val fragmentManager: FragmentManager = supportFragmentManager

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragmentContainer, fragment)

        fragmentTransaction.addToBackStack("NoteDetailFragment")

        fragmentTransaction.commit()
    }

    private fun hideComponents () {
        binding.recyclerview.visibility = View.GONE
        binding.fab.visibility = View.GONE
        binding.fragmentContainer.visibility = View.VISIBLE
    }

    private fun showComponents () {
        binding.recyclerview.visibility = View.VISIBLE
        binding.fab.visibility = View.VISIBLE
        binding.fragmentContainer.visibility = View.GONE
    }

    private fun initViews() {
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

    private fun initRecyclerView() {
        noteList = ArrayList()
        noteAdapter = MyNoteAdapter(this, noteList, this)
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
            adapter = noteAdapter
        }
        database.getNotes()
    }


//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        super.onCreateOptionsMenu(menu)
//        menuInflater.inflate(R.menu.main_option_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_item_profile -> openProfileActivity()
//            R.id.menu_item_logout -> logoutAccount()
//            R.id.menu_item_change_password -> openChangePasswordActivity()
//            R.id.menu_item_settings -> openSettingActivity()
//        }
//
//        return super.onOptionsItemSelected(item)
//    }

    private fun openSettingActivity() {
        val settingIntent = Intent(this, SettingActivity::class.java)
        startActivity(settingIntent)
    }

    private fun logoutAccount() {
        deleteSharedPreferences("MyPreferences")
        openLoginActivity()
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

    // -- Fragment Listener --

    override fun onFragmentClosed() {
        runOnUiThread {
            showComponents()
        }
    }

    // -- Item Click Listener --
    override fun onItemClick(note: Note) {
        val bundle = Bundle()
        bundle.putParcelable(NOTE_KEY, note)
        openNoteDetailFragment(bundle)
    }

    override fun onDeleteItemClick(note: Note) {
        val title = getString(R.string.delete_note)
        val message = getString(R.string.are_you_sure_want_to_delete_note)
        showDialogConfirm(title, message, note)
    }

    private fun showDialogConfirm(title: String, message: String, note: Note) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                database.deleteNote(note)
                database.getNotes()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
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

    companion object {
        val NOTE_KEY = "note_key"
    }

    override fun onBackPressed() {
        showComponents()
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        super.onBackPressed()
    }

    // -- Read User Listener --

    override fun onReadUserSuccess(user: User) {
        this.user = user
        binding.progressBar.visibility = View.GONE
        initViews()
    }

    override fun onReadUserFailure() {
        binding.progressBar.visibility = View.GONE
    }

    // -- Read Note Listener --

    override fun onReadNoteListComplete(notes: List<Note>) {
        noteList.clear()
        noteList.addAll(notes)
        noteAdapter.notifyDataSetChanged()
    }

    override fun onReadNoteListFailure() {

    }

    override fun onDeleteNoteSuccess() {
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.delete_note_success)
        showDialog(title, message)
    }

    override fun onDeleteNoteFailure() {
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.delete_note_fail)
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

    override fun applyLanguage() {
        super.applyLanguage()
        this.recreate()
    }

    override fun onRestart() {
        recreate()
        getUserFromDatabase()
        database.getNotes()
        super.onRestart()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_notes -> {

            }
            R.id.nav_storage -> {

            }
            R.id.nav_label -> {

            }
            R.id.nav_recycle_bin -> {

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

    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
}