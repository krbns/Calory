package com.kurban.calory.features.profile.ui

import com.arkivanov.decompose.ComponentContext

class ProfileComponent(
    val componentContext: ComponentContext,
    val onBack: () -> Unit,
) : ComponentContext by componentContext {

}