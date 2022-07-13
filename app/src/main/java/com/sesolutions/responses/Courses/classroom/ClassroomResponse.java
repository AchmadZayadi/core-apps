package com.sesolutions.responses.Courses.classroom;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.page.PageVo;
import com.sesolutions.responses.videos.Videos;

import java.util.ArrayList;
import java.util.List;

public class ClassroomResponse extends ErrorResponse {

    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {
        private List<CategoryPage<ClassroomContent>> category;
        private List<ClassroomContent> hot_classroom;
        private List<ClassroomContent> featured_classroom;
        private List<ClassroomContent> verified_classroom;
        private List<CategoryPage<ClassroomContent>> categories;
        private List<ClassroomContent> classrooms;
        private List<Options> options;
        private List<Options> sort;
        private List<Options> filterMenuOptions;
        private List<Videos> videos;
        private Options button;

        //For View Page
        private ClassroomContent classroom;
        private Options callToAction;
        private List<ClassroomContent> relatedClassrooms;
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

        public ClassroomContent getClassroom() {
            return classroom;
        }

        public List<ClassroomContent> getRelatedClassrooms() {
            return relatedClassrooms;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<ClassroomContent>> getCategories() {
            return categories;
        }

        public List<ClassroomContent> getClassrooms() {
            return classrooms;
        }


        public List<Options> getFilterMenuOptions() {
            return filterMenuOptions;
        }

        public List<ClassroomVo> getClassrooms(String screenType) {
            List<ClassroomVo> result = new ArrayList<>();
            if (classrooms != null) {
                for (ClassroomContent vo : classrooms) {
                    result.add(new ClassroomVo(screenType, vo));
                }
            }
            return result;
        }


        public List<ClassroomVo> getCategories(String screenType) {
            List<ClassroomVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<ClassroomContent> vo : categories) {
                    result.add(new ClassroomVo(screenType, vo));
                }
            }
            return result;
        }

        public List<ClassroomContent> getHotClassrooms() {
            return hot_classroom;

        }
        public List<ClassroomContent> getFeaturedClassrooms() {
            return featured_classroom;
        }
        public List<ClassroomContent> getVerifiedClassrooms() {
            return verified_classroom;
        }

        public List<CategoryPage<ClassroomContent>> getCategory() {
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

