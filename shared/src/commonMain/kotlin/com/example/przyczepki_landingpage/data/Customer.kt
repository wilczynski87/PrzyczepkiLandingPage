package com.example.przyczepki_landingpage.data

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: String? = null,
    val private: Private? = null,
    val company: Company? = null
) {
    fun getName(): String = company?.name ?: "${private?.firstName} ${private?.lastName}"
    fun getEmail(): String? = company?.email ?: private?.email

}


@Serializable
data class Private(
    val firstName: String? = null,
    val lastName: String? = null,
    val address: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val pesel: String? = null,
)

@Serializable
data class Company(
    val name: String? = null,
    val address: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val nip: String? = null,
)

