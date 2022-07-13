package com.sesolutions.responses.store;

import com.sesolutions.responses.business.BusinessContent;
import com.sesolutions.responses.contest.Contest;
import com.sesolutions.responses.groups.GroupContent;
import com.sesolutions.responses.page.PageContent;

import java.util.List;

public class StoreVo {

    private final String type;
    private final Object value;

    public StoreVo(String type, Object vo) {
        this.type = type;
        value = vo;
    }


    public String getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        if (null == value) {
            return null;
        }
        return (T) value;
    }

    public List<StoreContent> getProducts() {
        if (null == value) {
            return null;
        }
        return (List<StoreContent>) value;

    }

    public Contest getContest() {
        if (null == value) {
            return null;
        }
        return (Contest) value;
    }

    public StoreContent getItem() {
        if (null == value) {
            return null;
        }
        return (StoreContent) value;
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
