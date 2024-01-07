package com.example.notnotes.listener

import com.example.notnotes.model.Label
import com.example.notnotes.model.Note

interface FirebaseReadLabelListener {
    fun onReadLabelListComplete(labels: List<Label>)
    fun onReadLabelListFailure()
    fun onDeleteLabelSuccess()
    fun onDeleteLabelFailure()
}