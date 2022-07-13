package com.sesolutions.responses.store.product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.Announcement;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.store.StoreContent;
import com.sesolutions.responses.store.StoreVo;
import com.sesolutions.responses.videos.Videos;

import java.util.ArrayList;
import java.util.List;

public class ProductResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;
    private com.sesolutions.responses.store.product.ProductResponse.Result result;

    public class SliderImage {

        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("value")
        @Expose
        private String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public class Result extends PaginationHelper {
        private List<CategoryPage<StoreContent>> category;
        private List<StoreContent> popularStores;
        private List<CategoryPage<StoreContent>> categories;
        private List<StoreContent> products;
        private List<Options> options;
        private List<Options> sort;
        private List<Videos> videos;
        private Options button;
        private List<Announcement> announcements;
        private List<StoreContent> wishlists;


        @SerializedName("wishlist")
        private WishList wishlist;

        public WishList getWishlist() {
            return wishlist;
        }

        //For View Store
        private StoreContent product;
        private Options callToAction;
        private List<StoreContent> relatedStores;
        private List<Options> about;
        @SerializedName("menus")
        private List<Options> menus;
        private String message;
        private List<Albums> photo;
        @SerializedName("can_create")
        private boolean canCreateAlbum;

        @SerializedName("slider_images")
        private List<SliderImage> slider_images;

        public List<SliderImage> getSliderImages() {
            return slider_images;
        }

        public void setSliderImages(List<SliderImage> slider_images) {
            this.slider_images = slider_images;
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

        public StoreContent getStore() {
            return product;
        }

        public List<StoreContent> getRelatedStores() {
            return relatedStores;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<StoreContent>> getCategories() {
            return categories;
        }

        public List<Announcement> getAnnouncements() {
            return announcements;
        }

        public List<StoreContent> getStores() {
            return products;
        }
       /* public List<FundContent> getCampaigns() {
            return campaigns;
        }*/

        public List<StoreVo> getStores(String screenType) {
            List<StoreVo> result = new ArrayList<>();
            if (products != null) {
                for (StoreContent vo : products) {
                    result.add(new StoreVo(screenType, vo));
                }
            }
            return result;
        }

        public List<StoreContent> getWishlists() {
            return wishlists;
        }

        public List<StoreVo> getWishlists(String screenType) {
            List<StoreVo> result = new ArrayList<>();
            if (wishlists != null) {
                for (StoreContent vo : wishlists) {
                    result.add(new StoreVo(screenType, vo));
                }
            }
            return result;
        }

        public List<StoreVo> getCategories(String screenType) {
            List<StoreVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<StoreContent> vo : categories) {
                    result.add(new StoreVo(screenType, vo));
                }
            }
            return result;
        }

        public List<StoreContent> getPopularStores() {
            return popularStores;
        }

        public List<StoreContent> getAllProducts() {
            return products;
        }

        public List<CategoryPage<StoreContent>> getCategory() {
            return category;
        }

        public List<Videos> getVideos() {
            return videos;
        }

    }


    public String getSessionId() {
        return sessionId;
    }

    public com.sesolutions.responses.store.product.ProductResponse.Result getResult() {
        return result;
    }
}
