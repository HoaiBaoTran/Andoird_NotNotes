package com.example.notnotes.userservice

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityUploadImageProfileBinding
import com.example.notnotes.listener.FirebaseUploadImageListener
import java.util.Timer
import kotlin.concurrent.schedule

class UploadImageProfileActivity :
    AppCompatActivity(),
    FirebaseUploadImageListener {

    private lateinit var binding: ActivityUploadImageProfileBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var database: FirebaseService

    private lateinit var uriImage: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadImageProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseService(this, this)

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val responseIntent = result.data
                uriImage = responseIntent?.data!!
                binding.imgProfile.setImageURI(uriImage)
            }
        }

        binding.btnChoosePic.setOnClickListener {
            onFileChooser()
        }

        binding.btnUploadPic.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            database.changeProfilePic(uriImage)
        }

    }


    private fun onFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        resultLauncher.launch(intent)

    }

    override fun onUploadImageSuccess() {
        binding.progressBar.visibility = View.GONE
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.upload_picture_success)
        showDialog(title, message)

        Timer().schedule(3000) {
            val intent = Intent(this@UploadImageProfileActivity, ProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onUploadImageFailure() {
        binding.progressBar.visibility = View.GONE
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
}