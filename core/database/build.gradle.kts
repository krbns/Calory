plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqlDelight)
}

kotlin {
    androidTarget()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CoreDatabase"
            isStatic = true
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(libs.sql.delight.runtime)
            api(libs.sql.delight.coroutines)
            api(libs.kotlinx.coroutines.core)
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
    namespace = "com.kurban.calory.core.database"
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
        create("BarcodeProductDatabase") {
            packageName.set("sqldelight.barcodeProductScheme.barcode")
            srcDirs.from("src/commonMain/sqldelight/barcodeProductScheme")
        }
    }
}
