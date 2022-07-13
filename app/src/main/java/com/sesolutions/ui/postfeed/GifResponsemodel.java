package com.sesolutions.ui.postfeed;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GifResponsemodel {


    @SerializedName("result")
    public ResultDTO result;
    @SerializedName("session_id")
    public String sessionId;

    public static class ResultDTO {
        @SerializedName("gif")
        public List<GifDTO> gif;
        @SerializedName("loggedin_user_id")
        public int loggedinUserId;

        public static class GifDTO {
            @SerializedName("img_url")
            public String imgUrl;
        }
    }
}
