package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.Customer

interface CustomerService {
    suspend fun save(customer: Customer): Customer?
    suspend fun get(id: String): Customer?
    suspend fun getCustomerByEmail(email: String): Customer?
    suspend fun update(customer: Customer): Customer?
    suspend fun delete(id: String): Boolean
}