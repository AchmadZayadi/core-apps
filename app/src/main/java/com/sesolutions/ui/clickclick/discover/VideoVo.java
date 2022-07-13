package com.sesolutions.ui.clickclick.discover;

import com.sesolutions.responses.business.BusinessContent;
import com.sesolutions.responses.contest.Contest;
import com.sesolutions.responses.fund.FundContent;
import com.sesolutions.responses.groups.GroupContent;
import com.sesolutions.responses.page.PageContent;
import com.sesolutions.responses.videos.Videos;

public class VideoVo {

    private final String type;
    private final Object value;
    private final String title;

    public VideoVo(String type, Object vo, String title) {
        this.type = type;
        value = vo;
        this.title = title;
    }


    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        if (null == value) {
            return null;
        }
        return (T) value;
    }


    public Contest getContest() {
        if (null == value) {
            return null;
        }
        return (Contest) value;
    }

    public Videos getvideos() {
        if (null == value) {
            return null;
        }
        return (Videos) value;
    }

    public PageContent getItem() {
        if (null == value) {
            return null;
        }
        return (PageContent) value;
    }

    public FundContent getFund() {
        if (null == value) {
            return null;
        }
        return (FundContent) value;
    }

    public GroupContent getGroup() {
        if (null == value) {
            return null;
        }
        return (GroupContent) value;
    }

    public BusinessContent getBusiness() {
        if (null == value) {
            return null;
        }
        return (BusinessContent) value;
    }
}

