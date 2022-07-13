package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.utils.SpanUtil;

/**
 * Created by root on 10/11/17.
 */

public class Attachments {

    @SerializedName("message_id")
    private int messageId;
    @SerializedName("attachment_id")
    private int attachmentId;
    @SerializedName("attachment_type")
    private String attachmentType;
    @SerializedName("attachment_title")
    private String attachmentTitle;
    @SerializedName("attachment_photo_width")
    private int attachmentPhotoWidth;
    @SerializedName("attachment_photo_height")
    private int attachmentPhotoHeight;
    @SerializedName("attachment_description")
    private String attachmentDescription;
    @SerializedName("attachment_photo")
    private String attachmentPhoto;


    @SerializedName("attachment_uri")
    private String attachmentUri;


    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getAttachmentTitle() {
        return attachmentTitle;
    }

    public void setAttachmentTitle(String attachmentTitle) {
        this.attachmentTitle = attachmentTitle;
    }

    public int getAttachmentPhotoWidth() {
        return attachmentPhotoWidth;
    }

    public void setAttachmentPhotoWidth(int attachmentPhotoWidth) {
        this.attachmentPhotoWidth = attachmentPhotoWidth;
    }

    public int getAttachmentPhotoHeight() {
        return attachmentPhotoHeight;
    }

    public void setAttachmentPhotoHeight(int attachmentPhotoHeight) {
        this.attachmentPhotoHeight = attachmentPhotoHeight;
    }

    public String getAttachmentDescription() {
        return SpanUtil.getHtmlString(attachmentDescription);
    }

    public void setAttachmentDescription(String attachmentDescription) {
        this.attachmentDescription = attachmentDescription;
    }

    public String getAttachmentPhoto() {
        return attachmentPhoto;
    }

    public void setAttachmentPhoto(String attachmentPhoto) {
        this.attachmentPhoto = attachmentPhoto;
    }

    public String getAttachmentUri() {
        return attachmentUri;
    }

    public void setAttachmentUri(String attachmentUri) {
        this.attachmentUri = attachmentUri;
    }
}
