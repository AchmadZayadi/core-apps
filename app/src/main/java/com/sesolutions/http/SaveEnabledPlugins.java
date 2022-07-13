package com.sesolutions.http;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.sesolutions.ui.drawer.DrawerModel;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 11/1/18.
 */

public class SaveEnabledPlugins extends AsyncTask<Context, Void, Void> {
    private final List<DrawerModel.Menus> enabledPlugins;

    public SaveEnabledPlugins(List<DrawerModel.Menus> enabledPlugins) {
        this.enabledPlugins = enabledPlugins;
    }

    @Override
    protected Void doInBackground(Context... params) {
        try {
            List<String> menus = new ArrayList<>();
            for (DrawerModel.Menus menu : enabledPlugins) {
                menus.add(menu.getModule());
                switch (menu.getClazz()) {
                   /* case ModuleUtil.ITEM_CORE_EVENT:
                        Constant.ModuleName.EVENT = menu.getModule();
                        // Constant.ResourceType.SES_EVENT = "event";
                        break;*/
                    case ModuleUtil.ITEM_BLOG:
                        if ("blog".equals(menu.getModule())) {
                            Constant.ModuleName.BLOG = menu.getModule();
                            Constant.ResourceType.BLOG = "blog";
                        }
                        break;
                    case "core_main_album":
                        if ("album".equals(menu.getModule())) {
                            Constant.ModuleName.ALBUM = menu.getModule();
                            //Constant.ResourceType.ALBUM = "album";
                        }
                        break;
                }
            }

            String jsonPlugin = new Gson().toJson(menus);
            AppConfiguration.enabledPlugins = menus;
            SPref.getInstance().updateSharePreferences(params[0], SPref.KEY_PLUGINS, jsonPlugin);
            CustomLog.e("Plugins", "plugins saved/updated");
        } catch (Exception ex) {
            CustomLog.e(ex);
        }
        return null;
    }

   /* @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        CustomLog.e("Plugins", "plugins saved/updated");
    }*/
}
