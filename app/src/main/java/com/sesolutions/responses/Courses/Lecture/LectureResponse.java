package com.sesolutions.responses.Courses.Lecture;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.Announcement;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.page.PageVo;
import com.sesolutions.responses.videos.Videos;

import java.util.ArrayList;
import java.util.List;

public class LectureResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {
        private List<CategoryPage<LectureContent>> category;
        private List<LectureContent> popularLectures;
        private List<CategoryPage<LectureContent>> categories;
        private List<LectureContent> lectures;
        private List<Options> options;
        private List<Options> sort;
        private List<Videos> videos;
        private Options button;
        private List<Announcement> announcements;

        //For View Store
        private LectureContent lecture;
        private Options callToAction;
        private List<LectureContent> relatedLectures;
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

        public LectureContent getLecture() {
            return lecture;
        }

        public List<LectureContent> getRelatedLectures() {
            return relatedLectures;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<LectureContent>> getCategories() {
            return categories;
        }

        public List<Announcement> getAnnouncements() {
            return announcements;
        }

        public List<LectureContent> getLectures() {
            return lectures;
        }
       /* public List<FundContent> getCampaigns() {
            return campaigns;
        }*/


        public List<LectureVo> getLectures(String screenType) {
            List<LectureVo> result = new ArrayList<>();
            if (lectures != null) {
                for (LectureContent vo : lectures) {
                    result.add(new LectureVo(screenType, vo));
                }
            }
            return result;
        }


        public List<LectureVo> getCategories(String screenType) {
            List<LectureVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<LectureContent> vo : categories) {
                    result.add(new LectureVo(screenType, vo));
                }
            }
            return result;
        }

        public List<LectureContent> getPopularLectures() {
            return popularLectures;
        }

        public List<LectureContent> getAllStores() {
            return lectures;
        }

        public List<CategoryPage<LectureContent>> getCategory() {
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
