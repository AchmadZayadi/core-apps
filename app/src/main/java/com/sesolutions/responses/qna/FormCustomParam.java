package com.sesolutions.responses.qna;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.CustomParam;

import java.util.List;

public class FormCustomParam extends CustomParam {

    private boolean isPollDisable;
    private String multi;
    private int maxOptions;
    private List<String> options;

    //used in crowdfunding donation
    @SerializedName("payment_url")
    private String paymentUrl;

    public boolean isPollDisable() {
        return isPollDisable;
    }

    public String getMulti() {
        return multi;
    }

    public int getMaxOptions() {
        return maxOptions;
    }

    public List<String> getOptions() {
        return options;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }
}
