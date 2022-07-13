package com.sesolutions.ui.news

import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.CommonResponse
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.responses.news.RSS
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.SPref
import com.sesolutions.utils.Util
import org.apache.http.client.methods.HttpPost
import java.util.ArrayList

open class BrowseRSSFragment : RSSHelper(), View.OnClickListener, OnLoadMoreListener  {

    private var recyclerView: androidx.recyclerview.widget.RecyclerView? = null
    private var isLoading: Boolean = false
    private val REQ_LOAD_MORE = 2
    var searchKey: String? = null
    var result: CommonResponse.Result? = null
    private var pb: ProgressBar? = null
    var loggedinId: Int = 0
    var categoryId: Int = 0
    var userId: Int = 0
    private var txtNoMsg = Constant.MSG_NO_RSS


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        // activity.setTitle(title);
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_music_common, container, false)
        applyTheme(v)
        /*if loggedinid > 0 then this myBlog screen otherwise it is browse blog screen*/
        txtNoMsg = if (loggedinId > 0) Constant.MSG_NO_RSS_CREATED_YOU else Constant.MSG_NO_RSS_CREATED
        return v
    }

    override fun init() {
        recyclerView = v.findViewById(R.id.recyclerview)
        pb = v.findViewById(R.id.pb)
        hiddenPanel = v.findViewById<RelativeLayout>(R.id.hidden_panel)
        hiddenPanel.setOnClickListener(this)
    }

    fun setRecyclerView() {
        try {
            videoList = ArrayList<RSS>()
            recyclerView!!.setHasFixedSize(true)
            val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            recyclerView!!.layoutManager = layoutManager
            adapter = RSSAdapter(videoList, context, this, this, if (loggedinId > 0) Constant.FormType.TYPE_MY_ALBUMS else Constant.FormType.TYPE_MUSIC_ALBUM)
            //adapter.setLoggedInId(SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID));
            adapter.setLoggedInId(loggedinId)
            recyclerView!!.adapter = adapter
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }


    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.hidden_panel -> hideSlidePanel()
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun initScreenData() {
        init()
        setRecyclerView()
        callMusicAlbumApi(1)
    }

    fun callMusicAlbumApi(req: Int) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true
                try {
                    if (req == REQ_LOAD_MORE) {
                        pb!!.visibility = View.VISIBLE
                    } else {
                        showBaseLoader(true)
                    }
                    val request = if (loggedinId == 0){
                        HttpRequestVO(Constant.URL_RSS_BROWSE)
                    } else{
                        HttpRequestVO(Constant.BASE_URL + "sesnews/index/browserss/owner_id/" + loggedinId + Constant.POST_URL)
                    }

                    request.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD

                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params[Constant.KEY_SEARCH] = searchKey!!
                    } else if (categoryId > 0) {
                        request.params[Constant.KEY_CATEGORY_ID] = categoryId
                    }

                    val map = activity.filteredMap
                    if (null != map) {
                        request.params.putAll(map)
                    }
                    request.params[Constant.KEY_PAGE] = if (null != result) result!!.nextPage else 1
                    request.headres[Constant.KEY_COOKIE] = cookie
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)

                    request.requestMethod = HttpPost.METHOD_NAME

                    val callback = Handler.Callback { msg ->
                        hideBaseLoader()
                        try {
                            val response = msg.obj as String
                            isLoading = false
                            CustomLog.e("repsonse1", "" + response)

                            val err = Gson().fromJson(response, ErrorResponse::class.java)
                            if (TextUtils.isEmpty(err.error)) {
                                if (null != parent) {
                                    if (loggedinId == 0) {
                                        parent.onItemClicked(Constant.Events.SET_LOADED, null, 1)
                                    } else {
                                        parent.onItemClicked(Constant.Events.SET_LOADED, null, 5)
                                    }
                                }
                                val resp = Gson().fromJson(response, CommonResponse::class.java)
                                result = resp.result
                                menuItem = result!!.menus
                                if (null != result!!.rssList)
                                    videoList.addAll(result!!.rssList)

                                updateAdapter()
                            } else {
                                Util.showSnackbar(v, err.errorMessage)
                                goIfPermissionDenied(err.error)
                            }

                        } catch (e: Exception) {
                            hideBaseLoader()

                            CustomLog.e(e)
                        }

                        // dialog.dismiss();
                        true
                    }
                    HttpRequestHandler(activity, Handler(callback)).run(request)

                } catch (e: Exception) {
                    isLoading = false
                    pb!!.visibility = View.GONE
                    hideBaseLoader()

                }

            } else {
                isLoading = false

                pb!!.visibility = View.GONE
                notInternetMsg(v)
            }

        } catch (e: Exception) {
            isLoading = false
            pb!!.visibility = View.GONE
            CustomLog.e(e)
            hideBaseLoader()
        }

    }

    private fun updateAdapter() {
        isLoading = false
        pb!!.visibility = View.GONE
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged()
        runLayoutAnimation(recyclerView)


        (v.findViewById<View>(R.id.tvNoData) as TextView).text = txtNoMsg
        v.findViewById<View>(R.id.llNoData).visibility = if (videoList.size > 0) View.GONE else View.VISIBLE
        if (parent != null) {
            val index = if (loggedinId != 0) 5 else 1
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, index, result!!.total)
        }
    }

    companion object {

        fun newInstance(parent: OnUserClickedListener<Int, Any>?, loggedInId: Int, categoryId: Int): BrowseRSSFragment {
            val frag = BrowseRSSFragment()
            frag.parent = parent
            frag.loggedinId = loggedInId
            frag.categoryId = categoryId
            return frag
        }

        fun newInstance(parent: OnUserClickedListener<Int, Any>, loggedInId: Int): BrowseRSSFragment {
            return newInstance(parent, loggedInId, -1)

        }

        fun newInstance(categoryId: Int): BrowseRSSFragment {
            return newInstance(null, 0, categoryId)
        }
    }


    override fun onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result!!.currentPage < result!!.totalPage) {
                    callMusicAlbumApi(REQ_LOAD_MORE)
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }
}