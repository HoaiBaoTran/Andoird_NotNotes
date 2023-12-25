package com.example.notnotes.model

class User constructor() {
    var fullName: String = ""
    var email: String = ""
    var password: String = ""
    var phoneNumber: String? = null
    var address: String? = null
    var job: String? = null
    var homepage: String? = null

    constructor(fullName: String, email: String, password: String,
                phoneNumber: String?, address: String?, job: String?, homepage: String?) : this()
    {
        this.fullName = fullName
        this.email = email
        this.password = password
        this.phoneNumber = phoneNumber
        this.address = address
        this.job = job
        this.homepage = homepage
    }


    constructor (fullName: String, email: String, password: String) : this() {
        this.fullName = fullName
        this.email = email
        this.password = password
    }

    constructor(email: String, password: String) : this() {
        this.password = password
        this.email = email
    }

    override fun toString(): String {
        return "user[${email}]"
    }
}