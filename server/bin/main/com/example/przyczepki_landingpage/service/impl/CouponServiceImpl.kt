package com.example.przyczepki_landingpage.service.impl

import com.example.przyczepki_landingpage.data.ReservationPrice
import com.example.przyczepki_landingpage.data.dto.Coupon
import com.example.przyczepki_landingpage.data.dto.CouponStatus
import com.example.przyczepki_landingpage.data.dto.Discount
import com.example.przyczepki_landingpage.repo.CouponRepo
import com.example.przyczepki_landingpage.service.CouponService
import com.example.przyczepki_landingpage.service.startOfTheDay
import kotlinx.datetime.LocalDate
import kotlin.math.max
import kotlin.math.round

class CouponServiceImpl(
    private val couponRepo: CouponRepo,
) : CouponService {

    override suspend fun list(): List<Coupon> = couponRepo.list()

    override suspend fun get(id: String): Coupon? = couponRepo.get(id)

    override suspend fun create(coupon: Coupon): Coupon {
        val normalized = normalize(coupon)
        if (normalized.code.isNullOrBlank()) throw IllegalArgumentException("Kod kuponu jest wymagany")
        if (normalized.discount == null) throw IllegalArgumentException("Typ zniżki jest wymagany")
        if (couponRepo.getByCode(normalized.code!!) != null) {
            throw IllegalArgumentException("Kupon o kodzie ${normalized.code} już istnieje")
        }
        return couponRepo.save(normalized) ?: throw IllegalStateException("Nie udało się zapisać kuponu")
    }

    override suspend fun update(coupon: Coupon): Coupon? {
        if (coupon.id.isNullOrBlank()) return null
        return couponRepo.update(normalize(coupon.copy(createdAt = coupon.createdAt)))
    }

    override suspend fun delete(id: String): Boolean = couponRepo.delete(id)

    override suspend fun requireUsable(code: String, customerId: String?): Coupon {
        val coupon = couponRepo.getByCode(code)
            ?: throw IllegalArgumentException("Nie znaleziono kuponu")
        val today = today()
        val expiration = coupon.expirationDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        if (expiration != null && expiration < today) {
            if (coupon.status == CouponStatus.ACTIVE && coupon.id != null) {
                couponRepo.update(coupon.copy(status = CouponStatus.EXPIRED))
            }
            throw IllegalArgumentException("Kupon wygasł")
        }
        when (coupon.status ?: CouponStatus.ACTIVE) {
            CouponStatus.USED -> throw IllegalArgumentException("Kupon został już wykorzystany")
            CouponStatus.INACTIVE -> throw IllegalArgumentException("Kupon jest nieaktywny")
            CouponStatus.EXPIRED -> throw IllegalArgumentException("Kupon wygasł")
            CouponStatus.ACTIVE -> Unit
        }
        val owner = coupon.clientId?.trim().orEmpty()
        if (owner.isNotBlank() && owner != customerId?.trim()) {
            throw IllegalArgumentException("Ten kupon jest przypisany do innego klienta")
        }
        return coupon
    }

    override suspend fun applyToPrice(price: ReservationPrice, coupon: Coupon): ReservationPrice {
        val discount = coupon.discount ?: throw IllegalArgumentException("Kupon nie ma zniżki")
        val originalSum = price.sum ?: 0.0
        val originalReservation = price.reservation ?: 0.0
        val days = price.daysNumber ?: 0L
        val (sum, reservation) = when (discount) {
            is Discount.Fixed -> {
                val nextSum = max(0.0, originalSum - discount.amount)
                nextSum to minOf(originalReservation, nextSum)
            }
            is Discount.Percentage -> {
                val factor = 1.0 - (discount.percentage.coerceIn(0.0, 100.0) / 100.0)
                roundMoney(originalSum * factor) to roundMoney(originalReservation * factor)
            }
            is Discount.FixedPrice -> {
                val billedDays = if (days <= 0L) 1L else days
                roundMoney(discount.price * billedDays) to roundMoney(discount.price)
            }
        }
        return price.copy(
            sum = sum,
            reservation = reservation,
            couponCode = coupon.code,
            originalSum = originalSum,
        )
    }

    override suspend fun markUsed(id: String) {
        couponRepo.markUsed(id)
    }

    private fun normalize(coupon: Coupon): Coupon {
        val today = today().toString()
        return coupon.copy(
            code = coupon.code?.trim()?.uppercase(),
            clientId = coupon.clientId?.trim()?.ifBlank { null },
            status = coupon.status ?: CouponStatus.ACTIVE,
            createdAt = coupon.createdAt ?: today,
        )
    }

    private fun today(): LocalDate = startOfTheDay()

    private fun roundMoney(value: Double): Double = round(value * 100.0) / 100.0
}
