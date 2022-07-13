package com.sesolutions.ui.page;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.LinearLayoutCompat;
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
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.NestedOptions;
import com.sesolutions.responses.page.PageInformation;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.member.MoreMemberFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;


public class PageInfoFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private LinearLayoutCompat llMain;
    // private List<Options> infoList;
    private int mPageId;
    private int text2;
    private PageInformation resp;
    private boolean showToolbar=false;
    int PICK_CONTACT_REQUEST=1098;

    public static PageInfoFragment newInstance(int userId,boolean showToolbar) {
        PageInfoFragment frag = new PageInfoFragment();
        frag.mPageId = userId;
        frag.showToolbar = showToolbar;
        return frag;
    }

    public static PageInfoFragment newInstance(int userId) {
        PageInfoFragment frag = new PageInfoFragment();
        frag.mPageId = userId;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_page_info, container, false);
        try {
            applyTheme(v);
            if (!showToolbar) {
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

                setBasicInfoData();
                setOpenHoursData();
                setDetail();
                setContactInfoData();

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
                Util.showImageWithGlide((ImageView) view.findViewById(R.id.ivItemImage), list.get(0).getImages().getMain(), context, R.drawable.placeholder_square);
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
                Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(1).getImages().getMain(), context, R.drawable.placeholder_square);
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
                    Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(1).getImages().getMain(), context, R.drawable.placeholder_square);
                // llInfo.addView(view);
            }
            if (list.size() > 2) {
                View item1 = view.findViewById(R.id.item3);
                item1.setVisibility(View.VISIBLE);
                ((TextView) item1.findViewById(R.id.tvItemText)).setText(list.get(2).getName());
                Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(2).getImages().getMain(), context, R.drawable.placeholder_square);
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
                Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(3).getImages().getMain(), context, R.drawable.placeholder_square);
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
        bundle.putString(Constant.KEY_MODULE,MenuTab.Page.INFO);
        bundle.putString(Constant.KEY_TITLE, getStrings(id));
        bundle.putString(Constant.KEY_TYPE, type);
        bundle.putInt(Constant.KEY_ID, mPageId);
        fragmentManager.beginTransaction().replace(R.id.container, MoreMemberFragment.newInstance(bundle)).addToBackStack(null).commit();
    }

    private void setDetail() {
        //set "detail" data
        if (!TextUtils.isEmpty(resp.getDescription())) {
            v.findViewById(R.id.llDetail).setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ((TextView) v.findViewById(R.id.tvDetail)).setText(Html.fromHtml(resp.getDescription(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                ((TextView) v.findViewById(R.id.tvDetail)).setText(Html.fromHtml(resp.getDescription()));
            }
            ((TextView) v.findViewById(R.id.tvDetail)).setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            v.findViewById(R.id.llDetail).setVisibility(View.GONE);
        }
    }

    private void setBasicInfoData() {
        //setting basic info items
        try {
            LinearLayoutCompat llBasic = v.findViewById(R.id.basicInfo);
            llBasic.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
            if (resp.getBasicInformation() != null) {
                llBasic.setVisibility(View.VISIBLE);
                for (NestedOptions opt : resp.getBasicInformation()) {
                    switch (opt.getName()) {
                        case Constant.OptionType.STATS:
                            View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                            ((TextView) view.findViewById(R.id.tv1)).setText(opt.getLabel());
                            ((TextView) view.findViewById(R.id.tv2)).setText(opt.getStatsString());
                            llBasic.addView(view);
                            break;
                        case Constant.OptionType.CREATION_DATE:
                            view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                            ((TextView) view.findViewById(R.id.tv1)).setText(opt.getLabel());
                            ((TextView) view.findViewById(R.id.tv2)).setText(Util.changeFormat(opt.getValueString()));
                            llBasic.addView(view);
                            break;

                        case Constant.OptionType.TAG:
                            view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                            ((TextView) view.findViewById(R.id.tv1)).setText(opt.getLabel());
                            // ((TextView) view.findViewById(R.id.tv2)).setText(SpanUtil.getHashTags());
                            llBasic.addView(view);
                            break;

                        default:
                            view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                            ((TextView) view.findViewById(R.id.tv1)).setText(opt.getLabel());
                            ((TextView) view.findViewById(R.id.tv2)).setText(opt.getValueString());
                            llBasic.addView(view);
                            break;
                    }
                }
                applyTheme(llBasic);
            }

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
               /* ShareCompat.IntentBuilder.from(activity)
                        .setType("message/rfc822")
                        .addEmailTo(opt.getValueString())
                        .setSubject("")
                        .setText("")
                        //.setHtmlText(body) //If you are using HTML in your body text
                        .setChooserTitle(opt.getLabel())
                        .startChooser();*/

                Intent emailIntent = new Intent(android.content.Intent.ACTION_SENDTO);
                emailIntent.setType("image/jpeg");
                //File bitmapFile = new File(Environment.getExternalStorageDirectory()+"DCIM/Camera/img.jpg");
                //myUri = Uri.fromFile(bitmapFile);
              //  emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///mnt/sdcard/DCIM/Camera/img.jpg"));
                emailIntent.setData(Uri.parse("mailto:"+opt.getValueString()));


                startActivityForResult(Intent.createChooser(emailIntent, "Complete action using:"),PICK_CONTACT_REQUEST);

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
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_PAGE_INFO);
                    request.params.put(Constant.KEY_PAGE_ID, mPageId);
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
}
