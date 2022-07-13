package com.sesolutions.utils;

import android.content.Context;

public class ModuleUtil {



    /* Drawer item labels */
    public static final String ITEM_SEARCH = "core_main_search",
            ITEM_SIGN_OUT = "core_mini_auth",
            ITEM_NOTIFICATION = "core_mini_notifications",
            ITEM_FRIEND_REQUEST = "core_mini_friends",
            ITEM_MESSAGES = "core_mini_messages",
            ITEM_TERMS = "core_footer_terms",
            ITEM_COURSE = "core_main_courses",
            ITEM_CLASSROOM = "core_main_eclassroom",
            ITEM_PRIVACY = "core_footer_privacy",
            ITEM_CONTACT_US = "core_footer_contact",
            ITEM_SETTING = "core_main_settings",
            ITEM_MUSIC_PLAYLIST = "core_main_music_playlist",
            ITEM_MUSIC = "core_main_music",
            ITEM_MUSIC_SONG = "core_main_music_song",
            ITEM_VIDEO = "core_main_video",
            ITEM_VIDEO_CHANNEL = "core_main_video_chanel",
            ITEM_VIDEO_PLAYLIST = "core_main_video_playlist",
            ITEM_BLOG = "core_main_blog",
            ITEM_NEWS = "core_main_sesnews",
            ITEM_FORUM = "core_main_sesforum",
            ITEM_CFORUM = "core_main_forum",
            ITEM_STORE = "core_main_estore",
            ITEM_FUND = "core_main_sescrowdfunding",
            ITEM_CLASSIFIED = "core_main_classified",
            ITEM_ARTICLE = "core_main_sesarticle",
            ITEM_MEMBER = "core_main_members",
            ITEM_CORE_MEMBER = "core_main_member",
            ITEM_ALBUM = "core_main_album",
            ITEM_RATE_US = "core_main_sesapi_rate",
            ITEM_QUOTE = "core_main_sesquote",
            ITEM_WISH = "core_main_seswishe",
            ITEM_PRAYER = "core_main_sesprayer",
            ITEM_EVENT = "core_main_sesevent",
            ITEM_CORE_EVENT = "core_main_event",
            ITEM_PAGE = "core_main_sespage",
            ITEM_BUSINESS = "core_main_sesbusiness",
            ITEM_THOUGHT = "core_main_sesthought",
            ITEM_GROUP = "core_main_sesgroup",
            ITEM_CORE_GROUP = "core_main_group",
            ITEM_CORE_POLL = "core_main_poll",
            ITEM_RECIPE = "core_main_sesrecipe",
            ITEM_CONTEST = "core_main_contest",
            ITEM_QA = "core_main_sesqa",
            ITEM_BOOKING = "core_main_booking",
            ITEM_EGAMES = "core_main_egames",
            ITEM_ERESUME = "core_main_eresume",
            ITEM_EJOBPLUGIN= "core_main_job",
            ITEM_CREDIT = "core_main_sescredit";

    private ModuleUtil() {
    }

    // static variable single_instance of type Singleton
    private static ModuleUtil mClass = null;

    // private constructor restricted to this class itself

    // static method to create instance of Singleton class
    public static ModuleUtil getInstance() {
        if (mClass == null)
            mClass = new ModuleUtil();

        return mClass;
    }


    public int fetchDestination(String type) {
        try {
            if (Constant.ATTACHMENT_TYPE_MUSIC_ALBUM.equals(type) || Constant.ResourceType.CPLAYLIST.equals(type)) {
                return Constant.GoTo.VIEW_MUSIC_ALBUM;
            } else if (Constant.ACTIVITY_TYPE_ALBUM_SONG.equals(type)) {
                return Constant.GoTo.VIEW_SONG;
            } else if (Constant.ResourceType.VIEW_CORE_POLL.equals(type)) {
                return Constant.GoTo.VIEW_CPOLL;
            } else if (Constant.ResourceType.PROFESSIONAL.equals(type)) {
                return Constant.GoTo.VIEW_PROFESSIONAL;
            } else if (Constant.ResourceType.CLASSROOM.equals(type)) {
                return Constant.GoTo.VIEW_CLASSROOM;
            } else if (Constant.ResourceType.COURSE.equals(type)) {
                return Constant.GoTo.VIEW_COURSE;
            } else if (Constant.ResourceType.CLASSIFIED.equals(type)) {
                return Constant.GoTo.VIEW_CLASSIFIED;
            } else if (Constant.ResourceType.ARTICLE.equals(type)) {
                return Constant.GoTo.VIEW_ARTICLE;
            } else if (Constant.ResourceType.BLOG.equals(type)) {
                return Constant.GoTo.VIEW_BLOG;
            } else if (Constant.ResourceType.FORUM.equals(type)) {
                return Constant.GoTo.VIEW_FORUM;
            } else if (Constant.ResourceType.CFORUM.equals(type)) {
                return Constant.GoTo.VIEW_CFORUM;
            } else if (Constant.ResourceType.FORUM_TOPIC.equals(type)) {
                return Constant.GoTo.VIEW_FORUM_TOPIC;
            } else if (Constant.ResourceType.FORUM_CTOPIC2.equals(type)) {
                return Constant.GoTo.VIEW_CFORUM_TOPIC;
            } else if (Constant.ResourceType.FORUM_CTOPIC.equals(type)) {
                return Constant.GoTo.VIEW_CFORUM_TOPIC;
            } /*else if (Constant.ResourceType.FORUM_CATEGORY.equals(type)) {
                return Constant.GoTo.VIEW_FORUM_CATEGORY;
            }*/ else if (Constant.ResourceType.STORE.equals(type)) {
                return Constant.GoTo.VIEW_STORE;
            } else if (Constant.ResourceType.CORE_GROUP.equals(type)) {
                return Constant.GoTo.VIEW_CGROUP;
            } else if (Constant.ResourceType.PRODUCT.equals(type)) {
                return Constant.GoTo.VIEW_PRODUCT;
            } else if (Constant.ResourceType.PRODUCT_NEW.equals(type)) {
                return Constant.GoTo.VIEW_PRODUCT;
            } else if (Constant.ResourceType.PRODUCT_WISHLIST_CREATE.equals(type)) {
                return Constant.GoTo.VIEW_WISHLIST;
            } else if (Constant.ResourceType.STORE_COVER_PHOTO.equals(type)) {
                return Constant.GoTo.VIEW_ALBUM;
            } else if (Constant.ResourceType.NEWS.equals(type)) {
                return Constant.GoTo.VIEW_NEWS;
            } else if (Constant.ResourceType.FUND.equals(type)) {
                return Constant.GoTo.VIEW_FUND;
            } else if (Constant.ResourceType.QA.equals(type)) {
                return Constant.GoTo.VIEW_QA;
            } else if (Constant.ResourceType.PLAYLIST.equals(type)) {
                return Constant.GoTo.VIEW_MUSIC_PLAYLIST;
            } else if (Constant.ResourceType.ARTIST.equals(type)) {
                return Constant.GoTo.VIEW_MUSIC_ARTIST;
            } else if (Constant.ResourceType.QUOTE.equals(type)) {
                return Constant.GoTo.VIEW_QUOTE;
            } else if (Constant.ResourceType.WISH.equals(type)) {
                return Constant.GoTo.VIEW_WISH;
            } else if (Constant.ResourceType.GROUP.equals(type)) {
                return Constant.GoTo.VIEW_GROUP;
            } else if (Constant.ResourceType.GROUP_TOPIC.equals(type)) {
                return Constant.GoTo.VIEW_GROUP_TOPIC;
            } else if (Constant.ResourceType.BUSINESS.equals(type)) {
                return Constant.GoTo.VIEW_BUSINESS;
            } else if (Constant.ResourceType.PRAYER.equals(type)) {
                return Constant.GoTo.VIEW_PRAYER;
            } else if (Constant.ResourceType.CONTEST.equals(type)) {
                return Constant.GoTo.VIEW_CONTEST;
            } else if (Constant.ResourceType.ENTRY.equals(type)) {
                return Constant.GoTo.VIEW_ENTRY;
            } else if (Constant.ResourceType.PAGE.equals(type)) {
                return Constant.GoTo.VIEW_PAGE;
            } else if (Constant.ResourceType.RECIPE.equals(type)) {
                return Constant.GoTo.VIEW_RECIPE;
            } else if (Constant.ResourceType.SES_EVENT.equals(type)) {
                return Constant.GoTo.VIEW_EVENT;
            } else if (Constant.ResourceType.EVENT.equals(type)) {
                return Constant.GoTo.VIEW_CORE_EVENT;
            } else if (Constant.ResourceType.EVENT_TOPIC.equals(type)) {
                return Constant.GoTo.VIEW_EVENT_TOPIC;
            } else if (Constant.ResourceType.THOUGHT.equals(type)) {
                return Constant.GoTo.VIEW_THOUGHT;
            } else if (Constant.ResourceType.VIDEO_CHANNEL.equals(type)) {
                return Constant.GoTo.VIEW_CHANNEL;
            } else if (Constant.ResourceType.ALBUM.equals(type)) {
                return Constant.GoTo.VIEW_ALBUM;
            } else if (Constant.ResourceType.VIDEO_PLAYLIST.equals(type)) {
                return Constant.GoTo.VIEW_VIDEO_PLAYLIST;
            } else if (Constant.ResourceType.USER.equals(type)) {
                return Constant.GoTo.VIEW_PROFILE;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return -1;
    }

    public String fetchVoteUrl(String rcType) {
        switch (rcType) {
            case Constant.ResourceType.VIEW_CORE_POLL:
                return Constant.URL_POLL_VOTE;
            case Constant.ResourceType.PAGE_POLL:
                return Constant.URL_PAGE_POLL_VOTE;
            case Constant.ResourceType.GROUP_POLL:
                return Constant.URL_GROUP_POLL_VOTE;
            case Constant.ResourceType.BUSINESS_POLL:
                return Constant.URL_BUSINESS_POLL_VOTE;
        }
        return null;
    }

    private void loadPlugins(Context context) {
        if (null == AppConfiguration.enabledPlugins) {
            SPref.getInstance().loadEnabledPlugins(context);
        }
    }

    public boolean isCoreVideoEnabled(Context context) {
        loadPlugins(context);
        return AppConfiguration.enabledPlugins.contains("video");
    }

    public boolean isCoreAlbumEnabled(Context context) {
        loadPlugins(context);
        return AppConfiguration.enabledPlugins.contains("album");
    }

    public boolean isCorePlugin(Context context, String resourceType) {
        loadPlugins(context);
        return AppConfiguration.enabledPlugins.contains(resourceType);
    }
}
