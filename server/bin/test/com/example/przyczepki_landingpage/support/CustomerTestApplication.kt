package com.example.przyczepki_landingpage.support

import com.example.przyczepki_landingpage.controller.adminController
import com.example.przyczepki_landingpage.controller.customerController
import com.example.przyczepki_landingpage.controller.reservation
import com.example.przyczepki_landingpage.modules.ApiConfig
import com.example.przyczepki_landingpage.modules.AuthConfig
import com.example.przyczepki_landingpage.modules.DbConfig
import com.example.przyczepki_landingpage.modules.EmailConfig
import com.example.przyczepki_landingpage.modules.GateConfig
import com.example.przyczepki_landingpage.modules.PaymentConfig
import com.example.przyczepki_landingpage.modules.configureSecurity
import com.example.przyczepki_landingpage.modules.configureStatusPages
import com.example.przyczepki_landingpage.controller.couponController
import com.example.przyczepki_landingpage.repo.CouponRepo
import com.example.przyczepki_landingpage.repo.CustomerRepo
import com.example.przyczepki_landingpage.repo.ReservationRepo
import com.example.przyczepki_landingpage.repo.TrailersRepo
import com.example.przyczepki_landingpage.service.CouponService
import com.example.przyczepki_landingpage.service.CustomerService
import com.example.przyczepki_landingpage.service.EmailService
import com.example.przyczepki_landingpage.service.ReservationService
import com.example.przyczepki_landingpage.service.TrailersService
import com.example.przyczepki_landingpage.service.impl.CouponServiceImpl
import com.example.przyczepki_landingpage.service.impl.CustomerServiceImpl
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

val testAuthConfig = AuthConfig(
    secretAuth = "test-auth-secret",
    secretRefresh = "test-refresh-secret",
    issuer = "przyczepki-test",
    audience = "przyczepki-test-audience",
    realm = "przyczepki-test",
    claim = "userId",
    internalApiKey = "test-internal",
)

internal val testApiConfig = ApiConfig(
    env = "TEST",
    apiPort = 8090,
    apiHost = "localhost",
    email = EmailConfig(host = "localhost", port = 8091),
    db = DbConfig(
        host = "localhost",
        port = 27017,
        name = "test",
        user = "user",
        password = "password",
        authSource = "admin",
    ),
    auth = testAuthConfig,
    paymentConfig = PaymentConfig(
        merchantId = 0,
        posId = 0,
        secretId = "secret",
        crc = "crc",
        urlReturn = "http://localhost/return",
        urlStatus = "http://localhost/status",
        apiBaseUrl = "http://localhost/api",
        redirectBaseUrl = "http://localhost/redirect/",
        mockMode = true,
    ),
    gateConfig = GateConfig(openUrl = "", mockMode = true),
)

fun Application.installCustomerTestDependencies(
    customerRepo: FakeCustomerRepo = FakeCustomerRepo(),
): FakeCustomerRepo {
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
                single<CustomerRepo> { customerRepo }
                single<CustomerService> { CustomerServiceImpl("http://localhost/", get()) }
                single<EmailService> { FakeEmailService() }
            },
        )
    }
    configureSecurity()
    routing {
        customerController()
    }
    return customerRepo
}

fun Application.installAdminTestDependencies(
    customerRepo: FakeCustomerRepo = FakeCustomerRepo(),
    reservationRepo: FakeReservationRepo = FakeReservationRepo(),
    trailersRepo: FakeTrailersRepo = FakeTrailersRepo(),
    couponRepo: FakeCouponRepo = FakeCouponRepo(),
): AdminTestDependencies {
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
                single<CustomerRepo> { customerRepo }
                single<ReservationRepo> { reservationRepo }
                single<TrailersRepo> { trailersRepo }
                single<CouponRepo> { couponRepo }
                single<CustomerService> { CustomerServiceImpl("http://localhost/", get()) }
                single<TrailersService> { TrailersServiceImpl(get()) }
                single<CouponService> { CouponServiceImpl(get()) }
                single<ReservationService> { ReservationServiceImpl(get(), get(), get(), get()) }
            },
        )
    }
    configureSecurity()
    routing {
        adminController()
        couponController()
    }
    return AdminTestDependencies(customerRepo, reservationRepo, trailersRepo, couponRepo)
}

data class AdminTestDependencies(
    val customerRepo: FakeCustomerRepo,
    val reservationRepo: FakeReservationRepo,
    val trailersRepo: FakeTrailersRepo,
    val couponRepo: FakeCouponRepo,
)

fun Application.installMyReservationsTestDependencies(
    reservationRepo: FakeReservationRepo = FakeReservationRepo(),
): FakeReservationRepo {
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
                single<TrailersRepo> { FakeTrailersRepo() }
                single<CustomerRepo> { FakeCustomerRepo() }
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
    return reservationRepo
}
