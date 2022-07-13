package com.sesolutions.ui.store.product;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.NestedOptions;
import com.sesolutions.responses.page.PageInformation;
import com.sesolutions.responses.store.StoreContent;
import com.sesolutions.responses.store.product.PaymentOption;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.member.MoreMemberFragment;
import com.sesolutions.ui.store.StoreUtil;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductInfoFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private LinearLayoutCompat llMain;
    // private List<Options> infoList;
    private int mPageId;
    private int text2;
    private PageInformation resp;
    // private boolean showToolbar;

    private List<StoreContent> relatedList;

    public static ProductInfoFragment newInstance(int userId) {
        ProductInfoFragment frag = new ProductInfoFragment();
        frag.mPageId = userId;
        return frag;
    }

    boolean isToolbar=false;
    public static ProductInfoFragment newInstance(int userId,boolean isToolbar) {
        ProductInfoFragment frag = new ProductInfoFragment();
        frag.mPageId = userId;
        frag.isToolbar = isToolbar;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_product_info, container, false);
        try {
            applyTheme(v);
            if (!isToolbar) {
                v.findViewById(R.id.appBar).setVisibility(View.GONE);
            } else {
                v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.TITLE_INFO);
                initScreenData();
                v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();
                    }
                });
            }
            /*if (!showToolbar) {
                v.findViewById(R.id.appBar).setVisibility(View.GONE);
            } else {
                initScreenData();
            }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void initScreenData() {
        text2 = Color.parseColor(Constant.text_color_2);
        callMusicAlbumApi();
    }

    private void init() {
        // v.findViewById(R.id.mScrollView).setBackgroundColor(Color.parseColor(Constant.foregroundColor));
        try {
            llMain = v.findViewById(R.id.llInfo);
            if (resp != null) {
                //show main layout
                llMain.setVisibility(View.VISIBLE);

                setDescriptionData();
//                setOpenHoursData();
                setAditionalDetail();
//                setContactInfoData();
                initRelatedProductsUI();
                updateRelatedProductAdapter();

                if (resp.getProductInfo().get(0).getPaymentMethods().size() > 0)
                    setPaymentOptions();

                addPeopleLists(getStrings(R.string.people_liked), resp.getLikePeople(), R.drawable.music_like_selected, resp.getTotalLikePeople(), "like");
                addPeopleLists(getStrings(R.string.people_followed), resp.getFollowedPeople(), R.drawable.follow_artist_selected, resp.getTotalFollowedPeople(), "follow");
                addPeopleLists(getStrings(R.string.people_favorite), resp.getFavouritePeople(), R.drawable.music_favourite_selected, resp.getTotalFavouritePeople(), "favourite");
                addPeopleLists(getStrings(R.string.pages_liked), resp.getLikePages(), R.drawable.music_like_selected, resp.getTotalLikePages(), "page");

                applyTheme(llMain);
            } else {
                //hide mail lauout in case of invalid valid response
                llMain.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setPaymentOptions() {

        for (PaymentOption option : resp.getProductInfo().get(0).getPaymentMethods()) {
            // todo these dynamic code
            switch (option.getLabel()) {
                case "Pay With Stripe":
                    Util.showImageWithGlide(v.findViewById(R.id.ivpay1), option.getImage(), context, R.drawable.placeholder_square);
                    break;
                case "Pay With PayPal":
                    Util.showImageWithGlide(v.findViewById(R.id.ivpay1), option.getImage(), context, R.drawable.placeholder_square);
                    break;
                case "Pay With Cash on Delivery":
                    Util.showImageWithGlide(v.findViewById(R.id.ivpay2), option.getImage(), context, R.drawable.placeholder_square);
                    break;
                case "Pay With Cheque":
                    Util.showImageWithGlide(v.findViewById(R.id.ivpay3), option.getImage(), context, R.drawable.placeholder_square);
                    break;
                default:
                    break;
            }
        }
    }

    //ToDo
    private ProductChildAdapter adapterRelated;

    private void initRelatedProductsUI() {
        v.findViewById(R.id.relatedcard).setVisibility(View.VISIBLE);
        RecyclerView rvPhotos = v.findViewById(R.id.rvCommon);
        relatedList = new ArrayList<StoreContent>();
        rvPhotos.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new GridLayoutManager(context, 2);
        rvPhotos.setLayoutManager(layoutManager);
        // ToDo
        adapterRelated = new ProductChildAdapter(relatedList, context, this, true);
        rvPhotos.setAdapter(adapterRelated);
    }

    private void updateRelatedProductAdapter() {
        try {
            relatedList.clear();
            if (resp.getRelatedProducts() != null && resp.getRelatedProducts().size() > 0) {
                v.findViewById(R.id.rlRecent).setVisibility(View.VISIBLE);
                relatedList.addAll(resp.getRelatedProducts());
            } else {
                v.findViewById(R.id.rlRecent).setVisibility(View.GONE);
            }
            // ToDO
//            adapterRelated.notifyDataSetChanged();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void addPeopleLists(String s, List<Albums> list, int dImage, int total, String type) {
        try {
            if (list == null) {
                //do not set data in case of null response
                return;
            }
            LinearLayoutCompat llInfo = v.findViewById(R.id.llInfo);
            View view = getLayoutInflater().inflate(R.layout.layout_page_info_bottom, (ViewGroup) llInfo, false);
            ((TextView) view.findViewById(R.id.tvItemTitle)).setText(s);
            ((ImageView) view.findViewById(R.id.ivTitleImage)).setImageDrawable(ContextCompat.getDrawable(context, dImage));
            if (list.size() > 0) {
                View item1 = view.findViewById(R.id.item1);
                item1.setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.tvItemText)).setText(list.get(0).getName());
                Util.showImageWithGlide((ImageView) view.findViewById(R.id.ivItemImage), list.get(0).getImages().getMain(), context, 1);
                final int userId = list.get(0).getUserId();
                final int pageId = list.get(0).getPageId();
                item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userId > 0) {
                            goToProfileFragment(userId);
                        } else {
                            openViewPageFragment(pageId);
                        }
                    }
                });

            }
            if (list.size() > 1) {
                View item1 = view.findViewById(R.id.item2);
                item1.setVisibility(View.VISIBLE);
                ((TextView) item1.findViewById(R.id.tvItemText)).setText(list.get(1).getName());
                Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(1).getImages().getMain(), context, 1);
                final int userId = list.get(1).getUserId();
                final int pageId = list.get(1).getPageId();
                item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userId > 0) {
                            goToProfileFragment(userId);
                        } else {
                            openViewPageFragment(pageId);
                        }
                    }
                });
                if (list.get(1).getImages() != null)
                    Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(1).getImages().getMain(), context, 1);
                // llInfo.addView(view);
            }
            if (list.size() > 2) {
                View item1 = view.findViewById(R.id.item3);
                item1.setVisibility(View.VISIBLE);
                ((TextView) item1.findViewById(R.id.tvItemText)).setText(list.get(2).getName());
                Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(2).getImages().getMain(), context, 1);
                final int userId = list.get(2).getUserId();
                final int pageId = list.get(2).getPageId();
                item1.setOnClickListener(v -> {
                    if (userId > 0) {
                        goToProfileFragment(userId);
                    } else {
                        openViewPageFragment(pageId);
                    }
                });
            }
            if (list.size() > 3) {
                View item1 = view.findViewById(R.id.item4);
                item1.setVisibility(View.VISIBLE);
                ((TextView) item1.findViewById(R.id.tvItemText)).setText(list.get(3).getName());
                Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(3).getImages().getMain(), context, 1);
                final int userId = list.get(3).getUserId();
                final int pageId = list.get(3).getPageId();
                item1.setOnClickListener(v -> {
                    if (userId > 0) {
                        goToProfileFragment(userId);
                    } else {
                        openViewPageFragment(pageId);
                    }
                });
                //show count if total count is not 0
                if (total > 0) {
                    item1.findViewById(R.id.vItem).setVisibility(View.VISIBLE);
                    ((TextView) item1.findViewById(R.id.tvItemCount)).setText("+" + total);
                    item1.findViewById(R.id.tvItemCount).setVisibility(View.VISIBLE);
                    item1.setOnClickListener(v -> {
                        openMoreMemberFragment(type);
                    });
                } else {
                    item1.setOnClickListener(v -> {
                        if (userId > 0) {
                            goToProfileFragment(userId);
                        } else {
                            openViewPageFragment(pageId);
                        }
                    });
                }
            }

            llInfo.addView(view);
            applyTheme(llInfo);
        } catch (Exception e) {
            CustomLog.e(e);
        }


    }

    private void openMoreMemberFragment(String type) {
        int id;
        switch (type) {
            case "like":
                id = R.string.people_liked;
                break;
            case "follow":
                id = R.string.people_followed;
                break;
            case "favourite":
                id = R.string.people_favorite;
                break;
            case "page":
                id = R.string.pages_liked;
                break;
            default:
                id = R.string.people_liked;
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_MODULE, MenuTab.Page.INFO);
        bundle.putString(Constant.KEY_TITLE, getStrings(id));
        bundle.putString(Constant.KEY_TYPE, type);
        bundle.putInt(Constant.KEY_ID, mPageId);
        fragmentManager.beginTransaction().replace(R.id.container, MoreMemberFragment.newInstance(bundle)).addToBackStack(null).commit();
    }

    private void setDescriptionData() {
        //set "detail" data
        if (!TextUtils.isEmpty(resp.getProductInfo().get(0).getProductDescription())) {
            v.findViewById(R.id.cvDetail).setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ((TextView) v.findViewById(R.id.tvDetail)).setText(Html.fromHtml(resp.getProductInfo().get(0).getProductDescription(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                ((TextView) v.findViewById(R.id.tvDetail)).setText(Html.fromHtml(resp.getProductInfo().get(0).getProductDescription()));
            }
            ((TextView) v.findViewById(R.id.tvDetail)).setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            v.findViewById(R.id.cvDetail).setVisibility(View.GONE);
        }
    }

    private void setAditionalDetail() {
        //setting basic info items
        try {
            LinearLayoutCompat lladditional = v.findViewById(R.id.aditionalInfo);
            lladditional.setBackgroundColor(Color.parseColor(Constant.foregroundColor));

            lladditional.setVisibility(View.VISIBLE);

            View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) lladditional, false);
            ((TextView) view.findViewById(R.id.tv1)).setText("Store - ");
            ((TextView) view.findViewById(R.id.tv2)).setText(resp.getProductInfo().get(0).getStoreTitle());
            lladditional.addView(view);

            View view2 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) lladditional, false);
            ((TextView) view2.findViewById(R.id.tv1)).setText("Brand - ");
            ((TextView) view2.findViewById(R.id.tv2)).setText(resp.getProductInfo().get(0).getBrand());
            lladditional.addView(view2);

            View view3 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) lladditional, false);
            ((TextView) view3.findViewById(R.id.tv1)).setText("Category - ");
            ((TextView) view3.findViewById(R.id.tv2)).setText(resp.getProductInfo().get(0).getCategory_title());
            lladditional.addView(view3);

            View view4 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) lladditional, false);
            ((TextView) view4.findViewById(R.id.tv1)).setText("Location - ");
            ((TextView) view4.findViewById(R.id.tv2)).setText(resp.getProductInfo().get(0).getOwner_title()+"fix this");
            lladditional.addView(view4);

            applyTheme(lladditional);

//            if (resp.getBasicInformation() != null) {
////                lladditional.setVisibility(View.VISIBLE);
//                for (NestedOptions opt : resp.getBasicInformation()) {
//                    switch (opt.getName()) {
//                        case Constant.OptionType.STATS:
//                            View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) lladditional, false);
//                            ((TextView) view.findViewById(R.id.tv1)).setText("Store - ");
//                            ((TextView) view.findViewById(R.id.tv2)).setText(resp.getProductInfo().get(0).getStoreTitle());
//                            lladditional.addView(view);
//                            break;
//                        case Constant.OptionType.CREATION_DATE:
//                            view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) lladditional, false);
//                            ((TextView) view.findViewById(R.id.tv1)).setText("Brand - ");
//                            ((TextView) view.findViewById(R.id.tv2)).setText(resp.getProductInfo().get(0).getBrand());
//                            lladditional.addView(view);
//                            break;
//
//                        case Constant.OptionType.TAG:
//                            view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) lladditional, false);
//                            ((TextView) view.findViewById(R.id.tv1)).setText("Category - ");
//                             ((TextView) view.findViewById(R.id.tv2)).setText(resp.getProductInfo().get(0).getCategory_title());
//                            lladditional.addView(view);
//                            break;
//
//                        default:
//                            view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) lladditional, false);
//                            ((TextView) view.findViewById(R.id.tv1)).setText("Location - ");
//                            ((TextView) view.findViewById(R.id.tv2)).setText(resp.getProductInfo().get(0).getOwner_title()+"fix this");
//                            lladditional.addView(view);
//                            break;
//                    }
//                }
//                applyTheme(lladditional);
//            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void setOpenHoursData() {
        //setting basic info items
        try {
            LinearLayoutCompat llBasic = v.findViewById(R.id.openHours);
            if (resp.getOpenHours() != null) {
                llBasic.setVisibility(View.VISIBLE);
                llBasic.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
                NestedOptions vo = resp.getOpenHours();
                ((TextView) llBasic.findViewById(R.id.tvTimezone)).setText(vo.getLabel());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    for (Options opt : vo.getValueList()) {
                        View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                        ((TextView) view.findViewById(R.id.tv1)).setText(Html.fromHtml(opt.getLabel(), Html.FROM_HTML_MODE_LEGACY));
                        ((TextView) view.findViewById(R.id.tv2)).setText(Html.fromHtml(opt.getValue(), Html.FROM_HTML_MODE_LEGACY));
                        llBasic.addView(view);
                    }
                } else {
                    for (Options opt : vo.getValueList()) {
                        View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                        ((TextView) view.findViewById(R.id.tv1)).setText(Html.fromHtml(opt.getLabel()));
                        ((TextView) view.findViewById(R.id.tv2)).setText(Html.fromHtml(opt.getValue()));
                        llBasic.addView(view);
                    }
                }
                applyTheme(llBasic);
            } else {
                llBasic.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setContactInfoData() {
        LinearLayoutCompat llAbout = v.findViewById(R.id.contactInfo);
        // llAbout.removeAllViews();
        if (resp.getContactInformation() != null) {
            llAbout.setVisibility(View.VISIBLE);
            //add about layout items
            for (final NestedOptions opt : resp.getContactInformation()) {
                switch (opt.getName()) {
                    case Constant.OptionType.SEE_ALL:
                        View view = getLayoutInflater().inflate(R.layout.textview_seeall, (ViewGroup) llAbout, false);
                        view.setOnClickListener(v -> performAboutOptionClick(opt));
                        llAbout.addView(view);
                        break;
                    case Constant.OptionType.CREATE_DATE:
                        view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                        ((TextView) view.findViewById(R.id.tvOptionText)).setText(Util.changeDate(opt.getValueString()));
                        ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit_post));
                        llAbout.addView(view);
                        break;
                    default:
                        view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                        ((TextView) view.findViewById(R.id.tvOptionText)).setText(opt.getLabel());
                        ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, getDrawableId(opt.getName())));
                        view.setOnClickListener(v -> performAboutOptionClick(opt));
                        llAbout.addView(view);
                        break;
                }
            }
            applyTheme(llAbout);
        } else {
            llAbout.setVisibility(View.GONE);
        }
    }

    private void performAboutOptionClick(NestedOptions opt) {
        switch (opt.getName()) {
            case Constant.OptionType.CATEGORY:
                //openViewCategory();
                break;
            case Constant.OptionType.WEBSITE:
                openWebView(opt.getValueString(), opt.getValueString());
                break;
            case Constant.OptionType.PHONE:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + opt.getValueString()));
                startActivity(intent);
                break;
            case Constant.OptionType.MAIL:
                ShareCompat.IntentBuilder.from(activity)
                        .setType("message/rfc822")
                        .addEmailTo(opt.getValueString())
                        .setSubject("")
                        .setText("")
                        //.setHtmlText(body) //If you are using HTML in your body text
                        .setChooserTitle(opt.getLabel())
                        .startChooser();
                break;
            case Constant.OptionType.TAG:
                //TODO goto view feed with selected feed
                break;
            case Constant.OptionType.SEE_ALL:
                break;
            default:
                if (null != opt.getValueString() && opt.getValueString().startsWith("http")) {
                    openWebView(opt.getValueString(), opt.getValueString());
                }
                break;

        }
    }

  /*  public void goToProfileFragment(int userId, PageMemberAdapter.ContactHolder holder, int position) {
        try {
            String transitionName = friendList.get(position).getDisplayname();
            ViewCompat.setTransitionName(holder.ivImage, transitionName);
            ViewCompat.setTransitionName(holder.tvName, transitionName + Constant.Trans.TEXT);
            //  ViewCompat.setTransitionName(holder.llMain, transitionName + Constant.Trans.LAYOUT);


            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, friendList.get(position).getOwnerPhoto());
            //  bundle.putString(Constant.Trans.LAYOUT, transitionName + Constant.Trans.LAYOUT);

            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivImage, ViewCompat.getTransitionName(holder.ivImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    .addSharedElement(holder.tvName, ViewCompat.getTransitionName(holder.tvName))
                    .replace(R.id.container, ProfileFragment.newInstance(userId, bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            goToProfileFragment(userId);
        }
    }*/

    private void callMusicAlbumApi() {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {
                    showView(v.findViewById(R.id.pbMain));
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_PRODUCT_INFO);
                    request.params.put(Constant.KEY_PRODUCT_ID, mPageId);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideView(v.findViewById(R.id.pbMain));
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        //String result=
                                        CommonResponse res = new Gson().fromJson(response, CommonResponse.class);
                                        if (null != res.getResult().getInformation()) {
                                            resp = res.getResult().getInformation();
                                        }
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }
                                init();
                            } catch (Exception e) {
                                hideView(v.findViewById(R.id.pbMain));
                                CustomLog.e(e);
                            }
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideView(v.findViewById(R.id.pbMain));
                    CustomLog.e(e);
                }
            } else {

                notInternetMsg(v);
            }

        } catch (Exception e) {

            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                /*case R.id.ivBack:
                    onBackPressed();
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        switch (eventType){
            case Constant.Events.MUSIC_FAVORITE:

              //   callLikeApi(REQ_FAVORITE, postion, Constant.URL_PRODUCT_FAVORITE, -1);
                break;
            case Constant.Events.ADD_TO_CART:
                callAddToCartApi(relatedList.get(position).getProductId());
                break;

            case Constant.Events.MUSIC_MAIN:
                StoreUtil.openViewProductFragment(fragmentManager, position);
                break;
        }

        return false;
    }

    private void callAddToCartApi(int productId) {

        if (isNetworkAvailable(context)) {
//            final StoreContent vo = videoList.get(selectedVo).getProducts().get(position);
            showBaseLoader(false);
            try {
                HttpRequestVO request = new HttpRequestVO(Constant.URL_ADD_TO_CART);

                request.params.put(Constant.KEY_PRODUCT_ID, productId);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;

                            CustomLog.e("addtocart_response", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    JSONObject json = null;
                                    try {
                                        json = new JSONObject(response);

//                                        if (json.get(Constant.KEY_RESULT) instanceof String) {

                                        if ((json.getJSONObject(Constant.KEY_RESULT).getString("status")).equals("true")) {
                                            String message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                            Util.showSnackbar(v, message);
                                        }
//                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                } else {
                                    //revert changes in case of error
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                            Util.showSnackbar(v, getStrings(R.string.msg_something_wrong));
                        }

                        // dialog.dismiss();
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

    }


}
