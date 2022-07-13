package com.sesolutions.responses.music;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.SesModel;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.page.Shortcuts;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.utils.SpanUtil;

import java.util.List;

public class Albums extends SesModel {
    private boolean selected = false;
    private boolean isPlaying = false;
    @SerializedName("artist_id")
    private int artistId;
    @SerializedName("album_id")
    private int albumId;
    @SerializedName("albumsong_id")
    private int songId;
    @SerializedName("playlist_id")
    private int playlistId;
    @SerializedName("music_id")
    private int musicid;
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
    @SerializedName("musics")
    private Albums musics;
    @SerializedName("photo_id")
    private int photoId;
    @SerializedName("album_cover")
    private int albumCover;
    @SerializedName("search")
    private int search;
    @SerializedName("profile")
    private int profile;
    @SerializedName("duration")
    private int duration;
    @SerializedName("view_count")
    private int viewCount;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("song_count")
    private int songCount;
    @SerializedName("rating")
    private float rating;
    @SerializedName("favourite_count")
    private int favouriteCount;
    @SerializedName("featured")
    private int featured;
    @SerializedName("sponsored")
    private int sponsored;
    @SerializedName("hot")
    private int hot;
    @SerializedName("file_id")
    private int fileId;
    @SerializedName("lyrics")
    private String lyrics;
    @SerializedName("category_name")
    private String category_name;
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
    @SerializedName("content_like_count")
    private int contentLikeCount;
    @SerializedName("content_favourite_count")
    private int contentFavouriteCount;
    @SerializedName("images")
    private Images images;
    private Category category;
    private List<Artist> artists;
    @SerializedName("album_title")
    private String albumTitle;
    /* private Images cover;
     @SerializedName("profile_image_options")
     private List<Options> profileImageOptions;
     @SerializedName("cover_image_options")
     private List<Options> coverImageOptions;
     private Share share;*/






    private Share share;
    private List<Albums> result;

    public Share getShare() {
        return share;
    }

    public Category getCategory() {
        return category;
    }

    public List<Albums> getResults() {
        return result;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public boolean getselected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public Albums getMusics() {
        return musics;
    }

    private String name;
    private String overview;
    @SerializedName("play_count")
    private int playCount;
    @SerializedName("url")
    private String songUrl;

    @SerializedName("song_url")
    private String songselfurl;

    public String getSongselfurl() {
        return songselfurl;
    }

    public void setSongselfurl(String songselfurl) {
        this.songselfurl = songselfurl;
    }

    private List<Options> menus;

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public int getMusicid() {
        return musicid;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

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

    public int getFileId() {
        return fileId;
    }

    public String getImageUrl() {
        if (null != images) {
            return images.getNormal();
        }
        return null;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(int albumCover) {
        this.albumCover = albumCover;
    }

    public int getSearch() {
        return search;
    }

    public void setSearch(int search) {
        this.search = search;
    }

    public int getProfile() {
        return profile;
    }

    public int getDuration() {
        return duration;
    }

    public String getMainImageUrl() {
        if (images != null) {
            return images.getMain();
        } else {
            return "";
        }
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getSongCount() {
        return songCount;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(int favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    public int getFeatured() {
        return featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }

    public int getSponsored() {
        return sponsored;
    }

    public void setSponsored(int sponsored) {
        this.sponsored = sponsored;
    }


    public int getOfftheday() {
        return offtheday;
    }

    public void setOfftheday(int offtheday) {
        this.offtheday = offtheday;
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


    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }
}
