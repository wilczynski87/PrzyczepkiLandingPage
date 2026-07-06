package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.repo.impl.CustomerTable
import pl.przyczepki.email.api.dto.AccountConfirmationData

interface CustomerService {
    suspend fun save(customer: Customer): Customer?
    suspend fun accountConfirmationData(customer: Customer): AccountConfirmationData
    suspend fun confirm(id: String): Customer?
    suspend fun get(id: String): Customer?
    suspend fun getCustomerByEmail(email: String): Customer?
    suspend fun update(customer: Customer): Customer?
    suspend fun updatePassword(loginRequest: LoginRequest): Boolean
    suspend fun getCustomerTableByEmail(email: String): CustomerTable?
    suspend fun delete(id: String): Boolean
}