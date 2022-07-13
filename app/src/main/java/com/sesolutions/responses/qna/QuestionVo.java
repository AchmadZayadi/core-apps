package com.sesolutions.responses.qna;


public class QuestionVo {
    private final String type;
    private final Object content;

    public QuestionVo(String type, Object vo) {
        this.type = type;
        content = vo;
    }


    public String getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <T> T getContent() {
        if (null == content) {
            return null;
        }
        return (T) content;
    }

    public Question getQuestion() {
        if (null == content) {
            return null;
        }
        return (Question) content;
    }


   /*

    public PageContent getItem() {
        if (null == value) {
            return null;
        }
        return (PageContent) value;
    }

    public GroupContent getGroup() {
        if (null == value) {
            return null;
        }
        return (GroupContent) value;
    }

    public BusinessContent getBusiness() {
        if (null == value) {
            return null;
        }
        return (BusinessContent) value;
    }*/
}
