package com.example.przyczepki_landingpage.service

interface UserService {
    fun validateUser(username: String, password: String): Boolean
}
