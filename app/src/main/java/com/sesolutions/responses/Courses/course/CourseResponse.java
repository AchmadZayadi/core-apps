package com.sesolutions.responses.Courses.course;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.Announcement;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.page.PageVo;
import com.sesolutions.responses.store.product.WishList;
import com.sesolutions.responses.videos.Videos;

import java.util.ArrayList;
import java.util.List;

public class CourseResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {
        private List<CategoryPage<CourseContent>> category;
        private List<CourseContent> hot_courses;
        private List<CourseContent> featured_courses;
        private List<CourseContent> verified_courses;
        private List<CategoryPage<CourseContent>> categories;
        private List<CourseContent> wishlists;
        private List<CourseContent> courses;
        private List<CourseContent> upsell_course;
        private List<Options> options;
        private List<Options> sort;
        private List<Videos> videos;
        private Options button;
        private List<Announcement> announcements;

        //For View Store
        private CourseContent course;
        private Options callToAction;
        private List<CourseContent> relatedCourses;
        private List<Options> about;
        private List<Options> menus;
        private String message;
        private List<Albums> photo;
        @SerializedName("can_create")
        private boolean canCreateAlbum;


        @SerializedName("wishlist")
        private WishList wishlist;

        public WishList getWishlist() {
            return wishlist;
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

        public CourseContent getCourse() {
            return course;
        }

        public List<CourseContent> getRelatedCourses() {
            return relatedCourses;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<CourseContent>> getCategories() {
            return categories;
        }

        public List<Announcement> getAnnouncements() {
            return announcements;
        }

        public List<CourseContent> getCourses() {
            return courses;
        }

        public List<CourseContent> getUpsell_course() {
            return upsell_course;
        }
       /* public List<FundContent> getCampaigns() {
            return campaigns;
        }*/


        public List<CourseVo> getCourses(String screenType) {
            List<CourseVo> result = new ArrayList<>();
            if (courses != null) {
                for (CourseContent vo : courses) {
                    result.add(new CourseVo(screenType, vo));
                }
            }
            return result;
        }

        public List<CourseVo> getupsellCourses(String screenType) {
            List<CourseVo> result = new ArrayList<>();
            if (upsell_course != null) {
                for (CourseContent vo : upsell_course) {
                    result.add(new CourseVo(screenType, vo));
                }
            }
            return result;
        }


        public List<CourseVo> getCategories(String screenType) {
            List<CourseVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<CourseContent> vo : categories) {
                    result.add(new CourseVo(screenType, vo));
                }
            }
            return result;
        }

        public List<CourseContent> getWishlists() {
            return wishlists;
        }

        public List<CourseVo> getWishlists(String screenType) {
            List<CourseVo> result = new ArrayList<>();
            if (wishlists != null) {
                for (CourseContent vo : wishlists) {
                    result.add(new CourseVo(screenType, vo));
                }
            }
            return result;
        }

        public List<CourseContent> getHotClassrooms() {
            return hot_courses;

        }

        public List<CourseContent> getFeaturedClassrooms() {
            return featured_courses;
        }

        public List<CourseContent> getVerifiedClassrooms() {
            return verified_courses;
        }

        public List<CourseContent> getAllStores() {
            return courses;
        }

        public List<CategoryPage<CourseContent>> getCategory() {
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
