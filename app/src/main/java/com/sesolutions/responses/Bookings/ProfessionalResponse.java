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

public class ProfessionalResponse extends ErrorResponse {

    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {
        private List<CategoryPage<ProfessionalContent>> category;
        private List<ProfessionalContent> hot_classroom;
        private List<ProfessionalContent> featured_classroom;
        private List<ProfessionalContent> verified_classroom;
        private List<CategoryPage<ProfessionalContent>> categories;
        private List<ProfessionalContent> professionals;
        private List<Options> options;
        private List<Options> sort;
        private List<Options> filterMenuOptions;
        private List<Videos> videos;
        private Options button;
        private Share share;

        //For View Page
        private ProfessionalContent professional;
        private Options callToAction;
        private List<ProfessionalContent> relatedProfessionals;
        private List<Options> about;
        private List<Options> menus;
        private String message;
        private List<Albums> photo;
        @SerializedName("can_create")
        private boolean canCreateAlbum;


        public Options getButton() {
            return button;
        }
        public Share getShare() {
            return share;
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

        public ProfessionalContent getProfessional() {
            return professional;
        }

        public List<ProfessionalContent> getRelateProfessionals() {
            return relatedProfessionals;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<ProfessionalContent>> getCategories() {
            return categories;
        }

        public List<ProfessionalContent> getProfessionals() {
            return professionals;
        }


        public List<Options> getFilterMenuOptions() {
            return filterMenuOptions;
        }

        public List<ProfessionalVo> getProfessionals(String screenType) {
            List<ProfessionalVo> result = new ArrayList<>();
            if (professionals != null) {
                for (ProfessionalContent vo : professionals) {
                    result.add(new ProfessionalVo(screenType, vo));
                }
            }
            return result;
        }


        public List<ProfessionalVo> getCategories(String screenType) {
            List<ProfessionalVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<ProfessionalContent> vo : categories) {
                    result.add(new ProfessionalVo(screenType, vo));
                }
            }
            return result;
        }

        public List<ProfessionalContent> getHotProfessionals() {
            return hot_classroom;

        }
        public List<ProfessionalContent> getFeaturedProfessionals() {
            return featured_classroom;
        }
        public List<ProfessionalContent> getVerifieProfessionals() {
            return verified_classroom;
        }

        public List<CategoryPage<ProfessionalContent>> getCategory() {
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

