package com.example.notnotes.userservice

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityUploadImageProfileBinding
import com.example.notnotes.listener.FirebaseUploadImageListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Timer
import java.util.UUID
import kotlin.concurrent.schedule

class UploadImageProfileActivity :
    AppCompatActivity(),
    FirebaseUploadImageListener {

    private lateinit var binding: ActivityUploadImageProfileBinding
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var imgPhoto: Bitmap
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Intent>

    private lateinit var database: FirebaseService

    private lateinit var uriImage: Uri
    private val REQUEST_CODE_CAMERA = 1000
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

        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result ->
            if (result.resultCode == RESULT_OK) {
                imgPhoto = result.data?.extras?.get("data") as Bitmap
                uriImage = bitmapToUri(this, imgPhoto)
                Log.d("UploadImageProfile", uriImage.toString())
                binding.imgProfile.setImageURI(uriImage)
            }
        }

        binding.btnChoosePic.setOnClickListener {
            onFileChooser()
        }

        binding.btnCapturePic.setOnClickListener {
            onClickRequestPermission()
        }

        binding.btnUploadPic.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            database.changeProfilePic(uriImage)
        }

    }

    private fun bitmapToUri(context: Context, bitmap: Bitmap): Uri {
        val tempFile = saveBitmapToFile(context, bitmap)

        return Uri.fromFile(tempFile)
    }

    private fun saveBitmapToFile(context: Context, bitmap: Bitmap): File? {
        val cw = ContextWrapper(context)
        val directory = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileName = "image_${UUID.randomUUID()}.jpg"
        val file = File(directory, fileName)
        var outputStream: OutputStream? = null

        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return file
    }

    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoLauncher.launch(intent)
    }

    private fun onClickRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> takePhoto()
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                showExplainCameraRequest()
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_CODE_CAMERA
                )
            }
        }
    }

    private fun showExplainCameraRequest() {
        AlertDialog.Builder(this)
            .setTitle(R.string.camera_permission_title)
            .setMessage(R.string.camera_permission_message)
            .setPositiveButton(R.string.ok) { dialog, which ->
                dialog.dismiss()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_CODE_CAMERA
                )
            }
            .setNegativeButton(R.string.denied) { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            }
            else {
                Toast.makeText(this, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show()
            }
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