import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.plugin.serialization)
}


compose.resources {
    publicResClass = true
    packageOfResClass = "przyczepkilandingpage.shared.generated.resources"
}

kotlin {
    jvmToolchain(21)
    jvm()
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    sourceSets {
        val commonMain by getting
//        webMain.dependencies {
//            implementation(compose.components.resources)
//            implementation(compose.html.core)
//        }

        jsMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.html.core)
            implementation(libs.ktor.client.js)
        }

        wasmJsMain.dependencies {
            implementation(compose.components.resources)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(libs.ktor.client.js)
        }

        commonMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.icons.core)
            implementation(libs.compose.material3.icons.extended)
            implementation(compose.components.resources)
            implementation(libs.compose.material3.window.size)
            implementation(libs.kotlinx.datetime)
            implementation(libs.compose.material)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.ktor.client)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
