package com.sesolutions.http;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

/**
 * Created by root on 2/1/18.
 */

public class ResourceToConstantTask extends AsyncTask<String, Void, Void> {
    private Context context;


    public ResourceToConstantTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        Resources res = context.getResources();
        String packageName = context.getPackageName();
        for (String s : resourceName) {
            try {
                String value = res.getString(res.getIdentifier(s, "string", packageName));
                Constant.class.getDeclaredField(s).set(String.class, value);
            } catch (Exception e) {
                CustomLog.e("ResourceToConstantTask", "" + e.getMessage());
            }
        }
        context = null;
        return null;
    }

    /*public String getJson(int documentId) {
        String result = Constant.EMPTY;
        try {
            result = (String) this.getClass().getDeclaredField("d" + documentId).get(this);
        } catch (Exception e) {
            PwcCustomLog.error(e);
        }
        return result;*/

    private String[] resourceName = {
            "MSG_NO_BLOG",
            "MSG_NO_ARTICLE",
            "MSG_NO_CATEGORIES",
            "MSG_NO_ALBUM_AVAILABLE",
            //"MSG_NO_SONG_SEARCH",
            "MSG_NO_ALBUM_MUSIC_SEARCH",
            "TXT_TITLE_LYRICS",
            "MSG_NO_LYRICS",
            "CANCEL",
            "MSG_INVALID_EMAIL",
            "MSG_INVALID_PASSWORD",
            "MSG_NO_INTERNET",
            // "TXT_SIGNING_IN",
            "TXT_SUCCESS",
            "TXT_SIGN_IN",
            "TXT_SAVE_PHOTO",
            "MSG_SELECT_IMAGE",
            "MSG_SELECT_IMAGE_SOURCE",
            "MSG_SELECT_ATTACH_SOURCE",
            "MSG_NO_PENDING_REQUEST",
            "MSG_NO_SENT_MSG",
            "MSG_NO_VIDEO",
            "MSG_NO_PHOTO",
            "MSG_NO_PHOTO_SELECTED",
            "TITLE_SUBSCRIPTION",
            "MSG_SUBSCRIPTION_FAILED",
            "MSG_SUBSCRIPTION_SUCCESS",
            "MSG_PERMISSION_DENIED",
            "TXT_OK",
            "TXT_CANCEL",
            "MSG_LOGOUT",
            "TXT_VIDEO",
            "TXT_VIMEO",
            "MSG_CHOOSE_SOURCE",
            "TXT_ADD",
            "MSG_ENTER_VIDEO_URL",
            "MSG_ENTER_LINK",
            "TXT_YOU_TUBE",
            "MSG_USER_CANCEL_FB_LOGIN",
            "MSG_NO_NOTIFICATION",
            "MSG_NO_FOLLOWER",
            "TAB_TITLE_NOTIFICATION",
            "TAB_TITLE_MESSAGE",
            "TAB_TITLE_REQUEST",
            "TAB_TITLE_HOME",
            "TITLE_UPDATE_STATUS",
            "PRIVACY_EVERYONE",
            "PRIVACY_NETWORK",
            "PRIVACY_FRIEND",
            "PRIVACY_ME",
            "MSG_SOLD_MARK",
            "MSG_SOLD_MESSAGE",
            "TITLE_FILE_PREVIEW",
            "TXT_DISABLE_COMMENT",
            "TXT_ENABLE_COMMENT",
            "TXT_SHARE_FEED",
            "TXT_SHARE_INSIDE",
            "TXT_SHARE_OUTSIDE",
            "TITLE_REPORT_SPAM",
            "NO",
            "YES",
            "MSG_DELETE_CONFIRMATION",
            "MSG_DELETE_CONFIRMATION_PHOTO",
            "MSG_DELETE_CONFIRMATION_VIDEO",
            "MSG_DELETE_CONFIRMATION_ARTICLE",
            "MSG_DELETE_CONFIRMATION_BLOG",
            "MSG_DELETE_CONFIRMATION_PRE",
            "MSG_PROFILE_IMAGE_DELETE_CONFIRMATION",
            "MSG_COVER_DELETE_CONFIRMATION",
            "MSG_ACCOUNT_DELETE_CONFIRMATION",
            "MSG_DELETE_COMMENT_CONFIRMATION",
            "DISCARD",
            "MSG_ABANODONED",
            "MSG_NO_ACTIVITIES",
            "MSG_NO_FEED_DATA",
            "MSG_NO_FEELINGS",
            "MSG_NO_USER",
            "MSG_NO_STICKERS",
            "MSG_NO_STICKERS_EMOJI",
            "TITLE_COMMENT",
            "TITLE_FOLLOWERS",
            "MSG_NO_COMMENT",
            "TXT_LIKE",
            "TXT_UNLIKE",
            "MSG_NAME_MISSING",
            "MSG_EMAIL_MISSING",
            "MSG_MESSAGE_MISSING",
            "MSG_EMAIL_INVALID",
            "TITLE_SETTINGS",
            "TITLE_GENERAL_SETTING",
            "TITLE_PRIVACY_SETTING",
            "TITLE_NOTIFICATION_SETTING",
            "TITLE_PASSWORD_SETTING",
            "TITLE_NETWORK_SETTING",
            "TAB_TITLE_MUSIC_ALBUMS_1",
            "TAB_TITLE_MUSIC_ALBUMS_2",
            "TAB_TITLE_MUSIC_ALBUMS_3",
            "TAB_TITLE_MUSIC_ALBUMS_4",
            "TAB_TITLE_MUSIC_ALBUMS_5",
            "TAB_TITLE_MUSIC_ALBUMS_6",
            "MSG_NO_SONG_CREATED",
            "MSG_NO_VIDEO_CREATED",
            "MSG_NO_SEARCH_ITEM",
            "TITLE_FILTER_SEARCH",
            "TITLE_EDIT_ALBUM",
            "TITLE_EDIT_PLAYLIST",
            "TITLE_ADD_CHANNEL",
            "TITLE_EDIT_CHANNEL",
            "TITLE_ADD_NEW_VIDEO",
            "TITLE_EDIT_SONG",
            "TXT_FAVORITE",
            "TXT_COMMENT",
            "TITLE_EDIT_COVER",
            "TITLE_EDIT_MUSIC_PHOTO",
            "TITLE_EDIT_PROFILE_PHOTO",
            "TITLE_ADD_SONG",
            "TITLE_ADD_ALBUM",
            "TAB_TITLE_VIDEO_1",
            "TAB_TITLE_VIDEO_2",
            "TAB_TITLE_VIDEO_3",
            "TAB_TITLE_VIDEO_4",
            "TAB_TITLE_VIDEO_5",
            "TAB_TITLE_VIDEO_6",
            "TAB_TITLE_VIDEO_7",
            "TITLE_EDIT_VIDEO",
            "TITLE_PHOTOS",
            "TITLE_UPLOAD_PHOTOS",
            "TITLE_UPLOAD_VIDEO",
            "TAB_TITLE_ARTICLE_1",
            "TAB_TITLE_ARTICLE_2",
            "TAB_TITLE_ARTICLE_3",
            "TAB_TITLE_ARTICLE_4",
            "TAB_TITLE_BLOG_1",
            "TAB_TITLE_BLOG_2",
            "TAB_TITLE_BLOG_3",
            "TAB_TITLE_BLOG_4",
            "TXT_SERACH_BLOG",
            "TXT_SERACH_ARTICLE",
            "TXT_SERACH_ALBUM",
            "TITLE_EDIT_BLOG",
            "TITLE_EDIT_ARTICLE",
            "TAB_TITLE_ALBUM_1",
            "TAB_TITLE_ALBUM_2",
            "TAB_TITLE_ALBUM_3",
            "TITLE_ALBUM_SEARCH",
            "TAB_TITLE_ALBUM_4",
            "TAB_TITLE_ALBUM_5",
            "TITLE_EDIT_ALBUM_SETTING",
            "TXT_SERACH_PHOTO",
            "TXT_SERACH_PLAYLIST",
            "TITLE_MEMBER",
            "DELETE_PHOTO",
            "TITLE_ACTIVITY_FEED",
            "TITLE_EDIT_PROFILE",
            "TITLE_VIDEO_SEARCH",
            "TITLE_CHANNEl_SEARCH",
            "TXT_SEARCH_MUSIC",
            "TITLE_FRIENDS",
            "MUTUAL_FRINEDS",
            "TITLE_INFO",
            "TITLE_SEARCH_MEMBER",
            "INBOX",
            "OUTBOX",
            "MSG_FACEBOOK_ERROR",
            "MSG_SHARE_VIA",
            "ADD_FRIEND",
            "CANCEL_FREIND",
            "MSG_EMPTY_POST",
            "TITLE_BLOG",
            "MSG_NOT_TAGGED",
            "MSG_NOT_LOGGED_IN",
            "TITLE_FEED",
            "MSG_PRESS_AGAIN",
            "TITLE_SEARCH",
            "TITLE_SELECT_ALBUM",
            "TITLE_SELECT_PHOTO",
            "TXT_UPLOADING",
            "TXT_UNSAVE_FEED",
            "TXT_SAVE_FEED",
            "TXT_REMOVE",
            "TXT_JOIN",
            "MSG_NO_NETWORK",
            "MSG_NOTHING_TO_JOIN",
            "MSG_NO_ALBUMS",
            "MSG_NO_VIDEO_BY_YOU",
            "MSG_NO_CHANNEL",
            "MSG_NO_PLAYLIST",
            "MSG_NO_SONG_PLAYLIST",
            "MSG_NO_MEMBER",
            "MSG_NO_SONG_ARTIST",
            "MSG_NO_SONG_ALBUM",
            "TXT_REMOVE_COVER",
            "TXT_REMOVE_PHOTO",
            "TXT_BY",
            "_AND_",
            "_OTHERS",
            "_WITH_",
            "_IN_",
            "_COMMENT",
            "MSG_NO_ARTICLE_CREATED",
            "MSG_NO_BLOG_CREATED",
            "TITLE_ADD_VIDEO",
            "TITLE_TERMS",
            "TITLE_PRIVACY",
            "_COMMENTS"
    };
}
