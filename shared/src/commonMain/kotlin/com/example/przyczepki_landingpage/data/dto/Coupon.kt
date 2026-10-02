package com.example.przyczepki_landingpage.data.dto

import kotlinx.serialization.Contextual
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
    @Contextual
    val discount: Discount? = null,
)


enum class CouponStatus {
    ACTIVE,
    EXPIRED,
    INACTIVE,
    USED,
}

sealed class Discount {
    data class Fixed(val amount: Double) : Discount()
    data class Percentage(val percentage: Double) : Discount()
    // give fixed price for trailer, like every day and reservation cost 25 zl
    data class FixedPrice(val price: Double) : Discount()
}