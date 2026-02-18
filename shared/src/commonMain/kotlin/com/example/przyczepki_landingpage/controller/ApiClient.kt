package com.example.przyczepki_landingpage.controller

import com.example.przyczepki_landingpage.createHttpClient
import com.example.przyczepki_landingpage.data.Trailer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.api.Send.install
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

const val base_url = "http://przyczepki-api:8090"
//const val base_url = "http://localhost:8090"

object ApiClient {
    val client: HttpClient = createHttpClient()


    val trailerController by lazy { TrailerController(client) }
    

}