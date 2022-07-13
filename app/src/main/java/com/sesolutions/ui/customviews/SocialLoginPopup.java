package com.sesolutions.ui.customviews;

/**
 * Created by root on 29/12/17.
 */

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.ui.welcome.SocialOptionAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;

public class SocialLoginPopup extends RelativePopupWindow implements OnUserClickedListener<Integer, Object> {

    private final OnUserClickedListener<Integer, Object> listener;
    private final List<SearchVo> optionList;
    private View v;


    public SocialLoginPopup(Context context, OnUserClickedListener<Integer, Object> listener, List<SearchVo> optionList) {
        v = LayoutInflater.from(context).inflate(R.layout.dialog_list, null);
        setContentView(v);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.listener = listener;
        this.optionList = optionList;
        ((CardView) v.findViewById(R.id.cvMain)).setCardBackgroundColor(Color.parseColor(Constant.backgroundColor));

        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
        }
        //listener.onItemClicked(Constant.Events.POPUP, "" + false, position);
        setFeedUpdateRecycleView(context);
    }

    private void setFeedUpdateRecycleView(Context context) {
        try {
            RecyclerView recycleViewFeedUpdate = v.findViewById(R.id.rvFeedUpdate);
            recycleViewFeedUpdate.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewFeedUpdate.setLayoutManager(layoutManager);
            SocialOptionAdapter adapterFeed = new SocialOptionAdapter(optionList, context, this);
            recycleViewFeedUpdate.setAdapter(adapterFeed);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void showOnAnchor(@NonNull View anchor, int vertPos, int horizPos, int x, int y, boolean fitInScreen) {
        super.showOnAnchor(anchor, vertPos, horizPos, x, y, fitInScreen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circularReveal(anchor);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void circularReveal(@NonNull final View anchor) {
        final View contentView = getContentView();
        contentView.post(new Runnable() {
            @Override
            public void run() {

                try {
                    //region Description
                    final int[] myLocation = new int[2];
                    final int[] anchorLocation = new int[2];
                    contentView.getLocationOnScreen(myLocation);
                    anchor.getLocationOnScreen(anchorLocation);
                    final int cx = anchorLocation[0] - myLocation[0] + anchor.getWidth() / 2;
                    final int cy = anchorLocation[1] - myLocation[1] + anchor.getHeight() / 2;

                    contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    final int dx = Math.max(cx, contentView.getMeasuredWidth() - cx);
                    final int dy = Math.max(cy, contentView.getMeasuredHeight() - cy);
                    final float finalRadius = (float) Math.hypot(dx, dy);
                    Animator animator = ViewAnimationUtils.createCircularReveal(contentView, cx, cy, 0f, finalRadius);
                    animator.setDuration(400);
                    animator.start();
                    //endregion
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }
        });
    }

    @Override
    public void dismiss() {
        CustomLog.e("DISMIS_POPUP", "dismis");
        listener.onItemClicked(Constant.Events.POPUP, "" + true, -1);
        super.dismiss();
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        listener.onItemClicked(object1, object2, postion);
        dismiss();
        return false;
    }
}