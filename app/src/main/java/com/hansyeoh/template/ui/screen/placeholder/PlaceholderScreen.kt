package com.hansyeoh.template.ui.screen.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import com.hansyeoh.shared.ui.LocalUiMode
import com.hansyeoh.shared.ui.UiMode
import com.hansyeoh.shared.ui.theme.LocalEnableBlur
import com.hansyeoh.shared.ui.util.defaultHazeEffect
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * A generic placeholder pager for tabs that have no content yet.
 * Renders an empty page with a top app bar showing the given title.
 * Supports both Material and Miuix UI modes.
 */
@Composable
fun PlaceholderPager(
    title: String,
    bottomInnerPadding: Dp,
) {
    when (LocalUiMode.current) {
        UiMode.Material -> PlaceholderMaterial(title, bottomInnerPadding)
        UiMode.Miuix -> PlaceholderMiuix(title, bottomInnerPadding)
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PlaceholderMaterial(
    title: String,
    bottomInnerPadding: Dp,
) {
    val scrollBehavior = androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        androidx.compose.material3.rememberTopAppBarState()
    )

    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.LargeFlexibleTopAppBar(
                title = { androidx.compose.material3.Text(title) },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // Empty content â€?ready for your implementation
        }
    }
}

@Composable
private fun PlaceholderMiuix(
    title: String,
    bottomInnerPadding: Dp,
) {
    val scrollBehavior = MiuixScrollBehavior()
    val enableBlur = LocalEnableBlur.current
    val hazeState = remember { HazeState() }
    val hazeStyle = if (enableBlur) {
        HazeStyle(
            backgroundColor = MiuixTheme.colorScheme.surface,
            tint = HazeTint(MiuixTheme.colorScheme.surface.copy(0.8f))
        )
    } else {
        HazeStyle.Unspecified
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = if (enableBlur) {
                    Modifier.defaultHazeEffect(hazeState, hazeStyle)
                } else {
                    Modifier
                },
                color = if (enableBlur) Color.Transparent else MiuixTheme.colorScheme.surface,
                title = title,
                scrollBehavior = scrollBehavior
            )
        },
        popupHost = { },
    ) { _ ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Empty content â€?ready for your implementation
        }
    }
}
