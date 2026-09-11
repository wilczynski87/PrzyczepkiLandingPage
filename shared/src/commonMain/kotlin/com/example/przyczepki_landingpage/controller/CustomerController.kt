package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.CustomerRegisterRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess

data class CustomerController( private val client: HttpClient ) {

    suspend fun saveCustomer(customer: Customer, password: String): Result<Customer?> {
        return try {
            val response = client.post("$base_url/customer") {
                setBody(CustomerRegisterRequest(customer = customer, password = password))
            }.body<Customer?>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCustomer(clientId: String): Result<Customer?> {
        return try {
            val response = client.get("$base_url/customer/$clientId")
            when {
                response.status.isSuccess() -> Result.success(response.body())
                response.status.value == 401 || response.status.value == 403 ->
                    Result.failure(InvalidLoginCredentialsException())
                else -> Result.failure(Exception("Server error: ${response.status}"))
            }
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
