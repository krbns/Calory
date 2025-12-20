package com.kurban.calory.features.profile.ui.model

sealed class ProfileEffect {
    data class Error(val message: String) : ProfileEffect()
}
