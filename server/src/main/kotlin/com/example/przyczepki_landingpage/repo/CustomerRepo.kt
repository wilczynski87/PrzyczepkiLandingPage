package com.example.przyczepki_landingpage.repo

import com.example.przyczepki_landingpage.data.Customer

interface CustomerRepo {
    suspend fun save(customer: Customer): Customer?
    suspend fun get(id: String): Customer?
    suspend fun getByEmail(email: String): Customer?
    suspend fun update(customer: Customer): Customer?
    suspend fun delete(id: String): Boolean
}