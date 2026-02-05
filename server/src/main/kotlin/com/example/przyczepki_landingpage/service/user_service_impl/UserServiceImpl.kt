package com.example.przyczepki_landingpage.service.user_service_impl

import com.example.przyczepki_landingpage.service.UserService

class UserServiceImpl: UserService {
    override fun validateUser(username: String, password: String): Boolean {
        return true
    }
}