package com.sesolutions.ui.photo;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.sesolutions.R;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.customviews.FlingLayout;
import com.sesolutions.ui.customviews.photoview.PhotoView;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import kotlin.Unit;

public class SinglePhotoFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private Bundle bundle;
    private String imageUrl;
    private PhotoView ivImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // postponeEnterTransition();
            // setEnterTransition(new AutoTransition());
            //setExitTransition(new AutoTransition());
            // setEnterTransition(new Fade(Fade.IN));
            // setExitTransition(new Fade(Fade.OUT));
            //  setEnterTransition(new AutoTransition());
            //  setExitTransition(new Explode());
            //  setSharedElementEnterTransition(new DetailsTransition());
            //  setSharedElementReturnTransition(new DetailsTransition());
            //  setAllowEnterTransitionOverlap(false);
            //  setAllowReturnTransitionOverlap(false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivImage.setTransitionName(bundle.getString(Constant.Trans.IMAGE));
            // tvUserTitle.setTransitionName(bundle.getString(Constant.Trans.TEXT));
            //  tvUserTitle.setText(bundle.getString(Constant.Trans.IMAGE));
           /* setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                    ProfileFragment.this.setEnterSharedElementCallback(null);
                   // callMusicAlbumApi(1);
                }
            });*/
            //   v.findViewById(R.id.rlUpper).setTransitionName(bundle.getString(Constant.Trans.LAYOUT));
            try {
                Glide.with(context)
                        .setDefaultRequestOptions(new RequestOptions().dontAnimate().dontTransform().centerCrop())
                        .load(bundle.getString(Constant.Trans.IMAGE_URL))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                startPostponedEnterTransition();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                startPostponedEnterTransition();
                                return false;
                            }
                        })
                        .into(ivImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
            //    Util.showImageWithGlide(ivAlbumImage, bundle.getString(Constant.Trans.IMAGE_URL), context);
        } /*else {
            callMusicAlbumApi(1);
        }*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_photo_list, container, false);
        try {
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.setStatusBarColor(Color.BLACK);
    }

    @Override
    public void onStop() {
        activity.setStatusBarColor(Util.manipulateColor(Color.parseColor(Constant.colorPrimary)));
        super.onStop();
    }

    private void init() {

        final FlingLayout flingLayout = v.findViewById(R.id.fling_layout);
        flingLayout.setBackgroundColor(Color.BLACK);
        ivImage = v.findViewById(R.id.ivSongImage);

        flingLayout.setDismissListener(() -> {
            onBackPressed();
            return Unit.INSTANCE;
        });

        if (null == bundle)
            Glide.with(context).load(imageUrl).into(ivImage);

        ivImage.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> flingLayout.setDragEnabled(scaleFactor <= 1F));


    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static SinglePhotoFragment newInstance(String url, Bundle bundle) {
        SinglePhotoFragment frag = new SinglePhotoFragment();
        frag.bundle = bundle;
        frag.imageUrl = url;
        return frag;
    }
}
