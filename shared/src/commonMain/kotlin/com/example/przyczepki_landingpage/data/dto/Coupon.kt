package com.example.przyczepki_landingpage.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Coupon(
    val id: String? = null,
    val code: String? = null,
    val description: String? = null,
    val clientId: String? = null,
    val expirationDate: String? = null,
    val status: CouponStatus? = null,
    val createdAt: String? = null,
    val discount: Discount? = null,
)

@Serializable
enum class CouponStatus {
    ACTIVE,
    EXPIRED,
    INACTIVE,
    USED,
}

@Serializable
sealed class Discount {
    @Serializable
    @SerialName("fixed")
    data class Fixed(val amount: Double) : Discount()

    @Serializable
    @SerialName("percentage")
    data class Percentage(val percentage: Double) : Discount()

    /** Stała cena za każdą dobę i za kaucję, np. 25 zł. */
    @Serializable
    @SerialName("fixedPrice")
    data class FixedPrice(val price: Double) : Discount()
}

@Serializable
data class CouponValidateRequest(
    val code: String,
    val customerId: String? = null,
)
