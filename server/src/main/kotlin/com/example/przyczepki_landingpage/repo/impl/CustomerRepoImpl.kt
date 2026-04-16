package com.example.przyczepki_landingpage.repo.impl

import com.example.przyczepki_landingpage.data.Company
import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.LoginRequest
import com.example.przyczepki_landingpage.data.Private
import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.example.przyczepki_landingpage.service.auth.PasswordUtil.hash
import com.mongodb.client.model.Aggregates.set
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.or
import com.mongodb.client.model.Updates.set
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

class CustomerRepoImpl(
    private val customerCollection: MongoCollection<CustomerTable>
): CustomerRepo {

    override suspend fun save(customer: Customer): Customer? {
        val toSave = customer.toTable()

        val customerId = customerCollection.insertOne(toSave).insertedId

        return customerCollection.find(eq("_id", customerId)).firstOrNull()?.toCustomer()
    }

    override suspend fun get(id: String): Customer? = customerCollection.find(eq("id", id)).firstOrNull()?.toCustomer()

    override suspend fun getByEmail(email: String): Customer? =
        customerCollection.find( or(
            eq("private.email", email),
            eq("company.email", email)
            )
        ).firstOrNull()?.toCustomer()

    override suspend fun update(customer: Customer): Customer? {
        TODO("Not yet implemented")
    }

    override suspend fun updatePassword(loginRequest: LoginRequest): Boolean {
        val email = loginRequest.email
        return customerCollection.updateOne(
            filter = or(
                eq("private.email", email),
                eq("company.email", email)
            ),
            update = set(CustomerTable::passwordHash.name, hash(loginRequest.password)),
         ).wasAcknowledged()
    }

    override suspend fun getCustomerTableByEmail(email: String): CustomerTable? {
        return customerCollection.find(and(
            or(
                eq("private.email", email),
                eq("company.email", email)
            )
        )).firstOrNull()
    }

    override suspend fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }
}

@Serializable
data class CustomerTable(
    @Contextual
    val _id: ObjectId = ObjectId(),
    val id: String? = null,
    val private: Private? = null,
    val company: Company? = null,
    val passwordHash: String? = null
) {
    fun toCustomer(): Customer = Customer(
        id = id,
        private = private,
        company = company,
    )
}
fun Customer.toTable(): CustomerTable {
    val _id = ObjectId()
    return CustomerTable(
        _id = _id,
        id = _id.toHexString(),
        private = private,
        company = company,
        passwordHash = company?.nip ?: private?.pesel ?: ""
    )
}
