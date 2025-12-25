package com.kurban.calory

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.kurban.calory.core.navigation.RootComponent
import com.kurban.calory.core.theme.CaloryTheme
import com.kurban.calory.features.customfood.CustomFoodScreen
import com.kurban.calory.features.main.MainScreen
import com.kurban.calory.features.profile.ProfileScreen

@Composable
fun AppRoot(component: RootComponent, modifier: Modifier = Modifier) {
    CaloryTheme {
        Children(
            stack = component.stack,
            modifier = modifier,
            animation = stackAnimation(slide()),
        ) {
            when (val child = it.instance) {
                is RootComponent.Child.MainChild -> MainScreen(component = child.component)
                is RootComponent.Child.CustomFoodChild -> CustomFoodScreen(component = child.component)
                is RootComponent.Child.ProfileChild -> ProfileScreen(child.component)
            }
        }
    }
}
