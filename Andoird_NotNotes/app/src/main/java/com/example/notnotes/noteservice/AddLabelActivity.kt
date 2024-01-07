package com.example.notnotes.noteservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.ActivityAddLabelBinding
import com.example.notnotes.listener.FirebaseReadNoteListener
import com.example.notnotes.listener.FirebaseReadUserListener
import com.example.notnotes.listener.LabelClickListener
import com.example.notnotes.model.Label
import com.example.notnotes.model.User

class AddLabelActivity :
    AppCompatActivity(),
    LabelClickListener,
    FirebaseReadUserListener {
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

        database = FirebaseService(this)

        binding.addLabel.setOnClickListener {
            if (binding.labelName.text == null) {
                Toast.makeText(this, "Please fill in", Toast.LENGTH_SHORT).show()
            }
            addLabelToDatabase(binding.labelName.text.toString())
            Toast.makeText(this, "Add label successfully!", Toast.LENGTH_SHORT).show()
        }
//        initRecyclerView()
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
        TODO("Not yet implemented")
    }

    override fun onDeleteItemClick(label: Label) {
        TODO("Not yet implemented")
    }

    override fun onReadUserSuccess(user: User) {
        TODO("Not yet implemented")
    }

    override fun onReadUserFailure() {
        TODO("Not yet implemented")
    }
}