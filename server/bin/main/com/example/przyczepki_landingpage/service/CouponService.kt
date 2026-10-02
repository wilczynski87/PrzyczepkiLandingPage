package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.ReservationPrice
import com.example.przyczepki_landingpage.data.dto.Coupon

interface CouponService {
    suspend fun list(): List<Coupon>
    suspend fun get(id: String): Coupon?
    suspend fun create(coupon: Coupon): Coupon
    suspend fun update(coupon: Coupon): Coupon?
    suspend fun delete(id: String): Boolean
    suspend fun requireUsable(code: String, customerId: String?): Coupon
    suspend fun applyToPrice(price: ReservationPrice, coupon: Coupon): ReservationPrice
    suspend fun markUsed(id: String)
}
