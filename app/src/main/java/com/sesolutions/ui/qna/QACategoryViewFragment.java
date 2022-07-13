package com.sesolutions.ui.qna;


import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

public class QACategoryViewFragment extends QAFragment {

    String title;

    public static QACategoryViewFragment newInstance(int categoryId, String title) {
        QACategoryViewFragment frag = new QACategoryViewFragment();
        frag.selectedScreen = TYPE_CATEGORY_VIEW;
        frag.categoryId = categoryId;
        frag.title = title;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    public void init() {
        // super.init();
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
        recyclerView = v.findViewById(R.id.recyclerView);
        pb = v.findViewById(R.id.pb);
        txtNoData = R.string.NO_QUESTION_AVAILABLE;
        url = Constant.URL_QA_BROWSE;

    }


    @Override
    public void onRefresh() {
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
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

    @Override
    public void updateAdapter() {
        super.updateAdapter();
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title + " (" + result.getTotal() + ")");

    }
}
