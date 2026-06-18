package com.example.przyczepki_landingpage.support

import com.example.przyczepki_landingpage.data.Trailer
import com.example.przyczepki_landingpage.repo.TrailersRepo

class FakeTrailersRepo(
    private val trailers: MutableMap<String, Trailer> = mutableMapOf(),
) : TrailersRepo {

    fun addTrailer(trailer: Trailer) {
        trailers[trailer.id!!] = trailer
    }

    override suspend fun getTrailers(): List<Trailer> = trailers.values.toList()

    override suspend fun getTrailer(id: String): Trailer? = trailers[id]

    override suspend fun saveTrailer(trailer: Trailer): Trailer? {
        trailers[trailer.id!!] = trailer
        return trailer
    }

    override suspend fun deleteTrailer(id: String): Boolean = trailers.remove(id) != null

    override suspend fun updateTrailer(trailer: Trailer): Trailer? = saveTrailer(trailer)
}
