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
            implementation(libs.sql.delight.coroutines)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.viewModel)
            implementation(libs.kotlinx.datetime)
            implementation(libs.decompose)
            implementation(libs.decompose.extensions.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }

        iosMain.dependencies {
            implementation(libs.sql.delight.native.driver)
        }
        jvmMain.dependencies {
            implementation(libs.sql.delight.sqlite.driver)
        }
        jvmTest.dependencies {
            implementation(libs.koin.test)
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
        create("UserProfileDatabase") {
            packageName.set("sqldelight.userProfileScheme.profile")
            srcDirs.from("src/commonMain/sqldelight/userProfileScheme")
        }
        create("CustomFoodDatabase") {
            packageName.set("sqldelight.customFoodScheme.custom")
            srcDirs.from("src/commonMain/sqldelight/customFoodScheme")
        }
    }
}
