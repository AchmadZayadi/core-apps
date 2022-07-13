package com.sesolutions.ui.storyview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.utils.Util;

public class StoryReactionHelper {


    public void init(StoryContent vo, View llBottomLike) {
        //Context context=llBottomLike.getContext();
        if (null != vo.getReactionUserData() && null != vo.getReactionData()) {
            llBottomLike.setVisibility(View.VISIBLE);
            ImageView ivLikeUpper1 = llBottomLike.findViewById(R.id.ivLikeUpper1);
            ImageView ivLikeUpper2 = llBottomLike.findViewById(R.id.ivLikeUpper2);
            ImageView ivLikeUpper3 = llBottomLike.findViewById(R.id.ivLikeUpper3);
            ImageView ivLikeUpper4 = llBottomLike.findViewById(R.id.ivLikeUpper4);
            ImageView ivLikeUpper5 = llBottomLike.findViewById(R.id.ivLikeUpper5);
            ((TextView) llBottomLike.findViewById(R.id.tvLikeUpper)).setText(vo.getReactionUserData());
            if (vo.getReactionData().size() > 0) {
                ivLikeUpper1.setVisibility(View.VISIBLE);
                Util.showImageWithGlide(ivLikeUpper1, vo.getReactionData().get(0).getImageUrl());
            } else {
                ivLikeUpper1.setVisibility(View.GONE);
            }
            if (vo.getReactionData().size() > 1) {
                ivLikeUpper2.setVisibility(View.VISIBLE);
                Util.showImageWithGlide(ivLikeUpper2, vo.getReactionData().get(1).getImageUrl());
            } else {
                ivLikeUpper2.setVisibility(View.GONE);
            }
            if (vo.getReactionData().size() > 2) {
                ivLikeUpper3.setVisibility(View.VISIBLE);
                Util.showImageWithGlide(ivLikeUpper3, vo.getReactionData().get(2).getImageUrl());
            } else {
                ivLikeUpper3.setVisibility(View.GONE);
            }
            if (vo.getReactionData().size() > 3) {
                ivLikeUpper4.setVisibility(View.VISIBLE);
                Util.showImageWithGlide(ivLikeUpper4, vo.getReactionData().get(3).getImageUrl());
            } else {
                ivLikeUpper4.setVisibility(View.GONE);
            }
            if (vo.getReactionData().size() > 4) {
                ivLikeUpper5.setVisibility(View.VISIBLE);
                Util.showImageWithGlide(ivLikeUpper5, vo.getReactionData().get(4).getImageUrl());
            } else {
                ivLikeUpper5.setVisibility(View.GONE);
            }
        } else {
            llBottomLike.setVisibility(View.GONE);
        }
    }
}
