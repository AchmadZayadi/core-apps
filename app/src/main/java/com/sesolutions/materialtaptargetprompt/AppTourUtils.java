package com.sesolutions.materialtaptargetprompt;

import androidx.annotation.StringRes;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import android.view.View;

import com.sesolutions.R;
import com.sesolutions.materialtaptargetprompt.extras.focals.RectanglePromptFocal;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.SesColorUtils;

public class AppTourUtils {
    public static MaterialTapTargetPrompt.Builder getDefaultPrompt(final View view, BaseFragment fragment, @StringRes final int text1, @StringRes final int text2) {
        return new MaterialTapTargetPrompt.Builder(fragment)
                .setPrimaryText(text1)
                .setBackgroundColour(SesColorUtils.getPrimaryColor(fragment.getContext()))
                .setFocalColour(SesColorUtils.getNavigationTitleColor(fragment.getContext()))
                .setSecondaryText(text2)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setMaxTextWidth(R.dimen.max_prompt_width)
                .setTarget(view);
    }

    public static void showDefaultPrompt(final View view, BaseFragment fragment, @StringRes final int text1, @StringRes final int text2) {
        new MaterialTapTargetPrompt.Builder(fragment)
                .setPrimaryText(text1)
                .setBackgroundColour(SesColorUtils.getPrimaryColor(fragment.getContext()))
                .setFocalColour(SesColorUtils.getNavigationTitleColor(fragment.getContext()))
                .setSecondaryText(text2)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .setMaxTextWidth(R.dimen.max_prompt_width)
                .setTarget(view).showFor(7000);
    }

    public static void showDrawerSequence(BaseFragment fragment, View... view) {

        new MaterialTapTargetSequence()

                .addPrompt(new MaterialTapTargetPrompt.Builder(fragment)
                        .setTarget(view[0])
                        .setCaptureTouchEventOutsidePrompt(true)
                        .setPrimaryText(R.string.profile)
                        .setBackgroundColour(SesColorUtils.getPrimaryColor(fragment.getContext()))
                        //.setFocalColour(SesColorUtils.getNavigationTitleColor(fragment.getContext()))
                        .setSecondaryText(R.string.profile_d)
                        .setFocalPadding(R.dimen.margin_super)
                        .create(), 6000)
                .addPrompt(new MaterialTapTargetPrompt.Builder(fragment)
                        .setTarget(view[1])
                        .setCaptureTouchEventOutsidePrompt(true)
                        .setPrimaryText(R.string.navigation_item)
                        .setPromptFocal(new RectanglePromptFocal())
                        .setBackgroundColour(SesColorUtils.getPrimaryColor(fragment.getContext()))
                        //.setFocalColour(SesColorUtils.getNavigationTitleColor(fragment.getContext()))
                        .setSecondaryText(R.string.navigation_item_d)
                        .setAnimationInterpolator(new LinearOutSlowInInterpolator())
                        .setFocalPadding(R.dimen.margin_quarter)
                        //.setIcon(R.drawable.ic_search)
                        .create(), 6000)
                .show();
    }
}
