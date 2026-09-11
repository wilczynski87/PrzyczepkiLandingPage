package com.example.przyczepki_landingpage.support

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.example.przyczepki_landingpage.repo.impl.CustomerTable

class FakeCustomerRepo(
    private val customers: MutableMap<String, Customer> = mutableMapOf(),
) : CustomerRepo {

    fun addCustomer(customer: Customer) {
        val id = customer.id ?: error("Customer id is required for FakeCustomerRepo")
        customers[id] = customer
    }

    override suspend fun save(customer: Customer, password: String): Customer? {
        val id = customer.id ?: "customer-${customers.size + 1}"
        val saved = customer.copy(id = id)
        customers[id] = saved
        return saved
    }

    override suspend fun confirm(id: String): Customer? = customers[id]

    override suspend fun get(id: String): Customer? = customers[id]

    override suspend fun getByEmail(email: String): Customer? =
        customers.values.firstOrNull { it.getEmail() == email }

    override suspend fun update(customer: Customer): Customer? {
        val id = customer.id ?: return null
        customers[id] = customer
        return customer
    }

    override suspend fun updatePassword(loginRequest: LoginRequest): Boolean = true

    override suspend fun getCustomerTableByEmail(email: String): CustomerTable? = null

    override suspend fun delete(id: String): Boolean = customers.remove(id) != null
}
