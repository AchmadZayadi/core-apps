package com.sesolutions.ui.news


import android.os.Bundle
import android.os.Handler
import androidx.appcompat.widget.AppCompatEditText
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import com.sesolutions.R
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.ui.common.TTSDialogFragment
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter
import com.sesolutions.ui.music_album.FormFragment
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog

import java.util.HashMap

class SearchRSSFragment : BrowseRSSFragment(), View.OnClickListener, OnLoadMoreListener {


    private var etMusicSearch: AppCompatEditText? = null
    private var hideToolbar: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        // activity.setTitle(title);
        if (v != null) {
            if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
                activity.isBackFrom = 0
                videoList.clear()
                result = null
                val value = activity.filteredMap[Constant.KEY_SEARCH]
                if (null != value) {
                    searchKey = value.toString()
                    Handler().postDelayed({ etMusicSearch!!.setText(value.toString()) }, 200)
                }
                callMusicAlbumApi(1)
            }
            return v
        }

        v = inflater.inflate(R.layout.fragment_music_search, container, false)
        applyTheme()

        if (loggedinId <= 0) {
            initScreenData()
        }

        return v
    }

    override fun initScreenData() {
        init()
        setRecyclerView()
        if (loggedinId == 0) {
            Handler().postDelayed({
                openKeyboard()
                etMusicSearch!!.requestFocus()
            }, 200)
        } else {
            callMusicAlbumApi(1)
        }
    }

    override fun init() {
        super.init()

        if (hideToolbar) {
            v.findViewById<View>(R.id.toolbar).visibility = View.GONE
        }
        /* recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);*/
        setRoundedFilledDrawable(v.findViewById(R.id.rlCommentEdittext))
        etMusicSearch = v.findViewById(R.id.etMusicSearch)
        etMusicSearch!!.setHint(R.string.TXT_SERACH_NEWS)
        v.findViewById<View>(R.id.ivBack).setOnClickListener(this)
        v.findViewById<View>(R.id.ivFilter).setOnClickListener(this)
        val transitionsContainer = v.findViewById<View>(R.id.llOption) as ViewGroup
        val ivCancel = v.findViewById<View>(R.id.ivCancel)
        ivCancel.setOnClickListener {
            ivCancel.visibility = View.GONE
            etMusicSearch!!.setText("")
        }
        val ivMic = v.findViewById<View>(R.id.ivMic)
        ivMic.setOnClickListener(this)
        etMusicSearch!!.addTextChangedListener(object : CustomTextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                androidx.transition.TransitionManager.beginDelayedTransition(transitionsContainer)
                ivCancel.visibility = if (s != null && s.length != 0) View.VISIBLE else View.GONE
                ivMic.visibility = if (s != null && s.length != 0) View.GONE else View.VISIBLE
            }
        })

        etMusicSearch!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard()
                searchKey = etMusicSearch!!.text!!.toString()
                if (!TextUtils.isEmpty(searchKey)) {
                    videoList.clear()
                    result = null
                    callMusicAlbumApi(1)
                }
                return@OnEditorActionListener true
            }
            false
        })
    }


    override//@OnClick({R.id.bSignIn, R.id.bSignUp})
    fun onClick(v: View) {
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
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.FILTER_NEWS, map, Constant.URL_NEWS_FILTER_FORM))
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
                callMusicAlbumApi(1)
            }
        }

        return super.onItemClicked(object1, object2, postion)
    }

    companion object {


        fun newInstance(userId: Int): SearchRSSFragment {
            val frag = SearchRSSFragment()
            frag.loggedinId = userId
            frag.hideToolbar = true
            return frag
        }
    }
}
