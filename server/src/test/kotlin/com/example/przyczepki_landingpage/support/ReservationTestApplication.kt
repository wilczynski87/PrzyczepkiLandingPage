package com.example.przyczepki_landingpage.support

import com.example.przyczepki_landingpage.controller.reservation
import com.example.przyczepki_landingpage.modules.configureSecurity
import com.example.przyczepki_landingpage.modules.configureStatusPages
import com.example.przyczepki_landingpage.repo.CouponRepo
import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.service.CouponService
import com.example.przyczepki_landingpage.service.CustomerService
import com.example.przyczepki_landingpage.service.ReservationService
import com.example.przyczepki_landingpage.service.TrailersService
import com.example.przyczepki_landingpage.service.impl.CouponServiceImpl
import com.example.przyczepki_landingpage.service.impl.ReservationServiceImpl
import com.example.przyczepki_landingpage.service.impl.TrailersServiceImpl
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

data class ReservationTestDependencies(
    val reservationRepo: FakeReservationRepo,
    val trailersRepo: FakeTrailersRepo,
    val customerRepo: FakeCustomerRepo,
)

fun Application.installReservationCheckTestDependencies(
    reservationRepo: FakeReservationRepo = FakeReservationRepo(),
    trailersRepo: FakeTrailersRepo = FakeTrailersRepo(),
    customerRepo: FakeCustomerRepo = FakeCustomerRepo(),
): ReservationTestDependencies {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = false
            },
        )
    }
    configureStatusPages()
    install(Koin) {
        modules(
            module {
                single { testApiConfig }
                single<ReservationRepo> { reservationRepo }
                single<TrailersRepo> { trailersRepo }
                single<CustomerRepo> { customerRepo }
                single<CouponRepo> { FakeCouponRepo() }
                single<TrailersService> { TrailersServiceImpl(get()) }
                single<CouponService> { CouponServiceImpl(get()) }
                single<ReservationService> { ReservationServiceImpl(get(), get(), get(), get()) }
                single<CustomerService> { FakeCustomerService() }
            },
        )
    }
    configureSecurity()
    routing {
        reservation()
    }
    return ReservationTestDependencies(reservationRepo, trailersRepo, customerRepo)
}
