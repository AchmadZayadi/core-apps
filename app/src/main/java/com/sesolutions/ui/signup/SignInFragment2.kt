package com.sesolutions.ui.signup

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.facebook.*
import com.facebook.BuildConfig
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.gson.Gson
import com.kusu.linkedinlogin.Linkedin
import com.kusu.linkedinlogin.Linkedin.Companion.initialize
import com.kusu.linkedinlogin.Linkedin.Companion.login
import com.kusu.linkedinlogin.LinkedinLoginListener
import com.kusu.linkedinlogin.model.SocialUser
import com.sesolutions.R
import com.sesolutions.http.GetGcmId
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.SearchVo
import com.sesolutions.responses.SignInResponse
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.WebViewActivity
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.ui.welcome.FormError
import com.sesolutions.ui.welcome.SocialOptionDialogFragment
import com.sesolutions.utils.*
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class SignInFragment2 : BaseFragment(), View.OnClickListener, OnUserClickedListener<Int?, Any?> {
    var v: View? = null
    var etEmail: AppCompatEditText? = null
    var etPassword: AppCompatEditText? = null
    var bSignIn: AppCompatButton? = null
    var ivLinkedin: AppCompatImageView? = null
    var password: String? = null
    var email: String = ""
    var uri: Uri? = null
    var fEmail: String? = null
    var gender: String? = null
    var uuid: String? = null
    var fbToken: String? = null
    var fName: String? = null
    var lName: String? = null

    var bFbLogin: LoginButton? = null
    var callbackManager: CallbackManager? = null
    var card_register: CardView? = null
    var card_login: CardView? = null
    var text_title_register: TextView? = null
    var btn_back_login: AppCompatButton? = null
    var btn_register: AppCompatButton? = null
    var card_succes_register: CardView? = null
    var btn_login_after_register: AppCompatButton? = null
    var btn_back_after_login: AppCompatButton? = null
    var tv_terms: TextView? = null
    var tv_terms_regsiter: TextView? = null

    val ivShow: ImageView? = null
    var isPasswordShown = false
    val dHide: Drawable? = null
    val dShow: Drawable? = null
    var tvSkip: TextView? = null
    var tvkeyhash: TextView? = null
    var isGCMfetchedForFacebook = false
    var type = 0
    var cvDemo: View? = null
    var userId = 0
    var llDemoMain: View? = null
    var isDemoVisible = false
    val socialList: List<SearchVo>? = null
    var listener: OnUserClickedListener<Int, Any?>? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    val LINKEDIN_REQUEST_CODE = 9191

    //  private TwitterLoginButton loginButtonTwitter;
    //private VideoView videoView;
    //GMAIL
    var gmailEmail: String? = null
    var gmailId: String? = null
    var gmailToken: String? = null
    var gmailFname: String? = null
    var gmailSname: String? = null
    var gmailDisplayName: String? = null
    val TAG = SignInFragment2::class.java.simpleName

    //twitter
    var twitterEmail: String? = ""
    var twitterId: String? = null
    var twitterToken: String? = null
    var twitterFname: String? = null
    var twitterSname: String? = null
    var twitterPicurl: String? = null
    var twitterDisplayName: String? = null

    //instagram
    var instagramEmail: String? = null
    var instagramId: String? = null
    var instagramToken: String? = null
    var instagramFname: String? = null
    var instagramPicUrl: String? = null
    var instagramSname: String? = null


    val CONSUMER_KEY = "AlqfZ7Rzmn6eMhC4W0PVJK46h"
    val CONSUMER_SECRET = "Fw64iOOgDBPiWNIouB6R3yEaqlcs4Gi0xUdFSz8gyiIo4Z5Xhz"
    private val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66

    var mPrefs: SharedPreferences? = null
    val welcomeScreenShownPref = "welcomeScreenShown"

    var backgroundImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // postponeEnterTransition();
            // setEnterTransition(new AutoTransition());
            //setExitTransition(new AutoTransition());
            enterTransition = null
            exitTransition = null
            //  setEnterTransition(new AutoTransition());
            //  setExitTransition(new Explode());
            sharedElementEnterTransition = null
            sharedElementReturnTransition = null
            //  setAllowEnterTransitionOverlap(false);
            //  setAllowReturnTransitionOverlap(false);
            checkBackgroundLocation()
        }
    }

    fun printHashKey() {
        try {
            //getting application package name, as defined in manifest
            val packageName = context.applicationContext.packageName
            @SuppressLint("PackageManagerGetSignatures") val info: PackageInfo =
                context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.e("Keys", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("Keys ", "printHashKey()", e)
        } catch (e: java.lang.Exception) {
            Log.e("Keys", "printHashKey()", e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        saveInstanceState: Bundle?
    ): View? {
        if (v != null) {
            return v
        }
        v = inflater.inflate(R.layout.fragment_signin_3, container, false)
        printHashKey();

        try {
            callbackManager = CallbackManager.Factory.create()
            isPasswordShown = false
            //            v.findViewById(R.id.llSocial).setVisibility(AppConfiguration.IS_GOOGLE_LOGIN_ENABLED || AppConfiguration.IS_FB_LOGIN_ENABLED || AppConfiguration.IS_TWITTER_LOGIN_ENABLED ? View.VISIBLE : View.GONE);
            init()
            initLinkedin()
            val config = TwitterConfig.Builder(getActivity())
                .logger(DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
                .twitterAuthConfig(
                    TwitterAuthConfig(
                        CONSUMER_KEY,
                        CONSUMER_SECRET
                    )
                )
                //pass the created app Consumer KEY and Secret also called API Key and Secret
                .debug(true)//enable debug mode
                .build()
            //finally initialize twitter with created configs
            Twitter.initialize(config)
            mTwitterAuthClient = TwitterAuthClient()

            initUserDemo();
            //initSocialLoginLayout();
            registerFacabookCall()
            //            initTwitter();
            registerGmailCall()
            //            showHideSkipLogin();
            Handler().postDelayed({ openScreen() }, 300)

            showHideSkipLogin();
            GetGcmId(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context)

            Log.e("3333", "3333")

//            if (SPref.getInstance().getDefaultInfo(context, Constant.KEY_APPDEFAULT_DATA).result.isIs_core_activity) {
//                v!!.findViewById<View>(R.id.llSocial).visibility = View.GONE
//                v!!.findViewById<View>(R.id.llLogin).visibility = View.GONE
//            } else {
//                v!!.findViewById<View>(R.id.llSocial).visibility = View.VISIBLE
//                v!!.findViewById<View>(R.id.llLogin).visibility = View.VISIBLE
//            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        //        SPref.getInstance().removeDataOnLogout(context);
        if (AppConfiguration.IS_FB_LOGIN_ENABLED) LoginManager.getInstance().logOut()
    }

    private fun checkBackgroundLocation() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                }
                else -> {
                    // No location access granted.
                }
            }
        }
//…
// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }


    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
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


    private fun initUserDemo() {
        llDemoMain = v!!.findViewById(R.id.llMain)
        if (!AppConfiguration.isDemoUserAvailable) {
            llDemoMain!!.setVisibility(View.GONE)
            return
        }
        val demoUser = SPref.getInstance().getDemoUsers(context)
        val ivDefault = v!!.findViewById<ImageView>(R.id.ivDefault)
        cvDemo = v!!.findViewById(R.id.cvDemo)
        v!!.findViewById<View>(R.id.cvDefault).setOnClickListener(this)
        val recyclerView: RecyclerView = v!!.findViewById(R.id.rvDemo)
        (v!!.findViewById<View>(R.id.tvHeader) as TextView).text = demoUser.headingText
        (v!!.findViewById<View>(R.id.tvInner) as TextView).text = demoUser.innerText
        Util.showImageWithGlide(ivDefault, demoUser.defaultimage, context)
        // Util.showImageWithGlide(ivDefault, "http://mobileapps.socialenginesolutions.com/application/modules/Sesdemouser/externals/images/nophoto_user_thumb_icon.png", context, 1);
        val userList = demoUser.users
        recyclerView.setHasFixedSize(true)
        val layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        val adapter = DemoUserAdapter(userList, context, this)
        recyclerView.adapter = adapter
    }

    private fun toggleDemoLayout() {
        closeKeyboard()
        val params = cvDemo!!.layoutParams as ViewGroup.MarginLayoutParams
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            if (isDemoVisible) (-150).toFloat() else -1.toFloat(),
            context.resources.displayMetrics
        ).toInt()
        params.setMargins(px, 0, 0, 0)
        cvDemo!!.layoutParams = params
        // cvDemo.setVisibility(isDemoVisible ? View.INVISIBLE : View.VISIBLE);
        isDemoVisible = !isDemoVisible
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

    override fun onStart() {
        super.onStart()
        // GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        //updateUI(account);
    }

    fun openScreen() {
        val type = type
        this.type = 0
        when (type) {
            Constant.Events.ACCOUNT_DELETED -> {
                Util.showSnackbar(v, Constant.MSG_ACCOUNT_DELETED)
                SPref.getInstance().removeDataOnLogout(context)
            }
            2 -> goToSignUpFragment()
            3 -> bFbLogin!!.performClick()
            4 ->                 //this means user subscribed successfully after payment
                callCheckLogin()
        }
    }

    private fun showHideSkipLogin() {
        val isEnableSkipLogin = SPref.getInstance().getBoolean(context, Constant.KEY_ENABLE_SKIP)
        tvSkip!!.visibility = if (isEnableSkipLogin) View.VISIBLE else View.GONE
    }

    private fun init() {
        etEmail = v!!.findViewById(R.id.etEmail)
        ivLinkedin = v!!.findViewById(R.id.ivLinkedin)
        ivLinkedin!!.setOnClickListener(View.OnClickListener {
            login(activity, object : LinkedinLoginListener {
                override fun successLinkedInLogin(socialUser: SocialUser) {
                    Log.e("email", "" + socialUser.email);
                    Log.e("email2", "" + socialUser.firstName);
                    Log.e("email22", "" + socialUser.linkedinToken);
                    Log.e("email222", "" + socialUser.socialId);

                    instagramEmail = "" + socialUser.email
                    instagramFname = "" + socialUser.firstName
                    instagramId = "" + socialUser.socialId
                    instagramToken = "" + socialUser.linkedinToken
                    instagramSname = "" + socialUser.lastName
                    instagramPicUrl = "" + socialUser.profilePicture


                    callInstagramApi();
                }

                override fun failedLinkedinLogin(s: String) {
                    //todo failed functionality
                }
            })
        })
        //show saved email if available
//        val email = SPref.getInstance().getString(context, Constant.KEY_EMAIL)
//        etEmail.text = ""
//
//        if (!TextUtils.isEmpty(email)) {
//            etEmail!!.setText(email)
//        }
        etPassword = v!!.findViewById(R.id.etPassword)
        bSignIn = v!!.findViewById(R.id.bSignIn)
        bFbLogin = v!!.findViewById(R.id.login_button)
        bSignIn!!.setOnClickListener(this)
        v!!.findViewById<View>(R.id.tvForgotPassword).setOnClickListener(this)
        v!!.findViewById<View>(R.id.tvSignUp).setOnClickListener(this)
        v!!.findViewById<View>(R.id.llBack).setOnClickListener(this)
        v!!.findViewById<TextView>(R.id.tvTerms).setOnClickListener(this);
        v!!.findViewById<TextView>(R.id.tvPrivacy).setOnClickListener(this);
        v!!.findViewById<View>(R.id.ivEmail).visibility = View.VISIBLE
        v!!.findViewById<View>(R.id.ivEmail).setOnClickListener(this)
        v!!.findViewById<View>(R.id.ivTwitter).setOnClickListener(this)
        card_register = v!!.findViewById(R.id.card_register)
        card_login = v!!.findViewById(R.id.resultCard)
        text_title_register = v!!.findViewById(R.id.tv_title_register)
        btn_back_login = v!!.findViewById(R.id.btn_back_login)
        btn_back_login!!.setOnClickListener(this)
        btn_register = v!!.findViewById(R.id.btn_register)
        btn_login_after_register = v!!.findViewById(R.id.btn_login_register)
        btn_login_after_register!!.setOnClickListener(this)
        btn_register!!.setOnClickListener(this)
        card_succes_register = v!!.findViewById(R.id.card_register_succes)
        btn_back_after_login = v!!.findViewById(R.id.btn_back_login_succes_register)
        btn_back_after_login!!.setOnClickListener(this)



        tv_terms = v!!.findViewById(R.id.tv_tos)
        tv_terms_regsiter = v!!.findViewById(R.id.tv_tos_register)

        val ss =
            SpannableString("Dengan mendaftar, saya menyetujui \nSyarat Penggunaan dan Kebijakan Privasi Matani.ID")
        val span1: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.setColor(ds.linkColor) // you can use custom color
                ds.setUnderlineText(false) // this remove the underline
            }

            override fun onClick(widget: View) {
                val intent = Intent(
                    getActivity(),
                    WebViewActivity::class.java
                )
                intent.putExtra("web", "https://matani.id/pages/policy")
                intent.putExtra("title", "Syarat dan Ketentuan")
                startActivity(intent)

            }
//            fun onClick(textView: View?) {
//
//            }
        }
        val span2: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.setColor(ds.linkColor) // you can use custom color
                ds.setUnderlineText(false) // this remove the underline
            }

            override fun onClick(widget: View) {
                val intent = Intent(
                    getActivity(),
                    WebViewActivity::class.java
                )
                intent.putExtra("web", "https://matani.id/pages/tos")
                intent.putExtra("title", "Kebijakan Privasi")
                startActivity(intent)

            }
//            fun onClick(textView: View?) {
//
//            }
        }


        ss.setSpan(span1, 34, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(span2, 56, 74, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv_terms!!.text = ss
        tv_terms_regsiter!!.text = ss
        //  tv_terms!!.movementMethod(LinkMovementMethod.getInstance())
        tv_terms_regsiter!!.setMovementMethod(LinkMovementMethod.getInstance());
        tv_terms!!.setMovementMethod(LinkMovementMethod.getInstance());


        // bSignIn.setTextColor(Color.parseColor(Constant.outsideButtonTitleColor));


        // bSignIn!!.setBackgroundColor(Color.parseColor(Constant.outsideButtonBackgroundColor));


        etPassword!!.setHighlightColor(Color.WHITE)
        etEmail!!.setHighlightColor(Color.BLACK)
        tvSkip = v!!.findViewById(R.id.tvSkip)
        tvSkip!!.setOnClickListener(this)
        tvkeyhash = v!!.findViewById(R.id.tvkeyhash)
        tvkeyhash!!.setOnClickListener(this)
        //        ((TextView) v.findViewById(R.id.tvPrivacy)).setTextColor(Color.parseColor(Constant.outsideTitleColor));
//        ((TextView) v.findViewById(R.id.tvTerms)).setTextColor(Color.parseColor(Constant.outsideTitleColor));
        tvSkip!!.setTextColor(Color.parseColor(Constant.outsideTitleColor))
        // initTwitter();

        //   initialize(FacebookSdk.getApplicationContext());

        initialize(
            FacebookSdk.getApplicationContext(),
            "781s5y4us1visz",  //Client Id of your linkedin app like-> "47sf33fjflf"
            "oMIKD3SqrvFjKHOK",  //Client Secret of your linkedin app like-> "Udhfksfeu324uh4"
            "https://sandbox.socialnetworking.solutions/advancedapp/",  //Redirect url which has to be add in your linkedin app like-> "https://example.com/auth/linkedin/callback"
            "wfqafawqf",  //For security purpose used to prevent CSRF like-> "nfw4wfhw44r34fkwh"
            Arrays.asList("r_liteprofile, r_emailaddress") // app permission options like-> "r_liteprofile", "r_emailaddress", "w_member_social"
        )

        mPrefs = PreferenceManager.getDefaultSharedPreferences(context)

        // second argument is the default to use if the preference can't be found

        // second argument is the default to use if the preference can't be found
        val welcomeScreenShown = mPrefs!!.getBoolean(welcomeScreenShownPref, false)

        if (!welcomeScreenShown) {


            val editor = mPrefs!!.edit()
            editor.putBoolean(welcomeScreenShownPref, true)
            editor.commit() // Very important to save the preference
            card_login?.visibility = View.GONE


        } else {
            card_login?.visibility = View.VISIBLE


        }


        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

        if (AppConfiguration.hasWelcomeVideo) {
            if (null != listener) {
                listener!!.onItemClicked(Constant.Events.SET_LOADED, null, 0)
            }
        } else {
            if (null != listener) {
                listener!!.onItemClicked(Constant.Events.SET_LOADED, null, 1)
            }

        }
    }

    //    public boolean checkLocationPermission() {
    //        if (ContextCompat.checkSelfPermission(context,
    //                Manifest.permission.ACCESS_FINE_LOCATION)
    //                != PackageManager.PERMISSION_GRANTED) {
    //
    //            // Should we show an explanation?
    //            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
    //                    Manifest.permission.ACCESS_FINE_LOCATION)) {
    //
    //                // Show an explanation to the user *asynchronously* -- don't block
    //                // this thread waiting for the user's response! After the user
    //                // sees the explanation, try again to request the permission.
    ////                new AlertDialog.Builder(context)
    ////                        .setTitle("tittle")
    ////                        .setMessage("iyaa bener")
    ////                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
    ////                            @Override
    ////                            public void onClick(DialogInterface dialogInterface, int i) {
    ////                                //Prompt the user once explanation has been shown
    ////                                ActivityCompat.requestPermissions(activity,
    ////                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
    ////                                        MY_PERMISSIONS_REQUEST_LOCATION);
    ////                            }
    ////                        })
    ////                        .create()
    ////                        .show();
    //
    //
    //            } else {
    //                // No explanation needed, we can request the permission.
    //                ActivityCompat.requestPermissions(activity,
    //                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
    //                        MY_PERMISSIONS_REQUEST_LOCATION);
    //            }
    //            return false;
    //        } else {
    //            return true;
    //        }
    //    }


    private fun initTwitter() {
        v!!.findViewById<View>(R.id.llSocial3).visibility =
            if (AppConfiguration.IS_TWITTER_LOGIN_ENABLED) View.VISIBLE else View.GONE
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
    fun showHashKey() {
        try {
            @SuppressLint("PackageManagerGetSignatures") val info =
                context.packageManager.getPackageInfo(
                    context.packageName /*"bauwang.network.app"*/,
                    PackageManager.GET_SIGNATURES
                )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val sign = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                CustomLog.e("KeyHash", sign)
                //  Toast.makeText(getApplicationContext(),sign,     Toast.LENGTH_LONG).show();
            }
            CustomLog.d("KeyHash:", "****------------***")
            CustomLog.e("fbkey", FacebookSdk.getApplicationSignature(context))
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    private fun initLinkedin() {

        Linkedin.initialize(
            context = getActivity(),
            clientId = "78g1n6u1ookvg2", //Client Id of your linkedin app like-> "47sf33fjflf"
            clientSecret = "fH5dG5NyXq9bTwdd", //Client Secret of your linkedin app like-> "Udhfksfeu324uh4"
            redirectUri = "https://sandbox.socialnetworking.solutions", //Redirect url which has to be add in your linkedin app like-> "https://example.com/auth/linkedin/callback"
            state = "nfw4wfhw44r34fkwhqhssh2", //For security purpose used to prevent CSRF like-> "nfw4wfhw44r34fkwh"
            scopes = listOf(
                "r_liteprofile",
                "r_emailaddress",
                "w_member_social"
            ) // app permission options like-> "r_liteprofile", "r_emailaddress", "w_member_social"
        )
    }

    override fun onClick(v: View) {
        try {
            when (v.id) {
                R.id.bSignIn -> {
                    closeKeyboard()
                    val phone = etEmail?.text?.substring(0, 4)
                    if (phone.equals("0881") || phone.equals("0882") || phone.equals("0883") || phone.equals(
                            "0884"
                        ) || phone.equals("0885") || phone.equals("0886") || phone.equals("0887") || phone.equals(
                            "0888"
                        ) || phone.equals("0889")

                    ) {
                        showDialogNotice()
                        //Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show()
                    } else if (phone.equals("0896") || phone.equals("0895") || phone.equals("0897") || phone.equals(
                            "0898"
                        )
                        || phone.equals("0899")

                    ) {
                        showDialogNotice()
                        // Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show()
                    } else if (isValid) {
                        //  openOtpFragment(OTPFragment.FROM_SIGNIN, "08569004790", "08569004790")
                        callLoginApi()
                    }
                }
                R.id.btn_back_login_succes_register -> {
                    card_register?.visibility = View.GONE
                    card_succes_register?.visibility = View.GONE
                    card_login?.visibility = View.VISIBLE

                }
                R.id.btn_back_login -> {
                    card_register?.visibility = View.GONE
                    card_login?.visibility = View.VISIBLE
                }
                R.id.btn_register -> {
                    callApiRegister()
                    //fungsi register
                }
                R.id.btn_login_register -> {
                    callLoginApi()
                }
                R.id.llBack -> super.onBackPressed()
                R.id.tvkeyhash -> showHashKey()
                R.id.ivShow -> {
                    isPasswordShown = !isPasswordShown
                    showHidePassword()
                }
                R.id.ivFacebook, R.id.llSocial1 -> bFbLogin!!.performClick()
                R.id.tvForgotPassword -> fragmentManager.beginTransaction()
                    .replace(R.id.container, ForgotPasswordFragment.newInstance(listener, email))
                    .addToBackStack(null).commit()
                R.id.tvSignUp -> goToSignUpFragment()
                R.id.cvDefault -> toggleDemoLayout()
                R.id.tvTerms -> openTermsPrivacyFragment(Constant.URL_TERMS_2)
                R.id.llMore -> SocialOptionDialogFragment.newInstance(this, socialList)
                    .show(fragmentManager, "social")
                R.id.ivEmail -> {
                    val signInIntent = mGoogleSignInClient!!.signInIntent
                    startActivityForResult(signInIntent, RC_GOOGLE)
                }
                R.id.ivTwitter -> {
                    twitterLogin()
                }
                R.id.llSocial3 -> {
                }
                R.id.tvPrivacy -> openTermsPrivacyFragment(Constant.URL_PRIVACY_2)
                R.id.tvSkip -> goToDashboard()

            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun showHidePassword() {
        etPassword!!.transformationMethod =
            if (isPasswordShown) null else PasswordTransformationMethod()
        ivShow!!.setImageDrawable(if (isPasswordShown) dShow else dHide)
        etPassword!!.setSelection(etPassword!!.text!!.length)
    }

    private fun goToSignUpFragment() {
        fragmentManager.beginTransaction()
            .replace(R.id.container, SignUpFragment.newInstance(Constant.VALUE_GET_FORM_1))
            .addToBackStack(null)
            .commit()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        CustomLog.d("onActivityResult333", "" + requestCode)
        if (requestCode == RC_FACEBOOK) {
            Log.e("44444", "55555555");
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        } else if (requestCode == RC_GOOGLE) {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = completedTask.getResult(ApiException::class.java)
                if (null != account) {
                    //Fetch data
                    CustomLog.e("email", "" + account.email)
                    gmailEmail = account.email
                    CustomLog.e("id", "" + account.id)
                    gmailId = account.id
                    CustomLog.e("token", "" + account.idToken)
                    gmailToken = account.idToken
                    CustomLog.e("fname", "" + account.givenName)
                    gmailFname = account.displayName
                    CustomLog.e("Sname", "" + account.familyName)
                    gmailSname = account.familyName
                    CustomLog.e("GoogleSignInAccount", "" + account.account)
                    CustomLog.e("name", "" + account.displayName)
                    gmailDisplayName = account.displayName
                    showBaseLoader(false)
                    callGmailApi()
                } else {
                    CustomLog.e("err", "error in account.toString()")
                }
                // Signed in successfully, show authenticated UI.
                //updateUI(account);
            } catch (e: ApiException) {
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                CustomLog.e("SocialLogin", "signInResult:failed code=" + e.statusCode)
                Util.showSnackbar(v, "Sign in with google failed, please try again")
                // updateUI(null);
            }
        } else {
            if (mTwitterAuthClient != null)
                mTwitterAuthClient!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun registerGmailCall() {

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail().requestProfile()
            .requestIdToken(getString(R.string.gmail))
            .build()
        CustomLog.e("Google: ", gso.toString())
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    private fun registerFacabookCall() {
        v!!.findViewById<View>(R.id.ivFacebook).visibility =
            if (AppConfiguration.IS_FB_LOGIN_ENABLED) View.VISIBLE else View.GONE
        // v!!.findViewById<View>(R.id.ivFacebook).visibility=View.GONE
        v!!.findViewById<View>(R.id.ivFacebook).setOnClickListener(this)
        if (!AppConfiguration.IS_FB_LOGIN_ENABLED) return
        //  bFbLogin = (LoginButton) v.findViewById(R.id.login_button);
        bFbLogin!!.setReadPermissions(listOf("public_profile"))
        // If using in a fragment
        bFbLogin!!.fragment = this
        bFbLogin!!.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.e("222", "33333333333");
                    val accessToken = loginResult.accessToken
                    fbToken = accessToken.token
                    Log.e("accessToken", "" + accessToken);
                    //  accessTokenTracker.startTracking()
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS)
                    val request = GraphRequest.newMeRequest(
                        accessToken
                    ) { `object`: JSONObject, response: GraphResponse? ->
                        Log.e("7777777", "66666666");
                        try {
                            val obj = JSONObject(`object`.toString())
                            uuid = obj.getString("id")
                            Log.e("uuid", uuid!!);
                            fName = obj.getString("first_name")
                            Log.e("fName", fName!!);
                            lName = obj.getString("last_name")
                            if (`object`.toString().contains("gender")) {
                                gender = obj.getString("gender")
                            }
                            if (`object`.toString().contains("email")) {
                                fEmail = obj.getString("email")
                            }
                            uri = Uri.parse(FB_PROFILE_FIRST + uuid + FB_PROFILE_SECOND)
                            CustomLog.d("token", fbToken)
                            CustomLog.d("first_name", fName)
                            CustomLog.d("last_name", lName)
                            CustomLog.d("uuid", uuid)
                            CustomLog.d("gender", gender)
                            CustomLog.d("emailAddress", fEmail)
                            CustomLog.d("uri", uri.toString())
                            if (TextUtils.isEmpty(Constant.GCM_DEVICE_ID)) {
                                isGCMfetchedForFacebook = true
                                GetGcmId(this@SignInFragment2).executeOnExecutor(
                                    AsyncTask.THREAD_POOL_EXECUTOR,
                                    context
                                )
                            } else {
                                callFacebookApi()
                            }
                        } catch (ignore: Exception) {
                            Log.e("data", "" + ignore.printStackTrace())
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString(
                        "fields",
                        "id,first_name,last_name,name,email,picture.type(large)"
                    )
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    somethingWrongMsg(v)
                }

                override fun onError(e: FacebookException) {
                    somethingWrongMsg(v)
                }
            })
    }


    private fun callFacebookApi() {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                // bSignIn.setText(Constant.TXT_SIGNING_IN);
                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    val request = HttpRequestVO(Constant.URL_SIGNUP_FACEBOOK)
                    request.params[Constant.KEY_FB_NAME_1] = fName
                    request.params[Constant.KEY_FB_NAME_2] = lName
                    request.params[Constant.KEY_FB_PIC_URL] = uri.toString()
                    request.params[Constant.KEY_FB_EMAIL] = fEmail
                    request.params[Constant.KEY_FB_GENDER] = gender
                    request.params[Constant.KEY_FB_TOKEN] = fbToken
                    request.params[Constant.KEY_FB_UID] = uuid
                    request.headres[Constant.KEY_COOKIE] = cookie
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.requestMethod = HttpPost.METHOD_NAME
                    val callback = Handler.Callback { msg ->
                        try {
                            val response = msg.obj as String
                            CustomLog.e("repsonse", "" + response)
                            if (response != null) {
                                if (JSONObject(response)["result"] is JSONObject) {
                                    try {
                                        val res = Gson().fromJson(
                                            response,
                                            SignInResponse::class.java
                                        )
                                        if (TextUtils.isEmpty(res.error)) {
                                            handleLoginResponse(res, context)

                                            /* UserMaster userVo = res.getResult();
                                                                userVo.setAuthToken(res.getAouthToken());
                                                                SPref.getInstance().saveUserMaster(context, userVo, res.getSessionId());
                                                                userVo.setLoggedinUserId(userVo.getUserId());
                                                                SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN, true);
                                                                SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN_ID, userVo.getUserId());

                                                                goToDashboard();*/
                                        } else {
                                            Util.showSnackbar(v, res.errorMessage)
                                        }
                                    } catch (e: Exception) {
                                        CustomLog.e(e)
                                        //It means Form response is neither success nor go_forward
                                        //Check for Errors and print
                                        val resp = Gson().fromJson(response, FormError::class.java)
                                        val errorList = resp.result.valdatefieldserror
                                        CustomLog.e("from_vo", "" + Gson().toJson(errorList))
                                        Util.showSnackbar(v, resp.result.fetchFirstNErrors())
                                    }
                                } else {

                                    try {
                                        val res = Gson().fromJson(
                                            response,
                                            SignInModel::class.java
                                        )
                                        Constant.SESSION_ID =
                                            "PHPSESSID=" + res.session_id + ";"
                                        SPref.getInstance()
                                            .updateSharePreferences(
                                                context,
                                                Constant.KEY_COOKIE,
                                                "PHPSESSID=" + res.session_id + ";"
                                            )
                                    } catch (ex: java.lang.Exception) {
                                        ex.printStackTrace()
                                    }
                                    goToScreenAsPerResult(JSONObject(response))
                                }
                            }
                        } catch (e: Exception) {
                            CustomLog.e(e)
                        }

                        // dialog.dismiss();
                        true
                    }
                    HttpRequestHandler(activity, Handler(callback)).run(request)
                } catch (e: Exception) {
                    CustomLog.d(Constant.TAG, "Error while login$e")
                }
                CustomLog.d(Constant.TAG, "login Stop")
            } else {
                Util.showSnackbar(v, Constant.MSG_NO_INTERNET)
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun callGmailApi() {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                // bSignIn.setText(Constant.TXT_SIGNING_IN);
                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    val request = HttpRequestVO(Constant.URL_SIGNUP_GMAIL)
                    request.params[Constant.KEY_GMAIL_NAME_1] = gmailFname
                    request.params[Constant.KEY_GMAIL_NAME_2] = gmailSname
                    request.params[Constant.KEY_GMAIL_DISPLAY_NAME] = gmailDisplayName
                    request.params[Constant.KEY_GMAIL_EMAIL] = gmailEmail
                    request.params[Constant.KEY_GMAIL_ID] = gmailId
                    request.params[Constant.KEY_GMAIL_TOKEN2] = gmailToken
                    request.headres[Constant.KEY_COOKIE] = cookie
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.requestMethod = HttpPost.METHOD_NAME
                    val callback = Handler.Callback { msg ->
                        hideBaseLoader()
                        try {
                            val response = msg.obj as String
                            CustomLog.e("repsonse", "" + response)
                            if (response != null) {
                                if (JSONObject(response)["result"] is JSONObject) {
                                    try {
                                        val res = Gson().fromJson(
                                            response,
                                            SignInResponse::class.java
                                        )
                                        try {
                                            Constant.SESSION_ID =
                                                "PHPSESSID=" + res.getSessionId() + ";"
                                        } catch (ex: java.lang.Exception) {
                                            ex.printStackTrace()
                                        }
                                        if (TextUtils.isEmpty(res.error)) {
                                            handleLoginResponse(res, context)
                                        } else {
                                            Util.showSnackbar(v, res.errorMessage)
                                        }
                                    } catch (e: Exception) {
                                        CustomLog.e(e)
                                        //It means Form response is neither success nor go_forward
                                        //Check for Errors and print
                                        val resp = Gson().fromJson(response, FormError::class.java)
                                        val errorList = resp.result.valdatefieldserror
                                        CustomLog.e("from_vo", "" + Gson().toJson(errorList))
                                        Util.showSnackbar(v, resp.result.fetchFirstNErrors())
                                    }
                                } else {

                                    try {
                                        val res = Gson().fromJson(
                                            response,
                                            SignInModel::class.java
                                        )
                                        Constant.SESSION_ID =
                                            "PHPSESSID=" + res.session_id + ";"
                                        SPref.getInstance()
                                            .updateSharePreferences(
                                                context,
                                                Constant.KEY_COOKIE,
                                                "PHPSESSID=" + res.session_id + ";"
                                            )
                                    } catch (ex: java.lang.Exception) {
                                        ex.printStackTrace()
                                    }
                                    goToScreenAsPerResult(JSONObject(response))
                                }
                            }
                        } catch (e: Exception) {
                            CustomLog.e(e)
                        }

                        // dialog.dismiss();
                        true
                    }
                    HttpRequestHandler(activity, Handler(callback)).run(request)
                } catch (e: Exception) {
                    CustomLog.d(Constant.TAG, "Error while login$e")
                }
                CustomLog.d(Constant.TAG, "login Stop")
            } else {
                Util.showSnackbar(v, Constant.MSG_NO_INTERNET)
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }


    private fun callTwitterApi() {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                // bSignIn.setText(Constant.TXT_SIGNING_IN);
                try {
                    val request = HttpRequestVO(Constant.URL_SIGNUP_TWITTER)

                    request.params["Email"] = twitterEmail
                    request.params["username"] = twitterDisplayName

                    if (twitterDisplayName != null && twitterDisplayName!!.length > 0) {
                        var sp = twitterDisplayName!!.split(" ")
                        if (sp.size > 1) {
                            twitterFname = sp[0];
                            fName = twitterFname
                            twitterSname = sp[1];
                            lName = twitterSname
                        } else {
                            twitterFname = sp[0];
                            twitterSname = "";
                        }
                    }
                    request.params["FirstName"] = twitterFname
                    request.params["LastName"] = twitterSname
                    request.params["twitter_uid"] = twitterId
                    request.params["twitter_token"] = twitterToken
                    request.headres[Constant.KEY_COOKIE] = cookie
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.requestMethod = HttpPost.METHOD_NAME
                    val callback = Handler.Callback { msg ->
                        hideBaseLoader()
                        try {
                            val response = msg.obj as String
                            CustomLog.e("repsonse", "" + response)
                            if (response != null) {
                                if (JSONObject(response)["result"] is JSONObject) {
                                    try {
                                        val res =
                                            Gson().fromJson(response, SignInResponse::class.java)
                                        if (TextUtils.isEmpty(res.error)) {
                                            handleLoginResponse(res, context)

                                            /* UserMaster userVo = res.getResult();
                                                                userVo.setAuthToken(res.getAouthToken());
                                                                SPref.getInstance().saveUserMaster(context, userVo, res.getSessionId());
                                                                userVo.setLoggedinUserId(userVo.getUserId());
                                                                SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN, true);
                                                                SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN_ID, userVo.getUserId());

                                                                goToDashboard();*/
                                        } else {
                                            Util.showSnackbar(v, res.errorMessage)
                                        }
                                    } catch (e: Exception) {
                                        CustomLog.e(e)
                                        //It means Form response is neither success nor go_forward
                                        //Check for Errors and print
                                        val resp = Gson().fromJson(response, FormError::class.java)
                                        val errorList = resp.result.valdatefieldserror
                                        CustomLog.e("from_vo", "" + Gson().toJson(errorList))
                                        Util.showSnackbar(v, resp.result.fetchFirstNErrors())
                                    }
                                } else {
                                    try {
                                        val res = Gson().fromJson(
                                            response,
                                            SignInModel::class.java
                                        )
                                        Constant.SESSION_ID =
                                            "PHPSESSID=" + res.session_id + ";"
                                        SPref.getInstance()
                                            .updateSharePreferences(
                                                context,
                                                Constant.KEY_COOKIE,
                                                "PHPSESSID=" + res.session_id + ";"
                                            )
                                    } catch (ex: java.lang.Exception) {
                                        ex.printStackTrace()
                                    }
                                    goToScreenAsPerResult(JSONObject(response))
                                }
                            }
                        } catch (e: Exception) {
                            CustomLog.e(e)
                        }

                        // dialog.dismiss();
                        true
                    }
                    HttpRequestHandler(activity, Handler(callback)).run(request)
                } catch (e: Exception) {
                    CustomLog.d(Constant.TAG, "Error while login$e")
                }
                CustomLog.d(Constant.TAG, "login Stop")
            } else {
                Util.showSnackbar(v, Constant.MSG_NO_INTERNET)
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun callInstagramApi() {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                // bSignIn.setText(Constant.TXT_SIGNING_IN);
                try {
                    val request = HttpRequestVO(Constant.URL_SIGNUP_LINKDIN)

                    request.params["Email"] = instagramEmail
                    request.params["username"] = instagramFname + " " + instagramSname;
                    request.params["FirstName"] = instagramFname
                    request.params["PictureURL"] = instagramPicUrl
                    request.params["LastName"] = instagramSname
                    request.params["linkedin_uid"] = instagramId
                    request.params["access_token"] = instagramToken
                    request.headres[Constant.KEY_COOKIE] = cookie
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.requestMethod = HttpPost.METHOD_NAME
                    val callback = Handler.Callback { msg ->
                        hideBaseLoader()
                        try {
                            val response = msg.obj as String
                            CustomLog.e("repsonse", "" + response)
                            if (response != null) {
                                if (JSONObject(response)["result"] is JSONObject) {
                                    try {
                                        val res =
                                            Gson().fromJson(response, SignInResponse::class.java)
                                        if (TextUtils.isEmpty(res.error)) {
                                            handleLoginResponse(res, context)

                                            /* UserMaster userVo = res.getResult();
                                                                userVo.setAuthToken(res.getAouthToken());
                                                                SPref.getInstance().saveUserMaster(context, userVo, res.getSessionId());
                                                                userVo.setLoggedinUserId(userVo.getUserId());
                                                                SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN, true);
                                                                SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN_ID, userVo.getUserId());

                                                                goToDashboard();*/
                                        } else {
                                            Util.showSnackbar(v, res.errorMessage)
                                        }
                                    } catch (e: Exception) {
                                        CustomLog.e(e)
                                        //It means Form response is neither success nor go_forward
                                        //Check for Errors and print
                                        val resp = Gson().fromJson(response, FormError::class.java)
                                        val errorList = resp.result.valdatefieldserror
                                        CustomLog.e("from_vo", "" + Gson().toJson(errorList))
                                        Util.showSnackbar(v, resp.result.fetchFirstNErrors())
                                    }
                                } else {
                                    goToScreenAsPerResult(JSONObject(response))
                                }
                            }
                        } catch (e: Exception) {
                            CustomLog.e(e)
                        }

                        // dialog.dismiss();
                        true
                    }
                    HttpRequestHandler(activity, Handler(callback)).run(request)
                } catch (e: Exception) {
                    CustomLog.d(Constant.TAG, "Error while login$e")
                }
                CustomLog.d(Constant.TAG, "login Stop")
            } else {
                Util.showSnackbar(v, Constant.MSG_NO_INTERNET)
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }


    private fun handleLoginResponse(vo: SignInResponse, context: Context) {

        SPref.getInstance().saveUserInfo(
            context,
            Constant.KEY_USERINFO_JSON,
            vo
        )
        val userVo: UserMaster = vo.result
        userVo.authToken = vo.aouthToken
        userVo.loggedinUserId = userVo.userId

        try {
            AppConfiguration.DEFAULT_CURRENCY = vo.result.defaultcurrency
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        SPref.getInstance().saveUserMaster(context, userVo, vo.sessionId)
        SPref.getInstance().updateSharePreferences(context, Constant.KEY_AUTH_TOKEN, vo.aouthToken)
        SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN, true)
        SPref.getInstance()
            .updateSharePreferences(context, Constant.KEY_LOGGED_IN_ID, userVo.userId)
        goToDashboard()
        CustomLog.d("userVo", Gson().toJson(userVo))
    }

    fun callApiRegister() {

        try {
            if (isNetworkAvailable(context)) {
                btn_register!!.setText(R.string.text_daftar)
                try {
                    val request = HttpRequestVO(Constant.URL_REGISTER + etEmail!!.text.toString())
                    if (userId > 0) {
                        request.params[Constant.KEY_USER_ID] = userId
                    } else {
                        // request.params[Constant.KEY_PHONE_REGISTER] = "08569004710"
                        //request.params[Constant.KEY_PASSWORD] = password
                    }
                    // request.params[Constant.KEY_DEVICE_UID] = Constant.GCM_DEVICE_ID //FirebaseInstanceId.getInstance().getToken());
                    // request.headres[Constant.KEY_COOKIE] = cookie
                    // request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.requestMethod = HttpGet.METHOD_NAME


                    val callback = Handler.Callback { msg ->
                        try {
                            val response = msg.obj as String
                            CustomLog.e("repsonse", "" + response)
                            if (response != null) {
                                val json = JSONObject(response)
                                if (json[Constant.KEY_RESULT] is String) {

                                    card_register?.visibility = View.GONE
                                    card_succes_register?.visibility = View.VISIBLE
                                } else {
                                    val vo = Gson().fromJson(response, SignInResponse::class.java)
                                    if (TextUtils.isEmpty(vo.error)) {
                                        card_register?.visibility = View.GONE
                                        card_succes_register?.visibility = View.VISIBLE
                                        //handleLoginResponse(vo, context)
                                    } else {

                                        Util.showSnackbar(v, vo.errorMessage)
                                    }
                                }
                            } else {
                                btn_register!!.setText(R.string.text_daftar)
                            }
                        } catch (e: Exception) {
                            CustomLog.e(e)
                        }

                        // dialog.dismiss();
                        true
                    }
                    HttpRequestHandler(activity, Handler(callback)).run(request)
                } catch (ignore: Exception) {
                }
            } else {
                notInternetMsg(v)
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun callLoginApi() {
        try {
            if (isNetworkAvailable(context)) {
                bSignIn!!.setText(R.string.TXT_SIGNING_IN)
                try {
                    val request = HttpRequestVO(Constant.URL_LOGIN)
                    if (userId > 0) {
                        request.params[Constant.KEY_USER_ID] = userId
                    } else {
                        request.params[Constant.KEY_EMAIL] = email
                        request.params[Constant.KEY_PASSWORD] = password
                    }
                    // request.params[Constant.KEY_DEVICE_UID] = Constant.GCM_DEVICE_ID //FirebaseInstanceId.getInstance().getToken());
                    // request.headres[Constant.KEY_COOKIE] = cookie
                    // request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.params[email]
                    request.params[password]
                    request.requestMethod = HttpPost.METHOD_NAME
                    val callback = Handler.Callback { msg ->
                        try {
                            val response = msg.obj as String
                            CustomLog.e("repsonse", "" + response)
                            if (response != null) {
                                val json = JSONObject(response)
                                if (json[Constant.KEY_RESULT] is String) {
                                    goToScreenAsPerResult(json)
                                } else {
                                    val vo = Gson().fromJson(response, SignInResponse::class.java)
                                    if (TextUtils.isEmpty(vo.error)) {
                                        handleLoginResponse(vo, context)
                                    } else {
                                        bSignIn!!.setText(R.string.TXT_SIGN_IN)
                                        card_login?.visibility = View.GONE
                                        card_register?.visibility = View.VISIBLE
                                        text_title_register?.text =
                                            "Nomor " + etEmail!!.text.toString() + " Belum Terdaftar"
                                        // Util.showSnackbar(v, vo.errorMessage)
                                    }
                                }
                            } else {
                                bSignIn!!.setText(R.string.TXT_SIGN_IN)
                            }
                        } catch (e: Exception) {
                            CustomLog.e(e)
                        }

                        // dialog.dismiss();
                        true
                    }
                    HttpRequestHandler(activity, Handler(callback)).run(request)
                } catch (ignore: Exception) {
                }
            } else {
                notInternetMsg(v)
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun onBackPressed() {
        activity.finish()
    }

    private fun goToScreenAsPerResult(json: JSONObject) {
        try {
            val result = json.getString("result")
            when (result) {
                Constant.RESULT_FORM_0, Constant.RESULT_FORM_1 -> goToSignUpFragment()
                Constant.RESULT_FORM_2 -> goToProfileImageFragment()
                Constant.RESULT_FORM_3 -> {
                    val id = json.getInt("user_subscription_id")
                    openWebView(
                        Constant.URL_SUBSCRIPTION + "&user_subscription_id=" + id,
                        getStrings(R.string.TITLE_SUBSCRIPTION)
                    )
                }
                Constant.RESULT_FORM_4 -> fragmentManager.beginTransaction()
                    .replace(R.id.container, JoinFragment()).commit()
                Constant.RESULT_FORM_OTP, Constant.RESULT_FORM_OTP_LOGIN -> openOtpFragment(
                    OTPFragment.FROM_SIGNIN,
                    email,
                    password
                )
            }
        } catch (e: JSONException) {
            CustomLog.e(e)
        }
    }// etEmail.setError(txtInvalidEmail);//  etPassword.setError(txtInvalidPassword);

    /*&& EMAIL_ADDRESS.matcher(email).matches()*/


    private val isValid: Boolean
        private get() {
            userId = 0
            var result = false
            //  tv_title_register.text = "Nomor 08XXXXXX Belum Terdaftar" + etEmail!!.text
            email = etEmail!!.text.toString().drop(1)
            email = "phone-" + email + "@matani.id "
            password = etEmail!!.text.toString().drop(1) + "@matani"





            if (!TextUtils.isEmpty(email) /*&& EMAIL_ADDRESS.matcher(email).matches()*/) {
                val isChecked = true
                // SPref.getInstance().updateSharePreferences(context, Constant.KEY_EMAIL, if (isChecked) email else "")
                if (!TextUtils.isEmpty(password)) {
                    result = true
                } else {
                    Util.showSnackbar(v, getStrings(R.string.MSG_INVALID_PASSWORD))
                    //  etPassword.setError(txtInvalidPassword);
                }
            } else {
                Util.showSnackbar(v, getStrings(R.string.MSG_INVALID_EMAIL))
                // etEmail.setError(txtInvalidEmail);
            }
            return result
        }

    private fun callCheckLogin() {
        try {
            if (isNetworkAvailable(context)) {
                try {
                    showSubscribeDialog()
                    val request = HttpRequestVO(Constant.URL_CHECK_LOGIN)
                    request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                    request.requestMethod = HttpPost.METHOD_NAME
                    request.headres[Constant.KEY_COOKIE] = cookie
                    val callback = Handler.Callback { msg ->
                        try {
                            val response = msg.obj as String
                            CustomLog.e("repsonse", "" + response)
                            if (response != null) {
                                val json = JSONObject(response)
                                val isUserLoggedIn = json[Constant.KEY_RESULT] !is String
                                SPref.getInstance().updateSharePreferences(
                                    context,
                                    Constant.KEY_LOGGED_IN,
                                    isUserLoggedIn
                                )
                                if (isUserLoggedIn) {
                                    val resp = Gson().fromJson(response, SignInResponse::class.java)
                                    SPref.getInstance().saveUserInfo(
                                        context,
                                        Constant.KEY_USERINFO_JSON,
                                        resp
                                    )
                                    val vo: UserMaster = resp.result
                                    SPref.getInstance().updateSharePreferences(
                                        context,
                                        Constant.KEY_LOGGED_IN_ID,
                                        vo.loggedinUserId
                                    )
                                    vo.authToken = vo.authToken
                                    SPref.getInstance().saveUserMaster(context, vo, resp.sessionId)
                                    goToDashboard()
                                }
                            } else {
                                notInternetMsg(v)
                            }
                        } catch (e: Exception) {
                            hideBaseLoader()
                            CustomLog.e(e)
                            notInternetMsg(v)
                        } // dialog.dismiss();
                        true
                    }
                    HttpRequestHandler(activity, Handler(callback)).run(request)
                } catch (e: Exception) {
                    hideBaseLoader()
                }
            } else {
                hideBaseLoader()
                notInternetMsg(v)
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    fun showSubscribeDialog() {
        try {
            if (null != progressDialog && progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            progressDialog = ProgressDialog.show(context, "", "", true)
            progressDialog.setCanceledOnTouchOutside(true)
            progressDialog.setCancelable(true)
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog.setContentView(R.layout.dialog_subscription)
            ThemeManager().applyTheme(
                progressDialog.findViewById<View>(R.id.rlDialogMain) as ViewGroup,
                context
            )
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    /*  override fun onItemClicked(object1: Int, object2: Any, postion: Int): Boolean {
          when (object1) {
              Constant.Events.GCM_FETCHED -> if (isGCMfetchedForFacebook) callFacebookApi() else callLoginApi()
              Constant.Events.MUSIC_MAIN -> {
                  toggleDemoLayout()
                  userId = postion
                  if (TextUtils.isEmpty(Constant.GCM_DEVICE_ID)) {
                      isGCMfetchedForFacebook = false
                      GetGcmId(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context)
                  } else {
                      callLoginApi()
                  }
              }
              Constant.Events.CLICKED_OPTION -> openWebView(socialList!![postion].href, socialList[postion].title)
          }
          return false
      }
  */


    override fun onItemClicked(eventType: Int?, data: Any?, position: Int): Boolean {
        when (data) {
            Constant.Events.GCM_FETCHED -> if (isGCMfetchedForFacebook) callFacebookApi() else callLoginApi()
            Constant.Events.MUSIC_MAIN -> {
                toggleDemoLayout()
                userId = position
                if (TextUtils.isEmpty(Constant.GCM_DEVICE_ID)) {
                    isGCMfetchedForFacebook = false
                    GetGcmId(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context)
                } else {
                    callLoginApi()
                }
            }
            Constant.Events.CLICKED_OPTION -> openWebView(
                socialList!![position].href,
                socialList[position].title
            )
        }
        return false
    }

    companion object {


        //}, ParserCallbackInterface {
        private const val CODE_LOGIN = 100
        private const val RC_FACEBOOK = 64206
        private const val RC_GOOGLE = 64207
        private const val RC_TWITTER = 64208
        const val FB_PROFILE_FIRST = "http://graph.facebook.com/"
        const val FB_PROFILE_SECOND = "/picture?type=large"
        const val FIELDS = "fields"
        const val FB_PROFILE_CONTENT = "id,first_name,last_name,name,email,gender,birthday"
        lateinit var mTwitterAuthClient: TwitterAuthClient

        fun newInstance(type: Int): SignInFragment2 {
            val frag = SignInFragment2()
            frag.type = type
            return frag
        }

        fun newInstance(listener: OnUserClickedListener<Int, Any?>?, type: Int): SignInFragment2 {
            val frag = SignInFragment2()
            frag.type = type
            frag.listener = listener
            return frag
        }
    }

    private fun getTwitterSession(): TwitterSession? {

        //NOTE : if you want to get token and secret too use uncomment the below code
        /*TwitterAuthToken authToken = session.getAuthToken();
        String token = authToken.token;
        String secret = authToken.secret;*/

        return TwitterCore.getInstance().sessionManager.activeSession
    }

    fun twitterLogin() {
        if (getTwitterSession() == null) {
            mTwitterAuthClient!!.authorize(getActivity(), object : Callback<TwitterSession>() {
                override fun success(twitterSessionResult: Result<TwitterSession>) {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show()
                    val twitterSession = twitterSessionResult.data
                    fetchTwitterEmail(twitterSession)

                }

                override fun failure(e: TwitterException) {
                    Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show()
                }
            })
        } else {//if user is already authenticated direct call fetch twitter email api
            fetchTwitterEmail(getTwitterSession())
        }
    }

    fun fetchTwitterEmail(twitterSession: TwitterSession?) {
        mTwitterAuthClient?.requestEmail(twitterSession, object : Callback<String>() {
            override fun success(result: Result<String>) {
                //here it will give u only email and rest of other information u can get from TwitterSession

                Log.d(TAG, "twitterLogin:userId" + twitterSession!!.userId)
                Log.d(TAG, "twitterLogin:userName" + twitterSession!!.userName)
                Log.d(TAG, "twitterLogin: result.data" + result.data)

//                val i = Intent(this@LoginActivity, SignupActivity::class.java)
//                val bundle = Bundle()
//                bundle.putString(Utils.FIRST_NAME, "")
//                bundle.putString(Utils.LAST_NAME, "")
//                bundle.putString(Utils.EMAIL, result.data)
//                bundle.putString(Utils.AUTH_TYPE, "TWITTER")
//                bundle.putString(Utils.TPA_TOKEN, twitterSession.userId.toString())
//                i.putExtras(bundle)
//                startActivity(i)
//                finish()
                //  btnLogin!!.visibility = View.GONE
                // txtViewDetails!!.visibility = View.VISIBLE
                // btnLogut!!.visibility = View.VISIBLE
                twitterId = "" + twitterSession!!.userId
                twitterDisplayName = twitterSession!!.userName
                if (result.data != null) {
                    twitterEmail = result.data
                }

                twitterToken = twitterSession.userId.toString()
                var str = "Now you are successfully login with twitter \n\n"
                var tokenStr = ""
                var usernameStr = ""
                var emailStr = ""
                if (twitterToken != null || twitterToken != "") {
                    tokenStr = "User Id : " + twitterToken + "\n\n"
                }
                callTwitterApi()

                if (twitterDisplayName != null || twitterDisplayName != "") {
                    usernameStr = "Username : " + twitterDisplayName + "\n\n"
                }

//                if (email != null || email != "") {
//                    emailStr = "Email ID : " + email + "\n\n"
//                }

                Log.e("str", "" + str + tokenStr + usernameStr + emailStr)

                //        txtViewDetails!!.setText("" + str + tokenStr + usernameStr + emailStr)

            }

            override fun failure(exception: TwitterException) {
                Toast.makeText(
                    getActivity(),
                    "Failed to authenticate. Please try again.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
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

    fun showDialogNotice() {
        val dialogBuilder = AlertDialog.Builder(context)
            dialogBuilder.setMessage("Mohon maaf nomor handphone Anda tidak bisa menerima OTP registrasi untuk sementara ini, mohon gunakan nomor handphone yang lain, terima kasih.")
            // if the dialog is cancelable
            .setCancelable(false)
            .setPositiveButton("Selesai", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()

            })

        val alert = dialogBuilder.create()
       // alert.setTitle("Mohon maaf nomor handphone Anda tidak bisa menerima OTP registrasi untuk sementara ini, mohon gunakan nomor handphone yang lain, terima kasih.")
        alert.show()
    }

}