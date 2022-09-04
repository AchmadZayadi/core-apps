package com.sesolutions.ui.point;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.sesolutions.R;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.SPref;

public class PointFragment extends BaseFragment {
    View view;
    WebView webView;

    ImageView ivBack;
    TextView tvTittleToolbar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {


        view = inflater.inflate(R.layout.fragment_point, container, false);
        webView = view.findViewById(R.id.webView);

        ivBack = view.findViewById(R.id.ivBack);
        tvTittleToolbar = view.findViewById(R.id.tvTitle);

        ivBack.setOnClickListener(view -> onBackPressed());
        tvTittleToolbar.setText("Poin");

        String postData = "auth_token=" + SPref.getInstance().getToken(context);

        webView.postUrl(Constant.URL_POINT_MENU, postData.getBytes());

        return view;
    }
}
