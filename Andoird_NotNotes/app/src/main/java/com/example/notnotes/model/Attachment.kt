package com.example.notnotes.model

import android.os.Parcel
import android.os.Parcelable

class Attachment () : Parcelable {
    var fileUrl: String = ""
    var fileName: String = ""
    var fileType: String = ""

    constructor(parcel: Parcel) : this() {
        fileUrl = parcel.readString().toString()
        fileName = parcel.readString().toString()
        fileType = parcel.readString().toString()
    }

    constructor(fileUrl: String, fileName: String, fileType: String) : this() {
        this.fileUrl = fileUrl
        this.fileName = fileName
        this.fileType = fileType
    }

    override fun toString(): String {
        return "$fileName - $fileUrl"
    }

    override fun equals(other: Any?): Boolean {
        super.equals(other)
        if (this === other) return true
        if (other !is Attachment) return false
        return this.fileName == other.fileName && this.fileUrl == other.fileUrl
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fileUrl)
        parcel.writeString(fileName)
        parcel.writeString(fileType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Attachment> {
        override fun createFromParcel(parcel: Parcel): Attachment {
            return Attachment(parcel)
        }

        override fun newArray(size: Int): Array<Attachment?> {
            return arrayOfNulls(size)
        }
    }
}