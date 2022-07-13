package com.sesolutions.ui.common;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.sesolutions.R;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SesColorUtils;

public class CollapsingToolbarHelper {

    public void initCollapsingToolbar(View v) {

        AppBarLayout appBarLayout = null;
        try {
            Toolbar toolbar = v.findViewById(R.id.toolbar);
            //activity.setSupportActionBar(toolbar);
            // if (activity.getSupportActionBar() != null)
            //activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            CollapsingToolbarLayout collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(" ");
            collapsingToolbar.setContentScrimColor(SesColorUtils.getPrimaryColor(v.getContext()));

            appBarLayout = v.findViewById(R.id.appbar);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        //endregion
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {

            });
        }
    }
}
