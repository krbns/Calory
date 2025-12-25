package com.kurban.calory.core.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.kurban.calory.features.customfood.ui.CustomFoodComponent
import com.kurban.calory.features.main.ui.MainComponent
import com.kurban.calory.features.profile.ui.ProfileComponent
import kotlinx.serialization.Serializable

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<Config, RootComponent.Child>> = childStack(
        source = navigation,
        serializer = null,
        initialConfiguration = Config.Main,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child =
        when (config) {
            Config.Main -> RootComponent.Child.MainChild(
                MainComponent(
                    componentContext = componentContext,
                    onOpenProfile = { navigation.pushNew(Config.Profile) },
                    onOpenCustomFoods = { navigation.pushNew(Config.CustomFood) },
                )
            )

            Config.Profile -> RootComponent.Child.ProfileChild(
                ProfileComponent(
                    componentContext = componentContext,
                    onBack = { navigation.pop() },
                )
            )

            Config.CustomFood -> RootComponent.Child.CustomFoodChild(
                CustomFoodComponent(
                    componentContext = componentContext,
                    onBack = { navigation.pop() },
                )
            )
        }
}

@Serializable
sealed interface Config {
    @Serializable
    data object Main : Config

    @Serializable
    object Profile : Config

    @Serializable
    object CustomFood : Config
}

