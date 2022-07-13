package com.sesolutions.responses.forum;

import java.util.List;

public class ForumVo {
    private final String type;
    private final Object value;

    public ForumVo(String type, Object vo) {
        this.type = type;
        value = vo;
    }


    public String getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        if (null == value) {
            return null;
        }
        return (T) value;
    }

    public List<ForumResponse.Category> getCategoryList() {
        if (null == value) {
            return null;
        }
        return (List<ForumResponse.Category>) value;

    }

    public ForumResponse.Category getFormCategory() {
        if (null == value) {
            return null;
        }
        return (ForumResponse.Category) value;
    }

}
