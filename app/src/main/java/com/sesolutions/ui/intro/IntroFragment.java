package com.sesolutions.ui.intro;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.responses.SlideShowImage;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Util;

import java.util.Objects;

public class IntroFragment extends BaseFragment {

    private static final String BACKGROUND_COLOR = "backgroundColor";
    private static final String PAGE = "page";

    private int  mPage;
    private SlideShowImage slideVo;

    public static IntroFragment newInstance(SlideShowImage slideVo, int page) {
        IntroFragment frag = new IntroFragment();
        frag.mPage = page;
        frag.slideVo = slideVo;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Select a layout based on the current page
       /* int layoutResId;
        switch (mPage) {
            case 0:
                layoutResId = R.layout.intro_fragment_layout;
                break;
            default:
                layoutResId = R.layout.intro_fragment_layout_2;
        }*/

        // Inflate the layout resource file
        View view = Objects.requireNonNull(getActivity()).getLayoutInflater().inflate(R.layout.intro_fragment_layout, container, false);

        // Set the current page index as the View's tag (useful in the PageTransformer)
        view.setTag(mPage);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the background color of the root view to the color specified in newInstance()
        ((TextView) view.findViewById(R.id.title)).setText(slideVo.getTitle());
        ((TextView) view.findViewById(R.id.title)).setTextColor(Color.parseColor(slideVo.getTitleColor()));
        ((TextView) view.findViewById(R.id.description)).setText(slideVo.getDescription());
        Util.showImageWithGlide((ImageView) view.findViewById(R.id.ivImage), slideVo.getImage(), context);
        ((TextView) view.findViewById(R.id.description)).setTextColor(Color.parseColor(slideVo.getDescriptionColor()));
        view.findViewById(R.id.intro_background).setBackgroundColor(Color.parseColor(slideVo.getBackgroundColor()));
    }

}

