package com.example.przyczepki_landingpage.repo.impl

import com.example.przyczepki_landingpage.data.PaymentSessionStatus
import com.example.przyczepki_landingpage.repo.PendingPaymentRepo
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.combine
import com.mongodb.client.model.Updates.set
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import org.bson.types.ObjectId

class PendingPaymentRepoImpl(
    private val collection: MongoCollection<PendingPaymentTable>,
) : PendingPaymentRepo {

    override suspend fun save(pending: PendingPaymentTable): PendingPaymentTable {
        val toSave = pending.copy(_id = pending._id ?: ObjectId())
        collection.insertOne(toSave)
        return collection.find(eq("sessionId", toSave.sessionId)).firstOrNull() ?: toSave
    }

    override suspend fun findBySessionId(sessionId: String): PendingPaymentTable? =
        collection.find(eq("sessionId", sessionId)).firstOrNull()

    override suspend fun updateStatus(
        sessionId: String,
        status: PaymentSessionStatus,
        orderId: Long?,
        reservationId: String?,
        errorMessage: String?,
    ): PendingPaymentTable? {
        val updates = buildList {
            add(set("status", status.name))
            orderId?.let { add(set("orderId", it)) }
            reservationId?.let { add(set("reservationId", it)) }
            errorMessage?.let { add(set("errorMessage", it)) }
        }
        collection.findOneAndUpdate(eq("sessionId", sessionId), combine(updates))
        return findBySessionId(sessionId)
    }
}
