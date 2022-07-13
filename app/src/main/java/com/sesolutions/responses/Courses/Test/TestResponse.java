package com.sesolutions.responses.Courses.Test;

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

public class TestResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {
        private List<CategoryPage<TestContent>> category;
        private List<TestContent> popularTests;
        private List<CategoryPage<TestContent>> categories;
        private List<TestContent> tests;
        private List<Options> options;
        private List<Options> sort;
        private List<Videos> videos;
        private Options button;
        private List<Announcement> announcements;

        //For View Store
        private TestContent test;
        private Options callToAction;
        private List<TestContent> relatedTests;
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

        public TestContent getTest() {
            return test;
        }

        public List<TestContent> getRelatedTests() {
            return relatedTests;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<TestContent>> getCategories() {
            return categories;
        }

        public List<Announcement> getAnnouncements() {
            return announcements;
        }

        public List<TestContent> getTests() {
            return tests;
        }
       /* public List<FundContent> getCampaigns() {
            return campaigns;
        }*/


        public List<TestVo> getTests(String screenType) {
            List<TestVo> result = new ArrayList<>();
            if (tests != null) {
                for (TestContent vo : tests) {
                    result.add(new TestVo(screenType, vo));
                }
            }
            return result;
        }


        public List<TestVo> getCategories(String screenType) {
            List<TestVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<TestContent> vo : categories) {
                    result.add(new TestVo(screenType, vo));
                }
            }
            return result;
        }

        public List<TestContent> getPopularTests() {
            return popularTests;
        }

        public List<TestContent> getAllStores() {
            return tests;
        }

        public List<CategoryPage<TestContent>> getCategory() {
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
