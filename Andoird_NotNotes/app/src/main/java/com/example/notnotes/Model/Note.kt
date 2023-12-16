package com.example.notnotes.Model

data class Note constructor(
    var name: String) {
    var id: Int = -1
    var description: String = ""
    var progress: Double = -1.0
    var status: Int = 0

    constructor(id: Int, name: String, description: String,
                progress: Double, status: Int) : this(name) {
        this.id = id
        this.description = description
        this.progress = progress
        this.status = status
    }


}