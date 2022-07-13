package com.sesolutions.ui.store;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

public class StoreCategoryViewFragment extends StoreFragment {

    String title;

    public static StoreCategoryViewFragment newInstance(int categoryId, String title) {
        StoreCategoryViewFragment frag = new StoreCategoryViewFragment();
        frag.selectedScreen = STORE_CATEGORY_VIEW;
        frag.categoryId = categoryId;
        frag.title = title;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.layout_toolbar_list_refresh_offset, container, false);
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
        txtNoData = R.string.NO_STORE_AVAILABLE;
        url = Constant.STORE_BROWSE;

    }

    @Override
    public void onRefresh() {
        callStoreApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
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
