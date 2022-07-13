package com.sesolutions.ui.forum

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.widget.*
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.forum.ForumResponse
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.ui.common.TTSDialogFragment
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter
import com.sesolutions.ui.forum.adapter.SearchForumAdapter
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.SPref
import com.sesolutions.utils.Util
import org.apache.http.client.methods.HttpPost

class SearchForumFragment : BaseFragment(), View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Int, Any>, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {

    private lateinit var topics: MutableList<ForumResponse.Topic>
    private var result: ForumResponse.Result? = null
    private var v: View? = null
    private var etMusicSearch: AppCompatEditText? = null
    private var hideToolbar: Boolean = false
    var searchKey: String? = null
    private var isLoading: Boolean = false
    private var searchType: String = "topics"
    private var loggedId: Int = 0
    var txtNoData: Int = 0
    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView

    private val REQ_LOAD_MORE = 2
    lateinit var pb: ProgressBar
    var isTag: Boolean = false
    var parent: OnUserClickedListener<Int, Any>? = null
    lateinit var adapter: SearchForumAdapter

    //    private val loggedinId = SPref.getInstance().getLoggedInUserId(context)
    companion object {

        fun newInstance(key: String): SearchForumFragment {
            val fragment = SearchForumFragment()
            fragment.searchKey = key
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        // activity.setTitle(title);
        if (v != null) {
            if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
                activity.isBackFrom = 0
                topics.clear()
                result = null
                val value = activity.filteredMap[Constant.KEY_SEARCH]
                if (null != value) {
                    searchKey = value.toString()
                    Handler().postDelayed({ etMusicSearch!!.setText(value.toString()) }, 200)
                }
                callForumApi(1)
            }
            return v
        }

        v = inflater.inflate(R.layout.fragment_forum_search, container, false)
        applyTheme(v)

//        if (loggedinId <= 0) {
        initScreenData()
//        }
        return v
    }

    override fun initScreenData() {
        init()
        setRecyclerView()
        if (null != searchKey) {
            callForumApi(1)
        } else {
            Handler().postDelayed({
                openKeyboard()
                etMusicSearch!!.requestFocus()
            }, 200)
        }
    }

    fun setRecyclerView() {
        try {
            topics = ArrayList()
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            (recyclerView.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            adapter = SearchForumAdapter(topics, context, this, this)
            recyclerView.adapter = adapter
            swipeRefreshLayout = v!!.findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout.setOnRefreshListener(this)
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    fun init() {

        if (hideToolbar) {
            v!!.findViewById<View>(R.id.toolbar).visibility = View.GONE
        }
        recyclerView = v!!.findViewById(R.id.recyclerview)
        pb = v!!.findViewById(R.id.pb)
        txtNoData = R.string.NO_TOPIC_AVAILABLE
        setRoundedFilledDrawable(v!!.findViewById(R.id.rlCommentEdittext))
        etMusicSearch = v!!.findViewById(R.id.etMusicSearch)
        if (null != searchKey)
            etMusicSearch!!.hint = searchKey
        else
            etMusicSearch!!.setHint(R.string.search_topic)
        v!!.findViewById<View>(R.id.ivBack).setOnClickListener(this)
        v!!.findViewById<View>(R.id.ivFilter).setOnClickListener(this)
        val transitionsContainer = v!!.findViewById<View>(R.id.llOption) as ViewGroup
        val ivCancel = v!!.findViewById<View>(R.id.ivCancel)
        ivCancel.setOnClickListener {
            ivCancel.visibility = View.GONE
            etMusicSearch!!.setText("")
        }
        val ivMic = v!!.findViewById<View>(R.id.ivMic)
        ivMic.setOnClickListener(this)
        etMusicSearch!!.addTextChangedListener(object : CustomTextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                androidx.transition.TransitionManager.beginDelayedTransition(transitionsContainer)
                ivCancel.visibility = if (s != null && s.isNotEmpty()) View.VISIBLE else View.GONE
                ivMic.visibility = if (s != null && s.isNotEmpty()) View.GONE else View.VISIBLE
            }
        })

        etMusicSearch!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard()
                searchKey = etMusicSearch!!.text!!.toString()
                if (!TextUtils.isEmpty(searchKey)) {
                    topics.clear()
                    result = null
                    callForumApi(1)
                }
                return@OnEditorActionListener true
            }
            false
        })
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

                val request = HttpRequestVO(Constant.URL_TOPIC_SEARCH)
                request.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD
                if (loggedId > 0) {
                    request.params[Constant.KEY_USER_ID] = loggedId
                }
                when (searchType) {
                    "topics" -> request.params["search_type"] = "topics"
                    "posts" -> request.params["search_type"] = "posts"
                }

                request.params[Constant.KEY_SEARCH] = searchKey
                request.headres[Constant.KEY_COOKIE] = cookie
                request.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String
                        isLoading = false
                        setRefreshing(swipeRefreshLayout, false)
                        CustomLog.e("response_forum_category", "" + response)
                        val resp = Gson().fromJson(response, ForumResponse::class.java)
                        if (TextUtils.isEmpty(resp.error)) {
                            if (null != parent) {
                                parent?.onItemClicked(Constant.Events.SET_LOADED, "", 1)
                            }
                            //if screen is refreshed then clear previous data
                            if (req == Constant.REQ_CODE_REFRESH) {
                                topics.clear()
                            }

                            wasListEmpty = topics.size == 0
                            result = resp.result!!

                            if (null != result!!.topics)
                                topics.addAll(result!!.topics!!)

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
                hideBaseLoader()
            }
        } else {
            notInternetMsg(v)
        }
    }

    fun updateAdapter() {
        adapter.notifyDataSetChanged()
        runLayoutAnimation(recyclerView)
        (v!!.findViewById<View>(R.id.tvNoData) as TextView).setText(txtNoData)
        v!!.findViewById<View>(R.id.llNoData).visibility = if (topics.size > 0) View.GONE else View.VISIBLE

    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.ivBack -> onBackPressed()
                R.id.ivFilter -> showSearchTypeDialog()
                R.id.ivMic -> {
                    closeKeyboard()
                    TTSDialogFragment.newInstance(this).show(fragmentManager, "tts")
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    private fun showSearchTypeDialog() {
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

            tvMsg.text = "Choose the target of your search"

            val bCamera = progressDialog.findViewById<AppCompatButton>(R.id.bCamera)
            bCamera.text = "Topics"
            val bGallary = progressDialog.findViewById<AppCompatButton>(R.id.bGallary)
            bGallary.text = "Posts"

            progressDialog.findViewById<View>(R.id.bCamera).setOnClickListener { v ->
                progressDialog.dismiss()
                searchType = "topics"
                etMusicSearch!!.setHint(R.string.search_topic)
                txtNoData = R.string.NO_TOPIC_AVAILABLE
            }

            progressDialog.findViewById<View>(R.id.bGallary).setOnClickListener { v ->
                progressDialog.dismiss()
                searchType = "posts"
                etMusicSearch!!.setHint(R.string.search_post)
                txtNoData = R.string.NO_POST_AVAILABLE
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun onItemClicked(object1: Int?, object2: Any, position: Int): Boolean {
        when (object1) {
            Constant.Events.TTS_POPUP_CLOSED -> {
                searchKey = "" + object2
                etMusicSearch!!.setText(searchKey)
                result = null
                topics.clear()
                callForumApi(1)
            }
            Constant.Events.TOPIC_OPTION -> ForumUtil.openViewTopicFragment(fragmentManager, position)
        }
        return false
    }

    override fun onRefresh() {
        try {
            if (!swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = true
            }
            callForumApi(Constant.REQ_CODE_REFRESH)
        }catch (e: Exception){
            CustomLog.e(e)
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
}
