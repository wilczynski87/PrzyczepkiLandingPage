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

fun loadDotEnv(file: java.io.File): Map<String, String> {
    if (!file.exists()) return emptyMap()
    return file.readLines()
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") }
        .mapNotNull { line ->
            val separator = line.indexOf('=')
            if (separator <= 0) return@mapNotNull null
            val key = line.substring(0, separator).trim()
            val value = line.substring(separator + 1).trim()
            key to value
        }
        .toMap()
}

tasks.named<JavaExec>("run") {
    val dotEnv = loadDotEnv(rootProject.file(".env"))
    dotEnv.forEach { (key, value) ->
        environment(key, value)
    }
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

    // Mongo
    implementation(libs.mongodb.coroutine)

    // datetime
    implementation(libs.kotlinx.datetime)

    // Auth
    implementation(libs.jbcrypt)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)

    // Status page
    implementation(libs.ktor.server.status.pages)


}