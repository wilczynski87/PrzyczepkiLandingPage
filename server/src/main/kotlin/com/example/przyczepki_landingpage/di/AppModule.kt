package com.example.przyczepki_landingpage.di

import com.example.przyczepki_landingpage.data.Customer
import com.example.przyczepki_landingpage.data.Reservation
import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.repo.impl.CustomerRepoImpl
import com.example.przyczepki_landingpage.repo.impl.CustomerTable
import com.example.przyczepki_landingpage.repo.impl.ReservationRepoImpl
import com.example.przyczepki_landingpage.repo.impl.ReservationTable
import com.example.przyczepki_landingpage.repo.impl.TrailerTable
import com.example.przyczepki_landingpage.repo.impl.TrailersRepoImpl
import com.example.przyczepki_landingpage.service.CustomerService
import com.example.przyczepki_landingpage.service.ReservationService
import com.example.przyczepki_landingpage.service.TrailersService
import com.example.przyczepki_landingpage.service.impl.CustomerServiceImpl
import com.example.przyczepki_landingpage.service.impl.ReservationServiceImpl
import com.example.przyczepki_landingpage.service.impl.TrailersServiceImpl
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.koin.core.qualifier.named
import org.koin.dsl.module

// KOIN
val appModule = module {
    single<MongoClient> {
        val connectionString = System.getenv("MONGO_URI")
            ?: error("MONGO_URI not set")

        MongoClient.create(connectionString)
    }
    single<MongoDatabase> { get<MongoClient>().getDatabase("przyczepki") }

    single(named("trailerCollection")) {
        val db: MongoDatabase = get()
        db.getCollection<TrailerTable>("trailer")
    }
    single(named("reservationCollection")) {
        get<MongoDatabase>().getCollection<ReservationTable>("reservation")
    }
    single(named("customerCollection")) {
        get<MongoDatabase>().getCollection<CustomerTable>("customer")
    }

    // Repositories
    single<TrailersRepo> { TrailersRepoImpl(get(named("trailerCollection"))) }
    single<ReservationRepo> { ReservationRepoImpl(get(named("reservationCollection"))) }
    single<CustomerRepo> { CustomerRepoImpl(get(named("customerCollection"))) }


    // Services
    single<TrailersService> {
        TrailersServiceImpl(
            trailersRepo = get()
        )
    }

    single<ReservationService> {
        ReservationServiceImpl(
            reservationRepo = get(),
            trailersRepo = get()
        )
    }

    single<CustomerService> {
        CustomerServiceImpl(
            customerRepo = get()
        )
    }

//    factory { AuthService() }
}