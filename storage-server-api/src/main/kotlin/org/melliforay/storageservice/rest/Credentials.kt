package org.melliforay.storageservice.rest

open class Credentials() {

    constructor(uname: String, pw: String): this() {
        this.username = uname
        this.password = pw
    }

    open var username: String? = null

    open var password: String? = null

}