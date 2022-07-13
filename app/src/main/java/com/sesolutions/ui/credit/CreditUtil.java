package com.sesolutions.ui.credit;


import android.content.Context;
import androidx.fragment.app.FragmentManager;

import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.ui.crowdfunding.CreateEditFundFragment;
import com.sesolutions.ui.crowdfunding.ViewCrowdFragment;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.URL;

import java.util.HashMap;
import java.util.Map;

public class CreditUtil {
    public static void openViewFragment(FragmentManager fragmentManager, int id) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewCrowdFragment.newInstance(id))
                .addToBackStack(null)
                .commit();
    }

    static void openPointSendForm(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.POINT_PURCHASE, null, URL.CREDIT_SEND))
                .addToBackStack(null)
                .commit();
    }

    public static void openParentFragment(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        new CreditParentFragment())
                .addToBackStack(null)
                .commit();
    }

    static void openCreateForm(FragmentManager fragmentManager, Dummy.Result result, Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditFundFragment.newInstance(Constant.FormType.CREATE_FUND, map, Constant.URL_FUND_CREATE, result))
                .addToBackStack(null)
                .commit();
    }


    public static void openFilterForm(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, FormFragment.newInstance(Constant.FormType.FILTER_CORE, null, URL.CREDIT_SEARCH))
                .addToBackStack(null)
                .commit();
    }

    static void calRatingApi(int fundId, float rating, Context context) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_FUND_ID, fundId);
        map.put(Constant.KEY_RATING, rating);
        new ApiController(Constant.URL_FUND_RATE, map, context, null, -1).execute();
    }
}
