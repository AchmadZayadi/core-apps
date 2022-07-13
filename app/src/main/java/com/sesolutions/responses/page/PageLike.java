package com.sesolutions.responses.page;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;

import java.util.ArrayList;
import java.util.List;

public class PageLike {

    private String title;
    private String description;
    private Images image;
    private List<LikePageItem> page;
    private List<LikePageItem> group;
    private List<LikePageItem> business;

    //custom source id
    private int id;

    public List<String> getTitleList() {
        List<String> list = new ArrayList<>();
        if (page != null) {
            for (LikePageItem vo : page) {
                list.add(vo.getPageTitle());
            }
        }
        if (group != null) {
            for (LikePageItem vo : group) {
                list.add(vo.getGroupTitle());
            }
        }
        if (business != null) {
            for (LikePageItem vo : business) {
                list.add(vo.getBusinessTitle());
            }
        }
        return list;
    }

    public List<LikePageItem> getGroup() {
        return group;
    }

    public List<LikePageItem> getBusiness() {
        return business;
    }

    public class LikePageItem {
        @SerializedName("page_id")
        private int pageId;
        @SerializedName("group_id")
        private int groupId;
        @SerializedName("business_id")
        private int businessId;
        @SerializedName("group_title")
        private String groupTitle;
        @SerializedName("business_title")
        private String businessTitle;
        @SerializedName("page_title")
        private String pageTitle;

        public int getPageId() {
            return pageId;
        }

        public String getPageTitle() {
            return pageTitle;
        }

        public int getGroupId() {
            return groupId;
        }

        public int getBusinessId() {
            return businessId;
        }

        public String getGroupTitle() {
            return groupTitle;
        }

        public String getBusinessTitle() {
            return businessTitle;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Images getImage() {
        return image;
    }

    public String getImageUrl() {
        if (image != null) {
            return image.getMain();
        }
        return "";
    }

    public List<LikePageItem> getPage() {
        return page;
    }
}
