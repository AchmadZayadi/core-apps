package com.sesolutions.ui.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.animate.DetailsTransition;
import com.sesolutions.animate.Techniques;
import com.sesolutions.animate.YoYo;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.Notifications;
import com.sesolutions.responses.contest.Packages;
import com.sesolutions.responses.feed.Item_user;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.music.Artist;
import com.sesolutions.responses.qna.Question;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.responses.videos.Tags;
import com.sesolutions.responses.videos.ViewVideo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.albums.SearchAlbumFragment;
import com.sesolutions.ui.albums.ViewAlbumFragment;
import com.sesolutions.ui.albums_core.C_ViewAlbumFragment;
import com.sesolutions.ui.blogs.SearchBlogFragment;
import com.sesolutions.ui.blogs.ViewBlogFragment;
import com.sesolutions.ui.blogs_core.C_ViewBlogFragment;
import com.sesolutions.ui.bookings.ViewProfessionalFragment;
import com.sesolutions.ui.bookings.ViewServiceFragment;
import com.sesolutions.ui.business.BusinessCategoryViewFragment;
import com.sesolutions.ui.business.CreateEditBusinessFragment;
import com.sesolutions.ui.business.ViewBusinessFragment;
import com.sesolutions.ui.choose_album.SelectAlbumFragment;
import com.sesolutions.ui.classified.ViewClassifiedFragment;
import com.sesolutions.ui.comment.CommentFragment;
import com.sesolutions.ui.comment.ReactionViewFragment;
import com.sesolutions.ui.contest.CreateEditContestFragment;
import com.sesolutions.ui.contest.ViewContestFragment;
import com.sesolutions.ui.contest.ViewEntryFragment;
import com.sesolutions.ui.courses.classroom.CreateEditClassroomFragment;
import com.sesolutions.ui.courses.classroom.ViewClassroomFragment;
import com.sesolutions.ui.courses.course.CreateEditCourseFragment;
import com.sesolutions.ui.courses.course.ViewCourseFragment;
import com.sesolutions.ui.courses.lecture.CreateLectureFragment;
import com.sesolutions.ui.courses.lecture.ViewLectureFragment;
import com.sesolutions.ui.courses.lecture.ViewTextLectureFragment;
import com.sesolutions.ui.courses.test.GiveTestFragment;
import com.sesolutions.ui.courses.test.TestResultPage;
import com.sesolutions.ui.courses.test.ViewTestPage;
import com.sesolutions.ui.customviews.AnimationAdapter;
import com.sesolutions.ui.customviews.ExampleCardPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.ui.dashboard.MainActivity;
import com.sesolutions.ui.dashboard.PostFeedFragment;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.dashboard.ShareSEFragment;
import com.sesolutions.ui.dashboard.TnCFragment;
import com.sesolutions.ui.dashboard.composervo.ComposerOption;
import com.sesolutions.ui.event_core.ViewCEventFragment;
import com.sesolutions.ui.events.DiscussionViewFragment;
import com.sesolutions.ui.events.ViewEventCategoryFragment;
import com.sesolutions.ui.events.ViewEventFragment;
import com.sesolutions.ui.group_core.ViewCGroupFragment;
import com.sesolutions.ui.groups.CreateEditGroupFragment;
import com.sesolutions.ui.groups.GroupCategoryViewFragment;
import com.sesolutions.ui.groups.GroupFragment;
import com.sesolutions.ui.groups.GroupJoinFragment;
import com.sesolutions.ui.groups.ViewGroupFragment;
import com.sesolutions.ui.live.LiveVideoActivity;
import com.sesolutions.ui.member.MapMamberFragment;
import com.sesolutions.ui.member.MemberFragment;
import com.sesolutions.ui.message.MessageActivity;
import com.sesolutions.ui.music_album.AlbumImageFragment;
import com.sesolutions.ui.music_album.AlbumImageFragment2;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.ui.music_album.SearchMusicAlbumFragment;
import com.sesolutions.ui.music_album.ViewArtistFragment;
import com.sesolutions.ui.music_album.ViewMusicAlbumFragment;
import com.sesolutions.ui.music_album.ViewSongFragment;
import com.sesolutions.ui.packages.PackageFragment;
import com.sesolutions.ui.page.CreateEditPageFragment;
import com.sesolutions.ui.page.PageCategoryViewFragment;
import com.sesolutions.ui.page.PageReviewViewFragment;
import com.sesolutions.ui.page.SelectCategoriesFragment;
import com.sesolutions.ui.page.ViewPageFragment;
import com.sesolutions.ui.photo.GallaryFragment;
import com.sesolutions.ui.photo.SinglePhotoFragment;
import com.sesolutions.ui.poll.PollViewFragment;
import com.sesolutions.ui.poll_core.CViewPollFragment;
import com.sesolutions.ui.prayer.ViewPrayerCategoryFragment;
import com.sesolutions.ui.prayer.ViewPrayerFragment;
import com.sesolutions.ui.profile.InfoFragment;
import com.sesolutions.ui.profile.ViewProfileFragment;
import com.sesolutions.ui.qna.CreateEditQAFragment;
import com.sesolutions.ui.qna.QACategoryViewFragment;
import com.sesolutions.ui.qna.ViewQuestionFragment;
import com.sesolutions.ui.quotes.ViewPhotoQuoteFragment;
import com.sesolutions.ui.quotes.ViewQuoteCategoryFragment;
import com.sesolutions.ui.resume.MyPreviewList;
import com.sesolutions.ui.resume.ResumeParentFragment;
import com.sesolutions.ui.resume.resumedashordmodel;
import com.sesolutions.ui.settings.GeneralSettingFragment;
import com.sesolutions.ui.signup.OTPFragment;
import com.sesolutions.ui.signup.ProfileImageFragment;
import com.sesolutions.ui.signup.SignUpFragment;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.ui.store.CreateEditStoreFragment;
import com.sesolutions.ui.thought.ViewThoughtCategoryFragment;
import com.sesolutions.ui.thought.ViewThoughtFragment;
import com.sesolutions.ui.video.SearchVideoFragment;
import com.sesolutions.ui.video.VideoViewActivity;
import com.sesolutions.ui.video.VideoViewActivity2;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.ui.welcome.WelcomeActivity;
import com.sesolutions.ui.wish.ViewPhotoWishFragment;
import com.sesolutions.ui.wish.ViewWishCategoryFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomClickableSpan;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;
import com.sesolutions.utils.VibratorUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.apache.http.client.methods.HttpPost;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class BaseFragment extends Fragment {
    public Context context;
    public BaseActivity activity;
    public FragmentManager fragmentManager;
    public ProgressDialog progressDialog;
    public BottomSheetDialog bottomSheetDialog;
    //variable used for recyclerview animation for the first time
    public boolean wasListEmpty;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
           /* Tracker mTracker = ((MainApplication) activity.getApplication()).getDefaultTracker();
            mTracker.setScreenName(("" + getClass().getSimpleName()).replace("Fragment", ""));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());*/

            Bundle bundle = new Bundle();
            // bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, getClass().getSimpleName());
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "screen");
            ((MainApplication) activity.getApplication()).getDefaultTracker().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // postponeEnterTransition();
            // setEnterTransition(new AutoTransition());
            //setExitTransition(new AutoTransition());
            setEnterTransition(new Fade(Fade.IN));
            setReenterTransition(null);
            setExitTransition(new Fade(Fade.OUT));
            //  setEnterTransition(new AutoTransition());
            //  setExitTransition(new Explode());
            setSharedElementEnterTransition(new DetailsTransition());
            setSharedElementReturnTransition(new DetailsTransition());
            //  setAllowEnterTransitionOverlap(false);
            //  setAllowReturnTransitionOverlap(false);
        }
    }

    public void startAnimation(final View cv, final int animateType, int duration) {
        try {
            Techniques technique = Techniques.values()[animateType];
            YoYo.with(technique)
                    .duration(duration)
                    //  .repeat(1)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    // .withListener(listener)
                    .playOn(cv);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void startAnimation(final View cv, final int animateType, int duration, AnimationAdapter listener) {
        try {
            Techniques technique = Techniques.values()[animateType];
            YoYo.with(technique)
                    .duration(duration)
                    //  .repeat(1)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .withListener(listener)
                    .playOn(cv);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void goToReportFragment(String guid) {
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid, true)).addToBackStack(null).commit();
    }

    public void goToReport(String guid) {
        Intent intent = new Intent(activity, CommonActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_EDIT_SHARE_SE);
        intent.putExtra(Constant.KEY_TITLE, guid);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            if (!SPref.getInstance().isLoggedIn(context)) {
                MenuItem item = menu.findItem(R.id.option);
                if (null != item) item.setVisible(false);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setRoundedFilledDrawable(View view) {
        /*try {
            GradientDrawable shape2 = new GradientDrawable();
            shape2.setShape(GradientDrawable.RECTANGLE);
            int x = 12;
            shape2.setCornerRadii(new float[]{x, x, x, x, x, x, x, x});
            shape2.setColor(Util.manipulateColor(Color.parseColor(Constant.colorPrimary)));
            //shape2.setStroke(2, cPrimary);
            view.setBackground(shape2);
        } catch (Exception e) {
            CustomLog.e(e);
        }*/
    }

    public void setRoundedHoloDrawable(View view) {
        try {
            GradientDrawable shape2 = new GradientDrawable();
            shape2.setShape(GradientDrawable.RECTANGLE);
            int x = 12;
            shape2.setCornerRadii(new float[]{x, x, x, x, x, x, x, x});
            //shape2.setColor(Util.manipulateColor(Color.parseColor(Constant.colorPrimary)));
            shape2.setStroke(2, Color.parseColor(Constant.text_color_2));
            view.setBackground(shape2);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void setRefreshing(SwipeRefreshLayout swipeRefreshLayout, boolean isRefreshing) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(isRefreshing);
        }
    }

    public void goToViewAlbumFragment(int albumId, boolean uploadPhotos) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewAlbumFragment.newInstance(uploadPhotos, albumId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewAlbumBasicFragment(int albumId, boolean uploadPhotos) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , C_ViewAlbumFragment.newInstance(uploadPhotos, albumId))
                .addToBackStack(null)
                .commit();
    }


    public void goToViewAlbumFragment2(int albumId, boolean uploadPhotos) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , C_ViewAlbumFragment.newInstance(uploadPhotos, albumId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewGroupFragment(int groupId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewGroupFragment.newInstance(groupId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewCGroupFragment(int groupId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewCGroupFragment.newInstance(groupId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewClassifiedFragment(int classifiedId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewClassifiedFragment.newInstance(classifiedId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewQuoteFragment(int quoteId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewPhotoQuoteFragment.newInstance(quoteId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewWishFragment(int quoteId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewPhotoWishFragment.newInstance(quoteId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewEventFragment(int quoteId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewEventFragment.newInstance(quoteId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewCEventFragment(int id) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewCEventFragment.newInstance(id))
                .addToBackStack(null)
                .commit();
    }


    public void goToViewContestFragment(int id) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewContestFragment.newInstance(id))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewEntryFragment(int id) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewEntryFragment.newInstance(id))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewPrayerFragment(int quoteId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewPrayerFragment.newInstance(quoteId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewThoughtFragment(int quoteId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewThoughtFragment.newInstance(quoteId))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewReviewFragment(String resourceType, int reviewId) {
        fragmentManager.beginTransaction().replace(R.id.container, PageReviewViewFragment.newInstance(resourceType, reviewId)).addToBackStack(null).commit();
    }


 /*   public void goToViewQuoteCategoryFragment(int categoryId, String catName) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewQuoteCategoryFragment.newInstance(categoryId, catName))
                .addToBackStack(null)
                .commit();
    }*/

    public void askForPermission(PermissionListener permissionlistener, String... permission) {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(getStrings(R.string.MSG_PERMISSION_DENIED))
                    .setPermissions(permission)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void createPopUp(View v, int position, OnUserClickedListener listener) {
        try {
            ExampleCardPopup popup = new ExampleCardPopup(v.getContext(), position, listener);
            // popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            int vertPos = RelativePopupWindow.VerticalPosition.ABOVE;
            int horizPos = RelativePopupWindow.HorizontalPosition.CENTER;
            popup.showOnAnchor(v, vertPos, horizPos, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void applyTheme(View v) {
        if (v != null) {
            new ThemeManager().applyTheme((ViewGroup) v, context);
        }
    }

    public void openComposeActivity(List<Item_user> userList, String subject) {
        Intent intent = new Intent(activity, MessageActivity.class);
        if (null != userList) {
            intent.putExtra(Constant.KEY_DATA, (Serializable) userList);
            intent.putExtra(Constant.KEY_SUBJECT, subject);
            /*intent.putExtra(Constant.KEY_ID, user.getUser_id());
            intent.putExtra(Constant.KEY_IMAGE, user.getUser_image());*/
        }
        startActivity(intent);
    }


    public void openComposeActivity(List<Item_user> userList) {
        openComposeActivity(userList, null);
    }

    public void openOtpFragment(int previousScreen, String email, String password) {
        fragmentManager.beginTransaction().replace(R.id.container, OTPFragment.newInstance(previousScreen, email, password)).addToBackStack(null).commit();
    }

    public void openOtpFragment(int previousScreen, Map<String, Object> map, String response) {
        fragmentManager.beginTransaction().replace(R.id.container, OTPFragment.newInstance(previousScreen, map, response)).addToBackStack(null).commit();
    }

    public void goToGalleryFragment(int photoId, String resourceType, String url) {
        goToGalleryFragment(Constant.ACTIVITY_TYPE_ALBUM, 0, photoId, resourceType, url);
    }

    public void goToComposeMessageFragment() {
        context.startActivity(new Intent(activity, MessageActivity.class));
    }

    public void goToComposeMessageFragment(Share vo) {
        Intent intent = new Intent(activity, MessageActivity.class);
        try {
            if (vo.getUrl() != null && vo.getUrl().length() > 0 && !vo.getUrl().equalsIgnoreCase("null")) {
                intent.putExtra("DISCRIPTONTAG", "" + vo.getUrl());
            } else {
                intent.putExtra("DISCRIPTONTAG", "" + vo.getDescription());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            intent.putExtra("DISCRIPTONTAG", "" + vo.getDescription());
        }
        context.startActivity(intent);
    }

    public void goToComposeMessageFragment(Share vo, String sharemsg) {
        Intent intent = new Intent(activity, MessageActivity.class);
        try {
            if (sharemsg.length() > 0) {
                intent.putExtra("DISCRIPTONTAG", "" + sharemsg);
            } else {
                intent.putExtra("DISCRIPTONTAG", "" + vo.getDescription());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            intent.putExtra("DISCRIPTONTAG", "" + vo.getDescription());
        }
        context.startActivity(intent);
    }


    public void goToGalleryFragment(String type, int albumId, int photoId, String resourceType, String url) {

        if (activity instanceof MainActivity) {
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.GALLARY);
            intent.putExtra(Constant.KEY_ID, photoId);
            intent.putExtra(Constant.KEY_ALBUM_ID, albumId);
            intent.putExtra(Constant.KEY_TYPE, type);
            intent.putExtra(Constant.KEY_IMAGE, url);
            intent.putExtra(Constant.KEY_RESOURCES_TYPE, resourceType);
            startActivity(intent);
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_PHOTO_ID, photoId);
            map.put(Constant.KEY_ALBUM_ID, albumId);
            map.put(Constant.KEY_TYPE, type);
            map.put(Constant.KEY_IMAGE, url);
            map.put(Constant.KEY_RESOURCES_TYPE, resourceType);

            fragmentManager.beginTransaction()
                    .replace(R.id.container
                            , GallaryFragment.newInstance(map))
                    .addToBackStack(null)
                    .commit();
        }
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public String getCookie() {
        return TextUtils.isEmpty(Constant.SESSION_ID) ? SPref.getInstance().getCookie(context) : Constant.SESSION_ID;
    }

    public void goToUploadAlbumImage(String url, String main, String title, Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, AlbumImageFragment.newInstance(title, url, main, map))
                .addToBackStack(null)
                .commit();
    }


    public void goToUploadAlbumImage2(String url, String main, String title, Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, AlbumImageFragment2.newInstance(title, url, main, map))
                .addToBackStack(null)
                .commit();
    }


    public void openSelectAlbumFragment(String uploadUrl, Map<String, Object> map) {
        fragmentManager.beginTransaction().replace(R.id.container, SelectAlbumFragment.newInstance(uploadUrl, map)).addToBackStack(null).commit();
    }

    public void updateNotificationCount(int pos) {
        try {
            if (activity instanceof MainActivity && ((MainActivity) activity).dashboardFragment.unreadCount[pos] != 0) {
                ((MainActivity) activity).dashboardFragment.unreadCount[pos] = 0;
                ((MainActivity) activity).dashboardFragment.updateTabBadgeCount(pos);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void openUserProfileEditForm() {

        Intent intent = new Intent(activity, CommonActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.EDIT_USER);
        //  intent.putExtra(Constant.KEY_TYPE, Constant.FormType.EDIT_USER);
        // intent.putExtra(Constant.KEY_TITLE, (Serializable) composerOption);
        startActivity(intent);

       /* if (activity instanceof MainActivity) {
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.EDIT_USER);
            //  intent.putExtra(Constant.KEY_TYPE, Constant.FormType.EDIT_USER);
            // intent.putExtra(Constant.KEY_TITLE, (Serializable) composerOption);
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.EDIT_USER, new HashMap<String, Object>(), Constant.URL_EDIT_PROFILE)).addToBackStack(null).commit();
        }*/
    }


    public void performClick(String type, int id, String href, boolean openComment, int activityId) {

        try {
            switch (type) {
                case Constant.ResourceType.LIVE_STREAMING:
                    Intent intent = new Intent(activity, LiveVideoActivity.class);
                    intent.putExtra(Constant.KEY_HOST_ID, id);
                    intent.putExtra(Constant.KEY_ACTIVITY_ID, activityId);
                    startActivity(intent);
                    break;
                default:
                    int MODULE = ModuleUtil.getInstance().fetchDestination(type);
                    if (-1 < MODULE) {
                        goTo(MODULE, Constant.KEY_ID, id, openComment);
                    } else if (!TextUtils.isEmpty(href)) {
                        openWebView(href, " ");
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void performClick(String type, int id, String href, boolean openComment) {

        try {
            switch (type) {
                case Constant.ACTIVITY_TYPE_VIDEO:
                    goTo(Constant.GoTo.VIDEO, id);
                    break;

                case Constant.ResourceType.VIDEO_PLAYLIST:
                    goTo(Constant.GoTo.VIEW_VIDEO_PLAYLIST, id);
                    break;

//                case Constant.ACITIVITY_ACTION:
                case Constant.VALUE_RESOURCES_TYPE:
                case Constant.VALUE_RESOURCES_TYPE2:
                    Intent intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_VIEW_FEED);
                    intent.putExtra(Constant.KEY_ACTION_ID, id);
                    intent.putExtra(Constant.KEY_RESOURCE_ID, id);
                    intent.putExtra(Constant.KEY_RESOURCES_TYPE, type);
                    intent.putExtra(Constant.KEY_COMMENT_ID, openComment);
                    startActivity(intent);
                    break;

                case Constant.ResourceType.PAGE_POLL:
                case Constant.ResourceType.GROUP_POLL:
                case Constant.ResourceType.BUSINESS_POLL:
                    intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.VIEW_POLL);
                    intent.putExtra(Constant.KEY_ID, id);
                    intent.putExtra(Constant.KEY_RESOURCES_TYPE, type);
                    startActivity(intent);
                    break;
                default:
                    int MODULE = ModuleUtil.getInstance().fetchDestination(type);
                    if (-1 < MODULE) {
                        goTo(MODULE, Constant.KEY_ID, id, openComment);
                    } else if (!TextUtils.isEmpty(href)) {
                        openWebView(href, " ");
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void goTo(int goTo, String key, int value, boolean openComment) {
        Intent intent = new Intent(activity, CommonActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, goTo);
        intent.putExtra(key, value);
        intent.putExtra(Constant.KEY_COMMENT_ID, openComment);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity);
        startActivity(intent, options.toBundle());

    }

    public void goTo(int goTo, int videoId) {
        Intent intent = new Intent(activity, VideoViewActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, goTo);
        intent.putExtra(Constant.KEY_ID, videoId);
        startActivity(intent);
    }

    public void goTo2(int goTo, int videoId, String resourceType, int keytrr) {
        Intent intent = new Intent(activity, VideoViewActivity2.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, goTo);
        intent.putExtra(Constant.KEY_TYPE, resourceType);
        intent.putExtra(Constant.KEY_TRANSFER, keytrr);
        intent.putExtra(Constant.KEY_ID, videoId);
        startActivity(intent);
    }


    //go to video with resource type ,so it can be used to differentiate betwenn different plugins
    // current it is used to show Page and event plugin video
    public void goTo(int goTo, int videoId, String resourceType) {
        Intent intent = new Intent(activity, VideoViewActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, goTo);
        intent.putExtra(Constant.KEY_TYPE, resourceType);
        intent.putExtra(Constant.KEY_ID, videoId);
        startActivity(intent);
    }

    public void goTo(int goTo, String key, int value) {
        goTo(goTo, key, value, false);
    }


    @SuppressLint("WrongConstant")
    public void closeDrawer() {
        ((MainActivity) activity).drawerLayout.closeDrawer(Gravity.START);
    }

    @SuppressLint("WrongConstant")
    public void openDrawer() {
        ((MainActivity) activity).drawerLayout.openDrawer(Gravity.START);
    }


    AVLoadingIndicatorView avlodingimage;

    public void showBaseLoader(boolean isCancelable) {
        try {

            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCancelable(isCancelable);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_progress);
            avlodingimage = progressDialog.findViewById(R.id.loadingimage);

            try {
                // DefaultDataVo responsede=SPref.getInstance().getDefaultInfo(context,Constant.KEY_APPDEFAULT_DATA);
                // int progresstag=Integer.parseInt(responsede.getResult().getLoadingImage());
                // List<String> Lines = Arrays.asList(getResources().getStringArray(R.array.customprogressid));
                avlodingimage.setIndicator("BallClipRotateMultipleIndicator");

            } catch (Exception e) {
                e.printStackTrace();
                avlodingimage.setIndicator("BallPulseIndicator");
            }
          /*  if(responsede.getResult().getLoadingImage()){

            }*/
            //
            avlodingimage.show();
            // new showBaseLoaderAsync(context).execute();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void hideBaseLoader() {
        try {
            if (getActivity() != null && !getActivity().isFinishing() && progressDialog != null && progressDialog.isShowing()) {
                avlodingimage.hide();
                progressDialog.dismiss();

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showCustomBaseLoader(boolean isCancelable) {
        try {
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCancelable(isCancelable);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.layout_custom_loader);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void goToGeneralSettingForm(String url, String title) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, GeneralSettingFragment.newInstance(url, title))
                .addToBackStack(null).commit();
    }

    public void goToMemberFragment(int userId, boolean showToolbar) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, MemberFragment.newInstance(userId, showToolbar))
                .addToBackStack(null)
                .commit();
    }

    public void goToSearchBlogFragment(int userId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, SearchBlogFragment.newInstance(userId))
                .addToBackStack(null)
                .commit();
    }


    public void goToSearchAlbumFragment(int userId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, SearchAlbumFragment.newInstance(userId))
                .addToBackStack(null)
                .commit();
    }


    public void goToSearchVideoFragment(int userId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, SearchVideoFragment.newInstance(userId))
                .addToBackStack(null)
                .commit();
    }


    public void goToViewCBlogFragment(int groupId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewBlogFragment.newInstance(groupId, false))
                .addToBackStack(null)
                .commit();
    }

    public void goToViewCBlogbasicFragment(int groupId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , C_ViewBlogFragment.newInstance(groupId, false))
                .addToBackStack(null)
                .commit();
    }


    public void goToSearchMusicFragment(int userId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, SearchMusicAlbumFragment.newInstance(userId))
                .addToBackStack(null)
                .commit();
    }

    public void openViewQuoteCategoryFragment(int categoryId, String catName, boolean isTag) {
        if (activity instanceof MainActivity) {
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.QUOTE_CATEGORY);
            intent.putExtra(Constant.KEY_ID, categoryId);
            intent.putExtra(Constant.KEY_TITLE, catName);
            intent.putExtra(Constant.KEY_IS_TAG, isTag);
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ViewQuoteCategoryFragment.newInstance(categoryId, catName, isTag))
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void openViewWishCategoryFragment(int categoryId, String catName, boolean isTag) {
        if (activity instanceof MainActivity) {
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.WISH_CATEGORY);
            intent.putExtra(Constant.KEY_ID, categoryId);
            intent.putExtra(Constant.KEY_TITLE, catName);
            intent.putExtra(Constant.KEY_IS_TAG, isTag);
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ViewWishCategoryFragment.newInstance(categoryId, catName, isTag))
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void openViewPageCategoryFragment(int categoryId, String catName) {

        fragmentManager.beginTransaction()
                .replace(R.id.container, PageCategoryViewFragment.newInstance(categoryId, catName))
                .addToBackStack(null)
                .commit();

    }

    public void openViewLectureFragment(int categoryId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewLectureFragment.newInstance(categoryId))
                .addToBackStack(null)
                .commit();
    }

    public void openViewTextLectureFragment(int categoryId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewTextLectureFragment.newInstance(categoryId))
                .addToBackStack(null)
                .commit();
    }

    public void openViewTestFragment(int categoryId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewTestPage.newInstance(categoryId))
                .addToBackStack(null)
                .commit();
    }

    public void openViewTestResultFragment(int categoryId, int userid) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, TestResultPage.newInstance(categoryId, userid))
                .addToBackStack(null)
                .commit();
    }

    public void openGiveTestFragment(int categoryId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, GiveTestFragment.newInstance(categoryId))
                .addToBackStack(null)
                .commit();
    }

    public void openViewQACategoryFragment(int categoryId, String catName) {

        fragmentManager.beginTransaction()
                .replace(R.id.container, QACategoryViewFragment.newInstance(categoryId, catName))
                .addToBackStack(null)
                .commit();

    }

    public void openViewBusinessCategoryFragment(int categoryId, String catName) {

        fragmentManager.beginTransaction()
                .replace(R.id.container, BusinessCategoryViewFragment.newInstance(categoryId, catName))
                .addToBackStack(null)
                .commit();

    }

    public void openViewGroupCategoryFragment(int categoryId, String catName) {

        fragmentManager.beginTransaction()
                .replace(R.id.container, GroupCategoryViewFragment.newInstance(categoryId, catName))
                .addToBackStack(null)
                .commit();

    }

    public void openViewCategoryFragment(BaseFragment frag) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, frag)
                .addToBackStack(null)
                .commit();
    }

    public void openViewPrayerCategoryFragment(int categoryId, String catName, boolean isTag) {
        if (activity instanceof MainActivity) {
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.PRAYER_CATEGORY);
            intent.putExtra(Constant.KEY_ID, categoryId);
            intent.putExtra(Constant.KEY_TITLE, catName);
            intent.putExtra(Constant.KEY_IS_TAG, isTag);
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ViewPrayerCategoryFragment.newInstance(categoryId, catName, isTag, null))
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void openViewThoughtCategoryFragment(int categoryId, String catName, boolean isTag) {
        if (activity instanceof MainActivity) {
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.THOUGHT_CATEGORY);
            intent.putExtra(Constant.KEY_ID, categoryId);
            intent.putExtra(Constant.KEY_TITLE, catName);
            intent.putExtra(Constant.KEY_IS_TAG, isTag);
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ViewThoughtCategoryFragment.newInstance(categoryId, catName, isTag))
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void goToPostFeed(ComposerOption composerOption, int selectedOption, int resourceId, String resourceType) {


        if (activity instanceof MainActivity) {
            String compString = "{}";
            if (null != composerOption) {
                compString = new Gson().toJson(composerOption);
            }
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_POST_FEED);
            intent.putExtra(Constant.KEY_NAME, selectedOption);
            intent.putExtra(Constant.KEY_TITLE, compString);
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, PostFeedFragment.newInstance(composerOption, selectedOption, resourceId, resourceType)).addToBackStack(null).commit();
        }
    }

    public void openPageCreateForm(Dummy.Result result, Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditPageFragment.newInstance(Constant.FormType.CREATE_PAGE, map, Constant.URL_PAGE_CREATE, result, true))
                .addToBackStack(null)
                .commit();
    }

    public void openStoreCreateForm(Dummy.Result result, Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditStoreFragment.newInstance(Constant.FormType.CREATE_STORE, map, Constant.URL_STORE_CREATE, result))
                .addToBackStack(null)
                .commit();
    }

    public void openClassroomCreateForm(Dummy.Result result, Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditClassroomFragment.newInstance(Constant.FormType.CREATE_CLASSROOM, map, Constant.URL_CREATE_CLASSROOM, result))
                .addToBackStack(null)
                .commit();
    }

    public void openCreateLectureFragment(int courseId, Dummy.Result result, Map<String, Object> map) {
        activity.filteredMap = null;
        map.put(Constant.KEY_COURSE_ID, courseId);
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateLectureFragment.newInstance(courseId, Constant.FormType.CREATE_LECTURE, map, Constant.URL_CREATE_LECTURE, null))
                .addToBackStack(null)
                .commit();
    }

    public void gotoResumeBuilder(int resumeid, String title, List<resumedashordmodel.ResultBean.DashboardoptionsBean> tabslist) {
        fragmentManager.beginTransaction().replace(R.id.container, ResumeParentFragment.newInstance(resumeid, title, tabslist)).addToBackStack(null).commit();
    }

    public void gotoPreviewBuilder(int resumeid, String title) {
        fragmentManager.beginTransaction().replace(R.id.container, MyPreviewList.newInstance(resumeid, title)).addToBackStack(null).commit();
    }


    public void openCourseCreateForm(Dummy.Result result, Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditCourseFragment.newInstance(Constant.FormType.CREATE_COURSE, map, Constant.URL_CREATE_COURSE, result))
                .addToBackStack(null)
                .commit();
    }

    public void openQACreateForm(Dummy.Result result, Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditQAFragment.newInstance(Constant.FormType.CREATE_QA, map, Constant.URL_QA_CREATE, result))
                .addToBackStack(null)
                .commit();
    }

    public void openBusinessCreateForm(Dummy.Result result, Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditBusinessFragment.newInstance(Constant.FormType.CREATE_BUSINESS, map, Constant.URL_BUSINESS_CREATE, result))
                .addToBackStack(null)
                .commit();
    }

    public void openGroupCreateForm(Dummy.Result result, Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditGroupFragment.newInstance(Constant.FormType.CREATE_GROUP, map, Constant.URL_GROUP_CREATE, result))
                .addToBackStack(null)
                .commit();
    }

    public void openGroupJoinForm(Dummy.Result result, Map<String, Object> map, String url, String title, String desc) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        GroupJoinFragment.newInstance(Constant.FormType.JOIN_GROUP, map, url, result, title, desc))
                .addToBackStack(null)
                .commit();
    }

    public void openContestCreateForm(Dummy.Result result, Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditContestFragment.newInstance(Constant.FormType.CREATE_CONTEST, map, Constant.URL_CONTEST_CREATE, result))
                .addToBackStack(null)
                .commit();
    }

    public void openSelectCategory(List<Category> category, Map<String, Object> map, String rcType) {
        fragmentManager.beginTransaction().replace(R.id.container,
                SelectCategoriesFragment.newInstance(category, map, rcType))
                .addToBackStack(null)
                .commit();
    }

    public void openSelectPackage(List<Packages> packages, List<Packages> myPackages, Map<String, Object> map, String rcType) {
        fragmentManager.beginTransaction().replace(R.id.container,
                PackageFragment.newInstance(packages, myPackages, map, rcType))
                .addToBackStack(null)
                .commit();
    }


    public void goToTulisSesuatu(ComposerOption composerOption, int selectedOption) {
        if (activity instanceof MainActivity) {
            String compString = "{}";
            if (null != composerOption) {
                compString = new Gson().toJson(composerOption);
            }
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_TULIS_SESUATU);
            intent.putExtra(Constant.KEY_NAME, selectedOption);
            intent.putExtra(Constant.KEY_TITLE, compString);
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, PostFeedFragment.newInstance(composerOption, selectedOption)).addToBackStack(null).commit();
        }
    }

    public void goToUnggahFoto(ComposerOption composerOption, int selectedOption) {
        if (activity instanceof MainActivity) {
            String compString = "{}";
            if (null != composerOption) {
                compString = new Gson().toJson(composerOption);
            }
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_UNGGAH_FOTO);
            intent.putExtra(Constant.KEY_NAME, selectedOption);
            intent.putExtra(Constant.KEY_TITLE, compString);
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, PostFeedFragment.newInstance(composerOption, selectedOption)).addToBackStack(null).commit();
        }
    }


    public void goToPostFeed(ComposerOption composerOption, int selectedOption) {
        if (activity instanceof MainActivity) {
            String compString = "{}";
            if (null != composerOption) {
                compString = new Gson().toJson(composerOption);
            }
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_LAPOR_IRIGASI);
            intent.putExtra(Constant.KEY_NAME, selectedOption);
            intent.putExtra(Constant.KEY_TITLE, compString);
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, PostFeedFragment.newInstance(composerOption, selectedOption)).addToBackStack(null).commit();
        }
    }



    //go back if permission denied
    public void goIfPermissionDenied(String msg) {
        try {
            if (msg != null && msg.equals(Constant.MSG_PERMISSION_ERROR)) {
                VibratorUtils.vibrate(context);
                new Handler().postDelayed(() -> {
                    if (isAdded())
                        onBackPressed();
                }, 2500);

            }
        } catch (Exception e) {
            CustomLog.e(e);
            onBackPressed();
        }
    }


    public void goToDashboard() {
        /*if user share something
         * so go to OutSideShareActivity instead of MainActivity*/
        if (Constant.isSharingFromOutside) {
            Constant.isSharingFromOutside = false;
            Intent intent = activity.getIntent();
            Intent loginIntent = new Intent(activity, OutsideShareActivity.class);
            loginIntent.putExtras(intent.getExtras());
            loginIntent.setAction(intent.getAction());
            loginIntent.setType(intent.getType());
            startActivity(loginIntent);
        } else {
            Intent intent = new Intent(activity, MainActivity.class);
            //      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
        // activity.finish();
    }

    public void goToWelcome(int screen) {
        Intent intent = new Intent(activity, WelcomeActivity.class);
        intent.putExtra(Constant.KEY_TYPE, screen);
        context.startActivity(intent);
        //  activity.finish();
    }

    public SpannableString addClickableArtist(List<Artist> artists) {
        String artist = "";
        for (Artist art : artists) {
            artist += "\n" + art.getName();
        }

        SpannableString span = new SpannableString(artist.trim());
        try {
            String s = "";
            for (int i = 0; i < artists.size(); i++) {
                int start = s.length();
                s += "\n" + artists.get(i).getName();
                int end = s.length();
                final int artistId = artists.get(i).getArtistId();
                span.setSpan(new CustomClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        goToArtistView(artistId);
                    }
                }, start, end - 1, 0);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return span;
    }

    public SpannableString addClickableTaggs(List<Friends> tags) {
        StringBuilder artist = new StringBuilder();
        for (Tags art : tags) {
            artist.append(" ").append(art.getTitle());
        }

        SpannableString span = new SpannableString(artist.toString().trim());
        try {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < tags.size(); i++) {
                int start = s.length();
                s.append("\n").append(tags.get(i).getTitle());
                int end = s.length();
                final int catId = tags.get(i).getTagId();
                final String catName = tags.get(i).getTitle();
                span.setSpan(new CustomClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        openViewQuoteCategoryFragment(catId, catName, true);
                    }
                }, start, end - 1, 0);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return span;
    }


    public void goToArtistView(int artistId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewArtistFragment.newInstance(artistId))
                .addToBackStack(null)
                .commit();
    }

    public void closeKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            View v = ((Activity) context).getCurrentFocus();
            if (v == null) {
                return;
            }
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void openKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public boolean isNetworkAvailable(Context context) {
        boolean result = false;
        try {
            result = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return result;
    }

    public void showShareDialog(final Share share) {
        try {
            if (null != bottomSheetDialog && bottomSheetDialog.isShowing()) {
                bottomSheetDialog.dismiss();
            }
            View view = getLayoutInflater().inflate(R.layout.bottomsheet_share, null);
            bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);

//            bottomSheetDialog = ProgressDialog.show(context, "", "", true);
//            progressDialog.setCanceledOnTouchOutside(true);
//            progressDialog.setCancelable(true);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            new ThemeManager().applyTheme((ViewGroup) bottomSheetDialog.findViewById(R.id.rlDialogMain), context);

            UserMaster userVo = SPref.getInstance().getUserMasterDetail(context);
            Util.showImageWithGlide(view.findViewById(R.id.ivProfile), userVo.getPhotoUrl(), context);
            ((TextView) view.findViewById(R.id.tvTitleName)).setText(userVo.getDisplayname());

            AppCompatEditText etShare = bottomSheetDialog.findViewById(R.id.etShare);
            MaterialButton bShareIn = bottomSheetDialog.findViewById(R.id.bShare);
            AppCompatTextView tvShareMore = bottomSheetDialog.findViewById(R.id.tvShareMore);
            // boolean isLoggedIn = SPref.getInstance().isLoggedIn(context);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
            tvShareMore.setText(R.string.TXT_SHARE_OUTSIDE);
//            bShareIn.setText(getString(R.string.txt_share_on, AppConfiguration.SHARE));

            bottomSheetDialog.show();
            bShareIn.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
//                shareInside(share, true);
                callShareSubmitApi(share, etShare.getText().toString());
            });

            view.findViewById(R.id.llSendToMessage).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                goToComposeMessageFragment(share);
            });

            view.findViewById(R.id.llShareMore).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                shareOutside(share);
            });

            if (!SPref.getInstance().isLoggedIn(context)) {
                view.findViewById(R.id.ll_post).setVisibility(View.GONE);
                view.findViewById(R.id.llSendToMessage).setVisibility(View.GONE);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showShareDialog(final Share share, String shareurlmsg) {
        try {
            if (null != bottomSheetDialog && bottomSheetDialog.isShowing()) {
                bottomSheetDialog.dismiss();
            }
            View view = getLayoutInflater().inflate(R.layout.bottomsheet_share, null);

            bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
//            bottomSheetDialog = ProgressDialog.show(context, "", "", true);
//            progressDialog.setCanceledOnTouchOutside(true);
//            progressDialog.setCancelable(true);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            new ThemeManager().applyTheme((ViewGroup) bottomSheetDialog.findViewById(R.id.rlDialogMain), context);

            UserMaster userVo = SPref.getInstance().getUserMasterDetail(context);
            Util.showImageWithGlide(view.findViewById(R.id.ivProfile), userVo.getPhotoUrl(), context);
            ((TextView) view.findViewById(R.id.tvTitleName)).setText(userVo.getDisplayname());

            AppCompatEditText etShare = bottomSheetDialog.findViewById(R.id.etShare);
            MaterialButton bShareIn = bottomSheetDialog.findViewById(R.id.bShare);
            AppCompatTextView tvShareMore = bottomSheetDialog.findViewById(R.id.tvShareMore);
            // boolean isLoggedIn = SPref.getInstance().isLoggedIn(context);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
            tvShareMore.setText(R.string.TXT_SHARE_OUTSIDE);
//            bShareIn.setText(getString(R.string.txt_share_on, AppConfiguration.SHARE));

            bottomSheetDialog.show();
            bShareIn.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
//                shareInside(share, true);
                callShareSubmitApi(share, etShare.getText().toString());
            });

            view.findViewById(R.id.llSendToMessage).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                goToComposeMessageFragment(share, shareurlmsg);
            });

            view.findViewById(R.id.llShareMore).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                shareOutside(share);
            });


            if (!SPref.getInstance().isLoggedIn(context)) {
                view.findViewById(R.id.ll_post).setVisibility(View.GONE);
                view.findViewById(R.id.llSendToMessage).setVisibility(View.GONE);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void callShareSubmitApi(Share shareVo, String body) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_SHARE);

                    request.params.put(Constant.KEY_BODY, body);
                    request.params.put(Constant.KEY_TYPE, shareVo.getUrlParams().getType());
                    request.params.put(Constant.KEY_ID, shareVo.getUrlParams().getId());

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("response_share_post", "" + response);
                            if (response != null) {
                                hideBaseLoader();
                                BaseResponse<Object> res = new Gson().fromJson(response, BaseResponse.class);
                                if (TextUtils.isEmpty(res.getError())) {
                                    BaseResponse<String> resp = new Gson().fromJson(response, BaseResponse.class);
                                    Util.showToast(context.getApplicationContext(), resp.getResult());
                                } else {
                                    Util.showToast(context.getApplicationContext(), res.getErrorMessage());
                                }
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);
                } catch (Exception e) {
                    hideBaseLoader();
                }
            } else {
                Util.showToast(context, getStrings(R.string.MSG_NO_INTERNET));
//                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    public void noInternetGoBack(View v) {
        notInternetMsg(v);
        new Handler().postDelayed(() -> {
            if (isAdded()) onBackPressed();
        }, 2000);
        Util.showSnackbar(v, getStrings(R.string.MSG_NO_INTERNET));
    }

    public void notInternetMsg(View v) {
        Util.showSnackbar(v, getStrings(R.string.MSG_NO_INTERNET));
    }

    public void somethingWrongMsg(View v) {
        Util.showSnackbar(v, getStrings(R.string.msg_something_wrong));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.context = context;
            activity = (BaseActivity) context;
            fragmentManager = activity.getSupportFragmentManager();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void openWebView(String url, String title) {
        if (activity instanceof MainActivity) {
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_WEBVIEW);
            intent.putExtra(Constant.KEY_URI, url);
            intent.putExtra(Constant.KEY_TITLE, title);
            startActivity(intent);
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, WebViewFragment.newInstance(url, title)).addToBackStack(null).commit();
        }
    }

    public void openTermsPrivacyFragment(String url) {
        if (activity instanceof MainActivity) {
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.TnC);
            intent.putExtra(Constant.KEY_URI, url);
            startActivity(intent);
            return;
        }
        TnCFragment fragment = TnCFragment.newInstance(url);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fragment.setSharedElementEnterTransition(new DetailsTransition());
                fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                fragment.setExitTransition(new Slide(Gravity.BOTTOM));
                /*fragment.setEnterTransition(new Explode());
                fragment.setExitTransition(new Explode());*/
                fragment.setAllowEnterTransitionOverlap(true);
                fragment.setAllowReturnTransitionOverlap(false);
                fragment.setSharedElementReturnTransition(new DetailsTransition());
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    //.addSharedElement(cvLogin, res.getString(R.string.login_card))
                    //.addSharedElement(ivUserImage, res.getString(R.string.user_image))
                    //.addSharedElement(tvUserName, res.getString(R.string.username))
                    //.addSharedElement(ivMobile, res.getString(R.string.login_mobile))
                    //.addSharedElement(ivPassword, res.getString(R.string.login_password))
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            CustomLog.e(e);
            CustomLog.e("TRANSITION_ERROR", "Build.VERSION.SDK_INT =" + Build.VERSION.SDK_INT);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getParentFragment() == null) {
            activity.currentFragment = this;
        }
        try {
            if (getView() != null) {
                getView().setFocusableInTouchMode(true);
                getView().requestFocus();
                getView().setOnKeyListener((v, keyCode, event) -> {
                    /*if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                        onBackPressed();
                    }*/
                    return false;
                });
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void onBackPressed() {
        try {
            closeKeyboard();
            if (getParentFragment() != null) {
                activity.currentFragment.onBackPressed();
            } else if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStack();
            } else {
                activity.supportFinishAfterTransition();
            }
        } catch (Exception e) {
            CustomLog.e(e);
            activity.supportFinishAfterTransition();
        }
        getActivity().overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right);
    }

    public void Deletedgoback(View v) {
        notInternetMsg(v);
        new Handler().postDelayed(() -> {
            if (isAdded()) onBackPressed();
        }, 2000);
        Util.showSnackbar(v, getStrings(R.string.MSG_DELETED));
    }


    public void goDoubleback() {
        fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 2).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void goToSongsView(int songId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewSongFragment.newInstance(new HashMap<String, Object>(), songId, Constant.ACTIVITY_TYPE_ALBUM_SONG))
                .addToBackStack(null)
                .commit();
    }

    public void goToAlbumView(int songId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewMusicAlbumFragment.newInstance(songId))
                .addToBackStack(null)
                .commit();

    }

    public void goToMapView(int userid, List<Notifications> alublist) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, MapMamberFragment.newInstance(userid, alublist))
                .commit();

    }


    public void goToProfileImageFragment() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new ProfileImageFragment())
                .addToBackStack(null).commit();
    }

    public void goToSignUpFragment(String name) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, SignUpFragment.newInstance(name))
                .addToBackStack(null).commit();
    }


    public void goToProfileFragment(int userId) {
        fragmentManager.beginTransaction().replace(R.id.container, ViewProfileFragment.newInstance(userId)).addToBackStack(null).commit();
    }

    public void goToViewDiscussionFragment(int topicId) {
        fragmentManager.beginTransaction().replace(R.id.container, DiscussionViewFragment.newInstance(topicId)).addToBackStack(null).commit();
    }


    public void goToCommentFragment(int resourceId, String resourceType) {
        fragmentManager.beginTransaction().replace(R.id.container, CommentFragment.newInstance(resourceId, resourceType)).addToBackStack(null).commit();
    }

    protected String getVideoDetail(ViewVideo vo, boolean isChannel) {
        String detail = Constant.EMPTY;

        if (isChannel) {
            detail += "\uf164 " + vo.getLikeCount()
                    + "  \uf075 " + vo.getCommentCount()
                    + "  \uf004 " + vo.getFavouriteCount()
                    + "  \uf06e " + vo.getViewCount()
                    + "  \uf03e " + vo.getPhotos()
                    + "  \uf03d " + vo.getFollowVideos()
                    + "  \uf0c0 " + vo.getFollowCount();
        } else {
            detail += "\uf164 " + vo.getLikeCount()
                    + "  \uf004 " + vo.getFavouriteCount()
                    + "  \uf06e " + vo.getViewCount()
                    + "  \uf03d " + vo.getVideoCount();
        }

        return detail;
    }

    public void showView(View view) {
        if (null != view)
            view.setVisibility(View.VISIBLE);
    }

    public void hideView(View view) {
        if (null != view)
            view.setVisibility(View.GONE);
    }

    public void sharingToSocialMedia(Share vo, String applicationId) {

        try {
            final Intent socialIntent = new Intent(Intent.ACTION_SEND);
            socialIntent.putExtra(Intent.EXTRA_SUBJECT, vo.getTitle());
            socialIntent.setPackage(applicationId);
            socialIntent.setType("text/plain");
            if (!TextUtils.isEmpty(vo.getUrl())) {
                socialIntent.putExtra(Intent.EXTRA_TEXT, vo.getUrl());
                getActivity().startActivity(socialIntent);
            } else if (TextUtils.isEmpty(vo.getImageUrl())) {
                if (TextUtils.isEmpty(vo.getDescription())) {
                    socialIntent.putExtra(Intent.EXTRA_TEXT, vo.getTitle());
                } else {
                    socialIntent.putExtra(Intent.EXTRA_TEXT, vo.getDescription());
                }
                getActivity().startActivity(socialIntent);
            } else {

                askForPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        showBaseLoader(true);
                        try {
                            Glide.with(context).asBitmap()
                                    .load(vo.getImageUrl())//"https://www.google.es/images/srpr/logo11w.png")
                                    .into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            try {
                                                hideBaseLoader();
                                                socialIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                socialIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));
                                                socialIntent.setType("image/*");
                                                //sharingIntent.setType("image/*");
                                                getActivity().startActivity(socialIntent);
                                            } catch (Exception e) {
                                                CustomLog.e(e);
                                                getActivity().startActivity(socialIntent);
                                            }

                                        }
                                    });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
        } catch (android.content.ActivityNotFoundException ex) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + applicationId)));
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void sharingToSocialMedia2(Share vo, String applicationId) {

        try {
            Log.e("URLDATA", "" + vo.getUrl());
            Log.e("URLIMAGEDATA", "" + vo.getImageUrl());
            Log.e("URLNAME", "" + vo.getName());

            final Intent socialIntent = new Intent(Intent.ACTION_SEND);
            socialIntent.putExtra(Intent.EXTRA_SUBJECT, vo.getTitle());
            socialIntent.setPackage(applicationId);
            socialIntent.setType("text/plain");
            if (!TextUtils.isEmpty(vo.getUrl())) {
                socialIntent.putExtra(Intent.EXTRA_TEXT, vo.getUrl());
                getActivity().startActivity(socialIntent);
            } else if (TextUtils.isEmpty(vo.getImageUrl())) {
                if (TextUtils.isEmpty(vo.getDescription())) {
                    socialIntent.putExtra(Intent.EXTRA_TEXT, vo.getTitle());
                } else {
                    socialIntent.putExtra(Intent.EXTRA_TEXT, vo.getDescription());
                }
                getActivity().startActivity(socialIntent);
            } else {
                hideBaseLoader();
                socialIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                socialIntent.putExtra(Intent.EXTRA_TEXT, vo.getTitle() + "\n" + vo.getImageUrl());
                socialIntent.setType("text/plain");
                //sharingIntent.setType("image/*");
                getActivity().startActivity(socialIntent);
            }
        } catch (android.content.ActivityNotFoundException ex) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + applicationId)));
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }


    public void shareOutside(Share vo) {
        // UrlParams urlParams = sharelist.getUrlParams();
        try {
            final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, vo.getTitle());
            if (!TextUtils.isEmpty(vo.getUrl())) {
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, vo.getUrl());
                startActivityForResult(Intent.createChooser(sharingIntent, getString(R.string.MSG_SHARE_VIA)), 25);
            } else if (TextUtils.isEmpty(vo.getImageUrl())) {
                sharingIntent.setType("text/plain");
                if (TextUtils.isEmpty(vo.getDescription())) {
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, vo.getTitle());
                } else {
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, vo.getDescription());
                }
                startActivityForResult(Intent.createChooser(sharingIntent, getString(R.string.MSG_SHARE_VIA)), 25);
            } else {
                askForPermission(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        showBaseLoader(true);

                        Glide.with(context).asBitmap()
                                .load(vo.getImageUrl())//"https://www.google.es/images/srpr/logo11w.png")
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        try {
                                            hideBaseLoader();
                                            sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            sharingIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));
                                            sharingIntent.setType("image/*");
                                            //sharingIntent.setType("image/*");
                                            startActivityForResult(Intent.createChooser(sharingIntent, getString(R.string.MSG_SHARE_VIA)), 25);
                                        } catch (Exception e) {
                                            CustomLog.e(e);
                                            startActivityForResult(Intent.createChooser(sharingIntent, getString(R.string.MSG_SHARE_VIA)), 25);
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

/*    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            try {
                // out = new FileOutputStream(file);
                // bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(
                        context.getContentResolver(), bmp, "Image", null);
                bmpUri = Uri.parse(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return bmpUri;
    }*/

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            /*try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(
                        context.getContentResolver(), bmp, "Image", null);
                bmpUri = Uri.parse(path);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            bmpUri = performShareWithImage(bmp, getActivity());
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return bmpUri;
    }

    public Uri performShareWithImage(Bitmap bmp, Activity activity) {
        String file_path =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + activity.getString(
                        R.string.app_name);
        File dir = new File(file_path);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, "." + "product.jpg");
        //  Uri uri = Uri.fromFile(file);
        Uri photoURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        boolean isPicSaved = savePic(bmp, file);
        return photoURI;
    }


    private static boolean savePic(Bitmap bitmap, File strFileName) {
        FileOutputStream fos = null;
        boolean isFileSaved = false;
        try {
            fos = new FileOutputStream(strFileName);
            if (fos != null && bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                isFileSaved = true;
            }
        } catch (Exception e) {
            isFileSaved = false;
            e.printStackTrace();
        }
        return isFileSaved;
    }


    public void shareInside(Share vo, boolean isCommonActivity) {
        if (!(activity instanceof MainActivity)) {
            fragmentManager.
                    beginTransaction().replace(R.id.container, ShareSEFragment.newInstance(vo)).addToBackStack(null).commit();
        } else {
            Intent intent = new Intent(activity, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_SHARE_SE);
            intent.putExtra(Constant.KEY_TITLE, new Gson().toJson(vo));
            startActivity(intent);
        }
    }

    public void runLayoutAnimation(final RecyclerView recyclerView) {
        if (wasListEmpty) {
            final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.anim_fall_down);
            recyclerView.setLayoutAnimation(controller);
            recyclerView.scheduleLayoutAnimation();
        }
    }


    public void goToProfileInfo(int userId, boolean showToolbar) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, InfoFragment.newInstance(userId, showToolbar))
                .addToBackStack(null)
                .commit();
    }


    public String getStrings(int id) {
        return context.getResources().getString(id);
    }

    public void openViewPollFragment(String selectedModule, int pollId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, PollViewFragment.newInstance(selectedModule, pollId))
                .addToBackStack(null)
                .commit();
    }

    public void openViewPollFragment(String selectedModule, int pollId, String shareurl) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, PollViewFragment.newInstance(selectedModule, pollId, shareurl))
                .addToBackStack(null)
                .commit();
    }


    public void openCCViewPollFragment(int pollId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, CViewPollFragment.newInstance(pollId))
                .addToBackStack(null)
                .commit();
    }

    public void openViewPageFragment(int pageId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewPageFragment.newInstance(pageId))
                .addToBackStack(null)
                .commit();
    }

    public void openViewClassroomFragment(int classroomid) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewClassroomFragment.newInstance(classroomid))
                .addToBackStack(null)
                .commit();
    }

    public void openViewServiceFragment(int classroomid) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewServiceFragment.newInstance(classroomid))
                .addToBackStack(null)
                .commit();
    }

    public void openViewProfessionalPage(int classroomid) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewProfessionalFragment.newInstance(classroomid))
                .addToBackStack(null)
                .commit();
    }

    public void openViewCourseFragment(int courseId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewCourseFragment.newInstance(courseId))
                .addToBackStack(null)
                .commit();
    }


    public void openViewQuestionFragment(int id, Question vo) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewQuestionFragment.newInstance(id, vo))
                .addToBackStack(null)
                .commit();
    }

    public void openViewBusinessFragment(int pageId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewBusinessFragment.newInstance(pageId))
                .addToBackStack(null)
                .commit();
    }

    public void openViewGroupFragment(int pageId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewGroupFragment.newInstance(pageId))
                .addToBackStack(null)
                .commit();
    }

    public void openPageContactForm(int ownerId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.OWNER_ID, ownerId);
        map.put("page_owner_id", ownerId);
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.PAGE_CONTACT, map, Constant.URL_PAGE_CONTACT))
                .addToBackStack(null)
                .commit();
    }

    public void openClassroomContactForm(int ownerId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.OWNER_ID, ownerId);
        map.put("classroom_owner_id", ownerId);
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.PAGE_CONTACT, map, Constant.URL_CLASSROOM_CONTACT))
                .addToBackStack(null)
                .commit();
    }

    public void openBusinessContactForm(int ownerId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.OWNER_ID, ownerId);
        map.put("business_owner_id", ownerId);
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.PAGE_CONTACT, map, Constant.URL_BUSINESS_CONTACT))
                .addToBackStack(null)
                .commit();
    }

    public void openStoreContactForm(int ownerId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.OWNER_ID, ownerId);
        map.put("store_owner_id", ownerId);
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.PAGE_CONTACT, map, Constant.URL_STORE_CONTACT))
                .addToBackStack(null)
                .commit();
    }

    public void openGroupContactForm(int ownerId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.OWNER_ID, ownerId);
        map.put("group_owner_id", ownerId);
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.PAGE_CONTACT, map, Constant.URL_GROUP_CONTACT))
                .addToBackStack(null)
                .commit();
    }


    //method returns image drawable id as per option key
    public int getDrawableId(String name) {
        int id;
        switch (name) {
            case Constant.OptionType.WEBSITE:
                id = R.drawable.world;
                break;
            case Constant.OptionType.PHONE:
                id = R.drawable.page_phone;
                break;
            case Constant.OptionType.MAIL:
                id = R.drawable.envelope;
                break;
            case Constant.OptionType.CATEGORY:
                id = R.drawable.page_category;
                break;
            case Constant.OptionType.TAG:
                id = R.drawable.info_tag;
                break;
            case Constant.OptionType.POST_REPLY:
                id = R.drawable.reply;
                break;
          /*  case Constant.OptionType.WATCH_TOPIC:
                break;
            case Constant.OptionType.STOP_WATCH_TOPIC:
                break;*/
            default:
                id = context.getResources().getIdentifier("page_" + name, "drawable", context.getPackageName());
                if (id <= 0) {
                    id = R.drawable.dot_icon;
                }
                break;
        }
        return id;
    }

    /*public void goToCategoryFragment(int categoryId, String name) {

        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewEventCategoryFragment.newInstance(categoryId, name))
                .addToBackStack(null)
                .commit();
    }*/

    public void openFormFragment(int formType, Map<String, Object> map, String url) {
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(formType, map, url)).addToBackStack(null).commit();
    }

    //Override this method on child class
    public void onRefresh() {
    }

    public void openReactionViewfragment(Map<String, Object> map) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ReactionViewFragment.newInstance(map))
                .addToBackStack(null)
                .commit();
    }

    public void goToCategoryFragment(Category category, int categoryLevel) {
        category.setCategoryLevel(categoryLevel);
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewEventCategoryFragment.newInstance(category))
                .addToBackStack(null)
                .commit();
    }

    public void showPopup(List<Options> menus, View v, int idPrefix, PopupMenu.OnMenuItemClickListener listener) {
        try {
            PopupMenu menu = new PopupMenu(context, v);
            for (int index = 0; index < menus.size(); index++) {
                Options s = menus.get(index);
                menu.getMenu().add(1, idPrefix + index + 1, index + 1, s.getLabel());
            }
            menu.show();
            menu.setOnMenuItemClickListener(listener);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void openGoogleMap(String lat, String lng) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lng));
        startActivity(intent);
    }

    public Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, context.getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    public void updateFabColor(FloatingActionButton fab) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DrawableCompat.setTintList(DrawableCompat.wrap((fab).getDrawable()), ColorStateList.valueOf(SesColorUtils.getText1Color(context)));
                fab.setBackgroundTintList(ColorStateList.valueOf(SesColorUtils.getPrimaryColor(context)));
            }
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    public void openSinglePhotoFragment(ImageView ivImage, String url, String transitionName) {
        try {
            //  String transitionName = albumsList.get(position).getTitle();
            ViewCompat.setTransitionName(ivImage, transitionName);
            // ViewCompat.setTransitionName(holder.tvName, transitionName + Constant.Trans.TEXT);
            //  ViewCompat.setTransitionName(holder.llMain, transitionName + Constant.Trans.LAYOUT);


            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            // bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, url);
            //  bundle.putString(Constant.Trans.LAYOUT, transitionName + Constant.Trans.LAYOUT);

            fragmentManager.beginTransaction()
                    .addSharedElement(ivImage, ViewCompat.getTransitionName(ivImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    //.addSharedElement(holder.tvName, ViewCompat.getTransitionName(holder.tvName))
                    .replace(R.id.container, SinglePhotoFragment.newInstance(url, bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SinglePhotoFragment.newInstance(url, null)).addToBackStack(null).commit();
        }
    }

    public void showUpdateDialog(String title, String msg, String buttonText, OnUserClickedListener<Integer, Object> listener, int position) {

        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_update_app);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            ((TextView) progressDialog.findViewById(R.id.tvDialogTitle))
                    .setText(title);
            ((TextView) progressDialog.findViewById(R.id.tvDialogText))
                    .setText(msg);

            TextView bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.CANCEL);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(buttonText);

            bGallary.setOnClickListener(v -> {
                progressDialog.dismiss();
                listener.onItemClicked(Constant.Events.OK, null, position);
            });

            bCamera.setOnClickListener(v -> {
                progressDialog.dismiss();
                listener.onItemClicked(Constant.Events.CANCEL, null, position);
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showDeleteDialog(OnUserClickedListener<Integer, Object> listener, int position, String msg) {
        showDeleteDialog(listener, position, msg, R.string.YES, R.string.NO);
    }

    public void showDeleteDialog(OnUserClickedListener<Integer,
            Object> listener, int position, String msg,
                                 int positiveText, int negetiveText) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(positiveText);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(negetiveText);

            bCamera.setOnClickListener(v -> {
                progressDialog.dismiss();
                listener.onItemClicked(Constant.Events.OK, null, position);
            });

            bGallary.setOnClickListener(v -> {
                progressDialog.dismiss();
                listener.onItemClicked(Constant.Events.CANCEL, null, position);
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //Override this method on Child classed and start listening to Notification events
    public void listenNotificationEvent(final int TYPE) {

    }
}
