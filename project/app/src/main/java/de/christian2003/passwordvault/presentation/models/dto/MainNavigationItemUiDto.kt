package de.christian2003.passwordvault.presentation.models.dto

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes


internal data class MainNavigationItemUiDto(
    @param:StringRes val label: Int,
    @param:DrawableRes val icon: Int,
    @param:DrawableRes val selectedIcon: Int
)
