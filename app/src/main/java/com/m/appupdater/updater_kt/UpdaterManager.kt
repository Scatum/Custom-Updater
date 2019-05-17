package com.m.appupdater.updater_kt

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.support.v4.content.FileProvider
import android.util.Log
import com.m.appupdater.updater_kt.UpdateInfo
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class UpdaterManager {
    internal var updaterListener: UpdaterListener? = null
    internal var TAG = "UpdaterManager"
    internal var conection_download: HttpURLConnection? = null
    internal var conectionForJson: HttpURLConnection? = null
    var isInProgress = false
        private set


    fun download(context: Context, apkUrl: String) {
        if (context == null || conection_download != null) {
            return
        }
        try {
            deleteDownloadedFile(context)
            isInProgress = true
            val url = URL(apkUrl)

            conection_download = url.openConnection() as HttpURLConnection
            conection_download!!.connect()

            // getting file length
            val lenghtOfFile = conection_download!!.contentLength.toLong()
            Log.e(TAG, "lenghtOfFile $lenghtOfFile")

            // in kb
            val fLengthOfFile = lenghtOfFile / 1000


            // input stream to read file - with 8k buffer
            val input = BufferedInputStream(url.openStream(), 8192)

            val folder = context.filesDir

            val path = folder.absolutePath + "/Hhhh"
            var saveFile = File(path)

            saveFile.createNewFile()

            // Output stream to write file
            val output = FileOutputStream(saveFile, false)

            val data = ByteArray(1024)

            var total: Long = 0
            var count: Long = 0
            Log.e(TAG, "start $lenghtOfFile")

            if (updaterListener != null) {
                updaterListener!!.onStartUpdateing()

            }
            do {
                count = input.read(data).toLong()
                if (count != -1L) {
                    Log.e(TAG, "count  $count")

                    total += count

                    val progressPercent = (total * 100 / lenghtOfFile).toInt()
                    val fTotal = total / 1000

                    if (updaterListener != null) {
                        val progress = "$progressPercent%"
                        updaterListener!!.onProgressUpdate(progress)

                    }

                    // writing data to file
                    output.write(data, 0, count.toInt())
                }


            } while (count != -1L)


            // flushing output
            output.flush()

            // closing streams
            output.close()
            input.close()
            Log.i(TAG, "saveFile  $path")

            saveFile = File(path)
            conection_download = null
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                saveFile.setReadable(true, false)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.setDataAndType(Uri.fromFile(saveFile), "application/vnd.android.package-archive")
                context.startActivity(intent)
            } else {
                val intent = Intent(Intent.ACTION_VIEW)
                val fileUri = FileProvider.getUriForFile(context,
                        "app_updater",
                        saveFile)

                intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.startActivity(intent)
            }
            isInProgress = false


        } catch (e: Exception) {
            isInProgress = false
            conection_download = null
            val handler = Handler(Looper.getMainLooper())
            handler.post { updaterListener!!.onFail() }
            Log.e(TAG, "Exception___  " + e.message)
        }

        conection_download = null
        isInProgress = false
    }


    fun onCancel() {
        try {
            if (conectionForJson != null) {
                conectionForJson!!.disconnect()
                conectionForJson = null
            }
            if (conection_download != null) {
                conection_download!!.disconnect()
                conection_download = null
            }
            isInProgress = false
        } catch (e: Exception) {
            Log.e(TAG, " Exception onCancel " + e.message)
        }


    }


    fun setUpdaterListener(updaterListener: UpdaterListener) {
        this.updaterListener = updaterListener
    }

    fun removeUpdaterListener() {
        this.updaterListener = null
    }

    fun deleteDownloadedFile(context: Context?): Boolean {
        var deleted = false
        if (context == null) return false
        try {
            val folder = context.filesDir

            val path = folder.absolutePath + "/Hhhh"

            val file = File(path)
            val isExsistBefor = file.exists()
            deleted = file.delete()
            Log.d(TAG, "exists before deleting -> $isExsistBefor deleted ->$deleted")
        } catch (e: Exception) {
            Log.e(TAG, "Exception on deleting update fil: message... " + e.message)
        }

        return deleted
    }


    interface UpdaterListener {
        fun checkForUpdate(updateInfo: UpdateInfo)

        fun onStartUpdateing()

        fun onProgressUpdate(progress: String)

        fun onFinish()

        fun onFail()

    }

    companion object {
        val instance = UpdaterManager()
    }

}
