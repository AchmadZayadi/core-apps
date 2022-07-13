package com.sesolutions.ui.live

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.*
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.webkit.WebChromeClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sesolutions.R
import com.sesolutions.http.*
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.ui.live.custom_anim.Direction
import com.sesolutions.ui.live.custom_anim.ZeroGravityAnimation
import com.sesolutions.responses.FeedLikeResponse
import com.sesolutions.responses.ReactionPlugin
import com.sesolutions.responses.comment.CommentData
import com.sesolutions.responses.live.StreamingResponse
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.common.BaseActivity
import com.sesolutions.ui.common.BaseResponse
import com.sesolutions.ui.common.MainApplication
import com.sesolutions.ui.customviews.InsideWebViewClient
import com.sesolutions.ui.customviews.VideoEnabledWebChromeClient
import com.sesolutions.ui.customviews.VideoEnabledWebView
import com.sesolutions.ui.video.VideoViewActivity
import com.sesolutions.utils.*
import io.agora.interactivebroadcastingwithcdnstreaming.MessageItemDecoration
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.internal.LastmileProbeConfig
import io.agora.rtc.live.LiveTranscoding
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.http.client.methods.HttpPost
import org.apache.http.conn.HttpHostConnectException
import org.json.JSONObject
import java.lang.NullPointerException
import java.lang.Runnable
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LiveVideoActivity : BaseActivity(), View.OnClickListener, OnUserClickedListener<Int, Any>, View.OnTouchListener {

    private var userCount: Int = 1
    private var type: String? = null
    private var canGoLive: Boolean = true
    private var canJoin: Boolean = false
    private var isServerReady: Boolean = false
    private var canShowViews: Boolean = true
    private var isLive: Boolean = false
    private var actionPerformed = false
    private var isStoped = false
    private val REQ_STREAM_LIKE = 99
    private val REQ_LIVE_SETTING = 98
    private var commentList: MutableList<CommentData>? = null
    private var sid: String? = null
    private var userId = 0
    private var hostId = 0
    private var userIdhost = 0
    private var cRole = 0
    private var activityId = 0
    private var eliveHostId = 0
    private var postion = 0
    private var value: Any? = null
    private var canShareInStory = true
    private var canPost = true
    private var canSave = true
    var privacy = "everyone"
    var maxStreamDurations = 1000
    private lateinit var webView: VideoEnabledWebView
    lateinit var webChromeClient: VideoEnabledWebChromeClient
    private var timer: CountDownTimer? = null

    private lateinit var result: StreamingResponse.Result
    private lateinit var mSelfView: LinearLayout
    private var cvLive: androidx.cardview.widget.CardView? = null

    private var mRtcEngine: RtcEngine? = null
    private var mLiveTranscoding: LiveTranscoding? = null
    private val mUserInfo = HashMap<Int, UserInfo>()
    private var mBigView: SurfaceView? = null

    private var mMessageAdapter: MessageAdapter? = null
    private var mMsgList: ArrayList<CommentData>? = null

    private lateinit var tvShare: TextView

    private var ivBack: ImageView? = null
    private lateinit var ivSetting: LinearLayoutCompat
    private var ivMuteLocal: ImageView? = null
    private var ivMuteRemote: ImageView? = null
    private lateinit var ivPrivacyImage: AppCompatImageView
    private lateinit var tvEnd: AppCompatTextView
    private lateinit var tvProgress: AppCompatTextView
    private lateinit var tvViewPrivacy: AppCompatTextView
    private lateinit var tvTotalTime: AppCompatTextView
    private var mcvGoLive: MaterialCardView? = null
    private var mcvGoOwnLive: MaterialCardView? = null
    private var llAudienceAction: View? = null
    private var llGoOwnLive: View? = null
    private var llPostResult: View? = null
    private var rlGoLive: View? = null
    private var vScrim: View? = null
    private lateinit var scToggleStory: SwitchCompat
    private lateinit var scTogglePost: SwitchCompat
    private lateinit var msgRecycler: androidx.recyclerview.widget.RecyclerView

    private lateinit var rvReply: androidx.recyclerview.widget.RecyclerView
    private lateinit var reactionAdapter: LiveVideoReactionAdapter
    private var reactionList: MutableList<ReactionPlugin>? = null

    private var mRtcStats: IRtcEngineEventHandler.RtcStats? = null

    val formatter = SimpleDateFormat("m:ss")
    val timezone = TimeZone.getTimeZone("UTC")!!

    private var mRtcEngineEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onError(errorCode: Int) {
            super.onError(errorCode)
            CustomLog.e("-->onError", "<--$errorCode")
        }

        override fun onRtcStats(stats: RtcStats?) {
            super.onRtcStats(stats)
            try {
                mRtcStats = stats
                runOnUiThread {
                    if (stats != null) {
                        tvTotalTime.text = formatter.format(Date(stats.totalDuration * 1000L))
                    }
                }
            } catch (e: Exception) {
                CustomLog.e(e)
            }
        }

        override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed)
//            CustomLog.e("onRemoteVideoStateChanged<---", "$uid---$state---$reason")

            if (state == Constants.REMOTE_VIDEO_STATE_FROZEN && (reason == 1 || reason == 8))
                Util.showSnackbar(mSelfView, "Video is paused")
            else if (state == Constants.REMOTE_VIDEO_STATE_STARTING && (reason == Constants.REMOTE_VIDEO_STATE_REASON_NETWORK_RECOVERY ||
                            reason == Constants.REMOTE_VIDEO_STATE_REASON_AUDIO_FALLBACK_RECOVERY))
                Util.showSnackbar(mSelfView, "Video is Resuming")

        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            CustomLog.e("onJoinChannelSuccess", "<--$channel-->uid<--$uid")
//            mUserInfo[uid] = UserInfo(uid)
        }

        override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
            super.onFirstLocalVideoFrame(width, height, elapsed)
            CustomLog.e("onFirstLocalVideoFrame", "")
            hideBaseLoader()
        }

        override fun onRejoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            super.onRejoinChannelSuccess(channel, uid, elapsed)
            CustomLog.e("$uid -->", "RejoinChannel<--")
        }

        override fun onLeaveChannel(stats: RtcStats) {
            super.onLeaveChannel(stats)
            CustomLog.e("leaveChannel<--", "${stats.totalDuration}")
            runOnUiThread {
                tvTotalTime.text = "0:00"
            }
        }

        override fun onConnectionLost() {
            super.onConnectionLost()
            CustomLog.e("onConnectionLost<--", "")
        }



        override fun onConnectionStateChanged(state: Int, reason: Int) {
            super.onConnectionStateChanged(state, reason)
            CustomLog.e("onConnectionStateChanged<--", "$state--$reason")
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            CustomLog.e("onUserJoined<", "--$uid")
            runOnUiThread {
                mRtcEngine!!.stopPreview()
                if (null != mRtcStats) {
                    if (mRtcStats!!.users > 1)
                        findViewById<TextView>(R.id.liveUsers).text = (mRtcStats?.users!!.minus(1)).toString()
                } else
                    findViewById<androidx.cardview.widget.CardView>(R.id.cvUserCount).visibility = View.VISIBLE
//                mUserInfo[uid] = UserInfo(uid)
                setupRemoteVideo(uid)
                isLive = true
//                setTranscoding()
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
            CustomLog.e("EndedByHost", "<--$uid ----> $reason")
            runOnUiThread {
                //                mUserInfo.remove(uid)
//                setTranscoding()
                onRemoteUserLeft()
                isLive = false
            }
        }

        override fun onStreamMessage(uid: Int, streamId: Int, data: ByteArray?) {
            super.onStreamMessage(uid, streamId, data)
            CustomLog.e("-->$uid--$streamId--", "${data?.toString(Charsets.UTF_8)}")
            runOnUiThread {
                if (data?.toString(Charsets.UTF_8).equals("three"))
                    flyEmoji(reactionList?.get(0)!!.image)
            }
        }

        override fun onLastmileQuality(quality: Int) {
            super.onLastmileQuality(quality)
            tvProgress.visibility = View.VISIBLE
            when (quality) {
                Constants.QUALITY_DETECTING -> tvProgress.text = "Checking Connection"
                Constants.QUALITY_EXCELLENT -> {
                    canGoLive = true
                    tvProgress.text = " The quality is excellent $canGoLive"
                }
                Constants.QUALITY_GOOD -> {
                    canGoLive = true
                    tvProgress.text = " The quality is good $canGoLive"
                }
                Constants.QUALITY_POOR -> {
                    canGoLive = true
                    tvProgress.text = " The quality is poor $canGoLive"
                }
                Constants.QUALITY_BAD -> {
                    canGoLive = false
                    tvProgress.text = "Connection is bad. $canGoLive"
                }
                Constants.QUALITY_VBAD -> {
                    canGoLive = false
                    tvProgress.text = "Connection is very bad. $canGoLive"
                }
                Constants.QUALITY_DOWN -> {
                    canGoLive = false
                    tvProgress.text = "The network is disconnected and users cannot communicate at all. $canGoLive"
                }
            }
        }

        override fun onLastmileProbeResult(result: LastmileProbeResult?) {
            super.onLastmileProbeResult(result)
            mRtcEngine!!.stopLastmileProbeTest()
            if (result!!.state.equals(Constants.LASTMILE_PROBE_RESULT_COMPLETE)) {
                tvProgress.text = "test is complete"
                CoroutineScope(Dispatchers.Main).launch {
                    delay(200)
                    tvProgress.visibility = View.GONE
                }
            }
        }
    }

    private var mSocket: Socket? = null

    private fun isAudience(cRole: Int): Boolean {
        return cRole == Constants.CLIENT_ROLE_AUDIENCE
    }

    private var isConnected = true
    private val onConnect = Emitter.Listener {
        runOnUiThread(Runnable {
            if (!isConnected) {
                //     Toast.makeText(getApplicationContext(),
                //           R.string.connect, Toast.LENGTH_LONG).show()
                Log.e("connected","connected");
                isConnected = true

            }
        })
    }

    private val onDisconnect = Emitter.Listener {
        runOnUiThread(Runnable {
            Log.e("disconnected","disconnected");
            mSocket!!.connect()
            isConnected = false
            //Toast.makeText(getApplicationContext(),
            //      R.string.disconnect, Toast.LENGTH_LONG).show()
        })
    }

    private val onConnectError = Emitter.Listener {
        runOnUiThread(Runnable {
            Log.e("error data", "Error connecting" + it[0].toString());
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_live)
        try {

            val app: MainApplication = getApplication() as MainApplication
            mSocket = app.getSocket();
            mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
            mSocket!!.on(Socket.EVENT_DISCONNECT, onDisconnect)
            mSocket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)

            mSocket!!.on("liveComment", onLiveComment)
            mSocket!!.on("liveReaction", onLiveReaction)
            mSocket!!.connect()

            formatter.timeZone = timezone
        } catch (e: URISyntaxException) {
            CustomLog.e(e)
        } catch (e: NullPointerException) {
            Util.showSnackbar(findViewById(R.id.self_container), getString(R.string.live_setup_incomplete))
            Handler().postDelayed({ finish() }, 3000)
        } catch (e: Exception) {
            CustomLog.e(e)
            finish()
        }
        userId = SPref.getInstance().getLoggedInUserId(this)

        val intent = intent
        hostId = intent.getIntExtra(Constant.KEY_HOST_ID, 0)
        userIdhost = intent.getIntExtra(Constant.KEY_OBJECTID_Data, userId)
        activityId = intent.getIntExtra(Constant.KEY_ACTIVITY_ID, 1)
        postion = intent.getIntExtra(Constant.KEY_POSITION, 0)
        value = intent.getStringExtra(Constant.STORY_IMAGE_KEY)
//        type = intent.getStringExtra(Constant.KEY_TYPE)

        Log.e("host Id",""+hostId);
        Log.e("userId Id",""+userId);
        Log.e("userIdhost Id",""+userIdhost);
        Log.e("activityId Id",""+activityId);
        if(hostId==0){
            notifyUsers()
        }

        cRole = if (0 == hostId || hostId==userId) Constants.CLIENT_ROLE_BROADCASTER else Constants.CLIENT_ROLE_AUDIENCE
        if (cRole == 0) {
            throw RuntimeException("Should not reach here")
        }
        if (isAudience(cRole))
            callCheckStreamingStatus(activityId)

        init()

        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initializeAgoraEngine()
            if (!isAudience(cRole)) {
                startPreviewScreen()
                callLivePermissionApi()
            }
        }
    }

    private val onLiveComment = Emitter.Listener { args ->

        runOnUiThread {
            try {
                CustomLog.e("liveComment", "" + args[0])
                //   val itemComment = JSONObject(args[0].toString()).getJSONObject("result").getJSONObject("comment_data");
                sendMsg(Gson().fromJson(args[0].toString(), CommentData::class.java))
            } catch (e: Exception) {
                CustomLog.e(e)
                Log.e("11111","55555");
            }
        }
    }

    private val onLiveReaction = Emitter.Listener { args ->
        runOnUiThread {
            try {
                CustomLog.e("liveReaction", args[0].toString())
                flyEmoji(args[0].toString())
            } catch (e: Exception) {
                CustomLog.e(e)
            }
        }
    }

    private val onUserCount = Emitter.Listener { args ->
        runOnUiThread {
            try {
                CustomLog.e("onUserCount", "" + args[0])
                userCount = JSONObject(args[0].toString()).getString("userCount").toInt()
                if (userCount > 1) {
                    findViewById<androidx.cardview.widget.CardView>(R.id.cvUserCount).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.liveUsers).text = (userCount - 1).toString()
                    findViewById<TextView>(R.id.totalUsers).text = (userCount - 1).toString()
                } else {
                    findViewById<androidx.cardview.widget.CardView>(R.id.cvUserCount).visibility = View.GONE
                }
                if (JSONObject(args[0].toString()).has("comment_data")) {
                    val itemComment = JSONObject(args[0].toString()).getString("comment_data").toString()
                    sendMsg(Gson().fromJson(itemComment, CommentData::class.java))
                }
            } catch (e: Exception) {
                CustomLog.e(e)
            }
        }
    }

    private fun init() {
        mSelfView = findViewById(R.id.self_container)
        mSelfView.setOnTouchListener(this)

        llAudienceAction = findViewById(R.id.llAudienceAction)
        llGoOwnLive = findViewById(R.id.llGoOwnLive)
        llPostResult = findViewById(R.id.llPostResult)
        ivBack = findViewById(R.id.ivBack)
        ivSetting = findViewById(R.id.llPrivacy)
        ivMuteLocal = findViewById(R.id.ivMuteLocal)
        ivMuteRemote = findViewById(R.id.ivMuteRemote)
        ivBack?.setOnClickListener(this)
        ivSetting.setOnClickListener(this)
        cvLive = findViewById(R.id.cvLive)
        tvTotalTime = findViewById(R.id.tvTotalTime)
        rlGoLive = findViewById(R.id.rlgolive)
        tvShare = findViewById(R.id.tvShare)
        mcvGoLive = findViewById(R.id.mcvGoLive)
        mcvGoOwnLive = findViewById(R.id.mcvGoOwnLive)
        mcvGoLive!!.setOnClickListener(this)
        mcvGoOwnLive!!.setOnClickListener(this)
        tvEnd = findViewById(R.id.tvEnd)
        tvEnd.setOnClickListener(this)
        vScrim = findViewById(R.id.vScrim)
        tvProgress = findViewById(R.id.tvProgress)
        tvViewPrivacy = findViewById(R.id.tvViewPrivacy)
        tvViewPrivacy.text = getString(R.string.privacy_everyone)
        ivPrivacyImage = findViewById(R.id.ivPrivacyImage)
        webView = findViewById(R.id.webView)

        scToggleStory = findViewById(R.id.scToggleStory)
        scToggleStory.setOnCheckedChangeListener { _, isChecked ->

            if (!isChecked && !canPost) {
                canShareInStory = false
                findViewById<MaterialCardView>(R.id.mcvPost).isEnabled = false
                findViewById<MaterialCardView>(R.id.mcvPost).alpha = 0.4f
            } else if (!isChecked || canPost) {
                canShareInStory = false
                findViewById<MaterialCardView>(R.id.mcvPost).isEnabled = true
                findViewById<MaterialCardView>(R.id.mcvPost).alpha = 1f
            } else if (isChecked || canPost) {
                canShareInStory = true
                findViewById<MaterialCardView>(R.id.mcvPost).isEnabled = true
                findViewById<MaterialCardView>(R.id.mcvPost).alpha = 1f
            } else if (isChecked || !canPost) {
                canShareInStory = true
                findViewById<MaterialCardView>(R.id.mcvPost).isEnabled = true
                findViewById<MaterialCardView>(R.id.mcvPost).alpha = 1f
            }
            CustomLog.e("$canShareInStory", "$canPost")
        }

        scTogglePost = findViewById(R.id.scTogglePost)
        scTogglePost.setOnCheckedChangeListener { _, isChecked ->

            if (!isChecked && !canShareInStory) {
                canPost = false
                findViewById<MaterialCardView>(R.id.mcvPost).isEnabled = false
                findViewById<MaterialCardView>(R.id.mcvPost).alpha = 0.4f
            } else if (!isChecked || canShareInStory) {
                canPost = false
                findViewById<MaterialCardView>(R.id.mcvPost).isEnabled = true
                findViewById<MaterialCardView>(R.id.mcvPost).alpha = 1f
            } else if (isChecked || canShareInStory) {
                canPost = true
                findViewById<MaterialCardView>(R.id.mcvPost).isEnabled = true
                findViewById<MaterialCardView>(R.id.mcvPost).alpha = 1f
            } else if (isChecked || !canShareInStory) {
                canPost = true
                findViewById<MaterialCardView>(R.id.mcvPost).isEnabled = true
                findViewById<MaterialCardView>(R.id.mcvPost).alpha = 1f
            }
            CustomLog.e("$canShareInStory", "$canPost")
        }

        initMessage()
        initReactionView()
    }

    private fun initReactionView() {
        rvReply = findViewById(R.id.rvReply)
        reactionList = SPref.getInstance().getReactionPlugins(this)
        if (null != reactionList && reactionList!!.size > 0) {
            reactionList!!.add(0, ReactionPlugin())
            reactionAdapter = LiveVideoReactionAdapter(reactionList!!, this, this)
            rvReply.adapter = reactionAdapter
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        try {
            if (v == mSelfView) {
                closeKeyboard()
                rvReply.clearFocus()

                if (isLive) {
                    when (event?.action) {
                        MotionEvent.ACTION_DOWN -> {
                            if (canShowViews) {
                                canShowViews = !canShowViews
                                llAudienceAction?.visibility = View.GONE
                                msgRecycler.visibility = View.GONE
                                rlGoLive?.visibility = View.GONE
                                findViewById<androidx.cardview.widget.CardView>(R.id.cvUserCount).visibility = View.GONE
                                CustomLog.e("hide", "ontouch")
                            } else {
                                CustomLog.e("show", "ontouch")
                                canShowViews = !canShowViews
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                    findViewById<ImageView>(R.id.ivPipMode).visibility = View.VISIBLE
                                else
                                    findViewById<ImageView>(R.id.ivPipMode).visibility = View.GONE

                                if (userCount > 1)
                                    findViewById<androidx.cardview.widget.CardView>(R.id.cvUserCount).visibility = View.VISIBLE
                                msgRecycler.visibility = View.VISIBLE
                                llAudienceAction!!.visibility = View.VISIBLE
                                ivBack!!.visibility = View.GONE
                                ivSetting.visibility = View.GONE
                                cvLive!!.visibility = View.VISIBLE
                                if (isAudience(cRole)) {
                                    tvEnd.visibility = View.GONE
                                    ivMuteRemote!!.visibility = View.VISIBLE
                                    rlGoLive!!.visibility = View.GONE
                                } else {
                                    tvEnd.visibility = View.VISIBLE
                                    rlGoLive!!.visibility = View.VISIBLE
                                    ivMuteRemote!!.visibility = View.GONE
                                }

                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
        return false
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        Log.i(LOG_TAG, "checkSelfPermission $permission $requestCode")
        if (ContextCompat.checkSelfPermission(this,
                        permission) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode)

            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        Log.i(LOG_TAG, "onRequestPermissionsResult " + grantResults[0] + " " + requestCode)

        when (requestCode) {
            PERMISSION_REQ_ID -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                    Util.showToast(applicationContext, "Need permissions " + Manifest.permission.RECORD_AUDIO + "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    finish()
                }
                initializeAgoraEngine()
                if (!isAudience(cRole)) {

                    startPreviewScreen()
                    callLivePermissionApi()
                }
            }
        }
    }

    private fun initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(baseContext,AppConfiguration.AGORALIVESTRIMMINGID, mRtcEngineEventHandler)
            mRtcEngine!!.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
            mRtcEngine!!.enableVideo()
            mRtcEngine!!.enableDualStreamMode(true)
//            mRtcEngine!!.enableLastmileTest()

            mRtcEngine!!.setVideoEncoderConfiguration(VideoEncoderConfiguration(360, 640, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT))

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun startPreviewScreen() {

        mRtcEngine!!.setClientRole(cRole)
        ivSetting.visibility = View.VISIBLE
        mBigView = RtcEngine.CreateRendererView(this@LiveVideoActivity)
        if (mSelfView.childCount > 0)
            mSelfView.removeAllViews()
        mBigView!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        mBigView!!.setZOrderMediaOverlay(false)
        mBigView!!.setZOrderOnTop(false)
        mSelfView.addView(mBigView)

        mRtcEngine!!.setupLocalVideo(VideoCanvas(mBigView, Constants.RENDER_MODE_HIDDEN, userId))
        mRtcEngine!!.startPreview()
        rlGoLive!!.visibility = View.VISIBLE

//        initTranscoding(360, 640, 800)
//        setTranscoding()
    }

    private fun joinBroadcast(role: Int, channel: String, uid: Int) {
        mRtcEngine!!.setClientRole(role)
        mRtcEngine!!.joinChannel(null, channel, null, uid)
        rlGoLive!!.visibility = View.GONE
        val data = hashMapOf<String, Any>()
        val userVo = SPref.getInstance().getUserMasterDetail(this)
        val comment = CommentData(
                "user_Joined",
                userVo.displayname,
                userVo.photoUrl,
                Util.getCurrentdate(Constant.DATE_FROMAT_FEED))
        data["room"] = channel
        data["comment_data"] = comment

        val json = Gson().toJson(data).toString()
        CustomLog.e("audienceJoin", json)

        mSocket!!.emit("roomJoin", json)
        mSocket!!.on("userCount", onUserCount)




    }

    private fun joinChannel(channel: String, uid: Int) {
        mRtcEngine!!.joinChannel(null, channel, null, uid)

        val data = hashMapOf<String, Any>()
        data["room"] = channel

        val json = Gson().toJson(data).toString()
        CustomLog.e("hostJoin", json)

        mSocket!!.emit("roomJoin", json)
        mSocket!!.on("userCount", onUserCount)


    }

    private fun leaveChannel() {
        cvLive!!.visibility = View.GONE
        closeKeyboard()
        if (isLive) {
            val map = hashMapOf<String, Any>()
            map["room"] = "snsapp"+userIdhost
            map["isHost"] = !isAudience(cRole)

            val json = Gson().toJson(map).toString()
            CustomLog.e("roomLeave", json)

            mSocket!!.emit("roomLeave", json)
        }
        isLive = false
        findViewById<androidx.cardview.widget.CardView>(R.id.cvUserCount).visibility = View.GONE
        findViewById<ImageView>(R.id.ivPipMode).visibility = View.GONE
        if (mRtcEngine != null) {
            mRtcEngine!!.leaveChannel()
        }
        mSocket?.off("liveComment", onLiveComment)
        mSocket?.off("liveReaction", onLiveReaction)
        mSocket?.off("userCount", onUserCount)
        mMsgList!!.clear()
        mSocket?.disconnect()
    }

    private fun initMessage() {
        mMsgList = ArrayList()
        msgRecycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.msg_list)

        mMessageAdapter = MessageAdapter(this, mMsgList!!)
        mMessageAdapter!!.setHasStableIds(true)

        msgRecycler.adapter = mMessageAdapter
        val llm = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.RecyclerView.VERTICAL, false)
        llm.stackFromEnd = true

        msgRecycler.layoutManager = llm
        msgRecycler.addItemDecoration(MessageItemDecoration())
    }

    private fun callLivePermissionApi() {

        if (isNetworkAvailable(this)) {
            try {

                showBaseLoader(false)
                val request = HttpRequestVO(Constant.URL_LIVE_PERMISSION)

                request.params["privacy"] = privacy
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
                            if(result.maxStreamDurations!=0){
                                maxStreamDurations = result.maxStreamDurations
                            }

                            runOnUiThread {
                                val txt = StringBuilder()
                                if (canPost && canShareInStory)
                                    txt.append("Post, ")
                                if (canPost && !canShareInStory)
                                    txt.append("Post")
                                if (canShareInStory)
                                    txt.append("Story")

                                findViewById<AppCompatTextView>(R.id.tvPostNStory).text = txt

                                findViewById<View>(R.id.llSave).visibility = if (canSave) View.VISIBLE else View.GONE

                                findViewById<View>(R.id.rlStory).visibility = if (canShareInStory) View.VISIBLE else View.GONE

                                findViewById<View>(R.id.rlFeed).visibility = if (canPost) View.VISIBLE else View.GONE
                            }

                        } else {
                            Util.showSnackbar(mSelfView, resp.errorMessage)
                            Handler().postDelayed({ onBackPressed() }, 1000)
                        }

                    } catch (e: Exception) {
                        CustomLog.e(e)
                        Util.showSnackbar(mSelfView, getString(R.string.msg_something_wrong))
                    }
                    true
                }
                HttpRequestHandler(this, Handler(callback)).run(request)

            } catch (e: Exception) {
                CustomLog.e(e)
            }

        } else {
            Util.showSnackbar(mSelfView, getString(R.string.MSG_NO_INTERNET))
        }
    }

    private fun initTranscoding(width: Int, height: Int, bitrate: Int) {
        if (mLiveTranscoding == null) {
            mLiveTranscoding = LiveTranscoding()
            mLiveTranscoding!!.width = width
            mLiveTranscoding!!.height = height
            mLiveTranscoding!!.videoBitrate = bitrate
            // if you want high fps, modify videoFramerate
            mLiveTranscoding!!.videoFramerate = 15
        }
    }

    private fun sendMsg(msg: CommentData) {
        runOnUiThread {
            mMsgList!!.add(msg)
            if (mMsgList!!.size > 0)
                msgRecycler.visibility = View.VISIBLE
            else
                msgRecycler.visibility = View.GONE

            if (mMsgList!!.size > 20) {
                val remove = mMsgList!!.size - 20
                for (i in 0 until remove) {
                    mMsgList!!.removeAt(i)
                }
            }
            mMessageAdapter!!.notifyDataSetChanged()
            msgRecycler.smoothScrollToPosition(mMsgList!!.size - 1)
        }
    }

    private fun showCommnet(comment: CommentData) {
        runOnUiThread {
            commentList!!.add(comment)
            if (mMsgList!!.size > 0)
                msgRecycler.visibility = View.VISIBLE
            else
                msgRecycler.visibility = View.GONE

            if (mMsgList!!.size > 20) {
                val remove = mMsgList!!.size - 20
                for (i in 0 until remove) {
                    mMsgList!!.removeAt(i)
                }
            }
            mMessageAdapter!!.notifyDataSetChanged()
            msgRecycler.smoothScrollToPosition(mMsgList!!.size - 1)
        }
    }

    private fun setTranscoding() {

        try {
            val transcodingUsers: ArrayList<LiveTranscoding.TranscodingUser>
            val videoUsers = getAllVideoUser(mUserInfo)

            transcodingUsers = cdnLayout(userId, videoUsers, mLiveTranscoding!!.width, mLiveTranscoding!!.height)

            mLiveTranscoding!!.users = transcodingUsers
            mRtcEngine!!.setLiveTranscoding(mLiveTranscoding)

            CustomLog.e("last user-->", "" + mLiveTranscoding!!.users[mLiveTranscoding!!.getUserCount() - 1].uid)
            CustomLog.e("total users-->  ", "" + mLiveTranscoding!!.users.size)

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    fun onLocalAudioMuteClicked(view: View) {
        val iv = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        }

        mRtcEngine!!.muteLocalAudioStream(iv.isSelected)
    }

    fun onRemoteAudioMuteClicked(view: View) {
        val iv = view as ImageView
        if (iv.isSelected) {
            iv.isSelected = false
            iv.clearColorFilter()
        } else {
            iv.isSelected = true
            iv.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY)
        }

        mRtcEngine!!.muteAllRemoteAudioStreams(iv.isSelected)
    }

    fun onSwitchCameraClicked(view: View) {
        mRtcEngine!!.switchCamera()
//        view.rotation = view.rotation + 90
    }

    override fun onClick(v: View?) {
        try {
            when (v?.id) {
                R.id.ivBack -> onBackPressed()
                R.id.llPrivacy -> {
                    val intent = Intent(this, LiveVideoSetting::class.java)
                    intent.putExtra("privacy", privacy)
                    intent.putExtra("save", canSave)
                    intent.putExtra("story", canShareInStory)
                    intent.putExtra("feed", canPost)
                    startActivityForResult(intent, REQ_LIVE_SETTING)
                }
                R.id.mcvGoLive -> onGoLiveClicked()
                R.id.mcvGoOwnLive -> {
                    llGoOwnLive!!.visibility = View.GONE
                    onGoLiveClicked()
                }
                R.id.tvEnd -> showEndVideoDialog()

            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQ_LIVE_SETTING && resultCode == Activity.RESULT_OK && data != null) {
            try {
                when (data.getStringExtra("privacy")) {
                    "everyone" -> {
                        tvViewPrivacy.text = getString(R.string.privacy_everyone)
                        privacy = "everyone"
                    }
                    "networks" -> {
                        tvViewPrivacy.text = getString(R.string.privacy_network)
                        privacy = "networks"
                    }
                    "friends" -> {
                        tvViewPrivacy.text = getString(R.string.privacy_friends_only)
                        privacy = "friends"
                    }
                    "onlyme" -> {
                        tvViewPrivacy.text = getString(R.string.privacy_only_me)
                        privacy = "onlyme"
                    }
                }
                data.getStringExtra("privacy")?.let { setPrivacyImage(it) }

                canSave = data.getBooleanExtra("save", true)
                canShareInStory = data.getBooleanExtra("story", true)
                canPost = data.getBooleanExtra("feed", true)

                runOnUiThread {

                    val txt = StringBuilder()
                    if (canPost && canShareInStory)
                        txt.append("Post, ")
                    if (canPost && !canShareInStory)
                        txt.append("Post")
                    if (canShareInStory)
                        txt.append("Story")
                    findViewById<AppCompatTextView>(R.id.tvPostNStory).text = txt

                    findViewById<View>(R.id.llSave).visibility = if (canSave) View.VISIBLE else View.GONE

                    findViewById<View>(R.id.rlStory).visibility = if (canShareInStory) View.VISIBLE else View.GONE

                    findViewById<View>(R.id.rlFeed).visibility = if (canPost) View.VISIBLE else View.GONE
                }

            } catch (e: Exception) {
                CustomLog.e(e)
            }
        }
    }

    private fun setPrivacyImage(resName: String) {
        try {
            val id = this.resources.getIdentifier("privacy_$resName", "drawable", this.packageName)
            if (id > 0)
                ivPrivacyImage.setImageResource(id)
            else
                ivPrivacyImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.city))
        } catch (e: Exception) {
            ivPrivacyImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.privacy_everyone))
        }
    }

    override fun onItemClicked(eventType: Int?, data: Any?, position: Int): Boolean {
        try {
            when (eventType) {
                Constant.Events.COMMENT -> {
                    val params = HashMap<String, Any>()
                    val message = data as String

                    if (!TextUtils.isEmpty(message)) {
                        params["body"] = message
                    }
                    callCreateCommentApi(params)
                }

                Constant.Events.MUSIC_LIKE -> {
                    val reactionVo = reactionList?.get(position)
                    val map = hashMapOf<String, Any>()
                    map["room"] ="snsapp"+userIdhost
                    map["message"] = reactionVo!!.image

                    val json = Gson().toJson(map).toString()
                    CustomLog.e("reaction emited", json)

                    mSocket!!.emit("liveReaction", json)
                    callLikeApi(reactionVo.reactionId, position)
                }

                REQ_STREAM_LIKE -> {
                    val res = Gson().fromJson("" + data, FeedLikeResponse::class.java)
                    if (res.isSuccess)
                        flyEmoji(res.result.like!!.image)
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
        return false
    }

    fun onShowTimeClicked(view: View) {
        try {
            val ll = view as androidx.cardview.widget.CardView
            if (ll.isSelected) {
                ll.isSelected = false
                findViewById<AppCompatTextView>(R.id.tvTotalTime).visibility = View.GONE
            } else {
                ll.isSelected = true
                findViewById<AppCompatTextView>(R.id.tvTotalTime).visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun callStartRecording() {
        if (isNetworkAvailable(this)) {
            try {
                val request = HttpRequestVO(AppConfiguration.LINUX_BASE_URL + Constant.URL_START_RECORDING)
                request.params["appid"] = AppConfiguration.AGORALIVESTRIMMINGID
                request.params["channel"] = "snsapp"+userIdhost
                request.params["privacy"] = privacy
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(this)
                request.headres[Constant.KEY_COOKIE] = cookie
                request.requestMethod = HttpPost.METHOD_NAME

                val callback = Handler.Callback { msg ->
                    try {
                        CustomLog.e("response_startRecording", "" + msg.obj)
                        val response = "" + msg.obj

                        val resp = Gson().fromJson(response, StreamingResponse::class.java)
                        if (null != resp) {
                            result = resp.result!!
                            if (result.success!!) {
                                sid = result.sid
                                isServerReady = true
                            } else
                                sid = Constant.EMPTY
                            CustomLog.e("sid", sid)
                        } else
                            isServerReady = false

                    } catch (e: Exception) {
                        CustomLog.e(e)
                        Util.showSnackbar(rlGoLive, getString(R.string.msg_something_wrong))
                    }
                    true
                }
                HttpRequestHandler(this, Handler(callback)).run(request)

            } catch (e: HttpHostConnectException) {
                isServerReady = false
                CustomLog.e("connection error", e.printStackTrace().toString())
            } catch (e: Exception) {
                CustomLog.e("connection error1", e.printStackTrace().toString())
                isServerReady = false
            }

        } else {
            Util.showSnackbar(rlGoLive, getString(R.string.MSG_NO_INTERNET))
        }
    }

    private fun lastMileTest() {
        val config = LastmileProbeConfig()
        // Probe the uplink network quality.
        config.probeUplink = true
        // Probe the downlink network quality.
        config.probeDownlink = true
        // The expected uplink bitrate (Kbps). The value range is [100, 5000].
        config.expectedUplinkBitrate = 1000
        // The expected downlink bitrate (Kbps). The value range is [100, 5000].
        config.expectedDownlinkBitrate = 1000
        // Start the last-mile network test before joining the channel.
        mRtcEngine!!.startLastmileProbeTest(config)
    }

    private fun onGoLiveClicked() {
        try {
            closeKeyboard()
            mRtcEngine!!.stopPreview()

//        runBlocking {
//            findViewById<AppCompatTextView>(R.id.tvProgress).visibility = View.VISIBLE
//            findViewById<AppCompatTextView>(R.id.tvProgress).text = "Checking network connection"
//            lastMileTest()
//        }

//        if (canGoLive) {
            findViewById<AppCompatTextView>(R.id.tvProgress).visibility = View.VISIBLE
            findViewById<AppCompatTextView>(R.id.tvProgress).text = "Starting your Live Video"
            Handler().postDelayed({ findViewById<AppCompatTextView>(R.id.tvProgress).visibility = View.GONE }, 1000)

            callStartRecording()
            Handler().postDelayed({
                if (isServerReady) {
                    isLive = true
                    cRole = Constants.CLIENT_ROLE_BROADCASTER
                    mRtcEngine!!.setClientRole(cRole)
                    joinChannel("snsapp"+userIdhost, userId)
                    setupLocalVideo(userId)
                    showHostScreen()

                    startTimer()
                    tvProgress.visibility = View.VISIBLE
                    tvProgress.setTypeface(tvProgress.typeface, Typeface.BOLD)

                    if(maxStreamDurations!=1000){
                        tvProgress.text = "You can stay live for $maxStreamDurations minutes"
                    }else{
                        tvProgress.text = "You can live"
                    }

                    Handler().postDelayed({
                        tvProgress.visibility = View.GONE
                        tvProgress.setTypeface(tvProgress.typeface, Typeface.NORMAL)
                    }, 2000)
                } else {
                    tvProgress.visibility = View.VISIBLE
                    tvProgress.text = getString(R.string.not_ready)
                    Handler().postDelayed({
                        tvProgress.visibility = View.GONE
                        mRtcEngine?.startPreview()
                    }, 2000)
                }
            }, 2000)
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }



    private fun startTimer() {
        timer = object : CountDownTimer((maxStreamDurations * 60 * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {

//                CustomLog.e("seconds remaining : --->", "   " + millisUntilFinished / 1000)

            }

            override fun onFinish() {

                Util.showSnackbar(mSelfView, getString(R.string.duration_end))
                onEndCallClicked()
            }
        }
        timer?.start()
    }

    private fun notifyUsers() {

        if (isNetworkAvailable(this)) {
            try {
                val request = HttpRequestVO(Constant.URL_NOTIFY_USERS)

                request.params["privacy"] = privacy
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(this)
                request.headres[Constant.KEY_COOKIE] = cookie
                request.requestMethod = HttpPost.METHOD_NAME

                val callback = Handler.Callback { msg ->
                    try {
                        CustomLog.e("response_notify", "" + msg.obj)
                        val response = "" + msg.obj

                        val resp = Gson().fromJson(response, StreamingResponse::class.java)
                        if (resp.error.isNullOrEmpty()) {

                            result = resp.result!!
                            eliveHostId = result.eliveHostId!!
                            userIdhost = result.eliveHostId!!
                            activityId = result.activity?.actionId!!

                        } else
                            Util.showSnackbar(mSelfView, resp.errorMessage)

                    } catch (e: Exception) {
                        CustomLog.e(e)
                        Util.showSnackbar(rlGoLive, getString(R.string.msg_something_wrong))
                    }
                    true
                }
                HttpRequestHandler(this, Handler(callback)).run(request)

            } catch (e: Exception) {
                CustomLog.e(e)
            }

        } else {
            Util.showSnackbar(rlGoLive, getString(R.string.MSG_NO_INTERNET))
        }
    }

    private fun showEndVideoDialog() {
        try {
            if (null != progressDialog && progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            progressDialog = ProgressDialog.show(this, "", "", true)
            progressDialog.setCanceledOnTouchOutside(true)
            progressDialog.setCancelable(true)
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog.setContentView(R.layout.dialog_end_video)
            ThemeManager().applyTheme(progressDialog.findViewById<View>(R.id.rlDialogMain) as ViewGroup, this)

            progressDialog.findViewById<View>(R.id.end).setOnClickListener {
                progressDialog.dismiss()
                onEndCallClicked()
            }
            progressDialog.findViewById<View>(R.id.cancel).setOnClickListener { progressDialog.dismiss() }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun showDialogOnFail(message: String) {
        try {
            if (null != progressDialog && progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            progressDialog = ProgressDialog.show(this, "", "", true)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setCancelable(false)
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog.setContentView(R.layout.dialog_end_video)
            ThemeManager().applyTheme(progressDialog.findViewById<View>(R.id.rlDialogMain) as ViewGroup, this)

            progressDialog.findViewById<View>(R.id.llMsg).visibility = View.VISIBLE
            progressDialog.findViewById<AppCompatTextView>(R.id.title).text = message

            val relive = progressDialog.findViewById<AppCompatTextView>(R.id.end)
            relive.text = getString(R.string.live_again)

            progressDialog.findViewById<View>(R.id.end).setOnClickListener {
                progressDialog.dismiss()
                onGoLiveClicked()
            }
            progressDialog.findViewById<View>(R.id.cancel).setOnClickListener {
                progressDialog.dismiss()
                rlGoLive!!.visibility = View.VISIBLE
                mcvGoLive!!.visibility = View.VISIBLE
                cvLive!!.visibility = View.GONE
                findViewById<androidx.cardview.widget.CardView>(R.id.cvUserCount).visibility = View.GONE
                ivBack!!.visibility = View.VISIBLE
                ivSetting.visibility = View.VISIBLE
                vScrim!!.visibility = View.GONE
                mRtcEngine!!.startPreview()
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun showDialogDiscard(message: String, ok: String) {
        try {
            if (null != progressDialog && progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            progressDialog = ProgressDialog.show(this, "", "", true)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setCancelable(false)
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog.setContentView(R.layout.dialog_end_video)
            ThemeManager().applyTheme(progressDialog.findViewById<View>(R.id.rlDialogMain) as ViewGroup, this)

            progressDialog.findViewById<View>(R.id.llMsg).visibility = View.VISIBLE
            progressDialog.findViewById<AppCompatTextView>(R.id.title).text = message

            val relive = progressDialog.findViewById<AppCompatTextView>(R.id.end)
            relive.text = ok

            progressDialog.findViewById<View>(R.id.end).setOnClickListener {
                findViewById<LinearLayout>(R.id.llDelete).isEnabled = false
                findViewById<LinearLayout>(R.id.llDelete).alpha = 0.5f
                actionPerformed = true
                scTogglePost.isEnabled = false
                scToggleStory.isEnabled = false
                scTogglePost.alpha = 0.6f
                scToggleStory.alpha = 0.6f
                findViewById<MaterialCardView>(R.id.mcvPost).isEnabled = false
                findViewById<MaterialCardView>(R.id.mcvPost).alpha = 0.4f
                findViewById<LinearLayout>(R.id.llSave).isEnabled = false
                findViewById<LinearLayout>(R.id.llSave).background = ContextCompat.getDrawable(this, R.drawable.rounded_filled_grey_16)

                progressDialog.dismiss()
                callClearLiveStream()
                onBackPressed()
            }
            progressDialog.findViewById<View>(R.id.cancel).setOnClickListener {
                progressDialog.dismiss()
                actionPerformed = false

            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun onEndCallClicked() {
        try {
            mRtcEngine!!.startPreview()
            leaveChannel()
            showProgressBar("Ending Live Video")
            tvProgress.visibility = View.VISIBLE
            tvProgress.text = getString(R.string.stream_ending)
            callStopRecording()
            rlGoLive!!.visibility = View.GONE
            llAudienceAction!!.visibility = View.GONE
            tvEnd.visibility = View.GONE
            msgRecycler.visibility = View.GONE
            mMsgList!!.clear()
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    fun onSaveVideo(view: View) {
        try {
            webView.setDownloadListener(VideoDownloadController(this))
            webView.webChromeClient = WebChromeClient()
            webView.setBackgroundColor(Color.BLACK)
            webView.webViewClient = InsideWebViewClient()
            webView.loadUrl(AppConfiguration.LINUX_BASE_URL + "download/$sid/$sid.mp4")

            findViewById<TextView>(R.id.tvSave).text = "Saved"
            findViewById<LinearLayout>(R.id.llSave).isEnabled = false
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    fun onDeleteVideo(view: View) {
        showDialogDiscard(getString(R.string.msg_delete_live), getString(R.string.delete))
    }

    fun onPostActionClick(view: View) {
        actionPerformed = true
        llPostResult!!.visibility = View.GONE
        cvLive!!.visibility = View.GONE
        findViewById<androidx.cardview.widget.CardView>(R.id.cvUserCount).visibility = View.GONE
        vScrim!!.visibility = View.GONE
        mRtcEngine!!.startPreview()
        closeKeyboard()
        callShareLiveVideo()
        ivMuteRemote!!.visibility = View.GONE
    }

    private fun showHostScreen() {
        mcvGoLive!!.visibility = View.GONE
        tvEnd.visibility = View.VISIBLE
        llAudienceAction!!.visibility = View.VISIBLE
        rlGoLive!!.visibility = View.VISIBLE
        ivBack!!.visibility = View.GONE
        ivSetting.visibility = View.GONE
    }

    private fun showAuidenceScreen() {

        mcvGoLive!!.visibility = View.GONE
        tvEnd.visibility = View.GONE
        llAudienceAction!!.visibility = View.VISIBLE
        rlGoLive!!.visibility = View.GONE
        ivBack!!.visibility = View.GONE
        ivSetting.visibility = View.GONE
        ivMuteRemote!!.visibility = View.VISIBLE
    }

    private fun callStopRecording() {

        if (null != timer)
            timer!!.cancel()

        if (isNetworkAvailable(this)) {
            try {

                val request = HttpRequestVO(AppConfiguration.LINUX_BASE_URL + Constant.URL_STOP_RECORDING)
                request.params[Constant.KEY_SID] = sid
                request.params[Constant.KEY_ELIVEHOST_ID] = eliveHostId
                request.params["cancel_url"] = Constant.URL_STREAMING_CANCEL
                request.params["change_status_url"] = Constant.URL_CHANGE_STATUS
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(this)
                request.headres[Constant.KEY_COOKIE] = cookie
                request.requestMethod = HttpPost.METHOD_NAME

                val callback = Handler.Callback { msg ->
                    try {
                        CustomLog.e("response_stop", "" + msg.obj)
                        hideBaseLoader()
                        tvProgress.visibility = View.GONE
                        val response = "" + msg.obj

                        val resp = Gson().fromJson(response, StreamingResponse::class.java)
                        if (null != resp) {
                            result = resp.result!!

                            if (result.success!!) {
                                llPostResult!!.visibility = View.VISIBLE
                                isStoped = true
                                if (!canPost && !canShareInStory) {
                                    findViewById<MaterialCardView>(R.id.mcvPost).isEnabled = false
                                    findViewById<MaterialCardView>(R.id.mcvPost).alpha = 0.4f
                                } else {
                                    findViewById<MaterialCardView>(R.id.mcvPost).isEnabled = true
                                    findViewById<MaterialCardView>(R.id.mcvPost).alpha = 1f
                                }
                            } else {
                                callClearLiveStream()
                                showDialogOnFail("Streamed Video not saved")
                            }
                        } else {
                            callClearLiveStream()
                            showDialogOnFail(getString(R.string.error_occured))
                        }
                    } catch (e: Exception) {
                        CustomLog.e(e)
                        callClearLiveStream()
                        Snackbar.make(mSelfView, getString(R.string.msg_something_wrong), Snackbar.LENGTH_INDEFINITE).also { snackbar ->
                            snackbar.setAction("RELIVE") {
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(1000)
                                    onGoLiveClicked()
                                }
                            }
                        }.show()
                    }
                    true
                }
                HttpRequestHandler(this, Handler(callback)).run(request)

            } catch (e: Exception) {
                CustomLog.e(e)
            }

        } else {
            Util.showSnackbar(rlGoLive, getString(R.string.MSG_NO_INTERNET))
        }
    }

    private fun callShareLiveVideo() {

        if (isNetworkAvailable(this)) {
            try {
                showBaseLoader(false)
                val request = HttpRequestVO(AppConfiguration.LINUX_BASE_URL + Constant.URL_SHARE_LIVE_VIDEO)
                request.params["sid"] = sid
                request.params["canShareInStory"] = canShareInStory
                request.params["canPost"] = canPost
                request.params["post_feed_url"] = Constant.URL_POST_FEED.toString()
                request.params["stories_create_url"] = Constant.URL_STORY_CREATE.toString()
                request.params["cancel_url"] = Constant.URL_STREAMING_CANCEL.toString()
                request.params["privacy"] = privacy
                request.params[Constant.KEY_ELIVEHOST_ID] = eliveHostId
                request.params[Constant.KEY_USER_ID] = SPref.getInstance().getLoggedInUserId(this)
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(this)
                request.headres[Constant.KEY_COOKIE] = cookie
                request.requestMethod = HttpPost.METHOD_NAME

                val callback = Handler.Callback { msg ->
                    try {
                        CustomLog.e("response_share", "" + msg.obj)
                        val response = "" + msg.obj
                        val resp = Gson().fromJson(response, StreamingResponse::class.java)
                        if (null != resp) {
                            result = resp.result!!
                            tvProgress.visibility = View.VISIBLE
                            tvProgress.text = result.message
                        } else {
                            Util.showSnackbar(mSelfView, getString(R.string.msg_something_wrong))
                            callClearLiveStream()
                        }
                        rlGoLive!!.visibility = View.VISIBLE
                        mcvGoLive!!.visibility = View.VISIBLE
                        ivBack!!.visibility = View.VISIBLE
                        ivSetting.visibility = View.VISIBLE
                        vScrim!!.visibility = View.GONE
                        mRtcEngine!!.startPreview()

                        Handler().postDelayed({
                            tvProgress.visibility = View.GONE
                            hideBaseLoader()
                        }, 2000)

                    } catch (e: Exception) {
                        CustomLog.e(e)
                        Util.showSnackbar(rlGoLive, getString(R.string.msg_something_wrong))
                        hideBaseLoader()
                    }
                    true
                }
                HttpRequestHandler(this, Handler(callback)).run(request)

            } catch (e: Exception) {
                CustomLog.e(e)
                hideBaseLoader()
            }
        } else {
            Util.showSnackbar(rlGoLive, getString(R.string.MSG_NO_INTERNET))
        }
    }

    private fun callCheckStreamingStatus(activityId: Int) {

        if (isNetworkAvailable(this)) {
            try {
                showBaseLoader(false)

                findViewById<AppCompatTextView>(R.id.tvProgress).visibility = View.VISIBLE
                findViewById<AppCompatTextView>(R.id.tvProgress).text = getString(R.string.join_stream)
                val request = HttpRequestVO(Constant.URL_STREAMING_STATUS)
                request.params[Constant.KEY_ACTION_ID] = activityId
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(this)
                request.requestMethod = HttpPost.METHOD_NAME

                val callback = Handler.Callback { msg ->
                    try {
                        hideBaseLoader()
                        CustomLog.e("response_streaming_status", "" + msg.obj)
                        val response = "" + msg.obj

                        val resp = Gson().fromJson(response, StreamingResponse::class.java)
                        if (null != resp) {
                            if (resp.error.isNullOrEmpty()) {
                                result = resp.result!!
                                val message = result.message
                                when (result.status) {
                                    "started" -> {
                                        canJoin = true
//                                    if (type.equals(Constant.ResourceType.VIDEO_FEED_GO_LIVE)) {
//                                        findViewById<AppCompatTextView>(R.id.tvProgress).text = "You will get video shortly!"
//                                        onBackPressed()
//                                    }
                                        userIdhost= resp.result.eliveHostId!!;
                                        if (isAudience(cRole) && canJoin)
                                            joinBroadcast(cRole, "snsapp"+userIdhost, userId)
                                        findViewById<AppCompatTextView>(R.id.tvProgress).text = message
                                    }
                                    "processing" -> {
                                        canJoin = false
                                        findViewById<AppCompatTextView>(R.id.tvProgress).text = message
                                        Handler().postDelayed({ onBackPressed() }, 2000)
                                    }
                                    "completed" -> {
                                        canJoin = false
                                        findViewById<AppCompatTextView>(R.id.tvProgress).text = message
                                        Handler().postDelayed({

                                            if (null != result.videoId) {
                                                val intent = Intent(this, VideoViewActivity::class.java)
                                                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.VIDEO)
                                                intent.putExtra(Constant.KEY_ID, result.videoId!!)
                                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                startActivity(intent)
                                                finish()
                                            }
                                            onBackPressed()
//                                        else{
//
//                                            val intent = Intent(this, StoryPlayer::class.java)
//                                            //String model = new Gson().toJson(value);
//                                            intent.putExtra(Constant.STORY_IMAGE_KEY, Gson().toJson(value))
//                                            intent.putExtra(Constant.KEY_POSITION, postion)
//                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                            startActivity(intent)
//                                            finish()
//                                        }
                                        }, 1000)

                                    }
                                    "deleted" -> {
                                        canJoin = false
                                        findViewById<AppCompatTextView>(R.id.tvProgress).text = message
                                        Handler().postDelayed({ onBackPressed() }, 1000)
                                    }
                                }
                                if (null != result.eliveHostId)
                                    eliveHostId = result.eliveHostId!!

                                Handler().postDelayed({ findViewById<AppCompatTextView>(R.id.tvProgress).visibility = View.GONE }, 2000)
                            } else {
                                Util.showSnackbar(rlGoLive, resp.errorMessage)
//                            goIfPermissionDenied(resp.error)
                            }
                        } else {
                            Util.showSnackbar(mSelfView, getString(R.string.msg_something_wrong))
                            onBackPressed()
                        }
                    } catch (e: Exception) {
                        CustomLog.e(e)
                        Util.showSnackbar(rlGoLive, getString(R.string.msg_something_wrong))
                    }
                    true
                }
                HttpRequestHandler(this, Handler(callback)).run(request)

            } catch (e: Exception) {
                CustomLog.e(e)
            }

        } else {
            Util.showSnackbar(rlGoLive, getString(R.string.MSG_NO_INTERNET))
        }
    }

    private fun callClearLiveStream() {

        if (isNetworkAvailable(this)) {
            try {
                val request = HttpRequestVO(Constant.URL_STREAMING_CANCEL)
                request.params[Constant.KEY_ELIVEHOST_ID] = eliveHostId
                request.params["canShareInStory"] = false
                request.params["canPost"] = false
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(this)
                request.requestMethod = HttpPost.METHOD_NAME

                val callback = Handler.Callback { msg ->
                    try {
                        CustomLog.e("response_clear", "" + msg.obj)
                        val response = "" + msg.obj
                        val resp = Gson().fromJson(response, StreamingResponse::class.java)
                        if (null != resp)
                            result = resp.result!!

                    } catch (e: Exception) {
                        CustomLog.e(e)
                        rlGoLive!!.visibility = View.VISIBLE
                        mcvGoLive!!.visibility = View.VISIBLE
                    }
                    true
                }
                HttpRequestHandler(this, Handler(callback)).run(request)

            } catch (e: Exception) {
                CustomLog.e(e)
            }
        } else {
            Util.showSnackbar(rlGoLive, getString(R.string.MSG_NO_INTERNET))
        }
    }

    override fun onBackPressed() {
        if (isLive && !isAudience(cRole))
            showEndVideoDialog()
        else if (isStoped && !actionPerformed)
            showDialogDiscard(getString(R.string.discard_stream), getString(R.string.txt_discard))
        else
            finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mRtcEngine != null) {
            mRtcEngine!!.leaveChannel()
        }
        RtcEngine.destroy()
        mRtcEngine = null
        if (isLive) {
            val map = hashMapOf<String, Any>()
            map["room"] = "snsapp"+userIdhost
            map["isHost"] = !isAudience(cRole)

            val json = Gson().toJson(map).toString()
            CustomLog.e("roomleave", json)
            mSocket!!.emit("roomLeave", json)
        }
        mSocket?.off("liveComment", onLiveComment)
        mSocket?.off("liveReaction", onLiveReaction)
        mSocket?.off("userCount", onUserCount)
        mSocket?.disconnect()
        if (null != timer)
            timer!!.cancel()
    }

    private fun setupLocalVideo(uid: Int) {
        try {
            runOnUiThread {
                if (mSelfView.childCount > 0)
                    mSelfView.removeAllViews()
                val mSurfaceView = RtcEngine.CreateRendererView(baseContext)
                mSurfaceView!!.setZOrderMediaOverlay(true)
                mSurfaceView.setZOrderOnTop(false)
                mSelfView.addView(mSurfaceView)

                mRtcEngine!!.setupLocalVideo(VideoCanvas(mSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    findViewById<ImageView>(R.id.ivPipMode).visibility = View.VISIBLE
                else
                    findViewById<ImageView>(R.id.ivPipMode).visibility = View.GONE

                ivMuteRemote!!.visibility = View.GONE
                cvLive!!.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun setupRemoteVideo(uid: Int) {

        runOnUiThread(Runnable {
            if (isFinishing) {
                return@Runnable
            }
            rlGoLive!!.visibility = View.GONE
            ivBack!!.visibility = View.GONE
            ivSetting.visibility = View.GONE
            tvEnd.visibility = View.GONE
            llAudienceAction!!.visibility = View.VISIBLE
            cvLive!!.visibility = View.VISIBLE
            ivMuteRemote!!.visibility = View.VISIBLE
            if (mSelfView.childCount >= 1) {
                mSelfView.removeAllViews()
            }
            val surfaceView = RtcEngine.CreateRendererView(baseContext)
            mSelfView.addView(surfaceView)
            mRtcEngine!!.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                findViewById<ImageView>(R.id.ivPipMode).visibility = View.VISIBLE
            else
                findViewById<ImageView>(R.id.ivPipMode).visibility = View.GONE

            surfaceView.tag = uid // for mark purpose
        })
    }

    private fun onRemoteUserLeft() {
        try {
            mSelfView.removeAllViews()
            ivBack!!.visibility = View.VISIBLE
            llAudienceAction!!.visibility = View.GONE
            llGoOwnLive!!.visibility = View.VISIBLE
            msgRecycler.visibility = View.GONE
            ivMuteRemote!!.visibility = View.GONE
            rlGoLive!!.visibility = View.GONE
            leaveChannel()

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    fun flyEmoji(resId: String) {
        val animation = ZeroGravityAnimation()
        animation.setCount(1)
        animation.setScalingFactor(1f)
        animation.setOriginationDirection(Direction.BOTTOM)
        animation.setDestinationDirection(Direction.TOP)
        animation.setImageUrl(resId)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }
            override fun onAnimationEnd(animation: Animation) {
            }
            override fun onAnimationRepeat(animation: Animation) {
            }
        })
        val container = findViewById<ViewGroup>(R.id.activity_video_live)
        animation.play(this, container)
    }


    private fun callLikeApi(reactionId: Int, position: Int) {
        if (isNetworkAvailable(this)) {
            val map = HashMap<String, Any>()
            map[Constant.KEY_TYPE] = reactionId
            map[Constant.KEY_ACTIVITY_ID] = activityId
            map[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(this)
            ApiController(Constant.URL_LIKE_COMMENT, map, this, this, REQ_STREAM_LIKE).setExtraKey(position).execute()
        }
    }

    private fun callCreateCommentApi(params: Map<String, Any>) {

        if (isNetworkAvailable(this)) {
            val isDummyCommentAdded = booleanArrayOf(false)
            try {
                if (params.containsKey(Constant.KEY_BODY)) {
                    val userVo = SPref.getInstance().getUserMasterDetail(this)
//                    isDummyCommentAdded[0] = true
//                    commentList.add(0, CommentData(
//                            params["body"] as String?,
//                            userVo.displayname,
//                            userVo.photoUrl,
//                            Util.getCurrentdate(Constant.DATE_FROMAT_FEED)))
//                    updateFeelingAdapter()
//                    sendMsg((params["body"] as String) + "--dummy")
                } else {
                    showBaseLoader(true)
                }

                val request = HttpRequestVO(Constant.URL_CREATE_COMMENT)

                request.params.putAll(params)
                request.params[Constant.KEY_RESOURCE_ID] = activityId
                request.params[Constant.KEY_ACTIVITY_ID] = activityId
                request.params[Constant.KEY_RESOURCES_TYPE] = Constant.ResourceType.ACTIVITY_ACTION
//                if (null != guid) {
//                    request.params[Constant.KEY_GUID] = activityId
//                }
                request.headres[Constant.KEY_COOKIE] = cookie
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(this)
                request.requestMethod = HttpPost.METHOD_NAME

                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = "" + msg.obj
//                        isLoading = false
                        CustomLog.e("response_comment", "" + response)
                        commentList = ArrayList()
                        val comResp = Gson().fromJson(response, BaseResponse::class.java)
                        //  result = comResp.getResult();



                        if (TextUtils.isEmpty(comResp.error)) {

//                            attachmentList.clear()
//                            updateImageAttachAdapter()
                            val itemComment = JSONObject(response).getJSONObject("result").getJSONObject("comment_data").toString()
                            val vo = Gson().fromJson(itemComment, CommentData::class.java)



                            val data = hashMapOf<String, Any>()
                            data["room"] = "snsapp"+userIdhost
                            data["message"] = vo

                            val json = Gson().toJson(data)
                            CustomLog.e("comment emitted", json)

                            mSocket!!.emit("liveComment", json)

                            mMessageAdapter!!.notifyDataSetChanged()
                            if (mMsgList!!.size > 0)
                                msgRecycler.smoothScrollToPosition(mMsgList!!.size - 1)
//                            }
                        } else {
                            Util.showSnackbar(rlGoLive, comResp.errorMessage)
                        }

                    } catch (e: Exception) {
                        hideBaseLoader()

                        CustomLog.e(e)
                    }
                    true
                }
                HttpImageRequestHandler(this, Handler(callback)).run(request)

            } catch (e: Exception) {
                hideBaseLoader()
            }

        } else {
            Util.showSnackbar(rlGoLive, getString(R.string.MSG_NO_INTERNET))
        }
    }

    companion object {
        private val LOG_TAG = LiveVideoActivity::class.java.simpleName

        private const val PERMISSION_REQ_ID = 21

        // permission WRITE_EXTERNAL_STORAGE is not mandatory for Agora RTC SDK, just incase if you wanna save logs to external sdcard
        private val REQUESTED_PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        fun getAllVideoUser(userInfo: Map<Int, UserInfo>): ArrayList<UserInfo> {
            val users = ArrayList<UserInfo>()
            val iterator = userInfo.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val user = entry.value
                users.add(user)
            }
            return users
        }

        fun cdnLayout(bigUserId: Int, publishers: ArrayList<UserInfo>,
                      canvasWidth: Int,
                      canvasHeight: Int): ArrayList<LiveTranscoding.TranscodingUser> {

            val users: ArrayList<LiveTranscoding.TranscodingUser> = ArrayList(publishers.size)
            var index = 0
            var xIndex: Float
            var yIndex: Float
            val viewWidth = if (publishers.size <= 1)
                canvasWidth
            else
                canvasWidth / 2
            val viewHEdge = if (publishers.size <= 2)
                canvasHeight
            else
                canvasHeight / ((publishers.size - 1) / 2 + 1)

            val user0 = LiveTranscoding.TranscodingUser()
            user0.uid = bigUserId
            user0.alpha = 1f
            user0.zOrder = 0
            user0.audioChannel = 0

            user0.x = 0
            user0.y = 0
            user0.width = viewWidth
            user0.height = viewHEdge
            users.add(user0)

            index++
            for (entry in publishers) {
                if (entry.uid == bigUserId)
                    continue

                xIndex = (index % 2).toFloat()
                yIndex = (index / 2).toFloat()
                val tmpUser = LiveTranscoding.TranscodingUser()
                tmpUser.uid = entry.uid
                tmpUser.x = (xIndex * viewWidth).toInt()
                tmpUser.y = (viewHEdge * yIndex).toInt()
                tmpUser.width = viewWidth
                tmpUser.height = viewHEdge
                tmpUser.zOrder = index + 1
                tmpUser.audioChannel = 0
                tmpUser.alpha = 1f

                users.add(tmpUser)
                index++
            }

            return users
        }
    }

    fun onPipModeClicked(view: View) {
        if (isLive) {
            if (view.id == R.id.ivPipMode) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    this.enterPictureInPictureMode()
                }
//                else {
//                    Snackbar.make(mSelfView, "Your device hasn't support of PIP Mode", Snackbar.LENGTH_INDEFINITE).also { snackbar ->
//                        snackbar.setAction("DISMISS") { snackbar.dismiss() }
//                    }.show()
//                }
                return
            }
        }
    }

    override fun onUserLeaveHint() {
        if (isLive) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this.enterPictureInPictureMode()
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        if (isInPictureInPictureMode) {
            // Hide the full-screen UI (controls, etc.) while in picture-in-picture mode.
            ivBack!!.visibility = View.GONE
            cvLive!!.visibility = View.GONE
            tvEnd.visibility = View.GONE
            msgRecycler.visibility = View.GONE
            llAudienceAction!!.visibility = View.GONE
            rlGoLive!!.visibility = View.GONE
            ivMuteRemote!!.visibility = View.GONE
            ivSetting.visibility = View.GONE
            findViewById<ImageView>(R.id.ivPipMode).visibility = View.GONE
            findViewById<androidx.cardview.widget.CardView>(R.id.cvUserCount).visibility = View.GONE
        } else {
            // Restore the full-screen UI.

            if (isLive) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    findViewById<ImageView>(R.id.ivPipMode).visibility = View.VISIBLE
                else
                    findViewById<ImageView>(R.id.ivPipMode).visibility = View.GONE

                if (userCount > 1)
                    findViewById<androidx.cardview.widget.CardView>(R.id.cvUserCount).visibility = View.VISIBLE

                llAudienceAction!!.visibility = View.VISIBLE
                ivBack!!.visibility = View.GONE
                ivSetting.visibility = View.GONE
                cvLive!!.visibility = View.VISIBLE
                llGoOwnLive!!.visibility = View.GONE
                if (isAudience(cRole)) {
                    ivMuteRemote!!.visibility = View.VISIBLE
                    rlGoLive!!.visibility = View.GONE

                } else {
                    tvEnd.visibility = View.VISIBLE
                    rlGoLive!!.visibility = View.VISIBLE
                }
            }
        }
    }
}
