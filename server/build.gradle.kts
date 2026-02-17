plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    application
}

group = "com.example.przyczepki_landingpage"
version = "1.0.0"
application {
    mainClass.set("com.example.przyczepki_landingpage.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)

//    KOIN
    implementation(libs.koin.core)
    implementation(libs.koin.server)
    implementation(libs.koin.logger)

    // KOIN TEST
    implementation(libs.koin.test)
    implementation(libs.koin.test.junit5)

    // KTOR KLIENT
    implementation(libs.ktor.client)
    implementation(libs.ktor.client.cio)

    // SERIALISATION
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.content.negotiation.jvm)

    // Cors
    implementation(libs.ktor.server.cors)


}