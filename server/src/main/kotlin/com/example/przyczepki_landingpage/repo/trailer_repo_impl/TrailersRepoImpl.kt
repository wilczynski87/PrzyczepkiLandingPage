package com.example.przyczepki_landingpage.repo.trailer_repo_impl

import com.example.przyczepki_landingpage.data.LicenseCategory
import com.example.przyczepki_landingpage.data.Prices
import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.modules.MongoProvider
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.bson.conversions.Bson

class TrailersRepoImpl: TrailersRepo {

    override suspend fun getTrailers(): List<Trailer> {
        return trailerCollection
            .find()
            .map { it.toTrailer() }
            .toList()
    }

    override suspend fun getTrailer(id: String): Trailer? {
        return trailerCollection.find(Filters.eq("id", id)).firstOrNull()?.toTrailer()
    }

    override suspend fun saveTrailer(trailer: Trailer): Trailer? {
        val id = ObjectId()
        val trailerRepo = TrailerRepo(trailer).copy(_id = id, id = id.toHexString(), prices = trailer.prices?.copy(trailerId = id.toHexString()))
        return try {
            val _id = trailerCollection.insertOne(trailerRepo).insertedId
            trailerCollection.find(Filters.eq("_id", _id)).firstOrNull()?.toTrailer()
        } catch (e: Exception) {
            println("TrailersRepoImpl: Error saving trailer: ${e.message}")
            null
        } finally {
//            trailerCollection.
        }
    }

    // zwraca true jeśli usunął
    override suspend fun deleteTrailer(id: String): Boolean {
        return trailerCollection.findOneAndDelete(Filters.eq("id", id)) != null
    }

    override suspend fun updateTrailer(trailer: Trailer): Trailer? {
        val updated = TrailerRepo(trailer)
        val updates = mutableListOf<Bson>()

        updated.name?.let { updates.add(Updates.set("name", it)) }
        updated.size?.let { updates.add(Updates.set("size", it)) }
        updated.loadingMass?.let { updates.add(Updates.set("loadingMass", it)) }
        updated.gvw?.let { updates.add(Updates.set("gvw", it)) }
        updated.purpose?.let { updates.add(Updates.set("purpose", it)) }
        updated.axles?.let { updates.add(Updates.set("axles", it)) }
        updated.licenseCategory?.let { updates.add(Updates.set("licenseCategory", it.name)) } // enum → string
        updated.hasBreaks?.let { updates.add(Updates.set("hasBreaks", it)) }
        updated.prices?.let { updates.add(Updates.set("prices", it)) }
        updated.images?.let { updates.add(Updates.set("images", it)) }

        if (updates.isNotEmpty()) {
            val result = trailerCollection.updateOne(
                filter = Filters.eq("id", trailer.id),
                update = Updates.combine(updates)
            )
            println("Zaktualizowano dokumentów: ${result.modifiedCount}")
            return getTrailer(trailer.id ?: return null)
        } else {
            println("Brak pól do aktualizacji")
            return null
        }
    }

//    private fun trailerToRepo(trailer: Trailer): TrailerRepo {
//        return TrailerRepo(
//            _id = ObjectId(),
//            name = trailer.name,
//            size = trailer.size,
//            loadingMass = trailer.loadingMass,
//            gvw = trailer.gvw,
//            purpose = trailer.purpose,
//            axles = trailer.axles,
//            licenseCategory = trailer.licenseCategory,
//            hasBreaks = trailer.hasBreaks,
//            prices = trailer.prices,
//            images = trailer.images
//        )
//    }
}

@Serializable
data class TrailerRepo(
    @Contextual
    val _id: ObjectId = ObjectId(),
    val id: String? = _id.toHexString(),
    val name: String? = null,
    val size: String? = null,
    val loadingMass: Double? = null,
    val gvw: Double? = null,
    val purpose: String? = null,
    val axles: Int? = null,
    val licenseCategory: LicenseCategory? = null,
    val hasBreaks: Boolean? = null,
    val prices: Prices? = null,
    val images: Map<String, String>? = null,
) {
    constructor(trailer: Trailer) : this(
        id = trailer.id,
        name = trailer.name,
        size = trailer.size,
        loadingMass = trailer.loadingMass,
        gvw = trailer.gvw,
        purpose = trailer.purpose,
        axles = trailer.axles,
        licenseCategory = trailer.licenseCategory,
        hasBreaks = trailer.hasBreaks,
        prices = trailer.prices,
        images = trailer.images,
    )
    fun toTrailer(): Trailer {
        return Trailer(this.id, this.name, this.size, this.loadingMass, this.gvw, this.purpose, this.axles, this.licenseCategory, this.hasBreaks, this.prices, this.images)
    }
}


val trailerCollection: MongoCollection<TrailerRepo> =
    MongoProvider.database.getCollection("trailer")