package com.sesolutions.ui.store;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.packages.BrowseTransactionFragment;
import com.sesolutions.ui.packages.MyPackageFragment;
import com.sesolutions.ui.page.SearchPageAlbumFragment;
import com.sesolutions.ui.store.account.MyAccountFragment;
import com.sesolutions.ui.store.product.ProductFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.ui.wish.GlobalTabHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreParentFragment extends GlobalTabHelper {

    public static final String TYPE_CATEGORY_VIEW = "12";

    public static int storeVoCount = 0;

    private static final int PAGE_DEFAULT = 600;
    private List<Options> tempMenu;
    private FloatingActionButton fab;

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.CREATE_STORE) {
            activity.taskPerformed = 0;
            int myStoreIndex = getTabIndex(MenuTab.Store.MY_ACCOUNT);
            int browseIndex = getTabIndex(MenuTab.Store.STORE_BROWSE);

            tabLoaded[myStoreIndex] = false;
            tabLoaded[browseIndex] = false;
            if ("dashboard".equals(activity.stringValue)) {
                viewPager.setCurrentItem(browseIndex, true);
                if (selectedItem == browseIndex) {
                    adapter.getItem(browseIndex).onRefresh();
                }
            } else if ("manage".equals(activity.stringValue)) {
                viewPager.setCurrentItem(myStoreIndex, true);
                if (selectedItem == myStoreIndex) {
                    adapter.getItem(myStoreIndex).onRefresh();
                }
            } else {
                if (activity.taskId > 0) {
                    StoreUtil.openViewStoreFragment(fragmentManager, activity.taskId);
                }
            }
        } else if (activity.taskPerformed == Constant.FormType.EDIT_STORE) {
            activity.taskPerformed = 0;
            int myStoreIndex = getTabIndex(MenuTab.Store.MY_ACCOUNT);
            tabLoaded[myStoreIndex] = false;
            tabLoaded[getTabIndex(MenuTab.Store.STORE_BROWSE)] = false;
            viewPager.setCurrentItem(myStoreIndex);
            adapter.getItem(myStoreIndex).onRefresh();
//            loadFragmentIfNotLoaded(1);
        } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            int myStoreIndex = getTabIndex(MenuTab.Store.MY_ACCOUNT);
            int browseIndex = getTabIndex(MenuTab.Store.STORE_BROWSE);
//            int browsePollIndex = getTabIndex(MenuTab.Page.TYPE_BROWSE_POLL);
            tabLoaded[myStoreIndex] = false;
            tabLoaded[browseIndex] = false;
//            tabLoaded[browsePollIndex] = false;
            adapter.getItem(myStoreIndex).onRefresh();
            adapter.getItem(browseIndex).onRefresh();
//            adapter.getItem(browsePollIndex).onRefresh();
        } else if (activity.taskPerformed == 99) {
            activity.taskPerformed = 0;
            int browseProductIndex = getTabIndex(MenuTab.Store.PRODUCT);
            tabLoaded[2] = false;
            adapter.getItem(2).onRefresh();
            loadFragmentIfNotLoaded(2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_contest_parent, container, false);
        try {
            applyTheme(v);
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void init() {
        //first fetch tab items and then call init()
        v.findViewById(R.id.ivCart).setVisibility(View.VISIBLE);
        v.findViewById(R.id.ivCart).setOnClickListener(this);
        Map<String, Object> map = new HashMap<>();
        if (isNetworkAvailable(context)) {
            showBaseLoader(false);
            new ApiController(URL.STORE_MENU, map, context, this, PAGE_DEFAULT).execute();
        } else {
            notInternetMsg(v);
        }

        initButtons();
    }

    @Override
    public void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        tabItems = new ArrayList<>();
        for (Options opt : tempMenu) {
            switch (opt.getAction()) {
                case MenuTab.Store.HOME:
                    break;
                case MenuTab.Store.CREATE_STORE:
                    showFabIcon();
                    break;
                    // use Storefragment for categories
                case MenuTab.Store.CATEGORY_PRODUCT:
                    tabItems.add(opt);
                    adapter.addFragment(ProductFragment.newInstance(opt.getAction(), this), "Product Category");
                    break;
                case MenuTab.Store.CATEGORY_STORE:
                    tabItems.add(opt);
                    adapter.addFragment(StoreFragment.newInstance(opt.getAction(), this), "Store Category");
                    break;
                case MenuTab.Store.PRODUCT:
                    tabItems.add(opt);
                    adapter.addFragment(ProductFragment.newInstance(opt.getAction(), this), opt.getLabel());
                    break;
                case MenuTab.Store.STORE_BROWSE:
                    tabItems.add(opt);
                    adapter.addFragment(StoreBrowseFragment.newInstance(opt.getAction(), this), opt.getLabel());
                    break;
                case MenuTab.Store.MY_STORE:
                    tabItems.add(opt);
                    adapter.addFragment(StoreFragment.newInstance(opt.getAction(), this), opt.getLabel());
                    break;
                case MenuTab.Store.MY_ACCOUNT:
                    tabItems.add(opt);
                    adapter.addFragment(new MyAccountFragment(), opt.getLabel());
                    break;
                case MenuTab.Store.WISHLIST:
                    tabItems.add(opt);
                    adapter.addFragment(ProductFragment.newInstance(opt.getAction(), this), opt.getLabel());
                    break;
                default:
                    tabItems.add(opt);
                    adapter.addFragment(ProductFragment.newInstance(opt.getAction(), this), opt.getLabel());
                    break;
            }
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    @Override
    public void updateToolbarIcons(int position) {
        selectedItem = position;
        ivSearch.setVisibility(canShowSearch(tabItems.get(position).getAction()) ? View.VISIBLE : View.GONE);
        if (canShowCreate(tabItems.get(position).getAction())) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    private boolean canShowSearch(String name) {
        switch (name) {
            case MenuTab.Store.CATEGORY_STORE:
//            case MenuTab.Store.STORE_BROWSE:
            case MenuTab.Store.PRODUCT:
            case MenuTab.Store.MY_ACCOUNT:
            case MenuTab.Store.CATEGORY_PRODUCT:
            case MenuTab.Store.WISHLIST:
                return false;
        }
        return true;
    }

    private boolean canShowCreate(String name) {
        switch (name) {
            case MenuTab.Store.STORE_BROWSE:
            case MenuTab.Store.CATEGORY_STORE:
                return true;
        }
        return false;
    }

    @Override
    public void refreshScreenByPosition(int position) {
    }

    @Override
    public void loadFragmentIfNotLoaded(int position) {
        try {
            if (!tabLoaded[position])
                (adapter.getItem(position)).initScreenData();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        try {
            switch (object1) {
                case PAGE_DEFAULT:
                    hideBaseLoader();

                    if (object2 != null) {
                        PageResponse resp = new Gson().fromJson((String) object2, PageResponse.class);
                        if (resp.isSuccess()) {
                            if (resp.getResult() != null) {
                                tempMenu = resp.getResult().getMenus();
                                super.init();
                                ivSearch.setVisibility(View.VISIBLE);
                            } else {
                                somethingWrongMsg(v);
                            }
                        } else {
                            Util.showSnackbar(v, resp.getErrorMessage());
                            goIfPermissionDenied(resp.getError());
                        }
                    } else {
                        somethingWrongMsg(v);
                    }
                    break;
                case Constant.Events.SET_LOADED:
                    updateLoadStatus("" + object2, true);
                    break;
                case Constant.Events.UPDATE_TOTAL:
                    updateTotal("" + object2, postion);
                    break;
                case Constant.Events.CLICKED_OPTION:
                    v.findViewById(R.id.ll1).setVisibility(View.GONE);
                    v.findViewById(R.id.ll4).setVisibility(View.GONE);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
            somethingWrongMsg(v);
        }
        return super.onItemClicked(object1, object2, postion);
    }

    private void initButtons() {
        fab = v.findViewById(R.id.fabAdd);
        v.findViewById(R.id.llCreateMenu).setOnClickListener(this);
        //v.findViewById(R.id.fabCreate).setOnClickListener(this);

        v.findViewById(R.id.ll1).setOnClickListener(this);
        v.findViewById(R.id.ll2).setOnClickListener(this);
        v.findViewById(R.id.ll3).setOnClickListener(this);
        v.findViewById(R.id.ll4).setOnClickListener(this);
        v.findViewById(R.id.ll5).setOnClickListener(this);

        ((TextView) v.findViewById(R.id.tvCreate)).setText(R.string.title_create_store);

        // v.findViewById(R.id.ll1).setVisibility(resp.getPermission().getCanCreateAlbum() ? View.VISIBLE : View.GONE);
        //v.findViewById(R.id.ll2).setVisibility(null != myPackageList && myPackageList.size() > 0 ? View.VISIBLE : View.GONE);
        //v.findViewById(R.id.ll3).setVisibility(View.GONE);
        //v.findViewById(R.id.ll4).setVisibility(isMyPackageSelected ? View.VISIBLE : View.GONE);
        super.updateFabColor(v.findViewById(R.id.fabAdd));
    }

    private void showBottomOptions() {
        //  v.findViewById(R.id.ll4).setVisibility(isMyPackageSelected ? View.VISIBLE : View.GONE);
        //((TextView) v.findViewById(R.id.tvManage)).setText(isMyPackageSelected ? R.string.all_packages : R.string.my_package);
        this.v.findViewById(R.id.llCreateMenu).setVisibility(View.VISIBLE);
        this.v.findViewById(R.id.ll2).setVisibility(View.GONE);
        this.v.findViewById(R.id.ll4).setVisibility(getTotal(getTabIndex(MenuTab.Store.PACKAGE)) > 0 ? View.VISIBLE : View.GONE);
        ((FloatingActionButton) v.findViewById(R.id.fabAdd)).hide();
    }


    @Override
    public void onBackPressed() {
        if (v.findViewById(R.id.llCreateMenu).getVisibility() == View.VISIBLE) {
            hideBottomOptions();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showFabIcon() {
        new Handler().postDelayed(this::hideBottomOptions, 1000);
    }

    private void hideBottomOptions() {
        this.v.findViewById(R.id.llCreateMenu).setVisibility(View.GONE);
        ((FloatingActionButton) v.findViewById(R.id.fabAdd)).show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.ivCart:
//                StoreUtil.openCheckoutFragment(fragmentManager);
                StoreUtil.openCartFragment(fragmentManager);
                break;

            case R.id.ivFilter:
                //TODO  20/9/2018 add filter ;logic on @PageFragment and iuncomment this code
                // ((PageFragment) adapter.getItem(selectedItem)).onFilterClick(ivFilter);
                break;

            case R.id.ll1:
                hideBottomOptions();
                ((MyPackageFragment) adapter.getItem(selectedItem)).callCreateContestApi();
                break;
            case R.id.ll2:
                // hideBottomOptions();
                // isMyPackageSelected = !isMyPackageSelected;
                // ChangePackageViewingType(isMyPackageSelected);
                break;
            case R.id.ll3:
                hideBottomOptions();
                fragmentManager.beginTransaction().replace(R.id.container, BrowseTransactionFragment.newInstance(Constant.ResourceType.STORE)).addToBackStack(null).commit();
                break;
            case R.id.ll4:
                hideBottomOptions();
                ((MyPackageFragment) adapter.getItem(selectedItem)).showCancelDialog();
                //callCancelPackageApi();
                break;
            case R.id.llCreateMenu:
            case R.id.ll5:
                hideBottomOptions();
                break;

        }
    }


    @Override
    public void openCreateForm() {
        //in case of public user ,send him to sign-in screen
        if (!SPref.getInstance().isLoggedIn(context)) {
            goToWelcome(1);
            return;
        }
        if (MenuTab.Store.PACKAGE.equals(tabItems.get(selectedItem).getAction())) {
            showBottomOptions();
        } else if (MenuTab.Page.TYPE_BROWSE_POLL.equals(tabItems.get(selectedItem).getAction())) {
            //TODO open create page poll fragment
        } else {
            fetchFormData();
        }
    }

    @Override
    public void goToSearchFragment() {
        if (MenuTab.Store.ALBUM.equals(tabItems.get(selectedItem).getAction())) {
            HashMap<String, Object> map = new HashMap<>();
            //  map.put(Constant.KEY_PAGE_ID, mPageId);
            map.put(Constant.KEY_URI, URL.STORE_ALBUM);
            map.put(Constant.SELECT, MenuTab.Store.ALBUM);
            map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PAGE);
            fragmentManager.beginTransaction().replace(R.id.container, SearchPageAlbumFragment.newInstance(null, map)).addToBackStack(null).commit();
        } else if (MenuTab.Page.TYPE_BROWSE_POLL.equals(tabItems.get(selectedItem).getAction())) {
            //TODO open  search poll fragment
        } else {
//            v.findViewById(R.id.ivCart).setVisibility(View.GONE);
            fragmentManager.beginTransaction().replace(R.id.container, new SearchStoreFragment()).addToBackStack(null).commit();
        }
    }

    private void fetchFormData() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_STORE_CREATE);
                    request.params.put(Constant.KEY_GET_FORM, 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("create_store_response", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (err.isSuccess()) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (resp != null && resp.getResult() != null && resp.getResult().arePackagesAvailabel()) {
                                        openSelectPackage(resp.getResult().getPackages(), resp.getResult().getExistingPackage(), null, Constant.ResourceType.STORE);
                                    } else if (resp != null && resp.getResult() != null && resp.getResult().getCategory() != null) {
                                        openSelectCategory(resp.getResult().getCategory(), null, Constant.ResourceType.STORE);
                                    } else {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                            openStoreCreateForm(vo.getResult(), new HashMap<String, Object>());
                                        }
                                    }
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            } else {
                                Util.showSnackbar(v, getStrings(R.string.msg_something_wrong));
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);
                } catch (Exception ignore) {

                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public int getTabIndex(String selectedScreen) {
        for (int i = 0; i < tabItems.size(); i++) {
            if (tabItems.get(i).getAction().equals(selectedScreen)) {
                return i;
            }
        }
        return -1;
    }

    public void updateTotal(String index, int count) {
        updateTotal(getTabIndex(index), count);
    }

    public void updateLoadStatus(String selectedScreen, boolean isLoaded) {
        try {
            tabLoaded[getTabIndex(selectedScreen)] = isLoaded;
        } catch (Exception e) {
            CustomLog.e("AIOOBE", "tabItem not found ->" + selectedScreen);
        }
    }
}
