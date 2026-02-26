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

    override suspend fun getTrailer(id: String): Trailer? {
        return trailersRepo.getTrailer(id)
    }

    override suspend fun saveTrailer(trailer: Trailer): Trailer? {
        return trailersRepo.saveTrailer(trailer)
    }

    override suspend fun deleteTrailer(id: String): Boolean {
        return trailersRepo.deleteTrailer(id)
    }

    override suspend fun updateTrailer(trailer: Trailer): Trailer? {
        return trailersRepo.updateTrailer(trailer)
    }
}