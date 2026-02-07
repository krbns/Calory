package com.kurban.calory.core.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.kurban.calory.features.barcode.ui.BarcodeScannerComponent
import com.kurban.calory.features.customfood.ui.CustomFoodComponent
import com.kurban.calory.features.main.ui.MainComponent
import com.kurban.calory.features.onboarding.ui.OnboardingComponent
import com.kurban.calory.features.profile.ui.ProfileComponent

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        object LoadingChild : Child()
        data class OnboardingChild(val component: OnboardingComponent) : Child()
        data class MainChild(val component: MainComponent) : Child()
        data class ProfileChild(val component: ProfileComponent) : Child()
        data class CustomFoodChild(val component: CustomFoodComponent) : Child()
        data class BarcodeScannerChild(val component: BarcodeScannerComponent) : Child()
    }
}
