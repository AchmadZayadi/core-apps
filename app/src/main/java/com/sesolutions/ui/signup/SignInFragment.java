package com.sesolutions.ui.signup;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.GetGcmId;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.responses.SignInResponse;
import com.sesolutions.responses.ValidateFieldError;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.DefaultDataVo;
import com.sesolutions.ui.welcome.FormError;
import com.sesolutions.ui.welcome.SocialOptionDialogFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;


public class SignInFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {//}, ParserCallbackInterface {

    private static final int CODE_LOGIN = 100;
    private static final int RC_FACEBOOK = 64206;
    private static final int RC_GOOGLE = 64207;
    private static final int RC_TWITTER = 64208;
    private View v;
    AppCompatEditText etEmail;
    AppCompatEditText etPassword;
    private AppCompatButton bSignIn;
    private String password;
    private String email;

    public static final String FB_PROFILE_FIRST = "http://graph.facebook.com/";
    public static final String FB_PROFILE_SECOND = "/picture?type=large";
    public static final String FIELDS = "fields";
    public static final String FB_PROFILE_CONTENT = "id,first_name,last_name,name,email,gender,birthday";
    private Uri uri;
    private String fEmail;
    private String gender;
    private String uuid;
    private LoginButton bFbLogin;
    public CallbackManager callbackManager;
    private String fbToken;
    private String fName;
    private String lName;
    private ImageView ivShow;
    private ImageView ivImage;
    private boolean isPasswordShown = false;
    private Drawable dHide;
    private Drawable dShow;
    private TextView tvSkip, tvkeyhash;
    private boolean isGCMfetchedForFacebook;
    private int type;
    private View cvDemo;
    private int userId;
    private View llDemoMain;
    private boolean isDemoVisible;
    private List<SearchVo> socialList;
    private OnUserClickedListener<Integer, Object> listener;
    private GoogleSignInClient mGoogleSignInClient;
    //  private TwitterLoginButton loginButtonTwitter;
    //private VideoView videoView;

    private String TAG = SignInFragment.class.getSimpleName();

    public static SignInFragment newInstance(int type) {
        SignInFragment frag = new SignInFragment();
        frag.type = type;
        return frag;
    }

    public static SignInFragment newInstance(OnUserClickedListener<Integer, Object> listener, int type) {
        SignInFragment frag = new SignInFragment();
        frag.type = type;
        frag.listener = listener;
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // postponeEnterTransition();
            // setEnterTransition(new AutoTransition());
            //setExitTransition(new AutoTransition());
            setEnterTransition(null);
            setExitTransition(null);
            //  setEnterTransition(new AutoTransition());
            //  setExitTransition(new Explode());
            setSharedElementEnterTransition(null);
            setSharedElementReturnTransition(null);
            //  setAllowEnterTransitionOverlap(false);
            //  setAllowReturnTransitionOverlap(false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        if (v != null) {
            return v;
        }

        v = inflater.inflate(R.layout.fragment_signin_2, container, false);
        try {

            callbackManager = CallbackManager.Factory.create();
            isPasswordShown = false;
            v.findViewById(R.id.llSocial).setVisibility(AppConfiguration.IS_GOOGLE_LOGIN_ENABLED || AppConfiguration.IS_FB_LOGIN_ENABLED || AppConfiguration.IS_TWITTER_LOGIN_ENABLED ? View.VISIBLE : View.GONE);
            init();

            initUserDemo();
            //initSocialLoginLayout();
            registerFacabookCall();
            initTwitter();
            registerGmailCall();
            showHideSkipLogin();
            showHashKey();
            new Handler().postDelayed(this::openScreen, 300);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
//        SPref.getInstance().removeDataOnLogout(context);
        if (AppConfiguration.IS_FB_LOGIN_ENABLED)
            LoginManager.getInstance().logOut();
    }

    /*private void initSocialLoginLayout() {
        socialList = SPref.getInstance().getSocialLogin(context);
        if (socialList != null && socialList.size() > 0) {

            //showMainLayout if list is not empty
            v.findViewById(R.id.llSocial).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvSocial1)).setText(socialList.get(0).getTitle());
            int id = context.getResources().getIdentifier("social_" + socialList.get(0).getName().replace("-", "_"), "drawable", context.getPackageName());
            ((ImageView) v.findViewById(R.id.ivSocial1)).setImageResource(id);
            v.findViewById(R.id.llSocial1).setOnClickListener(this);
            if (socialList.size() > 1) {
                v.findViewById(R.id.llSocial2).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvSocial2)).setText(socialList.get(1).getTitle());
                v.findViewById(R.id.llSocial2).setOnClickListener(this);
                id = context.getResources().getIdentifier("social_" + socialList.get(1).getName().replace("-", "_"), "drawable", context.getPackageName());
                ((ImageView) v.findViewById(R.id.ivSocial2)).setImageResource(id);
            }
            if (socialList.size() > 2) {
                v.findViewById(R.id.llMore).setVisibility(View.VISIBLE);
                v.findViewById(R.id.llMore).setOnClickListener(this);
            }
        } else {
            v.findViewById(R.id.llSocial1).setVisibility(View.INVISIBLE);
        }
    }*/

    /* private void showSocialPopUp(View v) {
         try {
             SocialLoginPopup popup = new SocialLoginPopup(v.getContext(), this, socialList);
             // popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
             //popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
             int vertPos = RelativePopupWindow.VerticalPosition.ABOVE;
             int horizPos = RelativePopupWindow.HorizontalPosition.CENTER;
             popup.showOnAnchor(v, vertPos, horizPos, true);
         } catch (Exception e) {
             CustomLog.e(e);
         }
     }
 */
    private void initUserDemo() {
        llDemoMain = v.findViewById(R.id.llMain);
        if (!AppConfiguration.isDemoUserAvailable) {
            llDemoMain.setVisibility(View.GONE);
            return;
        }

        DefaultDataVo.Result.DemoUser demoUser = SPref.getInstance().getDemoUsers(context);
        final ImageView ivDefault = v.findViewById(R.id.ivDefault);
        cvDemo = v.findViewById(R.id.cvDemo);

        v.findViewById(R.id.cvDefault).setOnClickListener(this);
        RecyclerView recyclerView = v.findViewById(R.id.rvDemo);
        ((TextView) v.findViewById(R.id.tvHeader)).setText(demoUser.getHeadingText());
        ((TextView) v.findViewById(R.id.tvInner)).setText(demoUser.getInnerText());
        Util.showImageWithGlide(ivDefault, demoUser.getDefaultimage(), context);
        // Util.showImageWithGlide(ivDefault, "http://mobileapps.socialenginesolutions.com/application/modules/Sesdemouser/externals/images/nophoto_user_thumb_icon.png", context, 1);
        List<DefaultDataVo.Result.DemoUser.Users> userList = demoUser.getUsers();
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        DemoUserAdapter adapter = new DemoUserAdapter(userList, context, this);
        recyclerView.setAdapter(adapter);
    }

    private void toggleDemoLayout() {
        closeKeyboard();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) cvDemo.getLayoutParams();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                isDemoVisible ? -150 : -1,
                context.getResources().getDisplayMetrics());
        params.setMargins(px, 0, 0, 0);
        cvDemo.setLayoutParams(params);
        // cvDemo.setVisibility(isDemoVisible ? View.INVISIBLE : View.VISIBLE);
        isDemoVisible = !isDemoVisible;
//        if (isDemoVisible) {
//            startAnimation(llDemoMain, Techniques.SLIDE_OUT_LEFT, 1000, new AnimationAdapter() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    cvDemo.setVisibility(View.GONE);
//                    llDemoMain.setVisibility(View.VISIBLE);
//                    llDemoMain.setAlpha(1);
//                }
//            });
//        } else {
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        //updateUI(account);
    }

    public void openScreen() {
        int type = this.type;
        this.type = 0;
        switch (type) {
            case Constant.Events.ACCOUNT_DELETED:
                Util.showSnackbar(v, Constant.MSG_ACCOUNT_DELETED);
                SPref.getInstance().removeDataOnLogout(context);
                break;
            case 2:
                goToSignUpFragment();
                break;
            case 3:
                bFbLogin.performClick();
                break;
            case 4:
                //this means user subscribed successfully after payment
                callCheckLogin();
                break;
        }
    }


    private void showHideSkipLogin() {
        boolean isEnableSkipLogin = SPref.getInstance().getBoolean(context, Constant.KEY_ENABLE_SKIP);
        v.findViewById(R.id.id1).setVisibility(isEnableSkipLogin ? View.VISIBLE : View.GONE);
        tvSkip.setVisibility(isEnableSkipLogin ? View.VISIBLE : View.GONE);

    }

    private void init() {
        etEmail = v.findViewById(R.id.etEmail);

        //show saved email if available
        String email = SPref.getInstance().getString(context, Constant.KEY_EMAIL);
        if (!TextUtils.isEmpty(email)) {
            etEmail.setText(email);
        }
        etPassword = v.findViewById(R.id.etPassword);
        bSignIn = v.findViewById(R.id.bSignIn);
        bFbLogin = v.findViewById(R.id.login_button);
        bSignIn.setOnClickListener(this);
        ivImage = v.findViewById(R.id.ivImage);
        v.findViewById(R.id.tvForgotPassword).setOnClickListener(this);
        // v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.llSocial1).setOnClickListener(this);
        v.findViewById(R.id.tvSignUp).setOnClickListener(this);
        ivShow = v.findViewById(R.id.ivShow);
        ivShow.setOnClickListener(this);
        v.findViewById(R.id.tvTerms).setOnClickListener(this);
        v.findViewById(R.id.tvPrivacy).setOnClickListener(this);
        dShow = ContextCompat.getDrawable(context, R.drawable.password_show);
        dHide = ContextCompat.getDrawable(context, R.drawable.password_hide);
        //  ((TextView) v.findViewById(R.id.tvTitle)).setTextColor(Color.parseColor(Constant.outsideNavigationTitleColor));
        ((TextView) v.findViewById(R.id.tvForgotPassword)).setTextColor(Color.parseColor(Constant.outsideTitleColor));
        ((TextView) v.findViewById(R.id.tvSignUp)).setTextColor(Color.parseColor(Constant.outsideNavigationTitleColor));

        // bSignIn.setTextColor(Color.parseColor(Constant.outsideButtonTitleColor));
        // bSignIn.setBackgroundColor(Color.parseColor(Constant.outsideButtonBackgroundColor));

        etPassword.setHighlightColor(Color.WHITE);
        etEmail.setHighlightColor(Color.BLACK);

        tvSkip = v.findViewById(R.id.tvSkip);
        tvSkip.setOnClickListener(this);
        tvkeyhash = v.findViewById(R.id.tvkeyhash);
        tvkeyhash.setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvPrivacy)).setTextColor(Color.parseColor(Constant.outsideTitleColor));
        ((TextView) v.findViewById(R.id.tvTerms)).setTextColor(Color.parseColor(Constant.outsideTitleColor));
        tvSkip.setTextColor(Color.parseColor(Constant.outsideTitleColor));
        // initTwitter();


        if (AppConfiguration.hasWelcomeVideo) {
            ivImage.setVisibility(View.GONE);
            if (null != listener) {
                listener.onItemClicked(Constant.Events.SET_LOADED, null, 0);
            }
        } else {
            if (null != listener) {
                listener.onItemClicked(Constant.Events.SET_LOADED, null, 1);
            }
            ivImage.setVisibility(View.VISIBLE);
            Util.showImageWithGlide(ivImage, SPref.getInstance().getString(context, SPref.IMAGE_LOGIN_BG), context/*, R.drawable.placeholder_3_2*/);
        }
    }

    private void initTwitter() {
        v.findViewById(R.id.llSocial3).setVisibility(AppConfiguration.IS_TWITTER_LOGIN_ENABLED ? View.VISIBLE : View.GONE);
    }

   /* private void initTwitter() {
        try {
            v.findViewById(R.id.llTwitter).setOnClickListener(this);
            loginButtonTwitter = v.findViewById(R.id.login_button_twitter);
            loginButtonTwitter.setCallback(new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    // Do something with result, which provides a TwitterSession for making API calls
                    CustomLog.e("success", "twitter");
                }

                @Override
                public void failure(TwitterException exception) {
                    // Do something on failure
                    CustomLog.e(exception);
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

    public void showHashKey() {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName()/*"bauwang.network.app"*/,
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                CustomLog.e("KeyHash", sign);
                //  Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
            }
            CustomLog.d("KeyHash:", "****------------***");

            CustomLog.e("fbkey", FacebookSdk.getApplicationSignature(context));


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.bSignIn:
                    closeKeyboard();
                    if (isValid()) {
                        if (TextUtils.isEmpty(Constant.GCM_DEVICE_ID)) {
                            isGCMfetchedForFacebook = false;
                            new GetGcmId(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
                        } else {
                            callLoginApi();
                        }
                    }
                    break;
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.tvkeyhash:
                    showHashKey();
                    break;
                case R.id.ivShow:
                    isPasswordShown = !isPasswordShown;
                    showHidePassword();
                    break;
              /*  case R.id.llTwitter:
                    loginButtonTwitter.performClick();
                    break;*/
                case R.id.llSocial1:
                    bFbLogin.performClick();
                    break;
                case R.id.tvForgotPassword:
                    fragmentManager.beginTransaction().replace(R.id.container, ForgotPasswordFragment.newInstance(listener, email)).addToBackStack(null).commit();
                    break;
                case R.id.tvSignUp:
                    SPref.getInstance().removeDataOnLogout(context);
                    goToSignUpFragment();
                    break;
                case R.id.cvDefault:
                    toggleDemoLayout();
                    break;
                case R.id.tvTerms:
                    openTermsPrivacyFragment(Constant.URL_TERMS_2);
                    break;
                case R.id.llMore:
                    SocialOptionDialogFragment.newInstance(this, socialList).show(fragmentManager, "social");
                    // showSocialPopUp(v);
                    break;
                case R.id.llSocial2:
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_GOOGLE);
                    // onItemClicked(Constant.Events.CLICKED_OPTION, "", 0);
                    break;
                case R.id.llSocial3:
                    // onItemClicked(Constant.Events.CLICKED_OPTION, "", 1);
                    break;
                case R.id.tvPrivacy:
                    openTermsPrivacyFragment(Constant.URL_PRIVACY_2);
                    break;
                case R.id.tvSkip:
                    //TODO REFRESH COOCKIE
                    goToDashboard();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void showHidePassword() {
        etPassword.setTransformationMethod(isPasswordShown ? null : new PasswordTransformationMethod());
        ivShow.setImageDrawable(isPasswordShown ? dShow  : dHide);
        etPassword.setSelection(etPassword.getText().length());
    }

    private void goToSignUpFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, SignUpFragment.newInstance(Constant.VALUE_GET_FORM_1))
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CustomLog.d("onActivityResult", "" + requestCode);
        if (requestCode == RC_FACEBOOK) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == RC_GOOGLE) {
            Task<GoogleSignInAccount> completedTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = completedTask.getResult(ApiException.class);
                if (null != account) {
                    CustomLog.e("GoogleSignInAccount", "" + account.toString());
                } else {
                    CustomLog.e("err", "error in account.toString()");
                }
                // Signed in successfully, show authenticated UI.
                //updateUI(account);
            } catch (ApiException e) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                CustomLog.e("SocialLogin", "signInResult:failed code=" + e.getStatusCode());
                // updateUI(null);
            }
        }



        /*else {
            loginButtonTwitter.onActivityResult(requestCode, resultCode, data);
        }*/
        //Fragment fragment = getFragmentManager().findFragmentById(R.id.your_fragment_id);
        //  if (this != null) {

        //  }
    }

    private void registerGmailCall() {
        v.findViewById(R.id.llSocial2).setVisibility(AppConfiguration.IS_GOOGLE_LOGIN_ENABLED ? View.VISIBLE : View.GONE);
        if (!AppConfiguration.IS_GOOGLE_LOGIN_ENABLED)
            return;
        v.findViewById(R.id.llSocial2).setOnClickListener(this);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestProfile()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    private void registerFacabookCall() {
        v.findViewById(R.id.llSocial1).setVisibility(AppConfiguration.IS_FB_LOGIN_ENABLED ? View.VISIBLE : View.GONE);
        if (!AppConfiguration.IS_FB_LOGIN_ENABLED)
            return;
        //  bFbLogin = (LoginButton) v.findViewById(R.id.login_button);

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    CustomLog.e(TAG, "onFacebookLogout catched");
                    SPref.getInstance().removeDataOnLogoutFB(context);
                }
            }
        };
        bFbLogin.setReadPermissions("email");
        // If using in a fragment
        bFbLogin.setFragment(this);
        bFbLogin.registerCallback(/*((WelcomeActivity) activity).*/callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        final AccessToken accessToken = loginResult.getAccessToken();
                        fbToken = accessToken.getToken();
                        accessTokenTracker.startTracking();
                        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                (object, response) -> {

                                    try {
                                        JSONObject obj = new JSONObject(object.toString());
                                        uuid = obj.getString("id");
                                        fName = obj.getString("first_name");
                                        lName = obj.getString("last_name");
                                        if (object.toString().contains("gender")) {
                                            gender = obj.getString("gender");
                                        }
                                        if (object.toString().contains("email")) {
                                            fEmail = obj.getString("email");
                                        }

                                        uri = Uri.parse(FB_PROFILE_FIRST + uuid + FB_PROFILE_SECOND);
                                        CustomLog.d("token", fbToken);
                                        CustomLog.d("first_name", fName);
                                        CustomLog.d("last_name", lName);
                                        CustomLog.d("uuid", uuid);
                                        CustomLog.d("gender", gender);
                                        CustomLog.d("emailAddress", fEmail);
                                        CustomLog.d("uri", uri.toString());
                                        if (TextUtils.isEmpty(Constant.GCM_DEVICE_ID)) {
                                            isGCMfetchedForFacebook = true;
                                            new GetGcmId(SignInFragment.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
                                        } else {
                                            callFacebookApi();
                                        }
                                    } catch (JSONException ignore) {

                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString(FIELDS, FB_PROFILE_CONTENT);
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        somethingWrongMsg(v);
                    }

                    @Override
                    public void onError(FacebookException e) {
                        somethingWrongMsg(v);
                    }
                });

    }

    private void callFacebookApi() {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                // bSignIn.setText(Constant.TXT_SIGNING_IN);
                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_SIGNUP_FACEBOOK);
                    request.params.put(Constant.KEY_FB_NAME_1, fName);
                    request.params.put(Constant.KEY_FB_NAME_2, lName);
                    request.params.put(Constant.KEY_FB_PIC_URL, uri.toString());
                    request.params.put(Constant.KEY_FB_EMAIL, fEmail);
                    request.params.put(Constant.KEY_FB_GENDER, gender);
                    request.params.put(Constant.KEY_FB_TOKEN, fbToken);
                    request.params.put(Constant.KEY_FB_UID, uuid);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {

                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    if (new JSONObject(response).get("result") instanceof JSONObject) {
                                        try {
                                            SignInResponse res = new Gson().fromJson(response, SignInResponse.class);
                                            if (TextUtils.isEmpty(res.getError())) {
                                                handleLoginResponse(res, context);

                                                   /* UserMaster userVo = res.getResult();
                                                    userVo.setAuthToken(res.getAouthToken());
                                                    SPref.getInstance().saveUserMaster(context, userVo, res.getSessionId());
                                                    userVo.setLoggedinUserId(userVo.getUserId());
                                                    SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN, true);
                                                    SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN_ID, userVo.getUserId());

                                                    goToDashboard();*/

                                            } else {
                                                Util.showSnackbar(v, res.getErrorMessage());

                                            }
                                        } catch (Exception e) {
                                            CustomLog.e(e);
                                            //It means Form response is neither success nor go_forward
                                            //Check for Errors and print
                                            FormError resp = new Gson().fromJson(response, FormError.class);
                                            List<ValidateFieldError> errorList = resp.getResult().getValdatefieldserror();
                                            CustomLog.e("from_vo", "" + new Gson().toJson(errorList));
                                            Util.showSnackbar(v, resp.getResult().fetchFirstNErrors());
                                        }
                                    } else {
                                        goToScreenAsPerResult(new JSONObject(response));
                                    }
                                }

                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    CustomLog.d(Constant.TAG, "Error while login" + e);
                }
                CustomLog.d(Constant.TAG, "login Stop");
            } else {
                Util.showSnackbar(v, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void handleLoginResponse(SignInResponse vo, Context context) {

        SPref.getInstance().saveUserInfo(
                context,
                Constant.KEY_USERINFO_JSON,
                vo
        );
        UserMaster userVo = vo.getResult();
        userVo.setAuthToken(vo.getAouthToken());
        userVo.setLoggedinUserId(userVo.getUserId());
        SPref.getInstance().saveUserMaster(context, userVo, vo.getSessionId());
        SPref.getInstance().updateSharePreferences(context, Constant.KEY_AUTH_TOKEN, vo.getAouthToken());
        SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN, true);
        SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN_ID, userVo.getUserId());

        goToDashboard();
        CustomLog.d("userVo", new Gson().toJson(userVo));
    }


    private void callLoginApi() {
        try {

            if (isNetworkAvailable(context)) {
                bSignIn.setText(R.string.TXT_SIGNING_IN);

                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_LOGIN);

                    if (userId > 0) {
                        request.params.put(Constant.KEY_USER_ID, userId);
                    } else {
                        request.params.put(Constant.KEY_EMAIL, email);
                        request.params.put(Constant.KEY_PASSWORD, password);
                    }

                    request.params.put(Constant.KEY_DEVICE_UID, Constant.GCM_DEVICE_ID);//FirebaseInstanceId.getInstance().getToken());
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {

                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    JSONObject json = new JSONObject(response);
                                    if (json.get(Constant.KEY_RESULT) instanceof String) {
                                        goToScreenAsPerResult(json);
                                    } else {
                                        SignInResponse vo = new Gson().fromJson(response, SignInResponse.class);
                                        if (TextUtils.isEmpty(vo.getError())) {
                                            handleLoginResponse(vo, context);
                                        } else {
                                            bSignIn.setText(R.string.TXT_SIGN_IN);
                                            Util.showSnackbar(v, vo.getErrorMessage());
                                        }
                                    }
                                } else {
                                    bSignIn.setText(R.string.TXT_SIGN_IN);
                                }

                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception ignore) {

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onBackPressed() {
        activity.finish();
    }

    private void goToScreenAsPerResult(JSONObject json) {
        try {
            String result = json.getString("result");
            switch (result) {
                case Constant.RESULT_FORM_0:
                case Constant.RESULT_FORM_1:
                    goToSignUpFragment();
                    break;
                case Constant.RESULT_FORM_2:
                    goToProfileImageFragment();
                    break;
                case Constant.RESULT_FORM_3:
                    int id = json.getInt("user_subscription_id");
                    openWebView(Constant.URL_SUBSCRIPTION + "&user_subscription_id=" + id, getStrings(R.string.TITLE_SUBSCRIPTION));
                    break;
                case Constant.RESULT_FORM_4:
                    fragmentManager.beginTransaction().replace(R.id.container, new JoinFragment()).commit();
                    break;
                case Constant.RESULT_FORM_OTP:
                case Constant.RESULT_FORM_OTP_LOGIN:
                    openOtpFragment(OTPFragment.FROM_SIGNIN, email, password);
                    break;
               /* default:
                    fragmentManager.beginTransaction().replace(R.id.container, new WelcomeFragment()).commit();
                    break;*/
            }
        } catch (JSONException e) {
            CustomLog.e(e);
        }
    }

    private boolean isValid() {
        userId = 0;
        boolean result = false;
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if (!TextUtils.isEmpty(email) /*&& EMAIL_ADDRESS.matcher(email).matches()*/) {

            boolean isChecked = ((AppCompatCheckBox) v.findViewById(R.id.cbRemember)).isChecked();
            SPref.getInstance().updateSharePreferences(context, Constant.KEY_EMAIL, isChecked ? email : "");

            if (!TextUtils.isEmpty(password)) {
                result = true;
            } else {
                Util.showSnackbar(v, getStrings(R.string.MSG_INVALID_PASSWORD));
                //  etPassword.setError(txtInvalidPassword);
            }
        } else {
            Util.showSnackbar(v, getStrings(R.string.MSG_INVALID_EMAIL));
            // etEmail.setError(txtInvalidEmail);
        }
        return result;
    }

    private void callCheckLogin() {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    showSubscribeDialog();
                    final HttpRequestVO request = new HttpRequestVO(Constant.URL_CHECK_LOGIN);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {

                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);

                                if (response != null) {
                                    JSONObject json = new JSONObject(response);
                                    boolean isUserLoggedIn = !(json.get(Constant.KEY_RESULT) instanceof String);
                                    SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN, isUserLoggedIn);
                                    if (isUserLoggedIn) {
                                        SignInResponse resp = new Gson().fromJson(response, SignInResponse.class);
                                        UserMaster vo = resp.getResult();
                                        SPref.getInstance().saveUserInfo(
                                                context,
                                                Constant.KEY_USERINFO_JSON,
                                                resp
                                        );
                                        SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN_ID, vo.getLoggedinUserId());
                                        vo.setAuthToken(vo.getAuthToken());
                                        SPref.getInstance().saveUserMaster(context, vo, resp.getSessionId());
                                        goToDashboard();
                                    }
                                } else {
                                    notInternetMsg(v);
                                }

                            } catch (Exception e) {
                                hideBaseLoader();
                                CustomLog.e(e);
                                notInternetMsg(v);
                            }// dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();

                }

            } else {
                hideBaseLoader();
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showSubscribeDialog() {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_subscription);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.GCM_FETCHED:
                if (isGCMfetchedForFacebook)
                    callFacebookApi();
                else
                    callLoginApi();
                break;
            case Constant.Events.MUSIC_MAIN:
                toggleDemoLayout();
                userId = postion;
                if (TextUtils.isEmpty(Constant.GCM_DEVICE_ID)) {
                    isGCMfetchedForFacebook = false;
                    new GetGcmId(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
                } else {
                    callLoginApi();
                }
                break;
            case Constant.Events.CLICKED_OPTION:
                openWebView(socialList.get(postion).getHref(), socialList.get(postion).getTitle());
                break;
        }
        return false;
    }
}
