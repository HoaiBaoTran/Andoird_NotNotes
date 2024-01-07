package com.example.notnotes.model

class Attachment () {
    var fileName: String = ""
    var fileUrl: String = ""
    constructor(fileName: String, fileUrl: String) : this() {
        this.fileName = fileName
        this.fileUrl = fileUrl
    }
}