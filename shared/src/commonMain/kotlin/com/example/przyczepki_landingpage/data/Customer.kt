package com.example.przyczepki_landingpage.data

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: String? = null,
    val private: Private? = null,
    val company: Company? = null,
    val confirmed: String? = null,
) {
    fun getName(): String {
        val company = company?.name?.trim().orEmpty()
        if (company.isNotBlank()) return company
        val person = listOfNotNull(private?.firstName, private?.lastName).joinToString(" ").trim()
        if (person.isNotBlank()) return person
        return getEmail() ?: id.orEmpty()
    }
    fun getEmail(): String? = company?.email ?: private?.email
    fun getAddress(): String? = company?.address ?: private?.address
    fun isAdmin(adminEmails: Collection<String>): Boolean {
        val emails = listOfNotNull(private?.email, company?.email)
            .map { it.trim().lowercase() }
        if (emails.any { it in adminEmails }) return true
        return private?.firstName.equals("admin", ignoreCase = true)
    }

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

