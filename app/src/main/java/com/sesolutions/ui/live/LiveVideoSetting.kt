package com.sesolutions.ui.live

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.responses.live.StreamingResponse
import com.sesolutions.ui.common.BaseActivity
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.SPref
import com.sesolutions.utils.Util
import org.apache.http.client.methods.HttpPost

class LiveVideoSetting : BaseActivity(), View.OnClickListener {

    private lateinit var ivBack: AppCompatImageView
    private lateinit var tvTitle: AppCompatTextView
    private lateinit var scToggleShave: SwitchCompat
    private lateinit var scToggleShareStories: SwitchCompat
    private lateinit var scToggleShareFeed: SwitchCompat
    private var selectedPrivacy: String? = null
    private var canSave: Boolean? = null
    private var canPost: Boolean? = null
    private var canShareInStory: Boolean? = null
    private var maxStreamDurations = 1

    private lateinit var llLiveVideoSetting: LinearLayout
    private lateinit var result: StreamingResponse.Result

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_live_video_setting)
        init()
        callLivePermissionApi()
    }

    private fun init() {

        llLiveVideoSetting = findViewById(R.id.llLiveVideoSetting)
        ivBack = findViewById(R.id.ivBack)
        ivBack.setOnClickListener(this)
        tvTitle = findViewById(R.id.tvTitle)
        tvTitle.gravity = Gravity.CENTER_VERTICAL
        tvTitle.text = "Live Settings"

        scToggleShave = findViewById(R.id.scToggleShave)
        scToggleShave.setOnCheckedChangeListener { _, isChecked -> canSave = isChecked }

        scToggleShareStories = findViewById(R.id.scToggleShareStories)
        scToggleShareStories.setOnCheckedChangeListener { _, isChecked -> canShareInStory = isChecked }

        scToggleShareFeed = findViewById(R.id.scToggleShareFeed)
        scToggleShareFeed.setOnCheckedChangeListener { _, isChecked -> canPost = isChecked }

    }

    private fun callLivePermissionApi() {

        if (isNetworkAvailable(this)) {
            try {
                showBaseLoader(true)

                val request = HttpRequestVO(Constant.URL_LIVE_PERMISSION)

                request.params["privacy"] = selectedPrivacy
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(this)
                request.headres[Constant.KEY_COOKIE] = cookie
                request.requestMethod = HttpPost.METHOD_NAME

                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        CustomLog.e("response_live_permission", "" + msg.obj)
                        val response = "" + msg.obj

                        val resp = Gson().fromJson(response, StreamingResponse::class.java)
                        if (resp.error.isNullOrEmpty()) {

                            result = resp.result!!

                            canSave = result.canSave
                            canPost = result.canPost
                            canShareInStory = result.canShareInStory

                            if (canSave!!)
                                findViewById<View>(R.id.llSave).visibility = View.VISIBLE
                            if (canShareInStory!!)
                                findViewById<View>(R.id.rlStory).visibility = View.VISIBLE
                            if (canPost!!)
                                findViewById<View>(R.id.rlFeed).visibility = View.VISIBLE

                            if (!canPost!! && !canShareInStory!!)
                                findViewById<AppCompatTextView>(R.id.tvNote).text = getString(R.string.sharing_disabled)

                            maxStreamDurations = result.maxStreamDurations

                            applyUserSetting()

                        } else {
                            Util.showSnackbar(llLiveVideoSetting, resp.errorMessage)
                            Handler().postDelayed({ onBackPressed() }, 1000)
                        }

                    } catch (e: Exception) {
                        CustomLog.e(e)
                        Util.showSnackbar(llLiveVideoSetting, getString(R.string.msg_something_wrong))
                    }
                    true
                }
                HttpRequestHandler(this, Handler(callback)).run(request)

            } catch (e: Exception) {
                CustomLog.e(e)
            }

        } else {
            Util.showSnackbar(llLiveVideoSetting, getString(R.string.MSG_NO_INTERNET))
        }
    }

    private fun applyUserSetting() {

        val intent = intent

        selectedPrivacy = intent.getStringExtra("privacy")
        canSave = intent.getBooleanExtra("save", true)
        canShareInStory = intent.getBooleanExtra("story", true)
        canPost = intent.getBooleanExtra("feed", true)

        when(selectedPrivacy) {
            "everyone" -> findViewById<RadioButton>(R.id.radioButton1).isChecked = true
            "networks" -> findViewById<RadioButton>(R.id.radioButton2).isChecked = true
            "friends" -> findViewById<RadioButton>(R.id.radioButton3).isChecked = true
            "onlyme" -> findViewById<RadioButton>(R.id.radioButton4).isChecked = true
        }
        scToggleShave.isChecked = canSave!!
        scToggleShareStories.isChecked = canShareInStory!!
        scToggleShareFeed.isChecked = canPost!!
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.radioButton1 -> if (checked) {
                    selectedPrivacy = "everyone"
                    updatePrivacy()
                }

                R.id.radioButton2 -> if (checked) {
                    selectedPrivacy = "networks"
                    updatePrivacy()
                }

                R.id.radioButton3 -> if (checked) {
                    selectedPrivacy = "friends"
                    updatePrivacy()
                }

                R.id.radioButton4 -> if (checked) {
                    selectedPrivacy = "onlyme"
                    updatePrivacy()
                }

            }
        }
    }

    override fun onClick(v: View?) {

        if (v!!.id == R.id.ivBack)
            onBackPressed()

    }

    override fun onBackPressed() {

        CustomLog.e("LiveVideoSetting", "$selectedPrivacy, $canSave, $canShareInStory, $canPost")

        val output = Intent()
        output.putExtra("privacy", selectedPrivacy)
        output.putExtra("save", canSave)
        output.putExtra("story", canShareInStory)
        output.putExtra("feed", canPost)
        output.putExtra("maxTime", maxStreamDurations)
        setResult(RESULT_OK, output)
        finish()
    }


    private fun updatePrivacy() {

        if (isNetworkAvailable(this)) {
            try {
                showBaseLoader(true)

                val request = HttpRequestVO(Constant.URL_LIVE_PERMISSION)

                request.params["privacy"] = selectedPrivacy
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(this)
                request.headres[Constant.KEY_COOKIE] = cookie
                request.requestMethod = HttpPost.METHOD_NAME

                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        CustomLog.e("response_live_permission", "" + msg.obj)
                        val response = "" + msg.obj

                        val resp = Gson().fromJson(response, StreamingResponse::class.java)
                        if (resp.error.isNullOrEmpty()) {

                            result = resp.result!!
//
//                            canSave = result.canSave
//                            canPost = result.canPost
//                            canShareInStory = result.canShareInStory
//
//                            if (canSave!!)
//                                findViewById<View>(R.id.llSave).visibility = View.VISIBLE
//                            if (canShareInStory!!)
//                                findViewById<View>(R.id.rlStory).visibility = View.VISIBLE
//                            if (canPost!!)
//                                findViewById<View>(R.id.rlFeed).visibility = View.VISIBLE

                            maxStreamDurations = result.maxStreamDurations

//                            applyUserSetting()

                        } else {
                            Util.showSnackbar(llLiveVideoSetting, resp.errorMessage)
                            Handler().postDelayed({ onBackPressed() }, 1000)
                        }

                    } catch (e: Exception) {
                        CustomLog.e(e)
                        Util.showSnackbar(llLiveVideoSetting, getString(R.string.msg_something_wrong))
                    }
                    true
                }
                HttpRequestHandler(this, Handler(callback)).run(request)

            } catch (e: Exception) {
                CustomLog.e(e)
            }

        } else {
            Util.showSnackbar(llLiveVideoSetting, getString(R.string.MSG_NO_INTERNET))
        }
    }
}
