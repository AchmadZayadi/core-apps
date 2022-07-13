package com.sesolutions.responses;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class SesResponse extends ErrorResponse {
    private JsonElement result;

    public <T extends PaginationHelper> T getResult(Class<T> clazz) {
        return new Gson().fromJson(result, clazz);
    }

    public String getStringResult() {
        return result.getAsString();
    }
}
