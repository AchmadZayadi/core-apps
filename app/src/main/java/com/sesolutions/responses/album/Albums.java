package com.sesolutions.responses.album;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.Owner;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Like;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.responses.videos.Tags;
import com.sesolutions.utils.SpanUtil;

import java.util.ArrayList;
import java.util.List;

public class Albums {

    @SerializedName("album_id")
    private int albumId;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("owner_type")
    private String ownerType;
    @SerializedName("owner_id")
    private int ownerId;
    private Owner owner;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("modified_date")
    private String modifiedDate;

    @SerializedName("category_title")
    private String categoryTitle;

    @SerializedName("photo_id")
    private int photoId;
    @SerializedName("view_count")
    private int viewCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("search")
    private int search;
    @SerializedName("view_privacy")
    private String viewPrivacy;
    private Like like;
    /*   @SerializedName("rating")
       private JsonElement rating;*/
    @SerializedName("is_locked")
    private int isLocked;
    @SerializedName("offtheday")
    private int offtheday;
    @SerializedName("ic_location")
    private String location;

    @SerializedName("location")
    private String location_ad;

    public String getLocation_ad() {
        return location_ad;
    }

    public void setLocation_ad(String location_ad) {
        this.location_ad = location_ad;
    }

    @SerializedName("is_featured")
    private int isFeatured;
    @SerializedName("is_sponsored")
    private int isSponsored;
    @SerializedName("favourite_count")
    private int favouriteCount;
    @SerializedName("follow_count")
    private int followCount;
    @SerializedName("art_cover")
    private int artCover;
    @SerializedName("subcat_id")
    private int subcatId;
    @SerializedName("subsubcat_id")
    private int subsubcatId;
    @SerializedName("download_count")
    private int downloadCount;
    @SerializedName("ip_address")
    private String ipAddress;
    @SerializedName("adult")
    private int adult;
    @SerializedName("cover")
    private JsonElement cover;
    @SerializedName("draft")
    private int draft;
    @SerializedName("photo_count")
    private int photoCount;
    @SerializedName("user_title")
    private String userTitle;
    @SerializedName("user_image")
    private JsonElement userImage;
    @SerializedName("images")
    private final JsonElement images;
    private Images photos;
    private String main;
    @SerializedName("is_content_like")
    private boolean isContentLike;
    @SerializedName("is_like")
    private boolean isLike;
    @SerializedName("is_content_favourite")
    private boolean isContentFavourite;

    private Share share;
    private Share shareData;

    private Permission permission;
    @SerializedName("cover_pic")
    private JsonElement coverPic;
    private List<Tags> albumTags;
    private List<Tags> tags; //coming in gallary
    private int canDownload;
    @SerializedName("cover_image_options")
    private List<Options> coverImageOptions;
    @SerializedName("resource_type")
    private String resource_type;//":"sespage_photo",
    @SerializedName("resource_id")
    private int resource_id;//":88,


    //used in page view -> bottom tabs [Info]
    @SerializedName("user_id")
    private int userId;
    @SerializedName("page_id")
    private int pageId;
    @SerializedName("group_id")
    private int groupId;
    @SerializedName("business_id")
    private int businessId;
    @SerializedName("store_id")
    private int storeId;
    private String name;

    private String reactionUserData;
    private List<ReactionPlugin> reactionData;
    private int fileId;


    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public Albums(Images images) {
        this.images = new Gson().toJsonTree(images);
    }


    public int getGroupId() {
        return groupId;
    }

    public int getBusinessId() {
        return businessId;
    }

    public int getStoreId(){ return storeId;}

    public String getResource_type() {
        return resource_type;
    }

    public int getResource_id() {
        return resource_id;
    }

    public int getUserId() {
        return userId;
    }

    public int getPageId() {
        return pageId;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getReactionUserData() {
        return reactionUserData;
    }

    public void setReactionUserData(String reactionUserData) {
        this.reactionUserData = reactionUserData;
    }

    public List<ReactionPlugin> getReactionData() {
        return reactionData;
    }

    public void setReactionData(List<ReactionPlugin> reactionData) {
        this.reactionData = reactionData;
    }

    public int getFollowCount() {
        return followCount;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public Like getLike() {
        return like;
    }

    public void setLike(Like like) {
        this.like = like;
    }

    public Images getPhotos() {
        return photos;
    }

    public void setPhotos(Images photos) {
        this.photos = photos;
    }

    public Share getShareData() {
        return shareData;
    }

    public void setShareData(Share shareData) {
        this.shareData = shareData;
    }

    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

  /*  public String getUserImage() {
        return userImage;
    }*/

    public String getUserImage() {
        if (null != userImage) {
            if (userImage.isJsonObject()) {
                return new Gson().fromJson(userImage, Images.class).getNormal();
            } else {
                return userImage.getAsString();
            }
        }
        return null;
    }


    public List<Options> getCoverImageOptions() {
        return coverImageOptions;
    }

    public void setCoverImageOptions(List<Options> coverImageOptions) {
        this.coverImageOptions = coverImageOptions;
    }

    public boolean isContentLike() {
        return isContentLike;
    }

    public void setContentLike(boolean contentLike) {
        isContentLike = contentLike;
    }

    public boolean isContentFavourite() {
        return isContentFavourite;
    }

    public void setContentFavourite(boolean contentFavourite) {
        isContentFavourite = contentFavourite;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private List<String> getCoverPic() {
        if (null == coverPic) return null;
        List<String> cover = new ArrayList<>();
        if (coverPic.isJsonArray()) {
            JsonArray arr = coverPic.getAsJsonArray();
            for (int i = 0; i < arr.size(); i++) {
                cover.add(arr.get(i).getAsString());
            }
        } else {
            cover.add(coverPic.getAsString());
        }
        return cover;
    }

    public String getFirstCover() {
        List<String> list = getCoverPic();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return "";
    }

    public void setCoverPic(JsonElement coverPic) {
        this.coverPic = coverPic;
    }

    public List<Tags> getAlbumTags() {
        return albumTags;
    }

    public void setAlbumTags(List<Tags> albumTags) {
        this.albumTags = albumTags;
    }

    public int getCanDownload() {
        return canDownload;
    }

    public void setCanDownload(int canDownload) {
        this.canDownload = canDownload;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getImageUrl() {
        if (null != images && images.isJsonObject()) {
            Images img = new Gson().fromJson(images, Images.class);
            return img.getNormal();
        }
        return null;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return SpanUtil.getHtmlString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
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

    public int getSearch() {
        return search;
    }

    public void setSearch(int search) {
        this.search = search;
    }

    public String getViewPrivacy() {
        return viewPrivacy;
    }

    public void setViewPrivacy(String viewPrivacy) {
        this.viewPrivacy = viewPrivacy;
    }

    public int getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(int isLocked) {
        this.isLocked = isLocked;
    }

    public int getOfftheday() {
        return offtheday;
    }

    public void setOfftheday(int offtheday) {
        this.offtheday = offtheday;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(int isFeatured) {
        this.isFeatured = isFeatured;
    }

    public int getIsSponsored() {
        return isSponsored;
    }

    public void setIsSponsored(int isSponsored) {
        this.isSponsored = isSponsored;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(int favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    public int getArtCover() {
        return artCover;
    }

    public void setArtCover(int artCover) {
        this.artCover = artCover;
    }

    public int getSubcatId() {
        return subcatId;
    }

    public void setSubcatId(int subcatId) {
        this.subcatId = subcatId;
    }

    public int getSubsubcatId() {
        return subsubcatId;
    }

    public void setSubsubcatId(int subsubcatId) {
        this.subsubcatId = subsubcatId;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public Images getCover() {
        if (null != cover && cover.isJsonObject()) {
            return new Gson().fromJson(cover.toString(), Images.class);
        }
        return new Images();
    }

    public void setCover(JsonElement cover) {
        this.cover = cover;
    }

    public int getDraft() {
        return draft;
    }

    public void setDraft(int draft) {
        this.draft = draft;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public Images getImages() {
        if (null != images) {
            if (images.isJsonObject()) {
                return new Gson().fromJson(images, Images.class);
            } else {
                Images vo = new Images();
                vo.setMain(images.getAsString());
                return vo;
            }
        }
        return null;
    }

    public String getPhotoUrl() {
        if (null != images) {
            if (images.isJsonObject()) {
                return new Gson().fromJson(images, Images.class).getNormal();
            } else {
                return images.getAsString();
            }
        }
        return null;
    }

    public String getFundPhoto() {
        if (null != photos)
            return photos.getNormal();
        return "";
    }

 /*   public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }*/

    public String getName() {
        return name;
    }

    public int getFileId() {
        return fileId;
    }

    public String getStatsString(boolean isSesPlugin) {
        if (!isSesPlugin)
            return "\uf164 " + getLikeCount()
                    + "  \uf075 " + getCommentCount()
                    + "  \uf06e " + getViewCount()
                    + "  \uf004 " + getFavouriteCount()
                    + "  \uf03e " + getPhotoCount();
        return "\uf06e " + getViewCount()
                + "  \uf03e " + getPhotoCount();
    }
}
