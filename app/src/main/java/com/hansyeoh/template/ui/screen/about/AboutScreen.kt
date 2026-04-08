package com.hansyeoh.template.ui.screen.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.dropUnlessResumed
import com.hansyeoh.template.BuildConfig
import com.hansyeoh.template.R
import com.hansyeoh.shared.ui.LocalUiMode
import com.hansyeoh.shared.ui.UiMode
import com.hansyeoh.shared.ui.navigation3.LocalNavigator

@Composable
fun AboutScreen() {
    val navigator = LocalNavigator.current
    val state = AboutUiState(
        title = stringResource(R.string.about),
        appName = stringResource(R.string.app_name),
        versionName = BuildConfig.VERSION_NAME,
    )
    val actions = AboutScreenActions(
        onBack = dropUnlessResumed { navigator.pop() },
    )

    when (LocalUiMode.current) {
        UiMode.Miuix -> AboutScreenMiuix(state, actions)
        UiMode.Material -> AboutScreenMaterial(state, actions)
    }
}
