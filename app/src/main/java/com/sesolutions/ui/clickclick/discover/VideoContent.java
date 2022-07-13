package com.sesolutions.ui.clickclick.discover;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.page.Locations;
import com.sesolutions.responses.videos.Tags;
import com.sesolutions.utils.CustomLog;

import java.util.List;

public class VideoContent {

    //custom variable to show animation
    private int showAnimation;

    @SerializedName("category_title")
    private String category_title;
    @SerializedName("currency")
    private String currency;
    @SerializedName("owner_title")
    private String owner_title;
    @SerializedName("lng")
    private String lng;
    @SerializedName("lat")
    private String lat;
    @SerializedName("auto_approve")
    private int auto_approve;
    @SerializedName("modified_date")
    private String modified_date;
    @SerializedName("creation_date")
    private String creation_date;
    @SerializedName("offtheday")
    private int offtheday;
    @SerializedName("status")
    private int status;
    @SerializedName("approval")
    private int approval;
    @SerializedName("is_approved")
    private int is_approved;
    @SerializedName("verified")
    private int verified;
    @SerializedName("hot")
    private int hot;
    @SerializedName("sponsored")
    private int sponsored;
    @SerializedName("featured")
    private int featured;
    @SerializedName("member_count")
    private int member_count;
    @SerializedName("follow_count")
    private int follow_count;
    @SerializedName("favourite_count")
    private int favourite_count;
    @SerializedName("comment_count")
    private int comment_count;
    @SerializedName("like_count")
    private int like_count;
    @SerializedName("view_count")
    private int view_count;
    @SerializedName("seo_keywords")
    private String seo_keywords;
    @SerializedName("page_contact_pinterest")
    private String page_contact_pinterest;
    @SerializedName("page_contact_instagram")
    private String page_contact_instagram;
    @SerializedName("page_contact_linkedin")
    private String page_contact_linkedin;
    @SerializedName("page_contact_twitter")
    private String page_contact_twitter;
    @SerializedName("page_contact_facebook")
    private String page_contact_facebook;
    @SerializedName("page_contact_website")
    private String page_contact_website;
    @SerializedName("page_contact_phone")
    private String page_contact_phone;
    @SerializedName("page_contact_email")
    private String page_contact_email;
    @SerializedName("page_contact_name")
    private String page_contact_name;
    @SerializedName("view_privacy")
    private String view_privacy;
    @SerializedName("draft")
    private int draft;
    @SerializedName("search")
    private int search;
    @SerializedName("cover_position")
    private String cover_position;
    @SerializedName("can_invite")
    private int can_invite;
    @SerializedName("other_tag")
    private int other_tag;
    @SerializedName("member_title_plural")
    private String member_title_plural;
    @SerializedName("member_title_singular")
    private String member_title_singular;
    @SerializedName("can_join")
    private int can_join;
    @SerializedName("price_type")
    private int price_type;
    @SerializedName("price")
    private String price;
    @SerializedName("pagestyle")
    private int pagestyle;
    @SerializedName("category_id")
    private int category_id;
    @SerializedName("custom_url")
    private String custom_url;
    @SerializedName("description")
    private String description;
    @SerializedName("title")
    private String title;
    @SerializedName("resource_id")
    private int resource_id;
    @SerializedName("owner_id")
    private int owner_id;
    @SerializedName("page_id")
    private int page_id;
    private List<VideoContent> videos;

    private List<Tags> tag;
    private Images images;
    @SerializedName("cover_image")
    private Images coverImage;
    private JsonElement location;
    private boolean showLoginForm;
    @SerializedName("showloginform_for_join_share")
    private boolean showLoginFormForJoin;
    private Share share;
    private List<Options> buttons;
    private List<Options> join;

    @SerializedName("is_content_like")
    private String isContentLike;
    // @SerializedName("content_like_count")
    //private int contentLikeCount;
    @SerializedName("is_content_favourite")
    private String isContentFavourite;
    @SerializedName("is_content_follow")
    private String isContentFollow;
    private List<Options> updateProfilePhoto;
    private List<Options> updateCoverPhoto;
    private boolean likeFollowIntegrate;


    public boolean hasToChangeFollowLike() {
        return likeFollowIntegrate && !isContentFollow() && !isContentLike();
    }

    public List<Options> getUpdateProfilePhoto() {
        return updateProfilePhoto;
    }

    public List<Options> getUpdateCoverPhoto() {
        return updateCoverPhoto;
    }

    public boolean isContentLike() {
        return "true".equals(isContentLike);
    }

    public boolean canLike() {
        return null != isContentLike;
    }

    public boolean canFavourite() {
        return null != isContentFavourite;
    }

    public boolean canFollow() {
        return null != isContentFollow;
    }

    public boolean isContentFavourite() {
        return "true".equals(isContentFavourite);
    }

    public boolean isContentFollow() {
        return "true".equals(isContentFollow);
    }


    public void setContentLike(boolean contentLike) {
        isContentLike = String.valueOf(contentLike);
    }

    public void setContentFavourite(boolean contentFavourite) {
        isContentFavourite = String.valueOf(contentFavourite);
    }

    public Options getFirstJoinOption() {
        if (can_join != 0 && join != null && join.size() > 0) {
            return join.get(0);
        }
        return null;
    }
    public List<VideoContent> getVideos() {
        return videos;
    }

    public int isShowAnimation() {
        return showAnimation;
    }

    public void setShowAnimation(int showAnimation) {
        this.showAnimation = showAnimation;
    }

    public void setContentFollow(boolean contentFollow) {
        isContentFollow = String.valueOf(contentFollow);
    }

    public List<Options> getButtons() {
        return buttons;
    }

    public void setButtons(List<Options> buttons) {
        this.buttons = buttons;
    }

    public Share getShare() {
        return share;
    }

    public boolean isShowLoginForm() {
        return showLoginForm;
    }

    public boolean canShowLoginFormForJoin() {
        return showLoginFormForJoin;
    }

    public String getLocation() {
        try {
            if (null != location) {
                if (location.isJsonObject()) {
                    return new Gson().fromJson(location, Locations.class).getFullAddress();
                } else {
                    return location.getAsString();
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);

        }
        return null;
    }

    public JsonElement getLocationObject() {
        return location;

    }

    public List<Tags> getTag() {
        return tag;
    }

    public Images getImages() {
        return images;
    }

    public String getImageUrl() {
        if (images != null) {
            if (null != images.getNormal()) return images.getNormal();
            else return images.getMain();
        } else {
            return "";
        }
    }

    public String getMainImageUrl() {
        if (images != null) {
            return images.getMain();
        } else {
            return "";
        }
    }

    public String getCoverImageUrl() {
        if (coverImage != null) {
            return coverImage.getNormal();
        } else
            return "";
    }

    public Images getCoverImage() {
        return coverImage;
    }

    public String getCategory_title() {
        return category_title;
    }

    public String getCurrency() {
        return currency;
    }

    public String getOwner_title() {
        return owner_title;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    public int getAuto_approve() {
        return auto_approve;
    }

    public String getModified_date() {
        return modified_date;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public int getOfftheday() {
        return offtheday;
    }

    public int getStatus() {
        return status;
    }

    public int getApproval() {
        return approval;
    }

    public int getIs_approved() {
        return is_approved;
    }

    public int getVerified() {
        return verified;
    }

    public int getHot() {
        return hot;
    }

    public int getSponsored() {
        return sponsored;
    }

    public int getFeatured() {
        return featured;
    }

    public int getMember_count() {
        return member_count;
    }

    public int getFollow_count() {
        return follow_count;
    }

    public int getFavourite_count() {
        return favourite_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public int getView_count() {
        return view_count;
    }

    public String getSeo_keywords() {
        return seo_keywords;
    }

    public String getPage_contact_pinterest() {
        return page_contact_pinterest;
    }

    public String getPage_contact_instagram() {
        return page_contact_instagram;
    }

    public String getPage_contact_linkedin() {
        return page_contact_linkedin;
    }

    public String getPage_contact_twitter() {
        return page_contact_twitter;
    }

    public String getPage_contact_facebook() {
        return page_contact_facebook;
    }

    public String getPage_contact_website() {
        return page_contact_website;
    }

    public String getPage_contact_phone() {
        return page_contact_phone;
    }

    public String getPage_contact_email() {
        return page_contact_email;
    }

    public String getPage_contact_name() {
        return page_contact_name;
    }

    public String getView_privacy() {
        return view_privacy;
    }

    public int getDraft() {
        return draft;
    }

    public int getSearch() {
        return search;
    }

    public String getCover_position() {
        return cover_position;
    }

    public int getCan_invite() {
        return can_invite;
    }

    public int getOther_tag() {
        return other_tag;
    }

    public String getMember_title_plural() {
        return member_title_plural;
    }

    public String getMember_title_singular() {
        return member_title_singular;
    }

    public int getCan_join() {
        return can_join;
    }

    public int getPrice_type() {
        return price_type;
    }

    public String getPrice() {
        return price;
    }

    public int getPagestyle() {
        return pagestyle;
    }

    public int getCategory_id() {
        return category_id;
    }

    public String getCustom_url() {
        return custom_url;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public int getResource_id() {
        return resource_id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public int getPage_id() {
        return page_id;
    }

    /*public void updateButtons(int optionPosition, Options opt) {
        for (int i = 0; i < buttons.size(); i++) {
            if (i == optionPosition) {
                buttons.get(optionPosition).setLabel(opt.getLabel());
                buttons.get(optionPosition).setName(opt.getName());
                buttons.get(optionPosition).setValue(opt.getValue());
                break;
            }
        }
    }*/
}

