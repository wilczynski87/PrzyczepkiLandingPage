package com.example.przyczepki_landingpage.service.auth


import com.example.przyczepki_landingpage.service.UserService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthController: KoinComponent {
    private val userService by inject<UserService>()

    fun authenticate(username: String, password: String): Boolean {
        return userService.validateUser(username, password)
    }
}