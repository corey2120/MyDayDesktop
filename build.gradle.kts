plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("org.jetbrains.compose") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

group = "com.cobrien.myday"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.pkg.jetbrains.space/public/p/androidx/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    
    // JSON for data persistence
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Coroutines with Desktop dispatcher
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
}

compose.desktop {
    application {
        mainClass = "com.cobrien.myday.MainKt"
        
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Rpm
            )
            
            packageName = "MyDay"
            packageVersion = "1.0.0"
            description = "A task management application that combines Tasks, Calendar and Notes"
            copyright = "© 2024 Your Name. All rights reserved."
            vendor = "cobrien"
            
            linux {
                iconFile.set(project.file("src/main/resources/icon.png"))
                packageName = "com.cobrien.myday"
                debMaintainer = "cobrien@example.com"
                menuGroup = "Office"
                appCategory = "Office"
            }
        }
    }
}
