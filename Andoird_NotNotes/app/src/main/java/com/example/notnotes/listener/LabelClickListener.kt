package com.example.notnotes.listener

import com.example.notnotes.model.Label

interface LabelClickListener {
    fun onItemClick(label: Label)
    fun onDeleteItemClick(label: Label)
}