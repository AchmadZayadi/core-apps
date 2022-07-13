package com.sesolutions.responses.business;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.page.PageVo;
import com.sesolutions.responses.videos.Videos;

import java.util.ArrayList;
import java.util.List;

public class BusinessResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result {
        private List<CategoryPage<BusinessContent>> category;
        private List<BusinessContent> popularBusinesses;
        private List<CategoryPage<BusinessContent>> categories;
        private List<BusinessContent> businesses;
        private List<Options> options;
        private List<Options> sort;
        private List<Videos> videos;
        private Options button;

        //For View Business
        private BusinessContent business;
        private Options callToAction;
        private List<BusinessContent> relatedBusinesses;
        private List<Options> about;
        private List<Options> menus;
        private String message;
        private List<Albums> photo;
        @SerializedName("can_create")
        private boolean canCreateAlbum;


        @SerializedName("loggedin_user_id")
        private int loggedin_user_id;
        @SerializedName("total_page")
        private int totalPage;
        @SerializedName("current_page")
        private int currentPage;
        @SerializedName("next_page")
        private int nextPage;
        private int total;

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

        public BusinessContent getBusiness() {
            return business;
        }

        public List<BusinessContent> getRelatedBusinesses() {
            return relatedBusinesses;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<BusinessContent>> getCategories() {
            return categories;
        }

        public List<BusinessContent> getBusinesses() {
            return businesses;
        }


        public List<PageVo> getBusinesses(String screenType) {
            List<PageVo> result = new ArrayList<>();
            if (businesses != null) {
                for (BusinessContent vo : businesses) {
                    result.add(new PageVo(screenType, vo));
                }
            }
            return result;
        }


        public List<PageVo> getCategories(String screenType) {
            List<PageVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<BusinessContent> vo : categories) {
                    result.add(new PageVo(screenType, vo));
                }
            }
            return result;
        }

        public List<BusinessContent> getPopularBusinesses() {
            return popularBusinesses;
        }

        public List<CategoryPage<BusinessContent>> getCategory() {
            return category;
        }

        public List<Videos> getVideos() {
            return videos;
        }

        public int getTotal() {
            return total;
        }

        public int getLoggedin_user_id() {
            return this.loggedin_user_id;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getNextPage() {
            return nextPage;
        }
    }


    public String getSessionId() {
        return sessionId;
    }

    public Result getResult() {
        return result;
    }
}
