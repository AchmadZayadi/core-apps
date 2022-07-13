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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.dashboard.FeedUpdateAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;

public class FeedOptionPopup extends RelativePopupWindow implements OnUserClickedListener<Integer, Object> {

    private final OnUserClickedListener<Integer, Object> listener;
    private final int position;
    private final List<Options> optionList;
    private View v;
    private RecyclerView recycleViewFeedUpdate;
    private FeedUpdateAdapter adapterFeed;


    public FeedOptionPopup(Context context, int position, OnUserClickedListener<Integer, Object> listener, List<Options> optionList) {
        v = LayoutInflater.from(context).inflate(R.layout.dialog_list, null);
        setContentView(v);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.listener = listener;
        this.position = position;
        this.optionList = optionList;
        ((CardView) v.findViewById(R.id.cvMain)).setCardBackgroundColor(Color.parseColor(Constant.foregroundColor));

        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
        }
        //listener.onItemClicked(Constant.Events.POPUP, "" + false, position);
        setFeedUpdateRecycleView(context, position);
    }

    private void setFeedUpdateRecycleView(Context context, int position) {
        try {
            recycleViewFeedUpdate = v.findViewById(R.id.rvFeedUpdate);
            recycleViewFeedUpdate.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewFeedUpdate.setLayoutManager(layoutManager);
            adapterFeed = new FeedUpdateAdapter(optionList, context, this, null);
            adapterFeed.setActivityPosition(position);
            recycleViewFeedUpdate.setAdapter(adapterFeed);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void runLayoutAnimation(final RecyclerView recyclerView) {
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(v.getContext(), R.anim.anim_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void showOnAnchor(@NonNull View anchor, int vertPos, int horizPos, int x, int y, boolean fitInScreen) {
        super.showOnAnchor(anchor, vertPos, horizPos, x, y, fitInScreen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circularReveal(anchor);
        } else {
            updateRecylerView();
        }
    }

    private void updateRecylerView() {
        //runLayoutAnimation(recycleViewFeedUpdate);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void circularReveal(@NonNull final View anchor) {
        final View contentView = getContentView();
        contentView.post(() -> {

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
                animator.setDuration(350);
                animator.addListener(new AnimationAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        updateRecylerView();
                    }
                });
                animator.start();
                //endregion
            } catch (Exception e) {
                CustomLog.e(e);
            }
        });
    }

    @Override
    public void dismiss() {
        CustomLog.e("DISMIS_POPUP", "dismis");
        listener.onItemClicked(Constant.Events.POPUP, "" + true, position);
        super.dismiss();
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        listener.onItemClicked(object1, object2, postion);
        dismiss();
        return false;
    }
}