package com.example.notnotes.noteservice

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.notnotes.listener.DatePickerListener
import java.util.Calendar

class DatePickerFragment(): DialogFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var datePickerListener: DatePickerListener

    private var dayPrev: Int? = null
    private var monthPrev: Int? = null
    private var yearPrev: Int? = null

    constructor(currentDay: Int?, currentMonth: Int?, currentYear: Int?) : this() {
        this.dayPrev = currentDay
        this.monthPrev = currentMonth
        this.yearPrev = currentYear
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        val c = Calendar.getInstance()
        val day = if(dayPrev != null) dayPrev else c.get(Calendar.DAY_OF_MONTH)
        val month = if(monthPrev != null) monthPrev else  c.get(Calendar.MONTH)
        val year = if(yearPrev != null) yearPrev else c.get(Calendar.YEAR)

        return DatePickerDialog(requireContext(), this, year!!, month!!, day!!)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        datePickerListener.onDateSelected(day, month, year)
    }

    fun display(manager: FragmentManager, tag: String, listener: DatePickerListener) {
        super.show(manager, tag)
        datePickerListener = listener
    }


}