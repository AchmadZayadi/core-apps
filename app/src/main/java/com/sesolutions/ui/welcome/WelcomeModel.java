package com.sesolutions.ui.welcome;

/**
 * Created by root on 1/11/17.
 */

public class WelcomeModel {

    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WelcomeModel(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
