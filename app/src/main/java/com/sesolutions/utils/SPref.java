package com.sesolutions.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sesolutions.responses.ColorTheme;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.responses.SignInResponse;
import com.sesolutions.responses.SlideShowImage;
import com.sesolutions.responses.feed.Attribution;
import com.sesolutions.responses.feed.FeedResponse;
import com.sesolutions.responses.feed.PeopleSuggestion;
import com.sesolutions.ui.common.DefaultDataVo;
import com.sesolutions.ui.dashboard.composervo.ComposerOption;
import com.sesolutions.ui.dashboard.composervo.TextColorString;
import com.sesolutions.ui.drawer.DrawerModel;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.ui.welcome.NameValue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by root on 13/11/17.
 */

public class SPref {

    public static final String KEY_GRAPICS = "grapics";
    public static final String KEY_WELCOME_VIDEO = "video_url";
    public static final String KEY_PLUGINS = "enabled_plugins";
    private final String KEY_DEMO_USER = "demo_users";
    private final String KEY_SOCIAL_LOGIN = "social_login";
    private final String KEY_PEOPLE_SUGGESTION = "people_suggestion";
    private final String NAVIGATION_MENU = "navigation_menu";
    private final String COMPOSER_OPTION = "composer_option";
    private final String FEED_ITEMS = "feed_items";
    private final String KEY_ATTRIBUTION = "attribution";
    private final String KEY_ATTRIBUTION_OPTION = "attribution_option";

    public static final String IMAGE_LOGIN_BG = "login_bg";
    public static final String IMAGE_FORGOT_PASSWORD_BG = "password_bg";
    public static final String IMAGE_RATE_US = "image_rate_us";

    private SPref() {
    }

    // static variable single_instance of type Singleton
    private static SPref sPref = null;

    // private constructor restricted to this class itself

    // static method to create instance of Singleton class
    public static SPref getInstance() {
        if (sPref == null)
            sPref = new SPref();

        return sPref;
    }

    public  void saveUserInfo(Context context,String key, SignInResponse userInfo) {
        Gson gson = new Gson();
        String json = gson.toJson(userInfo);
        context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).edit().putString(key, json).apply();

    }

    public  SignInResponse getUserInfo(Context context,String key) {
        Gson gson = new Gson();
        String json = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getString(key, null);
        Type type = new TypeToken<SignInResponse>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public  void saveDefaultInfo(Context context,String key, DefaultDataVo userInfo) {
        Gson gson = new Gson();
        String json = gson.toJson(userInfo);
        context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).edit().putString(key, json).apply();

    }

    public  DefaultDataVo getDefaultInfo(Context context,String key) {
        Gson gson = new Gson();
        String json = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getString(key, null);
        Type type = new TypeToken<DefaultDataVo>() {
        }.getType();
        return gson.fromJson(json, type);
    }


    public void saveUserMaster(Context context, UserMaster vo, String sessionId) {
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            String json = new Gson().toJson(vo);
            editor.putString(Constant.KEY_USER_MASTER, json);
            if (!TextUtils.isEmpty(sessionId)) {
                Constant.SESSION_ID = "PHPSESSID=" + sessionId + ";";
                editor.putString(Constant.KEY_COOKIE, "PHPSESSID=" + sessionId + ";");
            }
            editor.apply();


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void updateSharePreferences(Context context, String key, String value) {
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void updateSharePreferences(Context context, String key, int value) {
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(key, value);
            editor.apply();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void updateSharePreferences(Context context, String key, boolean value) {
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(key, value);
            editor.apply();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void permessionLocation(Context context, String key, boolean value) {
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.KEY_LOCATION_FIRST_INSTALL, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean(key, value);
            editor.apply();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public String createThemeColors(boolean changeConstantColor, List<NameValue> list) {
        try {

            Map<String, String> map = new HashMap<>();
            ColorTheme theme = new ColorTheme();
            for (NameValue vo : list) {
                map.put(vo.getName(), vo.getValue());
            }
            if (changeConstantColor) {
                Constant.colorPrimary = map.get(Constant.Style.menuButtonActiveTitleColor);
                Constant.navigationTitleColor = map.get(Constant.Style.navigationTitleColor);
                // Constant.navigationActiveColor = map.get(Constant.Style.navigationActiveColor);
                Constant.text_color_1 = map.get(Constant.Style.appFontColor);
                Constant.text_color_2 = map.get(Constant.Style.titleLightColor);
                // Constant.colorAccent = map.get(Constant.Style.navigationColor);
                Constant.foregroundColor = map.get(Constant.Style.appforgroundcolor);
                Constant.backgroundColor = map.get(Constant.Style.appBackgroundColor);
                Constant.dividerColor = map.get(Constant.Style.appSepratorColor);
                //  Constant.statsTextColor = map.get(Constant.Style.statsTextColor);
                Constant.outsideButtonTitleColor = map.get(Constant.Style.outsideButtonTitleColor);
                Constant.outsideButtonBackgroundColor = map.get(Constant.Style.outsideButtonBackgroundColor);
                Constant.outsideNavigationTitleColor = map.get(Constant.Style.outsideNavigationTitleColor);
                Constant.outsideTitleColor = map.get(Constant.Style.outsideTitleColor);
                Constant.menuButtonTitleColor = map.get(Constant.Style.menuButtonTitleColor);
                Constant.ButtonTitleColor = map.get(Constant.Style.ButtonTitleColor);
                Constant.menuButtonBackgroundColor = map.get(Constant.Style.menuButtonBackgroundColor);
                Constant.ButtonBackgroundColor = map.get(Constant.Style.ButtonBackgroundColor);
                Constant.menuButtonActiveTitleColor = map.get(Constant.Style.menuButtonActiveTitleColor);
            }

            theme.toolbar = new ColorTheme.toolbar(map.get(Constant.Style.navigationColor));
            theme.text_style_1 = new ColorTheme.Text_style_1(map.get(Constant.Style.appFontColor));
            theme.text_style_toolbar = new ColorTheme.Text_style_1(map.get(Constant.Style.navigationActiveColor));
            theme.button_image_toolbar = new ColorTheme.image_star(map.get(Constant.Style.navigationActiveColor));
            theme.text_style_stats = new ColorTheme.Text_style_1(map.get(Constant.Style.statsTextColor));
            theme.text_style_2 = new ColorTheme.Text_style_1(map.get(Constant.Style.titleLightColor));
            theme.text_style_no_data = new ColorTheme.Text_style_1(map.get(Constant.Style.noDataLabelTextColor));
            theme.text_style_primary = new ColorTheme.Text_style_1(map.get(Constant.Style.navigationColor));
            theme.text_style_light = new ColorTheme.Text_style_1("#ffffff");
            theme.divider = new ColorTheme.Main_back(map.get(Constant.Style.appSepratorColor));
            theme.hint = new ColorTheme.Text_style_1(map.get(Constant.Style.noDataLabelTextColor));
            theme.Edit_text_style = new ColorTheme.Edit_text_style(map.get(Constant.Style.appFontColor), map.get(Constant.Style.titleLightColor));
            theme.Edit_text_style_toolbar = new ColorTheme.Edit_text_style(map.get(Constant.Style.navigationActiveColor), map.get(Constant.Style.navigationActiveColor).replace("#", "#60"));
            theme.card_style = new ColorTheme.Card_style(map.get(Constant.Style.appforgroundcolor));
            theme.Card_style_toolbar = new ColorTheme.Card_style("#33000000");
            //   theme.main_style = new ColorTheme.Main_back(map.get(Constant.Style.appBackgroundColor));
            theme.background_style = new ColorTheme.Main_back(map.get(Constant.Style.appBackgroundColor));
            theme.foreground_style = new ColorTheme.Main_back(map.get(Constant.Style.appforgroundcolor));
            theme.button_simple = new ColorTheme.button(map.get(Constant.Style.buttonTitleColor), map.get(Constant.Style.buttonBackgroundColor));
            theme.image_star = new ColorTheme.image_star(map.get(Constant.Style.starColor));
            theme.image_tint_primary = new ColorTheme.image_star(map.get(Constant.Style.navigationColor));
            theme.image_tint = new ColorTheme.image_star(map.get(Constant.Style.appFontColor));
            theme.image_tint_2 = new ColorTheme.image_star(map.get(Constant.Style.titleLightColor));
            theme.tab_layout = new ColorTheme.tab_layout(map.get(Constant.Style.appforgroundcolor));
            theme.dialog_style = new ColorTheme.dialog_style(map.get(Constant.Style.appforgroundcolor));
            return new Gson().toJson(theme);

        } catch (Exception e) {
            CustomLog.e(e);
        }

        return null;
    }

    public void saveThemeColors(Context context, List<NameValue> list) {
        SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Constant.THEME_STYLES_JSON = createThemeColors(true, list);
        editor.putString(Constant.KEY_THEME_STYLE, Constant.THEME_STYLES_JSON);
        editor.apply();
    }

    public void updateSharePreferences(Context context, List<SlideShowImage> slideshow, String KEY) {
        try {
            if (null != slideshow && slideshow.size() > 0) {
                SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(KEY, slideshow.size());
                int i = 0;
                for (SlideShowImage vo : slideshow) {
                    editor.putString(KEY + "_" + i, new Gson().toJson(vo));
                    i++;
                }

                editor.apply();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void saveSocialLogin(Context context, List<SearchVo> slideshow) {
        try {
            if (null != slideshow && slideshow.size() > 0) {
                SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(KEY_SOCIAL_LOGIN, slideshow.size());
                int i = 0;
                for (SearchVo vo : slideshow) {
                    editor.putString(KEY_SOCIAL_LOGIN + "_" + i, new Gson().toJson(vo));
                    i++;
                }
                editor.apply();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public List<SearchVo> getSocialLogin(Context context) {
        List<SearchVo> list = new ArrayList<>();
        SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
        int size = pref.getInt(KEY_SOCIAL_LOGIN, -1);
        for (int i = 0; i < size; i++) {
            list.add(new Gson().fromJson(pref.getString(KEY_SOCIAL_LOGIN + "_" + i, "{}"), SearchVo.class));
        }
        return list;
    }


    public void updateSharePreferences(Context context, DefaultDataVo.Result.DemoUser demoUser) {
        try {
            AppConfiguration.isDemoUserAvailable = demoUser != null;
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(KEY_DEMO_USER, new Gson().toJson(demoUser));
            editor.apply();

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public DefaultDataVo.Result.DemoUser getDemoUsers(Context context) {
        String json = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getString(KEY_DEMO_USER, "{}");
        return new Gson().fromJson(json, DefaultDataVo.Result.DemoUser.class);
    }

    public void saveReactionPluginType(Context context, List<ReactionPlugin> list) {
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(Constant.KEY_REACTION_TYPES, list.size());
            int i = 0;
            for (ReactionPlugin vo : list) {
                editor.putString(Constant.KEY_REACTION_TYPES + "_" + i, new Gson().toJson(vo));
                i++;
            }
            editor.apply();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void saveTextColorString(Context context, List<TextColorString> list) {
        try {
            if (null == list) {
                AppConfiguration.isFeedGreetingsAvailable = false;
                return;
            }
            AppConfiguration.isFeedGreetingsAvailable = true;
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(Constant.KEY_TEXT_COLOR_STRING, list.size());
            int i = 0;
            for (TextColorString vo : list) {
                editor.putString(Constant.KEY_TEXT_COLOR_STRING + "_" + i, new Gson().toJson(vo));
                i++;
            }
            editor.apply();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public List<TextColorString> getTextColorString(Context context) {
        List<TextColorString> list = new ArrayList<>();
        SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
        int size = pref.getInt(Constant.KEY_TEXT_COLOR_STRING, -1);
        for (int i = 0; i < size; i++) {
            list.add(new Gson().fromJson(pref.getString(Constant.KEY_TEXT_COLOR_STRING + "_" + i, "{}"), TextColorString.class));
        }
        return list;
    }

    public List<ReactionPlugin> getReactionPlugins(Context context) {
        List<ReactionPlugin> list = new ArrayList<>();
        SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
        int size = pref.getInt(Constant.KEY_REACTION_TYPES, -1);
        for (int i = 0; i < size; i++) {
            list.add(new Gson().fromJson(pref.getString(Constant.KEY_REACTION_TYPES + "_" + i, "{}"), ReactionPlugin.class));
        }
        return list;
    }

    public List<SlideShowImage> getSlideImages(Context context) {
        List<SlideShowImage> list = new ArrayList<>();
        SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
        int size = pref.getInt(Constant.KEY_SLIDE_SHOW, -1);
        for (int i = 0; i < size; i++) {
            list.add(new Gson().fromJson(pref.getString(Constant.KEY_SLIDE_SHOW + "_" + i, "{}"), SlideShowImage.class));
        }
        return list;
    }

    public List<SlideShowImage> getIntroImages(Context context) {
        List<SlideShowImage> list = new ArrayList<>();
        SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
        int size = pref.getInt(KEY_GRAPICS, -1);
        for (int i = 0; i < size; i++) {
            list.add(new Gson().fromJson(pref.getString(KEY_GRAPICS + "_" + i, "{}"), SlideShowImage.class));
        }
        return list;
    }

    public boolean getBoolean(Context context, String key) {
        boolean result = false;
        try {
            result = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getBoolean(key, false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return result;
    }

    public String getString(Context context, String key) {
        String result = "";
        try {
            result = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getString(key, Constant.EMPTY);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return result;
    }

    public String getCookie(Context context) {
        Constant.SESSION_ID = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getString(Constant.KEY_COOKIE, "");
        return Constant.SESSION_ID;
    }

    public int getInt(Context context, String key) {
        int result = 0;
        try {
            result = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getInt(key, 0);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return result;
    }

    public boolean isLoggedIn(Context context) {
        return getBoolean(context, Constant.KEY_LOGGED_IN);
    }


    public UserMaster getUserMasterDetail(Context context) {
        String json = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getString(Constant.KEY_USER_MASTER, "{}");
        return new Gson().fromJson(json, UserMaster.class);
    }

    private static String AUTH_TOKEN = null;

    public String getToken(Context context) {
        if (TextUtils.isEmpty(AUTH_TOKEN)) {
            AUTH_TOKEN = getInstance().getString(context, Constant.KEY_AUTH_TOKEN);
        }
        return AUTH_TOKEN;
    }

    public void saveNavigationMenus(Context context, String response) {
        updateSharePreferences(context, NAVIGATION_MENU, response);
    }

    public void saveComposerOptions(Context context, String response) {
        updateSharePreferences(context, COMPOSER_OPTION, response);
    }

    public void saveFeedItems(Context context, String response) {
        updateSharePreferences(context, FEED_ITEMS, response);
    }


    public DrawerModel getNavigationMenus(Context context) {
        DrawerModel drawerModel = null;
        try {
            drawerModel = null;
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            String response = pref.getString(NAVIGATION_MENU, Constant.EMPTY);
            if (!TextUtils.isEmpty(response)) {
                drawerModel = new Gson().fromJson(response, DrawerModel.class);
            }
        } catch (JsonSyntaxException e) {
            CustomLog.e(e);
        }
        return drawerModel;
    }

    public ComposerOption getComposerOptions(Context context) {
        ComposerOption composerOption = null;
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            String response = pref.getString(COMPOSER_OPTION, Constant.EMPTY);
            if (!TextUtils.isEmpty(response)) {
                composerOption = new Gson().fromJson(response, ComposerOption.class);
            }
        } catch (JsonSyntaxException e) {
            CustomLog.e(e);
        }
        return composerOption;
    }

    public FeedResponse getFeedItems(Context context) {
        FeedResponse feedResponse = null;
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            String response = pref.getString(FEED_ITEMS, Constant.EMPTY);
            if (!TextUtils.isEmpty(response)) {
                feedResponse = new Gson().fromJson(response, FeedResponse.class);
            }
        } catch (JsonSyntaxException e) {
            CustomLog.e(e);
        }
        return feedResponse;
    }

    public void savePeopleSuggestions(Context context, List<PeopleSuggestion> list) {
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(KEY_PEOPLE_SUGGESTION, list.size());
            int i = 0;
            for (PeopleSuggestion vo : list) {
                editor.putString(KEY_PEOPLE_SUGGESTION + "_" + i, new Gson().toJson(vo));
                i++;
            }
            editor.apply();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public List<PeopleSuggestion> getSuggestionList(Context context) {
        List<PeopleSuggestion> list = new ArrayList<>();
        SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
        int size = pref.getInt(KEY_PEOPLE_SUGGESTION, -1);
        for (int i = 0; i < size; i++) {
            list.add(new Gson().fromJson(pref.getString(KEY_PEOPLE_SUGGESTION + "_" + i, "{}"), PeopleSuggestion.class));
        }
        return list;
    }

    public int getLoggedInUserId(Context context) {
        return getInt(context, Constant.KEY_LOGGED_IN_ID);
    }

    public void removeDataOnLogout(Context context) {
        try {
            Constant.SESSION_ID = Constant.EMPTY;
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Constant.KEY_COOKIE, "");
            editor.putString(Constant.KEY_AUTH_TOKEN, "");
            AUTH_TOKEN = null;
            editor.putString(FEED_ITEMS, "");
            editor.putString(COMPOSER_OPTION, "");
            editor.putString(NAVIGATION_MENU, "");
            editor.putString(Constant.KEY_USER_MASTER, "{}");
            editor.putBoolean(Constant.KEY_LOGGED_IN, false);
            editor.putInt(Constant.KEY_LOGGED_IN_ID, 0);

            if (AppConfiguration.IS_FB_LOGIN_ENABLED)
                LoginManager.getInstance().logOut();
            editor.apply();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void removeDataOnLogoutFB(Context context) {
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(Constant.KEY_AUTH_TOKEN, "");
            AUTH_TOKEN = null;
            editor.putString(FEED_ITEMS, "");
            editor.putString(COMPOSER_OPTION, "");
            editor.putString(NAVIGATION_MENU, "");
            editor.putString(Constant.KEY_USER_MASTER, "{}");
            editor.putBoolean(Constant.KEY_LOGGED_IN, false);
            editor.putInt(Constant.KEY_LOGGED_IN_ID, 0);

            if (AppConfiguration.IS_FB_LOGIN_ENABLED)
                LoginManager.getInstance().logOut();
            editor.apply();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void saveAttributionOptions(Context context, List<Attribution> list) {
        if (null == list) return;
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(KEY_ATTRIBUTION_OPTION, list.size());
            int i = 0;
            for (Attribution vo : list) {
                editor.putString(KEY_ATTRIBUTION_OPTION + "_" + i, new Gson().toJson(vo));
                i++;
            }
            editor.apply();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public List<Attribution> getAttributionOptions(Context context) {
        List<Attribution> list = new ArrayList<>();
        SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
        int size = pref.getInt(KEY_ATTRIBUTION_OPTION, -1);
        for (int i = 0; i < size; i++) {
            list.add(new Gson().fromJson(pref.getString(KEY_ATTRIBUTION_OPTION + "_" + i, "{}"), Attribution.class));
        }
        return list;
    }

    public void saveAttribution(Context context, Attribution vo) {
        if (null == vo) return;
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();

            editor.putString(KEY_ATTRIBUTION, new Gson().toJson(vo));

            editor.apply();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public Attribution getAttribution(Context context) {
        Attribution vo = null;
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            String response = pref.getString(KEY_ATTRIBUTION, Constant.EMPTY);
            if (!TextUtils.isEmpty(response)) {
                vo = new Gson().fromJson(response, Attribution.class);
            }
        } catch (JsonSyntaxException e) {
            CustomLog.e(e);
        }
        return vo;
    }

    public void loadEnabledPlugins(Context context) {
        try {
            SharedPreferences pref = context.getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE);
            String response = pref.getString(KEY_PLUGINS, Constant.EMPTY);
            if (!TextUtils.isEmpty(response)) {
                AppConfiguration.enabledPlugins = new Gson().fromJson(response, List.class);
            }
        } catch (JsonSyntaxException e) {
            CustomLog.e(e);
        }
    }


    public boolean isBasicPlugins(Context context,String pluginname) {
        boolean isexit=false;
        try {
            try {
                JsonObject objectdata= SPref.getInstance().getDefaultInfo(context,Constant.KEY_APPDEFAULT_DATA).getResult().getCore_modules_enabled();
                //  String ojj=objectdata.getString("blog");
                Map<String, String> mapObj = new Gson().fromJson(
                        objectdata, new com.google.common.reflect.TypeToken<HashMap<String, String>>() {}.getType()
                );
                for (Map.Entry<String, String> e : mapObj.entrySet()) {
                    if(pluginname.equalsIgnoreCase(""+e.getValue())){
                        isexit=true;
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
                isexit=false;
            }

        } catch (JsonSyntaxException e) {
            CustomLog.e(e);
        }

        return isexit;
    }


    public boolean isUserOwner(Context context, int ownerId) {
        return ownerId == getLoggedInUserId(context);
    }

}

