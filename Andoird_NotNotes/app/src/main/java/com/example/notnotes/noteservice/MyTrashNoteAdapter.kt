package com.example.notnotes.noteservice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notnotes.R
import com.example.notnotes.listener.ItemClickListener
import com.example.notnotes.model.Note

class MyTrashNoteAdapter(
    private val context: Context,
    private val noteList: ArrayList<Note>,
    private val onItemClickListener: ItemClickListener
) : RecyclerView.Adapter<MyTrashNoteAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val tvLabel: TextView = itemView.findViewById(R.id.tvLabel)
        val tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteItem)
        val btnRestore: ImageButton = itemView.findViewById(R.id.btnRestoreItem)

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedItem = noteList[position]
                onItemClickListener.onItemClick(clickedItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_one_item_row_trash, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = noteList[position]

        if (note.deleted) {
            var smallContent: String? = null
            smallContent = if (note.content!!.length >= 200) {
                note.content?.slice(0..200) + "..."
            } else {
                note.content!!
            }

            holder.tvTitle.text = note.title
            holder.tvContent.text = smallContent
            val tvProgressLabel = context.applicationContext.getString(
                R.string.progress)
            val tvLabelLabel = context.applicationContext.getString(
                R.string.label)
            holder.tvProgress.text = "$tvProgressLabel ${note.progress}%"
            holder.tvLabel.text = "$tvLabelLabel ${note.label}"

            holder.btnDelete.setOnClickListener {
                onItemClickListener.onDeleteItemClick(note)
            }
            holder.btnRestore.setOnClickListener {
                onItemClickListener.onRestoreItemClick(note)
            }
        }
        else {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }
}