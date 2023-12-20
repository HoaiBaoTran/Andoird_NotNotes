package com.example.notnotes.noteservice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.notnotes.MainActivity
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.FragmentNoteDetailBinding
import com.example.notnotes.listener.FirebaseListener
import com.example.notnotes.listener.FirebaseNoteListener
import com.example.notnotes.listener.FragmentListener
import com.example.notnotes.model.Note
import com.example.notnotes.model.User
import java.util.Timer
import kotlin.concurrent.schedule

class NoteDetailFragment :
    Fragment(),
    FirebaseNoteListener {

    private lateinit var binding: FragmentNoteDetailBinding
    private lateinit var database: FirebaseService
    private lateinit var user: User
    private var fragmentListener: FragmentListener? = null
    private var isEdit = false
    private var editNote = Note()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        database = FirebaseService(requireContext(), this)
        return binding.root
    }



    override fun onDetach() {
        super.onDetach()
        fragmentListener = null
    }

    fun setFragmentListener(listener: FragmentListener) {
        fragmentListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val note = arguments?.getParcelable<Note>(MainActivity.NOTE_KEY)
        if (note != null) {
            loadNoteInformation(note)
            editNote = note
            isEdit = true
        }


        binding.btnSaveNote.setOnClickListener {
            if (checkValidField()) {
                if (isEdit) {
//                    editNoteInDatabase()
                }
                else {
                    addNoteToDatabase()
                }
            }
        }
    }

    private fun loadNoteInformation(note: Note) {
        binding.tietTitle.setText(note.title)
        binding.tietContent.setText(note.content)
        binding.etLabel.setText(note.label)
        binding.etProgress.setText(note.progress)
    }


    private fun checkValidField(): Boolean {
        val title = binding.tietTitle.text.toString()
        if (title.isEmpty()) {
            val titleError = getString(R.string.empty_field_error)
            val message = getString(R.string.title_not_empty)
            showDialog(titleError, message)
            return false
        }
        return true
    }

    private fun getNoteFromField() : Note {
        val note = Note()
        note.title = binding.tietTitle.text.toString()
        note.content = binding.tietContent.text.toString()
        note.progress = binding.etProgress.text.toString()
        note.label = binding.etLabel.text.toString()

        return note
    }

    private fun addNoteToDatabase() {
        val note = getNoteFromField()
        database.addNote(note)
    }

//    private fun editNoteInDatabase() {
//        val note = getNoteFromField()
//        editNote.progress = note.progress
//        editNote.title = note.title
//        editNote.content = note.content
//        editNote.label = note.label
//        database.editNote(editNote, user.userName)
//        closeFragment()
//    }

    private fun showDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun closeFragment() {
        requireActivity().supportFragmentManager.popBackStack(
            "NoteDetailFragment",
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        fragmentListener?.onFragmentClosed()
    }

    override fun onAddNoteSuccess() {
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.add_note_success)
        showDialog(title, message)
        Timer().schedule(3000) {
            closeFragment()
        }
    }

    override fun onAddNoteFailure() {

    }
}