package com.sesolutions.ui.courses.myaccount

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.widget.*
import androidx.appcompat.widget.PopupMenu
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toolbar
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.ApiController
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.Courses.course.CourseResponse
import com.sesolutions.responses.Courses.course.CourseVo
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.responses.store.StoreVo
import com.sesolutions.responses.store.product.ProductResponse
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.courses.adapters.CourseAdapter
import com.sesolutions.ui.courses.adapters.WishlistAdapter
import com.sesolutions.ui.courses.course.ViewCourseFragment
import com.sesolutions.ui.common.CommentLikeHelper
import com.sesolutions.ui.music_album.AddToPlaylistFragment
import com.sesolutions.ui.store.product.ProductAdapter
import com.sesolutions.utils.*
import org.apache.http.client.methods.HttpPost
import java.util.*

class ViewCourseWishlist : CommentLikeHelper(), OnLoadMoreListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    var selectedScreen = ""
    var searchKey: String? = null
    var loggedinId: Int = 0
    var txtNoData: Int = 0
    var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private var isLoading: Boolean = false
    private val REQ_LOAD_MORE = 2
    lateinit var pb: ProgressBar
    var rvQuotesCategory: androidx.recyclerview.widget.RecyclerView? = null
    var isTag: Boolean = false
    lateinit var url: String
    var parent: OnUserClickedListener<Int, Any>? = null
    lateinit var llheaderupper: LinearLayoutCompat

    //variable used when called from page view -> associated
    private var mPageId: Int = 0
    private var mWishListId: Int = 0
    private lateinit var wishlist: MutableList<CourseVo>
    lateinit var adapter: WishlistAdapter
    private var result: CourseResponse.Result? = null
    private var optionbar: Toolbar? = null
    private var ivOption: ImageView? = null
    private var ivBack: ImageView? = null


    companion object {

        fun newInstance(parent: OnUserClickedListener<Int, Any>?, loggedInId: Int, categoryId: Int): ViewCourseWishlist {
            val frag = ViewCourseWishlist()
            frag.parent = parent
            frag.loggedinId = loggedInId
            return frag
        }

        @JvmStatic
        fun newInstance(TYPE: String, id: Int): ViewCourseWishlist {
            val frag = ViewCourseWishlist()
            frag.selectedScreen = TYPE
            frag.mWishListId = id
            return frag
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_course_wishlist, container, false)
        applyTheme(v)
        callProductApi(1)
        return v
    }

    fun init() {
//        optionbar = v.findViewById(R.id.optionsbar)
        ivOption = v.findViewById(R.id.ivOption)
        ivBack = v.findViewById(R.id.ivBack)
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME)
        ivOption?.visibility = View.VISIBLE
        ivOption?.setOnClickListener(this)
        ivBack?.setOnClickListener(this)
        recyclerView = v.findViewById(R.id.rvProducts)
        llheaderupper = v.findViewById(R.id.llheaderupper)
        pb = v.findViewById(R.id.pb)
        txtNoData = R.string.NO_WISHLIST_AVAILABLE

        setUpperUIData()
        setRecyclerView()
    }

    fun setRecyclerView() {
        try {
            wishlist = ArrayList<CourseVo>()
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 1, androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false)
            (recyclerView.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            adapter = WishlistAdapter(wishlist, context, this, this)
            adapter.setType(selectedScreen)

            adapter.notifyDataSetChanged()
            recyclerView.adapter = adapter

            /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout!!.setOnRefreshListener(this)
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    private fun callProductApi(req: Int) {

        if (isNetworkAvailable(context)) {
            isLoading = true
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.visibility = View.VISIBLE
                } else if (req == 1) {
                    showBaseLoader(true)
                }
                val request = HttpRequestVO(Constant.URL_VIEW_COURSEWISHLIST) //url will change according to screenType
                request.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD
                if (loggedinId > 0) {
                    request.params[Constant.KEY_USER_ID] = loggedinId
                }

                // used when this screen called from page view -> associated
//                if (mPageId > 0) {
                request.params[Constant.KEY_WISHLIST_ID] = mWishListId
//                }// used when this screen called from page view -> associated
                /*if (categoryId > 0) {
                        request.params.put(Constant.KEY_CATEGORY_ID, categoryId);
                    }*/


                val map = activity.filteredMap
                if (null != map) {
                    request.params.putAll(map!!)
                }
//                request.params[Constant.KEY_PAGE] = if (null != result && req != 1) result?.getNextPage() else 1
                if (req == Constant.REQ_CODE_REFRESH) {
                    request.params[Constant.KEY_PAGE] = 1
                }

                request.headres[Constant.KEY_COOKIE] = getCookie()
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
//                request.params[Constant.KEY_AUTH_TOKEN] = "1641b1b8453a1ccc1555046244"
                request.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String
                        isLoading = false
                        setRefreshing(swipeRefreshLayout, false)
                        CustomLog.e("wishlist_response", "" + response)

                        val err = Gson().fromJson(response, ErrorResponse::class.java)
                        if (TextUtils.isEmpty(err.error)) {
                            parent?.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1)
                            val resp = Gson().fromJson(response, CourseResponse::class.java)
                            //if screen is refreshed then clear previous data
                            if (req == Constant.REQ_CODE_REFRESH) {
                                wishlist.clear()
                            }
                            result = resp.result

//                            setUpperUIData()
                            init()
                            wasListEmpty = wishlist.size == 0

                            if (null != result?.wishlist) {

                                for (vo in result?.wishlist!!.courses!!) {
                                    wishlist.add(CourseVo(selectedScreen, vo))
                                }
                            }
                            result?.wishlist!!.options

                            updateAdapter()
                        } else {
                            Util.showSnackbar(v, err.errorMessage)
                            goIfPermissionDenied(err.error)
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

    private fun setUpperUIData() {

        if (null != result?.wishlist) {
            (v.findViewById(R.id.tvTitle) as TextView).text = result?.wishlist!!.title
            iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME)

            v.findViewById<LinearLayout>(R.id.header).visibility = View.VISIBLE
            (v.findViewById(R.id.tvWishlist) as TextView).text = result?.wishlist!!.title
            (v.findViewById(R.id.tvDate) as TextView).text = Util.changeDateFormat(context, result?.wishlist!!.creationDate)
            (v.findViewById(R.id.tvCourses) as TextView).text = "" + result?.wishlist!!.courses_count + " Courses"
            (v.findViewById(R.id.ivArtist) as TextView).typeface = iconFont
            (v.findViewById(R.id.tvArtist) as TextView).text = result?.wishlist!!.ownerTitle
            (v.findViewById(R.id.tvDescription) as TextView).text = result?.wishlist!!.description
            val detail = ("\uf164 " + result?.wishlist!!.likeCount
                    + "  \uf075 " + result?.wishlist!!.coverId
                    + "  \uf06e " + result?.wishlist!!.viewCount
                    + "  \uf004 " + result?.wishlist!!.favouriteCount)
//                    + "  \uf00c " + result?.wishlist!!.getFollowCount()
//                    + "  \uf0c0 " + result?.wishlist!!.memberCount())
//            (v.findViewById(R.id.tvStats) as TextView).text = detail
            Util.showImageWithGlide(v.findViewById(R.id.ivUser) as ImageView, result?.wishlist!!.image, context, R.drawable.placeholder_square)

        }
    }


    fun hideLoaders() {
        isLoading = false
        setRefreshing(swipeRefreshLayout, false)
        pb.visibility = View.GONE
    }

    fun updateAdapter() {
        hideLoaders()
        adapter.notifyDataSetChanged()
        runLayoutAnimation(recyclerView)
        (v.findViewById(R.id.tvNoData) as TextView).setText(txtNoData)
        v.findViewById<RelativeLayout>(R.id.llNoData).visibility = if (wishlist.size > 0) View.GONE else View.VISIBLE
        //TODO you can update child product of wishlist
//        parent?.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result?.getTotal()!!)
    }

    override fun onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result?.currentPage!! < result?.totalPage!!) {
                    callProductApi(REQ_LOAD_MORE)
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivOption -> showPopup(result?.wishlist?.options, v, 10, this)
            R.id.ivBack -> onBackPressed()

        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        try {

            val opt = result?.wishlist?.options!![item.itemId - 11]

            when (opt.name) {
                Constant.OptionType.EDIT -> {
                    val map = HashMap<String, Any>()
                    map[Constant.KEY_WISHLIST_ID] = result?.wishlist!!.wishlistId
                    fragmentManager.beginTransaction().replace(R.id.container,
                            AddToPlaylistFragment.newInstance(Constant.FormType.TYPE_EDIT_WISHLIST, map, Constant.URL_WISHLIST_EDIT))
                            .addToBackStack(null).commit()
                }
                Constant.OptionType.DELETE -> showDeleteDialog()
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

        return false
    }

    fun showDeleteDialog() {
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
            val tvMsg = progressDialog.findViewById<TextView>(R.id.tvDialogText)
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_WISHLIST)

            val bCamera = progressDialog.findViewById<AppCompatButton>(R.id.bCamera)
            bCamera.setText(R.string.YES)
            val bGallary = progressDialog.findViewById<AppCompatButton>(R.id.bGallary)
            bGallary.setText(R.string.NO)

            progressDialog.findViewById<View>(R.id.bCamera).setOnClickListener { v ->
                progressDialog.dismiss()

                if (isNetworkAvailable(context)) {
                    // categoryList.remove(position);
                    // adapter.notifyItemRemoved(position);
//                    showBaseLoader(false)
                    val map = HashMap<String, Any>()
                    map[Constant.KEY_WISHLIST_ID] = result?.wishlist!!.wishlistId
                    map[Constant.KEY_TYPE] = result?.wishlist!!.wishlistId
                    ApiController(Constant.URL_WISHLIST_DELETE, map, context, this@ViewCourseWishlist, -3).execute()
                    onBackPressed()
                } else {
                    notInternetMsg(v)
                }
            }

            progressDialog.findViewById<View>(R.id.bGallary).setOnClickListener { progressDialog.dismiss() }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }


//    @Override
//    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
//        switch (object1) {
//            case Constant.Events.MUSIC_MAIN:
//                StoreUtil.openViewViewWishListFragment(fragmentManager, postion);
////                this.goToCategoryFragment(postion);
//                break;
//            case Constant.Events.OPEN_WISHLIST:
//                StoreUtil.openViewWishlistFragmnet(fragmentManager);
//                break;
//
//        }
//        return false;
//    }

    override fun onItemClicked(object1: Int?, object2: Any, postion: Int): Boolean {
        when (object1) {
            Constant.Events.OPEN_WISHLIST -> {
                fragmentManager.beginTransaction().add(R.id.container, ViewCourseFragment.newInstance(postion)).addToBackStack(null).commit()
            }
        }

        return super.onItemClicked(object1, object2, postion)
    }

    override fun onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout!!.isRefreshing) {
                swipeRefreshLayout!!.isRefreshing = true
            }
            callProductApi(Constant.REQ_CODE_REFRESH)
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

}
