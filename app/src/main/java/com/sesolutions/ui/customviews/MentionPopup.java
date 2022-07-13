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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Friends;
import com.sesolutions.ui.postfeed.TagSuggestionAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;
import java.util.List;

public class MentionPopup extends RelativePopupWindow implements OnUserClickedListener<Integer, String> {

    private final OnUserClickedListener<Integer, String> listener;
    private RecyclerView rvTag;
    private View pb;

    public void updateMentionList(List<Friends> friendList) {
        this.friendList.clear();
        this.friendList.addAll(friendList);
        pb.setVisibility(View.GONE);
        rvTag.setVisibility(View.VISIBLE);
    }

    public void showLoader() {
        this.friendList.clear();
        // this.friendList.addAll(friendList);
        pb.setVisibility(View.VISIBLE);
        rvTag.setVisibility(View.GONE);
    }

    //  private final List<Reaction_plugin> reactionList;
    private List<Friends> friendList;
    private final TagSuggestionAdapter adapter;
    /*    private ImageView ivLike;
    private ImageView ivLove;
    private ImageView ivHaha;
    private ImageView ivSad;
    private ImageView ivWow;
    private ImageView ivAngry;*/

    public MentionPopup(Context context, OnUserClickedListener<Integer, String> listener) {
        View v = LayoutInflater.from(context).inflate(R.layout.popup_mention, null);
        setContentView(v);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.listener = listener;
        // iv1 = v.findViewById(R.id.reaction1);
        rvTag = v.findViewById(R.id.rvTag);
        pb = v.findViewById(R.id.pb);
        friendList = new ArrayList<>();


        rvTag.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvTag.setLayoutManager(layoutManager);
        adapter = new TagSuggestionAdapter(friendList, context, this);
        rvTag.setAdapter(adapter);
        //  rvAttach1.setNestedScrollingEnabled(false);


        // Disable default animation for circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimationStyle(0);
        }
//        listener.onItemClicked(Constant.Events.POPUP, "" + false, position);
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
        listener.onItemClicked(Constant.Events.POPUP, "" + true, 0);
        super.dismiss();
    }

    @Override
    public boolean onItemClicked(Integer object1, String object2, int postion) {
        listener.onItemClicked(Constant.Events.USER_SELECT, "", postion);
        dismiss();
        return false;
    }
}