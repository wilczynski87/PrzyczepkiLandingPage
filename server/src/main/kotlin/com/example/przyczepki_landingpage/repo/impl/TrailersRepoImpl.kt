package com.example.przyczepki_landingpage.repo.impl

import com.example.przyczepki_landingpage.data.LicenseCategory
import com.example.przyczepki_landingpage.data.Prices
import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.data.Trailer
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

class TrailersRepoImpl(
    private val trailerCollection: MongoCollection<TrailerTable>
): TrailersRepo {

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
        val trailerToSave = trailer.toTable()

        val trailerId = trailerCollection.insertOne(trailerToSave).insertedId ?: throw NullPointerException("Id of Trailer is null: $trailer")

        return trailerCollection.find(Filters.eq("_id", trailerId)).firstOrNull()?.toTrailer()
    }

    // zwraca true jeśli usunął
    override suspend fun deleteTrailer(id: String): Boolean {
        return trailerCollection.deleteOne(Filters.eq("id", id)).deletedCount > 0
    }

    override suspend fun updateTrailer(trailer: Trailer): Trailer? {
        val updated = trailer.toTable()
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
}

@Serializable
data class TrailerTable(
    @Contextual
    val _id: ObjectId = ObjectId(),
    val id: String? = null,
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

    fun toTrailer(): Trailer {
        return Trailer(
            id = id,
            name = name,
            size = size,
            loadingMass = loadingMass,
            gvw = gvw,
            purpose = purpose,
            axles = axles,
            licenseCategory = licenseCategory,
            hasBreaks = hasBreaks,
            prices = prices,
            images = images,
        )
    }
}

fun Trailer.toTable(): TrailerTable {
    val id = ObjectId()
    return TrailerTable(
        _id = id,
        id = id.toHexString(),
        name = name,
        size = size,
        loadingMass = loadingMass,
        gvw = gvw,
        purpose = purpose,
        axles = axles,
        licenseCategory = licenseCategory,
        hasBreaks = hasBreaks,
        prices = prices?.copy(trailerId = id.toHexString()),
        images = images
    )
}