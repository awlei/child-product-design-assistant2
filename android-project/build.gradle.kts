// Top-level build file

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.10-1.0.15")
    }
}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.15" apply false
}

// Force Kotlin version across the entire project
allprojects {
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.10")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:1.9.10")
            force("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
            force("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.10")
            force("org.jetbrains.kotlin:kotlin-script-runtime:1.9.10")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
