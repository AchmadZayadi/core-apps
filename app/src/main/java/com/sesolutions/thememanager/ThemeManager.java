package com.sesolutions.thememanager;

import android.content.Context;
import android.graphics.Color;
import com.google.android.material.tabs.TabLayout;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rd.PageIndicatorView;
import com.sesolutions.sesdb.SesDB;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import java.util.Map;
import java.util.Set;


/**
 * Created by omkar on 24/5/17.
 */

public class ThemeManager {

    private JsonObject rulesJson;


    public ThemeManager(JsonObject rulesJson) {
        this.rulesJson = rulesJson;
    }

    public ThemeManager(String ruleJson) {
        rulesJson = new Gson().fromJson(ruleJson, JsonElement.class).getAsJsonObject();
    }

    public ThemeManager() {
    }

    public void applyTheme(ViewGroup view) {
        applyTheme(view, view.getContext());
    }

    public void applyTheme(ViewGroup view, Context context) {

        if (TextUtils.isEmpty(Constant.THEME_STYLES_JSON)) {
            Constant.THEME_STYLES_JSON = (null != SPref.getInstance().getString(context, Constant.KEY_THEME_STYLE))
                    ? SPref.getInstance().getString(context, Constant.KEY_THEME_STYLE) : SesDB.themeDao(context).getTheme().get(0).getColorTheme();
        }

        if (TextUtils.isEmpty(Constant.THEME_STYLES_JSON)) {
            return;
        } else {
            rulesJson = new Gson().fromJson(Constant.THEME_STYLES_JSON, JsonElement.class).getAsJsonObject();
        }
        try {
            int numOfViews = view.getChildCount();
            applyTheme((View) view, context);
            View childView = null;

            for (int i = 0; i < numOfViews; i++) {
                childView = view.getChildAt(i);
                if (childView instanceof ViewGroup) {
                    applyTheme((ViewGroup) childView, context);
                } else {
                    applyTheme(childView, context);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void applyDarkTheme(ViewGroup view, Context context) {

        try {
            int numOfViews = view.getChildCount();
            applyTheme((View) view, context);
            View childView = null;

            for (int i = 0; i < numOfViews; i++) {
                childView = view.getChildAt(i);
                if (childView instanceof ViewGroup) {
                    applyDarkTheme((ViewGroup) childView, context);
                } else {
                    applyTheme(childView, context);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void applyTheme(View view, Context context) {
        try {
            Object tagObj = view.getTag();
            if (tagObj == null) {
                return;
            }
            String tag = null;
            if (tagObj instanceof String) {
                tag = (String) tagObj;
            /*} else if (tagObj instanceof CTag) {
                tag = ((CTag) tagObj).first();
            }

            if (tag != null) {*/
                if (rulesJson.has(tag)) {
                    JsonObject rule = rulesJson.get(tag).getAsJsonObject();
                    if (rule != null) {
                        // applyDrawableStyles(rule, view, context);
                        applyTextStyles(rule, view, context);
                    }
                } else {
                    CustomLog.e("no_tag", tag);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void applyDrawableStyles(JsonObject ruleObj, View view, Context context) {
        Set<Map.Entry<String, JsonElement>> rules = ruleObj.entrySet();
        CDrawable drawable = CDrawable.build(context);
        for (Map.Entry<String, JsonElement> entry : rules) {
            String ruleKey = entry.getKey();
            String ruleValue = entry.getValue().getAsString();
            if (ruleKey.equals("borderRadius")) {
                if (isNumeric(ruleValue)) {
                    drawable = drawable.withCornerRadius(Integer.parseInt(ruleValue));
                }
            }
           /* if (ruleKey.equals("borderWidth")) {
                if (isNumeric(ruleValue)) {
                    JsonElement borderColorValue = ruleObj.getAsJsonObject().get("borderColor");
                    if (!(borderColorValue instanceof JsonNull)) {
                        String borderColor = borderColorValue
                                .getAsString();
                        drawable.setStroke(Integer.parseInt(ruleValue), Color.parseColor(borderColor));
                    }
                }
            }*/
            if (ruleKey.equals("bgColor")) {
                drawable = handleBgColor(drawable, ruleValue);
            }
           /* if (ruleKey.equals("tint")) {
                drawable.setTint(Color.parseColor(ruleValue));
            }
           if (ruleKey.equals("bgGradientColors")) {
                drawable = handleGradientColors(drawable, ruleValue);
            }
            if (ruleKey.equals("bgGradientType")) {
                drawable = drawable.withGradientType(ruleValue);
            }*/
            view.setBackground(drawable);
        }
    }

    private void applyTextStyles(JsonObject ruleObj, View view, Context context) {

        try {
            Set<Map.Entry<String, JsonElement>> rules = ruleObj.entrySet();
            // if (view instanceof TextView) {
            for (Map.Entry<String, JsonElement> entry : rules) {
                String ruleKey = entry.getKey();
                String ruleValue = entry.getValue().getAsString();
                   /* if (ruleKey.equals("textSize")) {
                        if (isNumeric(ruleValue)) {
                            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, Integer.parseInt(ruleValue));
                        }
                    }*/
                switch (ruleKey) {
                    case "textColor":
                        ((TextView) view).setTextColor(Color.parseColor(ruleValue));
                        break;
                    case "editTextColor":
                        ((EditText) view).setTextColor(Color.parseColor(ruleValue));
                        break;
                    case "editHintColor":
                        ((EditText) view).setHintTextColor(Color.parseColor(ruleValue));
                        break;
                    case "tint":
                        ((ImageView) view).setColorFilter(Color.parseColor(ruleValue));
                        break;
                    case "cardColor":
                        ((CardView) view).setCardBackgroundColor(Color.parseColor(ruleValue));
                        break;
                    case "mainBack":
                        view.setBackgroundColor(Color.parseColor(ruleValue));
                        break;
                    case "toolbar":
                        view.setBackgroundColor(Color.parseColor(ruleValue));
                        break;
                    case "tabLayout":
                    case "tab_layout":
                        view.setBackgroundColor(Color.parseColor(ruleValue));
                        ((TabLayout) view).setSelectedTabIndicatorColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
                        ((TabLayout) view).setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));
                        break;
                    case "page_indicator":
                        view.setBackgroundColor(Color.parseColor(ruleValue));
                        ((PageIndicatorView) view).setUnselectedColor(Color.parseColor(Constant.dividerColor));
                        ((PageIndicatorView) view).setSelectedColor(Color.parseColor(Constant.colorPrimary));
                        break;
                    case "buttonColor":
                        ((Button) view).setTextColor(Color.parseColor(ruleValue));
                        break;
                    case "buttonBack":
                        view.setBackgroundColor(Color.parseColor(ruleValue));
                        break;
                    case "bgColor":
                        CDrawable drawable = CDrawable.build(context);
                        drawable = drawable.withCornerRadius(Constant.DIALOG_RADIUS);
                        drawable = drawable.withColor(Color.parseColor(Constant.foregroundColor));
                        view.setBackground(drawable);
                        break;
                }
            }
            //  }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public CDrawable handleBgColor(CDrawable drawable, String ruleValue) {
        return drawable.withColor(Color.parseColor(ruleValue));
    }

    public CDrawable handleGradientColors(CDrawable drawable, String ruleValue) {
        String[] colorStrings = ruleValue.split(",");
        int[] colorArray = new int[colorStrings.length];
        int i = 0;
        for (String colorString : colorStrings) {
            colorArray[i++] = Color.parseColor(colorString.trim());
        }
        return drawable.withColors(colorArray);
    }

}
