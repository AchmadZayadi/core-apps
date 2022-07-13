package com.sesolutions.ui.core_forum

import android.os.Bundle
import android.os.Handler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.forum.ForumResponse
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.ui.core_forum.adapters.CoreForumHomeAdapter
//import com.sesolutions.ui.forum.adapter.ForumHomeAdapter
import com.sesolutions.ui.signup.SignInFragment
import com.sesolutions.ui.welcome.WelcomeActivity
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.SPref
import com.sesolutions.utils.Util
import org.apache.http.client.methods.HttpPost
import java.util.ArrayList

class CoreForumHomeFragment : BaseFragment(), OnUserClickedListener<Int, Any>, OnLoadMoreListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private var v: View? = null
    private var loggedId: Int = 0
    var txtNoData: Int = 0
    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private var isLoading: Boolean = false
    private val REQ_LOAD_MORE = 2
    lateinit var pb: ProgressBar
    var rvQuotesCategory: androidx.recyclerview.widget.RecyclerView? = null
    var parent: OnUserClickedListener<Int, Any>? = null
    lateinit var adapter: CoreForumHomeAdapter

    lateinit var categoryList: MutableList<ForumResponse.Category>
    lateinit var stats: ForumResponse.Stats
    lateinit var result: ForumResponse.Result
    private var toolbar: View? = null
    private lateinit var tvTitle: TextView
    private lateinit var ivBack: AppCompatImageView
    private lateinit var ivSearch: AppCompatImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_core_forum_home, container, false)
        try {
            applyTheme(v)
            init()
        } catch (e: Exception) {
            CustomLog.e(e)
        }
        return v
    }

    private fun init() {

        toolbar = v!!.findViewById<View>(R.id.toolbar)
        ivBack = v!!.findViewById(R.id.ivBack)
        ivSearch = v!!.findViewById(R.id.ivSearch)
        ivBack.setOnClickListener(this)
        ivSearch.setOnClickListener(this)
        tvTitle = v!!.findViewById<TextView>(R.id.tvTitle)!!
        tvTitle.text = getString(R.string.forums)
        recyclerView = v!!.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerview)!!
        txtNoData = R.string.NO_FORUM_AVAILABLE

        pb = v!!.findViewById<ProgressBar>(R.id.pb)!!
        setRecyclerView()
        callForumApi(1)
    }

    fun setRecyclerView() {
        try {
            categoryList = ArrayList()
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            (recyclerView.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            adapter = CoreForumHomeAdapter(categoryList, context, this, this)
            adapter.setType(adapter.VT_CATEGORIES)
            recyclerView.adapter = adapter
            swipeRefreshLayout = v!!.findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout.setOnRefreshListener(this)
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
                val request = HttpRequestVO(Constant.URL_CFORUM_HOME_PAGE)
                request.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD
                if (loggedId > 0) {
                    request.params[Constant.KEY_USER_ID] = loggedId
                }

                request.headres[Constant.KEY_COOKIE] = cookie
                request.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String
                        isLoading = false
                        setRefreshing(swipeRefreshLayout, false)
                        CustomLog.e("response_forum", "" + response)
                        val resp = Gson().fromJson(response, ForumResponse::class.java)
                        if (TextUtils.isEmpty(resp.error)) {
                            if (null != parent) {
                                parent?.onItemClicked(Constant.Events.SET_LOADED, "", 1)
                            }
                            //if screen is refreshed then clear previous data
                            if (req == Constant.REQ_CODE_REFRESH) {
                                categoryList.clear()
                            }

                            wasListEmpty = categoryList.size == 0
                            result = resp.result!!

                            /*add category list */
                            if (null != result.categories) {
                                categoryList.addAll(result.categories!!)
                                CoreForumUtil.dashboardUrl = result.dashboardUrl
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
                hideBaseLoader()
            }

        } else {
            notInternetMsg(v)
        }
    }


    fun hideLoaders() {
        isLoading = false
        setRefreshing(swipeRefreshLayout, false)
        pb.visibility = View.GONE
    }

    fun updateAdapter() {

        adapter.notifyDataSetChanged()
        runLayoutAnimation(recyclerView)
        (v!!.findViewById<View>(R.id.tvNoData) as TextView).setText(txtNoData)
        v!!.findViewById<View>(R.id.llNoData).visibility = if (categoryList.size > 0) View.GONE else View.VISIBLE

    }

    override fun onItemClicked(eventType: Int?, data: Any, position: Int): Boolean {

        when (eventType) {
            Constant.Events.MUSIC_MAIN -> CoreForumUtil.openViewForumFragment(fragmentManager, position)
        }
        return false
    }

    override fun onClick(v: View) {
        try{
            when(v.id){
                R.id.ivBack -> onBackPressed()
                R.id.ivSearch -> CoreForumUtil.openCoreSearchForumFragment(fragmentManager)
            }
        }
        catch (e: Exception){
                CustomLog.e(e)
        }
    }

    override fun onRefresh() {
        try {
            if (!swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = true
            }
            callForumApi(Constant.REQ_CODE_REFRESH)
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }


    override fun onLoadMore() {

    }
}

