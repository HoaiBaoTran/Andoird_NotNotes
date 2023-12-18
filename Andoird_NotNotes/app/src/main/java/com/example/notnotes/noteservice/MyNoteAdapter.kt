package com.example.notnotes.noteservice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notnotes.R
import com.example.notnotes.model.Note

class MyNoteAdapter(private val noteList: ArrayList<Note>) : RecyclerView.Adapter<MyNoteAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val tvContent = itemView.findViewById<TextView>(R.id.tvContent)
        val btnDelete = itemView.findViewById<ImageButton>(R.id.btnDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_one_item_row, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = noteList[position]
        val smallDescription = note.description.slice(listOf(0, 200))

        holder.tvTitle.text = note.title
        holder.tvContent.text = smallDescription
    }
}