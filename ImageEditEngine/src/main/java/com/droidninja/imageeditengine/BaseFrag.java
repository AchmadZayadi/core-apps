package com.droidninja.imageeditengine;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseFrag extends Fragment {
    public boolean isComingFromBack;

    public BaseFrag() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isComingFromBack)
            initView(view);
    }

    protected void setVisibility(View view, boolean visible) {
        if (visible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    protected abstract void initView(View view);
}
