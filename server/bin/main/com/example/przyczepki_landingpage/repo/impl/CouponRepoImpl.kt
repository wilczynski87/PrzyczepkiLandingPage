package com.example.przyczepki_landingpage.repo.impl

import com.example.przyczepki_landingpage.data.dto.Coupon
import com.example.przyczepki_landingpage.data.dto.CouponStatus
import com.example.przyczepki_landingpage.data.dto.Discount
import com.example.przyczepki_landingpage.repo.CouponRepo
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.combine
import com.mongodb.client.model.Updates.set
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.Contextual
import org.bson.types.ObjectId

class CouponRepoImpl(
    private val couponCollection: MongoCollection<CouponTable>,
) : CouponRepo {

    override suspend fun list(): List<Coupon> =
        couponCollection.find().map { it.toCoupon() }.toList()

    override suspend fun get(id: String): Coupon? =
        couponCollection.find(eq("id", id)).firstOrNull()?.toCoupon()

    override suspend fun getByCode(code: String): Coupon? {
        val normalized = code.trim().uppercase()
        if (normalized.isBlank()) return null
        return couponCollection.find().map { it.toCoupon() }.toList()
            .firstOrNull { it.code?.trim()?.uppercase() == normalized }
    }

    override suspend fun save(coupon: Coupon): Coupon? {
        val id = ObjectId()
        couponCollection.insertOne(coupon.toTable(id))
        return couponCollection.find(eq("_id", id)).firstOrNull()?.toCoupon()
    }

    override suspend fun update(coupon: Coupon): Coupon? {
        val id = coupon.id ?: return null
        couponCollection.updateOne(
            eq("id", id),
            combine(
                set("code", coupon.code),
                set("description", coupon.description),
                set("clientId", coupon.clientId),
                set("expirationDate", coupon.expirationDate),
                set("status", coupon.status?.name),
                set("createdAt", coupon.createdAt),
                set("discount", coupon.discount.toTable()),
            ),
        )
        return get(id)
    }

    override suspend fun delete(id: String): Boolean =
        couponCollection.deleteOne(eq("id", id)).deletedCount > 0

    override suspend fun markUsed(id: String): Coupon? {
        couponCollection.updateOne(eq("id", id), set("status", CouponStatus.USED.name))
        return get(id)
    }
}

data class CouponTable(
    @Contextual
    val _id: ObjectId = ObjectId(),
    val id: String? = null,
    val code: String? = null,
    val description: String? = null,
    val clientId: String? = null,
    val expirationDate: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val discount: DiscountTable? = null,
) {
    fun toCoupon(): Coupon = Coupon(
        id = id,
        code = code,
        description = description,
        clientId = clientId,
        expirationDate = expirationDate,
        status = status?.let { runCatching { CouponStatus.valueOf(it) }.getOrNull() },
        createdAt = createdAt,
        discount = discount?.toDiscount(),
    )
}

data class DiscountTable(
    val type: String? = null,
    val amount: Double? = null,
    val percentage: Double? = null,
    val price: Double? = null,
) {
    fun toDiscount(): Discount? = when (type) {
        "fixed", "Fixed" -> amount?.let { Discount.Fixed(it) }
        "percentage", "Percentage" -> percentage?.let { Discount.Percentage(it) }
        "fixedPrice", "FixedPrice" -> price?.let { Discount.FixedPrice(it) }
        else -> null
    }
}

private fun Coupon.toTable(objectId: ObjectId = ObjectId()): CouponTable = CouponTable(
    _id = objectId,
    id = objectId.toHexString(),
    code = code?.trim()?.uppercase(),
    description = description,
    clientId = clientId,
    expirationDate = expirationDate,
    status = (status ?: CouponStatus.ACTIVE).name,
    createdAt = createdAt,
    discount = discount.toTable(),
)

private fun Discount?.toTable(): DiscountTable? = when (this) {
    is Discount.Fixed -> DiscountTable(type = "fixed", amount = amount)
    is Discount.Percentage -> DiscountTable(type = "percentage", percentage = percentage)
    is Discount.FixedPrice -> DiscountTable(type = "fixedPrice", price = price)
    null -> null
}
