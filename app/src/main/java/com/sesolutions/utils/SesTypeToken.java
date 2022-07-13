package com.sesolutions.utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SesTypeToken {
    private static SesTypeToken instance;

    public static SesTypeToken getInstance() {
        if (instance == null) {
            instance = new SesTypeToken();
        }
        return instance;
    }

    public <T> Type getType(T type) {
        return new TypeToken<T>() {
        }.getType();
    }
}
