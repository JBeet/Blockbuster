import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.mavenPublish)
}

val publishedGroupId = "com.beetstra.blockbuster"
val publishedArtifactId = "blockbuster"
val publishedVersion = "0.1.0-SNAPSHOT"
group = publishedGroupId
version = publishedVersion

repositories {
    google()
    mavenCentral()
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs {
            testTask {
                useMocha()
            }
        }
        browser {
            testTask {
                useMocha()
            }
        }
    }
    androidTarget {
        publishLibraryVariants("release")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    linuxX64()
    mingwX64()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.serialization.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = publishedGroupId
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

mavenPublishing {
    coordinates(publishedGroupId, publishedArtifactId, publishedVersion)
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    pom {
        name.set("Blockbuster")
        description.set("Library to build Kotlin serialization deserializers that use callbacks")
        inceptionYear.set("2023")
        url.set("https://github.com/JBeet/Blockbuster/")
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("JBeet")
                name.set("Jaap Beetstra")
                url.set("https://github.com/JBeet/")
            }
        }
        scm {
            url.set("https://github.com/JBeet/Blockbuster/")
            connection.set("scm:git:git://github.com/JBeet/Blockbuster.git")
            developerConnection.set("scm:git:ssh://git@github.com/JBeet/Blockbuster.git")
        }
    }
}
