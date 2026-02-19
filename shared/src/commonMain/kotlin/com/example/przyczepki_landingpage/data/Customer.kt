package com.example.przyczepki_landingpage.data

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: Long,
    val private: Private,
    val company: Company?,
) {
}


@Serializable
data class Private(
    val firstName: String,
    val lastName: String,
    val address: String,
    val email: String?,
    val phoneNumber: String,
    val pesel: String?,
)

@Serializable
data class Company(
    val name: String,
    val address: String,
    val email: String?,
    val phoneNumber: String,
    val nip: String,
)

