package com.sesolutions.ui.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sesolutions.R;

/**
 * Created by root on 2/1/18.
 */
public class HeaderView extends RelativeLayout {

    // @Bind(R.id.header_view_title)
    TextView title;

    // @Bind(R.id.header_view_sub_title)
    TextView subTitle;

    Context context;

    public HeaderView(Context context) {
        super(context);
        this.context = context;
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // ButterKnife.bind(this.findViewById());
        title = findViewById(R.id.header_view_title);
        subTitle = findViewById(R.id.header_view_sub_title);
    }

    public void bindTo(String title) {
        bindTo(title, "");
    }

    public void bindTo(String title, String subTitle) {
        hideOrSetText(this.title, title);
        hideOrSetText(this.subTitle, subTitle);
    }

    private void hideOrSetText(TextView tv, String text) {
        if (text == null || text.equals(""))
            tv.setVisibility(GONE);
        else
            tv.setText(text);
    }

    public void setScaleXTitle(float scaleXTitle) {
        title.setScaleX(scaleXTitle);
        title.setPivotX(0);
    }

    public void setScaleYTitle(float scaleYTitle) {
        title.setScaleY(scaleYTitle);
        title.setPivotY(30);
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getSubTitle() {
        return subTitle;
    }
}
