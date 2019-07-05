package com.hit.software.cloudserver.examserver

class login {
    var username:String? = null
    var userpassword:String? = null

    override fun toString(): String {
        return "${this.username},${this.userpassword}"
    }
}