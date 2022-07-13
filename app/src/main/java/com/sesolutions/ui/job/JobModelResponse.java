package com.sesolutions.ui.job;

import com.google.gson.annotations.SerializedName;

public class JobModelResponse {


    @SerializedName("result")
    public ResultDTO result;
    @SerializedName("session_id")
    public String sessionId;

    public static class ResultDTO {
        @SerializedName("blog")
        public BlogDTO blog;
        @SerializedName("loggedin_user_id")
        public int loggedinUserId;

        public static class BlogDTO {
            @SerializedName("blog_id")
            public int blogId;
            @SerializedName("custom_url")
            public String customUrl;
            @SerializedName("parent_id")
            public int parentId;
            @SerializedName("photo_id")
            public int photoId;
            @SerializedName("title")
            public String title;
            @SerializedName("body")
            public String body;
            @SerializedName("location")
            public String location;
            @SerializedName("owner_type")
            public String ownerType;
            @SerializedName("owner_id")
            public int ownerId;
            @SerializedName("category_id")
            public int categoryId;
            @SerializedName("creation_date")
            public String creationDate;
            @SerializedName("modified_date")
            public String modifiedDate;
            @SerializedName("publish_date")
            public String publishDate;
            @SerializedName("starttime")
            public String starttime;
            @SerializedName("endtime")
            public String endtime;
            @SerializedName("view_count")
            public int viewCount;
            @SerializedName("comment_count")
            public int commentCount;
            @SerializedName("like_count")
            public int likeCount;
            @SerializedName("blog_contact_name")
            public String blogContactName;
            @SerializedName("blog_contact_email")
            public String blogContactEmail;
            @SerializedName("blog_contact_phone")
            public String blogContactPhone;
            @SerializedName("blog_contact_website")
            public String blogContactWebsite;
            @SerializedName("blog_contact_facebook")
            public String blogContactFacebook;
            @SerializedName("parent_type")
            public String parentType;
            @SerializedName("seo_keywords")
            public String seoKeywords;
            @SerializedName("featured")
            public int featured;
            @SerializedName("sponsored")
            public int sponsored;
            @SerializedName("verified")
            public int verified;
            @SerializedName("is_approved")
            public int isApproved;
            @SerializedName("ip_address")
            public String ipAddress;
            @SerializedName("favourite_count")
            public int favouriteCount;
            @SerializedName("offtheday")
            public int offtheday;
            @SerializedName("style")
            public int style;
            @SerializedName("rating")
            public int rating;
            @SerializedName("search")
            public int search;
            @SerializedName("draft")
            public int draft;
            @SerializedName("is_publish")
            public int isPublish;
            @SerializedName("readtime")
            public String readtime;
            @SerializedName("ssesblog_id")
            public int ssesblogId;
            @SerializedName("resource_type")
            public String resourceType;
            @SerializedName("resource_id")
            public int resourceId;
            @SerializedName("cotinuereading")
            public int cotinuereading;
            @SerializedName("continue_height")
            public int continueHeight;
            @SerializedName("package_id")
            public int packageId;
            @SerializedName("transaction_id")
            public int transactionId;
            @SerializedName("existing_package_order")
            public int existingPackageOrder;
            @SerializedName("orderspackage_id")
            public int orderspackageId;
            @SerializedName("owner_title")
            public String ownerTitle;
            @SerializedName("tags")
            public TagsDTO tags;
            @SerializedName("category_title")
            public String categoryTitle;
            @SerializedName("content_url")
            public String contentUrl;
            @SerializedName("can_favorite")
            public boolean canFavorite;
            @SerializedName("can_share")
            public boolean canShare;
            @SerializedName("share")
            public ShareDTO share;
            @SerializedName("subscribe")
            public SubscribeDTO subscribe;
            @SerializedName("blog_images")
            public BlogImagesDTO blogImages;
            @SerializedName("user_images")
            public String userImages;

            public static class TagsDTO {
                @SerializedName("69")
                public String $69;
                @SerializedName("70")
                public String $70;
                @SerializedName("71")
                public String $71;
                @SerializedName("72")
                public String $72;
                @SerializedName("73")
                public String $73;
                @SerializedName("74")
                public String $74;
            }

            public static class ShareDTO {
                @SerializedName("name")
                public String name;
                @SerializedName("label")
                public String label;
                @SerializedName("imageUrl")
                public String imageUrl;
                @SerializedName("url")
                public String url;
                @SerializedName("title")
                public String title;
                @SerializedName("description")
                public String description;
                @SerializedName("urlParams")
                public UrlParamsDTO urlParams;

                public static class UrlParamsDTO {
                    @SerializedName("type")
                    public String type;
                    @SerializedName("id")
                    public int id;
                }
            }

            public static class SubscribeDTO {
                @SerializedName("label")
                public String label;
                @SerializedName("user_id")
                public int userId;
                @SerializedName("action")
                public String action;
            }

            public static class BlogImagesDTO {
                @SerializedName("main")
                public String main;
                @SerializedName("icon")
                public String icon;
                @SerializedName("normal")
                public String normal;
                @SerializedName("normalmain")
                public String normalmain;
            }
        }
    }
}
