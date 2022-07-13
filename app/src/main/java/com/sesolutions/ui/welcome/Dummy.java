package com.sesolutions.ui.welcome;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.CustomParam;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 3/11/17.
 */

public class Dummy extends ErrorResponse {

    @SerializedName("result")
    private Result result;
    @SerializedName("session_id")
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Formfields {
        @SerializedName("type")
        private String type;
        @SerializedName("multiple")
        private String multiple;
        @SerializedName("name")
        private String name;
        @SerializedName("label")
        private String label;
        @SerializedName("description")
        private String description;
        @SerializedName("isRequired")
        private String isrequired;
        @SerializedName("value")
        private JsonElement value;
        private boolean isBold;

        private boolean isSelected;
        private String option;

        public String getOption() {
            return option;
        }

        public void setOption(String animal) {
            this.option = animal;
        }

        public boolean getSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
        public boolean isRequired() {
            return !TextUtils.isEmpty(isrequired) && isrequired.equals("1");
        }

        //  private Map<String, String> multiOptions;
        private JsonElement multiOptions;

        public Map<String, String> getMultiOptions() {
            Map<String, String> multiOption = new LinkedHashMap<>();
            try {
                if (multiOptions.isJsonObject()) {
                    multiOption = new Gson().fromJson(multiOptions.toString(), LinkedHashMap.class);
                } else {
                    List<String> option = new Gson().fromJson(multiOptions.toString(), List.class);
                    if (option != null) {
                        for (int i = 0; i < option.size(); i++) {
                            multiOption.put("" + i, option.get(i));
                        }
                    }

                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
            return multiOption;
        }

        public JsonElement getOptionAsJson() {
            return multiOptions;
        }

        public List<String> getMultiOptionsList() {
            List<String> result = new ArrayList<>();
            Map<String, String> map = getMultiOptions();
            try {
                if (null != map) {
                    Log.e("multiOptions", new Gson().toJson(map));
                    result.addAll(map.values());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        public void setMultiOptions(JsonElement multiOptions) {
            this.multiOptions = multiOptions;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMultiple() {
            return multiple;
        }

        public void setMultiple(String multiple) {
            this.multiple = multiple;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getValue() {
            return null != value && value.isJsonPrimitive() ? value.getAsString() : "";
        }

        public String getValueString() {
            return value.toString();
        }

        public boolean instanceOfJsonObject() {
            return value.isJsonObject();
        }

        public boolean instanceOfJsonArray() {
            return null != value && value.isJsonArray();
        }

        public void setValue(JsonElement value) {
            this.value = value;
        }

        public void setStringValue(Object value) {
            this.value = new JsonParser().parse("\"" + value + "\"");
        }

        public boolean isTitleBold() {
            return isBold;
        }

        public void setTitleBold(boolean isBold) {
            this.isBold = isBold;
        }
    }

    public static class Result {
        @SerializedName("formFields")
        private List<Formfields> formfields;
        private JsonElement customParams;
        @SerializedName("loggedin_user_id")
        private int loggedinUserId;
        @SerializedName("enabled_currencies")
        private JsonElement enabledCurrencies;;
        private String message;

        public JsonElement getCustomParams() {
            return customParams;
        }

        public <T extends CustomParam> T getCustomParams(Class<T> clazz) {
            if (null != customParams) {
                return new Gson().fromJson(customParams, clazz);
            }
            return null;
        }
        public Map<String, String> getenabledCurrencies() {
            Map<String, String> multiOption = new LinkedHashMap<>();
            try {
                if (enabledCurrencies.isJsonObject()) {
                    multiOption = new Gson().fromJson(enabledCurrencies.toString(), LinkedHashMap.class);
                } else {
                    List<String> option = new Gson().fromJson(enabledCurrencies.toString(), List.class);
                    if (option != null) {
                        for (int i = 0; i < option.size(); i++) {
                            multiOption.put("" + i, option.get(i));
                        }
                    }

                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
            return multiOption;
        }

        public List<Formfields> getFormfields() {
            return formfields;
        }

        public void setFormfields(List<Formfields> formfields) {
            this.formfields = formfields;
        }

        public Formfields getFormFielsByName(String name) {
            if (null != formfields)
                for (Formfields vo : formfields) {
                    if (name.equals(vo.getName()))
                        return vo;
                }
            return null;
        }

        public String getMessage() {
            return message;
        }

        public int getLoggedinUserId() {
            return loggedinUserId;
        }

        public void setLoggedinUserId(int loggedinUserId) {
            this.loggedinUserId = loggedinUserId;
        }
    }
}
