import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}


compose.resources {
    publicResClass = true
    packageOfResClass = "przyczepkilandingpage.shared.generated.resources"
}

kotlin {
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
        
//         Definiujemy webMain i ustawiamy zależność od commonMain
//        val webMain by creating {
//            dependsOn(commonMain)
//        }
//
//        val jsMain by getting {
//            dependsOn(webMain)
//        }
//
//        val wasmJsMain by getting {
//            dependsOn(webMain)
//        }
        webMain.dependencies {
            implementation(compose.components.resources)
        }

        commonMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.compose.material3.icons.core)
            implementation(libs.compose.material3.icons.extended)
            implementation(compose.components.resources)
            implementation(libs.compose.material3.window.size)
            implementation(libs.kotlinx.datetime)
            implementation(libs.compose.material)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
