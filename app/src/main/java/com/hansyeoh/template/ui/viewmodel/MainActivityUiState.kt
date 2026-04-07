package com.hansyeoh.template.ui.viewmodel

import androidx.compose.runtime.Immutable
import com.hansyeoh.template.ui.UiMode
import com.hansyeoh.template.ui.theme.AppSettings

@Immutable
data class MainActivityUiState(
    val appSettings: AppSettings,
    val pageScale: Float,
    val enableBlur: Boolean,
    val enableFloatingBottomBar: Boolean,
    val enableFloatingBottomBarBlur: Boolean,
    val uiMode: UiMode,
)
