package com.kurban.calory.core.di

import com.kurban.calory.core.data.db.DatabaseDriverFactory
import com.kurban.calory.core.data.db.DriverContext
import com.kurban.calory.features.customfood.domain.CustomFoodRepository
import com.kurban.calory.features.main.domain.FoodRepository
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import com.kurban.calory.features.main.domain.TrackedFoodRepository
import com.kurban.calory.features.profile.domain.UserProfileRepository
import com.kurban.calory.features.profile.domain.NeedsOnboardingUseCase
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.Test

class KoinModulesTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun modulesStartAndResolveDependencies() {
        val driverFactory = DatabaseDriverFactory(DriverContext())

        val koinApp = startKoin {
            modules(appModules(driverFactory))
        }

        val koin = koinApp.koin

        // Resolve a few representative beans to catch missing bindings.
        koin.get<FoodRepository>()
        koin.get<TrackedFoodRepository>()
        koin.get<UserProfileRepository>()
        koin.get<CustomFoodRepository>()
        koin.get<SearchFoodUseCase>()
        koin.get<NeedsOnboardingUseCase>()
    }
}
