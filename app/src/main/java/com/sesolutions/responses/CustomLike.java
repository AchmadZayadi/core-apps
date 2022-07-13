package com.sesolutions.responses;

import com.google.gson.JsonElement;

public class CustomLike {
    private JsonElement title;
    //custom variable to handle "title" type
    public String customTitle;

    //Custom logic to handle ARRAY and STRING object for key 'title'
    public String getTitle() {
        if (null != customTitle) {
            return customTitle;
        } else if (null != title && title.isJsonPrimitive()) {
            customTitle = title.getAsString();
        } else if (null != title) {
            customTitle = title.getAsJsonArray().get(0).getAsString();
        }
        return customTitle;
    }
}
