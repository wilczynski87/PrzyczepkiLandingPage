package com.example.przyczepki_landingpage.service

import com.example.przyczepki_landingpage.data.Trailer


interface TrailersService {
    suspend fun getTrailers(): List<Trailer>
    suspend fun getTrailer(id: Int): Trailer?
    suspend fun saveTrailer(trailer: Trailer): Trailer?
    suspend fun deleteTrailer(id: Int): Boolean
    suspend fun updateTrailer(trailer: Trailer): Trailer?
}