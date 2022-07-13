package com.sesolutions.ui.contest;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.contest.ContestItem;
import com.sesolutions.responses.page.NestedOptions;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SpanUtil;
import com.sesolutions.utils.Util;


public class ContestInfoFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private LinearLayoutCompat llMain;
    // private List<Options> infoList;
    private int mEventId;
    private int text2;
    private ContestItem resp;
    // private boolean showToolbar;
    boolean isToolbar=false;

    public static ContestInfoFragment newInstance(ContestItem resp) {
        ContestInfoFragment frag = new ContestInfoFragment();
        frag.resp = resp;
        return frag;
    }

    public static ContestInfoFragment newInstance(ContestItem resp,boolean isToolbar) {
        ContestInfoFragment frag = new ContestInfoFragment();
        frag.resp = resp;
        frag.isToolbar = isToolbar;
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
           if (!isToolbar) {
                v.findViewById(R.id.appBar).setVisibility(View.GONE);
            } else {
                v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.TITLE_INFO);
                v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       onBackPressed();
                    }
                });
                initScreenData();
            }


        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void initScreenData() {
        text2 = Color.parseColor(Constant.text_color_2);
        init();
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

                applyTheme(llMain);
            } else {
                //hide main layout in case of invalid response
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

            llBasic.setVisibility(View.VISIBLE);

            //Adding creating createdBy
            View view1 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.created_by);
            ((TextView) view1.findViewById(R.id.tv2)).setText(resp.getOwnerTitle());
            llBasic.addView(view1);

            //Adding creating createdOn
            View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view.findViewById(R.id.tv1)).setText(R.string.created_on);
            ((TextView) view.findViewById(R.id.tv2)).setText(Util.changeFormat(resp.getCreationDate()));
            llBasic.addView(view);

            //Adding creating createdBy
            View view2 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view2.findViewById(R.id.tv1)).setText(R.string.stats);
            String detail = resp.getLikeCount() + (resp.getLikeCount() != 1 ? getStrings(R.string._LIKES) : getStrings(R.string._LIKE))
                    + " " + resp.getCommentCount() + (resp.getCommentCount() != 1 ? getStrings(R.string._COMMENTS) : getStrings(R.string._COMMENT))
                    + " " + resp.getFavouriteCount() + (resp.getFavouriteCount() != 1 ? getStrings(R.string._FAVORITES) : getStrings(R.string._FAVORITE))
                    + " " + resp.getViewCountInt() + (resp.getViewCountInt() != 1 ? getStrings(R.string._VIEWS) : getStrings(R.string._VIEW))
                    + " " + resp.getFollowCount() + (resp.getFollowCount() != 1 ? getStrings(R.string._followers) : getStrings(R.string._follower))
                    + " " + (!TextUtils.isEmpty(resp.getEntries()) ? resp.getEntries().replace("\n", " ") : "");
            ((TextView) view2.findViewById(R.id.tv2)).setText(detail);
            llBasic.addView(view2);


            View view4 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view4.findViewById(R.id.tv1)).setText(R.string.category);
            ((TextView) view4.findViewById(R.id.tv2)).setText(resp.getCategoryTitle());
            llBasic.addView(view4);

            View view3 = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
            ((TextView) view3.findViewById(R.id.tv1)).setText(R.string.text_tags);
            ((TextView) view3.findViewById(R.id.tv2)).setText(SpanUtil.getHashTags(resp.getTag(), this));
            llBasic.addView(view3);

            applyTheme(llBasic);


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void setWhenNWhere() {
        try {
            LinearLayoutCompat llBasic = v.findViewById(R.id.openHours);

            llBasic.setVisibility(View.VISIBLE);
            llBasic.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
            //show title
            ((TextView) llBasic.findViewById(R.id.tvOpenHours)).setText(R.string.txt_where);
            //show timezone
            ((TextView) llBasic.findViewById(R.id.tvTimezone)).setText(resp.getTimezone());


            View view = getLayoutInflater().inflate(R.layout.layout_text_vertical, (ViewGroup) llBasic, false);
            ((TextView) view.findViewById(R.id.tv1)).setText(R.string.txt_contest_start_end_date);
            ((TextView) view.findViewById(R.id.tv2)).setText(resp.getCalanderStartTime() + " - " + resp.getCalanderEndTime());
            llBasic.addView(view);

            if (null != resp.getJoinedStartTime()) {
                View view1 = getLayoutInflater().inflate(R.layout.layout_text_vertical, (ViewGroup) llBasic, false);
                ((TextView) view1.findViewById(R.id.tv1)).setText(R.string.txt_entry_start_end_date);
                ((TextView) view1.findViewById(R.id.tv2)).setText(resp.getJoinedStartTime() + " - " + resp.getJoinedEndTime());
                llBasic.addView(view1);
            }

            if (null != resp.getVotingStartTime()) {
                View view2 = getLayoutInflater().inflate(R.layout.layout_text_vertical, (ViewGroup) llBasic, false);
                ((TextView) view2.findViewById(R.id.tv1)).setText(R.string.txt_vote_start_end_date);
                ((TextView) view2.findViewById(R.id.tv2)).setText(resp.getVotingStartTime() + " - " + resp.getVotingEndTime());
                llBasic.addView(view2);
            }

        } catch (Exception e) {
            CustomLog.e(e);
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
