package com.sesolutions.utils;

import java.util.List;

/**
 * Created by root on 14/11/17.
 */

public class AppConfiguration {
    public static final boolean IS_FB_LOGIN_ENABLED = true;
    public static final boolean IS_GOOGLE_LOGIN_ENABLED = false;
    public static final boolean IS_TWITTER_LOGIN_ENABLED = false;
    public static final long SLIDE_TIME = 3000;
    public static final int AD_POS = 8;
    public static final boolean SHOW_SHIMMER_ANIMATION = true;
    public static final boolean IS_ADVANCE_QUOTE_ENABLED = false;
    public static final boolean IS_APP_TOUR_ENABLED = false;
    public static final boolean IS_STORY_ENABLED = false;
    public static final boolean IS_BLOG_TTS_EBANBLED = true;
    public static boolean isAdEnabled = false;

    public static boolean enableHeaderFixedFeed;
    public static boolean enableTabbarTitle = false;
    public static int descriptionTrucationLimitFeed = 201;
    public static String limitForTablet;
    public final static String UNSPLASH_ACCESS_CODE = "b9ef87e7220ca4217de490dafd7dcf6df3341725cf679ae6d03194a234273387";
    public static int limitForPhone;
    public static boolean enableLoggedinUserphoto;
    public static String siteTitle;
    public static boolean isNavigationTransparent;
    public static boolean memberImageShapeIsRound;
    public static int titleHeaderType = 2; //Default is 2 ie. SHOW_SEARCH_BAR

    public static String SHARE = "SocialEngine";
    public static boolean SHOW_TAB_ICONS = true;
    public static boolean SHOW_TAB_AT_TOP = false;
    public static boolean isSlideImagesAvailable = false;
    public static boolean hasWelcomeVideo = false;
    public static boolean isFeedGreetingsAvailable = false;
    public static boolean isBgOptionEnabled = true;
    public static boolean isDemoUserAvailable = false;
    public static boolean isFeedCentered = false;
    public static boolean canSelectVideoDevice = true;
    public static boolean truncateBody = true;
    public static boolean isWelcomeScreenEnabled = false;
    public static List<String> enabledPlugins;

    public static boolean isStoryEnabled = true;
    public static boolean isLiveStreamingEnabled = false;
    public static boolean isMulticurrencyEnabled = false;
    public static String LINUX_BASE_URL = "https://livevideo.socialnetworking.solutions/";
    public static String DEFAULT_CURRENCY = "";
    public static String AGORALIVESTRIMMINGID = "";
    public static int theme = 2 ;
    public static boolean isRevampTheme = true;
}
