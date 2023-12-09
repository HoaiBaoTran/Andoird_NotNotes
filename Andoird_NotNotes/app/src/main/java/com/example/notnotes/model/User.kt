package com.example.notnotes.model

data class User(var name: String) {
    var id: Int = -1;
    var email: String = ""
    var password: String = ""
    var phoneNumber: String? = null
    var address: String? = null
    var job: String? = null
    var homepage: String? = null

    constructor(id: Int, name: String, email: String, phoneNumber: String,
                address: String, password: String, job: String, homepage: String) : this(name)
    {
        this.id = id
        this.email = email
        this.password = password
        this.phoneNumber = phoneNumber
        this.address = address
        this.job = job
        this.homepage = homepage
    }

    constructor (name: String, email: String, password: String) : this(name) {
        this.email = email
        this.password = password
    }
}
