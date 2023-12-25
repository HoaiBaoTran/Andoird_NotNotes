package com.example.notnotes.listener

import com.example.notnotes.model.Note

interface ItemClickListener {
    fun onItemClick(note: Note)
    fun onDeleteItemClick(note: Note)
}