package com.hansyeoh.template.ui.navigation3

import android.content.Intent

/**
 * Deep link resolution: maps external Intent/Uri to an initial back stack.
 * Currently no deep links are supported in the template project.
 */
object DeepLinkResolver {
    fun resolve(intent: Intent?): List<Route> {
        return emptyList()
    }
}
