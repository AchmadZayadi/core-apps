package com.sesolutions.ui.dashboard.composervo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by root on 13/11/17.
 */

public class ComposerOption {


    @SerializedName("result")
    private Result result;
    @SerializedName("session_id")
    private String session_id;

    public Result getResult() {
        return result;
    }

}
