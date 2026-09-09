package com.example.przyczepki_landingpage.repo.impl

import com.example.przyczepki_landingpage.repo.GateEventRepo
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Sorts.descending
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Contextual
import org.bson.types.ObjectId
import kotlin.time.Clock
import kotlin.time.Instant

class GateEventRepoImpl(
    private val collection: MongoCollection<GateEventTable>,
) : GateEventRepo {

    override suspend fun getLastOpenEvent(customerId: String): Instant? {
        return collection
            .find(eq("customerId", customerId))
            .sort(descending("openedAtEpochMs"))
            .limit(1)
            .firstOrNull()
            ?.openedAtEpochMs
            ?.let { Instant.fromEpochMilliseconds(it) }
    }

    override suspend fun logOpenEvent(customerId: String, reservationId: String) {
        collection.insertOne(
            GateEventTable(
                customerId = customerId,
                reservationId = reservationId,
                openedAtEpochMs = Clock.System.now().toEpochMilliseconds(),
            )
        )
    }
}

data class GateEventTable(
    @Contextual
    val _id: ObjectId? = ObjectId(),
    val customerId: String,
    val reservationId: String,
    val openedAtEpochMs: Long,
)
