import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.7.20"
    id("org.jetbrains.compose")
}

group = "net.lifeupapp"
version = "0.1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.materialIconsExtended)
                implementation(compose.uiTooling)
                // debugImplementation(compose.preview)

                // kotlinx-coroutines-swing
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.5.2")

                // TODO: md3 migration
                // implementation(compose.material3)
                // define a BOM and its version
                implementation("com.squareup.okhttp3:okhttp:4.10.0")
                implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("org.jmdns:jmdns:3.5.8")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            // https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution#configuring-included-jdk-modules
            modules("java.instrument", "java.prefs", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "LifeUp Desktop"
            packageVersion = "1.1.1"
            macOS {
                iconFile.set(project.file("icon.icns"))
            }
            windows {
                iconFile.set(project.file("icon.ico"))
                dirChooser = true // enables customizing the installation path during installation
                // console = true
                shortcut = true
                perUserInstall = true
                upgradeUuid = "6400cdde-3cb6-4bad-b238-70b02cc8d210"
                menuGroup = "LifeUp Desktop"
            }
            linux {
                iconFile.set(project.file("icon.png"))
            }
        }
        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}
