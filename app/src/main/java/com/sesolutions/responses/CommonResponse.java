package com.sesolutions.responses;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.Courses.Lecture.LectureContent;
import com.sesolutions.responses.Courses.Test.TestContent;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.blogs.Blog;
import com.sesolutions.responses.contest.Packages;
import com.sesolutions.responses.feed.LocationActivity;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.jobs.JobsResponse;
import com.sesolutions.responses.news.News;
import com.sesolutions.responses.news.RSS;
import com.sesolutions.responses.page.Announcement;
import com.sesolutions.responses.page.PageInformation;
import com.sesolutions.responses.page.PageServices;
import com.sesolutions.responses.quote.Quote;
import com.sesolutions.responses.store.StoreContent;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 7/11/17.
 */

public class CommonResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("result")
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static class Result {
        @SerializedName("loggedin_user_id")
        private int loggedinUserId;
        @SerializedName("total_page")
        private int totalPage;
        private int total;
        @SerializedName("current_page")
        private int currentPage;
        @SerializedName("next_page")
        private int nextPage;
        private List<Options> menus;
        @SerializedName("notification")
        private List<Notifications> notification;

        @SerializedName("activityCount")
        private int activityCount;

        public int getActivityCount() {
            return activityCount;
        }

        public void setActivityCount(int activityCount) {
            this.activityCount = activityCount;
        }

        @SerializedName("message")
        private JsonElement message;

        @SerializedName("friends")
        private List<Friends> friends;
        private List<Friends> tags;
        private List<Quote> quotes;
        private List<Quote> prayers;
        private List<Quote> thoughts;
        private List<Quote> wishes;
        @SerializedName("videos")
        private List<Videos> videos;
        private List<LocationActivity> locations;
        private LocationActivity location;
        private List<CommonVO> events;
        private List<CommonVO> lists;
        private List<CommonVO> hosts;
        private List<LikeData> likeData;
        @SerializedName("reaction_data")
        private List<LikeData> reactionData;
        @SerializedName("event_category")
        private Category eventCategory;
        @SerializedName("sub_category")
        private List<Category> eventSubCategory;
        private List<Packages> packages;
        @SerializedName("existingleftpackages")
        private List<Packages> existingPackage;

        private Quote quote;
        private Quote prayer;
        private Quote thought;
        @SerializedName("event_content")
        private CommonVO event;
        @SerializedName("wishe")
        private Quote wish;
        private List<SearchVo> search;

        @SerializedName("video")
        private JsonElement video;
        @SerializedName("link")
        private Links link;

        private List<Feeling> feelings;
        private List<Emotion> emotions;
        @SerializedName("useremotions")
        private List<Emotion> userEmotions;
        private List<Gallary> gallery;
        private List<Settings> settings;
        private List<Networks> networkSelected;
        private List<Networks> networkAvailable;
        private List<Albums> albums;
        private List<ChannelPhoto> photos;
        private List<Blog> blogs;
        private JsonElement states;

        public Map<String, String> getresults() {
            Map<String, String> multiOption = new HashMap<>();
            try {
                if (states.isJsonObject()) {
                    multiOption = new Gson().fromJson(states.toString(), Map.class);
                } else {
                    List<String> option = new Gson().fromJson(states.toString(), List.class);
                    if (option != null) {
                        for (String value : option) {
                            multiOption.put(value, value);
                        }
                    }

                }
            } catch (JsonSyntaxException e) {
                CustomLog.e(e);
            }
            return multiOption;
        }


        public boolean isresults() {
            return null != states;
        }


        private List<JobsResponse> jobs;

        private List<JobsResponse> companies;


        public List<JobsResponse> getJobs() {
            return jobs;
        }

        public void setJobs(List<JobsResponse> jobs) {
            this.jobs = jobs;
        }

        public List<RSS> getRssList() {
            return rssList;
        }

        public void setRssList(List<RSS> rssList) {
            this.rssList = rssList;
        }

        public RSS getRss() {
            return rss;
        }

        public void setRss(RSS rss) {
            this.rss = rss;
        }

        @SerializedName("news_list")
        private List<News> newsList;
        @SerializedName("rss_list")
        private List<RSS> rssList;
        private List<Group> groups;

        private List<Blog> classifieds;
        private Blog classified;
        private List<Blog> articles;
        private List<Blog> recipes;
        private List<Options> info;
        private List<Category> category;
        private List<Category> quoteCategories;
        private List<Category> prayerCategories;
        private List<Category> thoughtCategories;
        @SerializedName("wisheCategories")
        private List<Category> wishCategories;
        private Blog blog;
        private LectureContent lecture;
        private TestContent test;
        private Blog article;
        private News news;
        private RSS rss;
        @SerializedName("group_content")
        private Group groupContent;
        private Notifications member;
        private JsonElement subcategory;
        private JsonElement subsubcategory;
        @SerializedName("notification_count")
        private int notificationCount;
        @SerializedName("friend_req_count")
        private int friendReqCount;
        @SerializedName("message_count")
        private int messageCount;
        @SerializedName("total_notification")
        private int totalNotification;
        private Privacy privacy;
        private Privacy terms;
        @SerializedName("members")
        private List<UserMaster> groupMembers;
        @SerializedName("success_message")
        private String successMessage;
        private List<Options> gutterMenu;
        private PageInformation information;
        private List<PageServices> services;
        private Blog recipe;


        private String subscribe;
        private String unsubscribe;
        @SerializedName("subscribe_id")
        private int subscribeId;
        @SerializedName("watch")
        private int watch;

        public List<JobsResponse> getCompanies() {
            return companies;
        }

        public String getSubscribe() {
            return subscribe;
        }

        public String getUnsubscribe() {
            return unsubscribe;
        }

        public int getSubscribeId() {
            return subscribeId;
        }

        public int getWatch() {
            return watch;
        }

        public List<Blog> getRecipies() {
            return recipes;
        }

        public Category getEventCategory() {
            return eventCategory;
        }

        public List<Category> getEventSubCategory() {
            return eventSubCategory;
        }

        public List<CommonVO> getLists() {
            return lists;
        }

        public List<CommonVO> getHosts() {
            return hosts;
        }

        public List<PageServices> getServices() {
            return services;
        }

        public PageInformation getInformation() {
            return information;
        }


        public List<LocationActivity> getLocations() {
            return locations;
        }

        public LocationActivity getLocation() {
            return location;
        }


        public CommonVO getEvent() {
            return event;
        }

        public String getSuccessMessage() {
            return successMessage;
        }

        public void setSuccessMessage(String successMessage) {
            this.successMessage = successMessage;
        }

        public List<Options> getGutterMenu() {
            return gutterMenu;
        }

        public void setGutterMenu(List<Options> gutterMenu) {
            this.gutterMenu = gutterMenu;
        }

        public List<UserMaster> getGroupMembers() {
            return groupMembers;
        }

        public void setGroupMembers(List<UserMaster> groupMembers) {
            this.groupMembers = groupMembers;
        }

        public List<Packages> getPackages() {
            return packages;
        }

        public List<CommonVO> getEvents() {
            return events;
        }

        public void setEvents(List<CommonVO> events) {
            this.events = events;
        }

        public List<Category> getWishCategories() {
            return wishCategories;
        }

        public void setWishCategories(List<Category> wishCategories) {
            this.wishCategories = wishCategories;
        }

        public List<Quote> getWishes() {
            return wishes;
        }

        public void setWishes(List<Quote> wishes) {
            this.wishes = wishes;
        }

        public Quote getWish() {
            return wish;
        }

        public void setWish(Quote wish) {
            this.wish = wish;
        }

        public Group getGroupContent() {
            return groupContent;
        }

        public void setGroupContent(Group groupContent) {
            this.groupContent = groupContent;
        }

        public List<Group> getGroups() {
            return groups;
        }

        public void setGroups(List<Group> groups) {
            this.groups = groups;
        }


        public Blog getClassified() {
            return classified;
        }

        public void setClassified(Blog classified) {
            this.classified = classified;
        }

        public Privacy getPrivacy() {
            return privacy;
        }

        public void setPrivacy(Privacy privacy) {
            this.privacy = privacy;
        }

        public Privacy getTerms() {
            return terms;
        }

        public void setTerms(Privacy terms) {
            this.terms = terms;
        }

        public List<Category> getQuoteCategories() {
            return quoteCategories;
        }

        public void setQuoteCategories(List<Category> quoteCategories) {
            this.quoteCategories = quoteCategories;
        }

        public Quote getQuote() {
            return quote;
        }

        public void setQuote(Quote quote) {
            this.quote = quote;
        }

        public List<Friends> getTags() {
            return tags;
        }

        public List<Quote> getQuotes() {
            return quotes;
        }

        public List<Category> getCategory() {
            return category;
        }

        public void setCategory(List<Category> category) {
            this.category = category;
        }

        public void setQuotes(List<Quote> quotes) {
            this.quotes = quotes;
        }

        public void setTags(List<Friends> tags) {
            this.tags = tags;
        }

        public Blog getArticle() {
            return article;
        }

        public void setArticle(Blog article) {
            this.article = article;
        }

        public List<Blog> getArticles() {
            return articles;
        }

        public void setArticles(List<Blog> articles) {
            this.articles = articles;
        }

        public List<SearchVo> getSearch() {
            return search;
        }

        public void setSearch(List<SearchVo> search) {
            this.search = search;
        }

        public boolean isNewItemAvailable() {
            return (notificationCount + friendReqCount + messageCount) > 0;
        }

        public boolean isValidUserEmotion() {
            return null != userEmotions && userEmotions.size() > 1;
        }

        public List<Emotion> getUserEmotions() {
            return userEmotions;
        }

        public void setUserEmotions(List<Emotion> userEmotions) {
            this.userEmotions = userEmotions;
        }

        public int getNotificationCount() {
            return notificationCount;
        }

        public void setNotificationCount(int notificationCount) {
            this.notificationCount = notificationCount;
        }

        public int getFriendReqCount() {
            return friendReqCount;
        }

        public void setFriendReqCount(int friendReqCount) {
            this.friendReqCount = friendReqCount;
        }

        public int getMessageCount() {
            return messageCount;
        }

        public void setMessageCount(int messageCount) {
            this.messageCount = messageCount;
        }

        public int getTotalNotification() {
            return totalNotification;
        }

        public void setTotalNotification(int totalNotification) {
            this.totalNotification = totalNotification;
        }

        @SuppressWarnings("unchecked")
        public Map<String, String> getSubCategory() {
            Map<String, String> multiOption = new HashMap<>();
            try {
                if (subcategory.isJsonObject()) {
                    multiOption = new Gson().fromJson(subcategory.toString(), Map.class);
                } else {
                    List<String> option = new Gson().fromJson(subcategory.toString(), List.class);
                    if (option != null) {
                        for (String value : option) {
                            multiOption.put(value, value);
                        }
                    }

                }
            } catch (JsonSyntaxException e) {
                CustomLog.e(e);
            }
            return multiOption;
        }

        @SuppressWarnings("unchecked")
        public Map<String, String> getSubSubCategory() {
            Map<String, String> multiOption = new HashMap<>();
            try {
                if (subsubcategory.isJsonObject()) {
                    multiOption = new Gson().fromJson(subsubcategory.toString(), Map.class);
                } else {
                    List<String> option = new Gson().fromJson(subsubcategory.toString(), List.class);
                    if (option != null) {
                        for (String value : option) {
                            multiOption.put("", value);
                        }
                    }

                }
            } catch (JsonSyntaxException e) {
                CustomLog.e(e);
            }
            return multiOption;
        }


        public List<Options> getInfo() {
            return info;
        }

        public void setInfo(List<Options> info) {
            this.info = info;
        }

        public Notifications getMember() {
            return member;
        }

        public void setMember(Notifications member) {
            this.member = member;
        }

        public Blog getBlog() {
            return blog;
        }

        public LectureContent getLecture() {
            return lecture;
        }

        public TestContent getTest() {
            return test;
        }

        public void setBlog(Blog blog) {
            this.blog = blog;
        }

        public List<SearchVo> convertBlogToSearchVo() {
            List<SearchVo> list = new ArrayList<>();
            for (Blog vo : blogs) {
                list.add(new SearchVo(vo));
            }
            return list;
        }

        public List<SearchVo> convertNewsToSearchVo() {
            List<SearchVo> list = new ArrayList<>();
            for (News vo : newsList) {
                list.add(new SearchVo(vo));
            }
            return list;
        }

        public List<SearchVo> convertPhotoToSearchVo() {
            List<SearchVo> list = new ArrayList<>();
            for (ChannelPhoto vo : photos) {
                list.add(new SearchVo(vo));
            }
            return list;
        }


        public List<SearchVo> convertVideoToSearchVo() {
            List<SearchVo> list = new ArrayList<>();
            for (Videos vo : videos) {
                list.add(new SearchVo(vo));
            }
            return list;
        }

        public List<SearchVo> convertAlbumToSearchVo() {
            List<SearchVo> list = new ArrayList<>();
            for (Albums vo : albums) {
                list.add(new SearchVo(vo));
            }
            return list;
        }

        public void setBlogs(List<Blog> blogs) {
            this.blogs = blogs;
        }

        public List<Blog> getBlogs() {
            return blogs;
        }

        public List<News> getNewsList() {
            return newsList;
        }

        public void setNewsList(List<News> newsList) {
            this.newsList = newsList;
        }

        public News getNews() {
            return news;
        }

        public void setNews(News newses) {
            this.news = newses;
        }

        public void setMenus(List<Options> menus) {
            this.menus = menus;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ChannelPhoto> getPhotos() {
            return photos;
        }

        public List<Networks> getNetworkSelected() {
            return networkSelected;
        }

        public List<Networks> getNetworkAvailable() {
            return networkAvailable;
        }

        public List<Settings> getSettings() {
            return settings;
        }

        public void setSettings(List<Settings> settings) {
            this.settings = settings;
        }

        public List<Gallary> getGallery() {
            return gallery;
        }

        public void setGallery(List<Gallary> gallery) {
            this.gallery = gallery;
        }

        public List<Emotion> getEmotions() {
            return emotions;
        }

        public List<Feeling> getFeelings() {
            return feelings;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getNextPage() {
            return nextPage;
        }

        public void setNextPage(int nextPage) {
            this.nextPage = nextPage;
        }

        public int getLoggedinUserId() {
            return loggedinUserId;
        }

        public void setLoggedinUserId(int loggedinUserId) {
            this.loggedinUserId = loggedinUserId;
        }

        public List<Notifications> getNotification() {
            return notification;
        }

        public void setNotification(List<Notifications> notification) {
            this.notification = notification;
        }

        public List<Quote> getPrayers() {
            return prayers;
        }

        public List<Quote> getThoughts() {
            return thoughts;
        }

        public Quote getPrayer() {
            return prayer;
        }

        public void setPrayer(Quote prayer) {
            this.prayer = prayer;
        }

        public Quote getThought() {
            return thought;
        }

        public void setThought(Quote thought) {
            this.thought = thought;
        }

        public List<Category> getPrayerCategories() {
            return prayerCategories;
        }

        public List<Category> getThoughtCategories() {
            return thoughtCategories;
        }

        public List<MessageInbox> getMessageList() {
            List<MessageInbox> list = new ArrayList<>();
            if (null != message && message.isJsonArray()) {
                JsonArray arr = message.getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) {
                    list.add(new Gson().fromJson(arr.get(i).toString(), MessageInbox.class));
                }
            }
            return list;
        }

        public MessageInbox getMessage() {
            return null != message && message.isJsonObject() ? new Gson().fromJson(message.toString(), MessageInbox.class) : null;
        }

        public List<Friends> getFriends() {
            return friends;
        }

        public void setFriends(List<Friends> friends) {
            this.friends = friends;
        }

        public Video getVideo() {
            Video videos = null;
            if (video.isJsonObject()) {
                videos = new Gson().fromJson(video.toString(), Video.class);
            }
            return videos;
        }

        public List<Blog> getClassifieds() {
            return classifieds;
        }

        public void setClassifieds(List<Blog> classifieds) {
            this.classifieds = classifieds;
        }

        public List<Videos> getVideoList() {
            List<Videos> videos = new ArrayList<>();
            if (video.isJsonArray()) {
                JsonArray jsonArray = video.getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++)
                    videos.add(new Gson().fromJson(jsonArray.get(i).getAsJsonObject().toString(), Videos.class));
            }
            return videos;
        }

        public List<Videos> getVideos() {
            return videos;
        }

        public List<Albums> getAlbums() {
            return albums;
        }

        public void setVideo(JsonElement video) {
            this.video = video;
        }

        public Links getLink() {
            return link;
        }

        public void setLink(Links link) {
            this.link = link;
        }

        public boolean isSubCategoryNotNull() {
            return null != subcategory;
        }

        public boolean isSubSubCategoryNotNull() {
            return null != subsubcategory;
        }

        public List<LikeData> getLikeData() {
            return likeData;
        }

        public List<LikeData> getReactionData() {
            return reactionData;
        }

        public Blog getRecipe() {
            return recipe;
        }

        public List<Packages> getExistingPackage() {
            return existingPackage;
        }

        public boolean arePackagesAvailabel() {
            return packages != null || existingPackage != null;
        }
    }


}
