package com.sesolutions.responses.contest;

import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.videos.Category;

import java.util.List;

public class Contest {
    private final String type;
    private Banner banner;
    private CategoryPage<ContestItem> category;
    private List<Category> categories;
    private ContestItem item;
   // private EntryItem entry;

    public Contest(String type, ContestItem item) {
        this.type = type;
        this.item = item;
    }

    public Contest(String type, CategoryPage<ContestItem> category) {
        this.type = type;
        this.category = category;
    }

    public Contest(String type, List<Category> categories) {
        this.type = type;
        this.categories = categories;
    }

    /*public Contest(String type, EntryItem vo) {
        this.type = type;
        this.entry = vo;
    }*/

    public Contest(String type, Banner vo) {
        this.type = type;
        this.banner = vo;
    }

    public Banner getBanner() {
        return banner;
    }

    public String getType() {
        return type;
    }

    public CategoryPage<ContestItem> getCategory() {
        return category;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public ContestItem getItem() {
        return item;
    }
}
