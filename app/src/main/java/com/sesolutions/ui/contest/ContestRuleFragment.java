package com.sesolutions.ui.contest;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.responses.contest.ContestItem;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.CustomLog;


public class ContestRuleFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private ContestItem vo;


    public static ContestRuleFragment newInstance(ContestItem vo) {

        ContestRuleFragment fragment = new ContestRuleFragment();
        fragment.vo = vo;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_contest_rule, container, false);
        try {
            applyTheme(v);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void initScreenData() {
        init();
    }

    private void init() {
        try {

            if (TextUtils.isEmpty(vo.getRules())) {
                ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_no_rules_available);
                v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ((TextView) v.findViewById(R.id.tvText)).setText(Html.fromHtml(vo.getRules(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                ((TextView) v.findViewById(R.id.tvText)).setText(Html.fromHtml(vo.getRules()));
            }

            if (null != vo.getRuleOption()) {
                v.findViewById(R.id.cvPost).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvPost)).setText(vo.getRuleOption().getLabel());
                ((ImageView) v.findViewById(R.id.ivPost)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cam_ic_edit));
                v.findViewById(R.id.cvPost).setOnClickListener(this);
            } else {
                v.findViewById(R.id.cvPost).setVisibility(View.GONE);
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
