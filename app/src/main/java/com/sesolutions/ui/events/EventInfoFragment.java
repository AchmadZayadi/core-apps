package com.sesolutions.ui.events;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
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
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.NestedOptions;
import com.sesolutions.responses.page.PageInformation;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SpanUtil;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;


public class EventInfoFragment extends BaseFragment implements View.OnClickListener,OnUserClickedListener<Integer,Object> {

    private View v;
    private LinearLayoutCompat llMain;
    // private List<Options> infoList;
    private int mEventId;
    private int text2;
    CardView cvDetail;
    private PageInformation resp;
    AppCompatTextView tvTitle;
    // private boolean showToolbar;

    public static EventInfoFragment newInstance(int userId) {
        EventInfoFragment frag = new EventInfoFragment();
        frag.mEventId = userId;
        return frag;
    }

    public static EventInfoFragment newInstance(int userId,Boolean flagdata) {
        EventInfoFragment frag = new EventInfoFragment();
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
            cvDetail=v.findViewById(R.id.cvDetail);
            tvTitle=v.findViewById(R.id.tvTitle);
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);

            v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            tvTitle.setText("Info");
            initScreenData();
           /* if (!showToolbar) {
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
                setMetaInfoData();

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

    private void setDetail() {
        //set "detail" data
        if (!TextUtils.isEmpty(resp.getDescription())) {
            v.findViewById(R.id.llDetail).setVisibility(View.VISIBLE);
            v.findViewById(R.id.cvDetail).setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ((TextView) v.findViewById(R.id.tvDetail)).setText(Html.fromHtml(resp.getDescription(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                ((TextView) v.findViewById(R.id.tvDetail)).setText(Html.fromHtml(resp.getDescription()));
            }
            ((TextView) v.findViewById(R.id.tvDetail)).setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            v.findViewById(R.id.llDetail).setVisibility(View.GONE);
            v.findViewById(R.id.cvDetail).setVisibility(View.GONE);
        }

    }

    private void setBasicInfoData() {
        //setting basic info items
        try {
            LinearLayoutCompat llBasic = v.findViewById(R.id.basicInfo);
            llBasic.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
            if (resp.getBasicInfo() != null) {
                llBasic.setVisibility(View.VISIBLE);
                List<Options> list = resp.getBasicInfo().getValueList();
                for (Options opt : list) {

                    Log.e("NameEvent 1",""+opt.getName());
                    if (opt.getName() == null) {
                        View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                        ((TextView) view.findViewById(R.id.tv1)).setText(opt.getLabel());
                        ((TextView) view.findViewById(R.id.tv2)).setText(opt.getValue());
                        llBasic.addView(view);
                    } else {
                        Log.e("NameEvent 2",""+opt.getName());
                        switch (opt.getName()) {
                            case Constant.OptionType.STATS:
                                View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                                ((TextView) view.findViewById(R.id.tv1)).setText(opt.getLabel());
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    ((TextView) view.findViewById(R.id.tv2)).setText(Html.fromHtml(opt.getValue(), Html.FROM_HTML_MODE_LEGACY));
                                } else {
                                    ((TextView) view.findViewById(R.id.tv2)).setText(Html.fromHtml(opt.getValue()));
                                }
                                llBasic.addView(view);
                                break;
                            case Constant.OptionType.CREATE_DATE:
                                view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                                ((TextView) view.findViewById(R.id.tv1)).setText(opt.getLabel());
                                ((TextView) view.findViewById(R.id.tv2)).setText(Util.changeFormat(opt.getValue()));
                                llBasic.addView(view);
                                break;
                            default:
                                view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                                ((TextView) view.findViewById(R.id.tv1)).setText(opt.getLabel());
                                ((TextView) view.findViewById(R.id.tv2)).setText(opt.getValue());
                                llBasic.addView(view);
                                break;
                        }
                    }
                }
                applyTheme(llBasic);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void setWhenNWhere() {
        //setting basic info items
        try {
            LinearLayoutCompat llBasic = v.findViewById(R.id.openHours);
            if (resp.getWhenNwhere() != null) {
                llBasic.setVisibility(View.VISIBLE);
                llBasic.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
                NestedOptions vo = resp.getWhenNwhere();
                //show title
                ((TextView) llBasic.findViewById(R.id.tvOpenHours)).setText(vo.getLabel());
                //show timezone
                ((TextView) llBasic.findViewById(R.id.tvTimezone)).setText(resp.getTimezone());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    for (Options opt : vo.getValueList()) {
                        View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                        ((TextView) view.findViewById(R.id.tv1)).setText(SpanUtil.getHtmlString(opt.getLabel(), true));
                        ((TextView) view.findViewById(R.id.tv2)).setText(SpanUtil.getHtmlString(opt.getValue(), true));
                        Log.e("Value",""+opt.getValue());
                        llBasic.addView(view);
                    }
                } else {
                    for (Options opt : vo.getValueList()) {
                        View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                        ((TextView) view.findViewById(R.id.tv1)).setText(SpanUtil.getHtmlString(opt.getLabel(), false));
                        ((TextView) view.findViewById(R.id.tv2)).setText(SpanUtil.getHtmlString(opt.getValue(), false));
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

    private void setMetaInfoData() {
        LinearLayoutCompat llAbout = v.findViewById(R.id.contactInfo);
        CardView cardstyle = v.findViewById(R.id.cardstyleviewid);
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
                                // performAboutOptionClick(opt);
                            }
                        });
                        llAbout.addView(view);
                        break;
                }
            }
            applyTheme(llAbout);
        } else {
            llAbout.setVisibility(View.GONE);
            cardstyle.setVisibility(View.GONE);
        }
    }
    private void callMusicAlbumApi() {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    showView(v.findViewById(R.id.pbMain));
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_EVENT_INFO);
                    request.params.put(Constant.KEY_EVENT_ID, mEventId);
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
