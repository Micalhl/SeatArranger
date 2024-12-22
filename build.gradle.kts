import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "com.mcstarrysky"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    maven("https://repo.tabooproject.org/repository/releases/")
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
//    implementation("androidx.compose.material3:material3:1.1.2")
//    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
//    implementation("io.izzel.taboolib:common:6.0.12-69")
//    implementation("io.izzel.taboolib:common-5:6.0.12-69")
//    implementation("io.izzel.taboolib:module-configuration:6.0.12-69")
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "SeatArranger"
            packageVersion = "1.0.0"
        }
    }
}