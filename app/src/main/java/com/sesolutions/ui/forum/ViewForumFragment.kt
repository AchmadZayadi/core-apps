package com.sesolutions.ui.forum

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.forum.ForumResponse
import com.sesolutions.responses.forum.ForumResponse2
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.ui.forum.adapter.ForumAdapter2
import com.sesolutions.ui.signup.SignInFragment
import com.sesolutions.ui.signup.SignInFragment2
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.SPref
import com.sesolutions.utils.URL.URL_GROUP_FORUM_BROWSE
import com.sesolutions.utils.Util
import org.apache.http.client.methods.HttpPost
import java.util.*

open class ViewForumFragment : BaseFragment(), androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, OnUserClickedListener<Int, Any>, OnLoadMoreListener, View.OnClickListener {

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
    lateinit var adapter: ForumAdapter2

    private lateinit var forumList: List<ForumResponse.ForumContent>
    lateinit var topics: MutableList<ForumResponse2.Topic>
    var result: ForumResponse2.Result? = null
    private var toolbar: View? = null
    private var ivBack: View? = null
    private var llCreateTopic: View? = null
    private var tvTitle: TextView? = null
    private var request: HttpRequestVO? = null
    private var label: TextView? = null
    private var tvModerator: TextView? = null
    private var tvPost: TextView? = null
    private var ivPost: AppCompatImageView? = null
    private var dUser: Drawable? = null
    private var forumId: Int = 0
    var searchKey: String? = null
    public var fromGroup: Boolean? = false
    lateinit var title: String

    companion object {
        private const val REQ_DELETE_TOPIC = 405

        fun newInstance(forumId: Int): ViewForumFragment {

            val fragment = ViewForumFragment()
            fragment.forumId = forumId
            return fragment
        }

        fun newInstance(forumId: Int, fromGroup: Boolean): ViewForumFragment {

            val fragment = ViewForumFragment()
            fragment.forumId = forumId
            fragment.fromGroup = fromGroup
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_forum, container, false)
        try {
            applyTheme(v)
            init()
        } catch (e: Exception) {
            CustomLog.e(e)
        }

        return v
    }

    open fun init() {
        toolbar = v!!.findViewById(R.id.toolbar)
        ivBack = v!!.findViewById(R.id.ivBack)
        ivBack!!.setOnClickListener(this)
        tvTitle = v!!.findViewById(R.id.tvTitle)
        recyclerView = v!!.findViewById(R.id.recyclerview)
        txtNoData = R.string.NO_TOPIC_AVAILABLE

        llCreateTopic = v!!.findViewById(R.id.llCreateTopic)
        llCreateTopic!!.setOnClickListener(this)
        label = v!!.findViewById(R.id.label)
        tvModerator = v!!.findViewById(R.id.tvModerator)
        ivPost = v!!.findViewById(R.id.ivPost)
        tvPost = v!!.findViewById(R.id.tvPost)
        dUser = ContextCompat.getDrawable(context, R.drawable.user)

        pb = v!!.findViewById(R.id.pb)
        setRecyclerView()
        callForumApi(1)
    }

    private fun updateModerator(moderator: ForumResponse2.Moderator) {
        v!!.findViewById<View>(R.id.rlModerator).visibility = View.VISIBLE
        label!!.text = moderator.label!! + Constant.COLON
        tvTitle!!.text = moderator.forumTitle
        if (moderator.moderators != "") {
            v!!.findViewById<View>(R.id.llModerator).visibility = View.VISIBLE
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tvModerator!!.text = Html.fromHtml(moderator.moderators, Html.FROM_HTML_MODE_LEGACY)
            } else {
                tvModerator!!.text = Html.fromHtml(moderator.moderators)
            }
            tvModerator!!.movementMethod = LinkMovementMethod.getInstance()
        } else {
            v!!.findViewById<LinearLayout>(R.id.llModerator).visibility = View.GONE
        }

        if (SPref.getInstance().isLoggedIn(context)) {

            if (null == moderator.topic_create) {
                llCreateTopic!!.visibility = View.GONE
            } else {
                llCreateTopic!!.visibility = View.VISIBLE
                tvPost!!.text = moderator.topic_create
            }
        } else {
            llCreateTopic!!.visibility = View.VISIBLE
            ivPost!!.setImageDrawable(dUser)
            tvPost!!.text = "Login to Post"
        }
    }

    fun setRecyclerView() {
        try {
            topics = ArrayList()
            forumList = ArrayList()
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            (recyclerView.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            adapter = ForumAdapter2(topics, context, this, this)
            recyclerView.adapter = adapter
            swipeRefreshLayout = v!!.findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout!!.setOnRefreshListener(this)
        } catch (e: Exception) {
            CustomLog.e(e)
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

                if (fromGroup!!){
                    request = HttpRequestVO(URL_GROUP_FORUM_BROWSE)
                } else{
                    request = HttpRequestVO(Constant.URL_FORUM_VIEW_PAGE)

                }
                request!!.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD
                if (loggedinId > 0) {
                    request!!.params[Constant.KEY_USER_ID] = loggedinId
                }

                if (!TextUtils.isEmpty(searchKey)) {
                    request!!.params[Constant.KEY_SEARCH] = searchKey!!
                }
                if(fromGroup as Boolean){
                    request!!.params[Constant.KEY_GROUP_ID] = forumId
                } else{
                    request!!.params[Constant.KEY_FORUM_ID] = forumId
                }

                request!!.params[Constant.KEY_PAGE] = if (null != result) if (req == Constant.REQ_CODE_REFRESH) 1 else result!!.nextPage else 1
                request!!.headres[Constant.KEY_COOKIE] = cookie
                request!!.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context) //SPref.getInstance().getToken(context));
                request!!.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String
                        isLoading = false
                        setRefreshing(swipeRefreshLayout, false)
                        CustomLog.e("forum_response", "" + response)
                        val resp = Gson().fromJson(response, ForumResponse2::class.java)
                        if (TextUtils.isEmpty(resp.error)) {
                            if (null != parent) {
                                parent!!.onItemClicked(Constant.Events.SET_LOADED, "", 1)
                            }
                            //if screen is refreshed then clear previous data
                            if (req == Constant.REQ_CODE_REFRESH) {
                                topics.clear()
                            }

                            wasListEmpty = topics.size == 0
                            result = resp.result

                            if (null != result?.moderators)
                                updateModerator(result!!.moderators!![0])

                            /*add topics list */
                            if (null != result!!.topics)
                                topics.addAll(result!!.topics!!)

                            updateAdapter()
                        } else {
                            Util.showSnackbar(v, resp.errorMessage)
                            goIfPermissionDenied(resp.error)
                        }

                    } catch (e: Exception) {
                        isLoading = false
                        pb.visibility = View.GONE
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
        //        hideLoaders();
        adapter.notifyDataSetChanged()
        runLayoutAnimation(recyclerView)
        (v!!.findViewById<View>(R.id.tvNoData) as TextView).setText(txtNoData)
        v!!.findViewById<View>(R.id.llNoData).visibility = if (topics.size > 0) View.GONE else View.VISIBLE
        //        if (parent != null) {
        //            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, "", result.getTotal());
        //        }
    }

    override fun onItemClicked(eventType: Int?, data: Any, position: Int): Boolean {

        when (eventType) {

            Constant.Events.TOPIC_OPTION -> ForumUtil.openViewTopicFragment(fragmentManager, position)
            Constant.Events.CLICKED_HEADER_IMAGE -> goToProfileFragment(position)
        }

        return false
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

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.ivBack -> onBackPressed()
                R.id.llCreateTopic -> {
                    if (SPref.getInstance().isLoggedIn(context)) {
                        val map = HashMap<String, Any>()
                        map[Constant.KEY_FORUM_ID] = forumId
                        openFormFragment(Constant.FormType.CREATE_FORUM_TOPIC, map, Constant.URL_CREATE_TOPIC)
                    } else {

                        //  fragmentManager.beginTransaction().add(R.id.container, new SignInFragment()).addToBackStack(null).commit();
                        fragmentManager.beginTransaction().replace(R.id.container, SignInFragment2())
                                .addToBackStack(null)
                                .commit()
                    }
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun onStart() {
        super.onStart()
        if (activity.taskPerformed == Constant.FormType.CREATE_FORUM_TOPIC || activity.taskPerformed == REQ_DELETE_TOPIC) {
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
}
