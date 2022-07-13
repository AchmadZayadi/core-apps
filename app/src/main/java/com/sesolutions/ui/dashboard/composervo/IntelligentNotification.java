package com.sesolutions.ui.dashboard.composervo;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.utils.CustomLog;

import java.io.Serializable;

public class IntelligentNotification implements Serializable {
    @SerializedName("welcome_message")
    private FeedHtml welcomeMsg;
    @SerializedName("birthdayUser")
    private FeedHtml birthdayUser;
    @SerializedName("viewerBirthday")
    private FeedHtml viewerBirthday;

    private class FeedHtml implements Serializable {
        String html;

        public String getHtml() {
            CustomLog.e("html", "" + html);
            return html;
        }
    }

    @SerializedName("add_friend")
    private FeedHtml addFriend;
    @SerializedName("add_dateofbirth")
    private FeedHtml addDob;


    public String getWelcomeHtml() {
        return null != welcomeMsg ?/* SpanUtil.getHtmlString(welcomeMsg.getHtml())*/ welcomeMsg.getHtml() : null;
    }

    public String getUserBirthdayHtml() {
        return null != birthdayUser ?/* SpanUtil.getHtmlString(welcomeMsg.getHtml())*/ birthdayUser.getHtml() : null;
    }

    public String getFriendBirthdayHtml() {
        return null != viewerBirthday ?/* SpanUtil.getHtmlString(welcomeMsg.getHtml())*/ viewerBirthday.getHtml() : null;
    }

    public String getFriendHtml() {
        return null != addFriend ? /*SpanUtil.getHtmlString(addFriend.getHtml())*/ addFriend.getHtml() : null;
    }

    public String getDobHtml() {
        return null != addDob ? /*SpanUtil.getHtmlString(addDob.getHtml()) */addDob.getHtml() : null;
    }

}
