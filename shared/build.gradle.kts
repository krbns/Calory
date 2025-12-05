import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.sql.delight.runtime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.viewModel)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        iosMain.dependencies {
            implementation(libs.sql.delight.native.driver)
        }
        jvmMain.dependencies {
            implementation(libs.sql.delight.sqlite.driver)
        }

        androidMain.dependencies {
            implementation(libs.android.driver)
        }
    }
}

android {
    namespace = "com.kurban.calory.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

sqldelight {
    databases {
        create("FoodDatabase") {
            packageName.set("sqldelight.foodScheme.food")
            srcDirs.from("src/commonMain/sqldelight/foodScheme")
        }
        create("TrackedFoodDatabase") {
            packageName.set("sqldelight.trackedFoodScheme.tracked")
            srcDirs.from("src/commonMain/sqldelight/trackedFoodScheme")
        }
    }
}
