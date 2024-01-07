package com.example.notnotes.noteservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notnotes.R
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
import com.google.firebase.auth.FirebaseUser

class AddLabelActivity :
    AppCompatActivity(),
    LabelClickListener,
    FirebaseReadUserListener,
    FirebaseReadLabelListener,
    FirebaseLabelListener {
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
}