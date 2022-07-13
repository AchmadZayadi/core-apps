package com.sesolutions.utils;

import com.sesolutions.BuildConfig;

import java.util.Locale;

public class URL {
    public static String language = "id";
    public static final String BASE_URL = BuildConfig.BASE_URL;
    public static final String POST_URL = "?restApi=Sesapi&sesapi_version=" + BuildConfig.SES_API_VERSION + "&sesapi_platform=2&language=" + language + "&debug=1";
    public static final String POST_URL_GROUP = "&restApi=Sesapi&sesapi_version=" + BuildConfig.SES_API_VERSION + "&sesapi_platform=2&language=" + language + "&debug=1";
    public static final String URL_PARAMETER_LOGIN = "";
    public static final String URL_PARAMETER_REGISTER = "&ph=";


    //SE MUSIC APIs
    public static final String CMUSIC_BROWSE = BASE_URL + "music/browse" + POST_URL;
    public static final String CMUSIC_DELETE = BASE_URL + "music/delete" + POST_URL;
    public static final String CMUSIC_VIEW = BASE_URL + "music/playlist/view" + POST_URL;
    public static final String CMUSIC_SEARCH_FORM = BASE_URL + "music/search-form" + POST_URL;
    public static final String CMUSIC_EDIT = BASE_URL + "music/edit" + POST_URL;

    //SES-CREDIT APIs
    public static final String CREDIT_DEFAULT = BASE_URL + "sescredit/index/menus" + POST_URL;
    public static final String CREDIT_LEADERBOARD = BASE_URL + "sescredit/index/leaderboard" + POST_URL;
    public static final String CREDIT_BADGES = BASE_URL + "sescredit/index/badges" + POST_URL;
    public static final String CREDIT_MANAGE = BASE_URL + "sescredit/index/my-credit" + POST_URL;
    public static final String CREDIT_TERMS = BASE_URL + "sescredit/index/terms" + POST_URL;
    public static final String CREDIT_SEND = BASE_URL + "sescredit/index/send-point" + POST_URL;
    public static final String CREDIT_SEARCH = BASE_URL + "sescredit/index/browsesearch" + POST_URL;
    public static final String CREDIT_TRANSACTION = BASE_URL + "sescredit/index/my-transactions" + POST_URL;
    public static final String CREDIT_EARN = BASE_URL + "sescredit/index/earn-credit" + POST_URL;
    public static final String CREDIT_EARN_HOW = BASE_URL + "sescredit/index/how-earn-point" + POST_URL;
    public static final String CREDIT_PURCHASE = BASE_URL + "sescredit/index/purchase-points" + POST_URL;

    //STORY APIs
    public static final String URL_STORY_CREATE = BASE_URL + "sesstories/index/create" + POST_URL;
    public static final String URL_STORY_BROWSE = BASE_URL + "sesstories/index/allstories" + POST_URL;
    public static final String URL_STORY_VIEWERS = BASE_URL + "sesstories/index/getStoryViewers" + POST_URL;
    public static final String URL_STORY_DELETE = BASE_URL + "sesstories/index/delete" + POST_URL;
    public static final String URL_STORY_HIGHLIGHT = BASE_URL + "sesstories/index/highlight" + POST_URL;
    public static final String URL_STORY_SETTING = BASE_URL + "sesstories/index/storysettings" + POST_URL;
    public static final String URL_STORY_MUTE = BASE_URL + "sesstories/index/mute" + POST_URL;
    public static final String URL_STORY_UNMUTE = BASE_URL + "sesstories/index/unmute" + POST_URL;
    public static final String URL_STORY_VIEWED = BASE_URL + "sesstories/index/view" + POST_URL;
    public static final String URL_STORY_MUTED_MEMBERS = BASE_URL + "sesstories/index/getallmutedmembers" + POST_URL;

    //BLOG APIs
    public static final String URL_CREATE_BLOG = BASE_URL + "blogs/create" + POST_URL;
    public static final String URL_EDIT_BLOG_PHOTO = BASE_URL + "blogs/edit-photo" + POST_URL;
    public static final String URL_DELETE_BLOG = BASE_URL + "blogs/delete" + POST_URL;
    public static final String URL_EDIT_BLOG = BASE_URL + "blogs/edit" + POST_URL;
    public static final String URL_BLOG_BROWSE = BASE_URL + "blogs/browse" + POST_URL;
    public static final String URL_BLOG_VIEW = BASE_URL + "blog/" + POST_URL;
    public static final String URL_BLOG_FILTER_FORM = BASE_URL + "blogs/search-form" + POST_URL;
    public static final String URL_BLOG_CATEGORIES_BROWSE = BASE_URL + "blogs/category" + POST_URL;

    // JOBS APIS
    public static final String URL_JOB_BROWSE = BASE_URL + "sesjob/index/browse" + POST_URL;
    public static final String URL_JOB_CATEGORIES = BASE_URL + "sesjob/index/categories" + POST_URL;
    public static final String URL_JOB_BROWSECOMPANY = BASE_URL + "sesjob/index/browse-company" + POST_URL;
    public static final String URL_JOB_MYJOB = BASE_URL + "sesjob/index/my-jobs" + POST_URL;
    public static final String URL_CREATE_JOB = BASE_URL + "sesjob/index/create-job" + POST_URL;
    public static final String APPLY_JOB_CREATE = BASE_URL + "sesjob/index/apply-job" + POST_URL;
    public static final String URL_EDIT_JOB = BASE_URL + "sesjob/index/edit-job" + POST_URL;

    public static final String URL_DELETE_JOB = BASE_URL + "sesjob/index/job-delete" + POST_URL;



    public static final String URL_BLOG_SUBSCRIBE = BASE_URL + "blogs/add" + POST_URL;
    public static final String URL_BLOG_UNSUBSCRIBE = BASE_URL + "blogs/remove" + POST_URL;


    //CORE GROUP APIs
    public static final String URL_CGROUP_BROWSE = BASE_URL + "groups/browse" + POST_URL;
    public static final String URL_CGROUP_CATEGORY = BASE_URL + "groups/category" + POST_URL;
    public static final String URL_CGROUP_FILTER_FORM = BASE_URL + "groups/search-form" + POST_URL;
    public static final String URL_MY_CGROUP = "groups/browse/user_id/";
    public static final String URL_CREATE_CGROUP = BASE_URL + "groups/create" + POST_URL;
    public static final String URL_VIEW_CGROUP = BASE_URL + "group/profile/view" + POST_URL;
    public static final String URL_EDIT_CGROUP = "groups/edit/group_id/";
    public static final String URL_CGROUP_INFO = BASE_URL + "group/profile/info" + POST_URL;
    public static final String URL_CGROUP_MEMBER = BASE_URL + "group/profile/members" + POST_URL;
    public static final String URL_DELETE_CGROUP = "groups/delete";
    /*public static final String URL_CGROUP_JOIN = BASE_URL + "group/profile/join" + POST_URL;
    public static final String URL_CGROUP_LEAVE = BASE_URL + "group/profile/leave" + POST_URL;
    public static final String URL_CGROUP_REQUEST = BASE_URL + "group/profile/request" + POST_URL; */
    public static final String URL_CGROUP_JOIN = "group/profile/join/";
    public static final String URL_CGROUP_LEAVE = "group/profile/leave/";
    public static final String URL_CGROUP_REQUEST = "group/profile/request/";

    public static final String URL_CGROUP_PHOTO_UPLOAD = BASE_URL + "group/index/createalbum" + POST_URL;
    public static final String URL_CGROUP_REMOVE_MEMBER = BASE_URL + "group/profile/remove" + POST_URL;
    public static final String URL_CGROUP_CANCEL_MEMBER = BASE_URL + "group/profile/cancel" + POST_URL;
    public static final String URL_CGROUP_REJECT_MEMBER = BASE_URL + "group/profile/reject" + POST_URL;
    public static final String URL_CGROUP_APPROVE_MEMBER = BASE_URL + "group/profile/approve" + POST_URL;
    public static final String URL_CGROUP_ACCEPT_MEMBER = BASE_URL + "group/profile/accept" + POST_URL;

    public static final String URL_CGROUP_INVITE = BASE_URL + "group/profile/invite" + POST_URL;
    public static final String URL_CREATE_CORE_GROUP_DISCUSSION = BASE_URL + "group/profile/creatediscussion" + POST_URL;
    public static final String URL_EDIT_CORE_GROUP_POST = BASE_URL + "group/profile/editpost" + POST_URL;
    public static final String URL_DELETE_CORE_GROUP_POST = BASE_URL + "group/profile/deletepost" + POST_URL;
    public static final String URL_CORE_GROUP_TOPIC_WATCH = BASE_URL + "group/profile/watch" + POST_URL;
    public static final String URL_CORE_GROUP_REPLY_TOPIC = BASE_URL + "group/profile/commentonpost" + POST_URL;
    public static final String URL_CGROUP_ACCEPT_INVITE = BASE_URL + "group/profile/accept" + POST_URL;
    public static final String URL_CGROUP_PHOTO = BASE_URL + "group/profile/photos" + POST_URL;
    public static final String URL_CGROUP_LIGHTBOX = BASE_URL + "group/profile/lightbox" + POST_URL;
    public static final String URL_CGROUP_DISCUSSION = BASE_URL + "group/profile/discussions" + POST_URL;
    public static final String URL_CGROUP_DISCUSSION_VIEW = BASE_URL + "group/profile/discussionview" + POST_URL;


    //RECIPE APIs
    public static final String URL_EDIT_RECIPE_PHOTO = BASE_URL + "sesrecipe/index/edit-photo" + POST_URL;
    public static final String URL_DELETE_RECIPE = BASE_URL + "sesrecipe/index/delete" + POST_URL;
    public static final String URL_EDIT_RECIPE = BASE_URL + "sesrecipe/index/edit" + POST_URL;
    public static final String URL_RECIPE_BROWSE = BASE_URL + "sesrecipe/index/browse" + POST_URL;
    public static final String URL_RECIPE_VIEW = BASE_URL + "sesrecipe/index/view" + POST_URL;
    public static final String URL_RECIPE_FILTER_FORM = BASE_URL + "sesrecipe/index/search-form" + POST_URL;
    public static final String URL_RECIPE_CATEGORIES_BROWSE = BASE_URL + "sesrecipe/index/category" + POST_URL;
    public static final String URL_CREATE_RECIPE = BASE_URL + "sesrecipe/index/create" + POST_URL;

    //QUOTE APIs
    public static final String URL_BROWSE_QUOTE = BASE_URL + "sesquote/index/browse" + POST_URL;
    public static final String URL_QUOTE_FILTER_FORM = BASE_URL + "sesquote/index/search-form" + POST_URL;
    public static final String URL_CREATE_QUOTE = BASE_URL + "sesquote/index/create" + POST_URL;
    public static final String URL_VIEW_QUOTE = BASE_URL + "sesquote/index/view" + POST_URL;
    public static final String URL_EDIT_QUOTE = BASE_URL + "sesquote/index/edit" + POST_URL;
    public static final String URL_DELETE_QUOTE = BASE_URL + "sesquote/index/delete" + POST_URL;

    //WISH APIs
    public static final String URL_BROWSE_WISH = BASE_URL + "seswishe/index/browse" + POST_URL;
    public static final String URL_WISH_FILTER_FORM = BASE_URL + "seswishe/index/search-form" + POST_URL;
    public static final String URL_CREATE_WISH = BASE_URL + "seswishe/index/create" + POST_URL;
    public static final String URL_VIEW_WISH = BASE_URL + "seswishe/index/view" + POST_URL;
    public static final String URL_EDIT_WISH = BASE_URL + "seswishe/index/edit" + POST_URL;
    public static final String URL_DELETE_WISH = BASE_URL + "seswishe/index/delete" + POST_URL;

    //CROWD FUNDING
    public static final String URL_FUND_MENUS = BASE_URL + "sescrowdfunding/index/menus" + POST_URL;
    public static final String URL_BROWSE_FUND = BASE_URL + "sescrowdfunding/index/browse" + POST_URL;
    public static final String URL_FUND_SEARCH = BASE_URL + "sescrowdfunding/index/browsesearch" + POST_URL;
    public static final String URL_CATEGORIES_FUND = BASE_URL + "sescrowdfunding/index/categories" + POST_URL;
    public static final String URL_FUND_MANAGE = BASE_URL + "sescrowdfunding/index/manage" + POST_URL;
    public static final String URL_FUND_DONOR = BASE_URL + "sescrowdfunding/index/donor" + POST_URL;
    public static final String URL_MY_DON_FUND = BASE_URL + "sescrowdfunding/index/manage-donations" + POST_URL;
    public static final String URL_RECEIVED_DON_FUND = BASE_URL + "sescrowdfunding/index/manage-received-donations" + POST_URL;
    public static final String URL_FUND_VIEW = BASE_URL + "sescrowdfunding/index/view" + POST_URL;
    public static final String URL_FUND_OVERVIEW = BASE_URL + "sescrowdfunding/index/overview" + POST_URL;
    public static final String URL_FUND_OVERVIEW_EDIT = BASE_URL + "sescrowdfunding/index/update-overview" + POST_URL;
    public static final String URL_FUND_REWARD = BASE_URL + "sescrowdfunding/index/reward" + POST_URL;
    public static final String URL_FUND_DESCRIPTION = BASE_URL + "sescrowdfunding/index/description" + POST_URL;
    public static final String URL_FUND_RATE = BASE_URL + "sescrowdfunding/index/rate" + POST_URL;
    public static final String URL_FUND_UPLOAD_PHOTO = BASE_URL + "sescrowdfunding/index/upload" + POST_URL;
    public static final String URL_FUND_LIKE = BASE_URL + "sescrowdfunding/index/like" + POST_URL;
    public static final String URL_FUND_ANNOUNCEMENT = BASE_URL + "sescrowdfunding/index/announcement" + POST_URL;
    public static final String URL_FUND_ANNOUNCEMENT_EDIT = BASE_URL + "sescrowdfunding/index/edit-announcement" + POST_URL;
    public static final String URL_FUND_ANNOUNCEMENT_DELETE = BASE_URL + "sescrowdfunding/index/delete-announcement" + POST_URL;
    public static final String URL_FUND_ANNOUNCEMENT_POST = BASE_URL + "sescrowdfunding/index/post-announcement" + POST_URL;
    public static final String URL_FUND_CREATE = BASE_URL + "sescrowdfunding/index/create" + POST_URL;
    public static final String URL_FUND_EDIT = BASE_URL + "sescrowdfunding/index/edit" + POST_URL;
    public static final String URL_FUND_DELETE = BASE_URL + "sescrowdfunding/index/delete" + POST_URL;
    public static final String URL_FUND_ABOUT = BASE_URL + "sescrowdfunding/index/aboutme" + POST_URL;
    public static final String URL_FUND_SEO = BASE_URL + "sescrowdfunding/index/seo" + POST_URL;
    public static final String URL_FUND_CONTACT = BASE_URL + "sescrowdfunding/index/contact-information" + POST_URL;
    public static final String URL_FUND_DONATE_FORM = BASE_URL + "sescrowdfunding/index/donate-form" + POST_URL;


    //PAGE APIs
    public static final String URL_BROWSE_PAGE = BASE_URL + "sespage/index/browse" + POST_URL;
    public static final String URL_BROWSE_HOT = BASE_URL + "sespage/index/hot" + POST_URL;
    public static final String URL_BROWSE_FEATURED = BASE_URL + "sespage/index/featured" + POST_URL;
    public static final String URL_BROWSE_SPONSERED = BASE_URL + "sespage/index/sponsored" + POST_URL;
    public static final String URL_BROWSE_VERIFIED = BASE_URL + "sespage/index/verified" + POST_URL;
    public static final String URL_PAGE_CONTACT = BASE_URL + "sespage/index/contact" + POST_URL;
    public static final String URL_BROWSE_CATEGORIES = BASE_URL + "sespage/index/categories" + POST_URL;
    public static final String URL_PAGE_LIKE = BASE_URL + "sespage/index/like" + POST_URL;
    public static final String URL_PAGE_FOLLOW = BASE_URL + "sespage/index/follow" + POST_URL;
    public static final String URL_PAGE_FAVORITE = BASE_URL + "sespage/index/favourite" + POST_URL;
    public static final String URL_PAGE_DELETE = BASE_URL + "sespage/index/delete" + POST_URL;
    public static final String URL_MANAGE_PAGE = BASE_URL + "sespage/index/manage" + POST_URL;
    public static final String URL_VIEW_PAGE = BASE_URL + "sespage/index/view" + POST_URL;
    public static final String URL_PAGE_MEMBER = BASE_URL + "sespage/index/member" + POST_URL;
    public static final String URL_PAGE_INFO_MEMBER = BASE_URL + "sespage/index/more-members" + POST_URL;
    public static final String URL_PAGE_ALBUM = BASE_URL + "sespage/index/album" + POST_URL;
    public static final String URL_PAGE_MAP = BASE_URL + "sespage/index/map" + POST_URL;
    public static final String URL_PAGE_INFO = BASE_URL + "sespage/index/info" + POST_URL;
    public static final String URL_PAGE_ANNOUNCE = BASE_URL + "sespage/index/announcement" + POST_URL;
    public static final String URL_PAGE_CREATE = BASE_URL + "sespage/profile/create" + POST_URL;
    public static final String URL_PAGE_EDIT = BASE_URL + "sespage/profile/edit" + POST_URL;
    public static final String URL_PAGE_INVITE = BASE_URL + "sespage/index/invite" + POST_URL;
    public static final String URL_PAGE_SERVICES = BASE_URL + "sespage/index/services" + POST_URL;
    public static final String URL_PAGE_ASSOCIATED = BASE_URL + "sespage/index/associated" + POST_URL;
    public static final String URL_DELETE_PAGE = BASE_URL + "sespage/index/delete" + POST_URL;
    public static final String URL_PAGE_JOIN = BASE_URL + "sespage/index/join" + POST_URL;
    public static final String URL_PAGE_LEAVE = BASE_URL + "sespage/index/leave" + POST_URL;
    public static final String URL_SEARCH_PAGE = BASE_URL + "sespage/index/browsesearch" + POST_URL;
    public static final String URL_SEARCH_PAGE_POLL = BASE_URL + "sespage/poll/search" + POST_URL;
    public static final String URL_SEARCH_GROP_POLL = BASE_URL + "sesgroup/poll/search" + POST_URL;
    public static final String URL_UPLOAD_PAGE_COVER = BASE_URL + "sespage/index/uploadcover" + POST_URL;
    public static final String URL_PAGE_ALBUM_VIEW = BASE_URL + "sespage/index/albumview" + POST_URL;
    public static final String URL_PAGE_BROWSE_ALBUM = BASE_URL + "sespage/index/browsealbum" + POST_URL;
    public static final String URL_PAGE_VIDEO_VIEW = BASE_URL + "sespage/index/view-video" + POST_URL;
    public static final String URL_PAGE_ALBUM_EDIT = BASE_URL + "sespage/index/editalbum" + POST_URL;
    public static final String URL_PAGE_ALBUM_CREATE = BASE_URL + "sespage/index/createalbum" + POST_URL;
    public static final String URL_PAGE_ALBUM_DELETE = BASE_URL + "sespage/index/deletealbum" + POST_URL;
    public static final String URL_PAGE_DEFAULT = BASE_URL + "sespage/index/menu" + POST_URL;
    public static final String URL_LIKE_AS_PAGE = BASE_URL + "sespage/index/likeaspage" + POST_URL;
    public static final String URL_UNLIKE_AS_PAGE = BASE_URL + "sespage/index/unlikeaspage" + POST_URL;
    public static final String URL_PAGE_SEARCH_FILTER = BASE_URL + "sespage/index/albumsearchform" + POST_URL;
    public static final String URL_PAGE_CLAIM = BASE_URL + "sespage/index/claim" + POST_URL;
    public static final String URL_REMOVE_PAGE_COVER = BASE_URL + "sespage/index/removecover" + POST_URL;
    public static final String URL_UPLOAD_PAGE_PHOTO = BASE_URL + "sespage/index/uploadphoto" + POST_URL;
    public static final String URL_REMOVE_PAGE_PHOTO = BASE_URL + "sespage/index/removephoto" + POST_URL;
    public static final String URL_PAGE_LIGHTBOX = BASE_URL + "sespage/index/lightbox" + POST_URL;
    public static final String URL_PAGE_ADD_MORE_PHOTOS = BASE_URL + "sespage/index/addmorephotos" + POST_URL;
    public static final String URL_CLASSROOM_ADD_MORE_PHOTOS = BASE_URL + "courses/classroom/addmorephotos" + POST_URL;
    public static final String URL_PAGE_VIDEO_BROWSE = BASE_URL + "sespage/index/browsevideo" + POST_URL;
    public static final String URL_PAGE_VIDEO_CREATE = BASE_URL + "sespage/index/create-video" + POST_URL;
    public static final String URL_PAGE_VIDEO_EDIT = BASE_URL + "sespage/index/edit-video" + POST_URL;
    public static final String URL_PAGE_VIDEO_PROFILE = BASE_URL + "sespage/index/profile-videos" + POST_URL;
    public static final String URL_PAGE_VIDEO_DELETE = BASE_URL + "sespage/index/delete-video" + POST_URL;
    public static final String URL_PAGE_VIDEO_WATCH_LATER = BASE_URL + "sespage/index/add" + POST_URL;
    public static final String URL_PAGE_VIDEO_RATE = BASE_URL + "sespage/index/rate" + POST_URL;
    public static final String URL_PAGE_VIDEO_FAVOURITE = BASE_URL + "sespage/index/favourite-video" + POST_URL;
    public static final String URL_PAGE_VIDEO_LIKE = BASE_URL + "sespage/index/like-video" + POST_URL;
    public static final String URL_PAGE_REMOVE_MEMBER = BASE_URL + "sespage/index/remove" + POST_URL;
    public static final String URL_PAGE_CANCEL_MEMBER = BASE_URL + "sespage/index/cancel" + POST_URL;
    public static final String URL_PAGE_REJECT_MEMBER = BASE_URL + "sespage/index/reject" + POST_URL;
    public static final String URL_PAGE_APPROVE_MEMBER = BASE_URL + "sespage/index/approve" + POST_URL;
    public static final String URL_PAGE_ACCEPT_MEMBER = BASE_URL + "sespage/index/accept" + POST_URL;

    public static final String URL_PAGE_PACKAGE = BASE_URL + "sespage/profile/package" + POST_URL;
    public static final String URL_PAGE_TRANSACTION = BASE_URL + "sespage/profile/transactions" + POST_URL;
    public static final String URL_PAGE_DELETE_PACKAGE = BASE_URL + "sespage/profile/cancel" + POST_URL;
    public static final String URL_PAGE_VIDEO_PLAY_URL = BASE_URL + "sespage/index/geturl/video_id/";

    //STORE APIs
    public static final String STORE_MENU = BASE_URL + "estore/index/menu" + POST_URL;
    public static final String URL_STORE_CONTACT = BASE_URL + "estore/index/contact" + POST_URL;
    public static final String STORE_BROWSE = BASE_URL + "estore/index/browse" + POST_URL;
    public static final String PRODUCT_BROWSE = BASE_URL + "estore/product/browse" + POST_URL;
    public static final String STORE_HOT = BASE_URL + "estore/index/hot" + POST_URL;
    public static final String STORE_FEATURED = BASE_URL + "estore/index/featured" + POST_URL;
    public static final String STORE_SPONSORED = BASE_URL + "estore/index/sponsored" + POST_URL;
    public static final String STORE_VERIFIED = BASE_URL + "estore/index/verified" + POST_URL;
    public static final String PAGE_CONTACT = BASE_URL + "estore/index/contact" + POST_URL;
    public static final String STORE_CATEGORIES = BASE_URL + "estore/index/categories" + POST_URL;
    public static final String PRODUCT_CATEGORIES = BASE_URL + "estore/product/productcategories" + POST_URL;
    public static final String STORE_SEARCH_FILTER = BASE_URL + "estore/index/browsesearch" + POST_URL;
    public static final String URL_DELETE_STORE = BASE_URL + "estore/index/delete" + POST_URL;
    public static final String URL_STORE_LIKE = BASE_URL + "estore/index/like" + POST_URL;
    public static final String URL_STORE_FOLLOW = BASE_URL + "estore/index/follow" + POST_URL;
    public static final String URL_STORE_INFO = BASE_URL + "estore/index/info" + POST_URL;
    public static final String URL_STORE_ALBUM = BASE_URL + "estore/index/album" + POST_URL;
    public static final String URL_STORE_MAP = BASE_URL + "estore/index/map" + POST_URL;
    public static final String URL_STORE_SERVICES = BASE_URL + "estore/index/services" + POST_URL;
    public static final String URL_STORE_ANNOUNCE = BASE_URL + "estore/index/announcement" + POST_URL;
    public static final String URL_STORE_MEMBER = BASE_URL + "estore/index/member" + POST_URL;
    public static final String URL_STORE_CLAIM = BASE_URL + "estore/index/claim" + POST_URL;
    public static final String URL_STORE_FAVORITE = BASE_URL + "estore/index/favourite" + POST_URL;
    public static final String URL_STORE_INVITE = BASE_URL + "estore/index/invite" + POST_URL;
    public static final String URL_STORE_JOIN = BASE_URL + "estore/index/join" + POST_URL;
    public static final String URL_STORE_REQUEST = BASE_URL + "estore/index/request" + POST_URL;
    public static final String URL_STORE_LEAVE = BASE_URL + "estore/index/leave" + POST_URL;
    public static final String URL_STORE_CANCEL_MEMBER = BASE_URL + "estore/index/cancel" + POST_URL;
    public static final String URL_STORE_EDIT = BASE_URL + "estore/profile/edit" + POST_URL;
    public static final String URL_LIKE_AS_STORE = BASE_URL + "sesbusiness/index/likeasbusiness" + POST_URL;
    public static final String URL_UNLIKE_AS_STORE = BASE_URL + "sesbusiness/index/unlikeasbusiness" + POST_URL;

    public static final String STORE_DELETE = BASE_URL + "estore/index/delete" + POST_URL;
    public static final String URL_UPLOAD_STORE_COVER = BASE_URL + "estore/index/uploadcover" + POST_URL;
    public static final String URL_REMOVE_STORE_COVER = BASE_URL + "estore/index/removecover" + POST_URL;
    public static final String URL_UPLOAD_STORE_PHOTO = BASE_URL + "estore/index/uploadphoto" + POST_URL;
    public static final String URL_REMOVE_STORE_PHOTO = BASE_URL + "estore/index/removephoto" + POST_URL;
    public static final String MY_STORE = BASE_URL + "estore/index/manage" + POST_URL;
    public static final String URL_VIEW_STORE = BASE_URL + "estore/index/view" + POST_URL;
    public static final String URL_VIEW_PRODUCT = BASE_URL + "estore/product/view" + POST_URL;
    public static final String STORE_ALBUM = BASE_URL + "estore/index/view" + POST_URL;
    public static final String URL_STORE_CREATE = BASE_URL + "estore/profile/create" + POST_URL;
    public static final String URL_SEARCH_STORE = BASE_URL + "estore/index/browsesearch" + POST_URL;
    public static final String URL_STORE_REMOVE_MEMBER = BASE_URL + "estore/index/remove" + POST_URL;
    public static final String URL_STORE_REJECT_MEMBER = BASE_URL + "estore/index/reject" + POST_URL;
    public static final String URL_STORE_APPROVE_MEMBER = BASE_URL + "estore/index/approve" + POST_URL;
    public static final String URL_STORE_ACCEPT_MEMBER = BASE_URL + "estore/index/accept" + POST_URL;
    public static final String URL_STORE_PROFILE_PRODUCT = BASE_URL + "estore/index/profileproducts" + POST_URL;

    public static final String URL_BILLING_ADDRESS = BASE_URL + "estore/index/billing" + POST_URL;
    public static final String URL_SHIPING_ADDRESS = BASE_URL + "estore/index/shipping" + POST_URL;
    public static final String URL_MY_ORDERS = BASE_URL + "estore/index/myorder" + POST_URL;
    public static final String URL_VIEW_ORDERS = BASE_URL + "estore/index/vieworder" + POST_URL;
    public static final String URL_DELETE_ORDER = BASE_URL + "estore/index/deleteorder" + POST_URL;
    public static final String URL_CHECKOUT = BASE_URL + "estore/index/checkout" + POST_URL;
    public static final String URL_MY_CART = BASE_URL + "estore/index/mycart" + POST_URL;
    public static final String URL_EMPTY_CART = BASE_URL + "estore/index/deletecart" + POST_URL;

    public static final String URL_PRODUCT_VIDEO_EDIT = BASE_URL + "estore/index/edit-video" + POST_URL;
    public static final String URL_PRODUCT_VIDEO_PROFILE = BASE_URL + "estore/product/profile-videos" + POST_URL;
    public static final String URL_PRODUCT_VIDEO_DELETE = BASE_URL + "estore/index/delete-video" + POST_URL;
    public static final String URL_PRODUCT_VIDEO_WATCH_LATER = BASE_URL + "estore/index/add" + POST_URL;
    public static final String URL_PRODUCT_VIDEO_RATE = BASE_URL + "estore/index/rate" + POST_URL;
    public static final String URL_PRODUCT_VIDEO_BROWSE = BASE_URL + "estore/product/browsevideo" + POST_URL;
    public static final String URL_PRODUCT_VIDEO_CREATE = BASE_URL + "estore/index/create-video" + POST_URL;

    public static final String URL_PRODUCT_ALBUM_VIEW = BASE_URL + "estore/index/albumview" + POST_URL;
    public static final String URL_PRODUCT_BROWSE_ALBUM = BASE_URL + "estore/index/browsealbum" + POST_URL;
    public static final String URL_PRODUCT_VIDEO_VIEW = BASE_URL + "estore/index/view-video" + POST_URL;
    public static final String URL_PRODUCT_ALBUM_EDIT = BASE_URL + "estore/index/editalbum" + POST_URL;
    public static final String URL_PRODUCT_ALBUM_CREATE = BASE_URL + "estore/index/createalbum" + POST_URL;
    public static final String URL_PRODUCT_ALBUM_DELETE = BASE_URL + "estore/index/deletealbum" + POST_URL;
    public static final String URL_PRODUCT_ADD_MORE_PHOTOS = BASE_URL + "estore/index/addmorephotos" + POST_URL;

    public static final String URL_PRODUCT_MEMBER = BASE_URL + "sespage/index/member" + POST_URL;
    public static final String URL_PRODUCT_INFO_MEMBER = BASE_URL + "sespage/index/more-members" + POST_URL;
    public static final String URL_PRODUCT_CANCEL_MEMBER = BASE_URL + "sespage/index/more-members" + POST_URL;
    public static final String URL_PRODUCT_ALBUM = BASE_URL + "estore/product/album" + POST_URL;
    public static final String URL_PRODUCT_MAP = BASE_URL + "estore/product/map" + POST_URL;
    public static final String URL_PRODUCT_INFO = BASE_URL + "estore/product/info" + POST_URL;
    public static final String URL_PRODUCT_ANNOUNCE = BASE_URL + "sespage/index/announcement" + POST_URL;
    public static final String URL_PRODUCT_CREATE = BASE_URL + "sespage/profile/create" + POST_URL;
    public static final String URL_PRODUCT_EDIT = BASE_URL + "sespage/profile/edit" + POST_URL;
    public static final String URL_PRODUCT_INVITE = BASE_URL + "sespage/index/invite" + POST_URL;
    public static final String URL_PRODUCT_SERVICES = BASE_URL + "sespage/index/services" + POST_URL;
    public static final String URL_PRODUCT_ASSOCIATED = BASE_URL + "sespage/index/associated" + POST_URL;
    public static final String URL_DELETE_PRODUCT = BASE_URL + "sespage/index/delete" + POST_URL;
    public static final String URL_PRODUCT_JOIN = BASE_URL + "sespage/index/join" + POST_URL;
    public static final String URL_PRODUCT_LEAVE = BASE_URL + "sespage/index/leave" + POST_URL;
    public static final String URL_SEARCH_PRODUCT = BASE_URL + "estore/product/browsesearch" + POST_URL;
    public static final String URL_UPLOAD_PRODUCT_COVER = BASE_URL + "sespage/index/uploadcover" + POST_URL;
    public static final String URL_PRODUCT_UPSELL = BASE_URL + "estore/product/profile-upsell" + POST_URL;

    public static final String URL_PRODUCT_LIKE = BASE_URL + "estore/product/like" + POST_URL;
    public static final String URL_PRODUCT_FOLLOW = BASE_URL + "estore/product/follow" + POST_URL;
    public static final String URL_PRODUCT_FAVORITE = BASE_URL + "estore/product/favourite" + POST_URL;
    public static final String URL_PRODUCT_DELETE = BASE_URL + "estore/product/delete" + POST_URL;
    //  public static final String URL_MANAGE_PRODUCT = BASE_URL + "estore/product/manage" + POST_URL;

    public static final String URL_BROWSE_WISHLIST = BASE_URL + "estore/product/browse-wishlist" + POST_URL;
    public static final String URL_MY_WISHLIST = BASE_URL + "/estore/index/my-wishlists" + POST_URL;
    public static final String URL_VIEW_WISHLIST = BASE_URL + "estore/product/view-wishlist" + POST_URL;
    public static final String URL_ADD_WISHLIST = BASE_URL + "estore/product/add-wishlist" + POST_URL;
    public static final String URL_DELETE_WISHLIST = BASE_URL + "estore/index/deletewishlist" + POST_URL;
    public static final String URL_EDIT_WISHLIST = BASE_URL + "estore/index/editwishlist" + POST_URL;

    public static final String URL_ADD_TO_CART = BASE_URL + "estore/product/addtocart" + POST_URL;

    //BUSINESS APIs
    public static final String URL_BROWSE_BUSINESS = BASE_URL + "sesbusiness/index/browse" + POST_URL;
    public static final String URL_BUSINESS_CONTACT = BASE_URL + "sesbusiness/index/contact" + POST_URL;
    public static final String URL_BUSINESS_CATEGORIES = BASE_URL + "sesbusiness/index/categories" + POST_URL;
    public static final String URL_BUSINESS_LIKE = BASE_URL + "sesbusiness/index/like" + POST_URL;
    public static final String URL_BUSINESS_FOLLOW = BASE_URL + "sesbusiness/index/follow" + POST_URL;
    public static final String URL_BUSINESS_FAVORITE = BASE_URL + "sesbusiness/index/favourite" + POST_URL;
    public static final String URL_BUSINESS_DELETE = BASE_URL + "sesbusiness/index/delete" + POST_URL;
    public static final String URL_MANAGE_BUSINESS = BASE_URL + "sesbusiness/index/manage" + POST_URL;
    public static final String URL_VIEW_BUSINESS = BASE_URL + "sesbusiness/index/view" + POST_URL;
    public static final String URL_BUSINESS_MEMBER = BASE_URL + "sesbusiness/index/member" + POST_URL;
    public static final String URL_BUSINESS_INFO_MEMBER = BASE_URL + "sesbusiness/index/more-members" + POST_URL;
    public static final String URL_BUSINESS_ALBUM = BASE_URL + "sesbusiness/index/album" + POST_URL;
    public static final String URL_BUSINESS_MAP = BASE_URL + "sesbusiness/index/map" + POST_URL;
    public static final String URL_BUSINESS_INFO = BASE_URL + "sesbusiness/index/info" + POST_URL;
    public static final String URL_BUSINESS_ANNOUNCE = BASE_URL + "sesbusiness/index/announcement" + POST_URL;
    public static final String URL_BUSINESS_CREATE = BASE_URL + "sesbusiness/profile/create" + POST_URL;
    public static final String URL_BUSINESS_EDIT = BASE_URL + "sesbusiness/profile/edit" + POST_URL;
    public static final String URL_BUSINESS_INVITE = BASE_URL + "sesbusiness/index/invite" + POST_URL;
    public static final String URL_BUSINESS_SERVICES = BASE_URL + "sesbusiness/index/services" + POST_URL;
    public static final String URL_BUSINESS_ASSOCIATED = BASE_URL + "sesbusiness/index/associated" + POST_URL;
    public static final String URL_DELETE_BUSINESS = BASE_URL + "sesbusiness/index/delete" + POST_URL;
    public static final String URL_BUSINESS_JOIN = BASE_URL + "sesbusiness/index/join" + POST_URL;
    public static final String URL_BUSINESS_LEAVE = BASE_URL + "sesbusiness/index/leave" + POST_URL;
    public static final String URL_SEARCH_BUSINESS = BASE_URL + "sesbusiness/index/browsesearch" + POST_URL;
    public static final String URL_UPLOAD_BUSINESS_COVER = BASE_URL + "sesbusiness/index/uploadcover" + POST_URL;
    public static final String URL_BUSINESS_ALBUM_VIEW = BASE_URL + "sesbusiness/index/albumview" + POST_URL;
    public static final String URL_BUSINESS_BROWSE_ALBUM = BASE_URL + "sesbusiness/index/browsealbum" + POST_URL;
    public static final String URL_BUSINESS_VIDEO_VIEW = BASE_URL + "sesbusiness/index/view-video" + POST_URL;
    public static final String URL_BUSINESS_ALBUM_EDIT = BASE_URL + "sesbusiness/index/editalbum" + POST_URL;
    public static final String URL_BUSINESS_ALBUM_CREATE = BASE_URL + "sesbusiness/index/createalbum" + POST_URL;
    public static final String URL_BUSINESS_ALBUM_DELETE = BASE_URL + "sesbusiness/index/deletealbum" + POST_URL;
    public static final String URL_BUSINESS_DEFAULT = BASE_URL + "sesbusiness/index/menu" + POST_URL;
    public static final String URL_LIKE_AS_BUSINESS = BASE_URL + "sesbusiness/index/likeasbusiness" + POST_URL;
    public static final String URL_UNLIKE_AS_BUSINESS = BASE_URL + "sesbusiness/index/unlikeasbusiness" + POST_URL;
    public static final String URL_BUSINESS_SEARCH_FILTER = BASE_URL + "sesbusiness/index/albumsearchform" + POST_URL;
    public static final String URL_BUSINESS_CLAIM = BASE_URL + "sesbusiness/index/claim" + POST_URL;
    public static final String URL_REMOVE_BUSINESS_COVER = BASE_URL + "sesbusiness/index/removecover" + POST_URL;
    public static final String URL_UPLOAD_BUSINESS_PHOTO = BASE_URL + "sesbusiness/index/uploadphoto" + POST_URL;
    public static final String URL_REMOVE_BUSINESS_PHOTO = BASE_URL + "sesbusiness/index/removephoto" + POST_URL;
    public static final String URL_BUSINESS_LIGHTBOX = BASE_URL + "sesbusiness/index/lightbox" + POST_URL;
    public static final String URL_BUSINESS_ADD_MORE_PHOTOS = BASE_URL + "sesbusiness/index/addmorephotos" + POST_URL;
    public static final String URL_BUSINESS_VIDEO_BROWSE = BASE_URL + "sesbusiness/index/browsevideo" + POST_URL;
    public static final String URL_BUSINESS_VIDEO_CREATE = BASE_URL + "sesbusiness/index/create-video" + POST_URL;
    public static final String URL_BUSINESS_VIDEO_EDIT = BASE_URL + "sesbusiness/index/edit-video" + POST_URL;
    public static final String URL_BUSINESS_VIDEO_PROFILE = BASE_URL + "sesbusiness/index/profile-videos" + POST_URL;
    public static final String URL_BUSINESS_VIDEO_DELETE = BASE_URL + "sesbusiness/index/delete-video" + POST_URL;
    public static final String URL_BUSINESS_VIDEO_WATCH_LATER = BASE_URL + "sesbusiness/index/add" + POST_URL;
    public static final String URL_BUSINESS_VIDEO_RATE = BASE_URL + "sesbusiness/index/rate" + POST_URL;
    public static final String URL_BUSINESS_VIDEO_FAVOURITE = BASE_URL + "sesbusiness/index/favourite-video" + POST_URL;
    public static final String URL_BUSINESS_VIDEO_LIKE = BASE_URL + "sesbusiness/index/like-video" + POST_URL;
    public static final String URL_BUSINESS_REMOVE_MEMBER = BASE_URL + "sesbusiness/index/remove" + POST_URL;
    public static final String URL_BUSINESS_CANCEL_MEMBER = BASE_URL + "sesbusiness/index/cancel" + POST_URL;
    public static final String URL_BUSINESS_REJECT_MEMBER = BASE_URL + "sesbusiness/index/reject" + POST_URL;
    public static final String URL_BUSINESS_APPROVE_MEMBER = BASE_URL + "sesbusiness/index/approve" + POST_URL;
    public static final String URL_BUSINESS_ACCEPT_MEMBER = BASE_URL + "sesbusiness/index/accept" + POST_URL;

    public static final String URL_BUSINESS_PACKAGE = BASE_URL + "sesbusiness/profile/package" + POST_URL;
    public static final String URL_BUSINESS_TRANSACTION = BASE_URL + "sesbusiness/profile/transactions" + POST_URL;
    public static final String URL_BUSINESS_DELETE_PACKAGE = BASE_URL + "sesbusiness/profile/cancel" + POST_URL;
    public static final String URL_BUSINESS_VIDEO_PLAY_URL = BASE_URL + "sesbusiness/index/geturl/video_id/";

    //FORUM APIs
    public static final String URL_FORUM_HOME_PAGE = BASE_URL + "sesforum/index/index" + POST_URL;
    public static final String URL_FORUM_VIEW_PAGE = BASE_URL + "sesforum/index/forumview" + POST_URL;
    public static final String URL_TOPIC_VIEW_PAGE = BASE_URL + "sesforum/index/topicviewpage" + POST_URL;
    public static final String URL_CREATE_TOPIC = BASE_URL + "sesforum/index/topiccreate" + POST_URL;
    public static final String URL_FORUM_CATEGORY_VIEW = BASE_URL + "sesforum/index/categoryview" + POST_URL;
    public static final String URL_FORUM_SUBCATEGORY_VIEW = BASE_URL + "sesforum/index/subcategoryview" + POST_URL;
    public static final String URL_FORUM_SUBSUBCATEGORY_VIEW = BASE_URL + "sesforum/index/subsubcategoryview" + POST_URL;
    public static final String URL_SAY_THANK = BASE_URL + "sesforum/index/thank" + POST_URL;
    public static final String URL_TOPIC_POST_LIKE = BASE_URL + "sesforum/index/like" + POST_URL;
    public static final String URL_TOPIC_POST_DELETE = BASE_URL + "sesforum/index/deletepost" + POST_URL;
    public static final String URL_TOPIC_POST_EDIT = BASE_URL + "sesforum/index/editpost" + POST_URL;
    public static final String URL_TOPIC_POST_QUOTE = BASE_URL + "sesforum/index/postcreate" + POST_URL;
    public static final String URL_TOPIC_RENAME = BASE_URL + "sesforum/index/rename" + POST_URL;
    public static final String URL_TOPIC_DELETE = BASE_URL + "sesforum/index/deletetopic" + POST_URL;
    public static final String URL_TOPIC_MOVE = BASE_URL + "sesforum/index/move" + POST_URL;
    public static final String URL_TOPIC_CLOSE = BASE_URL + "sesforum/index/close" + POST_URL;
    public static final String URL_TOPIC_STICKY = BASE_URL + "sesforum/index/sticky" + POST_URL;
    public static final String URL_TOPIC_RATE = BASE_URL + "sesforum/index/rate" + POST_URL;
    public static final String URL_TOPIC_ADD_REPUTATION = BASE_URL + "sesforum/index/addreputation" + POST_URL;
    public static final String URL_TOPIC_SUBSCRIBE = BASE_URL + "sesforum/index/subscribe" + POST_URL;
    public static final String URL_TOPIC_SEARCH = BASE_URL + "sesforum/index/search" + POST_URL;

    //CORE FORUMS APIs
    public static final String URL_CFORUM_HOME_PAGE = BASE_URL + "forums" + POST_URL;
    public static final String URL_CFORUM_VIEW_PAGE = BASE_URL + "forum/index/forumview" + POST_URL;
    public static final String URL_CCREATE_TOPIC = BASE_URL + "forum/index/topiccreate" + POST_URL;
    public static final String URL_CTOPIC_VIEW_PAGE = BASE_URL + "forum/index/topicviewpage" + POST_URL;
    public static final String URL_CTOPIC_STICKY = BASE_URL + "forum/index/sticky" + POST_URL;
    public static final String URL_CTOPIC_POST_DELETE = BASE_URL + "forum/index/deletepost" + POST_URL;
    public static final String URL_CTOPIC_CLOSE = BASE_URL + "forum/index/close" + POST_URL;
    public static final String URL_CTOPIC_POST_EDIT = BASE_URL + "forum/index/editpost" + POST_URL;
    public static final String URL_CTOPIC_MOVE = BASE_URL + "forum/index/move" + POST_URL;
    public static final String URL_CTOPIC_RENAME = BASE_URL + "forum/index/rename" + POST_URL;
    public static final String URL_CTOPIC_POST_QUOTE = BASE_URL + "forum/index/postcreate" + POST_URL;
    public static final String URL_CTOPIC_SUBSCRIBE = BASE_URL + "forum/index/watch" + POST_URL;
    public static final String URL_CTOPIC_DELETE = BASE_URL + "forum/index/deletetopic" + POST_URL;
    public static final String URL_CTOPIC_SEARCH = BASE_URL + "forum/index/search" + POST_URL;

    //SES GROUP APIs
    public static final String URL_GROUP_DEFAULT = BASE_URL + "sesgroup/index/menu" + POST_URL;
    public static final String URL_GROUP_BROWSE = BASE_URL + "sesgroup/index/browse" + POST_URL;
    public static final String URL_GROUP_FILTER_FORM = BASE_URL + "sesgroup/index/browsesearch" + POST_URL;
    public static final String URL_GROUP_CONTACT = BASE_URL + "sesgroup/index/contact" + POST_URL;
    public static final String URL_GROUP_CATEGORIES = BASE_URL + "sesgroup/index/categories" + POST_URL;
    public static final String URL_GROUP_LIKE = BASE_URL + "sesgroup/index/like" + POST_URL;
    public static final String URL_GROUP_FOLLOW = BASE_URL + "sesgroup/index/follow" + POST_URL;
    public static final String URL_GROUP_FAVORITE = BASE_URL + "sesgroup/index/favourite" + POST_URL;
    public static final String URL_GROUP_DELETE = BASE_URL + "sesgroup/index/delete" + POST_URL;
    public static final String URL_GROUP_MANAGE = BASE_URL + "sesgroup/index/manage" + POST_URL;
    public static final String URL_VIEW_GROUP = BASE_URL + "sesgroup/index/view" + POST_URL;
    public static final String URL_GROUP_MEMBER = BASE_URL + "sesgroup/index/member" + POST_URL;
    public static final String URL_GROUP_CLAIM = BASE_URL + "sesgroup/index/claim" + POST_URL;
    public static final String URL_GROUP_INFO_MEMBER = BASE_URL + "sesgroup/index/more-members" + POST_URL;
    public static final String URL_GROUP_ALBUM = BASE_URL + "sesgroup/index/album" + POST_URL;
    public static final String URL_GROUP_MAP = BASE_URL + "sesgroup/index/map" + POST_URL;
    public static final String URL_GROUP_INFO = BASE_URL + "sesgroup/index/info" + POST_URL;
    public static final String URL_GROUP_ANNOUNCE = BASE_URL + "sesgroup/index/announcement" + POST_URL;
    public static final String URL_GROUP_CREATE = BASE_URL + "sesgroup/profile/create" + POST_URL;
    public static final String URL_GROUP_EDIT = BASE_URL + "sesgroup/profile/edit" + POST_URL;
    public static final String URL_GROUP_INVITE = BASE_URL + "sesgroup/index/invite" + POST_URL;
    public static final String URL_GROUP_SERVICES = BASE_URL + "sesgroup/index/services" + POST_URL;
    public static final String URL_GROUP_ASSOCIATED = BASE_URL + "sesgroup/index/associated" + POST_URL;
    public static final String URL_DELETE_GROUP = BASE_URL + "sesgroup/index/delete" + POST_URL;
    public static final String URL_GROUP_JOIN = BASE_URL + "sesgroup/index/join" + POST_URL;
    public static final String URL_GROUP_LEAVE = BASE_URL + "sesgroup/index/leave" + POST_URL;
    public static final String URL_SEARCH_GROUP = BASE_URL + "sesgroup/index/browsesearch" + POST_URL;
    public static final String URL_UPLOAD_GROUP_COVER = BASE_URL + "sesgroup/index/uploadcover" + POST_URL;
    public static final String URL_GROUP_ALBUM_VIEW = BASE_URL + "sesgroup/index/albumview" + POST_URL;
    public static final String URL_GROUP_BROWSE_ALBUM = BASE_URL + "sesgroup/index/browsealbum" + POST_URL;
    public static final String URL_GROUP_VIDEO_VIEW = BASE_URL + "sesgroup/index/view-video" + POST_URL;

    public static final String URL_GROUP_ALBUM_EDIT = BASE_URL + "sesgroup/index/editalbum" + POST_URL;
    public static final String URL_GROUP_ALBUM_CREATE = BASE_URL + "sesgroup/index/createalbum" + POST_URL;
    public static final String URL_GROUP_ALBUM_DELETE = BASE_URL + "sesgroup/index/deletealbum" + POST_URL;

    public static final String URL_LIKE_AS_GROUP = BASE_URL + "sesgroup/index/likeasgroup" + POST_URL;
    public static final String URL_UNLIKE_AS_GROUP = BASE_URL + "sesgroup/index/unlikeasgroup" + POST_URL;
    public static final String URL_GROUP_SEARCH_FILTER = BASE_URL + "sesgroup/index/albumsearchform" + POST_URL;

    public static final String URL_REMOVE_GROUP_COVER = BASE_URL + "sesgroup/index/removecover" + POST_URL;
    public static final String URL_UPLOAD_GROUP_PHOTO = BASE_URL + "sesgroup/index/uploadphoto" + POST_URL;
    public static final String URL_REMOVE_GROUP_PHOTO = BASE_URL + "sesgroup/index/removephoto" + POST_URL;
    public static final String URL_GROUP_LIGHTBOX = BASE_URL + "sesgroup/index/lightbox" + POST_URL;
    public static final String URL_GROUP_ADD_MORE_PHOTOS = BASE_URL + "sesgroup/index/addmorephotos" + POST_URL;
    public static final String URL_GROUP_VIDEO_BROWSE = BASE_URL + "sesgroup/index/browsevideo" + POST_URL;
    public static final String URL_GROUP_VIDEO_CREATE = BASE_URL + "sesgroup/index/create-video" + POST_URL;
    public static final String URL_GROUP_VIDEO_EDIT = BASE_URL + "sesgroup/index/edit-video" + POST_URL;
    public static final String URL_GROUP_VIDEO_PROFILE = BASE_URL + "sesgroup/index/profile-videos" + POST_URL;
    public static final String URL_GROUP_VIDEO_DELETE = BASE_URL + "sesgroup/index/delete-video" + POST_URL;
    public static final String URL_GROUP_VIDEO_WATCH_LATER = BASE_URL + "sesgroup/index/add" + POST_URL;
    public static final String URL_GROUP_VIDEO_RATE = BASE_URL + "sesgroup/index/rate" + POST_URL;
    public static final String URL_GROUP_VIDEO_FAVOURITE = BASE_URL + "sesgroup/index/favourite-video" + POST_URL;
    public static final String URL_GROUP_VIDEO_LIKE = BASE_URL + "sesgroup/index/like-video" + POST_URL;

    public static final String URL_GROUP_REMOVE_MEMBER = BASE_URL + "sesgroup/index/remove" + POST_URL;
    public static final String URL_GROUP_CANCEL_MEMBER = BASE_URL + "sesgroup/index/cancel" + POST_URL;
    public static final String URL_GROUP_REJECT_MEMBER = BASE_URL + "sesgroup/index/reject" + POST_URL;
    public static final String URL_GROUP_APPROVE_MEMBER = BASE_URL + "sesgroup/index/approve" + POST_URL;
    public static final String URL_GROUP_ACCEPT_MEMBER = BASE_URL + "sesgroup/index/accept" + POST_URL;

    public static final String URL_GROUP_PACKAGE = BASE_URL + "sesgroup/profile/package" + POST_URL;
    public static final String URL_GROUP_TRANSACTION = BASE_URL + "sesgroup/profile/transactions" + POST_URL;
    public static final String URL_GROUP_DELETE_PACKAGE = BASE_URL + "sesgroup/profile/cancel" + POST_URL;
    public static final String URL_GROUP_VIDEO_PLAY_URL = BASE_URL + "sesgroup/index/geturl/video_id/";


    //SES-QnA APIs
    public static final String URL_QA_DEFAULT = BASE_URL + "sesqa/index/menu" + POST_URL;
    public static final String URL_QA_BROWSE = BASE_URL + "sesqa/index/browse" + POST_URL;
    public static final String URL_QA_MANAGE = BASE_URL + "sesqa/index/manage" + POST_URL;
    public static final String URL_QA_FILTER_SEARCH = BASE_URL + "sesqa/index/browsesearch" + POST_URL;
    public static final String URL_QA_DELETE = BASE_URL + "sesqa/index/delete" + POST_URL;
    public static final String URL_QA_DELETE_ANSWER = BASE_URL + "sesqa/index/delete-answer" + POST_URL;
    public static final String URL_QA_CREATE_ANSWER = BASE_URL + "sesqa/index/create-answer" + POST_URL;
    public static final String URL_QA_CATEGORIES = BASE_URL + "sesqa/index/categories" + POST_URL;
    public static final String URL_QA_CATEGORY_VIEW = BASE_URL + "sesqa/index/categoryview" + POST_URL;
    public static final String URL_QA_LIKE = BASE_URL + "sesqa/index/like" + POST_URL;
    public static final String URL_QA_FOLLOW = BASE_URL + "sesqa/index/follow" + POST_URL;
    public static final String URL_QA_FAVORITE = BASE_URL + "sesqa/index/favourite" + POST_URL;
    public static final String URL_QA_SEARCH = BASE_URL + "sesqa/index/browsesearch" + POST_URL;
    public static final String URL_QA_CREATE = BASE_URL + "sesqa/index/create" + POST_URL;
    public static final String URL_QA_EDIT = BASE_URL + "sesqa/index/edit" + POST_URL;
    public static final String URL_QA_EDIT_ANSWER = BASE_URL + "sesqa/index/edit-answer" + POST_URL;
    public static final String URL_QA_VIEW = BASE_URL + "sesqa/index/view" + POST_URL;
    public static final String URL_QA_VOTE = BASE_URL + "sesqa/index/vote" + POST_URL;
    public static final String URL_QA_VOTE_UP_DOWN = BASE_URL + "sesqa/index/voteup" + POST_URL;
    public static final String URL_QA_MARK_BEST = BASE_URL + "sesqa/index/mark-best" + POST_URL;

    //SES-CONTEST APIs
    public static final String URL_CONTEST_DEFAULT = BASE_URL + "sescontest/index/menus" + POST_URL;
    public static final String URL_CONTEST_BROWSE = BASE_URL + "sescontest/index/browse" + POST_URL;
    public static final String URL_CONTEST_CATEGORY = BASE_URL + "sescontest/index/categories" + POST_URL;
    public static final String URL_CONTEST_CATEGORY_VIEW = BASE_URL + "sescontest/index/categoryview" + POST_URL;
    public static final String URL_CONTEST_MANAGE = BASE_URL + "sescontest/index/manage-contest" + POST_URL;
    public static final String URL_ENTRY_BROWSE = BASE_URL + "sescontest/index/browse-entries" + POST_URL;
    public static final String URL_WINNER_BROWSE = BASE_URL + "sescontest/index/browse-winner" + POST_URL;
    public static final String URL_CONTEST_CREATE = BASE_URL + "sescontest/contest/create" + POST_URL;
    public static final String URL_CONTEST_EDIT = BASE_URL + "sescontest/contest/edit" + POST_URL;
    public static final String URL_CONTEST_DELETE = BASE_URL + "sescontest/contest/delete" + POST_URL;
    public static final String URL_ENTRY_DELETE = BASE_URL + "sescontest/join/delete" + POST_URL;
    public static final String URL_ENTRY_EDIT = BASE_URL + "sescontest/join/edit" + POST_URL;
    public static final String URL_CONTEST_ENTRIES = BASE_URL + "sescontest/index/contest-entries" + POST_URL;
    public static final String URL_CONTEST_WINNERS = BASE_URL + "sescontest/contest/contest-winner" + POST_URL;
    public static final String URL_CONTEST_MEDIA = BASE_URL + "sescontest/index/browse-media-contest" + POST_URL;
    public static final String URL_FILTER_SEARCH_ENTRY = BASE_URL + "sescontest/index/search-winner-filter" + POST_URL;
    public static final String URL_FILTER_SEARCH_CONTEST = BASE_URL + "sescontest/index/search-filter" + POST_URL;
    public static final String URL_CONTEST_LIKE = BASE_URL + "sescontest/contest/like" + POST_URL;
    public static final String URL_CONTEST_FAVOURITE = BASE_URL + "sescontest/contest/favourite" + POST_URL;
    public static final String URL_CONTEST_FOLLOW = BASE_URL + "sescontest/contest/follow" + POST_URL;
    public static final String URL_CONTEST_JOIN = BASE_URL + "sescontest/join/create" + POST_URL;
    public static final String URL_CONTEST_LEAVE = BASE_URL + "sescontest/join/delete" + POST_URL;
    public static final String URL_CONTEST_VIEW = BASE_URL + "sescontest/contest/view" + POST_URL;
    public static final String URL_ENTRY_VIEW = BASE_URL + "sescontest/join/view" + POST_URL;
    public static final String URL_ENTRY_VOTE = BASE_URL + "sescontest/join/vote" + POST_URL;
    public static final String URL_ENTRY_GRAPH = BASE_URL + "sescontest/join/graph" + POST_URL;
    public static final String URL_CONTEST_ADD_COVER = BASE_URL + "sescontest/contest/upload-cover" + POST_URL;
    public static final String URL_CONTEST_REMOVE_COVER = BASE_URL + "sescontest/contest/remove-cover" + POST_URL;
    public static final String URL_CONTEST_ADD_PHOTO = BASE_URL + "sescontest/contest/mainphoto" + POST_URL;
    public static final String URL_CONTEST_EDIT_OVERVIEW = BASE_URL + "sescontest/contest/overview" + POST_URL;
    public static final String URL_CONTEST_EDIT_AWARD = BASE_URL + "sescontest/contest/award" + POST_URL;
    public static final String URL_CONTEST_EDIT_SEO = BASE_URL + "sescontest/contest/seo" + POST_URL;
    public static final String URL_CONTEST_EDIT_RULES = BASE_URL + "sescontest/contest/rules" + POST_URL;
    public static final String URL_CONTEST_EDIT_CONTACT = BASE_URL + "sescontest/contest/contact-information" + POST_URL;
    public static final String URL_CONTEST_PARTICIPANTS = BASE_URL + "sescontest/contest/contact-participants" + POST_URL;

    public static final String URL_CONTEST_PACKAGE = BASE_URL + "sescontest/contest/package" + POST_URL;
    public static final String URL_CONTEST_TRANSACTION = BASE_URL + "sescontest/contest/transactions" + POST_URL;
    public static final String URL_CONTEST_DELETE_PACKAGE = BASE_URL + "sescontest/contest/cancel" + POST_URL;

    //CORE EVENT APIs
    public static final String URL_CEVENT_BROWSE = BASE_URL + "events/browse" + POST_URL;
    public static final String URL_CEVENT_CATEGORY = BASE_URL + "events/category" + POST_URL;
    public static final String URL_FILTER_CEVENT = BASE_URL + "events/search-form" + POST_URL;
    // public static final String URL_MANAGE_CEVENT = "events/browse/user_id/";
    //public static final String URL_MANAGE_CEVENT = "events/browse" + POST_URL;
    public static final String URL_CREATE_CEVENT = BASE_URL + "events/create" + POST_URL;
    public static final String URL_VIEW_CEVENT = BASE_URL + "event/profile/index" + POST_URL;
    public static final String URL_EDIT_CEVENT = BASE_URL + "events/edit" + POST_URL;
    public static final String URL_CEVENT_INFO = BASE_URL + "event/profile/info" + POST_URL;
    public static final String URL_CEVENT_MEMBER = BASE_URL + "event/profile/members" + POST_URL;
    public static final String URL_CEVENT_DISCUSSION = BASE_URL + "event/profile/discussions" + POST_URL;
    public static final String URL_CEVENT_DISCUSSION_VIEW = BASE_URL + "event/profile/discussionview" + POST_URL;
    public static final String URL_CEVENT_INVITE = BASE_URL + "event/profile/invite" + POST_URL;
    public static final String URL_DELETE_CEVENT = BASE_URL + "event/profile/delete" + POST_URL;
    public static final String URL_CEVENT_PHOTO_UPLOAD = BASE_URL + "event/index/createalbum" + POST_URL;
    public static final String URL_CEVENT_JOIN = BASE_URL + "event/profile/join" + POST_URL;
    public static final String URL_CEVENT_LEAVE = BASE_URL + "event/profile/leave" + POST_URL;
    public static final String URL_CEVENT_REQUEST = BASE_URL + "event/profile/request" + POST_URL;
    public static final String URL_CEVENT_REJECT = BASE_URL + "event/profile/reject" + POST_URL;
    public static final String URL_CEVENT_CANCEL = BASE_URL + "event/profile/cancel" + POST_URL;
    public static final String URL_CREATE_CORE_EVENT_DISCUSSION = BASE_URL + "event/profile/creatediscussion" + POST_URL;
    public static final String URL_EDIT_CORE_EVENT_POST = BASE_URL + "event/profile/editpost" + POST_URL;
    public static final String URL_DELETE_CORE_EVENT_POST = BASE_URL + "event/profile/deletepost" + POST_URL;
    public static final String URL_CORE_EVENT_TOPIC_WATCH = BASE_URL + "event/profile/watch" + POST_URL;
    public static final String URL_CORE_EVENT_REPLY_TOPIC = BASE_URL + "event/profile/commentonpost" + POST_URL;
    public static final String URL_CEVENT_ACCEPT_INVITE = BASE_URL + "event/profile/accept" + POST_URL;
    public static final String URL_CEVENT_PHOTO = BASE_URL + "event/profile/photos" + POST_URL;
    public static final String URL_CEVENT_LIGHTBOX = BASE_URL + "event/profile/lightbox" + POST_URL;


    //SES-EVENT APIs
    public static final String URL_REACTION = BASE_URL + "activity/feed/likes" + POST_URL;
    public static final String URL_EVENT_DEFAULT = BASE_URL + "sesevent/index/menus" + POST_URL;
    public static final String URL_MANAGE_EVENT = BASE_URL + "sesevent/index/manageevents" + POST_URL;
    public static final String URL_LIST_EVENT = BASE_URL + "sesevent/index/browselist" + POST_URL;
    public static final String URL_EVENT_HOST = BASE_URL + "sesevent/index/browsehosts" + POST_URL;
    public static final String URL_EVENT_VIDEOS = BASE_URL + "sesevent/index/browsevideo" + POST_URL;
    public static final String URL_UPCOMING_EVENT = BASE_URL + "sesevent/index/browse" + POST_URL;
    public static final String URL_EVENT_MEMBER = BASE_URL + "sesevent/index/eventguest" + POST_URL;
    public static final String URL_EVENT_ALBUM = BASE_URL + "sesevent/index/eventalbum" + POST_URL;
    public static final String URL_EVENT_DISCUSSION = BASE_URL + "sesevent/index/eventdiscussion" + POST_URL;
    public static final String URL_BROWSE_REVIEWS = BASE_URL + "sesevent/index/reviews" + POST_URL;
    public static final String URL_EVENT_REVIEWS = BASE_URL + "sesevent/index/eventreview" + POST_URL;
    public static final String URL_CATEGORY_VIEW_EVENT = BASE_URL + "sesevent/index/categoryview" + POST_URL;
    public static final String URL_ADD_EVENT_TO_LIST = BASE_URL + "sesevent/index/addtolist" + POST_URL;
    public static final String URL_CREATE_DISCUSSION = BASE_URL + "sesevent/index/creatediscussion" + POST_URL;
    public static final String URL_CREATE_REVIEW = BASE_URL + "sesevent/index/createreviews" + POST_URL;
    public static final String URL_EDIT_TOPIC = BASE_URL + "sesevent/index/renametopic" + POST_URL;
    public static final String URL_DELETE_TOPIC = BASE_URL + "sesevent/index/deletetopic" + POST_URL;
    public static final String URL_EDIT_POST = BASE_URL + "sesevent/index/editpost" + POST_URL;
    public static final String URL_DELETE_POST = BASE_URL + "sesevent/index/deletepost" + POST_URL;
    public static final String URL_DELETE_REVIEW = BASE_URL + "sesevent/index/deletereviews" + POST_URL;
    public static final String URL_SAVE_EVENT = BASE_URL + "sesevent/index/save" + POST_URL;
    public static final String URL_CHANGE_RSVP = BASE_URL + "sesevent/index/profilersvpsave" + POST_URL;
    public static final String URL_VIEW_DISCUSSION = BASE_URL + "sesevent/index/discussionview" + POST_URL;
    public static final String URL_VIEW_REVIEW = BASE_URL + "sesevent/index/reviewview" + POST_URL;
    public static final String URL_EVENT_JOIN = BASE_URL + "sesevent/member/join" + POST_URL;
    public static final String URL_EVENT_LEAVE = BASE_URL + "sesevent/member/leave" + POST_URL;
    public static final String URL_UPLOAD_EVENT_PHOTO = BASE_URL + "sesevent/index/uploadphoto" + POST_URL;
    public static final String URL_UPLOAD_EVENT_COVER = BASE_URL + "sesevent/index/uploadcover" + POST_URL;
    public static final String URL_REMOVE_EVENT_PHOTO = BASE_URL + "sesevent/index/removephoto" + POST_URL;
    public static final String URL_REMOVE_EVENT_COVER = BASE_URL + "sesevent/index/removecover" + POST_URL;
    public static final String URL_DELETE_EVENT = BASE_URL + "sesevent/index/delete" + POST_URL;
    public static final String URL_DELETE_EVENT_LIST = BASE_URL + "sesevent/index/deletelist" + POST_URL;
    public static final String URL_FILTER_EVENT = BASE_URL + "sesevent/index/browsesearch" + POST_URL;
    public static final String URL_FILTER_EVENT_LIST = BASE_URL + "sesevent/index/listbrowsesearch" + POST_URL;
    public static final String URL_FILTER_HOST = BASE_URL + "sesevent/index/hostbrowsesearch" + POST_URL;
    public static final String URL_VIEW_EVENT = BASE_URL + "sesevent/index/eventview" + POST_URL;
    public static final String URL_VIEW_EVENT_LIST = BASE_URL + "sesevent/index/listview" + POST_URL;
    public static final String URL_EDIT_EVENT_LIST = BASE_URL + "sesevent/index/editlist" + POST_URL;
    public static final String URL_PAST_EVENT = BASE_URL + "events/past" + POST_URL;
    public static final String URL_CHANGE_RSVP_STATUS = BASE_URL + "sesevent/index/profilersvpsave" + POST_URL;
    public static final String URL_EVENT_INVITE = BASE_URL + "sesevent/index/invite" + POST_URL;
    public static final String URL_TOPIC_WATCH = BASE_URL + "sesevent/index/watch" + POST_URL;
    public static final String URL_REPLY_TOPIC = BASE_URL + "sesevent/index/commentonpost" + POST_URL;
    public static final String URL_EVENT_VIDEO_VIEW = BASE_URL + "sesevent/video/eventvideoview" + POST_URL;
    public static final String URL_EVENT_ALBUM_VIEW = BASE_URL + "sesevent/index/albumview" + POST_URL;
    public static final String URL_EVENT_LIGHTBOX = BASE_URL + "sesevent/index/lightbox" + POST_URL;
    public static final String URL_EVENT_VIDEO_RATE = BASE_URL + "sesevent/video/rate" + POST_URL;
    public static final String URL_EVENT_VIDEO_DELETE = BASE_URL + "sesevent/video/delete" + POST_URL;
    public static final String URL_EVENT_VIDEO_EDIT = BASE_URL + "sesevent/video/edit" + POST_URL;
    public static final String URL_EVENT_ALBUM_EDIT = BASE_URL + "sesevent/index/editalbum" + POST_URL;
    public static final String URL_EVENT_ALBUM_CREATE = BASE_URL + "sesevent/index/create-album" + POST_URL;
    public static final String URL_EVENT_ALBUM_DELETE = BASE_URL + "sesevent/index/deletealbum" + POST_URL;
    public static final String URL_HOST_VIEW = BASE_URL + "sesevent/index/viewhost" + POST_URL;
    public static final String URL_HOST_EDIT = BASE_URL + "sesevent/index/edithost" + POST_URL;
    public static final String URL_HOST_DELETE = BASE_URL + "sesevent/index/host-delete" + POST_URL;
    public static final String URL_EVENT_ADD_MORE_PHOTOS = BASE_URL + "sesevent/index/addmorephotos" + POST_URL;
    public static final String URL_HOST_FOLLOW = BASE_URL + "sesevent/index/follow-host" + POST_URL;
    public static final String URL_EVENT_REMOVE_MEMBER = BASE_URL + "sesevent/member/remove" + POST_URL;
    public static final String URL_EVENT_APPROVE_MEMBER = BASE_URL + "sesevent/member/approve" + POST_URL;
    public static final String URL_EVENT_REJECT_INVITE = BASE_URL + "sesevent/member/reject" + POST_URL;
    public static final String URL_EVENT_ACCEPT_INVITE = BASE_URL + "sesevent/member/accept" + POST_URL;
    public static final String URL_CREATE_EVENT = BASE_URL + "sesevent/index/create" + POST_URL;
    public static final String URL_CREATE_EVENT_VIDEO = BASE_URL + "sesevent/video/create" + POST_URL;
    public static final String URL_EDIT_EVENT = BASE_URL + "sesevent/index/edit" + POST_URL;
    public static final String URL_EDIT_REVIEW = BASE_URL + "sesevent/index/editreviews" + POST_URL;
    public static final String URL_EVENT_CATEGORY = BASE_URL + "sesevent/index/browsecategories" + POST_URL;
    public static final String URL_EVENT_VIDEO_WATCH_LATER = BASE_URL + "sesevent/video/add" + POST_URL;
    public static final String URL_EVENT_INFO = BASE_URL + "sesevent/index/eventinfo" + POST_URL;
    public static final String URL_EVENT_DASHBOARD = BASE_URL + "sesevent/dashboard/edit/event_id/";


    //PAGE POLL APIs
    public static final String URL_PAGE_POLLS = BASE_URL + "sespage/poll/browse" + POST_URL;
    public static final String URL_PAGE_PROFILE_POLLS = BASE_URL + "sespage/poll/page-poll" + POST_URL;
    public static final String URL_PAGE_POLL_CREATE = BASE_URL + "sespage/poll/create" + POST_URL;
    public static final String URL_PAGE_POLL_FAVORITE = BASE_URL + "sespage/poll/favourite" + POST_URL;
    public static final String URL_PAGE_POLL_LIKE = BASE_URL + "sespage/poll/like" + POST_URL;
    public static final String URL_CREATE_POLLS = BASE_URL + "sespage/poll/create" + POST_URL;
    public static final String URL_PAGE_POLL_VIEW = BASE_URL + "sespage/poll/view" + POST_URL;
    public static final String URL_PAGE_POLL_FILTER = BASE_URL + "sespage/poll/search" + POST_URL;
    public static final String URL_DELETE_POLL = BASE_URL + "polls/delete/poll_id/";
    public static final String URL_EDIT_POLL = BASE_URL + "polls/edit/poll_id/";
    public static final String URL_PAGE_POLL_EDIT = BASE_URL + "sespage/poll/edit" + POST_URL;
    public static final String URL_PAGE_POLL_DELETE = BASE_URL + "sespage/poll/delete" + POST_URL;
    public static final String URL_PAGE_POLL_CLOSE = BASE_URL + "sespage/poll/close" + POST_URL;
    public static final String URL_PAGE_POLL_VOTE = BASE_URL + "sespage/poll/vote" + POST_URL;
    public static final String URL_PAGE_POLL_GIF = BASE_URL + "sespage/poll/gifs" + POST_URL;
    public static final String URL_PAGE_POLL_VOTED_USER = BASE_URL + "sespage/poll/more" + POST_URL;

    //PAGE REVIEW
    public static final String URL_PAGE_REVIEW_FILTER = BASE_URL + "sespage/review/browse-search" + POST_URL;
    public static final String URL_PAGE_REVIEW_HOME = BASE_URL + "sespage/review/home" + POST_URL;
    public static final String URL_PAGE_REVIEW_PROFILE = BASE_URL + "sespage/review/page-reviews" + POST_URL;
    public static final String URL_PAGE_REVIEW_CREATE = BASE_URL + "sespage/review/create" + POST_URL;
    public static final String URL_PAGE_REVIEW_EDIT = BASE_URL + "sespage/review/edit" + POST_URL;
    public static final String URL_PAGE_REVIEW_DELETE = BASE_URL + "sespage/review/delete" + POST_URL;
    public static final String URL_PAGE_REVIEW_LIKE = BASE_URL + "sespage/review/like" + POST_URL;
    public static final String URL_PAGE_REVIEW_VOTE = BASE_URL + "sespage/review/review-votes" + POST_URL;
    public static final String URL_PAGE_REVIEW_VIEW = BASE_URL + "sespage/review/view" + POST_URL;

    //BUSINESS REVIEW
    public static final String URL_BUSINESS_REVIEW_FILTER = BASE_URL + "sesbusiness/review/browse-search" + POST_URL;
    public static final String URL_BUSINESS_REVIEW_HOME = BASE_URL + "sesbusiness/review/home" + POST_URL;
    public static final String URL_BUSINESS_REVIEW_PROFILE = BASE_URL + "sesbusiness/review/business-reviews" + POST_URL;
    public static final String URL_BUSINESS_REVIEW_CREATE = BASE_URL + "sesbusiness/review/create" + POST_URL;
    public static final String URL_BUSINESS_REVIEW_EDIT = BASE_URL + "sesbusiness/review/edit" + POST_URL;
    public static final String URL_BUSINESS_REVIEW_DELETE = BASE_URL + "sesbusiness/review/delete" + POST_URL;
    public static final String URL_BUSINESS_REVIEW_LIKE = BASE_URL + "sesbusiness/review/like" + POST_URL;
    public static final String URL_BUSINESS_REVIEW_VOTE = BASE_URL + "sesbusiness/review/review-votes" + POST_URL;
    public static final String URL_BUSINESS_REVIEW_VIEW = BASE_URL + "sesbusiness/review/view" + POST_URL;

    //PRODUCT REVIEW
    public static final String URL_PRODUCT_REVIEW_FILTER = BASE_URL + "estore/review/browse-search" + POST_URL;
    public static final String URL_PRODUCT_REVIEW_HOME = BASE_URL + "estore/review/home" + POST_URL;
    public static final String URL_PRODUCT_REVIEW_PROFILE = BASE_URL + "estore/product/review" + POST_URL;
    public static final String URL_PRODUCT_REVIEW_CREATE = BASE_URL + "estore/product/review-create" + POST_URL;
    public static final String URL_PRODUCT_REVIEW_EDIT = BASE_URL + "estore/product/edit-review" + POST_URL;
    public static final String URL_PRODUCT_REVIEW_DELETE = BASE_URL + "estore/product/delete-review" + POST_URL;
    public static final String URL_PRODUCT_REVIEW_LIKE = BASE_URL + "estore/product/like-review" + POST_URL;
    public static final String URL_PRODUCT_REVIEW_VOTE = BASE_URL + "estore/product/review-votes" + POST_URL;
    public static final String URL_PRODUCT_REVIEW_VIEW = BASE_URL + "estore/product/review-view" + POST_URL;

    //STORE REVIEW
    public static final String URL_STORE_REVIEW_FILTER = BASE_URL + "estore/review/browse-search" + POST_URL;
    public static final String URL_STORE_REVIEW_HOME = BASE_URL + "estore/index/storereviews" + POST_URL;
    public static final String URL_STORE_REVIEW_PROFILE = BASE_URL + "estore/index/storereviews" + POST_URL;
    public static final String URL_STORE_REVIEW_CREATE = BASE_URL + "estore/index/storecreatereview" + POST_URL;
    public static final String URL_STORE_REVIEW_EDIT = BASE_URL + "estore/index/storeeditreview" + POST_URL;
    public static final String URL_STORE_REVIEW_DELETE = BASE_URL + "estore/index/storereviews" + POST_URL;
    public static final String URL_STORE_REVIEW_LIKE = BASE_URL + "estore/index/storereviewlike" + POST_URL;
    public static final String URL_STORE_REVIEW_VOTE = BASE_URL + "estore/index/storereviewvotes" + POST_URL;
    public static final String URL_STORE_REVIEW_VIEW = BASE_URL + "estore/index/storereviewview" + POST_URL;

    //GROUP REVIEW
    public static final String URL_GROUP_REVIEW_FILTER = BASE_URL + "sesgroup/review/browse-search" + POST_URL;
    public static final String URL_GROUP_REVIEW_HOME = BASE_URL + "sesgroup/review/home" + POST_URL;
    public static final String URL_GROUP_REVIEW_PROFILE = BASE_URL + "sesgroup/review/group-reviews" + POST_URL;
    public static final String URL_GROUP_REVIEW_CREATE = BASE_URL + "sesgroup/review/create" + POST_URL;
    public static final String URL_GROUP_REVIEW_EDIT = BASE_URL + "sesgroup/review/edit" + POST_URL;
    public static final String URL_GROUP_REVIEW_DELETE = BASE_URL + "sesgroup/review/delete" + POST_URL;
    public static final String URL_GROUP_REVIEW_LIKE = BASE_URL + "sesgroup/review/like" + POST_URL;
    public static final String URL_GROUP_REVIEW_VOTE = BASE_URL + "sesgroup/review/review-votes" + POST_URL;
    public static final String URL_GROUP_REVIEW_VIEW = BASE_URL + "sesgroup/review/view" + POST_URL;

    //GROUP POLL APIs
    public static final String URL_GROUP_POLLS = BASE_URL + "sesgroup/poll/browse" + POST_URL;
    public static final String URL_GROUP_PROFILE_POLLS = BASE_URL + "sesgroup/poll/group-poll" + POST_URL;
    public static final String URL_GROUP_POLL_CREATE = BASE_URL + "sesgroup/poll/create" + POST_URL;
    public static final String URL_GROUP_POLL_FAVORITE = BASE_URL + "sesgroup/poll/favourite" + POST_URL;
    public static final String URL_GROUP_POLL_LIKE = BASE_URL + "sesgroup/poll/like" + POST_URL;
    public static final String URL_GROUP_POLL_VIEW = BASE_URL + "sesgroup/poll/view" + POST_URL;
    public static final String URL_GROUP_POLL_FILTER = BASE_URL + "sesgroup/poll/search" + POST_URL;
    public static final String URL_GROUP_POLL_EDIT = BASE_URL + "sesgroup/poll/edit" + POST_URL;
    public static final String URL_GROUP_POLL_DELETE = BASE_URL + "sesgroup/poll/delete" + POST_URL;
    public static final String URL_GROUP_POLL_CLOSE = BASE_URL + "sesgroup/poll/close" + POST_URL;
    public static final String URL_GROUP_POLL_VOTE = BASE_URL + "sesgroup/poll/vote" + POST_URL;
    public static final String URL_GROUP_POLL_GIF = BASE_URL + "sesgroup/poll/gifs" + POST_URL;
    public static final String URL_GROUP_POLL_VOTED_USER = BASE_URL + "sesgroup/poll/more" + POST_URL;

    //PAGE POLL APIs
    public static final String URL_BUSINESS_POLLS = BASE_URL + "sesbusiness/poll/browse" + POST_URL;
    public static final String URL_BUSINESS_PROFILE_POLLS = BASE_URL + "sesbusiness/poll/business-poll" + POST_URL;
    public static final String URL_BUSINESS_POLL_CREATE = BASE_URL + "sesbusiness/poll/create" + POST_URL;
    public static final String URL_BUSINESS_POLL_FAVORITE = BASE_URL + "sesbusiness/poll/favourite" + POST_URL;
    public static final String URL_BUSINESS_POLL_LIKE = BASE_URL + "sesbusiness/poll/like" + POST_URL;
    public static final String URL_BUSINESS_POLL_VIEW = BASE_URL + "sesbusiness/poll/view" + POST_URL;
    public static final String URL_BUSINESS_POLL_FILTER = BASE_URL + "sesbusiness/poll/search" + POST_URL;
    public static final String URL_BUSINESS_POLL_EDIT = BASE_URL + "sesbusiness/poll/edit" + POST_URL;
    public static final String URL_BUSINESS_POLL_DELETE = BASE_URL + "sesbusiness/poll/delete" + POST_URL;
    public static final String URL_BUSINESS_POLL_CLOSE = BASE_URL + "sesbusiness/poll/close" + POST_URL;
    public static final String URL_BUSINESS_POLL_VOTE = BASE_URL + "sesbusiness/poll/vote" + POST_URL;
    public static final String URL_BUSINESS_POLL_GIF = BASE_URL + "sesbusiness/poll/gifs" + POST_URL;
    public static final String URL_BUSINESS_POLL_VOTED_USER = BASE_URL + "sesbusiness/poll/more" + POST_URL;

    //Core-Poll APIs

    public static final String URL_POLL_BROWSE = BASE_URL + "polls/browse" + POST_URL;
    public static final String URL_POLL_CREATE = BASE_URL + "polls/create" + POST_URL;
    public static final String URL_POLL_DELETE = BASE_URL + "polls/delete" + POST_URL;
    public static final String URL_POLL_EDIT = BASE_URL + "polls/edit" + POST_URL;
    //public static final String URL_POLL_LIKE = BASE_URL + "polls/like" + POST_URL;
    public static final String URL_POLL_CLOSE = BASE_URL + "polls/close" + POST_URL;
    public static final String URL_POLL_VIEW = BASE_URL + "polls/view" + POST_URL;
    public static final String URL_POLL_VOTE = BASE_URL + "polls/vote" + POST_URL;
    public static final String URL_POLL_SEARCH = BASE_URL + "polls/search" + POST_URL;

    // LIVE STREAMING APIs
    public static final String URL_NOTIFY_USERS = BASE_URL + "elivestreaming/notification/send" + POST_URL;
    public static final String URL_START_RECORDING = "recorder/v1/start";
    public static final String URL_STOP_RECORDING = "recorder/v1/stop";
    public static final String URL_SHARE_LIVE_VIDEO = "activityfeed/livevideo" + POST_URL;
    public static final String URL_STREAMING_STATUS = BASE_URL + "elivestreaming/index/status" + POST_URL;
    public static final String URL_STREAMING_CANCEL = BASE_URL + "elivestreaming/index/cancel" + POST_URL;
    public static final String URL_LIVE_PERMISSION = BASE_URL + "elivestreaming/index/get-permission" + POST_URL;
    public static final String URL_CHANGE_STATUS = BASE_URL + "elivestreaming/index/change-status" + POST_URL;
    //COURSE APIS


    //Classroom APIs
    public static final String URL_BROWSE_CLASSROOM = BASE_URL + "courses/classroom/browse" + POST_URL;
    public static final String URL_SEARCH_CLASSROOM = BASE_URL + "courses/classroom/browsesearch" + POST_URL;
    public static final String URL_CREATE_CLASSROOM = BASE_URL + "courses/classroom/create" + POST_URL;
    public static final String URL_EDIT_CLASSROOM = BASE_URL + "courses/classroom/edit" + POST_URL;
    public static final String URL_INDEX_MENU = BASE_URL + "courses/index/menu" + POST_URL;
    public static final String URL_DELETE_CLASSROOM = BASE_URL + "courses/classroom/delete" + POST_URL;
    public static final String URL_FOLLOW_CLASSROOM = BASE_URL + "courses/classroom/follow" + POST_URL;
    public static final String URL_LIKE_CLASSROOM = BASE_URL + "courses/classroom/like" + POST_URL;
    public static final String URL_CLASSROOM_JOIN = BASE_URL + "courses/classroom/join" + POST_URL;
    public static final String URL_CLASSROOM_CREATE_REVIEW = BASE_URL + "courses/classroom/review-create" + POST_URL;
    public static final String  URL_CLASSROOM_REVIEW_PROFILE = BASE_URL + "courses/classroom/review" + POST_URL;
    public static final String URL_CLASSROOM_EDIT_REVIEW = BASE_URL + "courses/classroom/edit-review" + POST_URL;
    public static final String URL_CLASSROOM_REVIEW_VIEW = BASE_URL + "courses/classroom/review-view" + POST_URL;
    public static final String URL_CLASSROOM_REVIEW_DELETE = BASE_URL + "courses/classroom/delete-review" + POST_URL;
    public static final String URL_CLASSROOM_REVIEW_VOTE = BASE_URL + "courses/classroom/review-votes" + POST_URL;
    public static final String URL_CLASSROOM_MEMBER = BASE_URL + "courses/classroom/member" + POST_URL;
    public static final String URL_CLASSROOM_ANNOUNCEMENT = BASE_URL + "courses/classroom/announcement" + POST_URL;
    public static final String URL_CLASSROOM_VIEW = BASE_URL + "courses/classroom/view" + POST_URL;
    public static final String URL_CLASSROOM_MAP = BASE_URL + "courses/classroom/map" + POST_URL;
    public static final String URL_CLASSROOM_ALBUM = BASE_URL + "courses/classroom/browsealbum" + POST_URL;
    public static final String URL_CLASSROOM_INFO = BASE_URL + "courses/classroom/info" + POST_URL;

    public static final String URL_CLASSROOM_CLAIM = BASE_URL + "courses/classroom/claim" + POST_URL;
    public static final String URL_CLASSROOM_INVITE = BASE_URL + "courses/classroom/invite" + POST_URL;
    public static final String URL_CLASSROOM_CONTACT = BASE_URL + "courses/classroom/contact" + POST_URL;
    public static final String URL_CLASSROOM_CATEGORY = BASE_URL + "courses/classroom/categories" + POST_URL;
    public static final String URL_CLASSROOM_CATEGORY_VIEW = BASE_URL + "courses/classroom/category" + POST_URL;
    public static final String URL_CLASSROOM_DELETE = BASE_URL + "courses/classroom/delete" + POST_URL;
    public static final String URL_CLASSROOM_PROFILE_COURSE = BASE_URL + "courses/index/profile-courses" + POST_URL;
    public static final String URL_CLASSROOM_SEARCHFORM = BASE_URL + "courses/classroom/albumsearchform" + POST_URL;
    public static final String URL_CLASSROOM_LIKE = BASE_URL + "courses/classroom/like" + POST_URL;
    public static final String URL_CLASSROOM_FAVORITE = BASE_URL + "courses/classroom/favourite" + POST_URL;
    public static final String URL_CLASSROOM_FOLLOW = BASE_URL + "courses/classroom/follow" + POST_URL;
    public static final String URL_WISHLIST_DELETE = BASE_URL + "courses/index/delete-wishlist" + POST_URL;
    public static final String URL_WISHLIST_EDIT = BASE_URL + "courses/index/edit-wishlist" + POST_URL;
    public static final String URL_ACCEPT_MEMBER = BASE_URL + "courses/classroom/accept" + POST_URL;
    public static final String URL_APPROVE_MEMBER = BASE_URL + "courses/classroom/approve" + POST_URL;
    public static final String URL_REJECT_MEMBER = BASE_URL + "courses/classroom/remove" + POST_URL;
    public static final String URL_CANCEL_MEMBER = BASE_URL + "courses/classroom/cancel" + POST_URL;
    public static final String URL_CLASSROOM_SERVICES = BASE_URL + "courses/classroom/services" + POST_URL;
    public static final String URL_CLASSROOM_ALBUMVIEW = BASE_URL + "courses/classroom/albumview" + POST_URL;
    public static final String URL_CLASSROOM_ALBUMCREATE = BASE_URL + "courses/classroom/createalbum" + POST_URL;
    public static final String URL_MY_CLASSROOM = BASE_URL + "courses/classroom/manage" + POST_URL;
    public static final String URL_CLASSROOM_UPLOAD_COVER = BASE_URL + "courses/classroom/uploadcover" + POST_URL;
    public static final String URL_CLASSROOM_UPLOAD_MAIN = BASE_URL + "courses/classroom/uploadphoto" + POST_URL;
    public static final String URL_CLASSROOM_REMOVE_COVER = BASE_URL + "courses/classroom/removecover" + POST_URL;
    public static final String URL_CLASSROOM_REMOVE_MAIN = BASE_URL + "courses/classroom/removephoto" + POST_URL;
    public static final String URL_CLASSROOM_LEAVE = BASE_URL + "courses/classroom/leave" + POST_URL;
    public static final String URL_CLASSROOM_EDIT_ALBUM = BASE_URL + "courses/classroom/editalbum" + POST_URL;
    public static final String URL_CLASSROOM_DELETE_ALBUM = BASE_URL + "courses/classroom/deletealbum" + POST_URL;

    //Courses APIs
    public static final String URL_COURSE_CATEGORY_VIEW = BASE_URL + "courses/index/category" + POST_URL;
    public static final String URL_COURSE_RESULT = BASE_URL + "courses/index/view-result" + POST_URL;
    public static final String URL_CREATE_COURSE = BASE_URL + "courses/index/create" + POST_URL;
    public static final String URL_BROWSE_COURSE = BASE_URL + "courses/index/browse" + POST_URL;
    public static final String URL_COURSE_INFO = BASE_URL + "courses/index/info" + POST_URL;
    public static final String URL_BROWSE_COURSE_CATEGORY = BASE_URL + "courses/index/course-categories" + POST_URL;
    public static final String URL_EDIT_COURSE = BASE_URL + "courses/index/edit" + POST_URL;
    public static final String URL_VIEW_COURSE = BASE_URL + "courses/index/view" + POST_URL;
    public static final String URL_DELETE_COURSE = BASE_URL + "courses/index/delete" + POST_URL;
    public static final String URL_LIKE_COURSE = BASE_URL + "courses/index/like" + POST_URL;
    public static final String URL_COURSE_CREATE_REVIEW = BASE_URL + "courses/index/review-create" + POST_URL;
    public static final String URL_COURSE_EDIT_REVIEW = BASE_URL + "courses/index/edit-review" + POST_URL;
    public static final String URL_COURSE_PROFILE_REVIEW = BASE_URL + "courses/index/review" + POST_URL;
    public static final String URL_COURSE_REVIEW_VIEW = BASE_URL + "courses/index/review-view" + POST_URL;
    public static final String URL_COURSE_REVIEW_DELETE = BASE_URL + "courses/index/delete-review" + POST_URL;
    public static final String URL_COURSE_REVIEW_VOTE = BASE_URL + "courses/index/review-votes" + POST_URL;
    public static final String URL_SEARCH_COURSE = BASE_URL + "courses/index/browsesearch" + POST_URL;
    public static final String URL_COURSE_CART = BASE_URL + "courses/index/my-cart" + POST_URL;
    public static final String URL_COURSE_CART_DELETE = BASE_URL + "courses/index/deletecart" + POST_URL;
    public static final String URL_COURSE_ADD_WISHLIST = BASE_URL + "courses/index/add-wishlist" + POST_URL;
    public static final String URL_COURSE_ADD_CART = BASE_URL + "courses/index/addtocart" + POST_URL;
    public static final String URL_COURSE_VIEW = BASE_URL + "courses/index/view" + POST_URL;
    public static final String URL_COURSE_UPSELL = BASE_URL + "courses/index/profile-upsell" + POST_URL;
    public static final String URL_PROFILE_LECTURE = BASE_URL + "courses/index/profile-lecture" + POST_URL;
    public static final String URL_COURSE_BILLING = BASE_URL + "courses/index/billing" + POST_URL;
    public static final String URL_BROWSE_COURSEWISHLIST = BASE_URL + "courses/index/browse-wishlist" + POST_URL;
    public static final String URL_VIEW_COURSEWISHLIST = BASE_URL + "courses/index/view-wishlist" + POST_URL;
    public static final String URL_VIEW_COURSEORDERS = BASE_URL + "courses/index/my-order" + POST_URL;
    public static final String URL_COURSE_BROWSE_SEARCH = BASE_URL + "courses/index/browsesearch" + POST_URL;
    public static final String URL_COURSE_LIKE = BASE_URL + "courses/index/like" + POST_URL;
    public static final String URL_COURSE_FAVOURITE = BASE_URL + "courses/index/favourite" + POST_URL;
    public static final String URL_LECTURE_VIEW = BASE_URL + "courses/index/lecture-view" + POST_URL;
    public static final String URL_LECTURE_DELETE = BASE_URL + "courses/index/delete-lecture" + POST_URL;
    public static final String URL_CREATE_LECTURE = BASE_URL + "courses/index/create-lecture" + POST_URL;
    public static final String URL_COURSE_DELETE = BASE_URL + "courses/index/delete" + POST_URL;
    public static final String URL_COURSE_CHECKOUT = BASE_URL + "course/cart";
    public static final String URL_MY_COURSE = BASE_URL + "courses/index/manage" + POST_URL;
    public static final String URL_COURSE_VIEW_ORDER = BASE_URL + "courses/index/view-order" + POST_URL;
    public static final String URL_VIEW_ORDER = BASE_URL + "courses/order/view/";
    public static final String URL_LECTURE_EDIT = BASE_URL + "courses/index/edit-lecture" + POST_URL;
    public static final String URL_CLASSROOM_ACCEPT = BASE_URL + "courses/classroom/accept" + POST_URL;
    public static final String URL_CLASSROOM_REJECT = BASE_URL + "courses/classroom/reject" + POST_URL;

    //TEST APIs
    public static final String URL_PROFILE_TEST = BASE_URL + "courses/index/profile-test" + POST_URL;
    public static final String URL_CREATE_TEST = BASE_URL + "courses/index/create-test" + POST_URL;
    public static final String URL_MY_TEST = BASE_URL + "courses/index/my-test" + POST_URL;
    public static final String URL_VIEW_TEST = BASE_URL + "courses/index/join-test" + POST_URL;

    //COURSE APIS-END HERE
    // Interest APIs
    public static final String URl_CHOOSE_INTEREST = BASE_URL + "user/profile/chooseinterests" + POST_URL;

    //Booking APIs
    public static final String URL_BOOKING_INDEX = BASE_URL + "booking/index/menu" + POST_URL;
    public static final String URL_PROFESSIONAL_BROWSE = BASE_URL + "booking/index/browse-professionals" + POST_URL;
    public static final String URL_SERVICE_BROWSE = BASE_URL + "booking/index/browse-services" + POST_URL;
    public static final String URL_SERVICE_VIEW = BASE_URL + "booking/index/service-view" + POST_URL;
    public static final String URL_PROFESSIONAL_VIEW = BASE_URL + "booking/index/professional-view" + POST_URL;
    public static final String URL_SEARCH_FILTER_SERVICE = BASE_URL + "booking/index/filtersearch-services" + POST_URL;
    public static final String URL_SEARCH_FILTER_PROFESSIONAL = BASE_URL + "booking/index/filter-professional" + POST_URL;
    public static final String URL_BECOME_PROFESSIONAL = BASE_URL + "booking/index/add-service" + POST_URL;
    public static final String URL_PROFESSIONAL_LIKE = BASE_URL + "booking/index/like" + POST_URL;
    public static final String URL_PROFESSIONAL_FAVORITE = BASE_URL + "booking/index/favourite" + POST_URL;
    public static final String URL_PROFESSIONAL_FOLLOW = BASE_URL + "booking/index/follow" + POST_URL;
    public static final String URL_SERVICE_LIKE = BASE_URL + "booking/index/service-like" + POST_URL;
    public static final String URL_SERVICE_EDIT = BASE_URL + "booking/index/edit-service" + POST_URL;
    public static final String URL_SERVICE_DELETE = BASE_URL + "booking/index/delete-service" + POST_URL;
    public static final String URL_SERVICE_FAVORITE = BASE_URL + "booking/index/service-favourite" + POST_URL;
    public static final String URL_SERVICE_REVIEW_EDIT = BASE_URL + "booking/index/review-edit" + POST_URL;
    public static final String URL_SERVICE_REVIEW_DELETE = BASE_URL + "booking/index/review-delete" + POST_URL;
    public static final String URL_SERVICE_REVIEW_CREATE = BASE_URL + "booking/index/create-review" + POST_URL;

    public static final String URL_SERVICE_REVIEW_PROFILE = BASE_URL + "booking/index/service-viewreview" + POST_URL;
    public static final String URL_SERVICE_REVIEW_BROWSE = BASE_URL + "booking/index/service-viewreview" + POST_URL;
    public static final String URL_SERVICE_REVIEW_VIEW = BASE_URL + "booking/index/review-view" + POST_URL;
    public static final String URL_PROFESSIONAL_REVIEW_PROFILE = BASE_URL + "booking/index/browse-professionalreview" + POST_URL;
    public static final String URL_PROFESSIONAL_REVIEW_CREATE = BASE_URL + "booking/index/create-Professionalreview" + POST_URL;
    public static final String URL_PROFESSIONAL_REVIEW_VIEW = BASE_URL + "booking/index/professional-reviewview" + POST_URL;
    public static final String URL_PROFESSIONAL_REVIEW_EDIT = BASE_URL + "booking/index/edit-Professionalreview" + POST_URL;
    public static final String URL_PROFESSIONAL_REVIEW_DELETE = BASE_URL + "booking/index/delete-professionalreview" + POST_URL;
    public static final String URL_PROFESSIONAL_PROFILE_SERVICES = BASE_URL + "booking/index/professional-profileservices" + POST_URL;
    public static final String URL_PROFESSIONAL_REENABLE = BASE_URL + "booking/index/professional-enable" + POST_URL;

    //Group - Topic extension

    public static final String URL_GROUP_TOPIC_CREATE = BASE_URL + "sesgroup/forum/create" + POST_URL;
    public static final String URL_GROUP_TOPIC_DELETE = BASE_URL + "sesgroup/forum/delete" + POST_URL;
    public static final String URL_GROUP_FORUM_BROWSE = BASE_URL + "sesgroup/forum/browse" + POST_URL;
    public static final String URL_GROUP_TOPIC_VIEW = BASE_URL + "sesgroup/forum/topicviewpage" + POST_URL;
    public static final String URL_POST_CREATE = BASE_URL + "sesgroup/forum/post-create" + POST_URL;
    public static final String URL_POST_EDIT = BASE_URL + "sesgroup/forum/editpost" + POST_URL;
    public static final String URL_POST_DELETE = BASE_URL + "sesgroup/forum/deletepost" + POST_URL;
    public static final String URL_TOPIC_LIKE = BASE_URL + "sesgroup/forum/like" + POST_URL;
    public static final String URL_GROUP_TOPIC_SUBSCRIBE = BASE_URL + "sesgroup/forum/subscribe" + POST_URL;
    public static final String URL_SAY_THANK_GROUP = BASE_URL + "sesgroup/forum/thank" + POST_URL;
    public static final String URL_TOPIC_RENAME_GROUP = BASE_URL + "sesgroup/forum/rename" + POST_URL;
    public static final String URL_TOPIC_MOVE_GROUP = BASE_URL + "sesgroup/forum/move" + POST_URL;
    public static final String URL_TOPIC_CLOSE_GROUP = BASE_URL + "sesgroup/forum/close" + POST_URL;
    public static final String URL_TOPIC_STICKY_GROUP = BASE_URL + "sesgroup/forum/sticky" + POST_URL;
    public static final String URL_TOPIC_RATE_GROUP = BASE_URL + "sesgroup/forum/rate" + POST_URL;
    public static final String URL_TOPIC_ADD_REPUTATION_GROUP = BASE_URL + "sesgroup/forum/addreputation" + POST_URL;


    //Custom video apis
    public static final String URL_CREATE_TIKTOK = BASE_URL + "vavci/video/create" + POST_URL;
    public static final String URL_DISCOVER_ACTIVITY = BASE_URL + "tickvideo/index/activity" + POST_URL;
    public static final String URL_VIDEO_NOTIFICATION = BASE_URL + "core/index/notification" + POST_URL;
    public static final String URL_GET_MUSIC = BASE_URL + "tickvideo/index/getmusics" + POST_URL;
    public static final String URL_ALL_MUSIC = BASE_URL + "tickvideo/index/musics" + POST_URL;
    public static final String URL_MUSIC_LIST = BASE_URL + "tickvideo/index/musics" + POST_URL;
    public static final String URL_TICK_CREATE = BASE_URL + "tickvideo/index/create" + POST_URL;
    public static final String URL_TICK_BROWSE = BASE_URL + "tickvideo/index/browse" + POST_URL;
    public static final String URL_TICK_FORYOU = BASE_URL + "tickvideo/index/foryou" + POST_URL;
    public static final String URL_MUSIC_FAV = BASE_URL + "content/favourite" + POST_URL;
    public static final String URL_ACT_HASH = BASE_URL + "tickvideo/index/hashtag" + POST_URL;
    public static final String URL_CHANNEL_FOLLOWERS = BASE_URL + "sesvideo/chanel/channel-followers" + POST_URL;

    //3rd Party Multi Currency
    public static final String URL_GET_CURRENCY = BASE_URL + "sesmultiplecurrency/index/supported-currencies" + POST_URL;
    public static final String URL_CHANNEL_FOLLOWERS2 = BASE_URL + "members/index/followers/viewer_id/";
    public static final String URL_CHANNEL_FOLLOWING_USER = BASE_URL + "members/index/following/viewer_id/";
    public static final String URL_CHANGE_CURRENCY = BASE_URL + "sesmultiplecurrency/index/change-currency" + POST_URL;


    //SES-Resume APIs

    public static final String RESUME_PREVIEW = BASE_URL + "eresume/index/generate" + POST_URL;

    public static final String RESUME_PRE_DASHBOARD = BASE_URL + "eresume/index/dashboard" + POST_URL;

    public static final String CREDIT_RESUMEBUILDER = BASE_URL + "eresume/index/index" + POST_URL;
    public static final String CREDIT_RESUMEBUILDER_TiTLE = BASE_URL + "eresume/index/create" + POST_URL;
    public static final String CREDIT_RESUMEBUILDER_TiTLE_EDIT = BASE_URL + "eresume/index/edit" + POST_URL;
    public static final String CREDIT_RESUMEBUILDER_DELETE = BASE_URL + "eresume/index/delete" + POST_URL;

    public static final String CREDIT_RESUME_INFORMATION = BASE_URL + "eresume/index/contact-information" + POST_URL;
    public static final String CREDIT_RESUME_WORKEXPERIENCE = BASE_URL + "eresume/index/workexperience" + POST_URL;
    public static final String CREDIT_RESUME_ADDWORKEXPERIENCE = BASE_URL + "eresume/index/add-experience" + POST_URL;
    public static final String CREDIT_RESUME_EDIT_WORKEXPERIENCE = BASE_URL + "eresume/index/edit-experience" + POST_URL;
    public static final String CREDIT_RESUME_DELETE_WORKEXPERIENCE = BASE_URL + "eresume/index/delete-experience" + POST_URL;


    public static final String CREDIT_RESUME_EDUCTAION = BASE_URL + "eresume/index/education" + POST_URL;
    public static final String CREDIT_RESUME_ADDEDUCTAION = BASE_URL + "eresume/index/add-education" + POST_URL;
    public static final String CREDIT_RESUME_EDIT_EDUCTAION = BASE_URL + "eresume/index/edit-education" + POST_URL;
    public static final String CREDIT_RESUME_DELETE_EDUCTAION = BASE_URL + "eresume/index/delete-education" + POST_URL;

    public static final String CREDIT_RESUME_PROJECT = BASE_URL + "eresume/index/project" + POST_URL;
    public static final String CREDIT_RESUME_ADDPROJECT = BASE_URL + "eresume/index/add-project" + POST_URL;
    public static final String CREDIT_RESUME_DELETEPROJECT = BASE_URL + "eresume/index/delete-project" + POST_URL;
    public static final String CREDIT_RESUME_EDITPROJECT = BASE_URL + "eresume/index/edit-project" + POST_URL;

    public static final String CREDIT_RESUME_CERTIFICATE = BASE_URL + "eresume/index/certificate" + POST_URL;
    public static final String CREDIT_RESUME_ADD_CERTIFICATE = BASE_URL + "eresume/index/add-certificate" + POST_URL;
    public static final String CREDIT_RESUME_EDIT_CERTIFICATE = BASE_URL + "eresume/index/edit-certificate" + POST_URL;
    public static final String CREDIT_RESUME_DELETE_CERTIFICATE = BASE_URL + "eresume/index/delete-certificate" + POST_URL;

    public static final String CREDIT_RESUME_REFERENCE = BASE_URL + "eresume/index/reference" + POST_URL;
    public static final String CREDIT_RESUME_ADD_REFERENCE = BASE_URL + "eresume/index/add-reference" + POST_URL;
    public static final String CREDIT_RESUME_EDIT_REFERENCE = BASE_URL + "eresume/index/edit-reference" + POST_URL;
    public static final String CREDIT_RESUME_DELETE_REFERENCE = BASE_URL + "eresume/index/delete-reference" + POST_URL;

    public static final String CREDIT_RESUME_OBJECTIVES = BASE_URL + "eresume/index/objectives" + POST_URL;
    public static final String CREDIT_RESUME_ADD_OBJECTIVES = BASE_URL + "eresume/index/add-objectives" + POST_URL;


    public static final String CREDIT_RESUME_Achivements = BASE_URL + "eresume/index/achievements" + POST_URL;
    public static final String CREDIT_RESUME_ADD_Achivements = BASE_URL + "eresume/index/add-achievement" + POST_URL;
    public static final String CREDIT_RESUME_EDIT_Achivements = BASE_URL + "eresume/index/edit-achievement" + POST_URL;
    public static final String CREDIT_RESUME_DELETE_Achivements = BASE_URL + "eresume/index/delete-achievement" + POST_URL;

    public static final String CREDIT_RESUME_CURRICULAR = BASE_URL + "eresume/index/curricular" + POST_URL;
    public static final String CREDIT_RESUME_ADD_CURRICULAR = BASE_URL + "eresume/index/add-curricular" + POST_URL;
    public static final String CREDIT_RESUME_EDIT_CURRICULAR = BASE_URL + "eresume/index/edit-curricular" + POST_URL;
    public static final String CREDIT_RESUME_DELETE_CURRICULAR = BASE_URL + "eresume/index/delete-curricular" + POST_URL;

    public static final String CREDIT_RESUME_SKILLS = BASE_URL + "eresume/index/skills" + POST_URL;
    public static final String CREDIT_RESUME_ADD_SKILLS = BASE_URL + "eresume/index/add-skill" + POST_URL;
    public static final String CREDIT_RESUME_EDIT_SKILLS = BASE_URL + "eresume/index/edit-skill" + POST_URL;
    public static final String CREDIT_RESUME_DELETE_SKILLS = BASE_URL + "eresume/index/delete-skill" + POST_URL;

    public static final String CREDIT_RESUME_INTEREST = BASE_URL + "eresume/index/interest" + POST_URL;
    public static final String CREDIT_RESUME_ADD_INTEREST = BASE_URL + "eresume/index/add-interest" + POST_URL;
    public static final String CREDIT_RESUME_EDIT_INTEREST = BASE_URL + "eresume/index/edit-interest" + POST_URL;
    public static final String CREDIT_RESUME_DELETE_INTEREST = BASE_URL + "eresume/index/delete-interest" + POST_URL;

    public static final String CREDIT_RESUME_STRENGTH = BASE_URL + "eresume/index/strength" + POST_URL;
    public static final String CREDIT_RESUME_ADD_STRENGTH = BASE_URL + "eresume/index/add-strength" + POST_URL;
    public static final String CREDIT_RESUME_EDIT_STRENGTH = BASE_URL + "eresume/index/edit-strength" + POST_URL;
    public static final String CREDIT_RESUME_DELETE_STRENGTH = BASE_URL + "eresume/index/delete-strength" + POST_URL;

    public static final String CREDIT_RESUME_HOBBIE = BASE_URL + "eresume/index/hobbie" + POST_URL;
    public static final String CREDIT_RESUME_ADD_HOBBIE = BASE_URL + "eresume/index/add-hobbie" + POST_URL;
    public static final String CREDIT_RESUME_EDIT_HOBBIE = BASE_URL + "eresume/index/edit-hobbie" + POST_URL;
    public static final String CREDIT_RESUME_DELETE_HOBBIE = BASE_URL + "eresume/index/delete-hobbie" + POST_URL;

    public static final String URL_GETUSERID = BASE_URL + "user/index/get-user" + POST_URL;
}
