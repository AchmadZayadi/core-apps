package com.sesolutions.ui.store;


import android.content.Context;
import android.graphics.Paint;
import androidx.fragment.app.FragmentManager;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.ui.crowdfunding.CreateEditFundFragment;
import com.sesolutions.ui.crowdfunding.CrowdCategoryViewFragment;
import com.sesolutions.ui.events.ReviewCreateForm;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.ui.store.account.ViewOrderFragment;
import com.sesolutions.ui.store.cart.CartFragment;
import com.sesolutions.ui.store.cart.CheckoutFragment;
import com.sesolutions.ui.store.product.ProductCategoryViewFragment;
import com.sesolutions.ui.store.product.ProductViewFragment;
import com.sesolutions.ui.store.wishlist.ViewWishListFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;

import java.util.HashMap;
import java.util.Map;

public class StoreUtil {
    public static void openParentFragment(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , new StoreParentFragment())
                .addToBackStack(null)
                .commit();
    }

    public static void openCheckoutFragment(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , new CheckoutFragment())
                .addToBackStack(null)
                .commit();
    }

    public static void openCartFragment(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , new CartFragment())
                .addToBackStack(null)
                .commit();
    }

    public static void strikeThroughText(TextView tv) {
        tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static void openViewProductFragment(FragmentManager fragmentManager, int id) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ProductViewFragment.newInstance(id))
                .addToBackStack(null)
                .commit();
    }

    public static void openViewWishlistFragmnet(FragmentManager fragmentManager,String type, int id) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewWishListFragment.newInstance(type, id))
                .addToBackStack(null)
                .commit();
    }

    public static void openViewStoreFragment(FragmentManager fragmentManager, int storeId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewStoreFragment.newInstance(storeId))
                .addToBackStack(null)
                .commit();
    }

    public static void openViewStoreCategoryFragment(FragmentManager fragmentManager, int categoryId, String catName) {

        fragmentManager.beginTransaction()
                .replace(R.id.container, StoreCategoryViewFragment.newInstance(categoryId, catName))
                .addToBackStack(null)
                .commit();

    }

    public static void openViewProductCategoryFragment(FragmentManager fragmentManager, int categoryId, String catName) {

        fragmentManager.beginTransaction()
                .replace(R.id.container, ProductCategoryViewFragment.newInstance(categoryId, catName))
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

    public static void openCreateReview(FragmentManager fragmentManager, Map<String, Object> map, String url) {

        fragmentManager.beginTransaction()
                .replace(R.id.container, ReviewCreateForm.newInstance(Constant.FormType.CREATE_REVIEW, map, url))
                .addToBackStack(null)
                .commit();

    }

    public static void openViewOrderFragment(FragmentManager fragmentManager, int position) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewOrderFragment.newInstance(position))
                .addToBackStack(null)
                .commit();

    }
}
