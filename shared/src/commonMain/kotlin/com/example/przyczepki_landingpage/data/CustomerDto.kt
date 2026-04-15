package com.example.przyczepki_landingpage.data

import kotlinx.serialization.Serializable

@Serializable
data class CustomerDto(
    val id: String? = null,
    val private: Private? = null,
    val company: Company? = null
) {
    fun getName(): String = company?.name ?: "${private?.firstName} ${private?.lastName}"
    fun getEmail(): String? = company?.email ?: private?.email

    fun toDomain(): Customer = Customer(
        id = id,
        private = private,
        company = company,
        passwordHash = null
    )
}