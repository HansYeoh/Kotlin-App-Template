package com.hansyeoh.template.ui.util

import android.content.Context
import android.os.Build
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.util.zip.GZIPOutputStream

/**
 * Generates a basic bugreport file containing device info and logcat output.
 * This is a simplified version for the template project.
 */
fun getBugreportFile(context: Context): File {
    val bugreportDir = File(context.cacheDir, "bugreport")
    bugreportDir.mkdirs()
    val bugreportFile = File(bugreportDir, "bugreport.tar.gz")

    GZIPOutputStream(BufferedOutputStream(FileOutputStream(bugreportFile))).use { gzOut ->
        PrintWriter(gzOut).use { writer ->
            writer.println("=== Device Info ===")
            writer.println("Manufacturer: ${Build.MANUFACTURER}")
            writer.println("Model: ${Build.MODEL}")
            writer.println("Device: ${Build.DEVICE}")
            writer.println("Android Version: ${Build.VERSION.RELEASE}")
            writer.println("SDK: ${Build.VERSION.SDK_INT}")
            writer.println("Build: ${Build.DISPLAY}")
            writer.println()
            writer.println("=== Logcat ===")
            writer.flush()

            try {
                val process = Runtime.getRuntime().exec(arrayOf("logcat", "-d", "-t", "1000"))
                process.inputStream.bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        writer.println(line)
                    }
                }
                process.waitFor()
            } catch (e: Exception) {
                writer.println("Failed to capture logcat: ${e.message}")
            }

            writer.flush()
        }
    }

    return bugreportFile
}
