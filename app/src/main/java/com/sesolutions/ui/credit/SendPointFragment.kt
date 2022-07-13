package com.sesolutions.ui.credit

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.*
import com.sesolutions.responses.feed.Item_user
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.ui.customviews.MentionPopup
import com.sesolutions.ui.postfeed.TagSuggestionAdapter
import com.sesolutions.utils.*
import org.apache.http.client.methods.HttpPost
import org.json.JSONObject
import java.util.*

class SendPointFragment : BaseFragment(), OnUserClickedListener<Int, Any>, TextWatcher {

    private var v: View? = null
    private var t: TextView? = null

    var params: FlowLayout.LayoutParams? = null
    private var etPoint: AppCompatEditText? = null
    private var etBody: AppCompatEditText? = null
    private var flowLayout: FlowLayout? = null
    private var requestHandler: HttpRequestHandler? = null
    private var selectedMap: HashMap<Int, String>? = null

    private var etSearch: AppCompatEditText? = null
    private var list: MutableList<Friends>? = null
    private var pb: ProgressBar? = null
    private var rvTag: RecyclerView? = null
    private var adapter: TagSuggestionAdapter? = null
    private var isReceipentUnchangable = false

    var pointHint =  "<font color=#484744>Points</font> <font color=#FF0000>*</font>";


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_send_point, container, false)
        initViews()

        selectedMap = HashMap<Int, String>()
        params = FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT)
        params!!.setMargins(5, 5, 5, 5)

        setRecycleview()

        return v
    }

    private fun setRecycleview() {
        rvTag = v?.findViewById<RecyclerView>(R.id.rvTag)
        list = ArrayList()
        rvTag!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        rvTag!!.layoutManager = layoutManager
        adapter = TagSuggestionAdapter(list, context, this::onItemClicked)
        rvTag!!.adapter = adapter
    }

    private fun initViews() {

        pb = v!!.findViewById(R.id.pb)
        etSearch = v!!.findViewById(R.id.etSearch)
        etSearch?.addTextChangedListener(this)
        etSearch?.hint = getStrings(R.string.friend_name)
        etPoint = v!!.findViewById(R.id.etPoint)
        etBody = v!!.findViewById(R.id.etBody)
        flowLayout = v!!.findViewById(R.id.flowlayout)

        t = TextView(context)

        v!!.findViewById<Button>(R.id.bt_send).setOnClickListener {
            hideKeyboard()
            sendIfValid()
        }
    }

    private fun callSuggestionApi(value: String) {
        if (TextUtils.isEmpty(value)) {
            pb!!.visibility = View.GONE
            list?.clear()
            adapter!!.notifyDataSetChanged()
            return
        }
        try { //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                pb!!.visibility = View.VISIBLE
                try {
                    val request = HttpRequestVO(Constant.URL_SUGGEST)
                    request.headres[Constant.KEY_COOKIE] = cookie
                    request.params[Constant.KEY_VALUE] = value
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.requestMethod = HttpPost.METHOD_NAME
                    val callback = Handler.Callback { msg ->
                        try {
                            val response = msg.obj as String
                            CustomLog.e("response_suggest", "" + response)
                            if (null != response) {
                                val resp = Gson().fromJson(response, CommonResponse::class.java)
                                list?.clear()
                                if (null != resp.result.friends && resp.result.friends.size > 0) {
                                    list!!.addAll(resp.result.friends)
                                    rvTag!!.visibility = View.VISIBLE
                                    adapter!!.notifyDataSetChanged()
                                } else {
                                    rvTag!!.visibility = View.GONE
                                }
                                //adapter.notifyDataSetChanged();
                            }
                            pb!!.visibility = View.GONE
                        } catch (e: Exception) {
                            CustomLog.e(e)
                        }
                        // dialog.dismiss();
                        true
                    }
                    requestHandler = HttpRequestHandler(context, Handler(callback))
                    requestHandler!!.execute(request)
                } catch (e: Exception) {
                    pb!!.visibility = View.GONE
                    CustomLog.e(e)
                }
            } else {
                pb!!.visibility = View.GONE
                // Util.showSnackbar(drawerLayout, Constant.MSG_NO_INTERNET);
            }
        } catch (e: Exception) {
            pb!!.visibility = View.GONE
            CustomLog.e(e)
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (null != requestHandler /*&& !requestHandler.isCancelled()*/) {
            requestHandler!!.cancel(true)
        }
        callSuggestionApi("" + s)
    }

    override fun afterTextChanged(s: Editable?) {
//        etSearch!!.requestFocus()
    }

    private fun createChip(vo: Friends) {
        try {
            if (!selectedMap!!.containsKey(vo.id)) {
                selectedMap!![vo.id] = vo.label

                etSearch?.text?.clear()
                etSearch?.clearFocus()
                etSearch?.hint = ""
                hideKeyboard()

                t!!.layoutParams = params
                t!!.setPadding(16, 16, 16, 16)
                t!!.text = vo.label + "  X "
                if (isReceipentUnchangable) {
                    t!!.text = vo.label
                }
                t!!.setTextColor(Color.WHITE)
                t!!.tag = vo.id
                t!!.setBackgroundColor(Color.parseColor(Constant.menuButtonActiveTitleColor))
                if (!isReceipentUnchangable) {
                    t!!.setOnClickListener {
                        flowLayout?.removeView(t)
                        selectedMap?.remove(t!!.tag as Int)
                        etSearch?.hint = getStrings(R.string.friend_name)
                    }
                }
                flowLayout!!.addView(t)
            } else {
                CustomLog.e("add_create", "already added")
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun hideKeyboard() {

        val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // check if no view has focus:
        val currentFocusedView = activity?.currentFocus
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onItemClicked(eventType: Int?, data: Any?, position: Int): Boolean {

        try {
            when (eventType) {
                0 -> {
                    val vo = list!![position]
                    flowLayout?.removeView(t)
                    createChip(vo)
                    etSearch!!.setText(Constant.EMPTY)
                    rvTag!!.visibility = View.GONE
                }
            }

        } catch (e: Exception) {
            CustomLog.e(e)
        }
        return false
    }

    private fun sendIfValid() {

        val point = etPoint!!.text.toString()

        if (selectedMap!!.size < 1) {
            Util.showSnackbar(etSearch, getStrings(R.string.FRIEND_REQUIRED))
//            etSearch?.error = getStrings(R.string.FRIEND_REQUIRED)
            return
        }
        if (TextUtils.isEmpty(point)) {
            Util.showSnackbar(etPoint, getStrings(R.string.POINTS_REQUIRED))
//            etPoint?.error = getString(R.string.POINTS_REQUIRED)
            return
        }
        var ids = ""
        for ((key, value) in selectedMap!!) {
            ids = "$ids,$key"
        }
        sendPoints(point, ids.substring(1))
    }

    private fun sendPoints(point: String, uid: String) {

        if (isNetworkAvailable(context)) {
            try {
                showBaseLoader(true)

                val request = HttpRequestVO(URL.CREDIT_SEND)
                request.params["send_credit_value"] = point
                request.params["friend_user_id"] = uid
//                request.params["friend_name_search"] = selectedMap[]
                request.params["friend_message"] = etBody?.text.toString()
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)

                request.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String

                        CustomLog.e("response_points", "" + response)
                        val resp = Gson().fromJson(response, ErrorResponse::class.java)
                        if (null != resp) {

                            if (!resp.isSuccess) {
                                Util.showSnackbar(v, resp.errorMessage)

                            } else {
                                val json = JSONObject(response)
                                var message = ""
                                message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message")
                                Util.showSnackbar(v, message)

                                etPoint?.text?.clear()
                                etPoint?.clearFocus()
                                flowLayout?.removeView(t)
                                selectedMap?.remove(t!!.tag as Int)
                                etBody?.text?.clear()
                            }
                        } else
                            Util.showSnackbar(v, getString(R.string.msg_something_wrong))

                    } catch (e: Exception) {
                        hideBaseLoader()
                        CustomLog.e(e)
                        Util.showSnackbar(v, getString(R.string.msg_something_wrong))
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

    companion object {
        private const val CAMERA_PIC_REQUEST = 7079
        private const val TYPE_IMAGE = 1
        private const val TYPE_VIDEO = 2
        private const val TYPE_LINK = 3
    }

}