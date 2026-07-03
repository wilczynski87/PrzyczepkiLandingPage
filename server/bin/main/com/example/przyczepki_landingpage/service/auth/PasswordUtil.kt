package com.example.przyczepki_landingpage.service.auth

import org.mindrot.jbcrypt.BCrypt

object PasswordUtil {
    fun hash(password: String): String =
        BCrypt.hashpw(password, BCrypt.gensalt())

    fun verify(password: String, hash: String): Boolean =
        BCrypt.checkpw(password, hash)
}