package com.sesolutions.ui.courses.cart

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import com.google.android.material.card.MaterialCardView
import androidx.appcompat.widget.AppCompatButton
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
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.responses.store.checkout.CheckoutResponse
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.ui.music_album.AddToPlaylistFragment
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.SPref
import com.sesolutions.utils.Util
import org.apache.http.client.methods.HttpPost
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CourseCheckoutFragment : BaseFragment(), View.OnClickListener, OnLoadMoreListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, OnUserClickedListener<Int, Any> {

    var v: View? = null
    var searchKey: String? = null
    var loggedinId: Int = 0
    private val REQ_REMOVE_PRODUCT = 3
    private val REQ_REMOVE_ALL = 4
    var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private var isLoading: Boolean = false
    private val REQ_LOAD_MORE = 2
    lateinit var pb: ProgressBar
    lateinit var rvPrice: androidx.recyclerview.widget.RecyclerView
    var isTag: Boolean = false
    var parent: OnUserClickedListener<Int, Any>? = null

    //variable used when called from page view -> associated
    private var mPageId: Int = 0
    private var mWishListId: Int = 0
    private lateinit var cartDatalist: MutableList<CheckoutResponse.Result.CartData>
    private lateinit var pricelist: MutableList<CheckoutResponse.Result.PriceDetails>
    lateinit var adapter: CourseCheckoutAdapter
    lateinit var priceAdapter: PriceAdapter
    private var result: CheckoutResponse.Result? = null
    private var tvTitle: TextView? = null
    private var tvTotal: TextView? = null
    private var toolbar: View? = null
    private var ivBack: View? = null
    private var mcvEmpty: MaterialCardView? = null
    private var mcvContinue: MaterialCardView? = null
    private var mcvUpdateCart: MaterialCardView? = null
    private var lldetails: LinearLayout? = null
    private var llPriceDetail: LinearLayout? = null

    companion object{

        var qtyMap:HashMap<String,Int> = HashMap<String,Int>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_checkout, container, false)
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
        ivBack = v!!.findViewById<View>(R.id.ivBack)
        ivBack!!.setOnClickListener(this)
        tvTitle = v!!.findViewById(R.id.tvTitle)
        tvTitle!!.text = "Checkout Page"
        tvTotal = v!!.findViewById(R.id.tvTotal)
        mcvEmpty = v!!.findViewById(R.id.mcvEmpty)
        mcvContinue = v!!.findViewById(R.id.mcvContinue)
        mcvUpdateCart = v!!.findViewById(R.id.mcvUpdateCart)
        llPriceDetail = v!!.findViewById(R.id.llPriceDetail)
        mcvEmpty!!.setOnClickListener(this)
        mcvContinue!!.setOnClickListener(this)
        mcvUpdateCart!!.setOnClickListener(this)
        lldetails = v!!.findViewById(R.id.lldetails)

        v!!.findViewById<View>(R.id.tv_proceed).setOnClickListener(this)
        recyclerView = v!!.findViewById(R.id.recyclerview)
        rvPrice = v!!.findViewById(R.id.rv_price)
        pb = v!!.findViewById(R.id.pb)
        setRecyclerView()
        callCheckoutApi(1, null)

    }

    fun setRecyclerView() {
        try {
            cartDatalist = ArrayList()
            pricelist = ArrayList()
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            (recyclerView.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            adapter = CourseCheckoutAdapter(cartDatalist, context, this, this)
//            adapter.setType(adapter.VT_CATEGORIES)
            recyclerView.adapter = adapter

            rvPrice.setHasFixedSize(true)
            rvPrice.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            (rvPrice.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            priceAdapter = PriceAdapter(pricelist)
            rvPrice.adapter = priceAdapter

            swipeRefreshLayout = v!!.findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout!!.setOnRefreshListener(this)
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun callCheckoutApi(req: Int, map: HashMap<String, Int>?) {

        if (isNetworkAvailable(context)) {
            isLoading = true
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.visibility = View.VISIBLE
                } else if (req == 1) {
                    showBaseLoader(true)
                } else if (req == 89) {
                    showBaseLoader(true)
                }
                val request = HttpRequestVO(Constant.URL_CHECKOUT)
                request.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD
                if (loggedinId > 0) {
                    request.params[Constant.KEY_USER_ID] = loggedinId
                }
                if (req == 89) {
                    if (map != null) {

                        request.params.putAll(map)
//                        request.params.putAll(ArrayList<HashMap>())

                        for(key in map.keys){
                            CustomLog.e(key , ""+map[key])
                        }
                    }
                }

                if (!TextUtils.isEmpty(searchKey)) {
                    request.params[Constant.KEY_SEARCH] = searchKey!!
                }
                request.headres[Constant.KEY_COOKIE] = cookie
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                request.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String
                        isLoading = false
                        setRefreshing(swipeRefreshLayout, false)
                        CustomLog.e("response_checkout", "" + response)
                        val resp = Gson().fromJson(response, CheckoutResponse::class.java)
                        if (TextUtils.isEmpty(resp.error)) {
                            if (null != parent) {
                                parent!!.onItemClicked(Constant.Events.SET_LOADED, "", 1)
                            }
                            lldetails?.visibility = View.VISIBLE
                            //if screen is refreshed then clear previous data
                            if (req == Constant.REQ_CODE_REFRESH) {
                                cartDatalist.clear()
                                pricelist.clear()
                            } else if (req == 89) {
                                cartDatalist.clear()
                                pricelist.clear()
                            }

                            wasListEmpty = cartDatalist.size == 0
                            result = resp.result

                            /*add category list */

                            if (null != result?.cartData) {
                                cartDatalist.addAll(result!!.cartData!!)
                                tvTotal?.text = result!!.orderTotal
                                updateAdapter()

                                if (null != result?.priceDetails) {
                                    pricelist.addAll(result!!.priceDetails)
                                    updatePriceAdapter()
                                }
                            }

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

    private fun updatePriceAdapter() {

        priceAdapter.notifyDataSetChanged()
        runLayoutAnimation(rvPrice)
        llPriceDetail!!.visibility = if (pricelist.size > 1) View.VISIBLE else View.GONE
    }

    fun hideLoaders() {
        isLoading = false
        setRefreshing(swipeRefreshLayout, false)
        pb.visibility = View.GONE
    }

    fun updateAdapter() {
        //        hideLoaders();
        adapter.notifyDataSetChanged()
        runLayoutAnimation(recyclerView)
        (v!!.findViewById<View>(R.id.tvNoData) as TextView).text = Constant.EMPTY_CART_MSG
        v!!.findViewById<View>(R.id.llNoData).visibility = if (cartDatalist.size > 0) View.GONE else View.VISIBLE
        //        if (parent != null) {
        //            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, "", result.getTotal());
        //        }
    }

    override fun onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout!!.isRefreshing) {
                swipeRefreshLayout!!.isRefreshing = true
            }
            callCheckoutApi(Constant.REQ_CODE_REFRESH, qtyMap)
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun onLoadMore() {

    }

    override fun onClick(v: View) {

        try {
            when (v.id) {
                R.id.ivBack -> onBackPressed()
                R.id.mcvEmpty -> showDeleteDialog(0, REQ_REMOVE_ALL) //callEmptyCartApi(0, REQ_REMOVE_ALL)
                R.id.mcvContinue -> {
                    activity.taskPerformed = 99
                    onBackPressed()
                }
                R.id.mcvUpdateCart -> callCheckoutApi(89, qtyMap)
                R.id.tv_proceed -> {
                    CustomLog.e("checkout_URL",Constant.BASE_URL + result!!.checkouturl)
                    openWebView(Constant.BASE_URL + result!!.checkouturl, "Payment")
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun onItemClicked(eventType: Int?, data: Any?, position: Int): Boolean {

        when (eventType) {
            Constant.Events.MEMBER_REMOVE -> showDeleteDialog(position, REQ_REMOVE_PRODUCT) //callEmptyCartApi(position, REQ_REMOVE_PRODUCT)
            Constant.Events.USER_SELECT -> Util.showSnackbar(v, "Select Quantity")
            Constant.Events.ADD_TO_WISHLIST -> gotoWishListForm(position)
            Constant.Events.ATTRIBUTION_CHANGE -> { }
        }
        return false
    }

    private fun gotoWishListForm(position: Int) {
        val map = java.util.HashMap<String, Any>()
        val type = Constant.FormType.TYPE_ADD_WISHLIST
        map[Constant.KEY_PRODUCT_ID] = position

        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        AddToPlaylistFragment.newInstance(type, map, Constant.URL_ADD_WISHLIST))
                .addToBackStack(null)
                .commit()
    }

    private fun callEmptyCartApi(productId: Int, req: Int) {

        if (isNetworkAvailable(context)) {
            isLoading = true
            try {
                showBaseLoader(true)
                val request = HttpRequestVO(Constant.URL_EMPTY_CART)

                if (productId > 0)
                    request.params[Constant.KEY_ID] = productId

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
                            val json = JSONObject(response)

                            if (req == REQ_REMOVE_ALL) {
                                if (json.get(Constant.KEY_RESULT) is String) {
                                    val result = json.getString(Constant.KEY_RESULT)
                                    Util.showSnackbar(v, result)
//                                    activity.taskPerformed = Constant.TASK_EMPTY_CART
                                    activity.taskPerformed = 99
                                    onBackPressed()
                                }
                            } else if (req == REQ_REMOVE_PRODUCT) {
                                if (json.get(Constant.KEY_RESULT) is String) {
                                    val result = json.getString(Constant.KEY_RESULT)
                                    Util.showSnackbar(v, result)
                                    onRefresh()
                                }
                            }
                        } else {
                            Util.showSnackbar(v, err.errorMessage)
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


    fun showDeleteDialog(position: Int, req: Int) {
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
            if (req == REQ_REMOVE_PRODUCT) tvMsg.text = getStrings(R.string.msg_remove_product_cart)
            else if (req == REQ_REMOVE_ALL)tvMsg.text = getStrings(R.string.msg_empty_cart)


            val bCamera = progressDialog.findViewById<AppCompatButton>(R.id.bCamera)
            bCamera.text = Constant.YES
            val bGallary = progressDialog.findViewById<AppCompatButton>(R.id.bGallary)
            bGallary.text = Constant.NO

            progressDialog.findViewById<View>(R.id.bCamera).setOnClickListener { v ->
                progressDialog.dismiss()
                callEmptyCartApi(position, req)
            }

            progressDialog.findViewById<View>(R.id.bGallary).setOnClickListener { progressDialog.dismiss() }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }
}