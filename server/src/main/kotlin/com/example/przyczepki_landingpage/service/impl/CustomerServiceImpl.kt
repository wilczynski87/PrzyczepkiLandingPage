package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.modules.ApiConfig
import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.example.przyczepki_landingpage.repo.impl.CustomerTable
import com.example.przyczepki_landingpage.service.CustomerService
import io.ktor.server.plugins.BadRequestException
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.koin.ktor.ext.inject
import pl.przyczepki.email.api.dto.AccountConfirmationData
import kotlin.time.Clock

class CustomerServiceImpl(
    private val apiBaseUrl: String,
    private val customerRepo: CustomerRepo
): CustomerService {
    override suspend fun save(customer: Customer): Customer? =
        customerRepo.save(customer)

    override suspend fun accountConfirmationData(customer: Customer): AccountConfirmationData {
        val confirmationLink = "${apiBaseUrl}customer/confirm/${customer.id}"
        return AccountConfirmationData(
            confirmationLink = confirmationLink,
            recipientName = customer.getName(),
            email = customer.getEmail() ?: throw BadRequestException("Brak adresu email"),
            firstName = customer.private?.firstName ?: "Drogi Kliencie",
            lastName = customer.private?.lastName ?: "",
            taxIdentifier = customer.company?.nip ?: customer.private?.pesel ?: throw BadRequestException("Brak nip lub pesel"),
            taxIdentifierLabel = if(customer.company == null) "PESEL" else "NIP",
            linkExpiresAt = Clock.System.now()
                .plus(1, DateTimeUnit.HOUR)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
                .toString(),
        )
    }

    override suspend fun confirm(id: String): Customer? = customerRepo.confirm(id)

    override suspend fun get(id: String): Customer? = customerRepo.get(id)

    override suspend fun getCustomerByEmail(email: String): Customer? {
        return customerRepo.getByEmail(email)
    }

    override suspend fun update(customer: Customer): Customer? {
        TODO("Not yet implemented")
    }

    override suspend fun updatePassword(loginRequest: LoginRequest): Boolean = customerRepo.updatePassword(loginRequest)
    override suspend fun getCustomerTableByEmail(email: String): CustomerTable? = customerRepo.getCustomerTableByEmail(email)

    override suspend fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }
}