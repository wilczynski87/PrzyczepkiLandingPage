package com.example.przyczepki_landingpage.support

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.repo.impl.CustomerTable
import com.example.przyczepki_landingpage.service.CustomerService

class FakeCustomerService : CustomerService {
    override suspend fun save(customer: Customer): Customer? = customer
    override suspend fun confirm(id: String): Customer? = null
    override suspend fun get(id: String): Customer? = null
    override suspend fun getCustomerByEmail(email: String): Customer? = null
    override suspend fun update(customer: Customer): Customer? = customer
    override suspend fun updatePassword(loginRequest: LoginRequest): Boolean = false
    override suspend fun getCustomerTableByEmail(email: String): CustomerTable? = null
    override suspend fun delete(id: String): Boolean = false
}
