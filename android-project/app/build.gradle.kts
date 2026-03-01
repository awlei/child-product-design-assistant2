plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    // 显式禁用 kapt 插件（如果存在）
    // id("org.jetbrains.kotlin.kapt") apply false
}

android {
    namespace = "com.design.assistant"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.design.assistant"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            // 优先级：环境变量 > keystore.properties > 默认值
            val keystoreFile = System.getenv("KEYSTORE_FILE")
            val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
            val keyAlias = System.getenv("KEY_ALIAS")
            val keyPassword = System.getenv("KEY_PASSWORD")

            if (keystoreFile != null && keystorePassword != null && keyAlias != null && keyPassword != null) {
                // 从环境变量读取
                storeFile = file(keystoreFile)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            } else {
                // 从 keystore.properties 读取，或使用默认值
                val keystorePropertiesFile = project.layout.projectDirectory.file("keystore.properties").asFile

                if (keystorePropertiesFile.exists()) {
                    val keystoreProperties = java.util.Properties()
                    keystoreProperties.load(java.io.FileInputStream(keystorePropertiesFile))

                    val storeFilePath = keystoreProperties["storeFile"] as String
                    storeFile = if (storeFilePath.startsWith("/")) {
                        file(storeFilePath)  // 绝对路径
                    } else {
                        project.layout.projectDirectory.file(storeFilePath).asFile  // 相对于 app 目录
                    }
                    storePassword = keystoreProperties["storePassword"] as String
                    this.keyAlias = keystoreProperties["keyAlias"] as String
                    this.keyPassword = keystoreProperties["keyPassword"] as String
                } else {
                    // 默认配置
                    storeFile = project.layout.projectDirectory.file("release-keystore.jks").asFile
                    storePassword = "test123456"
                    this.keyAlias = "test-key"
                    this.keyPassword = "test123456"
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // 禁用 KAPT 任务生成
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KaptTask>().configureEach {
        enabled = false
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Networking - Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Networking - OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // JSON Parsing - Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Image Loading - Coil
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
