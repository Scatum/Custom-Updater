package com.m.appupdater.updater_kt

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import com.m.appupdater.R
import permission.PermissionUtils
import java.io.File


class UpdaterActivity : AppCompatActivity() {
    internal var TAG = "UpdaterActivity"
    internal var pd: ProgressDialog? = null
    private val MY_PERMISSION_REQUEST_CODE = 100


    internal var progressBar: ProgressBar? = null


    private val view: RelativeLayout
        get() {
            val relativeLayout = RelativeLayout(this)
            val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            relativeLayout.layoutParams = layoutParams
            relativeLayout.id = R.id.updater_screen_root_view

            return relativeLayout
        }

    // TextView textView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view)

        if (checkRunTimePermission()) {
            openUpdaterSragment()
        }

    }

    private fun openUpdaterSragment() {
        val fragmentManager = supportFragmentManager
        fragmentManager?.beginTransaction()?.replace(R.id.updater_screen_root_view, UpdaterFragment.newInstance())?.commitNow()

    }


    override fun onBackPressed() {
        if (!UpdaterManager.instance.isInProgress) {
            super.onBackPressed()
        }

    }

    internal fun getMicrophoneAvailable(context: Context): Boolean {
        val recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
        recorder.setOutputFile(File(context.cacheDir, "MediaUtil#micAvailTestFile").absolutePath)
        var available = true
        try {
            recorder.prepare()
            recorder.start()

        } catch (e: Exception) {
            available = false
        }

        recorder.release()
        return available
    }

    private fun checkRunTimePermission(): Boolean {
        val STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        val hasPermission = PermissionUtils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (!hasPermission) {
            PermissionUtils.requestPermissions(this, STORAGE_PERMISSION, MY_PERMISSION_REQUEST_CODE)
        }

        return hasPermission
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openUpdaterSragment()
                } else {
                    Toast.makeText(this, "You do not have Storage Permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}