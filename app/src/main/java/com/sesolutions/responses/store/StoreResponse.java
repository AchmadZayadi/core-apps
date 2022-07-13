package com.sesolutions.responses.store;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.Announcement;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.videos.Videos;

import java.util.ArrayList;
import java.util.List;

public class StoreResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {
        private List<CategoryPage<StoreContent>> category;
        private List<StoreContent> popularStores;
        private List<CategoryPage<StoreContent>> categories;
        private List<StoreContent> stores;
        private List<Options> options;
        private List<Options> sort;
        private List<Videos> videos;
        private Options button;
        private List<Announcement> announcements;

        //For View Store
        private StoreContent store;
        private Options callToAction;
        private List<StoreContent> relatedStores;
        private List<Options> about;
        private List<Options> menus;
        private String message;
        private List<Albums> photo;
        @SerializedName("can_create")
        private boolean canCreateAlbum;


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

        public StoreContent getStore() {
            return store;
        }

        public List<StoreContent> getRelatedStores() {
            return relatedStores;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<StoreContent>> getCategories() {
            return categories;
        }

        public List<Announcement> getAnnouncements() {
            return announcements;
        }

        public List<StoreContent> getStores() {
            return stores;
        }
       /* public List<FundContent> getCampaigns() {
            return campaigns;
        }*/


        public List<StoreVo> getStores(String screenType) {
            List<StoreVo> result = new ArrayList<>();
            if (stores != null) {
                for (StoreContent vo : stores) {
                    result.add(new StoreVo(screenType, vo));
                }
            }
            return result;
        }


        public List<StoreVo> getCategories(String screenType) {
            List<StoreVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<StoreContent> vo : categories) {
                    result.add(new StoreVo(screenType, vo));
                }
            }
            return result;
        }

        public List<StoreContent> getPopularStores() {
            return popularStores;
        }

        public List<StoreContent> getAllStores() {
            return stores;
        }

        public List<CategoryPage<StoreContent>> getCategory() {
            return category;
        }

        public List<Videos> getVideos() {
            return videos;
        }

    }


    public String getSessionId() {
        return sessionId;
    }

    public Result getResult() {
        return result;
    }
}
