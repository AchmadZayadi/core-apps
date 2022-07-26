package com.sesolutions.responses.feed;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.FeedLikeResponse;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.responses.videos.Tags;
import com.sesolutions.sesdb.SesConverter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Activity {

    //constructor for google ad
    @Ignore
    public Activity(String contentType) {
        this.contentType = contentType;
    }

    public Activity() {
    }


    @SerializedName("content_type")
    private String contentType = "";
    //Compaign ad vairables starts
    @SerializedName("campaign_id")
    private int campaignId;
    @SerializedName("ad_id")
    private int adId;
    @SerializedName("ad_content")
    private String adContent;
    //Compaign ad vairables ends

    //communityAds vairables starts
    private int userId;
    @SerializedName("ad_type")
    private String adType;
    @SerializedName("header_image")
    private String headerImage;
    private String title;
    private String sponsored;
    @TypeConverters(SesConverter.class)
    @SerializedName("hidden_data")
    private CommunityHiddenData hiddenData;

    @TypeConverters(SesConverter.class)
    @SerializedName("carousel_attachment")
    private List<Attachment> carouselAttachment;
    @TypeConverters(SesConverter.class)
    private List<Options> menus;
    private String url;
    @TypeConverters(SesConverter.class)
    private Attachment seemore;
    //communityAds vairables ends

    //PeopleYouMayKnow variables starts
    @SerializedName("seeall")
    private boolean seeAll;
    @TypeConverters(SesConverter.class)
    @SerializedName("result")
    private List<PeopleSuggestion> peoples;
    //PeopleYouMayKnow variables ends


    @PrimaryKey
    @SerializedName("action_id")
    private int actionId;
    @SerializedName("type")
    private String type;
    @SerializedName("posting_type")
    private int postingType;
    @SerializedName("subject_type")
    private String subjectType;
    @SerializedName("subject_id")
    private int subjectId;
    @SerializedName("object_type")
    private String objectType;
    @SerializedName("object_id")
    private int objectId;
    @SerializedName("body")
    private String body;
    @SerializedName("privacy")
    private String privacy;
    // TODO: 15/11/17 ignoring params key for now
  /*  @SerializedName("params")
    private Params params;*/
    @SerializedName("date")
    private String date;
    @SerializedName("attachment_count")
    private int attachmentCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("like_count")
    private int likeCount;
    /* @SerializedName("commentable")
     private int commentable;*/
    @SerializedName("reaction_id")
    private int reactionId;
    @SerializedName("schedule_time")
    private String scheduleTime;
    @SerializedName("modified_date")
    private String modifiedDate;
    @SerializedName("feedbg_id")
    private int feedbgId;
    @SerializedName("maxactionid")
    private int maxactionid;
    @SerializedName("group_action_id")
    private String groupActionId;
    @SerializedName("feedLink")
    private String feedlink;
    @SerializedName("guid")
    private String guid;

    private boolean comment_disable;
    private boolean can_comment;
    private boolean is_like;
    @SerializedName("can_share")
    private int canShare;
    private String reactionUserData;
    private String activityTypeContent;
    private String privacyImageUrl;
    private String activityIcon;
    @TypeConverters(SesConverter.class)
    private Poll poll;
    @TypeConverters(SesConverter.class)
    private Attachment attachment;
    @TypeConverters(SesConverter.class)
    @SerializedName("item_user")
    private Item_user itemUser;
    @TypeConverters(SesConverter.class)
    private List<String> hashTags;
    @TypeConverters(SesConverter.class)
    private List<Mention> mention;
    @TypeConverters(SesConverter.class)
    private CommentData comment;
    private Like like;
    private Share share;
    @TypeConverters(SesConverter.class)
    private List<Options> options;
    @TypeConverters(SesConverter.class)
    private List<ActivityType> activityType;
    @SerializedName("font-size")
    private int fornSize;
    @SerializedName("cover_photo_url")
    private String coverPhotoUrl;
    private String bg_image;
    @TypeConverters(SesConverter.class)
    private Feelings feelings;
    @TypeConverters(SesConverter.class)
    private LocationActivity locationActivity;
    @TypeConverters(SesConverter.class)
    private List<Tagged> tagged;
    private boolean hidden;
    private boolean reported;
    @TypeConverters(SesConverter.class)
    private List<ReactionPlugin> reactionData;
    private String hashTagString;
    @TypeConverters(SesConverter.class)
    private List<Tags> activityTags;
    @TypeConverters(SesConverter.class)
    @SerializedName(value = Constant.ResourceType.PAGE, alternate = {Constant.ResourceType.GROUP, Constant.ResourceType.BUSINESS})
    private Attribution pageAttribution;
   /* @TypeConverters(SesConverter.class)
    @SerializedName(Constant.ResourceType.GROUP)
    private Attribution groupAttribution;
    @TypeConverters(SesConverter.class)
    @SerializedName(Constant.ResourceType.BUSINESS)
    private Attribution businessAttribution;*/

    @SerializedName("post_attribution")
    private String postAttribution;

    @SerializedName("boost_post_label")
    private String boostPostLabel;
    @SerializedName("boost_post_url")
    private String boostPostUrl;

    @SerializedName("gif_url")
    private String gif_url;

    @SerializedName("gif_id")
    private String gif_id;



    @SerializedName("level_id")
    private int levelId = 0;

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }
    public String getGif_url() {
        return gif_url;
    }

    public String getGif_id() {
        return gif_id;
    }

    public void setGif_url(String gif_url) {
        this.gif_url = gif_url;
    }

    public void setGif_id(String gif_id) {
        this.gif_id = gif_id;
    }

    public String getBoostPostLabel() {
        return boostPostLabel;
    }

    public String getBoostPostUrl() {
        return boostPostUrl;
    }

    public String getPostAttribution() {
        return postAttribution;
    }

    public boolean isSeeAll() {
        return seeAll;
    }

    public List<PeopleSuggestion> getPeoples() {
        return peoples;
    }


    public String getContentType() {
        return contentType;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public int getAdId() {
        return adId;
    }

    public void setAdId(int adId) {
        this.adId = adId;
    }

    public String getAdContent() {
        return adContent;
    }

    public void setAdContent(String adContent) {
        this.adContent = adContent;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Poll getPoll() {
        return poll;
    }

    public String getHashTagString() {
        return hashTagString;
    }

    public void setHashTagString(String hashTagString) {
        this.hashTagString = hashTagString;
    }

    public List<Tags> getActivityTags() {
        return activityTags;
    }

    public void setActivityTags(List<Tags> activityTags) {
        this.activityTags = activityTags;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }

    public List<ReactionPlugin> getReactionData() {
        if (null == reactionData)
            reactionData = new ArrayList<>();
        return reactionData;
    }

    public void setReactionData(List<ReactionPlugin> reactionData) {
        this.reactionData = reactionData;
    }

    public String getBg_image() {
        return bg_image;
    }

    public void setBg_image(String bg_image) {
        this.bg_image = bg_image;
    }

    public List<Tagged> getTagged() {
        return tagged;
    }

    public void setTagged(List<Tagged> tagged) {
        this.tagged = tagged;
    }

    public LocationActivity getLocationActivity() {
        return locationActivity;
    }

    public void setLocationActivity(LocationActivity locationActivity) {
        this.locationActivity = locationActivity;
    }

    public Feelings getFeelings() {
        return feelings;
    }

    public void setFeelings(Feelings feelings) {
        this.feelings = feelings;
    }

    public int getFornSize() {
        if (fornSize > 0) return /*fornSize = fornSize - 2;//*/(fornSize * 4) / 5;
        return fornSize;
    }

    public void setFornSize(int fornSize) {
        this.fornSize = fornSize;
    }

    public boolean isComment_disable() {
        return comment_disable;
    }

    public void setComment_disable(boolean comment_disable) {
        this.comment_disable = comment_disable;
    }

    public boolean isCan_comment() {
        return can_comment;
    }

    public void setCan_comment(boolean can_comment) {
        this.can_comment = can_comment;
    }

    public boolean isIs_like() {
        return is_like;
    }

    public void setIs_like(boolean is_like) {
        this.is_like = is_like;
    }

    public int getCanShare() {
        return canShare;
    }

    public String getReactionUserData() {
        return reactionUserData;
    }


    public String getActivityTypeContent() {
        return activityTypeContent;
    }

    public void setActivityTypeContent(String activityTypeContent) {
        this.activityTypeContent = activityTypeContent;
    }

    public String getPrivacyImageUrl() {
        return privacyImageUrl;
    }

    public void setPrivacyImageUrl(String privacyImageUrl) {
        this.privacyImageUrl = privacyImageUrl;
    }

    public String getActivityIcon() {
        return activityIcon;
    }

    public void setActivityIcon(String activityIcon) {
        this.activityIcon = activityIcon;
    }


    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public Item_user getItemUser() {
        if (null == itemUser) {
            itemUser = new Item_user(0, "", "");
        }
        return itemUser;
    }

    public List<String> getHashTags() {
        return hashTags;
    }

    public void setHashTags(List<String> hashTags) {
        this.hashTags = hashTags;
    }

    public List<Mention> getMention() {
        return mention;
    }

    public void setMention(List<Mention> mention) {
        this.mention = mention;
    }

    public Like getLike() {
        return like;
    }

    public void setLike(Like like) {
        this.like = like;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public List<Options> getOptions() {
        return options;
    }

    public void setOptions(List<Options> options) {
        this.options = options;
    }

    public List<ActivityType> getActivityType() {
        return activityType;
    }

    public void setActivityType(List<ActivityType> activityType) {
        this.activityType = activityType;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPostingType() {
        return postingType;
    }

    public void setPostingType(int postingType) {
        this.postingType = postingType;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean getCommentable() {
        return !comment_disable && can_comment;
    }

    public int getReactionId() {
        return reactionId;
    }

    public void setReactionId(int reactionId) {
        this.reactionId = reactionId;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getFeedbgId() {
        return feedbgId;
    }

    public void setFeedbgId(int feedbgId) {
        this.feedbgId = feedbgId;
    }

    public int getMaxactionid() {
        return maxactionid;
    }

    public void setMaxactionid(int maxactionid) {
        this.maxactionid = maxactionid;
    }

    public String getGroupActionId() {
        return groupActionId;
    }

    public void setGroupActionId(String groupActionId) {
        this.groupActionId = groupActionId;
    }

    public String getFeedlink() {
        return feedlink;
    }

    public void setFeedlink(String feedlink) {
        this.feedlink = feedlink;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public boolean isReported() {
        return reported;
    }

    public void toggleCommantable() {
        comment_disable = !comment_disable;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSponsored(String sponsored) {
        this.sponsored = sponsored;
    }

    public void setHiddenData(CommunityHiddenData hiddenData) {
        this.hiddenData = hiddenData;
    }

    public void setCarouselAttachment(List<Attachment> carouselAttachment) {
        this.carouselAttachment = carouselAttachment;
    }

    public void setMenus(List<Options> menus) {
        this.menus = menus;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSeemore(Attachment seemore) {
        this.seemore = seemore;
    }

    public void setSeeAll(boolean seeAll) {
        this.seeAll = seeAll;
    }

    public void setPeoples(List<PeopleSuggestion> peoples) {
        this.peoples = peoples;
    }

    public void setCanShare(int canShare) {
        this.canShare = canShare;
    }

    public void setReactionUserData(String reactionUserData) {
        this.reactionUserData = reactionUserData;
    }

    public void setItemUser(Item_user itemUser) {
        this.itemUser = itemUser;
    }

    public void setComment(CommentData comment) {
        this.comment = comment;
    }

    public Attribution getPageAttribution() {
        return pageAttribution;
    }

    public void setPageAttribution(Attribution pageAttribution) {
        this.pageAttribution = pageAttribution;
    }

    public void setPostAttribution(String postAttribution) {
        this.postAttribution = postAttribution;
    }

    public void setBoostPostLabel(String boostPostLabel) {
        this.boostPostLabel = boostPostLabel;
    }

    public void setBoostPostUrl(String boostPostUrl) {
        this.boostPostUrl = boostPostUrl;
    }

    public void toggleLike(ReactionPlugin reactionVo) {
        if (is_like) {
            is_like = false;
            like = null;
        } else {
            is_like = true;
            like = new Like(reactionVo.getImage(), reactionVo.getTitle());
            // like.setType(reactionVo.get());
        }
    }

    public void updateLikeTemp(boolean isLike, Like likeVo) {
        like = likeVo;
        is_like = isLike;
    }

    public void updateFinalLike(FeedLikeResponse.Result result) {
        like = result.like;
        is_like = result.is_like;
        reactionData = result.reactionData;
        reactionUserData = result.reactionUserData;
    }

    public String getAttributionImage() {
        try {
            return pageAttribution.getPhoto();
            /*switch (postAttribution) {
                case Constant.ResourceType.PAGE:
                    return pageAttribution.getPhoto();
                case Constant.ResourceType.GROUP:
                    return groupAttribution.getPhoto();
                case Constant.ResourceType.BUSINESS:
                    return businessAttribution.getPhoto();
                default:
                    return null;

            }*/
        } catch (Exception e) {
            CustomLog.e(e);
            return null;
        }

    }

    public String getAttributionGuid() {
        try {
            if (postAttribution != null) {
                return pageAttribution.getSelectedGuid();
               /* switch (postAttribution) {
                    case Constant.ResourceType.PAGE:
                        return pageAttribution.getSelectedGuid();
                    case Constant.ResourceType.GROUP:
                        return groupAttribution.getSelectedGuid();
                    case Constant.ResourceType.BUSINESS:
                        return businessAttribution.getSelectedGuid();
                    default:
                        return null;

                }*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return null;

    }

    public void updateAttribution(Attribution vo) {
        try {

            pageAttribution.setSelectedGuid(vo.getGuid());
            pageAttribution.setSelectedType(vo.getGuid().split("_")[0]);
            pageAttribution.setSelectedId(vo.getGuid().split("_")[1]);
            pageAttribution.setPhoto(vo.getPhoto());
            pageAttribution.setTitle(vo.getTitle());


            /*switch (postAttribution) {
                case Constant.ResourceType.PAGE:
                    pageAttribution.setSelectedGuid(vo.getGuid());
                    pageAttribution.setSelectedType(vo.getGuid().split("_")[0]);
                    pageAttribution.setSelectedId(vo.getGuid().split("_")[1]);
                    pageAttribution.setPhoto(vo.getPhoto());
                    pageAttribution.setTitle(vo.getTitle());

                    break;
                case Constant.ResourceType.GROUP:
                    groupAttribution.setSelectedGuid(vo.getGuid());
                    groupAttribution.setSelectedType(vo.getGuid().split("_")[0]);
                    groupAttribution.setSelectedId(vo.getGuid().split("_")[1]);
                    groupAttribution.setPhoto(vo.getPhoto());
                    groupAttribution.setTitle(vo.getTitle());
                    break;
                case Constant.ResourceType.BUSINESS:
                    businessAttribution.setSelectedGuid(vo.getGuid());
                    businessAttribution.setSelectedType(vo.getGuid().split("_")[0]);
                    businessAttribution.setSelectedId(vo.getGuid().split("_")[1]);
                    businessAttribution.setPhoto(vo.getPhoto());
                    businessAttribution.setTitle(vo.getTitle());
                    break;

            }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private boolean canUpdate;

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public boolean canUpdate() {
        return canUpdate;
    }

    public void setPoll(Poll poll) {
        this.canUpdate = true;
        this.poll = poll;
    }

    public int getUserId() {
        return userId;
    }

    public String getAdType() {
        return adType;
    }

    public String getHeaderImage() {
        if (null != headerImage) {
            return headerImage;
        } else {
            return itemUser != null ? itemUser.getUser_image() : null;
        }
    }

    public String getTitle() {
        if (null != title) {
            return title;
        } else {
            return itemUser != null ? itemUser.getTitle() : null;
        }
    }

    public String getSponsored() {
        return sponsored;
    }

    public CommunityHiddenData getHiddenData() {
        return hiddenData;
    }

    public Attachment getSeemore() {
        return seemore;
    }

    public List<Attachment> getCarouselAttachment() {
        if (null != seemore) {
            List<Attachment> list = new ArrayList<>(carouselAttachment);
            list.add(seemore);
            return list;
        }
        return carouselAttachment;
    }

    public List<Options> getMenus() {
        //if ad is a boost post then menu items will come in key *options*
        if (options != null) {
            return options;
        }
        return menus;
    }

    public String getUrl() {
        return url;
    }

    public CommentData getComment() {
//        return null;
        return comment;
    }

    public boolean canShowDateAndType() {
        //custom logic to show/hide date layout
        return null == sponsored && actionId != 1;
    }
}
