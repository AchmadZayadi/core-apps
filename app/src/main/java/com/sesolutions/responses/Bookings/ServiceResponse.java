package com.sesolutions.responses.Bookings;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.page.PageVo;
import com.sesolutions.responses.videos.Videos;

import java.util.ArrayList;
import java.util.List;

public class ServiceResponse extends ErrorResponse {

    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {
        private List<CategoryPage<ServiceContent>> category;
        private List<ServiceContent> hot_classroom;
        private List<ServiceContent> featured_classroom;
        private List<ServiceContent> verified_classroom;
        private List<CategoryPage<ServiceContent>> categories;
        private List<ServiceContent> services;
        private List<Options> options;
        private List<Options> sort;
        private List<Options> filterMenuOptions;
        private List<Videos> videos;
        private Options button;

        //For View Page
        private ServiceContent service;
        private Share share;
        private Options callToAction;
        private List<ServiceContent> relatedServices;
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

        public ServiceContent getService() {
            return service;
        }        public Share getShare() {
            return share;
        }

        public List<ServiceContent> getRelateServices() {
            return relatedServices;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<ServiceContent>> getCategories() {
            return categories;
        }

        public List<ServiceContent> getServices() {
            return services;
        }


        public List<Options> getFilterMenuOptions() {
            return filterMenuOptions;
        }

        public List<ProfessionalVo> getServices(String screenType) {
            List<ProfessionalVo> result = new ArrayList<>();
            if (services != null) {
                for (ServiceContent vo : services) {
                    result.add(new ProfessionalVo(screenType, vo));
                }
            }
            return result;
        }


        public List<ProfessionalVo> getCategories(String screenType) {
            List<ProfessionalVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<ServiceContent> vo : categories) {
                    result.add(new ProfessionalVo(screenType, vo));
                }
            }
            return result;
        }


        public List<CategoryPage<ServiceContent>> getCategory() {
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

