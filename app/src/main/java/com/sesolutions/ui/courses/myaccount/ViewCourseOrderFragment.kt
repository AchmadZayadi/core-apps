package com.sesolutions.ui.courses.myaccount

import android.os.Bundle
import android.os.Handler

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.Courses.ViewCourseOrder
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.utils.*
import org.apache.http.client.methods.HttpPost
import java.util.*

class ViewCourseOrderFragment : BaseFragment(), androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    var v: View? = null
    var selectedScreen = ""
    var searchKey: String? = null
    var loggedinId: Int = 0
    var txtNoData: Int = 0
    var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout? = null
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    lateinit var rvOrderInfo: androidx.recyclerview.widget.RecyclerView
    private var isLoading: Boolean = false
    private val REQ_LOAD_MORE = 2
    lateinit var pb: ProgressBar
    var isTag: Boolean = false
    lateinit var url: String
    var parent: OnUserClickedListener<Int, Any>? = null
    lateinit var ivBack: ImageView
    lateinit var tvTitle: TextView

    //variable used when called from page view -> associated
    private var orderId: Int = 0
    private lateinit var productList: MutableList<ViewCourseOrder.Result.Courses.CoursesData>
    private lateinit var orderInfoList: MutableList<ViewCourseOrder.Result.Otherinfo>
    lateinit var adapter: ViewCourseOrderAdapter
    lateinit var orderInfoAdapter: CourseOrderInfoAdapter
    private var result: ViewCourseOrder.Result? = null


    companion object {

        @JvmStatic
        fun newInstance(id: Int): ViewCourseOrderFragment {
            val frag = ViewCourseOrderFragment()
            frag.orderId = id
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_view_order, container, false)
        applyTheme(v)
        callViewOrderApi(1)
        return v
    }

    fun init() {
        ivBack = v!!.findViewById(R.id.ivBack)
        ivBack.setOnClickListener(this)
        tvTitle = v!!.findViewById(R.id.tvTitle)
        tvTitle.text = "Order Details"
        recyclerView = v!!.findViewById(R.id.recyclerView)
        rvOrderInfo = v!!.findViewById(R.id.rvOrdferInfo)
        pb = v!!.findViewById(R.id.pb)
        txtNoData = R.string.NO_PRODUCT_AVAILABLE

        setUpperUIData()
        setRecyclerView()
    }

    fun setRecyclerView() {
        try {
            productList = ArrayList()
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            (recyclerView.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            adapter = ViewCourseOrderAdapter(productList, context)
//            adapter.setType(selectedScreen)

            adapter.notifyDataSetChanged()
            recyclerView.adapter = adapter

            orderInfoList = ArrayList()
            rvOrderInfo.setHasFixedSize(true)
            rvOrderInfo.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            (rvOrderInfo.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            orderInfoAdapter = CourseOrderInfoAdapter(orderInfoList)
            rvOrderInfo.adapter = orderInfoAdapter

            swipeRefreshLayout = v!!.findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout!!.setOnRefreshListener(this)
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun callViewOrderApi(req: Int) {

        if (isNetworkAvailable(context)) {
            isLoading = true
            try {
                if (req == Constant.REQ_CODE_REFRESH) {
                    pb.visibility = View.VISIBLE
                } else if (req == 1) {
                    showBaseLoader(true)
                }
                val request = HttpRequestVO(Constant.URL_COURSE_VIEW_ORDER) //url will change according to screenType
                //                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                // used when this screen called from page view -> associated

                //                request.params.put(Constant.KEY_PAGE, null != result && req != 1 ? result.getNextPage() : 1);
                //                if (req == Constant.REQ_CODE_REFRESH) {
                //                    request.params.put(Constant.KEY_PAGE, 1);
                //                }
                request.headres[Constant.KEY_COOKIE] = cookie
                request.params["order_id"] = orderId
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                request.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String
                        isLoading = false
                        setRefreshing(swipeRefreshLayout, false)
                        CustomLog.e("response_order_view", "" + response)
                        if (response != null) {
                            val err = Gson().fromJson(response, ErrorResponse::class.java)
                            if (TextUtils.isEmpty(err.error)) {

                                val resp = Gson().fromJson(response, ViewCourseOrder::class.java)
                                //if screen is refreshed then clear previous data
                                if (req == Constant.REQ_CODE_REFRESH) {
                                    productList.clear()
                                }
                                result = resp.result
                                init()
                                wasListEmpty = productList.size == 0

                                setUpperUIData()

                                orderInfoList.addAll(result!!.otherinfo.subList(5, result!!.otherinfo.size))
                                updateOrderInfoAdapter()

                                if (result!!.courses.coursesData.isNotEmpty())
                                    productList.addAll(result!!.courses.coursesData)

                                updateAdapter()


                            } else {
                                Util.showSnackbar(v, err.errorMessage)
                                goIfPermissionDenied(err.error)
                            }
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

        v!!.findViewById<LinearLayout>(R.id.llMain).visibility = View.VISIBLE

        v!!.findViewById<TextView>(R.id.tvBillingAddress).text = result!!.billing_address[0].name + "\n" + result!!.billing_address[0].address + " " + result!!.billing_address[0].city + " " + result!!.billing_address[0].phonecode + ",\n" + result!!.billing_address[0].stateName + " " + result!!.billing_address[0].billingName
        v!!.findViewById<TextView>(R.id.tvBillingPhone).text = result!!.billing_address[0].phoneNumber
        v!!.findViewById<TextView>(R.id.tvBillingEmail).text = result!!.billing_address[0].email
        v!!.findViewById<TextView>(R.id.tvShippingAddress).text = result!!.shipping[0].name + ",\n" + result!!.shipping[0].address + " " + result!!.shipping[0].city + " " + result!!.shipping[0].phonecode + ",\n" + result!!.shipping[0].stateName + " " + result!!.shipping[0].shippingName
        v!!.findViewById<TextView>(R.id.tvShippingPhone).text = result!!.shipping[0].phoneNumber
        v!!.findViewById<TextView>(R.id.tvShippingEmail).text = result!!.shipping[0].email

        v!!.findViewById<TextView>(R.id.tvorderId).text = result!!.courses.coursesData[0].parentOrderId.toString()

        if (result!!.footer.size > 1) {
            v!!.findViewById<TextView>(R.id.tvShippingCost).text = result!!.footer[0].label
            v!!.findViewById<TextView>(R.id.tvGrandTotal).text = result!!.footer[1].label
        } else {
            v!!.findViewById<RelativeLayout>(R.id.rlshippingcost).visibility = View.GONE
            v!!.findViewById<TextView>(R.id.tvGrandTotal).text = result!!.footer[0].label
        }

        v!!.findViewById<TextView>(R.id.tvStoreName).text = result!!.otherinfo[0].label
        v!!.findViewById<TextView>(R.id.tvOrderBy).text = result!!.otherinfo[1].label
        v!!.findViewById<TextView>(R.id.tvPaymentMethod).text = result!!.otherinfo[3].label

        v!!.findViewById<TextView>(R.id.tvLabelOrder).text = result!!.courses.title

    }

    fun hideLoaders() {
        isLoading = false
        setRefreshing(swipeRefreshLayout, false)
        pb.visibility = View.GONE
    }

    private fun updateOrderInfoAdapter() {

        orderInfoAdapter.notifyDataSetChanged()
        runLayoutAnimation(rvOrderInfo)
//        llPriceDetail!!.visibility = if (pricelist.size > 1) View.VISIBLE else View.GONE
    }

    fun updateAdapter() {
        hideLoaders()
        adapter.notifyDataSetChanged()
        runLayoutAnimation(recyclerView)
        (v!!.findViewById(R.id.tvNoData) as TextView).setText(txtNoData)
        v!!.findViewById<RelativeLayout>(R.id.llNoData).visibility = if (productList.size > 0) View.GONE else View.VISIBLE
        //TODO you can update child product of productList
//        parent?.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result?.getTotal()!!)
    }

    override fun onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout!!.isRefreshing) {
                swipeRefreshLayout!!.isRefreshing = true
            }
            callViewOrderApi(Constant.REQ_CODE_REFRESH)
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun onClick(v: View?) {

        if (v?.id == R.id.ivBack)
            onBackPressed()
    }

}
