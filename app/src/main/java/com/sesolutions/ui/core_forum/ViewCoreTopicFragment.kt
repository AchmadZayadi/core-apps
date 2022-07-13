package com.sesolutions.ui.core_forum

//import com.sesolutions.ui.forum.adapter.TagAdapter
//import com.sesolutions.ui.forum.adapter.TopicAdapter

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.animate.bang.SmallBangView
import com.sesolutions.http.ApiController
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.CommonResponse
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.responses.feed.Options
import com.sesolutions.responses.forum.ForumResponse
import com.sesolutions.responses.forum.Post
import com.sesolutions.responses.forum.TopicContent
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.ui.core_forum.adapters.CoreTopicAdapter
import com.sesolutions.ui.customviews.FeedOptionPopup
import com.sesolutions.ui.customviews.RelativePopupWindow
import com.sesolutions.utils.*
import org.apache.http.client.methods.HttpPost
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ViewCoreTopicFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener, OnUserClickedListener<Int, Any>, OnLoadMoreListener, View.OnClickListener {

    private var isTopicOptionSelected = false
    private var v: View? = null
    var loggedinId: Int = 0
    var txtNoData: Int = 0
    var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private var isLoading: Boolean = false
    private val REQ_LOAD_MORE = 2
    lateinit var pb: ProgressBar
    var isTag: Boolean = false
    var parent: OnUserClickedListener<Int, Any>? = null
    lateinit var adapter: CoreTopicAdapter

    lateinit var posts: MutableList<Post>
    private var topic: TopicContent? = null
    var result: ForumResponse.Result? = null
    private var toolbar: View? = null
    private var ivBack: View? = null
//    private var ivShare: View? = null
    private var tvTitle: TextView? = null
//    private var tvRatingCount: TextView? = null
//    private var tvLikeCount: TextView? = null
//    private var tvReplyCount: TextView? = null
    private var tvReply: TextView? = null
    private var tvPostReply: TextView? = null
    private var tvSubscribe: TextView? = null
    private var ivOption: AppCompatImageView? = null
    private var ivLike: AppCompatImageView? = null
    private var ivSubscribe: AppCompatImageView? = null
    private var topicId: Int = 0
    lateinit var mcvClose: MaterialCardView
    lateinit var mcvSticky: MaterialCardView
    lateinit var mcvReply: MaterialCardView
    lateinit var mcvPostReply: MaterialCardView
    private var cvSubscribe: androidx.cardview.widget.CardView? = null
    private var iconFont: Typeface? = null
    private var sbvLike: SmallBangView? = null
    private var dLike: Drawable? = null
    private var dLikeSelected: Drawable? = null
    private var dSubcribe: Drawable? = null
    private var dUnSubcribe: Drawable? = null
    var loggedIn: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_forum_topic_core, container, false)
        try {
            applyTheme(v)
            init()
        } catch (e: Exception) {
            CustomLog.e(e)
        }

        return v
    }

    private fun init() {
        toolbar = v!!.findViewById(R.id.toolbar)
        ivBack = v!!.findViewById(R.id.ivBack)
//        ivShare = v!!.findViewById(R.id.ivShare)
//        sbvLike = v!!.findViewById(R.id.sbvLike)
//        ivLike = v!!.findViewById(R.id.ivLike)
        ivBack!!.setOnClickListener(this)
//        ivShare!!.setOnClickListener(this)
//        ivLike!!.setOnClickListener(this)
        tvTitle = v!!.findViewById(R.id.tvTitle)

        recyclerView = v!!.findViewById(R.id.recyclerview)
        txtNoData = R.string.NO_TOPIC_AVAILABLE
        pb = v!!.findViewById(R.id.pb)
        ivOption = v!!.findViewById(R.id.ivOption)
        tvReply = v!!.findViewById(R.id.tvReply)
        mcvReply = v!!.findViewById(R.id.mcvReply)
        tvPostReply = v!!.findViewById(R.id.tvPostReply)
        mcvPostReply = v!!.findViewById(R.id.mcvPostReply)
        cvSubscribe = v!!.findViewById(R.id.cvSubscribe)
        ivSubscribe = v!!.findViewById(R.id.ivSubscribe)
        tvSubscribe = v!!.findViewById(R.id.tvSubscribe)
        mcvReply.setOnClickListener(this)
        mcvPostReply.setOnClickListener(this)
        cvSubscribe!!.setOnClickListener(this)
        mcvClose = v!!.findViewById(R.id.mcvClose)
        mcvClose!!.setOnClickListener(this)
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME)

        dLike = ContextCompat.getDrawable(context, R.drawable.like_quote)
        dLikeSelected = ContextCompat.getDrawable(context, R.drawable.music_like_selected)
        dSubcribe = ContextCompat.getDrawable(context, R.drawable.subcribe)
        dUnSubcribe = ContextCompat.getDrawable(context, R.drawable.unsubcribe)

        setRecyclerView()
        callForumApi(1)

    }

    fun setRecyclerView() {
        try {
            posts = ArrayList()
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            (recyclerView.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            adapter = CoreTopicAdapter(posts, context, this, this)
            recyclerView.adapter = adapter
            swipeRefreshLayout = v!!.findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout!!.setOnRefreshListener(this)
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    private fun updateUpperData(topic: TopicContent) {

        tvTitle!!.text = result!!.topicContent!!.topic_title
        mcvClose.visibility = if (topic.isClosed) View.GONE else View.VISIBLE

//        if (null != topic.tag) {
//            v!!.findViewById<View>(R.id.llTag).visibility = View.VISIBLE
//
//            /*set child item list*/
//            val rvTag = v!!.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvTag)
//            rvTag.setHasFixedSize(true)
//            rvTag.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
//
//            val subAdapter = TagAdapter(topic.tag, context, this, this)
//            rvTag.adapter = subAdapter
//        }

        if (SPref.getInstance().isLoggedIn(context)) {
            ivOption!!.visibility = if (!topic.isCanEdit && !topic.isCanDelete) View.GONE else View.VISIBLE
            ivOption!!.setOnClickListener { showOptionsPopUp(ivOption!!, result!!.topicContent!!.options) }

            if (null != topic.post_reply) {
                mcvReply.visibility = View.VISIBLE
                tvReply!!.text = topic.post_reply
                mcvPostReply.visibility = View.VISIBLE
                tvPostReply!!.text = topic.post_reply
            } else{
                mcvReply.visibility = View.GONE
                mcvPostReply.visibility = View.GONE
            }
            cvSubscribe!!.visibility =  View.VISIBLE

            if (null != topic.unsubscribe) {
                ivSubscribe!!.setImageDrawable(dUnSubcribe)
                tvSubscribe!!.text = topic.unsubscribe
            } else {
                ivSubscribe!!.setImageDrawable(dSubcribe)
                tvSubscribe!!.text = topic.subscribe
            }

        } else {
            mcvPostReply.visibility = View.GONE
            mcvReply.visibility = View.GONE
            cvSubscribe!!.visibility = View.GONE
            ivOption!!.visibility = View.GONE
        }
    }

    private fun callForumApi(req: Int) {

        if (isNetworkAvailable(context)) {
            isLoading = true
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.visibility = View.VISIBLE
                } else if (req == 1) {
                    showBaseLoader(true)
                }
                val request = HttpRequestVO(Constant.URL_CTOPIC_VIEW_PAGE)
                request.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD
                if (loggedinId > 0) {
                    request.params[Constant.KEY_USER_ID] = loggedinId
                }
                request.params[Constant.KEY_TOPIC_ID] = topicId

                request.params[Constant.KEY_PAGE] = if (null != result) if (req == Constant.REQ_CODE_REFRESH) 1 else result!!.nextPage else 1
                request.headres[Constant.KEY_COOKIE] = cookie
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                request.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String
                        isLoading = false
                        setRefreshing(swipeRefreshLayout, false)
                        CustomLog.e("topic_view_response", "" + response)
                        val resp = Gson().fromJson(response, ForumResponse::class.java)
                        if (TextUtils.isEmpty(resp.error)) {
                            if (null != parent) {
                                parent!!.onItemClicked(Constant.Events.SET_LOADED, "", 1)
                            }
                            if (req == Constant.REQ_CODE_REFRESH) {
                                posts.clear()
                            }

                            wasListEmpty = posts.size == 0
                            result = resp.result

                            /*add posts of that topic */
                            if (null != result!!.posts) {
                                posts.addAll(result!!.posts!!)
                            }
                            if (null != result!!.topicContent) {
                                topic = result!!.topicContent
                                updateUpperData(result!!.topicContent!!)
                            }

                            updateAdapter()
                        } else {
                            Util.showSnackbar(v, resp.errorMessage)
                            goIfPermissionDenied(resp.error)
                        }

                    } catch (e: Exception) {
                        hideBaseLoader()
                        CustomLog.e(e)
                        somethingWrongMsg(v)
                    }

                    true
                }
                HttpRequestHandler(activity, Handler(callback)).run(request)

            } catch (e: Exception) {
                isLoading = false
                pb.visibility = View.GONE
                hideBaseLoader()
            }

        } else {
            isLoading = false
            pb.visibility = View.GONE
            notInternetMsg(v)
        }
    }

    fun hideLoaders() {
        isLoading = false
        setRefreshing(swipeRefreshLayout, false)
        pb.visibility = View.GONE
    }

    fun updateAdapter() {
        isLoading = false
        pb.visibility = View.GONE
        adapter.notifyDataSetChanged()
        runLayoutAnimation(recyclerView)
        (v!!.findViewById<View>(R.id.tvNoData) as TextView).setText(txtNoData)
        v!!.findViewById<View>(R.id.llNoData).visibility = if (posts.size > 0) View.GONE else View.VISIBLE
    }


    private fun showOptionsPopUp(v: View, options: List<Options>) {
        try {
            isTopicOptionSelected = true
            val popup = FeedOptionPopup(v.context, 0, this, options)
            val vertPos = RelativePopupWindow.VerticalPosition.CENTER
            val horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_LEFT
            popup.showOnAnchor(v, vertPos, horizPos, true)
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun onItemClicked(eventType: Int?, data: Any, position: Int): Boolean {

        when (eventType) {
            Constant.Events.SEARCH -> {
                fragmentManager.beginTransaction().replace(R.id.container,
                        CoreSearchForumFragment.newInstance(data as String)).addToBackStack(null)
                        .commit()
            }
            Constant.Events.CLICKED_HEADER_IMAGE -> goToProfileFragment(position)
            Constant.Events.SHARE_FEED -> showShareDialog(posts[position].getShare())
            Constant.Events.POST_QUOTE -> {
                val map: MutableMap<String, Any> = HashMap()
                map[Constant.KEY_TOPIC_ID] = topicId
                map[Constant.KEY_QUOTE_ID] = posts[position].post_id
                openFormFragment(Constant.FormType.QUOTE_POST, map, Constant.URL_CTOPIC_POST_QUOTE)
            }
            Constant.Events.USER_SELECT -> getUserIdFromUserName(data as String)
            Constant.Events.WEBVIEW -> openWebView("" + data, "")

            REQ_RENAME -> onRefresh()
            REQ_MOVE -> onRefresh()

            REQ_CLOSE -> {
                var message = Constant.EMPTY
                try {
                    val json = JSONObject(data as String)
                    message = json.getJSONObject(Constant.KEY_RESULT).getString("message")
                    Util.showSnackbar(v, message)
                    if (message == "Done") {
                        onRefresh()
                        mcvClose.visibility = if (topic!!.options[1].close != 0) View.VISIBLE else View.GONE
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            REQ_DELETE_TOPIC -> try {
                val json = JSONObject(data as String)
                val message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message")
                Util.showSnackbar(v, message)
                if (message.isNotEmpty()) {
                    activity.taskPerformed = REQ_DELETE_TOPIC
                    onBackPressed()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            REQ_STICKY -> onRefresh()

            Constant.Events.FEED_UPDATE_OPTION ->
                //get clicked option
                if (isTopicOptionSelected) {
                    val opt = topic!!.options[position]
                    when (opt.name) {
                        Constant.OptionType.RENAME -> {
                            val map = HashMap<String, Any>()
                            map[Constant.KEY_TOPIC_ID] = topicId
                            openFormFragment(Constant.FormType.RENAME_FORUM_TOPIC, map, Constant.URL_CTOPIC_RENAME)
                            isTopicOptionSelected = false
                        }
                        Constant.OptionType.DELETE -> {
                            showDeleteDialog(Integer.parseInt("" + data), REQ_DELETE_TOPIC)
                            isTopicOptionSelected = false
                        }
                        Constant.OptionType.MOVE -> {
                            val map = HashMap<String, Any>()
                            map[Constant.KEY_TOPIC_ID] = topicId
                            openFormFragment(Constant.FormType.MOVE_FORUM_TOPIC, map, Constant.URL_CTOPIC_MOVE)
                            isTopicOptionSelected = false
                        }
                        Constant.OptionType.CLOSE -> {
                            val map = HashMap<String, Any>()
                            map[Constant.KEY_TOPIC_ID] = topicId
                            map["close"] = opt.close
                            ApiController(Constant.URL_CTOPIC_CLOSE, map, context, this, REQ_CLOSE).execute()
                            isTopicOptionSelected = false
                        }
                        Constant.OptionType.STICKY -> {
                            var map = HashMap<String, Any>()
                            map[Constant.KEY_TOPIC_ID] = topicId
                            map["sticky"] = opt.sticky
                            ApiController(Constant.URL_CTOPIC_STICKY, map, context, this, REQ_STICKY).execute()
                            isTopicOptionSelected = false
                        }
                    }

                } else {
                    val opt = posts[Integer.parseInt("" + data)].options[position]

                    //open share dialog if share clicked
                    when (opt.name) {
                        Constant.OptionType.SHARE -> showShareDialog(posts[Integer.parseInt("" + data)].getShare())
                        Constant.OptionType.DELETE -> showDeleteDialog(Integer.parseInt("" + data), REQ_DELETE)
                        Constant.OptionType.EDIT -> {
                            val map = HashMap<String, Any>()
                            map["post_id"] = posts[Integer.parseInt("" + data)].post_id
                            openFormFragment(Constant.FormType.EDIT_TOPIC, map, Constant.URL_CTOPIC_POST_EDIT)
                        }
                        Constant.OptionType.REPORT -> goToReportFragment(Constant.ResourceType.FORUM_POST + "_" + posts[Integer.parseInt("" + data)].post_id)

                    }
                }
        }

        return false
    }

    private fun getUserIdFromUserName(userName: String) {

        if (isNetworkAvailable(context)) {
            try {
                showBaseLoader(false)
                val request = HttpRequestVO(URL.URL_GETUSERID)
                request.params[Constant.KEY_USER_NAME] = userName
                request.headres[Constant.KEY_COOKIE] = cookie
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                request.requestMethod = HttpPost.METHOD_NAME

                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String
                        CustomLog.e("response_getUserId", "" + response)

                        val err = Gson().fromJson(response, ErrorResponse::class.java)
                        if (TextUtils.isEmpty(err.error)) {

                            val json = JSONObject(response)
                            var id = json.getJSONObject(Constant.KEY_RESULT).getString("user_id")
                            goToProfileFragment(id.toInt())

                        } else {
                            Util.showSnackbar(v, err.errorMessage)
                        }

                    } catch (e: Exception) {
                        hideBaseLoader()
                        CustomLog.e(e)
                    }
                    true
                }
                HttpRequestHandler(activity, Handler(callback)).run(request)

            } catch (e: Exception) {
                hideBaseLoader()
            }

        } else {
            notInternetMsg(v)
        }

    }

    override fun onLoadMore() {

        try {
            if (result != null && !isLoading) {
                if (result!!.currentPage < result!!.totalPage) {
                    callForumApi(REQ_LOAD_MORE)
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun onStart() {
        super.onStart()
        if (activity.taskPerformed == Constant.FormType.EDIT_TOPIC || activity.taskPerformed == Constant.FormType.REPLY_TOPIC
                || activity.taskPerformed == Constant.FormType.QUOTE_POST) {
            activity.taskPerformed = 0
            onRefresh()
        }
    }

    override fun onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout!!.isRefreshing) {
                swipeRefreshLayout!!.isRefreshing = true
            }
            callForumApi(Constant.REQ_CODE_REFRESH)

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.mcvClose -> Util.showSnackbar(v, "This topic has been Closed.")
                R.id.ivBack -> onBackPressed()
                R.id.ivShare -> showShareDialog(result!!.topicContent!!.share)
                R.id.mcvReply -> {
                    var map: MutableMap<String, Any> = HashMap()
                    map[Constant.KEY_TOPIC_ID] = topicId
                    openFormFragment(Constant.FormType.REPLY_TOPIC, map, Constant.URL_CTOPIC_POST_QUOTE)
                }
                R.id.mcvPostReply -> {
                    val map = HashMap<String, Any>()
                    map[Constant.KEY_TOPIC_ID] = topicId
                    openFormFragment(Constant.FormType.REPLY_TOPIC, map, Constant.URL_CTOPIC_POST_QUOTE)
                }

                R.id.cvSubscribe -> callSubscribeApi()
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun callSubscribeApi() {

        if (isNetworkAvailable(context)) {
            try {
                showBaseLoader(false)
                val request = HttpRequestVO(Constant.URL_CTOPIC_SUBSCRIBE)
                request.params["topic_id"] = topicId
//                request.params[Constant.KEY_RESOURCES_TYPE] = Constant.ResourceType.FORUM_TOPIC
//                request.params["subscribe_id"] = topic!!.subscribe_id
                request.params["watch"] = topic!!.watch
                request.headres[Constant.KEY_COOKIE] = cookie
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                request.requestMethod = HttpPost.METHOD_NAME

                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String
                        CustomLog.e("repsonse_subscribe", "" + response)
                        val err = Gson().fromJson(response, ErrorResponse::class.java)
                        if (TextUtils.isEmpty(err.error)) {

                            val res = Gson().fromJson(response, CommonResponse::class.java)
                            Util.showSnackbar(v, res.result.successMessage)

                            if (null != res.result.subscribe) {
                                tvSubscribe!!.text = res.result.subscribe
                                ivSubscribe!!.setImageDrawable(dSubcribe)
                                topic!!.subscribe_id = 0
                                topic!!.watch = res.result.watch
                            } else if (null != res.result.unsubscribe) {
                                tvSubscribe!!.text = res.result.unsubscribe
                                ivSubscribe!!.setImageDrawable(dUnSubcribe)
                                topic!!.subscribe_id = res.result.subscribeId
                                topic!!.watch = res.result.watch
                            }

                        } else {
                            Util.showSnackbar(v, err.errorMessage)
                        }

                    } catch (e: Exception) {
                        hideBaseLoader()
                        CustomLog.e(e)
                    }
                    true
                }
                HttpRequestHandler(activity, Handler(callback)).run(request)

            } catch (e: Exception) {
                hideBaseLoader()
            }

        } else {
            notInternetMsg(v)
        }
    }


    fun showDeleteDialog(position: Int, REQ_CODE: Int) {
        try {
            if (null != progressDialog && progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            progressDialog = ProgressDialog.show(context, "", "", true)
            progressDialog.setCanceledOnTouchOutside(true)
            progressDialog.setCancelable(true)
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog.setContentView(R.layout.dialog_message_two)
            ThemeManager().applyTheme(progressDialog.findViewById<View>(R.id.rlDialogMain) as ViewGroup, context)
            val tvMsg = progressDialog.findViewById<View>(R.id.tvDialogText) as TextView
            if (REQ_CODE == REQ_DELETE) {
                tvMsg.text = getStrings(R.string.MSG_DELETE_CONFIRMATION_FORUM_POST)
            } else if (REQ_CODE == REQ_DELETE_TOPIC) {
                tvMsg.text = getStrings(R.string.MSG_DELETE_CONFIRMATION_FORUM_TOPIC)
            }

            val bCamera = progressDialog.findViewById<AppCompatButton>(R.id.bCamera)
            bCamera.text = Constant.YES
            val bGallary = progressDialog.findViewById<AppCompatButton>(R.id.bGallary)
            bGallary.text = Constant.NO

            progressDialog.findViewById<View>(R.id.bCamera).setOnClickListener { v ->
                progressDialog.dismiss()
                if (REQ_CODE == REQ_DELETE) {
                    callLikeApi(REQ_DELETE, position, Constant.URL_CTOPIC_POST_DELETE, -1)
                } else if (REQ_CODE == REQ_DELETE_TOPIC) {
                    val map = HashMap<String, Any>()
                    map[Constant.KEY_TOPIC_ID] = topicId
                    ApiController(Constant.URL_CTOPIC_DELETE, map, context, this, REQ_DELETE_TOPIC).execute()
                }
            }
            progressDialog.findViewById<View>(R.id.bGallary).setOnClickListener { v -> progressDialog.dismiss() }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }


    private fun callLikeApi(REQ_CODE: Int, position: Int, url: String, optionPosition: Int) {


        if (isNetworkAvailable(context)) {
            val vo = posts[position]

            updateItemLikeFavorite(REQ_CODE, position, vo, -2 != optionPosition)
            try {

                val request = HttpRequestVO(url)

                when (REQ_CODE) {
                    REQ_DELETE -> request.params["post_id"] = vo.post_id
                }
                request.headres[Constant.KEY_COOKIE] = cookie
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                request.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String

                        CustomLog.e("repsonse1", "" + response)
                        val err = Gson().fromJson(response, ErrorResponse::class.java)
                        if (TextUtils.isEmpty(err.error)) {

                            when (REQ_CODE) {
                                REQ_FAVORITE -> Util.showSnackbar(v, "Thank Done")
                                REQ_LIKE_TOPIC -> onRefresh()
                                REQ_LIKE -> onRefresh()
                                REQ_DELETE -> onRefresh()
                            }
                        } else {
                            //revert changes in case of error
                            updateItemLikeFavorite(REQ_CODE, position, vo, false)
                            Util.showSnackbar(v, err.errorMessage)
                        }
                    } catch (e: Exception) {
                        hideBaseLoader()
                        CustomLog.e(e)
                        Util.showSnackbar(v, getStrings(R.string.msg_something_wrong))
                    }
                    true
                }
                HttpRequestHandler(activity, Handler(callback)).run(request)

            } catch (e: Exception) {
                hideBaseLoader()
            }

        } else {
            notInternetMsg(v)
        }
    }

    fun updateItemLikeFavorite(REQ_CODE: Int, position: Int, vo: Post, showAnimation: Boolean) {

        if (REQ_CODE == REQ_LIKE) {
            posts[position].isShowAnimation = if (showAnimation) 1 else 0
            posts[position].setContentLike(!vo.isContentLike())
            adapter.notifyItemChanged(position)
        } else if (REQ_CODE == REQ_FAVORITE) {
            //            posts.get(position).setShowAnimation(showAnimation ? 2 : 0);
            posts[position].setContentThank(!vo.canThank())
            adapter.notifyItemChanged(position)
            adapter.notifyDataSetChanged()
        }
    }

    companion object {
        private val REQ_LIKE = 100
        private val REQ_LIKE_TOPIC = 101
        private val REQ_FAVORITE = 200
        private val REQ_FOLLOW = 300
        private val REQ_DELETE = 400
        private val REQ_RENAME = 401
        private val REQ_CLOSE = 402
        private val REQ_STICKY = 403
        private val REQ_MOVE = 404
        private val REQ_DELETE_TOPIC = 405
        private val REQ_ADD_REPUTATION = 406

        fun newInstance(topicId: Int): ViewCoreTopicFragment {

            val fragment = ViewCoreTopicFragment()
            fragment.topicId = topicId
            return fragment
        }
    }
}
