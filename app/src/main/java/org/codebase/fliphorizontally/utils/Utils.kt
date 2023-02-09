package org.codebase.fliphorizontally.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.*

object Utils {

    val outPath: String
    get() {
        val path = Environment.getExternalStorageDirectory().toString() + File.separator + Constants.APP_FOLDER + File.separator

        val folder = File(path)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return path
    }

    fun copyFileToExternalStorage(resourceId: Int, resourceName: String, context: Context): File {
        val pathSdCard = outPath + resourceName
        Log.e("FFMpegpath", pathSdCard)
        try {
            val inputStream = context.resources.openRawResource(resourceId)
            Log.e("FFMpegpath1", inputStream.toString())

            val out = FileOutputStream(pathSdCard)
            Log.e("Out", out.toString())
            val buff = ByteArray(8*1024)
            var read: Int

            try {
                while (inputStream.read(buff).also { read = it } > 0) {
                    out.write(buff, 0, read)
                }
            } finally {
                inputStream.close()
                out.close()
            }

//            inputStream.toFile(pathSdCard)
//            Log.e("FFMpegpath2", inputStream.toFile(pathSdCard).toString())

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return File(pathSdCard)
    }

//    private fun InputStream.toFile(path: String) {
//        Log.e("FFMpegpath3", "here1")
//
//        File(path).outputStream().use {
//            this.copyTo(it)
//        }
//    }

    fun getConvertedFile(folder: String, fileName: String) : File {
        val f = File(folder)

        if (!f.exists()) {
            f.mkdirs()
        }
        return File(f.path + File.separator + fileName)
    }

    fun refreshGallery(path: String, context: Context) {

        val file = File(path)
        try {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0$seconds"
        } else {
            secondsString = "" + seconds
        }

        finalTimerString = "$finalTimerString$minutes:$secondsString"

        // return timer string
        return finalTimerString
    }
}