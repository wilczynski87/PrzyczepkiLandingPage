package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.example.przyczepki_landingpage.service.CustomerService

class CustomerServiceImpl(private val customerRepo: CustomerRepo): CustomerService {
    override suspend fun save(customer: Customer): Customer? =
        customerRepo.save(customer)

    override suspend fun get(id: String): Customer? = customerRepo.get(id)

    override suspend fun getCustomerByEmail(email: String): Customer? {
        return customerRepo.getByEmail(email)
    }

    override suspend fun update(customer: Customer): Customer? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }
}