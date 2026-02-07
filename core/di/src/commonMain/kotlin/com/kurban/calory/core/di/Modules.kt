package com.kurban.calory.core.di

import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.features.barcode.di.featureBarcodeModule
import com.kurban.calory.features.customfood.di.featureCustomFoodModule
import com.kurban.calory.features.main.di.featureMainModule
import com.kurban.calory.features.onboarding.di.featureOnboardingModule
import com.kurban.calory.features.profile.di.featureProfileModule
import org.koin.core.module.Module

fun appModules(driverFactory: DatabaseDriverFactory): List<Module> = listOf(
    coreCommonModule,
    coreDatabaseModule(driverFactory),
    featureProfileModule,
    featureMainModule,
    featureOnboardingModule,
    featureCustomFoodModule,
    featureBarcodeModule
)
