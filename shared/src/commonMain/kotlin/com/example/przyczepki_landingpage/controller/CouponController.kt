package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.dto.Coupon
import com.example.przyczepki_landingpage.data.dto.CouponValidateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class CouponController(private val client: HttpClient) {

    suspend fun validate(code: String, customerId: String?): Result<Coupon> {
        return try {
            val response = client.post("$base_url/coupon/validate") {
                setBody(CouponValidateRequest(code = code, customerId = customerId))
            }
            if (!response.status.isSuccess()) {
                val body = runCatching { response.bodyAsText() }.getOrDefault("")
                val message = Regex(""""error"\s*:\s*"([^"]+)"""").find(body)?.groupValues?.get(1)
                    ?: body.ifBlank { "Nie udało się zastosować kuponu" }
                Result.failure(Exception(message))
            } else {
                Result.success(response.body())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
