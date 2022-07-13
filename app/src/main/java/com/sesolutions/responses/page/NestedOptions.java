package com.sesolutions.responses.page;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Options;

import java.util.ArrayList;
import java.util.List;

public class NestedOptions {
    @SerializedName("name")
    private String name;
    @SerializedName("value")
    private JsonElement value;
    @SerializedName("label")
    private String label;


    public String getName() {
        return name;
    }


    public List<Options> getValueList() {
        List<Options> list = new ArrayList<>();
        if (value != null && value.isJsonArray()) {
            JsonArray arr = value.getAsJsonArray();
            for (JsonElement e : arr) {
                list.add(new Gson().fromJson(e, Options.class));
            }
        }
        return list;
    }

    public String getValueString() {
        if (value != null) {
            return value.getAsString();
        }
        return null;
    }

    public String getLabel() {
        return label;
    }

    public String getStatsString() {
        String s = "";
        for (Options opt : getValueList()) {
            s += ", " + opt.getValue() + " " + opt.getLabel();
        }
        return s.length() > 0 ? s.substring(1) : s;
    }
}
