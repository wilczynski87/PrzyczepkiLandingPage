package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.data.Trailer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

const val base_url = "http://localhost:8080"

object ApiClient {

    val trailerController by lazy { TrailerController() }
    

}