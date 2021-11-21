package com.nuhash.photoviewer.utils

import android.content.Context
import android.util.Base64
import com.nuhash.photoviewer.utils.CommonFunction.logger
import java.io.*
import java.nio.charset.StandardCharsets

class TextFileUtils(val context: Context) {
    companion object {
        const val tempPath = "temp"
        const val TAG = "TextFileUtils"
        fun base64Decode(input: String, urlSafe: Boolean = true): ByteArray {
            return try {
                if (urlSafe)
                    Base64.decode(input, Base64.URL_SAFE)
                else
                    Base64.decode(input, Base64.DEFAULT)
            } catch (ex: Exception) {
                logger(TAG, ex.message)
                "".toByteArray()
            }
        }

        fun base64Encode(input: ByteArray, urlSafe: Boolean = true): String {
            return try {
                if (urlSafe)
                    Base64.encodeToString(input, Base64.URL_SAFE)
                else
                    Base64.encodeToString(input, Base64.DEFAULT)
            } catch (ex: Exception) {
                logger(TAG, ex.message)
                ""
            }
        }
    }

    fun getSavedM3UFile(link: String): String {
        val fileName = base64Encode(link.toByteArray())
        if (fileName.isEmpty()) return ""
        val ans = readText(tempPath, fileName)
        return if (!ans.first) ""
        else ans.second
    }

    fun saveM3UFile(link: String, response: String) {
        val fileName = base64Encode(link.toByteArray())
        if (fileName.isEmpty()) return
        logger("saveM3UFile", "$link\n$response")
        writeText(tempPath, fileName, response)
    }


    private fun readText(pathNow: String, fileName: String): Pair<Boolean, String> {
        val ans = StringBuilder()
        try {
            val root = File(context.filesDir, pathNow)
            if (!root.exists()) {
                if (!root.mkdirs()) {
                    logger(TAG, "Problem creating folder")
                } else {
                    writeText(pathNow, fileName, "")
                    return Pair(false, "")
                }
            }
            val gpxFile = File(root, fileName)
            val fileReader: Reader = InputStreamReader(
                FileInputStream(gpxFile),
                StandardCharsets.UTF_8
            )
            val bufferedReader = BufferedReader(fileReader)
            while (true) {
                val lineNow = bufferedReader.readLine()
                if (lineNow != null) {
                    ans.append(lineNow)
                    ans.append("\n")
                } else {
                    break
                }
            }
            bufferedReader.close()
        } catch (ex: Exception) {
            logger(TAG, ex.message)
            return Pair(false, "")
        }
        return Pair(true, ans.toString())
    }

    private fun writeText(
        pathNow: String,
        fileName: String,
        sBody: String,
        append: Boolean = false
    ): String {
        try {
            val root = File(context.filesDir, pathNow)
            if (!root.exists()) {
                if (!root.mkdirs()) {
                    logger(TAG, "Problem creating folder")
                    return ""
                }
            }
            val gpxFile = File(root, fileName)
            val writer =
                OutputStreamWriter(FileOutputStream(gpxFile, append), StandardCharsets.UTF_8)

            val fout = BufferedWriter(writer)
            fout.append(sBody)
            fout.flush()
            fout.close()
            return gpxFile.absolutePath
        } catch (ex: Exception) {
            logger(TAG, ex.message)
            return "Error : " + ex.message
        }
    }
}