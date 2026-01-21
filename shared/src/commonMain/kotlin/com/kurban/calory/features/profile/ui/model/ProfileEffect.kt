package com.kurban.calory.features.profile.ui.model

import com.kurban.calory.core.domain.DomainError

sealed class ProfileEffect {
    data class Error(val error: DomainError) : ProfileEffect()
}
