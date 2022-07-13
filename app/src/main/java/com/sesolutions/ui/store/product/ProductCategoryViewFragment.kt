package com.sesolutions.ui.store.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sesolutions.R
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog

class ProductCategoryViewFragment : ProductFragment() {

    internal lateinit var title: String

    companion object {

        @JvmStatic
        fun newInstance(categoryId: Int, title: String): ProductCategoryViewFragment {
            val frag = ProductCategoryViewFragment()
            frag.selectedScreen = PRODUCT_CATEGORY_VIEW
            frag.categoryId = categoryId
            frag.title = title
            return frag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.layout_toolbar_list_refresh_offset, container, false)
        applyTheme(v)
        initScreenData()
        return v
    }

    override fun init() {
        // super.init();
        v.findViewById<View>(R.id.ivBack).setOnClickListener(this)
        (v.findViewById<View>(R.id.tvTitle) as TextView).text = title
        recyclerView = v.findViewById(R.id.recyclerView)
        pb = v.findViewById(R.id.pb)
        txtNoData = R.string.NO_PRODUCT_AVAILABLE
        url = Constant.PRODUCT_BROWSE

    }

    override fun onRefresh() {
        callProductApi(Constant.REQ_CODE_REFRESH)
    }

    override//@OnClick({R.id.bSignIn, R.id.bSignUp})
    fun onClick(v: View) {
        super.onClick(v)
        try {
            when (v.id) {
                R.id.ivBack -> onBackPressed()
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun updateAdapter() {
        super.updateAdapter()
        (v.findViewById<View>(R.id.tvTitle) as TextView).text = title + " (" + result.total + ")"

    }
}