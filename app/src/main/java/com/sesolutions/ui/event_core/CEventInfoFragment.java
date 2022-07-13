package com.sesolutions.ui.event_core;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
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
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.event.EventResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;


public class CEventInfoFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private LinearLayoutCompat llMain;
    // private List<Options> infoList;
    private int mEventId;
    private int text2;
    private CommonVO resp;
    // private boolean showToolbar;

    public static CEventInfoFragment newInstance(int userId) {
        CEventInfoFragment frag = new CEventInfoFragment();
        frag.mEventId = userId;
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

                setBasicInfoData();
                setWhenNWhere();
                setDetail();
                //setMetaInfoData();

                // addPeopleLists(getStrings(R.string.people_liked), resp.getLikePeople(), R.drawable.music_like_selected);
                // addPeopleLists(getStrings(R.string.people_followed), resp.getFollowedPeople(), R.drawable.follow_artist_selected);
                //  addPeopleLists(getStrings(R.string.people_favorite), resp.getFavouritePeople(), R.drawable.music_favourite_selected);
                // addPeopleLists(getStrings(R.string.pages_liked), resp.getLikePages(), R.drawable.music_like_selected);

                applyTheme(llMain);
            } else {
                //hide mail lauout in case of invalid valid response
                llMain.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void addPeopleLists(String s, List<Albums> list, int dImage) {
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
                if (list.get(0).getImages() != null)
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
                ((TextView) view.findViewById(R.id.tvItemText)).setText(list.get(1).getName());
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
                    Util.showImageWithGlide((ImageView) view.findViewById(R.id.ivItemImage), list.get(1).getImages().getMain(), context, 1);
                llInfo.addView(view);
            }
            if (list.size() > 2) {
                View item1 = view.findViewById(R.id.item3);
                item1.setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.tvItemText)).setText(list.get(2).getName());
                if (list.get(2).getImages() != null)
                    Util.showImageWithGlide((ImageView) view.findViewById(R.id.ivItemImage), list.get(2).getImages().getMain(), context, 1);
                final int userId = list.get(2).getUserId();
                final int pageId = list.get(2).getPageId();
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
            if (list.size() > 3) {
                View item1 = view.findViewById(R.id.item4);
                item1.setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.tvItemText)).setText(list.get(3).getName());
                if (list.get(3).getImages() != null)
                    Util.showImageWithGlide((ImageView) view.findViewById(R.id.ivItemImage), list.get(3).getImages().getMain(), context, 1);
                final int userId = list.get(3).getUserId();
                final int pageId = list.get(3).getPageId();
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
                //show count in case of more than 4 items
                if (list.size() > 4) {
                    view.findViewById(R.id.vItem).setVisibility(View.VISIBLE);
                    ((TextView) view.findViewById(R.id.tvItemCount)).setText("" + (list.size() - 4));
                    view.findViewById(R.id.tvItemCount).setVisibility(View.VISIBLE);
                }

            }
            //((TextView) view.findViewById(R.id.tv2)).setText(Util.changeFormat(opt.getValueString()));
            llInfo.addView(view);
            applyTheme(llInfo);
        } catch (Exception e) {
            CustomLog.e(e);
        }


    }

    private void setDetail() {
        //set "detail" data
        if (!TextUtils.isEmpty(resp.getDescription())) {
            v.findViewById(R.id.cvDetail).setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ((TextView) v.findViewById(R.id.tvDetail)).setText(Html.fromHtml(resp.getDescription(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                ((TextView) v.findViewById(R.id.tvDetail)).setText(Html.fromHtml(resp.getDescription()));
            }
            ((TextView) v.findViewById(R.id.tvDetail)).setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            v.findViewById(R.id.cvDetail).setVisibility(View.GONE);
        }
    }

    private void setBasicInfoData() {
        //setting basic info items
        try {
            LinearLayoutCompat llBasic = v.findViewById(R.id.basicInfo);
            llBasic.setBackgroundColor(SesColorUtils.getForegroundColor(context));
            llBasic.setVisibility(View.VISIBLE);


            //created by
            View view2 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view2.findViewById(R.id.tv1)).setText(R.string.created_by);
            ((TextView) view2.findViewById(R.id.tv2)).setText(resp.getCreatedBy());
            llBasic.addView(view2);

            //Hosted by
            if (!TextUtils.isEmpty(resp.getHost())) {
                View view3 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                ((TextView) view3.findViewById(R.id.tv1)).setText(R.string.hosted_by);
                ((TextView) view3.findViewById(R.id.tv2)).setText(Html.fromHtml(resp.getHost()));
                llBasic.addView(view3);
            }


            //created on
            View view1 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.created_on);
            ((TextView) view1.findViewById(R.id.tv2)).setText(resp.getCreationDate());
            llBasic.addView(view1);

            //STATS
            View view4 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view4.findViewById(R.id.tv1)).setText(R.string.stats);
            ((TextView) view4.findViewById(R.id.tv2)).setText(
                    context.getResources().getQuantityString(R.plurals.like_count, resp.getLikeCount(), resp.getLikeCount())
                            + ", " + context.getResources().getQuantityString(R.plurals.comment_count, resp.getCommentCount(), resp.getCommentCount())
                            + ", " + resp.getViewCount().getAsString()
            );
            llBasic.addView(view4);


            View view5 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view5.findViewById(R.id.tv1)).setText(R.string.websitest);
            ((TextView) view5.findViewById(R.id.tv2)).setText(""+resp.getWebsite());
            llBasic.addView(view5);

            applyTheme(llBasic);


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void setWhenNWhere() {
        //setting basic info items
        try {
            LinearLayoutCompat llBasic = v.findViewById(R.id.openHours);
            ((TextView) llBasic.findViewById(R.id.tvOpenHours)).setText(R.string.txt_when_n_where);
            llBasic.setVisibility(View.VISIBLE);
            llBasic.setBackgroundColor(Color.parseColor(Constant.foregroundColor));

            View view1 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.txt_when);
            ((TextView) view1.findViewById(R.id.tv2)).setText(Util.getEventDate(resp.getStartTime(), resp.getEndTime()));
            llBasic.addView(view1);

            if (null != resp.getLocationString()) {
                View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                ((TextView) view.findViewById(R.id.tv1)).setText(R.string.txt_where);
                ((TextView) view.findViewById(R.id.tv2)).setText(resp.getLocationString());
                llBasic.addView(view);
            }


            applyTheme(llBasic);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

  /*  private void setMetaInfoData() {
        LinearLayoutCompat llAbout = v.findViewById(R.id.contactInfo);
        // llAbout.removeAllViews();
        if (resp.getMeta() != null && resp.getMeta().getValueList() != null) {
            llAbout.setVisibility(View.VISIBLE);
            NestedOptions vo = resp.getMeta();
            ((TextView) llAbout.findViewById(R.id.tvContactInfo)).setText(vo.getLabel());
            //add meta layout items
            List<Options> list = vo.getValueList();
            for (final Options opt : list) {
                switch (opt.getName()) {
                    case Constant.OptionType.SEE_ALL:
                        View view = getLayoutInflater().inflate(R.layout.textview_seeall, (ViewGroup) llAbout, false);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //performAboutOptionClick(opt);
                            }
                        });
                        llAbout.addView(view);
                        break;
                    case Constant.OptionType.TAG:
                        if (null != resp.getTag()) {
                            view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                            ((TextView) view.findViewById(R.id.tvOptionText)).setText(SpanUtil.getHashTags(resp.getTag(), this));
                            ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, getDrawableId(opt.getName())));
                            llAbout.addView(view);
                        }
                        break;
                    case Constant.OptionType.CREATE_DATE:
                        view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                        ((TextView) view.findViewById(R.id.tvOptionText)).setText(Util.changeDate(opt.getValue()));
                        ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit_post));
                        llAbout.addView(view);
                        break;
                    default:
                        view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                        ((TextView) view.findViewById(R.id.tvOptionText)).setText(opt.getValue());
                        ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, getDrawableId(opt.getName())));
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                        llAbout.addView(view);
                        break;
                }
            }
            applyTheme(llAbout);
        } else {
            llAbout.setVisibility(View.GONE);
        }
    }*/

   /* private void performAboutOptionClick(NestedOptions opt) {
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
                break;
            case Constant.OptionType.SEE_ALL:
                break;
            default:
                if (null != opt.getValueString() && opt.getValueString().startsWith("http")) {
                    openWebView(opt.getValueString(), opt.getValueString());
                }
                break;

        }
    }*/

    private void callMusicAlbumApi() {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    showView(v.findViewById(R.id.pbMain));
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CEVENT_INFO);
                    request.params.put(Constant.KEY_ID, mEventId);
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
                                    EventResponse res = new Gson().fromJson(response, EventResponse.class);
                                    if (TextUtils.isEmpty(res.getError())) {
                                        //String result=

                                        if (null != res.getResult().getEventContent()) {
                                            resp = res.getResult().getEventContent();
                                        }
                                    } else {
                                        Util.showSnackbar(v, res.getErrorMessage());
                                    }
                                }

                                init();

                            } catch (Exception e) {
                                hideView(v.findViewById(R.id.pbMain));
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideView(v.findViewById(R.id.pbMain));
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
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
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
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        return false;
    }
}
