package com.example.notnotes.model

data class User(var name: String) {
    var id: Int = -1;
    var email: String = ""
    var phoneNumber: String = ""
    var address: String = ""
    var job: String = ""
    var homepage: String = ""

    constructor(id: Int, name: String, email: String, phoneNumber: String,
                address: String, job: String, homepage: String) : this(name)
    {
        this.id = id
        this.email = email
        this.phoneNumber = phoneNumber
        this.address = address
        this.job = job
        this.homepage = homepage
    }
}
