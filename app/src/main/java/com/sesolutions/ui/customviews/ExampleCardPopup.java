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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.List;

public class ExampleCardPopup extends RelativePopupWindow implements View.OnClickListener, OnUserClickedListener<Integer, String> {

    private int EVENT_TYPE;
    private final OnUserClickedListener<Integer, Object> listener;
    private final int position;
    private final List<ReactionPlugin> reactionList;
    private View v;
/*    private ImageView ivLike;
    private ImageView ivLove;
    private ImageView ivHaha;
    private ImageView ivSad;
    private ImageView ivWow;
    private ImageView ivAngry;*/

    public ExampleCardPopup(Context context, int position, OnUserClickedListener<Integer, Object> listener, int EVENT_TYPE) {
        this(context, position, listener);
        this.EVENT_TYPE = EVENT_TYPE;
    }

    public ExampleCardPopup(Context context, int position, OnUserClickedListener<Integer, Object> listener) {
        this.EVENT_TYPE = position == -1 ? Constant.Events.VIEW_LIKED : Constant.Events.LIKED;
        v = LayoutInflater.from(context).inflate(R.layout.layout_reaction, null);
        setContentView(v);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.listener = listener;
        this.position = position;
        ((CardView) v.findViewById(R.id.cvMain)).setCardBackgroundColor(Color.parseColor(Constant.foregroundColor));
        // iv1 = v.findViewById(R.id.reaction1);
        ImageView iv1 = v.findViewById(R.id.reaction1);
        ImageView iv2 = v.findViewById(R.id.reaction2);
        ImageView iv3 = v.findViewById(R.id.reaction3);
        ImageView iv4 = v.findViewById(R.id.reaction4);
        ImageView iv5 = v.findViewById(R.id.reaction5);
        ImageView iv6 = v.findViewById(R.id.reaction6);
        ImageView iv7 = v.findViewById(R.id.reaction7);
        ImageView iv8 = v.findViewById(R.id.reaction8);
        ImageView iv9 = v.findViewById(R.id.reaction9);
        ImageView iv10 = v.findViewById(R.id.reaction10);
        reactionList = SPref.getInstance().getReactionPlugins(context);

        int size = reactionList.size();
        if (size > 0) {
            Util.showImageWithGlide(iv1, reactionList.get(0).getImage(), context);
        } else {
            iv1.setVisibility(View.GONE);
        }
        if (size > 1) {
            Util.showImageWithGlide(iv2, reactionList.get(1).getImage(), context);
        } else {
            iv2.setVisibility(View.GONE);
        }
        if (size > 2) {
            Util.showImageWithGlide(iv3, reactionList.get(2).getImage(), context);
        } else {
            iv3.setVisibility(View.GONE);
        }
        if (size > 3) {
            Util.showImageWithGlide(iv4, reactionList.get(3).getImage(), context);
        } else {
            iv4.setVisibility(View.GONE);
        }
        if (size > 4) {
            Util.showImageWithGlide(iv5, reactionList.get(4).getImage(), context);
        } else {
            iv5.setVisibility(View.GONE);
        }
        if (size > 5) {
            Util.showImageWithGlide(iv6, reactionList.get(5).getImage(), context);
        } else {
            iv6.setVisibility(View.GONE);
        }
        if (size > 6) {
            iv7.setVisibility(View.VISIBLE);
            Util.showImageWithGlide(iv7, reactionList.get(6).getImage(), context);
        }
        if (size > 7) {
            iv8.setVisibility(View.VISIBLE);
            Util.showImageWithGlide(iv8, reactionList.get(7).getImage(), context);
        }
        if (size > 8) {
            iv9.setVisibility(View.VISIBLE);
            Util.showImageWithGlide(iv9, reactionList.get(8).getImage(), context);
        }
        if (size > 9) {
            iv10.setVisibility(View.VISIBLE);
            Util.showImageWithGlide(iv10, reactionList.get(9).getImage(), context);
        }


        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);
        iv3.setOnClickListener(this);
        iv4.setOnClickListener(this);
        iv5.setOnClickListener(this);
        iv6.setOnClickListener(this);
        iv7.setOnClickListener(this);
        iv8.setOnClickListener(this);
        iv9.setOnClickListener(this);
        iv10.setOnClickListener(this);


        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
        }
        listener.onItemClicked(Constant.Events.POPUP, "" + false, position);
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
    public void onClick(View v) {
        int pos = 0;
        switch (v.getId()) {
            case R.id.reaction1:
                CustomLog.e("reaction_click", "R.id.reaction1");
                pos = 0;
                break;
            case R.id.reaction2:
                CustomLog.e("reaction_click", "R.id.reaction2");
                pos = 1;

                break;
            case R.id.reaction3:
                CustomLog.e("reaction_click", "R.id.reaction3");
                pos = 2;

                break;
            case R.id.reaction4:
                CustomLog.e("reaction_click", "R.id.reaction4");
                pos = 3;
                break;
            case R.id.reaction5:
                CustomLog.e("reaction_click", "R.id.reaction5");
                pos = 4;
                break;
            case R.id.reaction6:
                CustomLog.e("reaction_click", "R.id.reaction6");
                pos = 5;
                break;
            case R.id.reaction7:
                CustomLog.e("reaction_click", "R.id.reaction6");
                pos = 6;
                break;
            case R.id.reaction8:
                CustomLog.e("reaction_click", "R.id.reaction6");
                pos = 7;
                break;
            case R.id.reaction9:
                CustomLog.e("reaction_click", "R.id.reaction6");
                pos = 8;
                break;
            case R.id.reaction10:
                CustomLog.e("reaction_click", "R.id.reaction6");
                pos = 9;
                break;
        }
        //listener.onItemClicked(Constant.Events.LIKED, "" + reactionList.get(pos).getReaction_id(), position);
        listener.onItemClicked(EVENT_TYPE, "" + pos, position);
        dismiss();
    }

    @Override
    public void dismiss() {
        CustomLog.e("DISMIS_POPUP", "dismis");
        listener.onItemClicked(Constant.Events.POPUP, "" + true, position);
        super.dismiss();
    }

    @Override
    public boolean onItemClicked(Integer object1, String object2, int postion) {
        return false;
    }
}