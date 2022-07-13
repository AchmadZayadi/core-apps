package com.sesolutions.ui.postfeed;


import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.imageeditengine.ImageEditActivity;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.dashboard.composervo.ActivityStikersMenu;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SesColorUtils;

import java.util.List;

public class FeelingStickerFragment extends BaseFragment implements View.OnClickListener {

    private static final int FEELING = 2;
    private static final int STICKER = 1;
    private static final int ACTIVITY = 0;
    private View v;

    TextView tvFeeling;
    TextView tvSticker;
    TextView tvActivity;

    private FeelingFragment feelingFragment;
    private ActivityFragment activityFragment;
    private StickerFragment stickerFragment;
    private int text2;
    private int colorPrimary;
    private List<ActivityStikersMenu> activityStikersMenu;
    public static TextView tvTitle;
    View devider12;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_feeling_sticker, container, false);
        try {
            init();
            applyTheme(v);
            text2 = SesColorUtils.getText2Color(context);
            colorPrimary = SesColorUtils.getPrimaryColor(context);


            if (activityStikersMenu.size() > 2) {
                v.findViewById(R.id.llTabs).setVisibility(View.VISIBLE);
                selectScreen(FEELING);
            }
            else {
                v.findViewById(R.id.llTabs).setVisibility(View.GONE);
                selectScreen(STICKER);
            }

            if(isboolen){
                tvFeeling.setVisibility(View.GONE);
                devider12.setVisibility(View.GONE);
                v.findViewById(R.id.llTabs).setVisibility(View.VISIBLE);
                selectScreen(STICKER);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (stickerFragment != null)
            stickerFragment.refreshBottomList();
    }

    private void selectScreen(int screen) {
        tvTitle.setText(activityStikersMenu.get(screen).getTitle());
        tvActivity.setTextColor(text2);
        tvSticker.setTextColor(text2);
        tvFeeling.setTextColor(text2);
        switch (screen) {
            case FEELING:
                tvFeeling.setTextColor(colorPrimary);
                if (feelingFragment == null) {
                    feelingFragment = new FeelingFragment();
                }
                getChildFragmentManager().beginTransaction().replace(R.id.container_feeling, feelingFragment).commit();
                break;
            case STICKER:
                tvSticker.setTextColor(colorPrimary);
                if (stickerFragment == null) {
                    stickerFragment = new StickerFragment();
                }
                getChildFragmentManager().beginTransaction().replace(R.id.container_feeling, stickerFragment).commit();
                break;
            case ACTIVITY:

                tvActivity.setTextColor(colorPrimary);
                if (activityFragment == null) {
                    activityFragment = new ActivityFragment();
                }
                getChildFragmentManager().beginTransaction().replace(R.id.container_feeling, activityFragment).commit();
                break;
        }
    }

    private void init() {
        tvFeeling = v.findViewById(R.id.tvFeeling);
        tvSticker = v.findViewById(R.id.tvSticker);
        tvActivity = v.findViewById(R.id.tvActivity);
        devider12=v.findViewById(R.id.devider12);

        tvFeeling.setOnClickListener(this);
        tvSticker.setOnClickListener(this);
        tvActivity.setOnClickListener(this);

        tvTitle = v.findViewById(R.id.tvTitle);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.tvDone).setOnClickListener(this);


    }

    @Override
    public void onBackPressed() {
        if (activity instanceof ImageEditActivity) {
            activity.currentFragment = null;
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                case R.id.tvDone:
                    onBackPressed();
                    break;
                case R.id.tvFeeling:
                    selectScreen(FEELING);
                    break;
                case R.id.tvSticker:
                    selectScreen(STICKER);
                    break;

                case R.id.tvActivity:
                    selectScreen(ACTIVITY);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static FeelingStickerFragment newInstance(List<ActivityStikersMenu> activityStikersMenu) {
        FeelingStickerFragment frag = new FeelingStickerFragment();
        frag.activityStikersMenu = activityStikersMenu;
        return frag;
    }

    Boolean isboolen=false;
    public static FeelingStickerFragment newInstance(List<ActivityStikersMenu> activityStikersMenu,Boolean isboolen21) {
        FeelingStickerFragment frag = new FeelingStickerFragment();
        frag.activityStikersMenu = activityStikersMenu;
        frag.isboolen = isboolen21;
        return frag;
    }

}
