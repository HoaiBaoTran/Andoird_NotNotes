package com.example.notnotes.noteservice

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.notnotes.MainActivity
import com.example.notnotes.R
import com.example.notnotes.database.FirebaseService
import com.example.notnotes.databinding.FragmentNoteDetailBinding
import com.example.notnotes.listener.DatePickerListener
import com.example.notnotes.listener.TimerPickerListener
import com.example.notnotes.listener.FirebaseNoteListener
import com.example.notnotes.listener.FirebaseReadLabelListener
import com.example.notnotes.listener.FragmentListener
import com.example.notnotes.model.Attachment
import com.example.notnotes.model.Label
import com.example.notnotes.model.Note
import java.util.Timer
import kotlin.concurrent.schedule

class NoteDetailFragment :
    Fragment(),
    FirebaseNoteListener,
    DatePickerListener,
    TimerPickerListener,
    FirebaseReadLabelListener {

    private lateinit var binding: FragmentNoteDetailBinding
    private lateinit var database: FirebaseService
    private var fragmentListener: FragmentListener? = null
    private var isEdit = false
    private var editNote = Note()

    private var currentDay: Int? = null
    private var currentMonth: Int? = null
    private var currentYear: Int? = null

    private var currentHour: Int? = null
    private var currentMinute: Int? = null

    private var labelName: String = "label"
    private var labelList: ArrayList<String> = ArrayList()
    private var fileNameListText: ArrayList<String> = ArrayList()
    private var attachmentList: ArrayList<Attachment> = ArrayList()
    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data
            if (uri != null) {
                Log.d("FragmentDebug", uri.toString())
                val (fileName, fileType) = getFileNameAndType(requireContext(), uri)
                if (fileName != null && fileType != null) {
                    Log.d("FragmentDebug", fileName)
                    fileNameListText.add(fileName)
                    val attachment = Attachment(uri.toString(), fileName, fileType)
                    attachmentList.add(attachment)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            fileNameListText = it.getStringArrayList("fileNameList")!!
            attachmentList.clear()
            attachmentList.addAll(it.getParcelableArrayList("attachmentList")!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        database = FirebaseService(requireContext(), this, this)
        return binding.root
    }


    fun setFragmentListener(listener: FragmentListener) {
        fragmentListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (fileNameListText.isNotEmpty()) {
            binding.tvFileLabel.visibility = View.VISIBLE
            binding.tvFileLabel.text = fileNameListText.joinToString { it -> it }
        }
        database.getLabels()

        val note = arguments?.getParcelable<Note>(MainActivity.NOTE_KEY)
        if (note != null) {
            loadNoteInformation(note)
            editNote = note
            isEdit = true
        }

        binding.btnBackNote.setOnClickListener {
            showConfirmBackDialog()
        }

        binding.etDateDeadline.setOnClickListener {
            datePicker()
        }

        binding.etTimeDeadline.setOnClickListener {
            timePicker()
        }

        binding.etProgress.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var percent = s.toString().toDoubleOrNull()
                if (percent != null) {
                    if (percent > 100) {
                        percent = 100.0
                    }
                    else if (percent < 0) {
                        percent = 0.0
                    }
                }
                else {
                    percent = 0.0
                }
                binding.progressBar.progress = percent.toInt() ?: 0
                binding.tvProgress.text = getString(R.string.template_percent, percent)
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })


        binding.btnSaveNote.setOnClickListener {
            if (checkValidField()) {
                if (isEdit) {
                    editNoteInDatabase()
                }
                else {
                    addNoteToDatabase()
                }
            }
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle the selected item
                val selectedItem = parent?.getItemAtPosition(position).toString()
                labelName = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.imgBtnAttachFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            filePickerLauncher.launch(intent)
        }

        (activity as MainActivity).hideComponents()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("fileNameList", fileNameListText)
        outState.putParcelableArrayList("attachmentList", attachmentList)
    }


    override fun onDetach() {
        super.onDetach()
        (activity as MainActivity).showComponents()
        fragmentListener = null
    }

    @SuppressLint("Range")
    private fun getFileNameAndType(context: Context, uri: Uri): Pair<String?, String?> {
        var fileName: String? = null
        var fileType: String? = null

        val contentResolver: ContentResolver = context.contentResolver

        // Get file name
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getString(it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                fileName = displayName
            }
        }

        // Get file type
        val mimeType: String? = contentResolver.getType(uri)
        mimeType?.let {
            fileType = MimeTypeMap.getSingleton().getExtensionFromMimeType(it)
        }

        return Pair(fileName, fileType)
    }

    private fun loadNoteInformation(note: Note) {
        binding.tietTitle.setText(note.title)
        binding.tietContent.setText(note.content)
        binding.etProgress.setText(note.progress)
        binding.progressBar.progress = note.progress?.toInt() ?: 0
        binding.tvProgress.text = getString(R.string.template_percent, note.progress?.toDouble())
        binding.etDateDeadline.setText(note.deadlineDate)
        binding.etTimeDeadline.setText(note.deadlineTime)

        for (attach in note.attachments) {
            if (!fileNameListText.contains(attach.fileName)) {
                fileNameListText.add(attach.fileName)
                attachmentList.add(attach)
            }
        }

        Log.d("FragmentDebug", "loadNoteInformation - note: ${note.attachments}")
        Log.d("FragmentDebug", "loadNoteInformation - attachment: ${attachmentList}")

        Log.d("FragmentDebug", "loadNoteInformation: $fileNameListText")
        if (fileNameListText.isNotEmpty()) {
            binding.tvFileLabel.visibility = View.VISIBLE
            binding.tvFileLabel.text = fileNameListText.joinToString { it -> it }
        }

    }

    private fun datePicker() {
        val datePickerFragment = DatePickerFragment(currentDay, currentMonth, currentYear)
        datePickerFragment.display(requireActivity().supportFragmentManager, "datePicker", this)
    }

    private fun timePicker() {
        val timePickerFragment = TimePickerFragment(currentHour, currentMinute)
        timePickerFragment.display(requireActivity().supportFragmentManager, "timePicker", this)
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
//        note.label = binding.etLabel.text.toString()

        note.label =
            binding.spinner.selectedItem.toString().ifEmpty { "label" }

        note.attachments.clear()
        note.attachments.addAll(attachmentList)

        note.deadlineDate = binding.etDateDeadline.text.toString()
        note.deadlineTime = binding.etTimeDeadline.text.toString()

        return note
    }

    private fun addNoteToDatabase() {
        val note = getNoteFromField()
        database.addNote(note)
    }

    private fun editNoteInDatabase() {
        val note = getNoteFromField()
        editNote.progress = note.progress
        editNote.title = note.title
        editNote.content = note.content
        editNote.label = note.label
        editNote.deadlineDate = note.deadlineDate
        editNote.deadlineTime = note.deadlineTime

        editNote.attachments.clear()
        editNote.attachments.addAll(note.attachments)

        database.editNote(editNote)
    }

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

    private fun showConfirmBackDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val title = getString(R.string.exit_title)
        val message = getString(R.string.exit_message)
        val exitBtnText = getString(R.string.exit)
        val cancelBtnText = getString(R.string.cancel)
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(exitBtnText) { dialog, _ ->
                dialog.dismiss()
                closeFragment()
            }
            .setNegativeButton(cancelBtnText) { dialog, _ ->
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

    override fun onUpdateNoteSuccess() {
        val title = getString(R.string.Annoucement)
        val message = getString(R.string.update_note_success)
        showDialog(title, message)
        Timer().schedule(3000) {
            closeFragment()
        }
    }

    override fun onUpdateNoteFailure() {

    }

    override fun onDateSelected(day: Int, month: Int, year: Int) {
        // month value 0 -> 11
        val dayFormatted = if(day < 10) "0$day" else day.toString()
        val monthFormatted = if(month < 9) "0${month + 1}" else (month + 1).toString()
        val date = "$dayFormatted/$monthFormatted/$year"
        currentDay = day
        currentMonth = month
        currentYear = year
        binding.etDateDeadline.setText(date)
    }

    override fun onTimeSelected(hour: Int, minute: Int) {
        val hourFormatted = if (hour < 10) "0$hour" else hour.toString()
        val minuteFormatted = if (minute < 10) "0$minute" else minute.toString()
        val time = "$hourFormatted:$minuteFormatted"
        currentHour = hour
        currentMinute = minute
        binding.etTimeDeadline.setText(time)
    }

    override fun onReadLabelListComplete(labels: List<Label>) {
        labelList = labels.map { it.name } as ArrayList<String>
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, labelList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        if (isEdit) {
            val index = labelList.indexOf(editNote.label)
            binding.spinner.setSelection(index)
        }
    }

    override fun onReadLabelListFailure() {

    }

    override fun onDeleteLabelSuccess() {

    }

    override fun onDeleteLabelFailure() {

    }

}