package com.sesolutions.ui.drawer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.http.SaveEnabledPlugins;
import com.sesolutions.listeners.CustomListAdapterInterface;
import com.sesolutions.materialtaptargetprompt.AppTourUtils;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.ui.dashboard.MainActivity;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * Created by mrnee on 10/7/2017.
 */

public class DrawerFragment extends BaseFragment implements CustomListAdapterInterface, View.OnClickListener {
    private ListView listDrawer;
    private DrawerAdapter listAdapter;
    private DrawerModel drawerModel;
    private ImageView ivLogo;
    private ImageView ivLogo1;
    ImageView ivVerifyDrawer;
    private TextView tvName;
    private List<DrawerModel.Menus> menus = new ArrayList<>();
    private View v;
    private RelativeLayout rlDrawerHeader2;
    private TextView tvDrawerHeader1;
    private LinearLayout rlDrawerFooter;
    private ImageView ivCoverPhoto;
    private int text_color_1;
    private int foregroundColor;
    private int backgroundColor;
    private int text_color_2;


    public static DrawerFragment getInstance(DrawerModel drawerModel) {
        DrawerFragment drawerFragment = new DrawerFragment();
        drawerFragment.drawerModel = drawerModel;
        return drawerFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            Util.showImageWithGlide(ivLogo, SPref.getInstance().getUserMasterDetail(context).getPhotoUrl(), context, R.drawable.placeholder_square);
        } catch (Exception e) {
            //todo solve this
            CustomLog.e(e);
        }
    }

    public void showIntro() {
        if (AppConfiguration.IS_APP_TOUR_ENABLED)
            AppTourUtils.showDrawerSequence(this, ivLogo, listDrawer.getChildAt(7));
    }

   /* public void showIntro() {
        TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                TapTarget.forView(ivLogo, "Profile", "View or Edit your profile page")
                        // All options below are optional
                        //.outerCircleAlpha(0.85f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(false)                   // Whether to tint the target view's color

                        //.icon(Drawable)                     // Specify a custom drawable to draw as the target
                        .targetRadius((int) (ivLogo.getWidth() / (getResources().getDisplayMetrics().density * 2)))
                        .dimColor(R.color.transparent_black_light)
                        .outerCircleColorInt(SesColorUtils.getPrimaryColor(context))
                        .transparentTarget(false)


                ,                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        CustomLog.d("AppIntro", "targetClicked");
                    }
                });
    }
*/

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_drawer_layout, container, false);
        try {
            new ThemeManager().applyTheme((ViewGroup) v, context);
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        text_color_1 = Color.parseColor(Constant.text_color_1);
        text_color_2 = Color.parseColor(Constant.text_color_2);
        backgroundColor = Color.parseColor(Constant.backgroundColor);
        foregroundColor = Color.parseColor(Constant.foregroundColor);
        ivLogo = v.findViewById(R.id.iv_logo_drawer);
        ivLogo1 = v.findViewById(R.id.iv_logo_drawer1);
        ivVerifyDrawer = v.findViewById(R.id.iv_verify_drawer);
        ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto);
        tvName = v.findViewById(R.id.tv_name);
        tvDrawerHeader1 = v.findViewById(R.id.tvDrawerHeader_1);
        rlDrawerHeader2 = v.findViewById(R.id.rlDrawerHeader_2);
        rlDrawerHeader2.setOnClickListener(this);
        tvDrawerHeader1.setOnClickListener(this);
        tvName.setOnClickListener(this);
        ivLogo.setOnClickListener(this);
        ivLogo1.setOnClickListener(this);
        rlDrawerFooter = v.findViewById(R.id.rlFooter);
        v.findViewById(R.id.tvEditProfile).setOnClickListener(this);

        listDrawer = v.findViewById(R.id.listview_drawer);
        listAdapter = new DrawerAdapter(context, R.layout.row_drawer, menus, this);
        listDrawer.setAdapter(listAdapter);

        setData();
    }

    public void setDrawerModel(DrawerModel drawerModel) {
        this.drawerModel = drawerModel;
    }

    public void setDataProfileImage(String imaGEURL) {
        if(imaGEURL!=null && imaGEURL.length()>0){
            Util.showImageWithGlide(ivCoverPhoto, imaGEURL, context, R.drawable.placeholder_3_2);
        }
    }


    public void setData() {
        try {
            if (drawerModel != null) {
                DrawerModel.Result result = drawerModel.getResult();
                if (result != null) {
                    int userId = result.getLoggedinUserId();
                    Boolean  loggedinId = SPref.getInstance().getBoolean(context, Constant.KEY_LOGGED_IN);
                    if (loggedinId) {
                        try {
                            rlDrawerHeader2.setVisibility(View.VISIBLE);
                            tvDrawerHeader1.setVisibility(View.GONE);
                            rlDrawerFooter.setVisibility(View.GONE);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    else {
                        try {
                            rlDrawerHeader2.setVisibility(View.GONE);
                            tvDrawerHeader1.setVisibility(View.VISIBLE);
                            rlDrawerFooter.setVisibility(View.VISIBLE);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        v.findViewById(R.id.bSignIn).setOnClickListener(this);
                        v.findViewById(R.id.bSignUp).setOnClickListener(this);
                        v.findViewById(R.id.rlFacebook).setOnClickListener(this);
                        //noinspection ConstantConditions
                       // v.findViewById(R.id.rlFacebook).setVisibility(AppConfiguration.IS_FB_LOGIN_ENABLED ? View.VISIBLE : View.GONE);
                    }
                    tvName.setText(SPref.getInstance().getUserMasterDetail(context).getDisplayname());

                    Util.showImageWithGlide(ivCoverPhoto, result.getCoverPhoto(), context, R.drawable.placeholder_3_2);
                    if (AppConfiguration.memberImageShapeIsRound) {
                        v.findViewById(R.id.cvProfileImage).setVisibility(View.GONE);
                        ivLogo.setVisibility(View.VISIBLE);
                        if (SPref.getInstance().getUserMasterDetail(context).getLevelId() == 3){
                            ivVerifyDrawer.setImageResource(R.drawable.ic_verified);
                        }
                        Util.showImageWithGlide(ivLogo, SPref.getInstance().getUserMasterDetail(context).getPhotoUrl(), context, R.drawable.placeholder_square);
                    } else {
                        v.findViewById(R.id.cvProfileImage).setVisibility(View.VISIBLE);
                        ivLogo.setVisibility(View.GONE);
                        Util.showImageWithGlide(ivLogo1, SPref.getInstance().getUserMasterDetail(context).getPhotoUrl(), context, R.drawable.placeholder_square);
                        if (SPref.getInstance().getUserMasterDetail(context).getLevelId() == 3){
                            ivVerifyDrawer.setImageResource(R.drawable.ic_verified);
                        }
                    }

                }
                if (null != result.getMenus()) {
                    menus = result.getMenus();
                    listDrawer = v.findViewById(R.id.listview_drawer);
                    listAdapter = new DrawerAdapter(getActivity(), R.layout.row_drawer, menus, this);
                    listDrawer.setAdapter(listAdapter);

                    //save all enabled plugins list
                    new SaveEnabledPlugins(result.getMenus()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onBackPressed() {
        closeDrawer();
    }


    public View getView(int position, View convertView, ViewGroup parent, int resourceID, LayoutInflater inflater) {

        try {
            Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(resourceID, parent, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            final DrawerModel.Menus menu = menus.get(position);
            Integer type = menu.getType();
            if (type == 0) {
                holder.rlRow.setVisibility(View.GONE);
                holder.tvHeading.setVisibility(View.VISIBLE);
                holder.tvHeading.setTextColor(text_color_2);
                holder.tvHeading.setBackgroundColor(backgroundColor);
                holder.tvHeading.setText(menu.getLabel());
            } else {
                holder.rlRow.setBackgroundColor(foregroundColor);
                holder.rlRow.setVisibility(View.VISIBLE);
                holder.tvHeading.setVisibility(View.GONE);
                holder.tvItem.setTextColor(text_color_1);
                holder.tvItem.setText(menu.getLabel());
                Util.showImageWithGlide(holder.ivIcon, menu.getIcon(), context);
                holder.rlRow.setOnClickListener(v -> onDrawerItemClicked(menu));

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return convertView;
    }

    private void onDrawerItemClicked(DrawerModel.Menus menu) {
        try {
            /*if (!selectedScreen.equals(Constant.ModuleUtil.ITEM_SIGN_OUT) && selectedScreen.equals(menu.getClazz())) {
                closeDrawer();
                return;
            }*/
            String selectedScreen = menu.getClazz();
            CustomLog.e("DrawerItemClicked", "" + new Gson().toJson(menu));
            switch (selectedScreen) {
                case ModuleUtil.ITEM_SIGN_OUT:
                    showDialog(Constant.MSG_LOGOUT);
                    break;
                case ModuleUtil.ITEM_NOTIFICATION:
                    closeDrawer();
                    gotoNotificationFragment();
                    break;
                case ModuleUtil.ITEM_FRIEND_REQUEST:
                    closeDrawer();
                    gotoFriendRequestFragment();
                    break;

                case ModuleUtil.ITEM_SEARCH:
                    closeDrawer();
                    ((MainActivity) activity).dashboardFragment.openCoreSearchFragment();
                    break;

                case ModuleUtil.ITEM_MESSAGES:
                    closeDrawer();
                    goToMesageDashboardFragment();
                    break;


                case ModuleUtil.ITEM_PRIVACY:
                    Intent intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.TnC);
                    intent.putExtra(Constant.KEY_URI, Constant.URL_PRIVACY_2);
                    // intent.putExtra(Constant.KEY_TITLE, Constant.TITLE_PRIVACY);
                    closeDrawer();
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_TERMS:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.TnC);
                    intent.putExtra(Constant.KEY_URI, Constant.URL_TERMS_2);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_COURSE:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.COURSE);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_BOOKING:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.BOOKING);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_CLASSROOM:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CLASSROOM);
                    closeDrawer();
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_CONTACT_US:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_CONTACT_US);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_SETTING:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_SETTINGS);
                    closeDrawer();
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_MUSIC_PLAYLIST:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_MUSIC_PLAYLIST);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_MUSIC:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_MUSIC);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_MUSIC_SONG:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_MUSIC_SONG);
                    closeDrawer();
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_VIDEO:
                  /*  try {
                        JsonObject objectdata= SPref.getInstance().getDefaultInfo(getActivity(),Constant.KEY_APPDEFAULT_DATA).getResult().getCore_modules_enabled();
                        //  String ojj=objectdata.getString("blog");
                        Log.e("OBJECTDATA",""+objectdata);
                        Map<String, String> mapObj = new Gson().fromJson(objectdata, new TypeToken<HashMap<String, String>>() {}.getType());
                    }catch (Exception exception){
                        exception.printStackTrace();
                    }*/
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_VIDEO);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_FUND:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.FUND);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_CREDIT:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CREDIT);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_VIDEO_CHANNEL:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_VIDEO_CHANNEL);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_VIDEO_PLAYLIST:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_VIDEO_PLAYLIST);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_BLOG:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_BLOG);
                   // intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_JOBS);
                    closeDrawer();
                    startActivity(intent);
                    break;

              /*  case ModuleUtil.ITEM_:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_BLOG);
                    closeDrawer();
                    startActivity(intent);
                    break;*/
                case ModuleUtil.ITEM_NEWS:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_NEWS);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_FORUM:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_FORUM);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_CFORUM:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_CFORUM);
                    closeDrawer();
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_CORE_POLL:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_POLL);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_STORE:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.STORE);
                    closeDrawer();
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_QA:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.QA);
                    closeDrawer();
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_CLASSIFIED:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CLASSIFIED);
                    closeDrawer();
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_ARTICLE:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.ARTICLE);
                    closeDrawer();
                    startActivity(intent);
                    break;


                case ModuleUtil.ITEM_CORE_GROUP:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CORE_GROUP);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_GROUP:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.GROUP);
                    closeDrawer();
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_CORE_MEMBER:
                case ModuleUtil.ITEM_MEMBER:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_MEMBER);
                    closeDrawer();
                    startActivity(intent);

                    break;
                case ModuleUtil.ITEM_QUOTE:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.QUOTE);
                    closeDrawer();
                    startActivity(intent);
                    break;


                case ModuleUtil.ITEM_WISH:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.WISH);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_PRAYER:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PRAYER);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_CORE_EVENT:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CORE_EVENT);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_EVENT:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.EVENT);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_PAGE:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PAGE);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_BUSINESS:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.BUSINESS);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case "core_main_sescontest":
                case ModuleUtil.ITEM_CONTEST:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CONTEST);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_RECIPE:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.RECIPE);
                    closeDrawer();
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_THOUGHT:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.THOUGHT);
                    closeDrawer();
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_ALBUM:
                    closeDrawer();
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_ALBUM);
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_RATE_US:
                    closeDrawer();
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_RATE_US);
                    startActivity(intent);
                    break;

                case ModuleUtil.ITEM_EGAMES:
                    closeDrawer();
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.GAME_BUILDER);
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_ERESUME:
                    closeDrawer();
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.RESUME_BUILDER);
                    startActivity(intent);
                    break;
                case ModuleUtil.ITEM_EJOBPLUGIN:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_JOBS);
                    closeDrawer();
                    startActivity(intent);
                    break;
           /*  case ModuleUtil.ITEM_QUOTE:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.POLLSNET);
                    closeDrawer();
                    startActivity(intent);
                    break;*/
                default:
                    closeDrawer();
                   if(menu.getLabel().equalsIgnoreCase("Jobs")){
                       intent = new Intent(activity, CommonActivity.class);
                      // intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_MULTISTORE);
                       intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_JOBS);
                        startActivity(intent);
                    }else {
                                intent = new Intent(activity, CommonActivity.class);
                                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_WEBVIEW);
                                intent.putExtra(Constant.KEY_URI, menu.getUrl());
                                intent.putExtra(Constant.KEY_TITLE, menu.getLabel());
                                startActivity(intent);
                    }


                   /* intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.POLLSNET);
                    startActivity(intent);*/

                    break;
            }

        } catch (Exception e) {
            CustomLog.e(e);
            closeDrawer();
        }
    }

    private void gotoNotificationFragment() {
        ((MainActivity) activity).dashboardFragment.changePagePoistion(3);
       /* fragmentManager.beginTransaction()
                .replace(R.id.container, new NotificationFragment())
                .addToBackStack(null).commit();*/
    }

    private void gotoFriendRequestFragment() {
        ((MainActivity) activity).dashboardFragment.changePagePoistion(1);
    }

    private void goToMesageDashboardFragment() {
        ((MainActivity) activity).dashboardFragment.changePagePoistion(2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_logo_drawer1:
            case R.id.iv_logo_drawer:
            case R.id.tv_name:
                goTo(Constant.GoTo.VIEW_PROFILE, Constant.KEY_ID, SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID));
                break;
            case R.id.tvEditProfile:
                openUserProfileEditForm();
                break;
            case R.id.rlDrawerHeader_2:
            case R.id.tvDrawerHeader_1:
                //do nothing ,this will prevent clicks of backward view
                break;
            case R.id.bSignIn:
                goToWelcome(1);
                break;
            case R.id.bSignUp:
                goToWelcome(2);
                break;
            case R.id.rlFacebook:
                goToWelcome(3);
                break;
        }
    }

    private void callLogoutApi() {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_LOGOUT);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                SPref.getInstance().removeDataOnLogout(context);
                                ((MainApplication) activity.getApplication()).stopMusic();
                                goToWelcome(0);
                                activity.finish();
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception ignored) {
                }
            } else {
                hideBaseLoader();
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void showDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            AppCompatButton bOk = progressDialog.findViewById(R.id.bCamera);
            bOk.setText("Ya");
            AppCompatButton bCancel = progressDialog.findViewById(R.id.bGallary);
            bCancel.setText("Tidak");

            bOk.setOnClickListener(v -> {
                progressDialog.dismiss();
                callLogoutApi();
                // takeImageFromCamera();
            });

            bCancel.setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    class Holder {
        final TextView tvItem;
        final TextView tvHeading;
        final ImageView ivIcon;
        final RelativeLayout rlRow;

        Holder(View view) {
            tvItem = view.findViewById(R.id.tv_drawer_option);
            tvHeading = view.findViewById(R.id.tv_drawer_heading);
            ivIcon = view.findViewById(R.id.iv_icon);
            // ivArrow = view.findViewById(R.id.iv_arrow);
            rlRow = view.findViewById(R.id.rl_row);
        }
    }

}
