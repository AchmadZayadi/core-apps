package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.utils.SpanUtil;

/**
 * Created by root on 28/11/17.
 */

public class Networks {

    @SerializedName("network_id")
    private int networkId;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("field_id")
    private int fieldId;
    @SerializedName("member_count")
    private int memberCount;
    @SerializedName("hide")
    private int hide;
    @SerializedName("assignment")
    private int assignment;

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return SpanUtil.getHtmlString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getHide() {
        return hide;
    }

    public void setHide(int hide) {
        this.hide = hide;
    }

    public int getAssignment() {
        return assignment;
    }

    public void setAssignment(int assignment) {
        this.assignment = assignment;
    }
}
