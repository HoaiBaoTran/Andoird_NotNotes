package com.example.notnotes.noteservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityTrashBinding
import com.example.notnotes.listener.FirebaseNoteListener
import com.example.notnotes.listener.FirebaseReadNoteListener
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.ItemClickListener
import com.example.notnotes.model.Note
import com.example.notnotes.model.User
import com.google.firebase.auth.FirebaseUser

class TrashActivity :
    AppCompatActivity(),
    ItemClickListener,
    FirebaseReadUserListener,
    FirebaseReadNoteListener,
    FirebaseNoteListener {

    private lateinit var binding: ActivityTrashBinding
    private lateinit var noteAdapter: MyTrashNoteAdapter
    private lateinit var noteList: ArrayList<Note>
    private lateinit var database: FirebaseService
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        database = FirebaseService(
            this,
            this,
            this,
            this)
        getUserFromDatabase()

        binding.progressBar.visibility = View.VISIBLE
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

    private fun initViews() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        noteList = ArrayList()
        noteAdapter = MyTrashNoteAdapter(this, noteList, this)
        binding.recyclerViewTrash.apply {
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

    override fun onItemClick(note: Note) {

    }

    override fun onDeleteItemClick(note: Note) {
        val title = getString(R.string.delete_note)
        val message = getString(R.string.are_you_sure_want_to_delete_note)
        showDialogDeleteConfirm(title, message, note)
    }

    private fun showDialogDeleteConfirm(title: String, message: String, note: Note) {
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

    override fun onRestoreItemClick(note: Note) {
        val title = getString(R.string.restore_note)
        val message = getString(R.string.are_you_sure_want_to_restore_note)
        showDialogRestoreConfirm(title, message, note)
    }

    private fun showDialogRestoreConfirm(title: String, message: String, note: Note) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.restore)) { dialog, _ ->
                note.deleted = false
                database.editNote(note)
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
        initViews()
    }

    override fun onReadUserFailure() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onReadNoteListComplete(notes: List<Note>) {
        Log.d("TrashActivity", "onReadNoteListComplete: $notes")
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

    override fun onAddNoteSuccess() {

    }

    override fun onAddNoteFailure() {

    }

    override fun onUpdateNoteSuccess() {
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.move_note_to_trash_success)
        showDialog(title, message)
        database.getNotes()
    }

    override fun onUpdateNoteFailure() {

    }
}