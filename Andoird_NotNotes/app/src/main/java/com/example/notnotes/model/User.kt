package com.example.notnotes.model

data class User(var email: String) {
    var id: Int = -1;
    var name: String = ""
    var password: String = ""
    var phoneNumber: String? = null
    var address: String? = null
    var job: String? = null
    var homepage: String? = null

    constructor(id: Int, name: String, email: String, password: String,
                phoneNumber: String?, address: String?, job: String?, homepage: String?) : this(email)
    {
        this.id = id
        this.name = name
        this.password = password
        this.phoneNumber = phoneNumber
        this.address = address
        this.job = job
        this.homepage = homepage
    }

    constructor (name: String, email: String, password: String) : this(email) {
        this.name = name
        this.password = password
    }
    constructor(email: String, password: String) : this(email) {
        this.password = password
    }
}
