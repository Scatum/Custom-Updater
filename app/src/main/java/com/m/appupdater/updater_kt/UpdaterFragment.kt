package com.m.appupdater.updater_kt

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.os.Handler
import android.support.annotation.DimenRes
import android.support.v4.app.Fragment
import android.util.Log
import android.view.*
import android.widget.*
import com.m.appupdater.R


class UpdaterFragment : Fragment(), UpdaterManager.UpdaterListener {
    private var TAG = "UpdaterFragment"
    private var versionName: TextView? = null
    private var updateStatusText: TextView? = null
    private var progressBar: ProgressBar? = null
    private var updateButton: TextView? = null
    private var updaterThread: UpdaterThread? = null

    private val versionCode: Int
        get() {
            try {
                val pInfo = context!!.packageManager.getPackageInfo(context!!.packageName, 0)
                return pInfo.versionCode

            } catch (e: Exception) {
                Log.e(TAG, "cannot get version code " + e.message)
            }

            return 0
        }

    private val updaterView: RelativeLayout
        get() {

            val dp10 = getDP(R.dimen.updater_10dp)

            val dp15 = getDP(R.dimen.updater_15dp)
            val dp44 = getDP(R.dimen.updater_44dp)
            val dp80 = getDP(R.dimen.updater_80dp)
            val dp64 = getDP(R.dimen.updater_32dp)
            val dp18 = getDP(R.dimen.updater_18dp)
            val dp16 = getDP(R.dimen.updater_16dp)
            val dp14 = getDP(R.dimen.updater_14dp)
            val dp42 = getDP(R.dimen.updater_42dp)


            val rootLayout = RelativeLayout(getActivity())
            rootLayout.setBackgroundColor(Color.BLACK)
            val rootLayoutParams = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            rootLayoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
            rootLayoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
            rootLayout.layoutParams = rootLayoutParams


            val contentLayout = LinearLayout(getActivity())
            contentLayout.orientation = LinearLayout.VERTICAL
            val contentLayoutParams = RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            contentLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)

            contentLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            contentLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            contentLayout.layoutParams = contentLayoutParams


            val appLogo = ImageView(getActivity())
            appLogo.setImageResource(R.drawable.logo)
            val appLogoParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            appLogoParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            appLogoParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            appLogoParams.gravity = Gravity.CENTER_HORIZONTAL
            appLogo.layoutParams = appLogoParams
            contentLayout.addView(appLogo)


            versionName = TextView(getActivity())
            versionName!!.gravity = Gravity.CENTER
            versionName!!.text = "Version 2.4.3"
            versionName!!.id = R.id.update_screen_current_version_name
            versionName!!.setTextColor(Color.WHITE)
            val versionNameLayoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            versionNameLayoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            versionNameLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            versionNameLayoutParams.gravity = Gravity.CENTER_HORIZONTAL
            versionNameLayoutParams.topMargin = dp44
            versionName!!.layoutParams = versionNameLayoutParams
            versionName!!.textSize = 16f
            versionName!!.alpha = 0.7.toFloat()
            versionName!!.text = "Version"
            contentLayout.addView(versionName)


            val progreasLayoutContent = LinearLayout(getActivity())
            val ProgreasLayoutContentParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            ProgreasLayoutContentParams.gravity = Gravity.CENTER_HORIZONTAL
            ProgreasLayoutContentParams.topMargin = dp64
            progreasLayoutContent.layoutParams = ProgreasLayoutContentParams


            progreasLayoutContent.id = R.id.update_screen_progressbar_layout_id

            progreasLayoutContent.orientation = LinearLayout.HORIZONTAL


            progressBar = ProgressBar(getActivity())
            progressBar!!.id = R.id.update_screen_progressbar_id

            val progressBarParams = LinearLayout.LayoutParams(
                    dp42, dp42)
            progressBarParams.rightMargin = dp10
            progressBarParams.gravity = Gravity.CENTER_VERTICAL
            progressBar!!.layoutParams = progressBarParams

            progressBar!!.indeterminateDrawable.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY)
            progressBar!!.alpha = 0.7f
            contentLayout.addView(progreasLayoutContent)

            progreasLayoutContent.addView(progressBar)


            updateStatusText = TextView(getActivity())
            val updateStatusTextParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            updateStatusText!!.id = R.id.update_screen_progressbar_updateStatusText_id
            updateStatusText!!.textSize = 16f
            updateStatusText!!.alpha = 0.7.toFloat()
            updateStatusText!!.gravity = Gravity.CENTER
            updateStatusText!!.text = "Checking for update"
            updateStatusTextParams.gravity = Gravity.CENTER_VERTICAL
            updateStatusText!!.setTextColor(Color.WHITE)
            updateStatusText!!.layoutParams = updateStatusTextParams

            progreasLayoutContent.addView(updateStatusText)

            updateButton = TextView(context)
            updateButton!!.id = R.id.updater_button
            updateButton!!.setPadding(dp15, dp10, dp15, dp10)
            val rect = RoundRectShape(
                    floatArrayOf(5f, 5f, 5f, 5f, 5f, 5f, 5f, 5f), null, null)
            val bg = ShapeDrawable(rect)
            bg.paint.color = Color.WHITE
            updateButton!!.setBackgroundDrawable(bg)
            updateButton!!.text = "Update"
            updateButton!!.setPadding(dp15, dp10, dp15, dp10)
            updateButton!!.gravity = Gravity.CENTER
            updateButton!!.setTextColor(Color.BLACK)
            val updateButtonParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            updateButtonParams.topMargin = 18
            updateButtonParams.gravity = Gravity.CENTER_HORIZONTAL
            updateButton!!.layoutParams = updateButtonParams
            updateButton!!.typeface = Typeface.DEFAULT_BOLD
            updateButton!!.textSize = 14f

            updateButton!!.visibility = View.GONE

            contentLayout.addView(updateButton)
            rootLayout.addView(contentLayout)
            return rootLayout
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        return updaterView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UpdaterManager.instance.setUpdaterListener(this)
        updaterThread = UpdaterThread()
        updaterThread?.context = context
        updaterThread?.url = "https://cs03.spac.me/f/084123071032179104219041191168023046157038118061066208/1558076195/77554439/0/37a7e8ed09b2237559a2abf363f38b4a/RAR_5.70.build71%5BMod%5D_arm-spcs.me.apk"
        updaterThread!!.start()


    }


    override fun onDestroyView() {
        UpdaterManager.instance.removeUpdaterListener()
        UpdaterManager.instance.onCancel()
        super.onDestroyView()
    }


    private fun getVersionName(): String? {
        try {
            val pInfo = context!!.packageManager.getPackageInfo(context!!.packageName, 0)
            return pInfo.versionName
        } catch (e: Exception) {
            Log.e(TAG, "cannot get version name " + e.message)
        }

        return null
    }


    override fun checkForUpdate(updateInfo: UpdateInfo) {
        val currentVersionCode = versionCode
        if (currentVersionCode != -1 && Integer.valueOf(updateInfo.code) > currentVersionCode) {

            val handler = Handler()
            handler.postDelayed({
                updateButton!!.visibility = View.VISIBLE
                progressBar!!.visibility = View.GONE
                updateStatusText!!.setText("New Version")
                updateButton!!.setOnClickListener {
                    UpdaterThread.setUrl(updaterThread!!, "url")
                    UpdaterThread.setContext(updaterThread!!, context!!)
                    updaterThread!!.start()
                    updateButton!!.isClickable = false
                    updateButton!!.visibility = View.GONE
                    progressBar!!.visibility = View.VISIBLE
                }
            }, 800)


        } else {
            progressBar!!.visibility = View.GONE
            updateStatusText!!.text = "your version is up to date"
        }


    }

    override fun onStartUpdateing() {
        val activity = activity
        if (activity != null && !activity.isFinishing) {
            activity.runOnUiThread {
                updateButton!!.visibility = View.GONE
                progressBar!!.visibility = View.GONE
            }
        }
    }

    override fun onProgressUpdate(progress: String) {

        val activity = activity
        if (activity != null && !activity.isFinishing) {
            activity.runOnUiThread { updateStatusText!!.text = progress }
        }

    }

    override fun onFinish() {

    }

    override fun onFail() {

        updateButton!!.visibility = View.GONE
        progressBar!!.visibility = View.GONE
        updateStatusText!!.text = "something wrong"
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun onBackPressed() {
        if (!UpdaterManager.instance.isInProgress) {
            val activity = activity
            if (activity != null && !activity.isFinishing) {
                activity.onBackPressed()
            }
        }
    }

    class UpdaterThread : Thread(), Runnable {
        var url = ""
        var context: Context? = null

        override fun run() {
            super.run()
            context?.apply {
                UpdaterManager.instance.download(this, url)
            }
        }

        companion object {
            fun setUrl(updaterThread: UpdaterThread, url: String) {
                updaterThread.url = url
            }

            fun setContext(updaterThread: UpdaterThread, context: Context?) {
                updaterThread.context = context
            }
        }
    }


    private fun getDP(@DimenRes dp: Int): Int {
        return resources.getDimension(dp).toInt()
    }

    companion object {
        fun newInstance(): UpdaterFragment {
            val fragment = UpdaterFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }


}