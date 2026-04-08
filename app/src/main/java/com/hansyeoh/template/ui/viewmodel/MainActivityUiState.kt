package com.hansyeoh.template.ui.viewmodel

import androidx.compose.runtime.Immutable
import com.hansyeoh.shared.ui.UiMode
import com.hansyeoh.shared.ui.theme.AppSettings

@Immutable
data class MainActivityUiState(
    val appSettings: AppSettings,
    val pageScale: Float,
    val enableBlur: Boolean,
    val enableFloatingBottomBar: Boolean,
    val enableFloatingBottomBarBlur: Boolean,
    val uiMode: UiMode,
)
