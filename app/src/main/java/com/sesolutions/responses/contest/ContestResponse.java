package com.sesolutions.responses.contest;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.videos.Category;

import java.util.ArrayList;
import java.util.List;

public class ContestResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {

        private List<ContestItem> contests;
        private List<ContestItem> entries;
        private List<ContestItem> winners;
        private List<Category> category;
        private Banner banner;
        private ContestItem contest;
        private ContestItem entry;
        private List<CategoryPage<ContestItem>> categories;

        //to be deleted
        private List<Options> options;

        private List<Options> menus;

        @SerializedName("post_button")
        private Options postButton;
        private ContestGraph graph;
        private List<Options> graphOptions;


        public ContestGraph getGraph() {
            return graph;
        }

        public ContestItem getEntry() {
            return entry;
        }


        public List<Options> getGraphOptions() {
            return graphOptions;
        }

        public ContestItem getContest() {
            return contest;
        }

        public Banner getBanner() {
            return banner;
        }


        public List<Category> getCategory() {
            return category;
        }


        public List<ContestItem> getContests() {
            return contests;
        }


        public List<Options> getOptions() {
            return options;
        }


        public Options getPostButton() {
            return postButton;
        }


        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<ContestItem>> getCategories() {
            return categories;
        }

        public List<Contest> getContestList(String type) {
            List<Contest> list = new ArrayList<>();
            if (null != contests) {
                for (ContestItem vo : contests) {
                    list.add(new Contest(type, vo));
                }
            }
            return list;
        }


        public List<Contest> getContestCategory(String type) {
            List<Contest> list = new ArrayList<>();
            if (null != categories) {
                for (CategoryPage<ContestItem> vo : categories) {
                    list.add(new Contest(type, vo));
                }
            }
            return list;
        }

        public List<Contest> getEntryList(String type) {
            List<Contest> list = new ArrayList<>();
            if (null != entries) {
                for (ContestItem vo : entries) {
                    list.add(new Contest(type, vo));
                }
            }
            return list;
        }

        public List<ContestItem> getEntries() {
            return entries;
        }

        public List<Contest> getWinnerList(String type) {
            List<Contest> list = new ArrayList<>();
            if (null != winners) {
                for (ContestItem vo : winners) {
                    list.add(new Contest(type, vo));
                }
            }
            return list;
        }

        public List<ContestItem> getWinners() {
            return winners;
        }

    }


    public String getSessionId() {
        return sessionId;
    }

    public Result getResult() {
        return result;
    }


}
