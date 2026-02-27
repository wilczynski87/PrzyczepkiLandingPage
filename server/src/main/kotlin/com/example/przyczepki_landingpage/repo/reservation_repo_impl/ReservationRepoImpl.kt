package com.example.przyczepki_landingpage.repo.reservation_repo_impl

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.data.ReservationPrice
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.model.toLocalDate
import com.example.przyczepki_landingpage.modules.MongoProvider.database
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.gte
import com.mongodb.client.model.Filters.lte
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.util.Date

class ReservationRepoImpl : ReservationRepo {

    override suspend fun getAllReservations(
        from: LocalDate,
        to: LocalDate?,
    ): List<Reservation> {
        val from = from.toJavaLocalDate()
        val to = to?.toJavaLocalDate()
        println("ReservationRepoImpl: getAllReservations from: $from to: $to")
        val filter = if (to != null) {
            and(
                lte("startDate", to),
                gte("endDate", from)
            )
        } else {
            gte("endDate", from)
        }

        return reservationCollection
            .find(filter)
            .map { it.toReservation() }
            .toList()
    }

    override suspend fun getReservationById(id: String): Reservation? {
        return reservationCollection.find(Filters.eq("id", id)).firstOrNull()?.toReservation()
    }

    override suspend fun createReservation(reservation: Reservation): Reservation? {
        println("createReservation in ReservationRepoImpl: $reservation")
        return try {
            val _id = ObjectId()
            reservationCollection.insertOne(ReservationTable(_id = _id, reservation = reservation))
            reservationCollection.find(eq("_id", _id)).firstOrNull()?.toReservation() ?: throw Exception("Reservation not created in createReservation")
        } catch (e: Exception) {
            println("ReservationRepoImpl: Error saving reservation: ${e.message}")
            null
        }
    }

    override suspend fun deleteReservation(id: String): Boolean {
        return reservationCollection.findOneAndDelete(Filters.eq("id", id)) != null
    }

    override suspend fun checkReservationDates(
        trailerId: String,
        from: LocalDate,
        to: LocalDate
    ): Reservation? {
        val from = from.toJavaLocalDate()
        val to = to.toJavaLocalDate()

        val filters = and(
            eq("trailer.id", trailerId),
            lte("startDate", from),
            gte("endDate", to)
        )
        return reservationCollection.find(filters).firstOrNull()?.toReservation()
    }

}

data class ReservationTable(
    @Contextual
    val _id: ObjectId? = ObjectId(),
    val id: String? = null,
    val customer: Customer? = null,
    val trailer: Trailer? = null,
    val startDate: java.time.LocalDate? = null,
    val endDate: java.time.LocalDate? = null,
    val reservationPrice: ReservationPrice? = null,
) {
    constructor(_id: ObjectId? = null, reservation: Reservation) : this(
        _id = _id,
        id = _id?.toHexString() ?: reservation.id,
        customer = reservation.customer,
        trailer = reservation.trailer,
        startDate = reservation.startDate?.toJavaLocalDate(),
        endDate = reservation.endDate?.toJavaLocalDate(),
        reservationPrice = reservation.reservationPrice,
    )

   fun toReservation(): Reservation = Reservation(
       this.id,
       this.customer,
       this.trailer,
       this.startDate?.toKotlinLocalDate(),
       this.endDate?.toKotlinLocalDate(),
       this.reservationPrice
   )
}

val reservationCollection: MongoCollection<ReservationTable> = database.getCollection("reservation")
