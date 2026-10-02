package com.example.przyczepki_landingpage.support

import com.example.przyczepki_landingpage.data.dto.Coupon
import com.example.przyczepki_landingpage.data.dto.CouponStatus
import com.example.przyczepki_landingpage.repo.CouponRepo

class FakeCouponRepo(
    private val coupons: MutableMap<String, Coupon> = mutableMapOf(),
) : CouponRepo {

    fun add(coupon: Coupon) {
        val id = coupon.id ?: "coupon-${coupons.size + 1}"
        coupons[id] = coupon.copy(id = id, code = coupon.code?.trim()?.uppercase())
    }

    override suspend fun list(): List<Coupon> = coupons.values.toList()

    override suspend fun get(id: String): Coupon? = coupons[id]

    override suspend fun getByCode(code: String): Coupon? {
        val normalized = code.trim().uppercase()
        return coupons.values.firstOrNull { it.code?.trim()?.uppercase() == normalized }
    }

    override suspend fun save(coupon: Coupon): Coupon? {
        val id = coupon.id ?: "coupon-${coupons.size + 1}"
        val saved = coupon.copy(id = id, code = coupon.code?.trim()?.uppercase())
        coupons[id] = saved
        return saved
    }

    override suspend fun update(coupon: Coupon): Coupon? {
        val id = coupon.id ?: return null
        if (!coupons.containsKey(id)) return null
        val saved = coupon.copy(code = coupon.code?.trim()?.uppercase())
        coupons[id] = saved
        return saved
    }

    override suspend fun delete(id: String): Boolean = coupons.remove(id) != null

    override suspend fun markUsed(id: String): Coupon? {
        val current = coupons[id] ?: return null
        val used = current.copy(status = CouponStatus.USED)
        coupons[id] = used
        return used
    }
}
