package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Customer
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

data class CustomerController( private val client: HttpClient ) {

    suspend fun saveCustomer(customer: Customer): Result<Customer?> {
        return try {
            val response = client.post("$base_url/customer") {
                setBody(customer)
            }.body<Customer?>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCustomer(clientId: String): Result<Customer?> {
        return try {
            val customer = client.get("$base_url/customer/$clientId").body<Customer?>()
            Result.success(customer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCustomer(customer: Customer): Result<Customer?> {
        return try {
            val response = client.put("$base_url/customer") {
                setBody(customer)
            }.body<Customer?>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCustomer(clientId: String): Result<Boolean> {
        return try {
            val response = client.delete("$base_url/customer/$clientId")
            Result.success(response.status.value == 200)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
