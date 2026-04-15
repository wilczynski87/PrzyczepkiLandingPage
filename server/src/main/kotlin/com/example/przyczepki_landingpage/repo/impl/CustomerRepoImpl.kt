package com.example.przyczepki_landingpage.repo.impl

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.modules.MongoProvider.database
import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.or
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

class CustomerRepoImpl: CustomerRepo {

    override suspend fun save(customer: Customer): Customer? {
        return try {
            val id = ObjectId().toHexString()
            val toSave = customer.copy(
                id = id,
                passwordHash = customer.private?.pesel
            )

            customerCollection.insertOne(toSave)

            customerCollection.find(eq("id", id)).firstOrNull()
        } catch (e: Exception) {
            println("CustomerRepoImpl, save error: ${e.message}")
            null
        }
    }

    override suspend fun get(id: String): Customer? = try {
        customerCollection.find(eq("id", id)).firstOrNull()
        } catch (e: Exception) {
            println("CustomerRepoImpl, get error: ${e.message}")
            null
        }

    override suspend fun getByEmail(email: String): Customer? = try {
        customerCollection.find( or(
            eq("private.email", email),
            eq("company.email", email)
            )
        ).firstOrNull()
    } catch (e: Exception) {
        println("CustomerRepoImpl, get error: ${e.message}")
        null
    }

    override suspend fun update(customer: Customer): Customer? {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String): Boolean {
        TODO("Not yet implemented")
    }
}

val customerCollection: MongoCollection<Customer> = database.getCollection("customer")