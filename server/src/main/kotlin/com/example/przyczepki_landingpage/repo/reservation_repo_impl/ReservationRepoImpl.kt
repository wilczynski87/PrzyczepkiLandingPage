package com.example.przyczepki_landingpage.repo.reservation_repo_impl

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.data.ReservationPrice
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.modules.MongoProvider.database
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.service.startOfTheDay
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

class ReservationRepoImpl : ReservationRepo {

    override suspend fun getAllReservations(
        from: Long,
        to: Long?,
    ): List<Reservation> {
        return reservationCollection.find(
            if(to != null) {
                Filters.and(
                    Filters.gte("startDate", from),
                    Filters.lte("endDate", to)
                )
            } else Filters.gte("startDate", from)
        )
        .map { it.toReservation() }
        .toList()
    }

    override suspend fun getReservationById(id: String): Reservation? {
        return reservationCollection.find(Filters.eq("id", id)).firstOrNull()?.toReservation()
    }

    override suspend fun createReservation(reservation: Reservation): Reservation? {
        return try {
            val id = ObjectId()
            reservationCollection.insertOne(ReservationTable(_id = ObjectId(), reservation = reservation))
            reservationCollection.find(Filters.eq("_id", id)).firstOrNull()?.toReservation()
        } catch (e: Exception) {
            println("ReservationRepoImpl: Error saving reservation: ${e.message}")
            null
        }
    }

    override suspend fun deleteReservation(id: String): Boolean {
        return reservationCollection.findOneAndDelete(Filters.eq("id", id)) != null
    }

}

@Serializable
data class ReservationTable(
    @Contextual
    val _id: ObjectId? = ObjectId(),
    val id: String? = null,
    val customer: Customer? = null,
    val trailer: Trailer? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val reservationPrice: ReservationPrice? = null,
) {
    constructor(_id: ObjectId? = null, reservation: Reservation) : this(
        _id = _id,
        id = _id?.toHexString() ?: reservation.id,
        customer = reservation.customer,
        trailer = reservation.trailer,
        startDate = reservation.startDate,
        endDate = reservation.endDate,
        reservationPrice = reservation.reservationPrice,
    )

   fun toReservation(): Reservation = Reservation(this.id, this.customer, this.trailer, this.startDate, this.endDate, this.reservationPrice)
}

val reservationCollection: MongoCollection<ReservationTable> = database.getCollection("reservation")