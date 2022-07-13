package com.sesolutions.ui.profile

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.forum.ForumResponse
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.ui.courses.test.Answer
import com.sesolutions.ui.dashboard.composervo.FeedSearchOptions
import com.sesolutions.ui.forum.ForumUtil
import com.sesolutions.utils.*
import org.apache.http.client.methods.HttpPost
import org.json.JSONArray
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.collections.HashMap

class ChooseInterestFragment : BaseFragment(), OnUserClickedListener<Int, Any>, View.OnClickListener {

    private var v: View? = null
    var txtNoData: Int = 0
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    lateinit var pb: ProgressBar
    var rvQuotesCategory: androidx.recyclerview.widget.RecyclerView? = null
    var parent: OnUserClickedListener<Int, Any>? = null
    lateinit var adapter: InterestAdapter

    lateinit var categoryList: MutableList<Answer>
    lateinit var result: ForumResponse.Result
    private var toolbar: View? = null
    private lateinit var tvTitle: TextView
    private lateinit var etInterest: TextInputEditText
    private lateinit var ivBack: AppCompatImageView

    private var list = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_choose_interest, container, false)
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
        ivBack.setOnClickListener(this)
        tvTitle = v!!.findViewById<TextView>(R.id.tvTitle)!!
        etInterest = v!!.findViewById<TextInputEditText>(R.id.etInterest)!!
        tvTitle.text = "Choose Interest"
        recyclerView = v!!.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerview)!!
        txtNoData = R.string.NO_FORUM_AVAILABLE

        pb = v!!.findViewById<ProgressBar>(R.id.pb)!!
        setRecyclerView()
        callInterestApi(0)

        v!!.findViewById<TextView>(R.id.btSave).setOnClickListener {

            try {

                callInterestApi(1)
            } catch (e: Exception) {
                CustomLog.e(e)
            }
        }
    }

    fun setRecyclerView() {
        try {
            categoryList = ArrayList()
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(context)
            (recyclerView.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            adapter = InterestAdapter(categoryList, context, this, object : InterestAdapter.OnItemCheckListener {
                override fun onItemCheck(item: Any?, position: Int) {

                    list.add(item as String)
                }

                override fun onItemUncheck(item: Any?, position: Int) {

                    list.remove(item as String)
                }
            })
            recyclerView.adapter = adapter
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    private fun callInterestApi(req: Int) {

        if (isNetworkAvailable(context)) {
            try {
                showBaseLoader(false)
                val request = HttpRequestVO(URL.URl_CHOOSE_INTEREST)
                request.params[Constant.KEY_LIMIT] = Constant.RECYCLE_ITEM_THRESHOLD
                request.params[Constant.KEY_ID] = SPref.getInstance().getLoggedInUserId(context)
                if (req == 0)
                    request.params[Constant.KEY_GET_FORM] = 1

                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)

                if (req == 1) {
                    request.params["interests"] = list
                    request.params["custom_interests"] = etInterest.text.toString()
                }

                request.headres[Constant.KEY_COOKIE] = cookie
                request.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String
                        CustomLog.e("response_choose_interest", "" + response)
                        val resp = Gson().fromJson(response, InterestResponse::class.java)
                        if (TextUtils.isEmpty(resp.error)) {

                            if (req == 1) {
                                Util.showSnackbar(v, "Your changes have been saved")
                            } else {

                                for (field in resp.result?.formFields!!) {

                                    if (field?.type == "MultiCheckbox") {


                                        val selecteditems = ArrayList<Int>()
                                        val arr = JSONArray(field.valueString)
                                        if (arr.length() > 0) {
                                            for (x in 0 until arr.length()) {
                                                selecteditems.add(arr.getInt(x))
                                            }
                                        }

                                        for (entry in field.multiOptions.entries) {

                                            var selected = selecteditems.contains(entry.key.toInt())
                                            if (selected)
                                                list.add(entry.key)

                                            categoryList.add(Answer(entry.key, entry.value, selected))

                                        }

                                    }
                                    if (field?.type == "Textarea") {

                                        v?.findViewById<TextView>(R.id.textView)?.text = field?.description
                                    }

                                }
                                updateAdapter()
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


    fun hideLoaders() {
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
            Constant.Events.MUSIC_MAIN -> ForumUtil.openViewForumFragment(fragmentManager, position)
            Constant.Events.CATEGORY -> {
                ForumUtil.openViewForumCategoryFragment(fragmentManager, data as String, position)
            }
            Constant.Events.SUB_CATEGORY -> ForumUtil.openViewForumCategoryFragment(fragmentManager, data as String, position)
        }
        return false
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.ivBack -> onBackPressed()
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

}