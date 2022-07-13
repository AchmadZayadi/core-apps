package com.sesolutions.ui.news


import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.*
import android.text.TextUtils
import android.view.*
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.sesolutions.R
import com.sesolutions.camerahelper.CameraActivity
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.responses.CommonResponse
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.responses.feed.Options
import com.sesolutions.responses.news.News
import com.sesolutions.responses.news.RSS
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.common.BaseResponse
import com.sesolutions.ui.customviews.NestedWebView
import com.sesolutions.ui.dashboard.ReportSpamFragment
import com.sesolutions.ui.music_album.AlbumImageFragment
import com.sesolutions.utils.*
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import org.apache.http.client.methods.HttpPost
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ViewRSSFragment : NewsHelper(), View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener {

    private val UPDATE_UPPER_LAYOUT = 101
    val CAMERA_PIC_REQUEST = 7080
    private var isLoading: Boolean = false
    private val REQ_LOAD_MORE = 2
    private var result: CommonResponse.Result? = null
    private lateinit var rssalbum: RSS
    private var pb: ProgressBar? = null

    //  private NestedScrollView mScrollView;
    //public View v;
    // public List<Albums> videoList;
    // public AlbumAdapter adapter;
    lateinit var ivCoverPhoto: ImageView
    lateinit var ivUserImage: ImageView
    //public TextView tvAlbumTitle;
    lateinit var tvUserTitle: TextView
    lateinit var tvAlbumDate: TextView
    lateinit var tvAlbumDetail: TextView
    private var webview: NestedWebView? = null

    private var rssalbumId: Int = 0
    private var isCameraOptionSelected: Boolean = false
    private var collapsingToolbar: CollapsingToolbarLayout? = null
    private var tvTitle: TextView? = null
    private var vItem: View? = null
    private var bundle: Bundle? = null


    private var recyclerView: androidx.recyclerview.widget.RecyclerView? = null
    var searchKey: String? = null
//    var loggedinId: Int = 0
    var categoryId: Int = 0
    var userId: Int = 0
    private var txtNoMsg = Constant.MSG_NO_BLOG

    var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {
                if (isCameraOptionSelected) {
                    takeImageFromCamera()
                } else {
                    showImageChooser()
                }
            } catch (e: Exception) {
                CustomLog.e(e)
            }

        }

        override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if ( bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivCoverPhoto.transitionName = bundle!!.getString(Constant.Trans.IMAGE)
            tvTitle!!.transitionName = bundle!!.getString(Constant.Trans.TEXT)
            tvTitle!!.text = bundle!!.getString(Constant.Trans.IMAGE)
            try {
                Glide.with(context)
                        .setDefaultRequestOptions(RequestOptions().dontAnimate().dontTransform().centerCrop())
                        .load(bundle!!.getString(Constant.Trans.IMAGE_URL))
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                                CustomLog.e("onLoadFailed", "onLoadFailed")
                                startPostponedEnterTransition()
                                return false
                            }

                            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                CustomLog.e("onResourceReady", "onResourceReady")
                                //  ivAlbumImage.setImageDrawable(resource);
                                startPostponedEnterTransition()
                                return false
                            }
                        })
                        .into(ivCoverPhoto)
            } catch (e: Exception) {
                CustomLog.e(e)
            }

            //    Util.showImageWithGlide(ivAlbumImage, bundle.getString(Constant.Trans.IMAGE_URL), context);
        } /*else {

        }*/
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_view_rss_news, container, false)
        applyTheme(v)
        init()
        setRecyclerView()
        callMusicAlbumApi(1)
        return v
    }

    override fun init() {
        super.init()
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME)
        //  ((TextView) v.findViewById(R.id.tvTitle)).setText(" ");
        initCollapsingToolbar()

        ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto)
        //  mScrollView = v.findViewById(R.id.mScrollView);
        v.findViewById<View>(R.id.llContent).setBackgroundColor(Color.parseColor(Constant.foregroundColor))
        tvTitle = v.findViewById(R.id.tvTitle)
//        setCookie()
/*        tvUserTitle = v.findViewById(R.id.tvUserTitle)
        ivUserImage = v.findViewById(R.id.ivUserImage)
        tvAlbumDate = v.findViewById(R.id.tvAlbumDate)
        tvAlbumDetail = v.findViewById(R.id.tvAlbumDetail)
        // v.findViewById(R.id.ivBack).setOnClickListener(this);
        //  v.findViewById(R.id.ivShare).setOnClickListener(this);
        //  v.findViewById(R.id.ivOption).setOnClickListener(this);

        (v.findViewById<View>(R.id.ivUserTitle) as TextView).typeface = iconFont
        (v.findViewById<View>(R.id.ivAlbumDate) as TextView).typeface = iconFont
        (v.findViewById<View>(R.id.ivUserTitle) as TextView).text = Constant.FontIcon.USER
        (v.findViewById<View>(R.id.ivAlbumDate) as TextView).text = Constant.FontIcon.CALENDAR
        tvAlbumDetail.typeface = iconFont*/

        recyclerView = v.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerview)
        pb = v.findViewById(R.id.pb)
        hiddenPanel = v.findViewById<RelativeLayout>(R.id.hidden_panel)
        hiddenPanel.setOnClickListener(this)
    }

    override fun initScreenData() {
        init()
        callMusicAlbumApi(1)
    }
    private fun setCookie() {
        try {
            CookieSyncManager.createInstance(context)
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setCookie(Constant.BASE_URL, cookie) { value ->
                    val cookie = cookieManager.getCookie(Constant.BASE_URL)
                    CookieManager.getInstance().flush()
                    CustomLog.d("cookie", "cookie ------>$cookie")
//                    setupWebView()
                }
            } else {
                cookieManager.setCookie(Constant.BASE_URL, cookie)
//                Handler().postDelayed({ this.setupWebView() }, 700)
                CookieSyncManager.getInstance().sync()
            }

        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    fun setRecyclerView() {
        try {
            videoList = ArrayList<News>()
            recyclerView!!.setHasFixedSize(true)
            val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            recyclerView!!.setLayoutManager(layoutManager)
            adapter = NewsAdapter(videoList, context, this, this, if (rssalbumId > 0) Constant.FormType.TYPE_MY_ALBUMS else Constant.FormType.TYPE_MUSIC_ALBUM)
            //adapter.setLoggedInId(SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID));
            adapter.setLoggedInId(rssalbumId)
            recyclerView!!.setAdapter(adapter)
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }


    private fun initCollapsingToolbar() {
        val toolbar = v.findViewById<Toolbar>(R.id.toolbar)
        activity.setSupportActionBar(toolbar)
        if (activity.supportActionBar != null)
            activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar)
        collapsingToolbar!!.title = " "
        collapsingToolbar!!.setContentScrimColor(Color.parseColor(Constant.colorPrimary))

        val appBarLayout = v.findViewById<AppBarLayout>(R.id.appbar)
        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            internal var isShow = false
            internal var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
//                    if (rssalbum != null) {
                        tvTitle!!.visibility = View.GONE
                        collapsingToolbar!!.title = bundle!!.getString(Constant.Trans.IMAGE)  /*rssalbum!!.getTitle()*/
//                    }
                    isShow = true
                } else if (isShow) {
                    tvTitle!!.visibility = View.VISIBLE
                    collapsingToolbar!!.title = " "//carefull there should a space between double quote otherwise it wont work
                    isShow = false
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater!!.inflate(R.menu.view_menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            when (item!!.itemId) {
                R.id.share -> showShareDialog(rssalbum!!.getShare())
                R.id.option -> {
                    vItem = getActivity()!!.findViewById(R.id.option)
                    showPopup(result!!.menus, vItem, 10, this)
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

        return super.onOptionsItemSelected(item)
    }

    fun showHideOptionIcon() {
        try {
            getActivity()!!.findViewById<View>(R.id.option).visibility = if (result!!.menus != null && result!!.menus.size > 0) View.VISIBLE else View.GONE
        } catch (ignore: Exception) {
        }

    }

    private fun updateUpperLayout() {
        try {
            showHideOptionIcon()
            (v.findViewById<View>(R.id.tvTitle) as TextView).setText(rssalbum!!.getTitle())
            //            collapsingToolbar.setTitle(rssalbum.getTitle());

            //    tvAlbumTitle.setText(rssalbum.getTitle());
            Util.showImageWithGlide(ivCoverPhoto, rssalbum!!.getRSSImages().getMain(), context/*, R.drawable.placeholder_square*/)
            Util.showImageWithGlide(ivUserImage, rssalbum!!.getUserImage(), context, R.drawable.placeholder_square)

            tvUserTitle.setText(rssalbum!!.getOwnerTitle())
            tvAlbumDate.text = Util.changeDateFormat(context, rssalbum!!.getCreationDate())

//            tvAlbumDetail.text = getDetail(rssalbum)
            webview!!.loadDataWithBaseURL(Constant.BASE_URL, rssalbum!!.getRawBody(), "text/html", "UTF-8", null)

        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun onClick(view: View) {
        super.onClick(view)
        try {
            when (view.id) {
                R.id.ivBack -> onBackPressed()

//                R.id.ivShare -> showShareDialog(rssalbum!!.getShare())

                R.id.ivOption -> showPopup(result!!.menus, view, 10, this)
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }


    private fun callRemoveImageApi(url: String) {


        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true


                try {
                    val request = HttpRequestVO(url)

                    request.params[Constant.KEY_RSS_ID] = rssalbum!!.getRSSId()
                    request.params[Constant.KEY_RESOURCE_ID] = rssalbum!!.getRSSId()
                    request.params[Constant.KEY_RESOURCES_TYPE] = rssalbum!!.getResourceType()
                    request.headres[Constant.KEY_COOKIE] = cookie
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)

                    request.requestMethod = HttpPost.METHOD_NAME

                    val callback = Handler.Callback { msg ->
                        hideBaseLoader()
                        try {
                            val response = msg.obj as String
                            isLoading = false

                            CustomLog.e("repsonse1", "" + response)
                            if (response != null) {
                                val err = Gson().fromJson(response, ErrorResponse::class.java)
                                if (TextUtils.isEmpty(err.error)) {
                                    val res = Gson().fromJson(response, BaseResponse::class.java)
                                    callMusicAlbumApi(UPDATE_UPPER_LAYOUT)
                                    Util.showSnackbar(v, res.result as String?)
                                } else {
                                    Util.showSnackbar(v, err.errorMessage)
                                }
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
                    hideBaseLoader()

                }

            } else {
                isLoading = false
                notInternetMsg(v)
            }

        } catch (e: Exception) {
            isLoading = false
            CustomLog.e(e)
            hideBaseLoader()
        }

    }

    /*private fun callMusicAlbumApi(req: Int) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true


                try {
                    if (req == 1) {
                        showView(v.findViewById(R.id.pbMain))
                    }
                    val request = HttpRequestVO(Constant.BASE_URL + "sesnews/index/rssview/rss_id" + rssalbumId + Constant.POST_URL)
                    request.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD
                    *//*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*//*
                    // request.params.put("rss", rssalbumId);
                    if (req == UPDATE_UPPER_LAYOUT) {
                        request.params[Constant.KEY_PAGE] = if (null != result) result!!.currentPage else 1
                    } else {
                        request.params[Constant.KEY_PAGE] = if (null != result) result!!.nextPage else 1
                    }
                    request.headres[Constant.KEY_COOKIE] = cookie
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)

                    request.requestMethod = HttpPost.METHOD_NAME

                    val callback = Handler.Callback { msg ->
                        hideView(v.findViewById(R.id.pbMain))
                        try {
                            val response = msg.obj as String
                            CustomLog.e("repsonse1", "" + response)

                            val err = Gson().fromJson(response, ErrorResponse::class.java)
                            if (TextUtils.isEmpty(err.error)) {
                                showView(v.findViewById(R.id.cvDetail))
                                val resp = Gson().fromJson(response, CommonResponse::class.java)
                                result = resp.result
                                rssalbum = result!!.rss
                                updateUpperLayout()

                            } else {
                                Util.showSnackbar(v, err.errorMessage)
                                goIfPermissionDenied(err.error)
                            }

                        } catch (e: Exception) {
                            CustomLog.e(e)
                        }

                        // dialog.dismiss();
                        true
                    }
                    HttpRequestHandler(activity, Handler(callback)).run(request)

                } catch (e: Exception) {
                    hideView(v.findViewById(R.id.pbMain))
                }

            } else {
                notInternetMsg(v)
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }*/

    fun callMusicAlbumApi(req: Int) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true
                try {
                    if (req == REQ_LOAD_MORE) {
                        pb!!.setVisibility(View.VISIBLE)
                    } else {
                        showBaseLoader(true)
                    }

                    val request = HttpRequestVO(Constant.BASE_URL + "sesnews/index/rssview/rss_id/" + rssalbumId + Constant.POST_URL)
                    request.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD
//
//                    if (!TextUtils.isEmpty(searchKey)) {
//                        request.params[Constant.KEY_SEARCH] = searchKey
//                    } else if (categoryId > 0) {
//                        request.params[Constant.KEY_CATEGORY_ID] = categoryId
//                    }

                    if (req == 1) {
//                        showView(v.findViewById(R.id.pbMain))
                    }
                    request.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD
                    /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
                    // request.params.put("rss", rssalbumId);
                    if (req == UPDATE_UPPER_LAYOUT) {
                        request.params[Constant.KEY_PAGE] = if (null != result) result!!.currentPage else 1
                    } else {
                        request.params[Constant.KEY_PAGE] = if (null != result) result!!.nextPage else 1
                    }
                    request.headres[Constant.KEY_COOKIE] = cookie
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)

                    request.requestMethod = HttpPost.METHOD_NAME

                    val map = activity.filteredMap
                    if (null != map) {
                        request.params.putAll(map)
                    }
//                    request.params[Constant.KEY_PAGE] = if (null != result) result!!.getNextPage() else 1

                    val callback = Handler.Callback { msg ->
                        hideBaseLoader()
                        try {
                            val response = msg.obj as String
                            isLoading = false
                            CustomLog.e("repsonse1", "" + response)
                            if (response != null) {
                                val err = Gson().fromJson(response, ErrorResponse::class.java)
                                if (TextUtils.isEmpty(err.error)) {
                                    if (null != parent) {
                                        if (rssalbumId == 0) {
                                            parent.onItemClicked(Constant.Events.SET_LOADED, null, 0)
                                        } else {
                                            parent.onItemClicked(Constant.Events.SET_LOADED, null, 2)
                                        }
                                    }
                                    val resp = Gson().fromJson(response, CommonResponse::class.java)
                                    result = resp.result
                                    menuItem = result!!.getMenus()
                                    if (null != result!!.getNewsList())
//                                        videoList = ArrayList<News>()
                                        videoList.addAll(result!!.getNewsList())

//                                    rssalbum = result!!.rss
//                                    updateUpperLayout()
                                    updateAdapter()
                                } else {
                                    Util.showSnackbar(v, err.errorMessage)
                                    goIfPermissionDenied(err.error)
                                    pb!!.setVisibility(View.GONE)
                                }
                            }

                        } catch (e: Exception) {
                            hideBaseLoader()
                            pb!!.setVisibility(View.GONE)
                            CustomLog.e(e)
                        }
                        // dialog.dismiss();
                        true
                    }
                    HttpRequestHandler(activity, Handler(callback)).run(request)

                } catch (e: Exception) {
                    isLoading = false
                    pb!!.setVisibility(View.GONE)
                    hideBaseLoader()
                }

            } else {
                isLoading = false
                pb!!.setVisibility(View.GONE)
                notInternetMsg(v)
            }

        } catch (e: Exception) {
            isLoading = false
            pb!!.setVisibility(View.GONE)
            CustomLog.e(e)
            hideBaseLoader()
        }
    }

    private fun updateAdapter() {
        isLoading = false
        pb?.setVisibility(View.GONE)
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged()
        runLayoutAnimation(recyclerView)

        (v.findViewById<View>(R.id.tvNoData) as TextView).setText(txtNoMsg)
        v.findViewById<View>(R.id.llNoData).visibility = if (videoList.size > 0) View.GONE else View.VISIBLE
        if (parent != null) {
            val index = if (rssalbumId != 0) 2 else 0
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, index, result!!.getTotal())
        }
    }

    override fun onLoadMore() {}

    /* public void loadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

    override fun onMenuItemClick(item: MenuItem): Boolean {
        try {
            var itemId = item.itemId
            val opt: Options
            itemId = itemId - 10
            opt = result!!.menus[itemId - 1]


            when (opt.name) {
                Constant.OptionType.EDIT -> goToFormFragment(rssalbum!!.getRSSId())
                Constant.OptionType.DELETE -> showDeleteDialog(RSSHelper.VIEW_RSS_DELETE, rssalbumId, 0)
                Constant.OptionType.REPORT -> goToReportFragment()

                Constant.OptionType.CHANGE_PHOTO, Constant.OptionType.UPLOAD_PHOTO -> gToAlbumImage(Constant.URL_EDIT_RSS_PHOTO, rssalbum!!.getRSSImages().getMain(), Constant.TITLE_EDIT_RSS_PHOTO)
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

        return false
    }

    override fun onStart() {
        super.onStart()
        if (activity.taskPerformed == Constant.TASK_IMAGE_UPLOAD) {
            activity.taskPerformed = 0
            Util.showImageWithGlide(ivCoverPhoto, activity.stringValue, context, R.drawable.placeholder_square)
        } else if (activity.taskPerformed == Constant.FormType.TYPE_RSS_EDIT) {
            activity.taskPerformed = 0
            callMusicAlbumApi(1)
        }

    }

    private fun goToReportFragment() {
        val guid = rssalbum!!.getResourceType() + "_" + rssalbum!!.getRSSId()
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit()
    }

    private fun gToAlbumImage(url: String, main: String?, title: String) {
        val map = HashMap<String, Any>()
        map[Constant.KEY_RSS_ID] = rssalbum!!.getRSSId()
        fragmentManager.beginTransaction()
                .replace(R.id.container, AlbumImageFragment.newInstance(title, url, main, map))
                .addToBackStack(null)
                .commit()
    }


    fun showImageRemoveDialog(isCover: Boolean, msg: String, url: String) {
        try {
            if (null != progressDialog && progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            progressDialog = ProgressDialog.show(context, "", "", true)
            progressDialog.setCanceledOnTouchOutside(true)
            progressDialog.setCancelable(true)
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            progressDialog.setContentView(R.layout.dialog_message_two)
            ThemeManager().applyTheme(progressDialog.findViewById<View>(R.id.rlDialogMain) as ViewGroup, context)
            (progressDialog.findViewById<View>(R.id.tvDialogText) as TextView).text = msg

            val bCamera = progressDialog.findViewById<AppCompatButton>(R.id.bCamera)
            bCamera.text = if (isCover) Constant.TXT_REMOVE_COVER else Constant.TXT_REMOVE_PHOTO
            val bGallary = progressDialog.findViewById<AppCompatButton>(R.id.bGallary)
            bGallary.text = Constant.CANCEL

            progressDialog.findViewById<View>(R.id.bCamera).setOnClickListener {
                progressDialog.dismiss()
                callRemoveImageApi(url)
                //callSaveFeedApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);
            }

            progressDialog.findViewById<View>(R.id.bGallary).setOnClickListener { progressDialog.dismiss() }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    fun askForPermission(permission: String) {
        try {
            TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(permission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check()
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    fun showImageChooser() {
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setActivityTheme(R.style.FilePickerTheme)
                .showFolderView(true)
                .enableImagePicker(true)
                .enableVideoPicker(false)
                .pickPhoto(this)
    }

    fun takeImageFromCamera() {
        // fimg = new File(image_path_source_temp + imageName);
        // Uri uri = Uri.fromFile(fimg);
        val imagePath = Environment.getExternalStorageDirectory().absolutePath + "/SeSolutions/"
        val imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP)

        val dir = File(imagePath)
        try {
            if (dir.mkdir()) {
            } else {
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

        val cameraIntent = Intent(activity, CameraActivity::class.java)
        cameraIntent.putExtra("path", imagePath)
        cameraIntent.putExtra("name", imageName)
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST)
    }

    fun showImageDialog(msg: String) {
        try {
            if (null != progressDialog && progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            progressDialog = ProgressDialog.show(context, "", "", true)
            progressDialog.setCanceledOnTouchOutside(true)
            progressDialog.setCancelable(true)
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            progressDialog.setContentView(R.layout.dialog_message_two)
            ThemeManager().applyTheme(progressDialog.findViewById<View>(R.id.rlDialogMain) as ViewGroup, context)
            val tvMsg = progressDialog.findViewById<TextView>(R.id.tvDialogText)
            tvMsg.text = msg

            progressDialog.findViewById<View>(R.id.bCamera).setOnClickListener {
                progressDialog.dismiss()
                isCameraOptionSelected = true
                askForPermission(Manifest.permission.CAMERA)
                // takeImageFromCamera();
            }

            progressDialog.findViewById<View>(R.id.bGallary).setOnClickListener {
                progressDialog.dismiss()
                isCameraOptionSelected = false
                askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                //showImageChooser();
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    /**
     * camera activity call back
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        CustomLog.e("onActivityResult", "requestCode : $requestCode resultCode : $resultCode")
        try {
            when (requestCode) {
                FilePickerConst.REQUEST_CODE_PHOTO -> if (resultCode == -1 && data != null) {
                    val photoPaths = ArrayList(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS))
                    //  setImage(photoPaths.get(0));


                }
                CAMERA_PIC_REQUEST -> if (resultCode == -1) {
                    //setImage(Constant.path);
                    val photoPaths = ArrayList<String>()
                    photoPaths.add(Constant.path)
                    CustomLog.d("CAMERA_PIC_REQUEST", Constant.path)

                }
            }

        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    companion object {

        private val UPDATE_UPPER_LAYOUT = 101
        val CAMERA_PIC_REQUEST = 7080


        fun newInstance(rssalbumId: Int, comment: Boolean): ViewRSSFragment {
            val frag = ViewRSSFragment()
            frag.rssalbumId = rssalbumId
//            frag.openComment = comment
            return frag
        }

        fun newInstance(rssalbumId: Int, bundle: Bundle): ViewRSSFragment {
            val frag = ViewRSSFragment()
            frag.rssalbumId = rssalbumId
            frag.bundle = bundle
            return frag
        }


        fun newInstance(rssalbumId: Int): ViewRSSFragment {
            return ViewRSSFragment.newInstance(rssalbumId, false)
        }
    }

}
