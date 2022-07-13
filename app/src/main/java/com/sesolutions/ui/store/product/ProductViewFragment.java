package com.sesolutions.ui.store.product;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rd.PageIndicatorView;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.store.StoreContent;
import com.sesolutions.responses.store.product.ProductResponse;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.events.HtmlTextFragment;
import com.sesolutions.ui.groups.ClaimFormFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.music_album.AddToPlaylistFragment;
import com.sesolutions.ui.page.AnnouncementFragment;
import com.sesolutions.ui.page.PageAlbumFragment;
import com.sesolutions.ui.page.PageFragment;
import com.sesolutions.ui.page.PageMapFragment;
import com.sesolutions.ui.page.PageServicesFragment;
import com.sesolutions.ui.page.PageVideoFragment;
import com.sesolutions.ui.page.ViewPageAlbumFragment;
import com.sesolutions.ui.poll.profile_poll.ProfilePollFragment;
import com.sesolutions.ui.profile.FeedFragment;
import com.sesolutions.ui.profile.ProfileTabsAdapter;
import com.sesolutions.ui.review.PageProfileReviewFragment;
import com.sesolutions.ui.store.StoreUtil;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;

public class ProductViewFragment extends CommentLikeHelper implements PopupMenu.OnMenuItemClickListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, TabLayout.OnTabSelectedListener, OnUserClickedListener<Integer, Object> {//}, ParserCallbackInterface {

    private static final int REQ_UPDATE_UPPER = 99;

    private View v;
    private MessageDashboardViewPagerAdapter adapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ProductResponse.Result result;

    private int mProductId;
    private int mUserId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MultiSnapRecyclerView rvPhotos;
    private PageIndicatorView pageIndicatorView;

    private List<StoreContent> relatedList;
    private List<ProductResponse.SliderImage> photoList;
    private boolean[] isLoaded;
    private AppBarLayout appBarLayout;
    private AppCompatImageView addedtoWishlist;

    private TextView price, discountedPrice, tvRatingTotal, tvRatingCount;
    private StoreContent mProduct;


    public static ProductViewFragment newInstance(int productId) {

        ProductViewFragment fragment = new ProductViewFragment();
        fragment.mProductId = productId;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            switch (activity.taskPerformed) {
//                case Constant.TASK_IMAGE_UPLOAD:
//                    if (activity.taskId == Constant.TASK_PHOTO_UPLOAD) {
//                        result.getPage().getImages().setMain(Constant.BASE_URL + activity.stringValue);
//                        updateProfilePhoto(Constant.BASE_URL + activity.stringValue);
//                    } else if (activity.taskId == Constant.TASK_COVER_UPLOAD) {
//                        result.getPage().getCoverImage().setMain(Constant.BASE_URL + activity.stringValue);
//                        updateCoverPhoto(Constant.BASE_URL + activity.stringValue);
//                    }
//                    activity.taskPerformed = 0;
//                    break;
                case Constant.FormType.CREATE_ALBUM_OTHERS:
                    activity.taskPerformed = 0;
                    int pos = getTabPositionByName(Constant.TabOption.ALBUM);
                    //collapse app bar layout
                    appBarLayout.setExpanded(false, true);
                    //set album tab and refresh data
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();

                    //open view album
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put(Constant.KEY_PRODUCT_ID, mProductId);
                    map1.put(Constant.KEY_ALBUM_ID, activity.taskId);
                    map1.put(Constant.KEY_URI, Constant.URL_PAGE_ALBUM_VIEW);
                    map1.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT_ALBUM);
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ViewPageAlbumFragment.newInstance(map1, null))
                            .addToBackStack(null).commit();
                    break;

                case Constant.FormType.CREATE_PAGE_VIDEO:
                    activity.taskPerformed = 0;
                    pos = getTabPositionByName(Constant.TabOption.VIDEO);
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    break;

                case Constant.FormType.EDIT_REVIEW:
                    activity.taskPerformed = 0;
                    pos = getTabPositionByName(Constant.TabOption.REVIEW);
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    break;
                case Constant.FormType.CREATE_REVIEW:
                    activity.taskPerformed = 0;
                    pos = getTabPositionByName(Constant.TabOption.REVIEW);
                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    goToViewReviewFragment(Constant.ResourceType.PRODUCT_REVIEW, activity.taskId);
                    break;

                case Constant.FormType.CREATE_POLL:
                case Constant.TASK_ALBUM_DELETED:
                    activity.taskPerformed = 0;
                    appBarLayout.setExpanded(false, true);
                    pos = getTabPositionByName(Constant.TabOption.POLL);
                    viewPager.setCurrentItem(pos, true);
                    (adapter.getItem(pos)).onRefresh();
                    openViewPollFragment(MenuTab.Page.TYPE_PROFILE_POLL, activity.taskId);
            }

               /* case Constant.Task.ALBUM_DELETED:
                case Constant.Task.NOTE_DELETED:
                    activity.taskPerformed = 0;
                    swipeRefreshLayout.setEnabled(true);
                    onRefresh();
                    break;*/

            if (Constant.TASK_POST) {
                Constant.TASK_POST = false;
                appBarLayout.setExpanded(false, true);
                viewPager.setCurrentItem(0, true);
                adapter.getItem(0).onRefresh();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // Inflate the layout for this fragment
        if (v != null) {
            return v;
        }
        try {
            v = inflater.inflate(R.layout.fragment_product_view, container, false);
            applyTheme(v);
            swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.setEnabled(false);

            callProductApi(1);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }
//    private void setProductThumbnailList() {
//        MultiSnapRecyclerView rvThumbnail = v.findViewById(R.id.rvThumbnail);
//        rvThumbnail.setHasFixedSize(true);
//        //LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
//        //holder.rvCommentAttachment.setLayoutManager(layoutManager);
//        rvThumbnail.setAdapter(new ProductThumbAdapter(new ArrayList<>(), context, this));
//    }

    private ProductSliderAdapter adapterPhoto;

    private void initPhoto() {
        rvPhotos = v.findViewById(R.id.rvChild);
        photoList = new ArrayList<ProductResponse.SliderImage>();
        if (adapterPhoto == null) {
            rvPhotos.setHasFixedSize(true);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvPhotos.setLayoutManager(layoutManager);
            adapterPhoto = new ProductSliderAdapter(photoList, context, this, true);
            rvPhotos.setAdapter(adapterPhoto);
            pageIndicatorView = v.findViewById(R.id.pageIndicatorView);

//            pageIndicatorView.setCount(adapterPhoto.getItemCount());
//            pageIndicatorView.setUnselectedColor(Color.parseColor(Constant.dividerColor));
//            pageIndicatorView.setSelectedColor(Color.parseColor(Constant.colorPrimary));
              rvPhotos.setOnSnapListener(position1 -> pageIndicatorView.setSelection(position1));
        } else {
            adapterPhoto.notifyDataSetChanged();
            pageIndicatorView.setSelection(0);
        }
    }


    private void initUI() {
        //v.findViewById(R.id.rlMain).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
//        v.findViewById(R.id.ivBack).setOnClickListener(this);
//        setProductThumbnailList();
//        setUpBlurView();
        try {
            v.findViewById(R.id.rlDetail).setVisibility(View.VISIBLE);
            price = v.findViewById(R.id.tvPrice2);
            discountedPrice = v.findViewById(R.id.tvPrice);
            tvRatingTotal = v.findViewById(R.id.tvRatingTotal);
            tvRatingCount = v.findViewById(R.id.tvRatingCount);
            addedtoWishlist = v.findViewById(R.id.ivFavorite);


            // getActivity().invalidateOptionsMenu();
            initCollapsingToolbar();
            initPhoto();
            setUpperUIData();
            initTablayout();
        // todo addtocart setting
            if (result.getStore().getCanAddToCart() != 0) {
                v.findViewById(R.id.llCartAdd).setVisibility(View.VISIBLE);
                v.findViewById(R.id.llCartAdd).setOnClickListener(this);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setUpperUIData() {

        mUserId = SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID);
        if (mProduct != null) {
            v.findViewById(R.id.rlDetail).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
//            v.findViewById(R.id.llDetail).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
            v.findViewById(R.id.rlDetail).setVisibility(View.VISIBLE);
            StoreContent resp = result.getStore();
            ((TextView) v.findViewById(R.id.tvProductTitle)).setText(resp.getTitle());
            if (mProduct.getVerified() != 0)
                ( v.findViewById(R.id.ivVerified)).setVisibility(View.VISIBLE);

            if(mProduct.getDiscount() == 1) {
                price.setVisibility(View.VISIBLE);
                try {
                    double newdoble=Double.parseDouble(mProduct.getPrice())*100;
                    int newpr= (int) (newdoble);
                    double myprr=((double)newpr)/100.00;
                    price.setText(mProduct.getCurrency() + myprr);
                }catch (Exception ex){
                    ex.printStackTrace();
                    price.setText(mProduct.getCurrency() + mProduct.getPrice());
                }

                try {
                    double newdoble=Double.parseDouble(mProduct.getPriceWithDiscount())*100;
                    int newpr= (int) (newdoble);
                    double myprr=((double)newpr)/100.00;
                    discountedPrice.setText(mProduct.getCurrency() + myprr);
                }catch (Exception ex){
                    ex.printStackTrace();
                    discountedPrice.setText(mProduct.getCurrency() + mProduct.getPriceWithDiscount());
                }

                StoreUtil.strikeThroughText(price);
            }
            else{
                try {
                    double newdoble=Double.parseDouble(mProduct.getPrice())*100;
                    int newpr= (int) (newdoble);
                    double myprr=((double)newpr)/100.00;
                    discountedPrice.setText(mProduct.getCurrency() + myprr);
                }catch (Exception ex){
                    ex.printStackTrace();
                    discountedPrice.setText(mProduct.getCurrency() + mProduct.getPrice());
                }
            }

            tvRatingCount.setText(mProduct.getRating());
            tvRatingTotal.setText(mProduct.getReviewCount());
            v.findViewById(R.id.cvCreateReview).setOnClickListener(this);
            v.findViewById(R.id.ll_add_to_wishlist).setOnClickListener(this);

            updatePhotoAdapter();
        }
    }

    private void updatePhotoAdapter() {
        try {
            photoList.clear();
            if (result.getSliderImages() != null && result.getSliderImages().size() > 0) {
                photoList.addAll(result.getSliderImages());
                pageIndicatorView.setCount(photoList.size());
                adapterPhoto.notifyDataSetChanged();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void callProductApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    if (req == 1) {
                        showBaseLoader(true);
                    } else if (req == REQ_UPDATE_UPPER) {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_PRODUCT);
                    request.params.put(Constant.KEY_PRODUCT_ID, mProductId);
                    request.params.put(Constant.KEY_TYPE, Constant.ResourceType.PRODUCT);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideAllLoaders();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("view_product_response", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        ProductResponse commonResponse = new Gson().fromJson(response, ProductResponse.class);
                                        if (commonResponse.getResult() != null) {
                                            result = commonResponse.getResult();
                                        }

                                        if (req == REQ_UPDATE_UPPER) {
                                            setUpperUIData();
                                        } else {

                                            if (null != result.getStore()){
                                                mProduct = result.getStore();
                                            }
                                            initUI();
                                        }

                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
                                }

                            } catch (Exception e) {
                                somethingWrongMsg(v);
                                CustomLog.e(e);
                            }
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {

                    hideBaseLoader();
                }
            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideAllLoaders();
            CustomLog.e(e);
        }
    }

    private void hideAllLoaders() {
        try {
            swipeRefreshLayout.setRefreshing(false);
            hideBaseLoader();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

//    private void setUpBlurView() {
//        final float radius = 7f;
//        final float minBlurRadius = 10f;
//        final float step = 4f;
//
//        //set background, if your root layout doesn't have one
//        final Drawable windowBackground = ContextCompat.getDrawable(context, R.drawable.dummy_profile);//activity.getWindow().getDecorView().getBackground();
//        ((BlurView) v.findViewById(R.id.blurView)).setupWith(v.findViewById(R.id.root))
//                .setFrameClearDrawable(windowBackground)
//                .setBlurAlgorithm(new SupportRenderScriptBlur(context))
//                .setBlurRadius(radius)
//                .setHasFixedTransformationMatrix(true);
//    }

    private CollapsingToolbarLayout collapsingToolbar;

    private void initCollapsingToolbar() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setContentScrimColor(Color.parseColor(Constant.colorPrimary));
        appBarLayout = v.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                try {
                    //  swipeRefreshLayout.setEnabled((verticalOffset == 0));
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbar.setTitle(result.getStore().getTitle());
                        isShow = true;
                    } else if (isShow) {
                        collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }
        });
    }

    //set tab bar items
    private void initTablayout() {
       // tabLayout = v.findViewById(R.id.tabs);
        // Todo set menu from result
        if (result.getMenus() != null) {
            //create a boolean array that can be used in preventing multple loading of any tab
            isLoaded = new boolean[result.getMenus().size()];
            setupViewPager();
         //   tabLayout.clearOnTabSelectedListeners();
        //    tabLayout.setupWithViewPager(viewPager, true);
            applyTabListener();

            RecyclerView profiletabs = v.findViewById(R.id.profiletabs);
            if(result.getMenus()!=null && result.getMenus().size()>0){
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                profiletabs.setLayoutManager(layoutManager);
                ProfileTabsAdapter adapter1    = new ProfileTabsAdapter(result.getMenus(), context, this);
                profiletabs.setAdapter(adapter1);
                profiletabs.setVisibility(View.VISIBLE);

            }else {
                profiletabs.setVisibility(View.GONE);
            }

            new Handler().postDelayed(() -> loadScreenData(0), 200);
        } else {
        //    tabLayout.setVisibility(View.GONE);
        }
    }

    private void setupViewPager() {
        try {
            viewPager = v.findViewById(R.id.viewPager);
            adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
            adapter.showTab(true);
            List<Options> list = result.getMenus();
            // Todo set menus at runtime here and remove hard-coded
            for (Options opt : list) {
                //adapter.addFragment(getFragmentByName(opt.getName()), opt.getLabel());
                switch (opt.getName()) {
                    case Constant.TabOption.INFO:
                        Bundle bundle = new Bundle();
                        adapter.addFragment(ProductInfoFragment.newInstance(mProductId), opt.getLabel());
                        break;

                    case Constant.TabOption.ALBUM:
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_PRODUCT_ID, mProductId);
                        map.put(Constant.KEY_URI, Constant.URL_PRODUCT_ALBUM);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT);
                        adapter.addFragment(PageAlbumFragment.newInstance(map), opt.getLabel());
                        break;

                    case Constant.TabOption.REVIEW:
                        map = new HashMap<>();
                        map.put(Constant.KEY_PRODUCT_ID, mProductId);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT_REVIEW);
                        adapter.addFragment(PageProfileReviewFragment.newInstance(opt.getName(), this, map), opt.getLabel());
                        break;

                    case Constant.TabOption.VIDEO:
                        adapter.addFragment(PageVideoFragment.newInstance(opt.getName(), Constant.ResourceType.PRODUCT, mProductId, this), opt.getLabel());
                        break;

                    case Constant.TabOption.POSTS:
                        adapter.addFragment(FeedFragment.newInstance(mProductId, "sesproduct"), opt.getLabel());
                        break;

                    case Constant.TabOption.MEMBERS:
                        // Todo replace it with storemamber fragment
                        adapter.addFragment(FeedFragment.newInstance(mProductId, Constant.ResourceType.PRODUCT), opt.getLabel());
                        break;

                    case Constant.TabOption.MAP:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_PRODUCT_MAP);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mProductId);
                        map = new HashMap<>();
                        map.put(Constant.KEY_PRODUCT_ID, mProductId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT);
                        adapter.addFragment(PageMapFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
                        map = new HashMap<>();
                        map.put(Constant.TEXT, result.getStore().getDescription());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());
                        break;

                    case Constant.TabOption.POLL:
                        map = new HashMap<>();
                        map.put(Constant.KEY_BUSINESS_ID, mProductId);
                        adapter.addFragment(ProfilePollFragment.newInstance(MenuTab.Business.TYPE_PROFILE_POLL, this, map), opt.getLabel());
                        break;

                    case Constant.TabOption.ANNOUNCE:
                        bundle = new Bundle();
                        map = new HashMap<>();
                        map.put(Constant.KEY_BUSINESS_ID, mProductId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_URI, Constant.URL_BUSINESS_ANNOUNCE);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS);
                        adapter.addFragment(AnnouncementFragment.newInstance(bundle), opt.getLabel());
                        break;
                    case Constant.TabOption.SERVICES:
                        bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_BUSINESS_SERVICES);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mProductId);// mPageId);
                        map = new HashMap<>();
                        map.put(Constant.KEY_BUSINESS_ID, mProductId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS);
                        adapter.addFragment(PageServicesFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case "claim":
                        map = new HashMap<>();
                        map.put(Constant.KEY_BUSINESS_ID, mProductId);
                        adapter.addFragment(ClaimFormFragment.newInstance(Constant.FormType.CLAIM, map, Constant.URL_BUSINESS_CLAIM), opt.getLabel());
                        break;
                    case "upsell":

                        adapter.addFragment(ProductFragment.newInstance("upsell_product", mProductId), opt.getLabel());
                        break;

                    default:
                        CustomLog.e("Not Handled", "handle this profile widget name:" + opt.getName() + " __Label: " + opt.getLabel());
                        break;
                }
            }

            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(isLoaded.length);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        //Todo
        callProductApi(Constant.REQ_CODE_REFRESH);
    }


    private void applyTabListener() {

     //   tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        loadScreenData(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        try {
                    /*if (tab.getPosition() == 0) {
                        ((VideoHelper) adapter.getItem(tab.getPosition())).scrollToStart();
                    }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void loadScreenData(int position) {
        // do not load tab if already loaded
        if (!isLoaded[position] && isNetworkAvailable(context)) {
            isLoaded[position] = true;
            adapter.getItem(position).initScreenData();
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.llCartAdd:
                    StoreUtil.openCartFragment(fragmentManager);
                    break;
                case R.id.cvCreateReview:
//                    HashMap<String, Object> map = new HashMap<>();
//                    map.put(Constant.KEY_PRODUCT_ID, mProductId);
//                    map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT_REVIEW);
//                    StoreUtil.openCreateReview(fragmentManager, map, URL.URL_PRODUCT_REVIEW_CREATE);

                    appBarLayout.setExpanded(false, true);
                    viewPager.setCurrentItem(getTabPositionByName(Constant.TabOption.REVIEW));
                    break;
                case R.id.ll_add_to_wishlist:

//                    addedtoWishlist.setColorFilter(result.isContentFavourite() ? Color.parseColor(Constant.red) : Color.parseColor(Constant.text_color_1));
                    addedtoWishlist.setColorFilter(Color.parseColor(Constant.red));
                    goToFormFragment(v.getId(),mProductId);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


        private void goToFormFragment(int screenType, int position) {
            Map<String, Object> map = new HashMap<>();
            int type;
            if (screenType == Constant.FormType.TYPE_MUSIC_ALBUM) {
                type = Constant.FormType.TYPE_ADD_ALBUM;
                map.put(Constant.KEY_ALBUM_ID, relatedList.get(position).getProductId());
            } else {
                type = Constant.FormType.TYPE_ADD_WISHLIST;
                map.put(Constant.KEY_PRODUCT_ID, mProductId);
            }

            fragmentManager.beginTransaction()
                    .replace(R.id.container,
                            AddToPlaylistFragment.newInstance(type, map, Constant.URL_ADD_WISHLIST))
                    .addToBackStack(null)
                    .commit();
        }



    private int getTabPositionByName(String name) {
        int position = 0;
        for (int i = 0; i < result.getMenus().size(); i++) {
            if (result.getMenus().get(i).getName().equals(name)) {
                position = i;
                break;
            }
        }
        return position-1;
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position)
    {
        try {
              switch (eventType) {
                case Constant.Events.TAB_OPTION_PROFILE:
                    // handleTabOptionClicked("" + value, postion);
                    callFragment(data);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return  false;
    }

     /*case Constant.TabOption.INFO:
    Bundle bundle = new Bundle();
                        adapter.addFragment(ProductInfoFragment.newInstance(mProductId), opt.getLabel());
                        break;

                    case Constant.TabOption.ALBUM:
    HashMap<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_PRODUCT_ID, mProductId);
                        map.put(Constant.KEY_URI, Constant.URL_PRODUCT_ALBUM);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT);
                        adapter.addFragment(PageAlbumFragment.newInstance(map), opt.getLabel());
                        break;

                    case Constant.TabOption.REVIEW:
    map = new HashMap<>();
                        map.put(Constant.KEY_PRODUCT_ID, mProductId);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT_REVIEW);
                        adapter.addFragment(PageProfileReviewFragment.newInstance(opt.getName(), this, map), opt.getLabel());
                        break;

                    case Constant.TabOption.VIDEO:
            adapter.addFragment(PageVideoFragment.newInstance(opt.getName(), Constant.ResourceType.PRODUCT, mProductId, this), opt.getLabel());
                        break;

                    case Constant.TabOption.POSTS:
            adapter.addFragment(FeedFragment.newInstance(mProductId, "sesproduct"), opt.getLabel());
                        break;

                    case Constant.TabOption.MEMBERS:
            adapter.addFragment(FeedFragment.newInstance(mProductId, Constant.ResourceType.PRODUCT), opt.getLabel());
                        break;

                    case Constant.TabOption.MAP:
    bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_PRODUCT_MAP);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mProductId);
    map = new HashMap<>();
                        map.put(Constant.KEY_PRODUCT_ID, mProductId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT);
                        adapter.addFragment(PageMapFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case Constant.TabOption.OVERVIEW:
    map = new HashMap<>();
                        map.put(Constant.TEXT, result.getStore().getDescription());
                        map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));
                        adapter.addFragment(HtmlTextFragment.newInstance(map, null), opt.getLabel());
                        break;

                    case Constant.TabOption.POLL:
    map = new HashMap<>();
                        map.put(Constant.KEY_BUSINESS_ID, mProductId);
                        adapter.addFragment(ProfilePollFragment.newInstance(MenuTab.Business.TYPE_PROFILE_POLL, this, map), opt.getLabel());
                        break;

                    case Constant.TabOption.ANNOUNCE:
    bundle = new Bundle();
    map = new HashMap<>();
                        map.put(Constant.KEY_BUSINESS_ID, mProductId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_URI, Constant.URL_BUSINESS_ANNOUNCE);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS);
                        adapter.addFragment(AnnouncementFragment.newInstance(bundle), opt.getLabel());
                        break;
                    case Constant.TabOption.SERVICES:
    bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_BUSINESS_SERVICES);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mProductId);// mPageId);
    map = new HashMap<>();
                        map.put(Constant.KEY_BUSINESS_ID, mProductId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS);
                        adapter.addFragment(PageServicesFragment.newInstance(bundle), opt.getLabel());
                        break;

                    case "claim":
    map = new HashMap<>();
                        map.put(Constant.KEY_BUSINESS_ID, mProductId);
                        adapter.addFragment(ClaimFormFragment.newInstance(Constant.FormType.CLAIM, map, Constant.URL_BUSINESS_CLAIM), opt.getLabel());
                        break;
                    case "upsell":

                            adapter.addFragment(ProductFragment.newInstance("upsell_product", mProductId), opt.getLabel());
                        break;

    default:
            CustomLog.e("Not Handled", "handle this profile widget name:" + opt.getName() + " __Label: " + opt.getLabel());
                        break;*/

    private void callFragment(Object value) {
        Options opt= (Options) value;
        Intent intent2=null;
        Bundle bundle = new Bundle();
        HashMap<String, Object> map = new HashMap<>();
        switch (opt.getName()){
            case Constant.TabOption.INFO:


                //    goToProfileInfo(userId, false);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_S_PRODUCT_INFO);
                intent2.putExtra(Constant.KEY_ID, mProductId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                //  adapter.addFragment(InfoFragment.newInstance(userId, false), opt.getLabel());

                break;

            case Constant.TabOption.ALBUM:
                //goToSearchAlbumFragment(userId);
                bundle = new Bundle();
                map = new HashMap<>();

                map.put(Constant.KEY_PRODUCT_ID, mProductId);
                map.put(Constant.KEY_URI, Constant.URL_PRODUCT_ALBUM);
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT);
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ALBUM);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(SearchAlbumFragment.newInstance(userId), opt.getLabel());
                break;

            case Constant.TabOption.REVIEW:
                //goToSearchAlbumFragment(userId);
                bundle = new Bundle();
                map = new HashMap<>();

                map.put(Constant.KEY_PRODUCT_ID, mProductId);
                map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT_REVIEW);
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_REVIEW);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                intent2.putExtra(Constant.KEY_NAME, ((Options) value).getName());
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(SearchAlbumFragment.newInstance(userId), opt.getLabel());
                break;

            case Constant.TabOption.MAP:
                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_PRODUCT_MAP);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mProductId);
                map = new HashMap<>();
                map.put(Constant.KEY_PRODUCT_ID, mProductId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRODUCT);


                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_MAP);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                //   adapter.addFragment(PageMapFragment.newInstance(bundle), opt.getLabel());
                break;
            case Constant.TabOption.OVERVIEW:
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.TEXT, result.getStore().getDescription());
                map.put(Constant.KEY_ERROR, getStrings(R.string.msg_no_overview_available));

                bundle.putSerializable(Constant.POST_REQUEST, map);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_OVERVIEW);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);
                break;
            case Constant.TabOption.POLL:
                bundle = new Bundle();
                map = new HashMap<>();
                map.put(Constant.KEY_BUSINESS_ID, mProductId);
                bundle.putSerializable(Constant.POST_REQUEST, map);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_POLL);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;

            case Constant.TabOption.ANNOUNCE:
                bundle = new Bundle();

                //bundle.putInt(Constant.KEY_RESOURCE_ID, mPageId);
                map = new HashMap<>();
                map.put(Constant.KEY_BUSINESS_ID, mProductId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_URI, Constant.URL_BUSINESS_ANNOUNCE);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ANNOUNCE);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;

            case Constant.TabOption.SERVICES:
                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_BUSINESS_SERVICES);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mProductId);// mPageId);
                map = new HashMap<>();
                map.put(Constant.KEY_BUSINESS_ID, mProductId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.BUSINESS);

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_SERVICE);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(PageServicesFragment.newInstance(bundle), opt.getLabel());
                break;

            case Constant.TabOption.MEMBERS:
                bundle = new Bundle();
                bundle.putString(Constant.KEY_URI, Constant.URL_PAGE_MEMBER);
                bundle.putInt(Constant.KEY_RESOURCE_ID, mProductId);
                bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PAGE);
                map = new HashMap<>();
                map.put(Constant.KEY_PAGE_ID, mProductId);
                bundle.putSerializable(Constant.POST_REQUEST, map);
                // adapter.addFragment(PageMemberFragment.newInstance(bundle), opt.getLabel());

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_MEMBERS);
                intent2.putExtra(Constant.KEY_BUNDEL, bundle);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;
            case Constant.TabOption.VIDEO:

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_S_PRODUCT_VIDEO);
                intent2.putExtra(Constant.KEY_ID, mProductId);
                intent2.putExtra(Constant.KEY_NAME, ((Options) value).getName());
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                // adapter.addFragment(PageVideoFragment.newInstance(opt.getName(), Constant.ResourceType.PAGE, mPageId, this), opt.getLabel());
                break;

            case Constant.TabOption.ASSOCIATE:

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_PAGE_ASSOCIATE);
                intent2.putExtra(Constant.KEY_ID, mProductId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);


                break;

            case Constant.TabOption.UPSELL:

                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PROFILE_S_PRODUCT_UPSELL);
                intent2.putExtra(Constant.KEY_ID, mProductId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);



                break;

            case Constant.TabOption.COMMENT:
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_COMMENT);
                intent2.putExtra(Constant.KEY_ACTION_ID, mProductId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);

                break;
     /*

            case "claim":
                map = new HashMap<>();
                map.put(Constant.KEY_PAGE_ID, mPageId);
                adapter.addFragment(ClaimFormFragment.newInstance(Constant.FormType.CLAIM, map, Constant.URL_PAGE_CLAIM), opt.getLabel());
                break;



            default:
                CustomLog.e("Not Handled", "handle this profile widget name:" + opt.getName() + " __Lable: " + opt.getLabel());
                        *//*bundle = new Bundle();
                        bundle.putString(Constant.KEY_URI, Constant.URL_GROUP_MEMBER);
                        bundle.putInt(Constant.KEY_RESOURCE_ID, mGroupId);
                        bundle.putString(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
                        map = new HashMap<>();
                        map.put(Constant.KEY_GROUP_ID, mGroupId);
                        bundle.putSerializable(Constant.POST_REQUEST, map);
                        adapter.addFragment(PageMemberFragment.newInstance(bundle), opt.getLabel());*//*
                break;*/
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }
}
