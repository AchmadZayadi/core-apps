package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.blogs.Blog;
import com.sesolutions.responses.news.News;
import com.sesolutions.responses.poll.PollOption;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.utils.SpanUtil;

/**
 * Created by root on 27/12/17.
 */

public class SearchVo {
    @SerializedName("images")
    private String images;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("href")
    private String href;
    @SerializedName("id")
    private int id;
    @SerializedName("type")
    private String type;
    private int fileId;

    //variable used in Signin screen for Social Login
    @SerializedName("name")
    private String name;

    public SearchVo(Blog vo) {
        id = vo.getBlogId();
        title = vo.getTitle();
        description = vo.getBody();
        images = vo.getImageUrl();
    }
    public SearchVo(News vo) {
        id = vo.getNewsId();
        title = vo.getTitle();
        description = vo.getBody();
        images = vo.getImageUrl();
    }

    public SearchVo(ChannelPhoto vo) {
        id = vo.getPhotoId();
        title = vo.getTitle();
        //description = vo.getBody();
        images = vo.getImageUrl();
        fileId = vo.getPhotoId();
    }

    public SearchVo(Videos vo) {
        id = vo.getVideoId();
        title = vo.getTitle();
        description = vo.getDescription();
        images = vo.getImageUrl();
        fileId = vo.getVideoId();
    }

    public SearchVo(Albums vo) {
        id = vo.getAlbumId();
        title = vo.getTitle();
        description = vo.getDescription();
        images = vo.getImageUrl();
        fileId = vo.getPhotoId();
    }

    public SearchVo(com.sesolutions.responses.music.Albums vo) {
        id = vo.getAlbumId();
        title = vo.getTitle();
        // description = vo.getDescription();
        images = vo.getImageUrl();
        fileId = vo.getSongId();
    }

    public SearchVo(PollOption vo) {
        id = vo.getImageId();
        images = vo.getOptionImage();
        fileId = vo.getFileId();
    }

    public int getFileId() {
        return fileId;
    }

    public String getName() {
        return name;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
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

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
