package com.sesolutions.responses.contest;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.utils.SpanUtil;

public class Banner {
    private String image;
    @SerializedName("banner_title")
    private String bannerTitle;
    private String description;

    public String getImage() {
        return image;
    }

    public String getBannerTitle() {
        return bannerTitle;
    }

    public String getDescription() {
        return SpanUtil.getHtmlString(description);
    }
}
