package com.sesolutions.responses.Courses.Test;


import com.sesolutions.responses.Courses.classroom.ClassroomContent;
import com.sesolutions.responses.Courses.course.CourseContent;
import com.sesolutions.responses.business.BusinessContent;
import com.sesolutions.responses.contest.Contest;
import com.sesolutions.responses.fund.FundContent;
import com.sesolutions.responses.groups.GroupContent;

public class TestVo {
    private final String type;
    private final Object value;

    public TestVo(String type, Object vo) {
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

    public Contest getContest() {
        if (null == value) {
            return null;
        }
        return (Contest) value;
    }


    public TestContent getItem() {
        if (null == value) {
            return null;
        }
        return (TestContent) value;
    }
    public FundContent getFund() {
        if (null == value) {
            return null;
        }
        return (FundContent) value;
    }

    public GroupContent getGroup() {
        if (null == value) {
            return null;
        }
        return (GroupContent) value;
    }


    public TestContent getTest() {
        if (null == value) {
            return null;
        }
        return (TestContent) value;
    }

    public CourseContent getCourse() {
        if (null == value) {
            return null;
        }
        return (CourseContent) value;
    }

    public BusinessContent getBusiness() {
        if (null == value) {
            return null;
        }
        return (BusinessContent) value;
    }
}
