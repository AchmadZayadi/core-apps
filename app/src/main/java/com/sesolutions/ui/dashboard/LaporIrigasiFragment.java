package com.sesolutions.ui.dashboard;

import android.Manifest;
import android.animation.Animator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Looper;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.Techniques;
import com.sesolutions.http.HttpImageNotificationRequest;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.imageeditengine.ImageEditor;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SesResponse;
import com.sesolutions.responses.location.MyLastLocation;
import com.sesolutions.ui.WebViewActivity;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.live.LiveVideoActivity;
import com.sesolutions.responses.Emotion;
import com.sesolutions.responses.Feeling;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.Video;
import com.sesolutions.responses.feed.Activity;
import com.sesolutions.responses.feed.Attachment;
import com.sesolutions.responses.feed.Attribution;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Item_user;
import com.sesolutions.responses.feed.LocationActivity;
import com.sesolutions.responses.feed.Tagged;
import com.sesolutions.sesdb.SesDB;
import com.sesolutions.slidedatetimepicker.SlideDateTimeListener;
import com.sesolutions.slidedatetimepicker.SlideDateTimePicker;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.OutsideShareActivity;
import com.sesolutions.ui.customviews.AnimationAdapter;
import com.sesolutions.ui.customviews.AttributionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.customviews.slidinguppanel.SlidingUpPanelLayout;
import com.sesolutions.ui.dashboard.composervo.ComposerOption;
import com.sesolutions.ui.dashboard.composervo.ComposerOptions;
import com.sesolutions.ui.dashboard.composervo.FeedBg;
import com.sesolutions.ui.dashboard.composervo.PrivacyOptions;
import com.sesolutions.ui.poll.CreateEditPollFragment;
import com.sesolutions.ui.postfeed.FeelingStickerFragment;
import com.sesolutions.ui.postfeed.GifFragment;
import com.sesolutions.ui.postfeed.MentionPeopleFragment;
import com.sesolutions.ui.postfeed.PrivacyDialogFragment;
import com.sesolutions.ui.postfeed.TagPeopleFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SpanUtil;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.graphics.Typeface.BOLD;
import static android.graphics.Typeface.NORMAL;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.sesolutions.utils.Constant.TAG;

public class LaporIrigasiFragment extends ApiHelper implements View.OnClickListener, OnUserClickedListener<Integer, Object>, TextWatcher, SlidingUpPanelLayout.PanelSlideListener, SwipeRefreshLayout.OnRefreshListener, OnKeyboardVisibilityListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener/*, MyMultiPartEntity.ProgressListener*/ {

    private AppCompatEditText etBodyBg;
    private View v;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private final int LOCATION_AUTOCOMPLETE_REQUEST_CODE = 2;
    private ImageView ivProfileImage;
    private TextView tvTitle;
    private TextView tvTitleName;
    private TextView tvPostSetting;
    private ImageView ivPrivacyImage;

    private TextView tvPostDate;
    private ComposerOption composerOption;
    private View cvBgOption;
    private View cvBgReveal;
    private View cvBgCollapse;
    private View cvHorizontal;
    private RecyclerView rvBgImage;
    private FeedAttachImageAdapter adapterImage;
    private List<ComposerOptions> attachList;
    private TextView tvVideoDesc;
    private TextView tvVideoTitle;
    private ImageView ivVideoImage;
    private List<String> imageList;
    private TextView tvLocation;
    private EditText etPrice;
    private EditText etSelling;
    private EditText etDescription;
    private int emojiSize;
    private ImageView ivPostSticker;

    private boolean isStickerSelected;
    private boolean isImageSelected;
    private boolean isVideoSelected;
    private boolean isLinkSelected;
    private boolean isBuySellSelected;
    private boolean isMusicSelected;
    private boolean isGifSelected;
    private boolean isTaggedUsers;
    private boolean isFeelingSelected;
    private boolean isDateSelected;
    private boolean isCheckedIn;

    private Place place;
    private boolean selectorShown;
    private int selectedOption;
    private LocationActivity vo;

    //all mentioned friend object saved here
    private List<Friends> friendList;

    private int bodyLength;
    private int index;
    private List<FeedBg> bgList;
    private View llPostBg;
    private ImageView ivBgImage;
    private int previousBgPosition = 0;
    private View cvBgImageGrid;
    private View rlBgImageOptionHorizontal;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private int resId;
    private String resType;
    private List<String> photopath;
    private SlidingUpPanelLayout mLayout;
    private PrivacyOptions selectedPrivacy;
    private List<String> musicLIst;
    private String postingType;
    private int currentEditingImageIndex = -1;
    SharedPreferences mPrefs;
    final String dialogPosting = "dialogPosting";
    public int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    MyLastLocation location = new MyLastLocation();
    double latitdue = 0;
    double longtitude = 0;
    LinearLayout layoutDragView;
    TextView tvPilihKategori, tvPilihFoto, tvKategori;
    LinearLayout layputKategori;
    String hastagTitle = "";
    AlertDialog alertDialog1;

    CharSequence[] values = {"Infrastruktur Irigasi", "Kondisi Air Irigasi"};

    public static LaporIrigasiFragment newInstance(ComposerOption response, int selectedOption) {
        return newInstance(response, selectedOption, 0, null);
    }

    public static LaporIrigasiFragment newInstance(ComposerOption response, int selectedOption, List<String> photopath) {
        LaporIrigasiFragment frag = new LaporIrigasiFragment();
        frag.composerOption = response;
        frag.selectedOption = selectedOption;
        frag.photopath = photopath;
        return frag;
    }

    public static LaporIrigasiFragment newInstance(ComposerOption response, int selectedOption, List<String> photopath, String resType, int resId) {
        LaporIrigasiFragment frag = new LaporIrigasiFragment();
        frag.composerOption = response;
        frag.selectedOption = selectedOption;
        frag.photopath = photopath;
        frag.resType = resType;
        frag.resId = resId;
        return frag;
    }

    public static LaporIrigasiFragment newInstance(ComposerOption response, int selectedOption, int resId, String resType) {

        LaporIrigasiFragment frag = new LaporIrigasiFragment();
        frag.composerOption = response;
        frag.selectedOption = selectedOption;
        frag.resId = resId;
        frag.resType = resType;
        return frag;
    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_lapor_irigasi, container, false);
        try {
            // applyTheme(v);
            friendList = new ArrayList<>();
            //rootLayout=v.findViewById(R.id.sliding_layout);
            AppConfiguration.isBgOptionEnabled = composerOption.getResult().getFeedBgStatusPost() != null;
            init();
            setData();
            setAttribution();

            //get location
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }

            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            Boolean firstInstall = mPrefs.getBoolean(dialogPosting, false);
            if (!firstInstall) {
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean(dialogPosting, true);
                editor.commit(); // Very important to save the preference
                showDialog();
            }

            checkLocationPermission();

            firstflag = false;

            try {
                //    rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
                setKeyboardVisibilityListener(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }


         /*   try {
                rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
            }catch (Exception ex){
                ex.printStackTrace();
            }*/

            BaseActivity.gifimageurl = "";

//            if (selectedOption > -1) {
//                new Handler().postDelayed(() -> onItemClicked(0, "", selectedOption), 500);
            if (selectedOption == 1) {
                onResponseSuccess(REQ_CODE_IMAGE, photopath);
            } else if (selectedOption == -2) {
                //it means user sharing data from outside
                new Handler().postDelayed(this::setDataComingFromOtherApp, 500);

            }

            try {
                (activity).setEmotion(null);
                hideView(R.id.rlSticker);
                isStickerSelected = false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
//                new AlertDialog.Builder(context)
//                        .setTitle("tittle")
//                        .setMessage("iyaa bener")
//                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //Prompt the user once explanation has been shown
//                                ActivityCompat.requestPermissions(activity,
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                        MY_PERMISSIONS_REQUEST_LOCATION);
//                            }
//                        })
//                        .create()
//                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    private void showDialog() {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }


            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            SpannableString msg = new SpannableString("Dengan menggunakan fitur ini, saya menyetujui Syarat Penggunaan dan Kebijakan Privasi Matani.ID");


            ClickableSpan syarat = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("web", "https://matani.id/pages/policy");
                    intent.putExtra("title", "Syarat dan Ketentuan");
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);

                }

            };

            ClickableSpan kebijakan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("web", "https://matani.id/pages/tos");
                    intent.putExtra("title", "Kebijakan Privasi");

                    startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);

                }
            };

            msg.setSpan(syarat, 46, 64, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            msg.setSpan(kebijakan, 68, 86, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tvMsg.setText(msg);
            tvMsg.setMovementMethod(LinkMovementMethod.getInstance());

            AppCompatButton bOk = progressDialog.findViewById(R.id.bCamera);
            bOk.setText(R.string.TXT_OK);
            AppCompatButton bCancel = progressDialog.findViewById(R.id.bGallary);
            bCancel.setText(R.string.TXT_CANCEL);
            bCancel.setVisibility(View.GONE);
            bOk.setText("Setuju");
            bOk.setOnClickListener(v -> {
                progressDialog.dismiss();


            });


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setDataComingFromOtherApp() {
        hideSlide();
        switch (OutsideShareActivity.SHARING) {
            case OutsideShareActivity.SHARING_URL:
                callPreviewLinkApi(((OutsideShareActivity) activity).sharingText);
                etBody().setText(((OutsideShareActivity) activity).sharingText);
                tempLink = ((OutsideShareActivity) activity).sharingText;
                break;
            case OutsideShareActivity.SHARING_TEXT:
                etBody().setText(((OutsideShareActivity) activity).sharingText);
                break;
            case OutsideShareActivity.SHARING_IMAGE_MULTIPLE:
            case OutsideShareActivity.SHARING_IMAGE:
                etBody().setText(((OutsideShareActivity) activity).sharingText);
                CustomLog.e("path", new Gson().toJson(((OutsideShareActivity) activity).mSelectPath));
                onResponseSuccess(REQ_CODE_IMAGE, ((OutsideShareActivity) activity).mSelectPath);
                break;
        }
    }

    private void hideView(int id) {
        v.findViewById(id).setVisibility(View.GONE);
    }

    private void showView(int id) {
        v.findViewById(id).setVisibility(View.VISIBLE);
    }

    private void init() {
        try {
            if (AppConfiguration.memberImageShapeIsRound) {
                v.findViewById(R.id.ivProfile).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivProfile1).setVisibility(View.GONE);
                ivProfileImage = v.findViewById(R.id.ivProfile);
            } else {
                v.findViewById(R.id.ivProfile).setVisibility(View.GONE);
                v.findViewById(R.id.ivProfile1).setVisibility(View.VISIBLE);
                ivProfileImage = v.findViewById(R.id.ivProfile1);
            }
            //ivProfileImage = v.findViewById(R.id.ivProfile);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvTitleName = v.findViewById(R.id.tvTitleName);
            tvPostSetting = v.findViewById(R.id.tvPostSetting);
            cvHorizontal = v.findViewById(R.id.cvHorizontal);

            tvPilihFoto = v.findViewById(R.id.tvPilihFoto);
            tvPilihKategori = v.findViewById(R.id.tvPilihKategori);
            layputKategori = v.findViewById(R.id.layout_bottom);
            tvKategori = v.findViewById(R.id.tvKategory);

            layputKategori.setVisibility(View.VISIBLE);

            v.findViewById(R.id.llPrivacy).setOnClickListener(this);
            v.findViewById(R.id.tvDone).setOnClickListener(this);
            etBody = v.findViewById(R.id.etPost);

            tvPilihKategori.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogCheckBox();
                }
            });

            tvPilihFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openImagePicker();
                }
            });

            etBody.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    if (etBody.hasFocus()) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_SCROLL:
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                return true;
                        }
                    }
                    return false;
                }
            });
            etBodyBg = v.findViewById(R.id.etPostBg);
            llPostBg = v.findViewById(R.id.llPostBg);
            ivBgImage = v.findViewById(R.id.ivBgImage);
            // etBody.setOnClickListener(this);

            ivPrivacyImage = v.findViewById(R.id.ivPrivacyImage);
            TextView tv1 = v.findViewById(R.id.tv1);
            TextView tv2 = v.findViewById(R.id.tv2);
            TextView tv3 = v.findViewById(R.id.tv3);
            TextView tv4 = v.findViewById(R.id.tv4);
            TextView tv5 = v.findViewById(R.id.tv5);
            TextView tv6 = v.findViewById(R.id.tv6);
            TextView tv7 = v.findViewById(R.id.tv7);
            TextView tv8 = v.findViewById(R.id.tv8);

            tvPostDate = v.findViewById(R.id.tvPostDate);
            TextView tvDateImage = v.findViewById(R.id.tvDateImage);

            v.findViewById(R.id.ivBack).setOnClickListener(this);
            initSlider();
            cvHorizontal.setOnClickListener(this);
            Typeface iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            //  FontManager.markAsIconContainer(cvHorizontal, iconFont);
            tvDateImage.setTypeface(iconFont, NORMAL);
            tv1.setTypeface(iconFont, NORMAL);
            tv2.setTypeface(iconFont, NORMAL);
            tv3.setTypeface(iconFont, NORMAL);
            tv4.setTypeface(iconFont, NORMAL);
            tv5.setTypeface(iconFont, NORMAL);
            tv6.setTypeface(iconFont, NORMAL);
            tv7.setTypeface(iconFont, NORMAL);
            tv8.setTypeface(iconFont, NORMAL);
            emojiSize = context.getResources().getInteger(R.integer.header_emoji_size);

            selectorShown = false;

            layoutDragView = v.findViewById(R.id.dragView);

            try {

                try {

                    for (int k = 0; k < composerOption.getResult().getComposerOptions().size(); k++) {
                        switch (composerOption.getResult().getComposerOptions().get(k).getName()) {
                            case "addPhoto":
                                tv1.setVisibility(View.VISIBLE);
                                break;
                            case "addVideo":
                                tv2.setVisibility(View.VISIBLE);
                                break;
                            case "checkIn":
                                tv3.setVisibility(View.VISIBLE);
                                break;
                            case "addLink":
                                tv4.setVisibility(View.VISIBLE);
                                break;
                            case "sellSomething":
                                tv5.setVisibility(View.VISIBLE);
                                break;
                            case "scheduledPost":
                                tv6.setVisibility(View.VISIBLE);
                                break;
                            case "tagPeople":
                                tv7.setVisibility(View.VISIBLE);
                                break;
                            case "emotions":
                                tv8.setVisibility(View.VISIBLE);
                                break;

                        }

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    tv1.setTextColor(Color.parseColor(Util.getCode(composerOption.getResult().getComposerOptions().get(0).getName(), true)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    tv2.setTextColor(Color.parseColor(Util.getCode(composerOption.getResult().getComposerOptions().get(1).getName(), true)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    tv3.setTextColor(Color.parseColor(Util.getCode(composerOption.getResult().getComposerOptions().get(2).getName(), true)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    tv4.setTextColor(Color.parseColor(Util.getCode(composerOption.getResult().getComposerOptions().get(3).getName(), true)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


                try {
                    tv5.setTextColor(Color.parseColor(Util.getCode(composerOption.getResult().getComposerOptions().get(4).getName(), true)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


                try {
                    tv6.setTextColor(Color.parseColor(Util.getCode(composerOption.getResult().getComposerOptions().get(5).getName(), true)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    tv7.setTextColor(Color.parseColor(Util.getCode(composerOption.getResult().getComposerOptions().get(6).getName(), true)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                try {
                    tv8.setTextColor(Color.parseColor(Util.getCode(composerOption.getResult().getComposerOptions().get(7).getName(), true)));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            } catch (NullPointerException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setAttribution() {
        if (Constant.ResourceType.PAGE.equals(resType)
                || Constant.ResourceType.BUSINESS.equals(resType)
        ) {
            Attribution attr = SPref.getInstance().getAttribution(context);
            if (null != attr) {
                postingType = attr.getGuid();
                v.findViewById(R.id.llAttribution).setVisibility(View.VISIBLE);
                v.findViewById(R.id.llAttribution).setOnClickListener(this);
                Util.showImageWithGlide(v.findViewById(R.id.ivAttribution), attr.getPhoto(), context, R.drawable.placeholder_square);
            }
        }
    }

    private void showAttributionPopUp(View v, int position) {
        try {
            AttributionPopup popup = new AttributionPopup(v.getContext(), position, this, SPref.getInstance().getAttributionOptions(context));
            // popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //  popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            // int vertPos = RelativePopupWindow.VerticalPosition.BELOW;
            //int horizPos = RelativePopupWindow.HorizontalPosition.CENTER;
            popup.showOnAnchor(v, RelativePopupWindow.VerticalPosition.BELOW, RelativePopupWindow.HorizontalPosition.CENTER, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //init sliding up panel
    private void initSlider() {

        cvBgOption = v.findViewById(R.id.cvBgOption);
        cvBgCollapse = v.findViewById(R.id.cvBgCollapse);
        cvBgReveal = v.findViewById(R.id.cvBgReveal);
        cvBgOption.setOnClickListener(this);
        cvBgCollapse.setOnClickListener(this);

        rlBgImageOptionHorizontal = v.findViewById(R.id.rlBgImageOptionHorizontal);
        rlBgImageOptionHorizontal.setVisibility(AppConfiguration.isBgOptionEnabled ? View.VISIBLE : View.GONE);
        // swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        //swipeRefreshLayout.setOnRefreshListener(this);
        cvBgImageGrid = v.findViewById(R.id.cvBgImageGrid);
        cvBgImageGrid.setOnClickListener(this);


        mLayout = v.findViewById(R.id.sliding_layout);
        mLayout.setAnchorPoint(0.5f);
        cvHorizontal.setVisibility(View.GONE);
        //  mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        mLayout.addPanelSlideListener(this);
        mLayout.setPanelHeight(dpToPx(AppConfiguration.isBgOptionEnabled ? 88 : 44));
        mLayout.setFadeOnClickListener(view -> mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED));
        hideSlide();
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        // Log.i(TAG, "onPanelSlide, offset " + slideOffset);
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        Log.i(TAG, "onPanelStateChanged " + newState);
        switch (newState) {
            case DRAGGING:
                closeKeyboard();
                cvHorizontal.setVisibility(View.GONE);
                v.findViewById(R.id.llArrow).setVisibility(View.GONE);
                //   rvAttach1.setVisibility(View.VISIBLE);

                break;
            case ANCHORED:
                closeKeyboard();
                cvHorizontal.setVisibility(View.GONE);
                v.findViewById(R.id.llArrow).setVisibility(View.VISIBLE);

                break;
            case EXPANDED:

                break;
            case COLLAPSED:
                cvHorizontal.setVisibility(View.GONE);
                //   rvAttach1.setVisibility(View.GONE);
                //mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                break;
        }
    }

    private Timer timer = new Timer();
    private final long DELAY = 1000; // Milliseconds

    @Override
    public void afterTextChanged(Editable s) {
        try {
            if (s.toString().contains("#")) {
                removeEditTextListener();
                etBody().setText(getSpan(s.toString()));
                //  etBody().setSelection(index + wordCount == 0 ? -1 : wordCount);
                etBody().setSelection(index + 1);
                addEditTextListener();
            }

        } catch (Exception e) {
            CustomLog.e("PostFeed", "ArrayIndexOutofBound _index" + index + "__s.length()" + s.length());
            etBody().setSelection(s.length());
            addEditTextListener();
        }

    }

    private AppCompatEditText etBody() {
        return (previousBgPosition == 0) ? etBody : etBodyBg;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        //  CustomLog.e("beforeTextChanged", s + " start__:" + start + " before__:" + after + " count__:" + count);
        bodyLength = s.length();


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            CustomLog.e("onTextChanged", s + " start__:" + start + " before__:" + before + " count__:" + count);


            String text = etBody.getText().toString();
            if (text.startsWith(" "))
                etBody.setText(text.trim());

            int remove = -1;
            //current index
            index = start + before;
            // int selectedIndex = etBody().getSelectionStart();
            boolean isAtMiddle = (index < s.length() - 1);
            boolean found = false;
            if (friendList.size() > 0 && isAtMiddle) {
                for (int i = 0; i < friendList.size(); i++) {
                    if (!found) {
                        if (index >= friendList.get(i).getStartIndex() && index <= friendList.get(i).getEndIndex()) {
                            remove = i;
                            found = true;
                        }
                    }

                    if (bodyLength > s.length()) {
                        //user deleted something
                        if (index < friendList.get(i).getStartIndex()) {
                            friendList.get(i).decreamentIndex();
                        }
                    } else {
                        //user typed something

                        if (index < friendList.get(i).getStartIndex()) {
                            friendList.get(i).increamentIndex(1);
                        }
                    }
                    //    }
                  /*  CustomLog.e("friendList", "__index=" + i
                            + "__" + friendList.get(i).getStartIndex()
                            + "," + friendList.get(i).getEndIndex());*/
                }
            }
            if (remove > -1) {
                friendList.remove(remove);
                etBody().setText(getSpan(s.toString()));
                etBody().setSelection(index);
            } else {
                checkIfMentioning(getMentioningSequence(s, start, count));
            }

            handler.removeCallbacks(workRunnable);
            workRunnable = () -> {
                ArrayList<String> links = pullLinks(s.toString());
                if (links != null && links.size() > 0) {
                    if (!firstflag) {
                        firstflag = true;
                        callPreviewLinkApi(links.get(0));
                    }

                } else {
                    firstflag = false;
                }
            };
            handler.postDelayed(workRunnable, 1500 /*delay*/);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    boolean firstflag = true;

    Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
    Runnable workRunnable;


    private void checkIfMentioning(String mentionSequence) {
        try {
            if (mentionSequence != null) {
                if (!selectorShown) {
                    OnMentionStarted(mentionSequence);
                }
            }

            Log.e("seq", "" + mentionSequence);
           /* if (mentionSequence == null && selectorShown) {
                OnMentionFinished();
            }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public ArrayList<String> pullLinks(String text) {
        ArrayList<String> links = new ArrayList<String>();

        //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        String regex = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);

        while (m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);
        }
        return links;
    }


    private void OnMentionStarted(String sequence) {
        try {
            CustomLog.e("start", sequence);
            (activity).taskPerformed = 0;
            etBody.removeTextChangedListener(this);
            etBodyBg.removeTextChangedListener(this);
            // String s = etBody.getText().toString();
            //   etBody.setText(s.substring(0, s.length() - 1));
            //  etBody.setText(textBeforeMention);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, MentionPeopleFragment.newInstance(sequence)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private String getMentioningSequence(CharSequence s, int start, int count) {


        Pattern pattern = Pattern.compile("(?<=\\s|^)@([a-z|A-Z|\\.|\\-|\\_|0-9]*)(?=\\s|$)");
        Matcher matcher = pattern.matcher(s.toString());
        String mention = null;
        try {
            while (matcher.find()) {
                if (matcher.start(1) <= start + count &&
                        start + count <= matcher.end(1)
                ) {
                    mention = matcher.group(1);
                    break;
                }
            }
        } catch (IllegalStateException e) {
            CustomLog.e(e);
        }
        return mention;
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
        if (activity.taskPerformed == Constant.FormType.CREATE_POLL) {
            activity.taskPerformed = 0;
            Constant.TASK_POST = true;
            onBackPressed();
        } else if (activity.taskPerformed == Constant.FormType.CREATE_VIDEO_DATA) {

            activity.taskPerformed = 0;
            Constant.TASK_POST = true;
            onBackPressed();
        } else if (activity.taskPerformed == Constant.FormType.CREATE_PAGE_VIDEO) {

            activity.taskPerformed = 0;
            Constant.TASK_POST = true;
            onBackPressed();
        } else {
            updateBodyText();
            refreshText();
            setStickerImage();
            new Handler().postDelayed(() -> {
                if (selectedOption == -3)
                    hideSlide();
            }, 200);
        }
    }

    private void updateBodyText() {
        try {
            removeEditTextListener();
            int task = (activity).taskPerformed;
            if (task == Constant.TASK_MENTION) {
                (activity).taskPerformed = 0;
                String body = etBody().getText().toString();
                if (!TextUtils.isEmpty(body)) {
                    Friends vo = (activity).getFreinds();
                    if (index < body.length() - 1) {

                        vo.setStartIndex(index - 1);
                        vo.setEndIndex(index + vo.getLabel().length());
                        body = body.substring(0, index - 1) + vo.getLabel() + " " + body.substring(index + 1);
                        for (Friends fr : friendList) {
                            if (index < fr.getStartIndex()) {
                                fr.increamentIndex(vo.getLabel().length());
                            }
                        }

                    } else {
                        body = body.substring(0, body.length() - 1);
                        vo.setStartIndex(body.length());
                        body = body + vo.getLabel() + " ";
                        vo.setEndIndex(body.length() - 1);
                    }


                    friendList.add(vo);

                    Collections.sort(friendList, (m1, m2) -> {
                        if (m1.getStartIndex() == m2.getStartIndex()) {
                            return 0;
                        } else if (m1.getStartIndex() > m2.getStartIndex()) {
                            return -1;
                        }
                        return 1;
                    });

                    etBody().setText(getSpan(body));

                    final int len = body.length();
                    new Handler().postDelayed(() -> {
                        try {
                            etBody().setSelection(len);
                            openKeyboard();
                            etBody().requestFocus();
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                    }, 100);

                }
            } else if (task == Constant.TASK_MENTION_CANCEL) {
                etBody().setSelection(etBody().getText().length());
                openKeyboard();
                etBody().requestFocus();
            } else if (task == Constant.TASK_NOMENTION) {

                String sttt = etBody.getText().toString();
                sttt = sttt.replaceAll("[^a-zA-Z0-9]", " ");
                etBody().setText("" + sttt);
                openKeyboard();
                etBody().requestFocus();

                etBody.requestFocus();
                etBody.setFocusable(true);
                etBody.setEnabled(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestFocus(etBody, activity);
                    }
                }, 500);
            }
            // etBody().removeTextChangedListener(this)
        } catch (Exception e) {
            CustomLog.e(e);
        } finally {
            addEditTextListener();
        }
    }


    void requestFocus(View editText, android.app.Activity activity) {
        try {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeEditTextListener() {
        try {
            if (etBody.onCheckIsTextEditor()) {
                etBody.removeTextChangedListener(this);
                etBodyBg.removeTextChangedListener(this);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            CustomLog.e("Ignore", "Ignore this error");
        }
    }


    private void addEditTextListener() {

        etBody.addTextChangedListener(this);
        etBodyBg.addTextChangedListener(this);
    }

    private SpannableString getSpan(String body) {
        SpannableString span = new SpannableString(body);
        try {
            for (Friends fr : friendList) {
                span.setSpan(new StyleSpan(BOLD), fr.getStartIndex(), fr.getEndIndex(), 0);
            }


            int start = -1;
            for (int i = 0; i < body.length(); i++) {
                if (body.charAt(i) == '#') {
                    start = i;
                } else if (body.charAt(i) == ' ' || (i == body.length() - 1 && start != -1)) {
                    if (start != -1) {
                        if (i == body.length() - 1) {
                            i++; // case for if hash is last word and there is no space after word
                        }

                        //final String tag = body.substring(start, i);
                        span.setSpan(new StyleSpan(BOLD), start, i, 0);
                        start = -1;
                    }
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return span;
    }


    /* private int UIColor(float v, float v1, float v2) {
         return new Color().(1, v * 255, v1 * 255, v2 * 255);
     }*/
    private void setStickerImage() {
        try {
            Emotion vo = (activity).getEmotion2();
            if (vo != null) {
                initStickerlayout();
                llPostBg.setVisibility(View.GONE);
                Util.showImageWithGlide(ivPostSticker, vo.getIcon(), context, R.drawable.placeholder_square);
                try {
                    if (imageList.size() > 0) {
                        imageList.clear();
                        if (selectedImageList.size() > 0) {
                            selectedImageList.clear();
                        }
                        adapterImage.notifyDataSetChanged();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void refreshText() {
        try {
            if ((activity).activity != null) {
                isFeelingSelected = (activity).activity.getFeelings() != null;
                SpannableString span = SpanUtil.createSpan(composerOption.getResult().getUser_title(), (activity).activity, context, tvTitleName, emojiSize);
                tvTitleName.setText(span);
                tvTitleName.setMovementMethod(LinkMovementMethod.getInstance());
            }
            if (activity.taskPerformed == Constant.TASK_TAGGING) {
                isTaggedUsers = true;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setData() {
        try {
            Util.showImageWithGlide(ivProfileImage, composerOption.getResult().getUser_image(), context, R.drawable.placeholder_menu);

            tvTitleName.setText(composerOption.getResult().getUser_title());
            activity.activity = new Activity();

            tvTitle.setText(R.string.TITLE_UPDATE_STATUS);
            attachList = composerOption.getResult().getComposerOptions();
            if (attachList != null && attachList.size() > 0) {
                for (ComposerOptions vo : attachList) {
                    vo.setImageCode(Util.getCode(vo.getName(), false));
                    vo.setColorCode(Util.getCode(vo.getName(), true));
                }
                setAttach1RecycleView();
            }
            setBgImageRecycleView();
            tvPostSetting.setText(getPrivacy());
            setPrivacyImage(selectedPrivacy.getName());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setPrivacyImage(String resName) {
        try {
            String packageName = context.getPackageName();
            int id = context.getResources().getIdentifier("privacy_" + resName, "drawable", packageName);
            if (id > 0)
                ivPrivacyImage.setImageResource(id);
            else
                ivPrivacyImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.city));
        } catch (Exception e) {
            ivPrivacyImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.privacy_everyone));
        }
    }

    private void setAttach1RecycleView() {
        try {
            RecyclerView rvAttach1 = v.findViewById(R.id.rvAttachOption);
            rvAttach1.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvAttach1.setLayoutManager(layoutManager);
            AttachOptionAdapter adapter1 = new AttachOptionAdapter(attachList, context, this);
            rvAttach1.setAdapter(adapter1);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setBgImageRecycleView() {
        try {
            // bgList = new ArrayList<String>();
            bgList = composerOption.getResult().getFeedBgStatusPost();
            if (bgList != null && bgList.size() > 0) {
                AppConfiguration.isBgOptionEnabled = true;
                //    initSlider();
            } else {
                AppConfiguration.isBgOptionEnabled = false;
                return;
            }

            rvBgImage = v.findViewById(R.id.rvBgImage);
            rvBgImage.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvBgImage.setLayoutManager(layoutManager);
            AttachBgAdapter adapterBg = new AttachBgAdapter(bgList, context, this);
            rvBgImage.setAdapter(adapterBg);
            // rvAttach1.setNestedScrollingEnabled(false);

            setBgImageGridRecycleView();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setBgImageGridRecycleView() {
        try {

            RecyclerView rvBgImageGrid = v.findViewById(R.id.rvBgImageGrid);
            rvBgImageGrid.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(6, StaggeredGridLayoutManager.VERTICAL);
            rvBgImageGrid.setLayoutManager(layoutManager);
            AttachBgAdapter adapterBgGrid = new AttachBgAdapter(bgList, context, this);
            adapterBgGrid.setGrid(true);
            rvBgImageGrid.setAdapter(adapterBgGrid);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    goBack();
                    break;

                case R.id.llPrivacy:
                    PrivacyDialogFragment.newInstance(composerOption.getResult().getPrivacyOptions(), this).show(fragmentManager, Constant.TITLE_PRIVACY);
                    break;

                case R.id.llAttribution:
                    showAttributionPopUp(v, -1);
                    break;

                case R.id.tvDone:

                    if (hastagTitle.equals("")) {
                        Util.showToast(context, "Anda Belum Memilih Kategori");
                    } else if (etBody().getText().toString().equals("")) {
                        Util.showToast(context, "Anda Belum Menulis Sesuatu");
                    } else {
                        sendPost();
                    }


                    break;
                case R.id.cvBgCollapse:
                    if (cvBgOption.getVisibility() == View.VISIBLE) {
                        hideBgImageOption();
                    } else {
                        showBgImageOption();
                    }
                    break;

                case R.id.cvBgOption:
                    showBgImageGridOption();
                    break;

                case R.id.cvHorizontal:
                    closeKeyboard();
                    //  new Handler().postDelayed(this::showSlide, 200);
                    initSlider();
                    CustomLog.e("cvHorizontal", "clicked");
                    break;

                case R.id.cvBgImageGrid:
                    hideBgImageGridOption();
                    break;

                case R.id.tvLocation:
                    fetchAddress(LOCATION_AUTOCOMPLETE_REQUEST_CODE);
                    break;

                case R.id.ivClearSticker:
                    (activity).setEmotion(null);
                    hideView(R.id.rlSticker);
                    isStickerSelected = false;
                    break;

                case R.id.ivClearLocation:
                    tvLocation.setText(Constant.EMPTY);
                    break;
                case R.id.cvCancel:
                    hideView(R.id.cvChip);
                    isMusicSelected = false;
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {

        try {
            Log.e("OBJECT", "" + object1);

            switch (object1) {
                case Constant.Events.FEED_ATTACH_IMAGE_CANCEL:
                    imageList.remove(postion);
                    selectedImageList.remove(postion);
                    adapterImage.notifyItemRemoved(postion);
                    if (imageList.size() == 0)
                        isImageSelected = false;
                    break;
                case Constant.Events.CONTENT_EDIT:
                    currentEditingImageIndex = postion;
                    startActivityForResult(
                            new ImageEditor.Builder(getActivity(), imageList.get(postion))
                                    .setStickerAssets("stickers")
                                    // .setQuote(title)
                                    // .setQuoteSource(source)
                                    .getEditorIntent(),
                            ImageEditor.RC_IMAGE_EDITOR);
                    break;
                case Constant.Events.PRIVACY_CHANGED:
                    updatePrivacy("" + object2, postion);
                    break;
                case Constant.Events.BG_ATTACH:
                    updateBgImage(postion);
                    break;
                case Constant.Events.ATTRIBUTION_OPTION_CLICK:

                    try {
                        List<Attribution> list = SPref.getInstance().getAttributionOptions(context);
                        postingType = list.get(postion).getGuid();
                        updateImageAndName(list.get(postion));
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                    break;
                default:
                    selectedOption = -3;
                    hideSlide();
                    String name = attachList.get(postion).getName();
                    Log.e("NAMEKEY", "" + name);
                    switch (name) {

                        case "elivestreaming":
                            if (AppConfiguration.isLiveStreamingEnabled)
                                context.startActivity(new Intent(context, LiveVideoActivity.class));
                            else
                                Util.showSnackbar(v, getStrings(R.string.live_disabled));
                            break;
                        case "addPhoto":
                            isVideoSelected = false;
                            //    showImageDialog(Constant.MSG_SELECT_IMAGE_SOURCE);
                            openImagePicker();

                            break;
                        case "addGif":
                            isGifSelected = false;
                            BaseActivity.gifimageurl = "";
                            try {
                                fragmentManager.beginTransaction().replace(R.id.container, GifFragment.newInstance(true)).addToBackStack(null).commit();
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }
                            break;
                        case "addVideo":
                            showVideoSourceDialog(Constant.MSG_CHOOSE_SOURCE, AppConfiguration.canSelectVideoDevice, resType, resId);
                            break;
                        case "checkIn":
                            fetchAddress(PLACE_AUTOCOMPLETE_REQUEST_CODE);
                            break;
                        case "addFile":
                            break;
                        case "addLink":
                            isVideoSelected = false;
                            showLinkDialog(Constant.EMPTY);
                            break;
                        case "sellSomething":
                            initSellLayout();
                            break;
                        case "scheduledPost":
                            selectDateTime();
                            break;
                        case "addPoll":
                            String URL_CREATE = null;
                            String KEY_ID = null;
                            if (Constant.ResourceType.PAGE.equals(resType)) {
                                URL_CREATE = Constant.URL_PAGE_POLL_CREATE;
                                KEY_ID = Constant.KEY_PAGE_ID;
                            } else if (Constant.ResourceType.GROUP.equals(resType)) {
                                URL_CREATE = Constant.URL_GROUP_POLL_CREATE;
                                KEY_ID = Constant.KEY_GROUP_ID;
                            } else if (Constant.ResourceType.BUSINESS.equals(resType)) {
                                URL_CREATE = Constant.URL_BUSINESS_POLL_CREATE;
                                KEY_ID = Constant.KEY_BUSINESS_ID;
                            }
                            if (null != KEY_ID) {
                                Map<String, Object> map = new HashMap<>();
                                map.put(KEY_ID, resId);
                                fragmentManager.beginTransaction().replace(R.id.container, CreateEditPollFragment.newInstance(Constant.FormType.CREATE_POLL, map, URL_CREATE, resType)).addToBackStack(null).commit();
                            }
                            break;
                        case "tagPeople":
                            fragmentManager.beginTransaction().replace(R.id.container, new TagPeopleFragment()).addToBackStack(null).commit();
                            break;
                        case "emotions":
                            goToFeelingStickerFragment();
                            break;
                        case "addMusic":
                            showAudioChooser(false);
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void updateImageAndName(Attribution attribution) {
        Util.showImageWithGlide(ivProfileImage, attribution.getPhoto(), context, R.drawable.placeholder_menu);
        Util.showImageWithGlide(v.findViewById(R.id.ivAttribution), attribution.getPhoto(), context, R.drawable.placeholder_square);
        if (null != attribution.getTitle()) {
            composerOption.getResult().setUser_title(attribution.getTitle());
            refreshText();
        }
    }

    private void updateBgImage(int position) {
        try {
            if (previousBgPosition != position) {
                removeEditTextListener();
                if (position == 0) {

                    etBody.setVisibility(View.VISIBLE);
                    llPostBg.setVisibility(View.GONE);
                    String body = etBodyBg.getText().toString();
                    if (!TextUtils.isEmpty(body)) {
                        etBody.setText(getSpan(body));
                        etBody.setSelection(etBody.getText().length());
                        etBody.requestFocus();
                    }
                } else {
                    llPostBg.setVisibility(View.VISIBLE);
                    etBody.setVisibility(View.GONE);
                    //  llPostBg.setBackgroundColor(Color.parseColor(bgList.get(position)));

                    if (previousBgPosition == 0) {
                        initBgPostLayout();
                        String body = etBody.getText().toString();
                        if (!TextUtils.isEmpty(body)) {
                            etBodyBg.setText(getSpan(body));
                            etBodyBg.setSelection(etBodyBg.getText().length());
                            etBodyBg.requestFocus();
                        }
                    }
                    // if (position == 0) {
                    ivBgImage.setVisibility(View.VISIBLE);
                    String url = bgList.get(position).getPhoto();//"https://media.giphy.com/media/3o85xHGh9Badlvlxn2/giphy.gif";
                    //"https://media.giphy.com/media/1kwH4pweyDm00/giphy.gif";
                    Util.showImageWithGlide(
                            ivBgImage, url, context, R.drawable.placeholder_3_2);
                    // } else {
                    //     ivBgImage.setVisibility(View.GONE);
                    // }
                }
                previousBgPosition = position;
                addEditTextListener();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void hideSlide() {
        if (mLayout != null) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    private void showSlide() {
        if (mLayout != null) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
        }
    }

    private void hideBgImageOption() {

        startAnimation(rvBgImage, Techniques.SLIDE_OUT_LEFT, 300, new AnimationAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                cvBgOption.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // rvBgImage.setVisibility(View.GONE);
            }
        });

        startAnimation(cvBgCollapse, Techniques.ROTATE_OUT, 500, new AnimationAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                // cvBgOption.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cvBgReveal.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showBgImageOption() {
        //cvBgOption.setVisibility(View.VISIBLE);
        //rvBgImage.setVisibility(View.VISIBLE);

        startAnimation(rvBgImage, Techniques.SLIDE_IN_LEFT, 300, new AnimationAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                cvBgOption.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cvBgOption.setVisibility(View.VISIBLE);
                // rvBgImage.setVisibility(View.GONE);
            }
        });

        startAnimation(cvBgCollapse, Techniques.ROTATE_IN, 500, new AnimationAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                cvBgReveal.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
    }

    private void showBgImageGridOption() {
        startAnimation(cvBgImageGrid, Techniques.SLIDE_IN_UP, 400, new AnimationAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLayout.removePanelSlideListener(LaporIrigasiFragment.this);
                rlBgImageOptionHorizontal.setVisibility(View.GONE);
                // cvBgImageGrid.setVisibility(View.VISIBLE);
                cvBgImageGrid.setVisibility(AppConfiguration.isBgOptionEnabled ? View.VISIBLE : View.GONE);
                mLayout.setPanelHeight(dpToPx(44));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLayout.addPanelSlideListener(LaporIrigasiFragment.this);
            }
        });
    }

    private void hideBgImageGridOption() {
        startAnimation(cvBgImageGrid, Techniques.SLIDE_OUT_DOWN, 200, new AnimationAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mLayout.removePanelSlideListener(LaporIrigasiFragment.this);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLayout.setPanelHeight(dpToPx(AppConfiguration.isBgOptionEnabled ? 88 : 44));
                rlBgImageOptionHorizontal.setVisibility(AppConfiguration.isBgOptionEnabled ? View.VISIBLE : View.GONE);
                cvBgImageGrid.setVisibility(View.GONE);
                mLayout.addPanelSlideListener(LaporIrigasiFragment.this);

            }
        });
    }

    private void fetchAddress(int req) {

        try {
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), getStrings(R.string.places_api_key));
            }
            /*Intent intent =

                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(activity);*/
            // Use fields to define the data types to return.

            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.builder(placeFields).build();
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .build(activity);
            startActivityForResult(intent, req);
        } catch (Exception e) {
            CustomLog.e(e);

        }
    }

    private void updatePrivacy(String object2, int position) {
        selectedPrivacy = composerOption.getResult().getPrivacyOptions().get(position);
        SPref.getInstance().updateSharePreferences(context, Constant.KEY_PRIVACY, selectedPrivacy.getName());
        // SPref.getInstance().updateSharePreferences(context, Constant.KEY_PRIVACY_POSITION, position);
        // privacyPosition = position;
        tvPostSetting.setText(selectedPrivacy.getValue());
        setPrivacyImage(selectedPrivacy.getName());
        //ivPrivacyImage.setImageDrawable(ContextCompat.getDrawable(context, privacyImage[position]));
    }

    private String getPrivacy() {
        if (null == selectedPrivacy) {
            String pr = SPref.getInstance().getString(getContext(), Constant.KEY_PRIVACY);
            if (TextUtils.isEmpty(pr)) {
                pr = "everyone";
            }
            List<PrivacyOptions> list = composerOption.getResult().getPrivacyOptions();
            for (PrivacyOptions options : list) {
                if (options.getName().equals(pr)) {
                    selectedPrivacy = options;
                    break;
                }
            }
        }
        return selectedPrivacy.getValue();
    }


    private void goToFeelingStickerFragment() {
        try {
            fragmentManager.beginTransaction().replace(R.id.container, FeelingStickerFragment.newInstance(composerOption.getResult().getActivityStikersMenu(), true)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void initSellLayout() {
        hideView(R.id.llVideoMain);
        hideView(R.id.llLinkMain);
        hideView(R.id.cvChip);
        hideView(R.id.etPost);
        showView(R.id.llSellMain);

        isBuySellSelected = true;

        etSelling = v.findViewById(R.id.etSelling);
        etPrice = v.findViewById(R.id.etPrice);
        tvLocation = v.findViewById(R.id.tvLocation);
        ImageView ivClearLocation = v.findViewById(R.id.ivClearLocation);
        etDescription = v.findViewById(R.id.etDescription);
        etDescription.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (etDescription.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });
        ivClearLocation.setOnClickListener(this);
        tvLocation.setOnClickListener(this);
    }

    private void initStickerlayout() {
        hideView(R.id.llVideoMain);
        hideView(R.id.llLinkMain);
        hideView(R.id.cvChip);
        showView(R.id.etPost);
        hideView(R.id.llSellMain);
        showView(R.id.rlSticker);

        isStickerSelected = true;
        isImageSelected = false;
        isVideoSelected = false;
        isLinkSelected = false;
        isBuySellSelected = false;
        isMusicSelected = false;
        isGifSelected = false;

        llPostBg.setVisibility(View.GONE);
        ivPostSticker = v.findViewById(R.id.ivPostSticker);
        ImageView ivClearSticker = v.findViewById(R.id.ivClearSticker);
        ivClearSticker.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("gif", "" + BaseActivity.gifimageurl);
        if (BaseActivity.gifimageurl != null && BaseActivity.gifimageurl.length() > 0) {
            isGifSelected = true;
            isStickerSelected = false;
            isImageSelected = false;
            isVideoSelected = false;
            isLinkSelected = false;
            isBuySellSelected = false;
            isMusicSelected = false;
            etBody.setVisibility(View.VISIBLE);
            llPostBg.setVisibility(View.VISIBLE);
            ivBgImage.setVisibility(View.VISIBLE);
            etBodyBg.setVisibility(View.GONE);
            Util.showImageWithGlideGIF(ivBgImage, BaseActivity.gifimageurl, context, R.drawable.placeholder_3_2);
        }
    }

    @Override
    public void onResponseSuccess(int reqCode, Object response) {
        try {
            switch (reqCode) {
                case REQ_CODE_LINK:
                    initLinkLayout();
                    break;

                case REQ_CODE_IMAGE:
                    hideView(R.id.llVideoMain);
                    hideView(R.id.llLinkMain);
                    hideView(R.id.cvChip);
                    hideView(R.id.rlSticker);
                    showView(R.id.etPost);
                    unselectBgView();
                    isStickerSelected = false;
                    isImageSelected = true;
                    isVideoSelected = false;
                    isGifSelected = false;
                    isLinkSelected = false;
                    // isBuySellSelected=false;

                    // showView(R.id.rvImageAttach);
                    if (imageList == null) {
                        initImageLayout();
                    }
                    for (String str : (List<String>) response) {
                        if (!imageList.contains(str)) {
                            imageList.add(str);
                        }
                    }
                    selectedImageList = new ArrayList<>();
                    selectedImageList.addAll(imageList);
                    adapterImage.notifyDataSetChanged();

                    try {
                        (activity).setEmotion(null);
                        hideView(R.id.rlSticker);
                        isStickerSelected = false;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    break;

                case REQ_CODE_VIDEO_LINK:
                    initVideoLayout();
                    tvVideoDesc.setText(videoDetail.getDescription());
                    tvVideoTitle.setText(videoDetail.getTitle());
                    ivVideoImage.setVisibility(View.VISIBLE);
                    Util.showImageWithGlide(ivVideoImage, videoDetail.getSrc(), context, R.drawable.placeholder_3_2);
                    try {
                        (activity).setEmotion(null);
                        hideView(R.id.rlSticker);
                        isStickerSelected = false;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;

                case REQ_CODE_VIDEO:
                    if (null != response) {
                        initVideoLayout();
                        String filePath = ((List<String>) response).get(0);
                        videoDetail = new Video();
                        videoDetail.setFromDevice(true);
                        videoDetail.setSrc(filePath);
//                        tvVideoDesc.setText(filePath);
                        //  if (canShowThumbnail) {
                        ivVideoImage.setVisibility(View.VISIBLE);

                        Glide.with(this)
                                .load(Constant.videoUri)
                                .into(ivVideoImage);
                        Log.e("tag", "video");
                        // mFormBuilder.getAdapter().setThumbnailAtTag(clickedFilePostion, getThumbnailPathForLocalFile(activity, Constant.videoUri));
                       /* } else {
                            ivVideoImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_3_2));
                        }*/
                        Constant.videoUri = null;
                        try {
                            (activity).setEmotion(null);
                            hideView(R.id.rlSticker);
                            isStickerSelected = false;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    break;

                case ImageEditor.RC_IMAGE_EDITOR:
                    if (response != null) {
                        String imagePath = ((List<String>) response).get(0);
                        imageList.set(currentEditingImageIndex, imagePath);
                        adapterImage.notifyItemChanged(currentEditingImageIndex);

                        try {
                            (activity).setEmotion(null);
                            hideView(R.id.rlSticker);
                            isStickerSelected = false;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        //   edited_image.setImageBitmap(BitmapFactory.decodeFile(imagePath))
                    }
                    break;

                case REQ_CODE_MUSIC:
                    try {
                        if (null != response) {
                            //  String filePath = ((List<String>) result).get(0);
                            musicLIst = new ArrayList<>();
                            musicLIst.addAll((List<String>) response);
                            initMusicLayout();

                            try {
                                (activity).setEmotion(null);
                                hideView(R.id.rlSticker);
                                isStickerSelected = false;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void unselectBgView() {
        updateBgImage(0);
    }

    private void initLinkLayout() {
        try {
            hideView(R.id.rvImageAttach);
            hideView(R.id.llSellMain);
            hideView(R.id.llVideoMain);
            showView(R.id.llLinkMain);
            hideView(R.id.rlSticker);
            showView(R.id.etPost);
            unselectBgView();
            isStickerSelected = false;
            isImageSelected = false;
            isVideoSelected = false;
            isGifSelected = false;
            isLinkSelected = true;
            isBuySellSelected = false;
            isMusicSelected = false;
            ImageView ivLinkImage = v.findViewById(R.id.ivLinkImage);
            YouTubeThumbnailView videoThumbnailImageView = v.findViewById(R.id.video_thumbnail_image_view);
            TextView tvLinkText = v.findViewById(R.id.tvLinkText);
            tvLinkText.setText(linkDetail.getTitle());
            if (linkDetail.isYouTubeUrl()) {
                ivLinkImage.setVisibility(View.GONE);
                videoThumbnailImageView.setVisibility(View.VISIBLE);

                /*  initialize the thumbnail image view , we need to pass Developer Key */
                videoThumbnailImageView.initialize(getStrings(R.string.places_api_key), new YouTubeThumbnailView.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                        //when initialization is sucess, set the video id to thumbnail to load
                        String videoID = tempLink.substring(tempLink.lastIndexOf("/") + 1);
                        Log.e(TAG, "Youtube Thumbnail " + videoID);
                        youTubeThumbnailLoader.setVideo(videoID);//"CyNeT1VLn3Q");
                        youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                            @Override
                            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                                //when thumbnail loaded successfully release the thumbnail loader as we are showing thumbnail in adapter
                                youTubeThumbnailLoader.release();
                            }

                            @Override
                            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                                //print or show error when thumbnail load failed
                                CustomLog.e(TAG, "Youtube Thumbnail Error " + errorReason.toString());
                            }
                        });
                    }

                    @Override
                    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                        //print or show error when initialization failed
                        CustomLog.e(TAG, "Youtube Initialization Failure");

                    }
                });
            } else {
                ivLinkImage.setVisibility(View.VISIBLE);
                videoThumbnailImageView.setVisibility(View.GONE);
                Util.showImageWithGlide(ivLinkImage, linkDetail.getImages(), context, R.drawable.placeholder_3_2);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void initMusicLayout() {
        try {
            hideView(R.id.rvImageAttach);
            hideView(R.id.llSellMain);
            hideView(R.id.llVideoMain);
            hideView(R.id.llLinkMain);
            hideView(R.id.rlSticker);
            showView(R.id.etPost);
            showView(R.id.cvChip);
            unselectBgView();
            isStickerSelected = false;
            isImageSelected = false;
            isVideoSelected = false;
            isGifSelected = false;
            isLinkSelected = false;
            isBuySellSelected = false;
            isMusicSelected = true;

            ((TextView) v.findViewById(R.id.tvSongName)).setText(musicLIst.get(0));
            v.findViewById(R.id.cvCancel).setOnClickListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void initImageLayout() {
        unselectBgView();
        RecyclerView rvImageAttach = v.findViewById(R.id.rvImageAttach);
        rvImageAttach.setVisibility(View.VISIBLE);
        try {
            imageList = new ArrayList<>();
            rvImageAttach.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvImageAttach.setLayoutManager(layoutManager);
            adapterImage = new FeedAttachImageAdapter(imageList, context, this);
            rvImageAttach.setAdapter(adapterImage);
            rvImageAttach.setNestedScrollingEnabled(false);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void initVideoLayout() {
        hideView(R.id.rvImageAttach);
        hideView(R.id.llSellMain);
        hideView(R.id.llLinkMain);
        hideView(R.id.cvChip);
        hideView(R.id.rlSticker);
        showView(R.id.etPost);
        showView(R.id.llVideoMain);
        unselectBgView();

        isStickerSelected = false;
        isImageSelected = false;
        isVideoSelected = true;
        isGifSelected = false;
        isLinkSelected = false;
        isBuySellSelected = false;
        isMusicSelected = false;

        tvVideoDesc = v.findViewById(R.id.tvImageDescription);
        tvVideoTitle = v.findViewById(R.id.tvImageTitle);
        ivVideoImage = v.findViewById(R.id.ivVideoImage);
    }

    private void initBgPostLayout() {
        hideView(R.id.rvImageAttach);
        hideView(R.id.llSellMain);
        hideView(R.id.llLinkMain);
        hideView(R.id.cvChip);
        hideView(R.id.rlSticker);
        hideView(R.id.llVideoMain);

        isStickerSelected = false;
        isImageSelected = false;
        isVideoSelected = false;
        isGifSelected = false;
        isLinkSelected = false;
        isBuySellSelected = false;
        isMusicSelected = false;
    }

    private void goBack() {

        try {
            if (checkIfAnythingChanged()) {
                showDiscardDialog();
            } else {
                onBackPressed();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

  /*  @Override
    public void onBackPressed() {
        if (checkIfAnythingChanged()) {
            showDiscardDialog();
        } else {
            onBackPressed();
        }
    }*/

    private boolean checkIfAnythingChanged() {
        return !TextUtils.isEmpty(etBody().getText()) ||
                isStickerSelected ||
                isImageSelected ||
                isVideoSelected ||
                isGifSelected ||
                isLinkSelected ||
                isBuySellSelected ||
                isTaggedUsers ||
                isMusicSelected ||
                // isDateSelected ||
                isFeelingSelected ||
                isCheckedIn;
    }

    private void showDiscardDialog() {
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
            ((TextView) progressDialog.findViewById(R.id.tvDialogText)).setText(R.string.MSG_ABANODONED);
            ((AppCompatButton) progressDialog.findViewById(R.id.bCamera)).setText("Tetap Tulis");
            ((AppCompatButton) progressDialog.findViewById(R.id.bGallary)).setText("Batal Kirim");
            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> progressDialog.dismiss());

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> {
                progressDialog.dismiss();
                closeKeyboard();
                if (fragmentManager.getBackStackEntryCount() > 1) {
                    fragmentManager.popBackStack();
                } else {
                    onBackPressed();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }

    private void selectDateTime() {
        // minDate = Util.getNext24HourDateTime();
        try {
            SlideDateTimePicker picker = new SlideDateTimePicker.Builder(activity.getSupportFragmentManager())
                    .setListener(new SlideDateTimeListener() {

                        @Override
                        public void onDateTimeSet(Date date) {
                            // Date d1 = new Date();
                            // long diff = date.getTime() - d1.getTime();
                           /* if (diff > 0) {
                                //  tvDate.setText("Date Time");
                                Util.showSnackbar(v, Constant.MSG_INVALID_DATE);
                            } else {*/
                            v.findViewById(R.id.llDate).setVisibility(View.VISIBLE);
                            tvPostDate.setText(Util.getCurrentdate(date));
                            isDateSelected = true;
                            CustomLog.d("date1", Util.getCurrentdate(date));
                            //  }
                        }

                        @Override
                        public void onDateTimeCancel() {
                            // Overriding onDateTimeCancel() is optional.
                        }
                    })
                    .setInitialDate(new Date())
                    .setMinDate(new Date())
                    .setIndicatorColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                    .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                    .build();

            try {
                picker.show();
            } catch (Exception e) {
                CustomLog.e(e);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static final int CAMERA_IMAGE_REQ_CODE = 185;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
        try {
            switch (resultCode) {
                case RESULT_OK:
                    try {
                        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                            place = Autocomplete.getPlaceFromIntent(data);
                            CustomLog.d("onActivityResult", "Place: " + place.getName());
                            vo = new LocationActivity();
                            vo.setLat("" + place.getLatLng().latitude);
                            vo.setLng("" + place.getLatLng().longitude);
                            vo.setVenue(place.getAddress());
                            activity.activity.setLocationActivity(vo);
                            //  JSONArray arr = new JSONArray();
                            JSONObject checkInArray = new JSONObject();
                            checkInArray.put("label", place.getAddress());
                            checkInArray.put("latitude", place.getLatLng().latitude);
                            checkInArray.put("longitude", place.getLatLng().longitude);
                            isCheckedIn = true;
                            refreshText();
                        } else if (requestCode == LOCATION_AUTOCOMPLETE_REQUEST_CODE) {
                            place = Autocomplete.getPlaceFromIntent(data);
                            tvLocation.setText(place.getName());
                        } else if (requestCode == CAMERA_IMAGE_REQ_CODE) {
                            List<String> photoPaths2 = new ArrayList<>();
                            photoPaths2.add(data.getData().getPath());
                            if (photoPaths2.size() > 0) {
                                if (photoPaths2.get(0).endsWith(".mp4")) {
                                    Constant.videoUri = Uri.fromFile(new File(photoPaths2.get(0)));
                                    onResponseSuccess(REQ_CODE_VIDEO, photoPaths2);
                                } else {
                                    onResponseSuccess(REQ_CODE_IMAGE, photoPaths2);
                                }
                            }
                        } else if (requestCode == GALLERY_IMAGE_REQ_CODE) {
                            List<String> photoPaths2 = new ArrayList<>();
                            photoPaths2.add(data.getData().getPath());
                            if (photoPaths2.size() > 0) {
                                if (photoPaths2.get(0).endsWith(".mp4")) {
                                    Constant.videoUri = Uri.fromFile(new File(photoPaths2.get(0)));
                                    onResponseSuccess(REQ_CODE_VIDEO, photoPaths2);
                                } else {
                                    onResponseSuccess(REQ_CODE_IMAGE, photoPaths2);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case AutocompleteActivity.RESULT_ERROR:
                    Status status = Autocomplete.getStatusFromIntent(data);
                    CustomLog.e("onActivityResult", status.getStatusMessage());
                    break;
                case RESULT_CANCELED:
                    // The user canceled the operation.
                    break;
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private void callPostSubmitApi(Map<String, Object> params) {
        if (isNetworkAvailable(context)) {
            try {
                //showBaseLoader(true);
                HttpRequestVO request = new HttpRequestVO(Constant.URL_POST_FEED);

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.putAll(params);

                if (resId > 0) {
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resType);
                    request.params.put(Constant.KEY_RESOURCE_ID, resId);
                }

                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = msg -> {
                    hideBaseLoader();
                    try {
                        String response = (String) msg.obj;
                        if (null != response) {
                            CustomLog.e("response_createPost_PostFeedClass", "" + response);
                            SesResponse resp = new Gson().fromJson(response, SesResponse.class);
                            if (TextUtils.isEmpty(resp.getError())) {

                             /*   if (videoDetail != null && videoDetail.isFromDevice()) {
                                    Constant.TASK_POST = false;
                                    //  Util.showToast(context, "Upload in progress");
                                     showNotification();
                                } else {
                                    Constant.TASK_POST = true;
                                }*/

                                Constant.TASK_POST = true;
                                //LaporIrigasiFragment.super.onBackPressed();
                                onBackPressed();

                            } else {
                                Util.showToast(context, resp.getErrorMessage());
                            }
                        }

                    } catch (Exception e) {
                        somethingWrongMsg(v);
                        CustomLog.e(e);
                    }
                    return true;
                };
                new HttpImageNotificationRequest(activity, new Handler(callback), true).run(request);

            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
        }

    }

    private static final int NOTIFICATION_ID = 2135;
    private static final int PROGRESS_MAX = 100;
    private NotificationCompat.Builder mBuilder;
    private NotificationManagerCompat notificationManager;

    private void showNotification() {
       /* notificationManager = NotificationManagerCompat.from(context);
        String channelName = BuildConfig.APP_NAME.replace(" ", "");
        String channelId = BuildConfig.APP_NAME.replace(" ", "");
        mBuilder = new NotificationCompat.Builder(context, channelId);
        mBuilder.setContentTitle("Updating Status")
                .setContentText("Upload in progress")
                .setSmallIcon(R.mipmap.ic_launcher2)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Issue the initial notification with zero progress
        mBuilder.setProgress(PROGRESS_MAX, 0, false);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());*/
        NotificationChannel mChannel;
        String title = "Updating Status";
        String messageBody = "Upload in progress";
        Intent intent = new Intent(context, MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 8521 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notifManager = (NotificationManager) context.getSystemService
                    (Context.NOTIFICATION_SERVICE);
            mChannel = new NotificationChannel
                    ("0", title, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription(messageBody);
            mChannel.enableVibration(true);
            notifManager.createNotificationChannel(mChannel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "0");
            builder.setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher) // required
                    .setContentText(messageBody)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setGroupSummary(true)
                    .setSound(defaultSoundUri);
            Notification notification = builder.build();
            notifManager.notify(NOTIFICATION_ID, notification);
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSound(defaultSoundUri)
                    .setSmallIcon(getNotificationIcon())
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(messageBody));

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }


    private void sendPost() {
        try {
            closeKeyboard();
            if (!checkIfAnythingChanged()) {
                Util.showSnackbar(v, getString(R.string.MSG_EMPTY_POST));
                return;
            }

            Activity feedVo = new Activity();
            feedVo.setActionId(1);
            feedVo.setType("");
            feedVo.setCan_comment(true);
            feedVo.setDate(Util.getCurrentdate("dd-MM-yyyy HH:mm:ss"));
            feedVo.setItemUser(new Item_user(1, composerOption.getResult().getUser_title(), composerOption.getResult().getUser_image()));

            Map<String, Object> params = new HashMap<>();
            String body = etBody().getText().toString().trim();
            String hastag = hastagTitle.replace(" ", "");
            if (!TextUtils.isEmpty(body)) {
                feedVo.setBody(body);
                //check if user mentioned someone,
                // If yes then replace name with "@_user_id"
                if (friendList.size() > 0) {
                    //  Collections.reverse(friendList);
                    for (Friends vo : friendList) {
                        body = body.substring(0, vo.getStartIndex()) + "@_user_" + vo.getId() + " " + body.substring(vo.getEndIndex());
                    }
                    CustomLog.e("body", body);
                }

                params.put("body", body + "\n#" + hastag);


            }

            if (isStickerSelected) {
                Emotion vo = (activity).getEmotion();
                if (vo != null) {
                    params.put("reaction_id", vo.getFileId());
                }
            }

            if (isCheckedIn) {
                params.put("checkin_loc[label]", vo.getVenue());
                params.put("checkin_loc[latitude]", vo.getLat());
                params.put("checkin_loc[longitude]", vo.getLng());
            }
            //if(isTaggedUsers
            List<Tagged> tagList = (activity).activity.getTagged();
            if (tagList != null) {
                String taggedData = "";
                for (Tagged tag : tagList) {
                    taggedData = taggedData + "," + tag.getUserId();
                }
                params.put("taggedData", taggedData.substring(1));
            }
            if (isDateSelected) {
                params.put("scheduled_post", tvPostDate.getText().toString());
            }

            Attachment attachment = null;
            if (isLinkSelected) {
                attachment = new Attachment();
                params.put("attachment[description]", linkDetail.getDescription());
                params.put("attachment[thumb]", linkDetail.getImages());
                params.put("attachment[title]", linkDetail.getTitle());
                if (!linkDetail.getUri().startsWith("http")) {
                    String link = "http://" + linkDetail.getUri();
                    params.put("attachment[uri]", link);
                } else {
                    params.put("attachment[uri]", linkDetail.getUri());
                }
                params.put("attachment[type]", "sesadvancedactivitylink");

            }
            if (isVideoSelected) {
                if (videoDetail.isFromDevice()) {
                    params.put(Constant.FILE_TYPE + "videoupload", videoDetail.getSrc());
                    params.put("not_merge_video", "1");
                } else {
                    attachment = new Attachment();
                    params.put("attachment[description]", videoDetail.getDescription());
                    params.put("attachment[photo_id]", videoDetail.getPhotoId());
                    params.put("attachment[title]", videoDetail.getTitle());
                    params.put("attachment[video_id]", videoDetail.getVideoId());

                    if (resType != null && resType.equalsIgnoreCase("sesgroup_group")) {
                        params.put("attachment[type]", Constant.TYPE_VIDEO_GROUP);
                        params.put("parent_id", "" + resId);
                    } else if (resType != null && resType.equalsIgnoreCase("businesses")) {
                        params.put("attachment[type]", Constant.TYPE_VIDEO_BUSINESS);
                        params.put("parent_id", "" + resId);
                    } else {
                        params.put("attachment[type]", Constant.TYPE_VIDEO);
                    }
                }
            }

            if (isBuySellSelected) {
                attachment = new Attachment();
                feedVo.setType(Constant.ACTIVITY_TYPE_BUY_SELL);
                //JSONObject json = new JSONObject();
                String sellTitle = etSelling.getText().toString();
                if (!TextUtils.isEmpty(sellTitle))
                    params.put("buysell-title", sellTitle);
                String sellPrice = etPrice.getText().toString();
                if (!TextUtils.isEmpty(sellPrice))
                    params.put("buysell-price", sellPrice);
                String sellDescription = etDescription.getText().toString();
                if (!TextUtils.isEmpty(sellDescription))
                    params.put("buysell-description", sellDescription);
                params.put("buysell-location", Objects.requireNonNull(place.getAddress()));
                params.put("activitybuyselllng", place.getLatLng().longitude);
                params.put("activitybuyselllat", place.getLatLng().latitude);
                params.put("attachment[type]", "buysell");
            }
            if (isFeelingSelected) {
                Feeling feelVo = (activity).getFeelings();
                params.put("feelings[feeling_id]", feelVo.getFeeling_id() > 0 ? feelVo.getFeeling_id() : 1);
                if (!TextUtils.isEmpty(feelVo.getResourceType()))
                    params.put("feelings[resource_type]", feelVo.getResourceType());
                params.put("feelings[feelingicon_id]", feelVo.getFeelingiconId());
            }


            if (isImageSelected) {
                int i = 0;
                attachment = new Attachment();
                attachment.setTotalImagesCount(imageList.size());
                attachment.setAttachmentType("album_photo");
                List<Images> listImages = new ArrayList<>();
                for (String s : imageList) {
                    listImages.add(new Images().setMainImage(s));
                    params.put(Constant.FILE_TYPE + "attachmentImage[" + i + "]", s);
                    i = i + 1;
                }
                attachment.setImages(listImages);
            }

            if (isMusicSelected) {
                params.put(Constant.FILE_TYPE + Constant.KEY_MUSIC_SONG, musicLIst.get(0));
            }

            if (isGifSelected) {
                params.put("image_id", "" + BaseActivity.gifimageurl);
            }


            if (previousBgPosition > 0 && !isStickerSelected) {
                params.put("feedbg_id", bgList.get(previousBgPosition).getBgId());
            }

            if (null != postingType) {
                params.put("postingType", postingType);
            }


            params.put("privacy", selectedPrivacy.getName());
            params.put("longitude", longtitude);
            params.put("latitude", latitdue);

            feedVo.setPrivacy(selectedPrivacy.getName());

            feedVo.setAttachment(attachment);

//            CustomLog.d("hasilnyaa",attachment.getImages().toString());
            if (attachment.getImages().isEmpty()) {

                Util.showToast(context, "Anda Belum Memilih Foto");

            } else {
                callPostSubmitApi(params);
            }

            new Thread(() ->
            {
                SesDB.daoInstance(context).updateFeed(feedVo);
                //SesDB.daoInstance(context).saveFeed(feedVo);
            }).run();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }




  /*  @Override
    public void onDestroy() {
        super.onDestroy();
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
    }

    private ViewGroup rootLayout;
    private boolean keyboardListenersAttached = false;

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            int contentViewTop = getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
            Log.e("diifress",""+heightDiff);
            Log.e("contentViewTop",""+contentViewTop);

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());

            if(heightDiff <= contentViewTop){
              //  onHideKeyboard();
                cvHorizontal.setVisibility(View.VISIBLE);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
               // onShowKeyboard(keyboardHeight);
                cvHorizontal.setVisibility(View.GONE);
            }
        }
    };*/

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) v.findViewById(R.id.sliding_layout)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }


    @Override
    public void onVisibilityChanged(boolean visible) {
        // Toast.makeText(getActivity(), visible ? "Keyboard is active" : "Keyboard is Inactive", Toast.LENGTH_SHORT).show();
        if (visible) {
            cvHorizontal.setVisibility(View.GONE);
            v.findViewById(R.id.rvAttachOption).setVisibility(View.GONE);
        } else {
            cvHorizontal.setVisibility(View.VISIBLE);
            v.findViewById(R.id.rvAttachOption).setVisibility(View.VISIBLE);
            // rvAttachOption.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
//                Intent intent = new Intent();
//                intent.putExtra("Longitude", mLastLocation.getLongitude());
//                intent.putExtra("Latitude", mLastLocation.getLatitude());
//                setResult(1,intent);
//                finish();


                longtitude = mLastLocation.getLongitude();
                latitdue = mLastLocation.getLatitude();
                //   CustomLog.d("hasilnyaa33",String.valueOf(loca.getLongitude()) + " haadahah");
                ////   CustomLog.d("hasilnyaa", String.valueOf(location.getLongitude()) + "   asdakndkasn");
//                CustomLog.d("hasilnyaa", String.valueOf(mLastLocation.getLatitude()) + " haadahah");
//
//                CustomLog.d("hasilnyaa22", String.valueOf(mLastLocation.getLongitude()) + " haadahah");

            }
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void showDialogCheckBox() {
        // Set up the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Pilih Kategori");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        hastagTitle = "Infrastruktur Irigasi";

                        // Toast.makeText(MainActivity.this, "First Item Clicked", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        hastagTitle = "Kondisi Air Irigasi";
                        // Toast.makeText(MainActivity.this, "Second Item Clicked", Toast.LENGTH_LONG).show();
                        break;

                }

                tvKategori.setText("Kategori : " + hastagTitle);
                tvKategori.setVisibility(View.VISIBLE);

                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
//        String[] kategori = {"Infra Struktur Irigasi", "Kondisi Air Irigasi",};
//        boolean[] checkedItems = new boolean[] {false};
//        builder.setMultiChoiceItems(kategori, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                checkedItems[which] = isChecked;
//
//            }
//        });
//
//
//        builder.setPositiveButton("Pilih", (dialog, which) -> {
//
//            dialog.dismiss();
//
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
    }

}
