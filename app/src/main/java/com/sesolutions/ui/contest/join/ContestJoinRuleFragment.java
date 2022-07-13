package com.sesolutions.ui.contest.join;


import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.customviews.NestedWebView;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContestJoinRuleFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private OnUserClickedListener<Integer, Object> listener;
    private NestedWebView wbRules;
    private List<Dummy.Formfields> contestRules;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // postponeEnterTransition();
            // setEnterTransition(new AutoTransition());
            //setExitTransition(new AutoTransition());
            /*setEnterTransition(new Slide(Gravity.END));
            setExitTransition(new Slide(Gravity.START));*/
            setEnterTransition(new Fade(Fade.IN));
            setExitTransition(new Fade(Fade.OUT));
            // setSharedElementEnterTransition(new DetailsTransition());
            //  setSharedElementReturnTransition(new DetailsTransition());
            // setAllowEnterTransitionOverlap(false);
            // setAllowReturnTransitionOverlap(false);
        }
    }

    public static ContestJoinRuleFragment newInstance(List<Dummy.Formfields> contestRules, OnUserClickedListener<Integer, Object> listener) {
        ContestJoinRuleFragment fragment = new ContestJoinRuleFragment();
        fragment.listener = listener;
        fragment.contestRules = contestRules;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_contest_rules, container, false);
        try {
            applyTheme(v);
            initScreenData();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void initScreenData() {
        init();
        setUIData();
    }

    private void init() {
        wbRules = v.findViewById(R.id.wbRules);
        /*v.findViewById(R.id.bAccept).setOnClickListener(this);
        v.findViewById(R.id.tvDecline).setOnClickListener(this);*/

        for (Dummy.Formfields fld : contestRules) {
            if ("contest_rules".equals(fld.getName())) {
                wbRules.loadData(fld.getValue(), null, null);
            } else if ("save_second_1".equals(fld.getName())) {
                listener.onItemClicked(Constant.Events.UPDATE_NEXT, fld.getLabel(), -1);
                // ((AppCompatButton) v.findViewById(R.id.bAccept)).setText(fld.getLabel());
            } else if ("save_second_2".equals(fld.getName())) {
                listener.onItemClicked(Constant.Events.UPDATE_PREV, fld.getLabel(), -1);
                //((TextView) v.findViewById(R.id.tvDecline)).setText(fld.getLabel());
            }
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.bAccept:

                    break;
                /*case R.id.tvDecline:
                    listener.onItemClicked(Constant.Events.DECLINE, null, -1);
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void setUIData() {

    }


    public void onNextClick() {
        Map<String, Dummy.Formfields> temp = new HashMap<>();
        for (Dummy.Formfields fld : contestRules) {
            if ("save_second_1".equals(fld.getName())) {
                fld.setStringValue("1");
                temp.put("save_second_1", fld);
                break;
            }
        }

        listener.onItemClicked(Constant.Events.ACCEPT, temp, -1);
    }
}
