package com.example.notnotes.noteservice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notnotes.R
import com.example.notnotes.listener.ItemClickListener
import com.example.notnotes.listener.LabelClickListener
import com.example.notnotes.model.Label

class MyLabelAdapter(
    private val context: Context,
    private val labelList: ArrayList<Label>,
    private val onLabelClickListener: LabelClickListener
) : RecyclerView.Adapter<MyLabelAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val labelName: TextView = itemView.findViewById(R.id.labelName)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteLabel)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            TODO("Not yet implemented")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_one_item_row_label, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return labelList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val label = labelList[position]
        holder.labelName.text = label.name
        holder.deleteBtn.setOnClickListener {
            onLabelClickListener.onDeleteItemClick(label)
        }
    }
}