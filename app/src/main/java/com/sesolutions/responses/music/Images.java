package com.sesolutions.responses.music;

import com.google.gson.annotations.SerializedName;

public class Images {
    @SerializedName("main")
    private String main;
    @SerializedName("icon")
    private String icon;
    @SerializedName("normal")
    private String normal;
    @SerializedName("profile")
    private String profile;

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNormal() {
        if (null != normal) {
            return normal;
        } else return getMain();
    }

    public void setNormal(String normal) {
        this.normal = normal;
    }

    public String getProfile() {

        if (null != profile) {
            return profile;
        } else return getNormal();
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
