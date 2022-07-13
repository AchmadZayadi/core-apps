package com.sesolutions.responses;

import com.sesolutions.responses.music.Albums;

import java.util.List;

/**
 * Created by AheadSoft on 11-04-2018.
 */

public class SongParent {
    private String name;
    private int type;
    private List<Albums> childList;

    public SongParent(String name, List<Albums> childList) {
        this.name = name;
        this.childList = childList;
    }

    public SongParent(List<Albums> childList) {
        this.type = 1;
        this.childList = childList;
    }

    public SongParent(String name, int type, List<Albums> childList) {
        this.name = name;
        this.type = type;
        this.childList = childList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Albums> getChildList() {
        return childList;
    }

    public void setChildList(List<Albums> childList) {
        this.childList = childList;
    }

    public boolean isChildValid() {
        return childList != null && childList.size() > 0;

    }
}
