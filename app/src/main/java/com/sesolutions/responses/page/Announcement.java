package com.sesolutions.responses.page;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.utils.SpanUtil;

import java.util.List;

public class Announcement {
    private String title;
    @SerializedName("announcement_id")
    private int announcementId;
    @SerializedName("crowdfunding_id")
    private int crowdfundingId;
    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName(value = "detail", alternate = {"body"})
    private String body;
    private List<Options> options;

    public String getTitle() {
        return title;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getDetail() {

        if (body != null)
            return SpanUtil.getHtmlString(body);
        else return "";
    }

    public int getAnnouncementId() {
        return announcementId;
    }

    public int getCrowdfundingId() {
        return crowdfundingId;
    }

    public List<Options> getOptions() {
        return options;
    }
}
