package com.example.przyczepki_landingpage.di

import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.repo.impl.CustomerRepoImpl
import com.example.przyczepki_landingpage.repo.impl.ReservationRepoImpl
import com.example.przyczepki_landingpage.repo.impl.TrailersRepoImpl
import com.example.przyczepki_landingpage.service.CustomerService
import com.example.przyczepki_landingpage.service.ReservationService
import com.example.przyczepki_landingpage.service.TrailersService
import com.example.przyczepki_landingpage.service.impl.CustomerServiceImpl
import com.example.przyczepki_landingpage.service.impl.ReservationServiceImpl
import com.example.przyczepki_landingpage.service.impl.TrailersServiceImpl
import org.koin.dsl.module


// KOIN
val appModule = module {
    // Repositories
    single<TrailersRepo> { TrailersRepoImpl() }
    single<ReservationRepo> { ReservationRepoImpl() }
    single<CustomerRepo> { CustomerRepoImpl() }


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