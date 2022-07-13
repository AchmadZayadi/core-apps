package com.sesolutions.ui.store

import android.os.Bundle
import android.os.Handler
import androidx.transition.TransitionManager
import androidx.appcompat.widget.AppCompatEditText
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.sesolutions.R
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.ui.common.TTSDialogFragment
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter
import com.sesolutions.ui.music_album.FormFragment
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.URL
import java.util.*

class SearchStoreFragment : StoreBrowseFragment(), View.OnClickListener, OnLoadMoreListener {

    private var etMusicSearch: AppCompatEditText? = null
    private var isBackFrom: Int = 0


    /* public static SearchPageFragment newInstance(String screenType) {
        SearchPageFragment frag = new SearchPageFragment();
        frag.selectedScreen = screenType;
        return frag;
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        // activity.setTitle(title);
        if (v != null) {
            if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
                isBackFrom = activity.isBackFrom
                activity.isBackFrom = 0
                videoList.clear()
                result = null
                val value = activity.filteredMap[Constant.KEY_SEARCH]
                if (null != value) {
                    searchKey = value.toString()
                    Handler().postDelayed({ etMusicSearch!!.setText(value.toString()) }, 200)
                }
                callStoreApi(1)
            }
            return v
        }
        v = inflater.inflate(R.layout.fragment_search_store_refresh, container, false)
        applyTheme(v)
        selectedVo = 0
        CustomLog.e("value",""+selectedVo)
        init()
        txtNoData = R.string.MSG_NO_STORE_FOUND
        setRecyclerView()
        //disable swipe to refresh beacuse it is useless on search...
        if (null != swipeRefreshLayout)
            swipeRefreshLayout.isEnabled = false

        if (loggedinId > 0) {
            callStoreApi(1)
        } else {
            Handler().postDelayed({
                openKeyboard()
                etMusicSearch!!.requestFocus()
            }, 200)
        }
        return v
    }

    override fun init() {
        super.init()
        selectedVo = 0
        CustomLog.e("value",""+selectedVo)
        etMusicSearch = v.findViewById(R.id.etMusicSearch)
        etMusicSearch!!.setHint(R.string.TXT_SERACH_STORES)

        v.findViewById<View>(R.id.ivBack).setOnClickListener(this)
        v.findViewById<View>(R.id.ivFilter).setOnClickListener(this)
        val transitionsContainer = v.findViewById<ViewGroup>(R.id.llOption)
        val ivCancel = v.findViewById<View>(R.id.ivCancel)
        ivCancel.setOnClickListener {
            ivCancel.visibility = View.GONE
            etMusicSearch!!.setText("")
        }
        val ivMic = v.findViewById<View>(R.id.ivMic)
        ivMic.setOnClickListener(this)
        etMusicSearch!!.addTextChangedListener(object : CustomTextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                TransitionManager.beginDelayedTransition(transitionsContainer)
                ivCancel.visibility = if (s != null && s.length != 0) View.VISIBLE else View.GONE
                ivMic.visibility = if (s != null && s.isNotEmpty()) View.GONE else View.VISIBLE
            }
        })
        setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext))

        etMusicSearch!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard()
                searchKey = etMusicSearch!!.text!!.toString()
                if (!TextUtils.isEmpty(searchKey)) {
                    activity.filteredMap = null
                    videoList.clear()
                    result = null
                    callStoreApi(1)
                }
                return@setOnEditorActionListener true
//                return@etMusicSearch.setOnEditorActionListener true
            }
            false
        }
    }

    override fun onBackPressed() {
        activity.filteredMap = null
//        v.findViewById<View>(R.id.ivCart).visibility = View.VISIBLE
        super.onBackPressed()

    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.ivBack -> onBackPressed()
                R.id.ivFilter -> goToMusicSearchForm()
                R.id.ivMic -> {
                    closeKeyboard()
                    TTSDialogFragment.newInstance(this).show(fragmentManager, "tts")
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    private fun goToMusicSearchForm() {
        val map = HashMap<String, Any>()
        activity.filteredMap = null
        //fragmentManager.beginTransaction().replace(R.id.container, new SearchFormFragment()).addToBackStack(null).commit();
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.FILTER_STORE, map, Constant.URL_SEARCH_STORE))
                .addToBackStack(null)
                .commit()
    }

    override fun onItemClicked(object1: Int?, object2: Any, postion: Int): Boolean {
        when (object1) {
            Constant.Events.TTS_POPUP_CLOSED -> {
                searchKey = "" + object2
                etMusicSearch!!.setText(searchKey)
                result = null
                videoList.clear()
                url = URL.STORE_BROWSE
                callStoreApi(1)
            }
        }

        return super.onItemClicked(object1, object2, postion)
    }

}