package com.example.przyczepki_landingpage.modules

import kotlin.getValue
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase

object MongoProvider {
    private val CONNECTION_STRING by lazy {
        System.getenv("MONGO_URI")
    }
//    private const val CONNECTION_STRING = "mongodb://admin:admin@localhost:27017/przyczepki?authSource=admin"
    private val client: MongoClient by lazy {
        MongoClient.create(CONNECTION_STRING)
    }
    val database: MongoDatabase by lazy {
        client.getDatabase("przyczepki")
    }
}