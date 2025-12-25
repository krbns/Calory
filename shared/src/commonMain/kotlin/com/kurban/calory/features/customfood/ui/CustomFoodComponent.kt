package com.kurban.calory.features.customfood.ui

import com.arkivanov.decompose.ComponentContext

class CustomFoodComponent(
    val componentContext: ComponentContext,
    val onBack: () -> Unit,
) : ComponentContext by componentContext {

}