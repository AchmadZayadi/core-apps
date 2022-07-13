package com.sesolutions.responses.feed;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.utils.Constant;

public class Options {
    @SerializedName("name")
    private String name;
    @SerializedName("value")
    private String value;
    @SerializedName("cl")
    private int closed;
    @SerializedName("profreview_id")
    private int profreview_id;
    @SerializedName("review_id")
    private int review_id;
    @SerializedName("can_create_classroom")
    private boolean cancreateclassroom;
    @SerializedName("label")
    private String label;
    private String action;
    private String href;
    private UrlParams params;
    @SerializedName("class")
    private String clazz;
    private boolean isSelected;
    private String url;
    @SerializedName("is_useful")
    private int isUseful;
    @SerializedName("mute_id")
    private int muteId;
    private int close;
    private int sticky;

    public int getClose() {
        return close;
    }

    public int getClosed() {
        return closed;
    }

    public int getSticky() {
        return sticky;
    }

    public Options(String name, String label) {
        this.name = name;
        this.label = label;
        value = "0";
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getIsUseful() {
        return isUseful;
    }

    public String getHref() {
        return href;
    }

    public String getUrl() {
        return url;
    }

    public UrlParams getParams() {
        return params;
    }

    public void setParams(UrlParams params) {
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public int getprofreview_id() {
        return profreview_id;
    }

    public int getreview_id() {
        return review_id;
    }

    public String getClazz() {
        return clazz;
    }

    public String getAction() {
        return action;
    }

    public boolean getcancreateclassroom() {
        return cancreateclassroom;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void toggleFollow() {
        if (TextUtils.isEmpty(name)) return;
        name = name.equals(Constant.OptionType.UNFOLLOW) ? Constant.OptionType.FOLLOW : Constant.OptionType.UNFOLLOW;
        label = name.equals(Constant.OptionType.UNFOLLOW) ? Constant.TXT_MEMBER_UNFOLLOW : Constant.TXT_MEMBER_FOLLOW;
    }

    public void toggleBlock() {
        if (TextUtils.isEmpty(name)) return;
        name = name.equals(Constant.OptionType.BLOCK) ? Constant.OptionType.UNBLOCK : Constant.OptionType.BLOCK;
        label = name.equals(Constant.OptionType.BLOCK) ? Constant.TXT_MEMBER_BLOCK : Constant.TXT_MEMBER_UNBLOCK;
    }

    public void toggleUseful(String s1, String s2) {
        if (isUseful == 0) {
            isUseful = 1;
            label = s2;
        } else {
            isUseful = 0;
            label = s1;
        }
    }

    public int getMuteId() {
        return muteId;
    }

    public void setMuteId(int id) {
        muteId = id;
    }

    public void toggleValue() {
        if ("true".equals(value)) {
            value = "false";
            action = "" + (Integer.parseInt(action) - 1);
        } else {
            value = "true";
            action = "" + (Integer.parseInt(action) + 1);
        }

    }

    public String getParamsAction() {
        return null != params ? params.getAction() : "";
    }
}
