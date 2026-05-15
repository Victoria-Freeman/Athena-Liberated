
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.kin.athena"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kin.athena"
        minSdk = 24
        targetSdk = 36
        versionCode = 307
        versionName = "1.9.0"

        vectorDrawables {
            useSupportLibrary = true
        }
        androidResources {
            localeFilters += listOf("en","ca","de","es","fr","pl")
        }
        externalNativeBuild {
            cmake {
                cppFlags += ""
                abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            }
        }
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
        aidl = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/LICENSE.md",
                "/META-INF/README.md",
                "/META-INF/DEPENDENCIES",
                "/META-INF/INDEX.LIST"
            )
        }
    }
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs/")
        }
    }
    
    tasks.register<Exec>("buildGoLibraries") {
        group = "build"
        description = "Build Go libraries for Android"
        
        val goDir = layout.projectDirectory.dir("src/main/go")
        val libsDir = layout.projectDirectory.dir("libs")
        
        inputs.dir(goDir)
        outputs.files(
            libsDir.file("arm64-v8a/libnflog.so"),
            libsDir.file("armeabi-v7a/libnflog.so")
        )
        outputs.upToDateWhen { false }
        
        workingDir(goDir)
        
        val isWindows = org.gradle.internal.os.OperatingSystem.current().isWindows
        val buildScript = if (isWindows) "build-android.bat" else "build-android.sh"
        
        if (isWindows) {
            commandLine("cmd", "/c", buildScript)
        } else {
            commandLine("bash", buildScript)
        }
        
        doFirst {
            val buildScriptFile = goDir.file(buildScript).asFile
            if (!buildScriptFile.exists()) {
                throw GradleException("Build script $buildScript not found in ${goDir.asFile}")
            }
            
            libsDir.dir("arm64-v8a").asFile.mkdirs()
            libsDir.dir("armeabi-v7a").asFile.mkdirs()
        }
    }
    ksp {
        arg("dagger.fastInit", "enabled")
        arg("room.schemaLocation", "$projectDir/schemas")
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    packaging {
        jniLibs.useLegacyPackaging = true
    }
}

dependencies {
    implementation(libs.m3color)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.common)
    ksp(libs.hilt.android.compiler)
    ksp(libs.hilt.compile)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Compose
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.splashscreen)

    // AndroidX
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.datastore.preferences)

    // OkHttp (for custom blocklist downloads)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Notification
    implementation(libs.androidx.core)

    //Fingerprint
    implementation(libs.androidx.biometric)

    // DNS Blocking
    implementation(libs.atomicfu)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.dnsjava)
    implementation(libs.pcap4j.core)

    // Shizuku
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
}
