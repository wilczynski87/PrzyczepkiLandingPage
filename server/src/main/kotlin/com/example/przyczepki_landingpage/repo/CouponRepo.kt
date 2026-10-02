package com.example.przyczepki_landingpage.repo

import com.example.przyczepki_landingpage.data.dto.Coupon

interface CouponRepo {
    suspend fun list(): List<Coupon>
    suspend fun get(id: String): Coupon?
    suspend fun getByCode(code: String): Coupon?
    suspend fun save(coupon: Coupon): Coupon?
    suspend fun update(coupon: Coupon): Coupon?
    suspend fun delete(id: String): Boolean
    suspend fun markUsed(id: String): Coupon?
}
