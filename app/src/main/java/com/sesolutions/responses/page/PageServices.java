package com.sesolutions.responses.page;

import com.sesolutions.responses.feed.Images;

public class PageServices {
    private String title;
    private long duration;
    private String durationtype;
    private String description;
    private int price;
    private Images images;

    public String getTitle() {
        return title;
    }

    public long getDuration() {
        return duration;
    }

    public String getDurationtype() {
        return durationtype;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public Images getImages() {
        return images;
    }

    public String getDurationString() {
        if (duration > 0) {
            return duration + " " + durationtype;
        }
        return null;
    }
}
