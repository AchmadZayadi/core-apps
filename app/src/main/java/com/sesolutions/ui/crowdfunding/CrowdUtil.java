package com.sesolutions.ui.crowdfunding;


import android.content.Context;
import androidx.fragment.app.FragmentManager;

import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;

import java.util.HashMap;
import java.util.Map;

public class CrowdUtil {
    public static void openParentFragment(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , new CrowdParentFragment())
                .addToBackStack(null)
                .commit();
    }




    public static void openViewFragment(FragmentManager fragmentManager, int id) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewCrowdFragment.newInstance(id))
                .addToBackStack(null)
                .commit();
    }

    static void openEditFragment(FragmentManager fragmentManager, int id) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_FUND_ID, id);
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditFundFragment.newInstance(Constant.FormType.EDIT_FUND, map, Constant.URL_FUND_EDIT, null))
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

    static void openViewCategoryFragment(FragmentManager fragmentManager, int categoryId, String catName) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, CrowdCategoryViewFragment.newInstance(categoryId, catName))
                .addToBackStack(null)
                .commit();

    }

    public static void openCreateAnnouncementForm(FragmentManager fragmentManager, int fundId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_FUND_ID, fundId);
        fragmentManager.beginTransaction()
                .replace(R.id.container, FormFragment.newInstance(Constant.FormType.CREATE_ANNOUNCEMENT, map, Constant.URL_FUND_ANNOUNCEMENT_POST))
                .addToBackStack(null)
                .commit();
    }

    public static void openDonateForm(FragmentManager fragmentManager, int fundId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_FUND_ID, fundId);
        fragmentManager.beginTransaction()
                .replace(R.id.container, FormFragment.newInstance(Constant.FormType.CREATE_ANNOUNCEMENT, map, Constant.URL_FUND_ANNOUNCEMENT_POST))
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
