package com.sesolutions.responses.videos;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;

/**
 * Created by root on 5/12/17.
 */


public class Category {

    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("subcat_id")
    private int subCategoryId;
    @SerializedName("subsubcat_id")
    private int subSubCategoryId;
    @SerializedName("label")
    private String label;
    @SerializedName("category_name")
    private String name;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("cat_icon")
    private String catIcon;
    private String description;
    @SerializedName("count")
    private String count;
    @SerializedName("total_event_categories")
    private String totaleventcategories;
    @SerializedName("total_contest_categories")
    private String total_contest_categories;

    private Images images;
    private Images icon;
    @SerializedName("icon_colored")
    private Images iconColored;



    //for contest category
    private String image;

    public String getTotaleventcategories() {
        return totaleventcategories;
    }

    public void setTotaleventcategories(String totaleventcategories) {
        this.totaleventcategories = totaleventcategories;
    }

    /*custom variable
            O : category
            1 : sub_category
            2 : sub_subcategory*/
    private int categoryLevel = 0;
    private CategoryImagesBean category_images;

    public int getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(int categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        if (images != null)
            return images.getMain();
        else if (null != image) {
            return image;
        } else {
            return "";
        }
    }

    public String getIcon() {
        if (null != icon) {
            return icon.getNormal();
        }
        return "";
    }

    public String getIconColored() {
        if (null != iconColored) {
            iconColored.getNormal();
        }
        return "";
    }

    public CategoryImagesBean getCategory_images() {
        return category_images;
    }

    public void setCategory_images(CategoryImagesBean category_images) {
        this.category_images = category_images;
    }

    public String getTotal_contest_categories() {
        return total_contest_categories;
    }

    public void setTotal_contest_categories(String total_contest_categories) {
        this.total_contest_categories = total_contest_categories;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public int getSubSubCategoryId() {
        return subSubCategoryId;
    }

    public String getDescription() {
        return description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getLabel() {
        if (null != label)
            return label;
        return name;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCatIcon() {
        if (null != icon) {
            return icon.getNormal();
        }
        return catIcon;
    }

    public void setCatIcon(String catIcon) {
        this.catIcon = catIcon;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }


    public static class CategoryImagesBean {
        private String main;
        @SerializedName("icon")
        private String iconX;
        private String thumb;


        public String getMain() {
            return main;
        }

        public String getIconX() {
            return iconX;
        }

        public String getThumb() {
            return thumb;
        }
    }
}
