package com.sesolutions.responses.Courses.Lecture;


import com.sesolutions.responses.Courses.classroom.ClassroomContent;
import com.sesolutions.responses.Courses.course.CourseContent;
import com.sesolutions.responses.business.BusinessContent;
import com.sesolutions.responses.contest.Contest;
import com.sesolutions.responses.fund.FundContent;
import com.sesolutions.responses.groups.GroupContent;

public class LectureVo {
    private final String type;
    private final Object value;

    public LectureVo(String type, Object vo) {
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


    public LectureContent getItem() {
        if (null == value) {
            return null;
        }
        return (LectureContent) value;
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

    public ClassroomContent getClassroom() {
        if (null == value) {
            return null;
        }
        return (ClassroomContent) value;
    }
    public LectureContent getLecture() {
        if (null == value) {
            return null;
        }
        return (LectureContent) value;
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
