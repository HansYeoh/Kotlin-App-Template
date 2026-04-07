package com.hansyeoh.template.ui.util

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import com.hansyeoh.template.templateApp
import okhttp3.Request

/**
 * Data class for latest version info from GitHub releases.
 */
data class LatestVersionInfo(
    val versionCode: Int = 0,
    val downloadUrl: String = "",
    val changelog: String = ""
)

/**
 * @author weishu
 * @date 2023/6/22.
 */
fun download(
    url: String,
    fileName: String,
    onDownloaded: (Uri) -> Unit = {},
    onDownloading: () -> Unit = {},
    onProgress: (Int) -> Unit = {}
) {
    onDownloading()

    val downloadId = DownloadManager.enqueue(
        context = templateApp,
        url = url,
        fileName = fileName,
        onCompleted = onDownloaded,
    )

    CoroutineScope(Dispatchers.Main).launch {
        DownloadManager.downloads.collect { map ->
            val state = map[downloadId] ?: return@collect
            onProgress(state.progress)
            if (state.status == DownloadManager.Status.COMPLETED ||
                state.status == DownloadManager.Status.FAILED
            ) {
                cancel()
            }
        }
    }
}

fun checkNewVersion(): LatestVersionInfo {
    if (!isNetworkAvailable(templateApp)) return LatestVersionInfo()
    val url = "https://api.github.com/repos/tiann/KernelSU/releases/latest"
    val defaultValue = LatestVersionInfo()
    runCatching {
        templateApp.okhttpClient.newCall(Request.Builder().url(url).build()).execute()
            .use { response ->
                if (!response.isSuccessful) {
                    return defaultValue
                }
                val body = response.body.string()
                val json = org.json.JSONObject(body)
                val changelog = json.optString("body")

                val assets = json.getJSONArray("assets")
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.getString("name")
                    if (!name.endsWith(".apk")) {
                        continue
                    }

                    val regex = Regex("v(.+?)_(\\d+)-")
                    val matchResult = regex.find(name) ?: continue
                    matchResult.groupValues[1]
                    val versionCode = matchResult.groupValues[2].toInt()
                    val downloadUrl = asset.getString("browser_download_url")

                    return LatestVersionInfo(
                        versionCode,
                        downloadUrl,
                        changelog
                    )
                }

            }
    }
    return defaultValue
}
