package com.sesolutions.responses.unsplash;

import com.google.gson.annotations.SerializedName;

public class SesWallpaper {
    private final int type;
    private int width;
    private int height;
    private String color;

    private ImageUnsplash urls;
    private ImageUnsplash links;


    public SesWallpaper(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getColor() {
        return color;
    }

    public String getFull() {
        return null != urls ? urls.getFull() : null;
    }

    public String getSmall() {
        return null != urls ? urls.getSmall() : null;
    }

    public String getRegular() {
        return null != urls ? urls.getRegular() : null;
    }

    public String getDownload() {
        return null != links ? links.getDownload() : null;
    }

    private class ImageUnsplash {
        private String raw;
        private String full;
        private String regular;
        private String small;
        private String thumb;

        private String self;
        private String html;
        private String download;
        @SerializedName("download_location")
        private String downloadLocation;

        public String getFull() {
            return full;
        }

        public String getRegular() {
            return regular;
        }

        public String getSmall() {
            return small;
        }

        public String getDownload() {
            return download;
        }
    }
}
