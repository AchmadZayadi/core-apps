package com.sesolutions.responses.groups;

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

public class GroupResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {
        private List<CategoryPage<GroupContent>> category;
        private List<GroupContent> popularGroups;
        private List<CategoryPage<GroupContent>> categories;
        private List<GroupContent> groups;
        private List<Options> options;
        private List<Options> sort;
        private List<Options> filterMenuOptions;
        private List<Videos> videos;
        private Options button;

        //For View Page
        private GroupContent group;
        private Options callToAction;
        private List<GroupContent> relatedGroups;
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

        public GroupContent getGroup() {
            return group;
        }

        public List<GroupContent> getRelatedGroups() {
            return relatedGroups;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<GroupContent>> getCategories() {
            return categories;
        }

        public List<GroupContent> getGroups() {
            return groups;
        }


        public List<Options> getFilterMenuOptions() {
            return filterMenuOptions;
        }

        public List<PageVo> getGroups(String screenType) {
            List<PageVo> result = new ArrayList<>();
            if (groups != null) {
                for (GroupContent vo : groups) {
                    result.add(new PageVo(screenType, vo));
                }
            }
            return result;
        }


        public List<PageVo> getCategories(String screenType) {
            List<PageVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<GroupContent> vo : categories) {
                    result.add(new PageVo(screenType, vo));
                }
            }
            return result;
        }

        public List<GroupContent> getPopularGroups() {
            return popularGroups;
        }

        public List<CategoryPage<GroupContent>> getCategory() {
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
