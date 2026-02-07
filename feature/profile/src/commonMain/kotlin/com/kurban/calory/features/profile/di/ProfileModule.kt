package com.kurban.calory.features.profile.di

import com.kurban.calory.core.di.USER_PROFILE_DATABASE_DRIVER
import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.features.profile.data.DefaultUserProfileRepository
import com.kurban.calory.features.profile.data.UserProfileDataSource
import com.kurban.calory.features.profile.data.local.LocalUserProfileDataSource
import com.kurban.calory.features.profile.domain.CalculateMacroTargetsUseCase
import com.kurban.calory.features.profile.domain.GetUserProfileUseCase
import com.kurban.calory.features.profile.domain.NeedsOnboardingUseCase
import com.kurban.calory.features.profile.domain.ObserveUserProfileUseCase
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.domain.UserProfileRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module
import sqldelight.userProfileScheme.profile.UserProfileDatabase

val featureProfileModule = module {
    single<UserProfileDataSource> {
        LocalUserProfileDataSource(get<UserProfileDatabase>(), get(named(USER_PROFILE_DATABASE_DRIVER)))
    }
    single<UserProfileRepository> { DefaultUserProfileRepository(get()) }

    factory { CalculateMacroTargetsUseCase() }
    factory { GetUserProfileUseCase(get(), get<AppDispatchers>().io) }
    factory { SaveUserProfileUseCase(get(), get<AppDispatchers>().io) }
    factory { ObserveUserProfileUseCase(get(), get<AppDispatchers>().io) }
    factory { NeedsOnboardingUseCase(get(), get<AppDispatchers>().io) }
}
