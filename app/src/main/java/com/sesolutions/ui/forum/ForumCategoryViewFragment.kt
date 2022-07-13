package com.sesolutions.ui.forum

import android.os.Bundle
import android.os.Handler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.widget.*
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.forum.ForumResponse
import com.sesolutions.responses.forum.ForumResponse2
import com.sesolutions.responses.forum.ForumVo
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.ui.forum.adapter.BreadCrumbAdapter
import com.sesolutions.ui.forum.adapter.BreadCrumbAdapter2
import com.sesolutions.ui.forum.adapter.CategoryViewAdapter
import com.sesolutions.ui.forum.adapter.CategoryViewAdapter2
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.Util
import org.apache.http.client.methods.HttpPost

class ForumCategoryViewFragment : BaseFragment(), OnUserClickedListener<Int, Any>, OnLoadMoreListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private var categoryId: Int = 0
    private lateinit var url: String
    private lateinit var catType: String
    private var v: View? = null
    private var loggedId: Int = 0
    var txtNoData: Int = 0
    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    lateinit var rvBreadCrumb: androidx.recyclerview.widget.RecyclerView
    private var isLoading: Boolean = false
    private val REQ_LOAD_MORE = 2
    lateinit var pb: ProgressBar
    var isTag: Boolean = false
    var parent: OnUserClickedListener<Int, Any>? = null
    lateinit var adapter: CategoryViewAdapter2
    var breadCrumbAdapter: BreadCrumbAdapter2? = null
    lateinit var forumVoList: MutableList<ForumVo>
    lateinit var result: ForumResponse2.Result
    private lateinit var tvTitle: TextView
    private lateinit var cvDesc: androidx.cardview.widget.CardView
    private lateinit var cvBreadCrumb: androidx.cardview.widget.CardView
    private lateinit var tvDescription: TextView
    private lateinit var ivBack: AppCompatImageView
    private lateinit var ivSearch: AppCompatImageView
    private lateinit var ivDashBoard: AppCompatImageView

    companion object {
        @JvmStatic
        fun newInstance(type: String, id: Int): ForumCategoryViewFragment {
            val frag = ForumCategoryViewFragment()
            frag.catType = type
            frag.categoryId = id
            return frag
        }
        var breadCrumbList: MutableList<ForumResponse.Category> = ArrayList()
        var breadCrumbList2: MutableList<ForumResponse2.Category> = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_forum_category_view, container, false)
        try {
            applyTheme(v)
            init()
        } catch (e: Exception) {
            CustomLog.e(e)
        }
        return v
    }

    private fun init() {
        ivBack = v!!.findViewById(R.id.ivBack)
        ivSearch = v!!.findViewById(R.id.ivSearch)
        ivDashBoard = v!!.findViewById(R.id.ivDashBoard)
        ivBack.setOnClickListener(this)
        ivSearch.setOnClickListener(this)
        ivDashBoard.setOnClickListener(this)
        tvTitle = v!!.findViewById<TextView>(R.id.tvTitle)!!
        tvTitle.text = catType
        cvDesc = v!!.findViewById(R.id.cvDesc)
        cvBreadCrumb = v!!.findViewById(R.id.cvBreadCrumb)
        tvDescription = v!!.findViewById(R.id.tvDescription)
        recyclerView = v!!.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)!!
        rvBreadCrumb = v!!.findViewById(R.id.rvChild)
        txtNoData = R.string.NO_FORUM_AVAILABLE

        pb = v!!.findViewById<ProgressBar>(R.id.pb)!!
        setBreadCrumbRecyclerView()
        setRecyclerView()
        callForumApi(1)
    }

    private fun setBreadCrumbRecyclerView() {
        try {
            rvBreadCrumb.setHasFixedSize(true)
            rvBreadCrumb.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
            (rvBreadCrumb.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            breadCrumbAdapter = BreadCrumbAdapter2(breadCrumbList2, context, this, this)
            rvBreadCrumb.adapter = breadCrumbAdapter
            breadCrumbAdapter?.notifyDataSetChanged()

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    fun setRecyclerView() {
        try {
            forumVoList = ArrayList()
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            (recyclerView.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            adapter = CategoryViewAdapter2(forumVoList, context, this, this)
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
                when {
                    catType.equals("category") -> url = Constant.URL_FORUM_CATEGORY_VIEW
                    catType.equals("subcat") -> url = Constant.URL_FORUM_SUBCATEGORY_VIEW
                    catType.equals("subsubcat") -> url = Constant.URL_FORUM_SUBSUBCATEGORY_VIEW
                }

                val request = HttpRequestVO(url)
                request.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD
                if (loggedId > 0) {
                    request.params[Constant.KEY_USER_ID] = loggedId
                }
                //if screen is refreshed then clear previous data
                /*add category list */
                when {
                    catType.equals("category") -> request.params[Constant.KEY_CATEGORY_ID] = categoryId
                    catType.equals("subcat") -> request.params[Constant.KEY_SUB_CAT_ID] = categoryId
                    catType.equals("subsubcat") -> request.params[Constant.KEY_SUB_SUB_CAT_ID] = categoryId
                }

                request.headres[Constant.KEY_COOKIE] = cookie
                request.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String
                        isLoading = false
                        setRefreshing(swipeRefreshLayout, false)
                        CustomLog.e("response_forum_category", "" + response)
                        val resp = Gson().fromJson(response, ForumResponse2::class.java)
                        if (TextUtils.isEmpty(resp.error)) {
                            if (null != parent)
                                parent?.onItemClicked(Constant.Events.SET_LOADED, "", 1)

                            //if screen is refreshed then clear previous data
                            if (req == Constant.REQ_CODE_REFRESH) {
                                forumVoList.clear()
                            }

                            wasListEmpty = forumVoList.size == 0
                            result = resp.result!!

                            if (null != result.categoryDesc) {
                                cvDesc.visibility = View.VISIBLE
                                tvDescription.text = result.categoryDesc
                            }
                            /*add category list */
                            if (null != result.categories && result.categories!!.isNotEmpty()) {
                                forumVoList.add(ForumVo(adapter.VT_HEADING, "SUB CATEGORIES"))
                                forumVoList.addAll(result.getCategoriesList(adapter.VT_CATEGORY))
                            }
                            if (null != result.subcat && result.subcat!!.isNotEmpty()) {
                                forumVoList.add(ForumVo(adapter.VT_HEADING, "SUB CATEGORIES"))
                                forumVoList.addAll(result.getCategoriesList(adapter.VT_CATEGORY))
                            }
                            if (null != result.subsubcat && result.subsubcat!!.isNotEmpty()) {
                                forumVoList.add(ForumVo(adapter.VT_HEADING, "3RD LEVEL CATEGORIES"))
                                forumVoList.addAll(result.getCategoriesList(adapter.VT_CATEGORY))
                            }
                            if (null != result.forums) {
                                forumVoList.add(ForumVo(adapter.VT_HEADING, "FORUMS"))
                                forumVoList.addAll(result.getForumList(adapter.VT_FORUM))
                            }
                            if (null != result.topics) {
                                forumVoList.add(ForumVo(adapter.VT_HEADING, "TOPICS"))
                                forumVoList.addAll(result.getTopicList(adapter.VT_TOPIC))
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
        v!!.findViewById<View>(R.id.llNoData).visibility = if (forumVoList.size > 0) View.GONE else View.VISIBLE

    }

    override fun onBackPressed() {
        try {
            breadCrumbList.remove(breadCrumbList[breadCrumbList2.size-1])
            breadCrumbAdapter?.notifyDataSetChanged()

        } catch (e: Exception) {
            CustomLog.e(e)
            super.onBackPressed()
        }
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.ivBack -> this.onBackPressed()
                R.id.ivSearch -> ForumUtil.gotoSearchFragment(fragmentManager)
                R.id.ivDashBoard -> openWebView(ForumUtil.dashboardUrl, "Dashboard")
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun onItemClicked(eventType: Int?, data: Any, position: Int): Boolean {

        when (eventType) {
            Constant.Events.MUSIC_MAIN -> ForumUtil.openViewForumFragment(fragmentManager, position)

            Constant.Events.CATEGORY -> {
                ForumUtil.openViewForumCategoryFragment(fragmentManager, data as String, position)
                breadCrumbAdapter?.notifyDataSetChanged()
            }
            Constant.Events.SUB_CATEGORY -> ForumUtil.openViewForumCategoryFragment(fragmentManager, data as String, position)
            Constant.Events.TOPIC_OPTION -> ForumUtil.openViewTopicFragment(fragmentManager, position)
            Constant.Events.BREADCRUMB -> {
                ForumUtil.openViewForumCategoryFragment(fragmentManager, data as String, position)
                breadCrumbAdapter?.notifyDataSetChanged()
            }
            Constant.Events.CLICKED_HEADER_IMAGE -> goToProfileFragment(position)
        }
        return false
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
