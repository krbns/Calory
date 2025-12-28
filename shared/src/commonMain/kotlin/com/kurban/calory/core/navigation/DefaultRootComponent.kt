package com.kurban.calory.core.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.kurban.calory.core.domain.AppDispatchers
import com.kurban.calory.core.ui.time.DayProvider
import com.kurban.calory.features.customfood.ui.CustomFoodComponent
import com.kurban.calory.features.customfood.ui.CustomFoodDependencies
import com.kurban.calory.features.main.domain.AddTrackedFoodUseCase
import com.kurban.calory.features.main.domain.CalculateTotalsUseCase
import com.kurban.calory.features.main.domain.DeleteTrackedFoodUseCase
import com.kurban.calory.features.main.domain.ObserveTrackedForDayUseCase
import com.kurban.calory.features.main.domain.SearchFoodUseCase
import com.kurban.calory.features.main.ui.MainComponent
import com.kurban.calory.features.main.ui.MainDependencies
import com.kurban.calory.features.onboarding.ui.OnboardingComponent
import com.kurban.calory.features.onboarding.ui.OnboardingDependencies
import com.kurban.calory.features.profile.domain.CalculateMacroTargetsUseCase
import com.kurban.calory.features.profile.domain.NeedsOnboardingUseCase
import com.kurban.calory.features.profile.domain.ObserveUserProfileUseCase
import com.kurban.calory.features.profile.domain.SaveUserProfileUseCase
import com.kurban.calory.features.profile.ui.ProfileComponent
import com.kurban.calory.features.profile.ui.ProfileDependencies
import kotlinx.serialization.Serializable
import org.koin.core.Koin
import kotlinx.coroutines.launch

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val koin: Koin,
) : RootComponent, ComponentContext by componentContext {

    private val scope = componentScope()
    private val mainDependencies by lazy {
        MainDependencies(
            searchFoodUseCase = koin.get<SearchFoodUseCase>(),
            observeTrackedForDayUseCase = koin.get<ObserveTrackedForDayUseCase>(),
            deleteTrackedFoodUseCase = koin.get<DeleteTrackedFoodUseCase>(),
            addTrackedFoodUseCase = koin.get<AddTrackedFoodUseCase>(),
            calculateTotalsUseCase = koin.get<CalculateTotalsUseCase>(),
            calculateMacroTargetsUseCase = koin.get<CalculateMacroTargetsUseCase>(),
            observeUserProfileUseCase = koin.get<ObserveUserProfileUseCase>(),
            dispatchers = koin.get<AppDispatchers>(),
            dayProvider = koin.get<DayProvider>()
        )
    }

    private val profileDependencies by lazy {
        ProfileDependencies(
            getUserProfileUseCase = koin.get(),
            saveUserProfileUseCase = koin.get(),
            dispatchers = koin.get()
        )
    }

    private val customFoodDependencies by lazy {
        CustomFoodDependencies(
            observeCustomFoodsUseCase = koin.get(),
            createCustomFoodUseCase = koin.get(),
            addCustomFoodToDiaryUseCase = koin.get()
        )
    }

    private val onboardingDependencies by lazy {
        OnboardingDependencies(
            saveUserProfileUseCase = koin.get<SaveUserProfileUseCase>(),
            dispatchers = koin.get()
        )
    }

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<Config, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = null,
        initialConfiguration = Config.Loading,
        handleBackButton = true,
        childFactory = ::child,
    )

    init {
        val scope = componentScope()
        scope.launch {
            val needsOnboarding = koin.get<NeedsOnboardingUseCase>()(Unit) ?: false
            if (needsOnboarding) {
                navigation.replaceAll(Config.Onboarding)
            } else {
                navigation.replaceAll(Config.Main)
            }
        }
    }

    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child =
        when (config) {
            Config.Loading -> RootComponent.Child.LoadingChild
            Config.Onboarding -> RootComponent.Child.OnboardingChild(
                OnboardingComponent(
                    componentContext = componentContext,
                    dependencies = onboardingDependencies,
                    onFinished = { navigation.replaceAll(Config.Main) }
                )
            )
            Config.Main -> RootComponent.Child.MainChild(
                MainComponent(
                    componentContext = componentContext,
                    dependencies = mainDependencies,
                    onOpenProfile = { navigation.pushNew(Config.Profile) },
                    onOpenCustomFoods = { navigation.pushNew(Config.CustomFood) },
                )
            )

            Config.Profile -> RootComponent.Child.ProfileChild(
                ProfileComponent(
                    componentContext = componentContext,
                    dependencies = profileDependencies,
                    onBack = { navigation.pop() },
                )
            )

            Config.CustomFood -> RootComponent.Child.CustomFoodChild(
                CustomFoodComponent(
                    componentContext = componentContext,
                    dependencies = customFoodDependencies,
                    onBack = { navigation.pop() },
                )
            )
        }
}

@Serializable
sealed interface Config {
    @Serializable
    object Loading : Config

    @Serializable
    object Onboarding : Config

    @Serializable
    data object Main : Config

    @Serializable
    object Profile : Config

    @Serializable
    object CustomFood : Config
}
