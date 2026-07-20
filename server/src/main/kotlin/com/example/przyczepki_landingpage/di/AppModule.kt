package com.example.przyczepki_landingpage.di

import com.example.przyczepki_landingpage.modules.ApiConfig
import com.example.przyczepki_landingpage.modules.toApiConfig
import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.example.przyczepki_landingpage.repo.GateEventRepo
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.repo.PendingPaymentRepo
import com.example.przyczepki_landingpage.repo.impl.CustomerRepoImpl
import com.example.przyczepki_landingpage.repo.impl.CustomerTable
import com.example.przyczepki_landingpage.repo.impl.GateEventRepoImpl
import com.example.przyczepki_landingpage.repo.impl.GateEventTable
import com.example.przyczepki_landingpage.repo.impl.PendingPaymentRepoImpl
import com.example.przyczepki_landingpage.repo.impl.PendingPaymentTable
import com.example.przyczepki_landingpage.repo.impl.ReservationRepoImpl
import com.example.przyczepki_landingpage.repo.impl.ReservationTable
import com.example.przyczepki_landingpage.repo.impl.SuplaTokenRepoImpl
import com.example.przyczepki_landingpage.repo.impl.SuplaTokenTable
import com.example.przyczepki_landingpage.repo.impl.TrailerTable
import com.example.przyczepki_landingpage.repo.impl.TrailersRepoImpl
import com.example.przyczepki_landingpage.repo.SuplaTokenRepo
import com.example.przyczepki_landingpage.service.CustomerService
import com.example.przyczepki_landingpage.service.EmailService
import com.example.przyczepki_landingpage.service.GateService
import com.example.przyczepki_landingpage.service.PaymentService
import com.example.przyczepki_landingpage.service.ReservationConfirmationService
import com.example.przyczepki_landingpage.service.ReservationService
import com.example.przyczepki_landingpage.service.SuplaTokenProvider
import com.example.przyczepki_landingpage.service.TrailersService
import com.example.przyczepki_landingpage.service.auth.JwtService
import com.example.przyczepki_landingpage.service.impl.CustomerServiceImpl
import com.example.przyczepki_landingpage.service.impl.EmailServiceImpl
import com.example.przyczepki_landingpage.service.impl.GateServiceImpl
import com.example.przyczepki_landingpage.service.impl.PaymentServiceImpl
import com.example.przyczepki_landingpage.service.impl.ReservationConfirmationServiceImpl
import com.example.przyczepki_landingpage.service.impl.ReservationServiceImpl
import com.example.przyczepki_landingpage.service.impl.SuplaTokenProviderImpl
import com.example.przyczepki_landingpage.service.impl.TrailersServiceImpl
import io.ktor.client.HttpClient
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.koin.core.qualifier.named
import org.koin.dsl.module

// KOIN
val appModule = module {
    single<EmailService> {
        val authConfig = get<ApiConfig>().auth
        val cfgEmail = get<ApiConfig>().email
        EmailServiceImpl(
            client = get<HttpClient>(),
            cfgEmail = cfgEmail,
            cfgAuth = authConfig,
        )
    }

    single<ReservationConfirmationService> {
        ReservationConfirmationServiceImpl(
            reservationRepo = get(),
            emailService = get(),
        )
    }

    single<PaymentService> {
        PaymentServiceImpl(
            client = get<HttpClient>(),
            paymentConfig = get<ApiConfig>().paymentConfig,
            pendingPaymentRepo = get(),
            reservationService = get(),
            reservationRepo = get(),
            reservationConfirmationService = get(),
        )
    }

    single<ApiConfig> {
        toApiConfig()
    }

    single {
        val authConfig = get<ApiConfig>().auth
        JwtService(authConfig)
    }

    single<MongoClient> {
        val cfg = get<ApiConfig>().db
        val connectionString = cfg.toConnectionString()

        MongoClient.create(connectionString)
    }
    single<MongoDatabase> {
        val cfg = get<ApiConfig>().db
        get<MongoClient>().getDatabase(cfg.name)
    }

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
    single(named("pendingPaymentCollection")) {
        get<MongoDatabase>().getCollection<PendingPaymentTable>("pending_payment")
    }
    single(named("gateEventCollection")) {
        get<MongoDatabase>().getCollection<GateEventTable>("gate_event")
    }
    single(named("suplaTokenCollection")) {
        get<MongoDatabase>().getCollection<SuplaTokenTable>("supla_token")
    }

    // Repositories
    single<TrailersRepo> { TrailersRepoImpl(get(named("trailerCollection"))) }
    single<ReservationRepo> { ReservationRepoImpl(get(named("reservationCollection"))) }
    single<CustomerRepo> { CustomerRepoImpl(get(named("customerCollection"))) }
    single<PendingPaymentRepo> { PendingPaymentRepoImpl(get(named("pendingPaymentCollection"))) }
    single<GateEventRepo> { GateEventRepoImpl(get(named("gateEventCollection"))) }
    single<SuplaTokenRepo> { SuplaTokenRepoImpl(get(named("suplaTokenCollection"))) }


    // Services
    single<TrailersService> {
        TrailersServiceImpl(
            trailersRepo = get()
        )
    }

    single<ReservationService> {
        ReservationServiceImpl(
            reservationRepo = get(),
            trailersRepo = get(),
            customerRepo = get(),
        )
    }

    single<CustomerService> {
        CustomerServiceImpl(
            apiBaseUrl = "https://${get<ApiConfig>().apiHost}:${get<ApiConfig>().apiPort}/",
            customerRepo = get()
        )
    }

    single<SuplaTokenProvider> {
        SuplaTokenProviderImpl(
            client = get<HttpClient>(),
            gateConfig = get<ApiConfig>().gateConfig,
            tokenRepo = get(),
        )
    }

    single<GateService> {
        GateServiceImpl(
            client = get<HttpClient>(),
            gateConfig = get<ApiConfig>().gateConfig,
            reservationRepo = get(),
            gateEventRepo = get(),
            suplaTokenProvider = get(),
        )
    }

//    factory { AuthService() }
}