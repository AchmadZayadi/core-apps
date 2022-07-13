package com.sesolutions.responses.videos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 6/12/17.
 */

public class Tags {

    @SerializedName("tagmap_id")
    private int tagmapId;
    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("resource_id")
    private int resourceId;
    @SerializedName("tagger_type")
    private String taggerType;
    @SerializedName("tagger_id")
    private int taggerId;
    @SerializedName("tag_type")
    private String tagType;
    @SerializedName("hashtag_id")
    private int hashtagId;
    @SerializedName("tag_id")
    private int tagId;
    private String title;
    @SerializedName("creation_date")
    private String creationDate;
    private String text;

    public int getHashtagId() {
        return hashtagId;
    }

    public void setHashtagId(int hashtagId) {
        this.hashtagId = hashtagId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTagmapId() {
        return tagmapId;
    }

    public void setTagmapId(int tagmapId) {
        this.tagmapId = tagmapId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getTaggerType() {
        return taggerType;
    }

    public void setTaggerType(String taggerType) {
        this.taggerType = taggerType;
    }

    public int getTaggerId() {
        return taggerId;
    }

    public void setTaggerId(int taggerId) {
        this.taggerId = taggerId;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
