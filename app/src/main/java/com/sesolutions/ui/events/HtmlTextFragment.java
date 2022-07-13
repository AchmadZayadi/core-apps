package com.sesolutions.ui.events;


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
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.Map;


public class HtmlTextFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private Map<String, Object> map;
    private OnUserClickedListener<Integer, Object> listener;
    boolean istoolbar;


    public static HtmlTextFragment newInstance(Map<String, Object> map, OnUserClickedListener<Integer, Object> listener) {

        HtmlTextFragment fragment = new HtmlTextFragment();
        fragment.map = map;
        fragment.listener = listener;
        return fragment;
    }

    public static HtmlTextFragment newInstance(Map<String, Object> map, OnUserClickedListener<Integer, Object> listener,boolean istoolbar) {

        HtmlTextFragment fragment = new HtmlTextFragment();
        fragment.map = map;
        fragment.listener = listener;
        fragment.istoolbar = istoolbar;
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_html_text, container, false);
        try {
            applyTheme(v);
        } catch (Exception e) {
            CustomLog.e(e);
        }

        if (!istoolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.overviewid);
            v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });
            initScreenData();
        }

        return v;
    }

    @Override
    public void onRefresh() {
        //  new ApiController().execute();
    }

    @Override
    public void initScreenData() {
        init();
    }

    private void init() {
        try {

            // show/hide edit rule option
            if (map.containsKey(Constant.BUTTON) && (boolean) map.get(Constant.BUTTON)) {
                v.findViewById(R.id.cvPost).setOnClickListener(this);
                ((ImageView) v.findViewById(R.id.ivPost)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cam_ic_edit));
                ((TextView) v.findViewById(R.id.tvPost)).setText(R.string.edit_rule);
                v.findViewById(R.id.cvPost).setVisibility(View.VISIBLE);

            } else if (map.containsKey(Constant.KEY_FILTER) && (boolean) map.get(Constant.KEY_FILTER)) {
                v.findViewById(R.id.cvPost).setOnClickListener(this);
                ((ImageView) v.findViewById(R.id.ivPost)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cam_ic_edit));
                ((TextView) v.findViewById(R.id.tvPost)).setText(R.string.update_overview);
                v.findViewById(R.id.cvPost).setVisibility(View.VISIBLE);

            }

            String text = (String) map.get(Constant.TEXT);
            String errorMsg = (String) map.get(Constant.KEY_ERROR);
            if (TextUtils.isEmpty(text)) {
                ((TextView) v.findViewById(R.id.tvNoData)).setText(errorMsg);
                v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ((TextView) v.findViewById(R.id.tvText)).setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
            } else {
                ((TextView) v.findViewById(R.id.tvText)).setText(Html.fromHtml(text));
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
                case R.id.cvPost:
                    listener.onItemClicked(Constant.Events.CONTENT_EDIT, null, -1);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
