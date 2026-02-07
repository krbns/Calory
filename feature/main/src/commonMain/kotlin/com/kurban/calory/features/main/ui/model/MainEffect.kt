package com.kurban.calory.features.main.ui.model

import com.kurban.calory.core.domain.DomainError

sealed class MainEffect {
    data class Error(val error: DomainError) : MainEffect()
}
