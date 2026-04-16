package com.example.przyczepki_landingpage.modules

import kotlin.getValue
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import org.koin.ktor.ext.getKoin

//object MongoProvider {
//    private val CONNECTION_STRING by lazy {
//        System.getenv("MONGO_URI")
//            ?: error("MONGO_URI not set")
//    }
////    private const val CONNECTION_STRING = "mongodb://admin:admin@localhost:27017/przyczepki?authSource=admin"
//    val mongoClient: MongoClient by lazy {
//        MongoClient.create(CONNECTION_STRING)
//    }
//    val database: MongoDatabase by lazy {
//        mongoClient.getDatabase("przyczepki")
//    }
//}

fun Application.configureMongo() {
    val client = getKoin().get<MongoClient>()

    monitor.subscribe(ApplicationStopping) {
        client.close()
    }
}