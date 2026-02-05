package com.example.przyczepki_landingpage.di

import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.repo.trailer_repo_impl.TrailersRepoImpl
import com.example.przyczepki_landingpage.service.TrailersService
import com.example.przyczepki_landingpage.service.auth.AuthController
import com.example.przyczepki_landingpage.service.trailer_service_impl.TrailersServiceImpl
import org.koin.dsl.module


// KOIN
val appModule = module {
    // Repositories
    single<TrailersRepo> { TrailersRepoImpl() }

    // Services
    single<TrailersService> {
        TrailersServiceImpl(
            trailersRepo = get()
        )
    }

    factory { AuthController() }
}