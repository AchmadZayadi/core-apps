package com.sesolutions.ui.contest;


import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.responses.contest.ContestItem;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.customviews.NestedWebView;
import com.sesolutions.utils.CustomLog;


public class ContestAwardFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private ContestItem vo;
    boolean isToolbar=false;

    public static ContestAwardFragment newInstance(ContestItem vo) {
        ContestAwardFragment fragment = new ContestAwardFragment();
        fragment.vo = vo;
        return fragment;
    }

    public static ContestAwardFragment newInstance(ContestItem vo,boolean isToolbar) {
        ContestAwardFragment fragment = new ContestAwardFragment();
        fragment.vo = vo;
        fragment.isToolbar = isToolbar;
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_contest_award, container, false);

        if (!isToolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.award);
            v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                  onBackPressed();
                }
            });
            initScreenData();
        }

        return v;
    }

    @Override
    public void initScreenData() {
        init();
    }

    private void init() {
        try {
            applyTheme(v);
            if (vo.getAwardCount() > 0) {

                if (vo.getAwardCount() > 0) {
                    v.findViewById(R.id.cv1).setVisibility(View.VISIBLE);
                    ((NestedWebView) v.findViewById(R.id.tv1)).loadData(vo.getAward(), null, null);
                    ((NestedWebView) v.findViewById(R.id.tv1)).setNestedScrollingEnabled(false);
                } else {
                    v.findViewById(R.id.cv1).setVisibility(View.GONE);
                }

                if (vo.getAwardCount() > 1) {
                    v.findViewById(R.id.cv2).setVisibility(View.VISIBLE);
                    ((NestedWebView) v.findViewById(R.id.tv2)).loadData(vo.getAward2(), null, null);
                    ((NestedWebView) v.findViewById(R.id.tv2)).setNestedScrollingEnabled(false);
                } else {
                    v.findViewById(R.id.cv2).setVisibility(View.GONE);
                }

                if (vo.getAwardCount() > 2) {
                    v.findViewById(R.id.cv3).setVisibility(View.VISIBLE);
                    ((NestedWebView) v.findViewById(R.id.tv3)).loadData(vo.getAward3(), null, null);
                    ((NestedWebView) v.findViewById(R.id.tv3)).setNestedScrollingEnabled(false);
                } else {
                    v.findViewById(R.id.cv3).setVisibility(View.GONE);
                }

                if (vo.getAwardCount() > 3) {
                    v.findViewById(R.id.cv4).setVisibility(View.VISIBLE);
                    ((NestedWebView) v.findViewById(R.id.tv4)).loadData(vo.getAward4(), null, null);
                    ((NestedWebView) v.findViewById(R.id.tv4)).setNestedScrollingEnabled(false);
                } else {
                    v.findViewById(R.id.cv4).setVisibility(View.GONE);
                }

                if (vo.getAwardCount() > 4) {
                    v.findViewById(R.id.cv5).setVisibility(View.VISIBLE);
                    ((NestedWebView) v.findViewById(R.id.tv5)).loadData(vo.getAward5(), null, null);
                    ((NestedWebView) v.findViewById(R.id.tv5)).setNestedScrollingEnabled(false);
                } else {
                    v.findViewById(R.id.cv5).setVisibility(View.GONE);
                }
            } else {
                ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_no_award_available);
                v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
