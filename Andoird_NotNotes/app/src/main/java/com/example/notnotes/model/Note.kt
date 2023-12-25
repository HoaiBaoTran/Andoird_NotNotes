package com.example.notnotes.model

import android.os.Parcel
import android.os.Parcelable

class Note() : Parcelable {
    var id: String = ""
    var title: String = ""
    var content: String? = ""
    var progress: String? = ""
    var label: String? = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        title = parcel.readString().toString()
        content = parcel.readString()
        progress = parcel.readString()
        label = parcel.readString()
    }

    override fun toString(): String {
        return "Note[$id - $title - $progress - $label]"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(progress)
        parcel.writeString(label)
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