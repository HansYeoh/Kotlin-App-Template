package com.hansyeoh.template.ui.screen.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.ContactPage
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Update
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeSource
import com.hansyeoh.template.R
import com.hansyeoh.shared.ui.UiMode
import com.hansyeoh.template.ui.component.dialog.rememberLoadingDialog
import com.hansyeoh.template.ui.component.miuix.SendLogDialog
import com.hansyeoh.shared.ui.theme.LocalEnableBlur
import com.hansyeoh.shared.ui.util.defaultHazeEffect
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun SettingPagerMiuix(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    bottomInnerPadding: Dp,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val enableBlur = LocalEnableBlur.current
    val hazeState = remember { HazeState() }
    val hazeStyle = if (enableBlur) {
        HazeStyle(
            backgroundColor = colorScheme.surface,
            tint = HazeTint(colorScheme.surface.copy(0.8f))
        )
    } else {
        HazeStyle.Unspecified
    }
    val loadingDialog = rememberLoadingDialog()
    val showSendLogDialog = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = if (enableBlur) {
                    Modifier.defaultHazeEffect(hazeState, hazeStyle)
                } else {
                    Modifier
                },
                color = if (enableBlur) Color.Transparent else colorScheme.surface,
                title = stringResource(R.string.settings),
                scrollBehavior = scrollBehavior
            )
        },
        popupHost = { },
        contentWindowInsets = WindowInsets.systemBars.add(WindowInsets.displayCutout).only(WindowInsetsSides.Horizontal),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .let { if (enableBlur) it.hazeSource(state = hazeState) else it }
                .padding(horizontal = 12.dp),
            contentPadding = innerPadding,
            overscrollEffect = null,
        ) {
            item {
                // Check Update
                Card(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                ) {
                    SuperSwitch(
                        title = stringResource(id = R.string.settings_check_update),
                        summary = stringResource(id = R.string.settings_check_update_summary),
                        startAction = {
                            Icon(
                                Icons.Rounded.Update,
                                modifier = Modifier.padding(end = 6.dp),
                                contentDescription = stringResource(id = R.string.settings_check_update),
                                tint = colorScheme.onBackground
                            )
                        },
                        checked = uiState.checkUpdate,
                        onCheckedChange = actions.onSetCheckUpdate
                    )
                }

                // Page Style + Theme
                Card(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth(),
                ) {
                    SuperDropdown(
                        title = stringResource(id = R.string.settings_ui_mode),
                        summary = stringResource(id = R.string.settings_ui_mode_summary),
                        items = UiMode.entries.map { it.name },
                        startAction = {
                            Icon(
                                Icons.Rounded.Dashboard,
                                modifier = Modifier.padding(end = 6.dp),
                                contentDescription = stringResource(id = R.string.settings_ui_mode),
                                tint = colorScheme.onBackground
                            )
                        },
                        selectedIndex = if (uiState.uiMode == UiMode.Material.value) 1 else 0,
                        onSelectedIndexChange = actions.onSetUiModeIndex
                    )
                    SuperArrow(
                        title = stringResource(id = R.string.settings_theme),
                        summary = stringResource(id = R.string.settings_theme_summary),
                        startAction = {
                            Icon(
                                Icons.Rounded.Palette,
                                modifier = Modifier.padding(end = 6.dp),
                                contentDescription = stringResource(id = R.string.settings_theme),
                                tint = colorScheme.onBackground
                            )
                        },
                        onClick = actions.onOpenTheme
                    )
                }

                // Send Log + About
                Card(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(),
                ) {
                    SuperArrow(
                        title = stringResource(id = R.string.send_log),
                        startAction = {
                            Icon(
                                Icons.Rounded.BugReport,
                                modifier = Modifier.padding(end = 6.dp),
                                contentDescription = stringResource(id = R.string.send_log),
                                tint = colorScheme.onBackground
                            )
                        },
                        onClick = { showSendLogDialog.value = true },
                    )
                    SendLogDialog(
                        show = showSendLogDialog.value,
                        onDismissRequest = { showSendLogDialog.value = false },
                        loadingDialog = loadingDialog
                    )
                    val about = stringResource(id = R.string.about)
                    SuperArrow(
                        title = about,
                        startAction = {
                            Icon(
                                Icons.Rounded.ContactPage,
                                modifier = Modifier.padding(end = 6.dp),
                                contentDescription = about,
                                tint = colorScheme.onBackground
                            )
                        },
                        onClick = actions.onOpenAbout,
                    )
                }
                Spacer(Modifier.height(bottomInnerPadding))
            }
        }
    }
}
