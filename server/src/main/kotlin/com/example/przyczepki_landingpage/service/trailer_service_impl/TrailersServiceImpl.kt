package com.example.przyczepki_landingpage.service.trailer_service_impl

import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.service.TrailersService
import com.example.przyczepki_landingpage.data.Trailer

class TrailersServiceImpl(
    private val trailersRepo: TrailersRepo,
): TrailersService {
    override suspend fun getTrailers(): List<Trailer> {
        return trailersRepo.getTrailers()
    }

    override suspend fun getTrailer(id: Int): Trailer? {
        return trailersRepo.getTrailer(id)
    }

    override suspend fun saveTrailer(trailer: Trailer): Trailer? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrailer(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateTrailer(trailer: Trailer): Trailer? {
        TODO("Not yet implemented")
    }
}