package com.sesolutions.http;

public interface ParserCallbackInterface {

    void onResponseSuccess(int reqCode, Object result);

    void onConnectionTimeout(int reqCode, String result);
}
