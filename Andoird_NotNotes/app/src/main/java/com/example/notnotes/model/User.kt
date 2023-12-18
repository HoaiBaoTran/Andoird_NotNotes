package com.example.notnotes.model

class User constructor() {
    var fullName: String = ""
    var userName: String = ""
    var password: String = ""
    var email: String? = null
    var phoneNumber: String? = null
    var address: String? = null
    var job: String? = null
    var homepage: String? = null

    constructor(fullName: String, userName: String, password: String, email: String?,
                phoneNumber: String?, address: String?, job: String?, homepage: String?) : this()
    {
        this.fullName = fullName
        this.userName = userName
        this.password = password
        this.email = email
        this.phoneNumber = phoneNumber
        this.address = address
        this.job = job
        this.homepage = homepage
    }


    constructor (fullName: String, userName: String, password: String) : this() {
        this.fullName = fullName
        this.userName = userName
        this.password = password
    }
    constructor(userName: String, password: String) : this() {
        this.password = password
        this.userName = userName
    }

    override fun toString(): String {
        return "user[${userName}]"
    }
}