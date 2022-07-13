package com.sesolutions.responses.fund;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.CategoryPage;
import com.sesolutions.responses.page.PageVo;

import java.util.ArrayList;
import java.util.List;

public class FundResponse extends ErrorResponse {
    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result extends PaginationHelper {
        private List<CategoryPage<FundContent>> category;
        //  private List<Category> category;
        private List<FundContent> donations;
        private List<CategoryPage<FundContent>> categories;
        private List<FundContent> campaigns;
        private List<Options> options;
        private List<Options> sort;
        private List<Options> buttons;
        private JsonElement button;

        //For View Page
        @SerializedName(value = "campaign", alternate = {"description"})
        private FundContent campaign;
        private List<FundContent> relatedPages;
        private List<Options> aboutme;
        private List<Options> menus;
        @SerializedName("slide_image")
        private List<Albums> slides;
        private String message;
        private List<Albums> photo;
        @SerializedName("can_create")
        private boolean canCreateAlbum;
        @SerializedName(value = "donors", alternate = {"rewards"})
        private List<Donor> donors;


        /*public Options getButton() {
            return button;
        }*/
        public Options getButton() {
            if (null != button) {
                if (button.isJsonObject()) {
                    return new Gson().fromJson(button, Options.class);
                } else {
                    return new Gson().fromJson(button.getAsJsonArray().get(0), Options.class);
                }
            } else return null;

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


        public List<Options> getOptions() {
            return options;
        }

        public List<Albums> getPhoto() {
            return photo;
        }

        public FundContent getCampaign() {
            return campaign;
        }

        public List<FundContent> getRelatedPages() {
            return relatedPages;
        }

        public List<Options> getAbout() {
            return aboutme;
        }

        public List<Options> getMenus() {
            return menus;
        }

        public List<CategoryPage<FundContent>> getCategories() {
            return categories;
        }

        public List<Options> getButtons() {
            return buttons;
        }

        public List<FundContent> getCampaigns() {
            return campaigns;
        }
       /* public List<FundContent> getCampaigns() {
            return campaigns;
        }*/


        public List<PageVo> getCampaigns(String screenType) {
            List<PageVo> result = new ArrayList<>();
            if (campaigns != null) {
                for (FundContent vo : campaigns) {
                    result.add(new PageVo(screenType, vo));
                }
            }
            return result;
        }

        public List<PageVo> getDonations(String screenType) {
            List<PageVo> result = new ArrayList<>();
            if (donations != null) {
                for (FundContent vo : donations) {
                    result.add(new PageVo(screenType, vo));
                }
            }
            return result;
        }


        public List<PageVo> getCategories(String screenType) {
            List<PageVo> result = new ArrayList<>();
            if (categories != null) {
                for (CategoryPage<FundContent> vo : categories) {
                    result.add(new PageVo(screenType, vo));
                }
            }
            return result;
        }

        public List<Albums> getSlides() {
            return slides;
        }

        public List<FundContent> getDonations() {
            return donations;
        }

        /*public List<Category> getCategory() {
            return category;
        }*/
        public List<CategoryPage<FundContent>> getCategory() {
            return category;
        }

        public List<Donor> getDonors() {
            return donors;
        }
    }


    public String getSessionId() {
        return sessionId;
    }

    public Result getResult() {
        return result;
    }
}
