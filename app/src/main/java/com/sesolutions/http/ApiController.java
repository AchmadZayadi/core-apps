package com.sesolutions.http;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import org.apache.http.client.methods.HttpPost;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by root on 11/1/18.
 */

public class ApiController {

    private final int REQ;
    private final String url;
    private final Map<String, Object> map;
    private final Context context;
    private final String cookie;
    private final OnUserClickedListener<Integer, Object> listener;
    private int POS;
    private String requestType = HttpPost.METHOD_NAME;

    public ApiController(String url, Map<String, Object> map, Context context, OnUserClickedListener<Integer, Object> listener, int req) {
        this.url = url;
        this.map = map;
        this.REQ = req;
        this.context = context;
        this.listener = listener;
        this.cookie = TextUtils.isEmpty(Constant.SESSION_ID) ? context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getString(Constant.KEY_COOKIE, Constant.EMPTY) : Constant.SESSION_ID;
    }

    public ApiController setExtraKey(int position) {
        this.POS = position;
        return this;
    }

    public ApiController setPostType(String type) {
        this.requestType = type;
        return this;
    }

    public void execute() {
        HttpRequestVO request = new HttpRequestVO(url);
        if (map != null) {
            request.params.putAll(map);
        }
        request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
        request.headres.put(Constant.KEY_COOKIE, cookie);
        request.requestMethod = requestType;
        Handler.Callback callback = msg -> {
            try {
                String response = (String) msg.obj;
                CustomLog.e("response", "" + response);
                if (listener != null) {
                    listener.onItemClicked(REQ, msg.obj, POS);
                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
            return true;
        };
        new HttpRequestHandler(context, new Handler(callback)).run(request);
    }
}
