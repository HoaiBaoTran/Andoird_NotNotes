package com.example.notnotes.noteservice

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.notnotes.listener.TimerPickerListener
import java.util.Calendar

class TimePickerFragment() : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private lateinit var timePickerListener: TimerPickerListener

    private var hourPrev: Int? = null
    private var minutePrev: Int? = null

    constructor(currentHour: Int?, currentMinute: Int?) : this() {
        this.hourPrev = currentHour
        this.minutePrev = currentMinute
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val c = Calendar.getInstance()
        val hour = if (hourPrev != null) hourPrev else c.get(Calendar.HOUR_OF_DAY)
        val minute = if (minutePrev != null) minutePrev else c.get(Calendar.MINUTE)

        return TimePickerDialog(activity, this, hour!!, minute!!,
                                            DateFormat.is24HourFormat(activity))
    }

    fun display(fragment: FragmentManager, tag: String, listener: TimerPickerListener) {
        super.show(fragment, tag)
        timePickerListener = listener
    }


    override fun onTimeSet(view: TimePicker?, hour: Int, minute: Int) {
        timePickerListener.onTimeSelected(hour, minute)
    }
}