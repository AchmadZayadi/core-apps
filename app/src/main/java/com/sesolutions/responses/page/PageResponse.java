package com.sesolutions.responses.page;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.videos.Videos;

import java.util.ArrayList;
import java.util.List;

public class PageResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {
        private List<CategoryPage<PageContent>> category;
        private List<PageContent> popularPages;
        private List<CategoryPage<PageContent>> categories;
        private List<PageContent> pages;
        private List<Options> options;
        private List<Options> sort;
        private List<Videos> videos;
        private Options button;
        private List<Announcement> announcements;

        //For View Page
        private PageContent page;
        private Options callToAction;
        private List<PageContent> relatedPages;
        private List<Options> about;
        private List<Options> menus;
        private String message;
        private List<Albums> photo;
        @SerializedName("can_create")
        private boolean canCreateAlbum;
        @SerializedName("can_create_classroom")
        private boolean canCreateClassroom;
        @SerializedName("can_create_course")
        private boolean canCreateCourse;
        @SerializedName("isProfessional")
        private boolean isProfessional;

        @SerializedName("shortcut_id")
        private int shortcut_id23;


        public int getShortcut_id23() {
            return shortcut_id23;
        }

        public void setShortcut_id23(int shortcut_id23) {
            this.shortcut_id23 = shortcut_id23;
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

        public boolean CanCreateClassroom() {
            return canCreateClassroom;
        }

        public boolean CanCreateCourse() {
            return canCreateCourse;
        }

        public boolean isProfessional() {
            return isProfessional;
        }
        public void setProfessional(boolean pro) {
            isProfessional = pro;
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

        public PageContent getPage() {
            return page;
        }

        public List<PageContent> getRelatedPages() {
            return relatedPages;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<PageContent>> getCategories() {
            return categories;
        }

        public List<Announcement> getAnnouncements() {
            return announcements;
        }

        public List<PageContent> getPages() {
            return pages;
        }
       /* public List<FundContent> getCampaigns() {
            return campaigns;
        }*/


        public List<PageVo> getPages(String screenType) {
            List<PageVo> result = new ArrayList<>();
            if (pages != null) {
                for (PageContent vo : pages) {
                    result.add(new PageVo(screenType, vo));
                }
            }
            return result;
        }


        public List<PageVo> getCategories(String screenType) {
            List<PageVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<PageContent> vo : categories) {
                    result.add(new PageVo(screenType, vo));
                }
            }
            return result;
        }

        public List<PageContent> getPopularPages() {
            return popularPages;
        }

        public List<CategoryPage<PageContent>> getCategory() {
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
