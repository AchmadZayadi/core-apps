package com.sesolutions.ui.clickclick.discover;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.User2;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.videos.Videos;

import java.util.List;

public class VideoResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {
        private List<CategoryPage<VideoContent>> category;
        private Videos recently_created;
        private Videos most_viewed;
        private Videos most_commented;
        private Videos featured;
        private Videos sponsored;
        private Videos hot;
        private Videos most_favourite;
        private User2 user_info;
        private Videos most_liked;
        private List<CategoryPage<VideoContent>> categories;
        private List<VideoContent> pages;
        private List<Options> options;
        private List<Options> sort;
        private List<Videos> videos;
        private List<Videos> musics;
        private List<Videos> items;
        private Options button;

        //For View Page
        private VideoContent page;
        private Options callToAction;
        private List<VideoContent> relatedPages;
        private List<Options> about;
        private List<Options> menus;
        private String message;
        private List<Albums> photo;
        @SerializedName("can_create")
        private boolean canCreateAlbum;

        public User2 getUser_info() {
            return user_info;
        }

        public void setUser_info(User2 user_info) {
            this.user_info = user_info;
        }

        public Options getButton() {
            return button;
        }

        public List<Options> getSort() {
            return sort;
        }

        public String getMessage() {
            return message;
        }

        public boolean canCreateAlbum() {
            return canCreateAlbum;
        }

        public Options getCallToAction() {
            return callToAction;
        }

        public List<Options> getOptions() {
            return options;
        }

        public List<Albums> getPhoto() {
            return photo;
        }

        public VideoContent getPage() {
            return page;
        }

        public List<VideoContent> getRelatedPages() {
            return relatedPages;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<VideoContent>> getCategories() {
            return categories;
        }

        public List<VideoContent> getPages() {
            return pages;
        }
       /* public List<FundContent> getCampaigns() {
            return campaigns;
        }*/


        public Videos getRecently_created() {
            return recently_created;
        }

        public Videos getMostViewed() {
            return most_viewed;
        }

        public Videos getMost_commented() {
            return most_commented;
        }

        public Videos getFeatured() {
            return featured;
        }

        public Videos getSponsored() {
            return sponsored;
        }

        public Videos getHot() {
            return hot;
        }

        public Videos getMost_liked() {
            return most_liked;
        }

        public Videos getMost_favourite() {
            return most_favourite;
        }

        public List<CategoryPage<VideoContent>> getCategory() {
            return category;
        }

        public List<Videos> getVideos() {
            return videos;
        }

        public List<Videos> getMusics() {
            return musics;
        }

        public List<Videos> getItems() {
            return items;
        }

    }


    public String getSessionId() {
        return sessionId;
    }

    public Result getResult() {
        return result;
    }
}
