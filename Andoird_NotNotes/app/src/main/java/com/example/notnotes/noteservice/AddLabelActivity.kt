package com.example.notnotes.noteservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityAddLabelBinding
import com.example.notnotes.model.Label

class AddLabelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddLabelBinding
    private lateinit var database: FirebaseService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLabelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        database = FirebaseService(this)

        binding.addLabel.setOnClickListener {
            if (binding.labelName.text == null) {
                Toast.makeText(this, "Please fill in", Toast.LENGTH_SHORT).show()
            }
            addLabelToDatabase(binding.labelName.text.toString())
        }
    }

    private fun addLabelToDatabase(labelName: String) {
        database.addLabel(Label(labelName))
    }
}