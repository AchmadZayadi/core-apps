package com.sesolutions.responses.Courses.Test;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Testquestion {

    @SerializedName("question")
    private String question;
    private List<Answer> currectAnswer;



    public List<Answer> getCurrectAnswer(){
        return currectAnswer;
    }

    public String getQuestion(){
        return question;
    }
}
