package com.sesolutions.ui.wish;


import android.view.View;

import com.sesolutions.R;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.quotes.CreateQuoteFragment;
import com.sesolutions.ui.video.CreateVideoForm;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

public class WishParentFragment extends GlobalTabHelper {

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.CREATE_WISH) {
            activity.taskPerformed = 0;
            isBrowseLoaded = false;
            isManageLoaded = false;
            loadFragmentIfNotLoaded(1);
            goToViewWishFragment(activity.taskId);
        } else if (activity.taskPerformed == Constant.FormType.EDIT_WISH) {
            activity.taskPerformed = 0;
            isBrowseLoaded = false;
            isManageLoaded = false;
            loadFragmentIfNotLoaded(1);
        } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            isBrowseLoaded = false;
            isManageLoaded = false;
            refreshScreenByPosition(0);
            refreshScreenByPosition(1);
            // loadFragmentIfNotLoaded(0);
        }
    }

    @Override
    public void init() {
        FORM_CREATE = Constant.FormType.CREATE_WISH;
        FORM_CREATE_URL = Constant.URL_CREATE_WISH;
        super.init();
    }

    @Override
    public void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        adapter.addFragment(BrowseWishFragment.newInstance(this, 0), getStrings(R.string.TAB_TITLE_WISH_1));

        if(SPref.getInstance().isLoggedIn(context)){
            adapter.addFragment(ManageWishFragment.newInstance(this, 0), getStrings(R.string.TAB_TITLE_WISH_2));
        }
        // adapter.addFragment(QuotesCategoriesFragment.newInstance(this), Constant.TAB_TITLE_QUOTES_3);
        if (SPref.getInstance().isLoggedIn(context)) {
            showFabIcon();
            // adapter.addFragment(CreateQuoteFragment.newinstance(Constant.FormType.CREATE_QUOTE, Constant.URL_CREATE_QUOTE, this), Constant.TAB_TITLE_QUOTES_4);
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        // viewPager.setCurrentItem(0);
    }

    @Override
    public void updateToolbarIcons(int position) {
        selectedItem = position;
        ivSearch.setVisibility(position <= 1 ? View.VISIBLE : View.GONE);
    }


    @Override
    public void refreshScreenByPosition(int position) {

        try {
            switch (position) {
                case 0:
                    if (!isBrowseLoaded)
                        ((BrowseWishFragment) (adapter.getItem(position))).onRefresh();
                    break;

                case 1:
                    if (!isManageLoaded)
                        ((ManageWishFragment) adapter.getItem(position)).onRefresh();
                    break;

             /*   case 2:
                    if (!isCategoryLoaded)
                        //  ((QuotesCategoriesFragment) adapter.getItem(position)).
                        break;
                case 3:
                    if (!isMyAlbumLoaded)
                        ((MyAlbumFragment) adapter.getItem(position)).onRefresh();
                    break;
*/
                case 2:
                    // if (!isPostVideoLoaded)
                    ((CreateVideoForm) adapter.getItem(position)).initScreenData();
                    //  break;


            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void loadFragmentIfNotLoaded(int position) {

        try {
            switch (position) {
                case 0:
                    if (!isBrowseLoaded)
                        (adapter.getItem(position)).initScreenData();
                    break;

                case 1:
                    if (!isManageLoaded)
                        adapter.getItem(position).initScreenData();
                    break;

            /*    case 2:
                    if (!isCategoryLoaded)
                        ((QuotesCategoriesFragment) adapter.getItem(position)).initScreenData();
                    break;*/
              /*  case 3:
                    if (!isMyAlbumLoaded)
                        ((MyAlbumFragment) adapter.getItem(position)).initScreenData();
                    break;*/

                case 2:
                    // if (!isPostVideoLoaded)
                    ((CreateQuoteFragment) adapter.getItem(position)).initScreenData();
                    //  break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void openCreateForm() {

        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateQuoteFragment.newinstance(Constant.FormType.CREATE_WISH, Constant.URL_CREATE_WISH, null))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchWishFragment()).addToBackStack(null).commit();
    }
}
