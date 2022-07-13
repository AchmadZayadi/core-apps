package com.sesolutions.responses.music;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.page.Shortcuts;
import com.sesolutions.utils.SpanUtil;

import java.util.List;

public class AlbumView {
    @SerializedName("artist_id")
    private int artistId;
    @SerializedName("album_id")
    private int albumId;
    @SerializedName("albumsong_id")
    private int songId;
    @SerializedName("playlist_id")
    private int playlistId;
    @SerializedName("owner_id")
    private int ownerId;
    @SerializedName("owner_type")
    private String ownerType;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("subcat_id")
    private int subcatId;
    @SerializedName("subsubcat_id")
    private int subsubcatId;
    @SerializedName("title")
    private String title;
    @SerializedName("photo_id")
    private int photoId;
    @SerializedName("album_cover")
    private int albumCover;
    @SerializedName("search")
    private int search;
    @SerializedName("profile")
    private int profile;
    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("modified_date")
    private String modifiedDate;
    @SerializedName("view_count")
    private int viewCount;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("song_count")
    private int songCount;
    @SerializedName("rating")
    private Ratings rating;
    @SerializedName("favourite_count")
    private int favouriteCount;
    @SerializedName("featured")
    private int featured;
    @SerializedName("sponsored")
    private int sponsored;
    @SerializedName("hot")
    private int hot;
    @SerializedName("upcoming")
    private int upcoming;
    @SerializedName("ip_address")
    private String ipAddress;
    @SerializedName("offtheday")
    private int offtheday;
    @SerializedName("starttime")
    private String starttime;
    @SerializedName("endtime")
    private String endtime;
    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("resource_id")
    private int resourceId;
    @SerializedName("user_title")
    private String userTitle;
    @SerializedName("description")
    private String description;
    @SerializedName("is_content_like")
    private boolean isContentLike;
    @SerializedName("content_like_count")
    private int contentLikeCount;
    @SerializedName("is_content_favourite")
    private boolean isContentFavourite;
    @SerializedName("content_favourite_count")
    private int contentFavouriteCount;
    @SerializedName("images")
    private Images images;

    @SerializedName("cover")
    private Images cover;


    @SerializedName("profile_image_options")
    private List<Options> profileImageOptions;
    @SerializedName("cover_image_options")
    private List<Options> coverImageOptions;
    private Share share;
    @SerializedName("album_title")
    private String albumTitle;
    @SerializedName("lyrics")
    private String lyrics;
    @SerializedName("category_title")
    private String categoryTitle;
    private List<Artist> artists;

    @SerializedName("enable_add_shortcut")
    private boolean enable_add_shortcut;

    @SerializedName("shortcut_save")
    private Shortcuts shortcut_save;

    public boolean isEnable_add_shortcut() {
        return enable_add_shortcut;
    }

    public void setEnable_add_shortcut(boolean enable_add_shortcut) {
        this.enable_add_shortcut = enable_add_shortcut;
    }

    public Shortcuts getShortcut_save() {
        return shortcut_save;
    }

    public void setShortcut_save(Shortcuts shortcut_save) {
        this.shortcut_save = shortcut_save;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public Images getCover() {
        return cover;
    }

    public void setCover(Images cover) {
        this.cover = cover;
    }

    public List<Options> getProfileImageOptions() {
        return profileImageOptions;
    }

    public void setProfileImageOptions(List<Options> profileImageOptions) {
        this.profileImageOptions = profileImageOptions;
    }

    public List<Options> getCoverImageOptions() {
        return coverImageOptions;
    }

    public void setCoverImageOptions(List<Options> coverImageOptions) {
        this.coverImageOptions = coverImageOptions;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    private String name;
    private String overview;
    @SerializedName("play_count")
    private int playCount;
    @SerializedName("song_url")
    private String songUrl;

    private List<Options> menus;

    public List<Options> getMenus() {
        return menus;
    }

    public void setMenus(List<Options> menus) {
        this.menus = menus;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getTitle() {
        return title;
    }

    public int getPhotoId() {
        return photoId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public int getViewCount() {
        return viewCount;
    }


    public int getLikeCount() {
        return likeCount;
    }


    public int getCommentCount() {
        return commentCount;
    }

    public int getSongCount() {
        return songCount;
    }


    public Ratings getRating() {
        return rating;
    }


    public int getFavouriteCount() {
        return favouriteCount;
    }


    public int getFeatured() {
        return featured;
    }


    public int getSponsored() {
        return sponsored;
    }


    public int getOfftheday() {
        return offtheday;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public String getDescription() {
        return SpanUtil.getHtmlString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsContentLike() {
        return isContentLike;
    }

    public void setIsContentLike(boolean isContentLike) {
        this.isContentLike = isContentLike;
    }

    public boolean getIsContentFavourite() {
        return isContentFavourite;
    }

    public void setIsContentFavourite(boolean isContentFavourite) {
        this.isContentFavourite = isContentFavourite;
    }


    public Images getImages() {
        return images;
    }

    public String getImageUrl() {
        if (null != images)
            return images.getNormal();
        return null;
    }

    public void setImages(Images images) {
        this.images = images;
    }


    public Albums getAlbums() {
        Albums vo = new Albums();
        vo.setSongUrl(songUrl);
        vo.setTitle(title);
        vo.setAlbumId(albumId);
        vo.setSongId(songId);
        vo.setImages(images);
        vo.setAlbumTitle(albumTitle);
        vo.setArtists(artists);
        vo.setLyrics(lyrics);
        vo.setShare(share);

        return vo;
    }
}
