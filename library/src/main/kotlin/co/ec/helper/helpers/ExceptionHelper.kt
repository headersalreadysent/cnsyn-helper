package co.ec.helper.helpers

import co.ec.helper.utils.dateString
import co.ec.helper.utils.timeString
import android.content.Context
import co.ec.helper.CnsynApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter


class ExceptionHelper(private val context: Context) : Thread.UncaughtExceptionHandler {


    private val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    @OptIn(DelicateCoroutinesApi::class)
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // Log the exception
        LogHelper.e("Uncaught exception in thread ${thread.name}: ${throwable.message}", throwable )

        // Record the exception to a file
        recordExceptionToFile(throwable)
        val settings = SettingsHelper(context)

        val restartCount = settings.getInt("appRestartAfterError", 0)
        if (restartCount < 3) {
            //try only 3 time
            LogHelper.w("try to restart for $restartCount", "exception")
            GlobalScope.launch {
                delay(2000L)
                settings.putInt("appRestartAfterError", restartCount + 1)
            }
        } else {
            // Pass the exception to the default handler (optional)
            defaultExceptionHandler?.uncaughtException(thread, throwable)
        }

    }


    companion object {
        private const val APP_LOG_FILE = "application_log"
        private val CRASH_FILE = "crash_report_"

        /**
         * record exception to file
         */
        fun recordExceptionToFile(throwable: Throwable) {
            val logDir = getLogDirectory()
            val fileName = "$CRASH_FILE${System.currentTimeMillis() / 1000L}"
            val file = File(logDir, fileName)
            FileOutputStream(file).use { fos ->
                PrintWriter(fos).use { writer ->
                    val sw = StringWriter()
                    val pw = PrintWriter(sw)
                    throwable.printStackTrace(pw)
                    writer.println(sw.toString())
                }
            }
        }

        fun appendLog(message: String) {
            try {
                val logFile = File(CnsynApp.context().filesDir, APP_LOG_FILE)
                val writer = FileWriter(logFile, true)
                writer.append(message)
                writer.append("\n")
                writer.flush()
                writer.close()
            } catch (e: IOException) {
                LogHelper.e("Error writing log to file: ${e.message}", e)
            }
        }

        private fun getLogDirectory(): File {
            val logDir = File(CnsynApp.context().filesDir, "crash")
            if (!logDir.exists()) {
                //if not exists make it valid
                logDir.mkdirs()
            }
            return logDir
        }


        /**
         * read logs
         */
        fun readLogs(): List<String> {
            val logFile = File(CnsynApp.context().filesDir, APP_LOG_FILE)
            val maxFileSize = 1 * 1024 * 1024 // 1 MB

            if (!logFile.exists()) return emptyList()
            // Check if the file exceeds 1 MB and trim if necessary
            if (logFile.length() > maxFileSize) {
                trimLogFile(logFile)
            }
            // Read the file in reverse
            val reversedLogs = mutableListOf<String>()
            logFile.readLines().asReversed().forEach { line ->
                reversedLogs.add(line)
            }
            return reversedLogs
        }

        /**
         * trim log file to size
         */
        private fun trimLogFile(logFile: File) {
            try {
                val lines = logFile.readLines()
                val halfSize = lines.size / 2
                val remainingLines = lines.subList(halfSize, lines.size)
                // Rewrite the file with the remaining lines
                logFile.writeText(remainingLines.joinToString("\n"))
            } catch (e: IOException) {
                LogHelper.e("Error trimming log file: ${e.message}", e)
            }
        }

        /**
         * clear log file
         */
        fun clearLogs() {
            val logFile = File(CnsynApp.context().filesDir, APP_LOG_FILE)
            logFile.writeText("")
        }

        /**
         * read crash logs
         */
        fun readCrashLogs(): List<Pair<String, String>> {
            val logs = mutableMapOf<String, String>()
            val logDir = getLogDirectory()
            val logFiles = logDir.listFiles { _, name -> name.startsWith(CRASH_FILE) }
                ?.sortedByDescending { it.name } ?: listOf()

            logFiles.forEach { file ->
                file.bufferedReader().use { reader ->
                    val date = file.name.replace(CRASH_FILE, "").toLong()
                    val name = date.dateString() + " " + date.timeString()
                    logs[name] = reader.readText()
                }
            }
            return logs.toList()
        }

        /**
         * clear crashes
         */
        fun clearCrashLogs() {
            val logDir = getLogDirectory()
            val logFiles = logDir.listFiles { _, name -> name.startsWith(CRASH_FILE) }

            logFiles?.forEach { file ->
                if (file.exists()) {
                    file.delete()
                }
            }
        }

    }
}