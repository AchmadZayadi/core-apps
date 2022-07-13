package com.sesolutions.responses.page;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;

import java.util.List;

public class CategoryPage<E> {
    @SerializedName("category_images")
    private Images images;
    private String slug;
    @SerializedName("category_name")
    private String name;
    @SerializedName("total_page_categories")
    private String totalPageCategories;

    @SerializedName("total_contest_categories")
    private String total_contest_categories;


    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("see_all")
    private boolean seeAll;
    private String image;

    private Images icon;
    @SerializedName("icon_colored")
    private Images iconColored;

    @SerializedName("items")
    private List<E> items;

    public boolean isSeeAll() {
        return seeAll;
    }

    public List<E> getItems() {
        return items;
    }

    public Images getImages() {
        return images;
    }

    public String getImageUrl() {
        if (images != null)
            return images.getNormal();
        else if (null != image) {
            return image;
        } else if (null != iconColored) {
            return iconColored.getNormal();
        }
        return "";
    }

    public String getTotal_contest_categories() {
        return total_contest_categories;
    }

    public void setTotal_contest_categories(String total_contest_categories) {
        this.total_contest_categories = total_contest_categories;
    }

    public String getSlug() {
        return slug;
    }

    public String getCategoryName() {
        return name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getTotalPageCategories() {
        return totalPageCategories;
    }
}
