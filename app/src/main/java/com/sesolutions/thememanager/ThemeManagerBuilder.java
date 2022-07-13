package com.sesolutions.thememanager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;
import java.util.List;

/*import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;*/

/**
 * Created by omkar on 26/5/17.
 */

public class ThemeManagerBuilder {

    JsonObject rulesJson;
    int numOfRequestsPending = 0;
    boolean isBuildInvocted = false;
    OnLoadResourceListener listener;
    List<OnLoadResourceListener> listenerList = new ArrayList<OnLoadResourceListener>();


    ThemeManager manager;

    private static ThemeManagerBuilder builder;

    public static ThemeManagerBuilder builder() {
        return new ThemeManagerBuilder();
    }

    public static ThemeManagerBuilder getInstance() {
        if (builder == null) {
            builder = new ThemeManagerBuilder();
        }
        return builder;
    }

    public ThemeManagerBuilder withJson() {
        Gson gson = new Gson();
        try {
            rulesJson = gson.fromJson(Constant.THEME_STYLES_JSON, JsonElement.class).getAsJsonObject();
            CustomLog.e("rules", "" + gson.toJson(rulesJson));
           /* if (rulesJson == null) {
                rulesJson = newJsonElement;
            } else {
                rulesJson = mergeJson(new JsonObject[]{rulesJson, newJsonElement});
            }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return this;
    }


   /* public ThemeManagerBuilder withAsset(String fileName, Context context) {
        Gson gson = new Gson();
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");
            JsonObject newJsonElement = gson.fromJson(jsonString, JsonElement.class).getAsJsonObject();

            if (rulesJson == null) {
                rulesJson = newJsonElement;
            } else {
                rulesJson = mergeJson(new JsonObject[]{rulesJson, newJsonElement});
            }
        } catch (IOException e) {
            Log.e("Error in reading file", e.getMessage(), e);
        }

        return this;
    }*/

    public void addListener(OnLoadResourceListener listener) {
        if (listenerList.isEmpty()) {
            if (manager == null) {
                manager = new ThemeManager(rulesJson);
            }
            listener.onLoadFinished(manager);
        } else {
            this.listenerList.add(listener);
        }
    }
}
