package com.sesolutions.responses.event;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.feed.Options;

import java.util.List;

public class EventCore extends CommonVO {

    //for Core event
    @SerializedName("profile_tabs")
    private List<Options> profileTabs;
    private List<Options> gutterMenu;

    @Override
    public List<Options> getProfileTabs() {
        return profileTabs;
    }

    public List<Options> getGutterMenu() {
        return gutterMenu;
    }
}
