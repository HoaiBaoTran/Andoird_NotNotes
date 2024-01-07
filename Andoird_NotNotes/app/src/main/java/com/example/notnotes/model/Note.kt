package com.example.notnotes.model

import android.os.Parcel
import android.os.Parcelable

class Note() : Parcelable {
    var id: String = ""
    var title: String = ""
    var content: String? = ""
    var progress: String? = ""
    var label: String? = ""
    var deleted: Boolean = false
    var deadlineDate: String? = ""
    var deadlineTime: String? = ""
    val attachments: List<Attachment> = emptyList()

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        title = parcel.readString().toString()
        content = parcel.readString()
        progress = parcel.readString()
        label = parcel.readString()
        deleted = parcel.readByte() != 0.toByte()
        deadlineDate = parcel.readString()
        deadlineTime = parcel.readString()
    }


    override fun toString(): String {
        return "Note[$id - $title - $progress - $label - $deleted - $deadlineDate - $deadlineTime"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(progress)
        parcel.writeString(label)
        parcel.writeByte(if (deleted) 1 else 0)
        parcel.writeString(deadlineDate)
        parcel.writeString(deadlineTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }


}