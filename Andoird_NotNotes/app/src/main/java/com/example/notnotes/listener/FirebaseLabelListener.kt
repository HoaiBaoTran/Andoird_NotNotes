package com.example.notnotes.listener

interface FirebaseLabelListener {
    fun onAddLabelSuccess()
    fun onAddLabelFailure()
    fun onUpdateLabelSuccess()
    fun onUpdateLabelFailure()
}