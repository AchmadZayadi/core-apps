package com.sesolutions.ui.credit

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.ApiController
import com.sesolutions.responses.feed.Options
import com.sesolutions.responses.qna.QAResponse
import com.sesolutions.ui.common.TextWebViewFragment
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter
import com.sesolutions.ui.wish.GlobalTabHelper
import com.sesolutions.utils.*
import java.util.*

class CreditParentFragment : GlobalTabHelper() {
    private var tempMenu: List<Options>? = null
    private var fab: FloatingActionButton? = null
    override fun onStart() {
        super.onStart()
        if (activity.isBackFrom == Constant.FormType.FILTER_CORE) {
            activity.isBackFrom = 0
            (adapter.getItem(selectedItem) as TransactionFragment).onFilterClick()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_contest_parent, container, false)
        try {
            applyTheme(v)
            init()
        } catch (e: Exception) {
            CustomLog.e(e)
        }
        return v
    }

    override fun init() { //first fetch tab items and then call init()
        val map: Map<String, Any> = HashMap()
        if (isNetworkAvailable(context)) {
            showBaseLoader(false)
            ApiController(URL.CREDIT_DEFAULT, map, context, this, _DEFAULT).execute()
        } else {
            notInternetMsg(v)
        }
        initButtons()
    }

    override fun setupViewPager() {
        adapter = MessageDashboardViewPagerAdapter(fragmentManager)
        adapter.showTab(true)
        tabItems = ArrayList()
        for (opt in tempMenu!!) {
            when (opt.action) {
                MenuTab.Credit.POINT_SEND -> {
                    tabItems.add(opt)
                    adapter.addFragment(SendPointFragment(), "Send Points")
                }
                MenuTab.Credit.MANAGE -> {
                    tabItems.add(opt)
                    adapter.addFragment(CreditFragment.newInstance(opt.action, this), opt.label)
                }
                MenuTab.Credit.BADGE -> {
                    tabItems.add(opt)
                    adapter.addFragment(BadgeFragment.newInstance(opt.action, this), opt.label)
                }
                MenuTab.Credit.LEADERBOARD -> {
                    tabItems.add(opt)
                    adapter.addFragment(LeaderboardFragment.newInstance(opt.action, this), opt.label)
                }
                MenuTab.Credit.TERMS, MenuTab.Credit.POINT_EARN_HOW -> {
                    tabItems.add(opt)
                    adapter.addFragment(TextWebViewFragment.newInstance(opt.action, null, this), opt.label)
                }
                MenuTab.Credit.HELP -> {
                    tabItems.add(opt)
                    adapter.addFragment(TextWebViewFragment.newInstance(opt.action, opt.value, this), opt.label)
                }
                MenuTab.Credit.MANAGE_TRANSACTION -> {
                    tabItems.add(opt)
                    adapter.addFragment(TransactionFragment.newInstance(opt.action, this), opt.label)
                }
                MenuTab.Credit.POINT_PURCHASE -> {
                    tabItems.add(opt)
                    adapter.addFragment(PurchaseFormagment.newInstance(Constant.FormType.POINT_PURCHASE, null, URL.CREDIT_PURCHASE), opt.label)
                }
                MenuTab.Credit.POINT_EARN -> {
                    tabItems.add(opt)
                    adapter.addFragment(EarnCreditFragment.newInstance(opt.action, this), opt.label)
                }
                else -> {
                    tabItems.add(opt)
                    adapter.addFragment(CreditFragment.newInstance(opt.action, this), opt.label)
                }
            }
        }
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = adapter.count
    }

    override fun updateToolbarIcons(position: Int) {
        selectedItem = position
        ivFilter.visibility = if (canShowFilter(tabItems[position].action)) View.VISIBLE else View.GONE
    }

    private fun canShowFilter(action: String): Boolean {
        return MenuTab.Credit.MANAGE_TRANSACTION == action
    }

    override fun refreshScreenByPosition(position: Int) {}
    override fun loadFragmentIfNotLoaded(position: Int) {
        try {
            if (!tabLoaded[position]) adapter.getItem(position).initScreenData()
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun onItemClicked(object1: Int, object2: Any, postion: Int): Boolean {
        try {
            when (object1) {
                _DEFAULT -> {
                    hideBaseLoader()
                    if (object2 != null) {
                        val resp = Gson().fromJson(object2 as String, QAResponse::class.java)
                        if (resp.isSuccess) {
                            if (resp.result != null) {
                                tempMenu = resp.result.menus
                                super.init()
                                // ivSearch.setVisibility(View.VISIBLE);
                            } else {
                                somethingWrongMsg(v)
                            }
                        } else {
                            Util.showSnackbar(v, resp.errorMessage)
                            goIfPermissionDenied(resp.error)
                        }
                    } else {
                        somethingWrongMsg(v)
                    }
                }
                Constant.Events.SET_LOADED -> updateLoadStatus("" + object2, true)
                Constant.Events.UPDATE_TOTAL -> updateTotal("" + object2, postion)
                Constant.Events.CLICKED_OPTION -> {
                    v.findViewById<View>(R.id.ll1).visibility = View.GONE
                    v.findViewById<View>(R.id.ll4).visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
            somethingWrongMsg(v)
        }
        return super.onItemClicked(object1, object2, postion)
    }

    private fun initButtons() {
        fab = v.findViewById(R.id.fabAdd)
        v.findViewById<View>(R.id.ivSearch).visibility = View.GONE
        ivFilter = v.findViewById(R.id.ivFilter)
        ivFilter.setOnClickListener(this)
        super.updateFabColor(v.findViewById(R.id.fabAdd))
    }

    override fun showFabIcon() {
        Handler().postDelayed({ fab!!.show() }, 1000)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.ivFilter -> CreditUtil.openFilterForm(fragmentManager)
        }
    }

    override fun openCreateForm() { //in case of public user ,send him to sign-in screen
        CreditUtil.openPointSendForm(fragmentManager)
    }

    override fun goToSearchFragment() { // fragmentManager.beginTransaction().replace(R.id.container, new SearchQAFragment()).addToBackStack(null).commit();
    }

    fun getTabIndex(selectedScreen: String): Int {
        for (i in tabItems.indices) {
            if (tabItems[i].action == selectedScreen) {
                return i
            }
        }
        return -1
    }

    fun updateTotal(index: String, count: Int) {
        updateTotal(getTabIndex(index), count)
    }

    fun updateLoadStatus(selectedScreen: String, isLoaded: Boolean) {
        try {
            tabLoaded[getTabIndex(selectedScreen)] = isLoaded
        } catch (e: Exception) {
            CustomLog.e("AIOOBE", "tabItem not found ->$selectedScreen")
        }
    }

    companion object {
        private const val _DEFAULT = 600
    }
}