package com.example.przyczepki_landingpage.model

enum class ServerStatus {

    OK,
    UNAUTHORIZED,
    FORBIDDEN,
    SERVER_ERROR,
    NOT_FOUND,
    TIMEOUT,
    UNREACHABLE,
    UNEXPECTED_STATUS
}

data class ServerResponse(
    val status: ServerStatus,
    val message: String
)