package com.sesolutions.ui.photo;


import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Like;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.CommentLike;
import com.sesolutions.responses.photo.PhotoResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.common.ImageViewPagerAdapter;
import com.sesolutions.ui.customviews.GalleryViewPager;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.member.TagSuggestionFragment;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;

public class GallaryFragment extends CommentLikeHelper implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final int REQ_UPDATE_UPPER = 100;
    private static final int LOAD_MORE = 101;

    private GalleryViewPager viewPager;
    private ImageViewPagerAdapter adapter;
    private PhotoResponse.Result result;
    private String url;

    private int photoId;
    private List<Albums> albumList;
    private Map<String, Object> map;
    private int selectedItem;
    private TextView tvTaggedUser;
    private TextView tvDescription;
    private RelativeLayout rlLightBox;
    private ImageView ivTempImage;
    private boolean isLoading = false;
    private final int REQ_DELETE_PHOTO = 101;
    private final int REQ_MAKE_PROFILE = 102;
    private boolean isUserLoggedIn;

    protected TextView tvLikeUpper;
    protected View rlUpperLike;
    protected ImageView ivLikeUpper1;
    protected ImageView ivLikeUpper2;
    protected ImageView ivLikeUpper3;
    protected ImageView ivLikeUpper4;
    protected ImageView ivLikeUpper5;
    protected TextView tvCommentUpper;
    protected View llReactionUpper;
    private boolean isMorePhotoAvailable = true;
    private boolean isBuySell = false;
    private boolean isChannel = false;
    private int userId;
    private Bundle bundle;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivTempImage.setTransitionName(bundle.getString(Constant.Trans.IMAGE));
            // tvAlbumTitle.setTransitionName(bundle.getString(Constant.Trans.TEXT));
            // tvAlbumTitle.setText(bundle.getString(Constant.Trans.IMAGE));
            try {
                Glide.with(context)
                        .setDefaultRequestOptions(new RequestOptions().dontAnimate().dontTransform().centerCrop())
                        .load(bundle.getString(Constant.Trans.IMAGE_URL))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                CustomLog.e("onLoadFailed", "onLoadFailed");
                                startPostponedEnterTransition();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                CustomLog.e("onResourceReady", "onResourceReady");
                                //  ivAlbumImage.setImageDrawable(resource);
                                startPostponedEnterTransition();
                                return false;
                            }
                        })
                        .into(ivTempImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
            //    Util.showImageWithGlide(ivAlbumImage, bundle.getString(Constant.Trans.IMAGE_URL), context);
        } /*else {

        }*/
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            callMusicAlbumApi(REQ_UPDATE_UPPER);
            return v;
        }
        v = inflater.inflate(R.layout.act_lightbox, container, false);
        try {
            //applyTheme(v);
            init();
            /*this will be referenced on CommentLikeHelper class*/
            isGallery = true;
            setupViewPager();
            callMusicAlbumApi(1);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        hideAllViews();
        isUserLoggedIn = SPref.getInstance().isLoggedIn(context);
        if (isUserLoggedIn) userId = SPref.getInstance().getUserMasterDetail(context).getUserId();
        //rlLightBox = v.findViewById(R.id.rlLightbox);
        viewPager = v.findViewById(R.id.viewpager);
        ivTempImage = v.findViewById(R.id.ivTempImage);
        viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        tvTaggedUser = v.findViewById(R.id.tvTaggedUser);
        tvDescription = v.findViewById(R.id.tvDescription);
        v.findViewById(R.id.llShare).setOnClickListener(this);
        v.findViewById(R.id.llComment).setOnClickListener(this);
        v.findViewById(R.id.llLike).setOnClickListener(this);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivOption).setOnClickListener(this);
        v.findViewById(R.id.ivTag).setOnClickListener(this);
        tvTaggedUser.setOnClickListener(this);
        String imageUrl = (String) map.get(Constant.KEY_IMAGE);
        Util.showImageWithGlide(ivTempImage, imageUrl, context);
        map.put(Constant.KEY_IMAGE, null);
        stats = new CommentLike.Stats();
        llReactionUpper = v.findViewById(R.id.llReactionUpper);
        rlUpperLike = v.findViewById(R.id.rlUpperLike);
        tvLikeUpper = v.findViewById(R.id.tvLikeUpper);
        ivLikeUpper1 = v.findViewById(R.id.ivLikeUpper1);
        ivLikeUpper2 = v.findViewById(R.id.ivLikeUpper2);
        ivLikeUpper3 = v.findViewById(R.id.ivLikeUpper3);
        ivLikeUpper4 = v.findViewById(R.id.ivLikeUpper4);
        ivLikeUpper5 = v.findViewById(R.id.ivLikeUpper5);
        tvCommentUpper = v.findViewById(R.id.tvCommentUpper);
        llReactionUpper.setOnClickListener(this);
        v.findViewById(R.id.tvUserTitle).setOnClickListener(this);
        v.findViewById(R.id.llLike).setOnLongClickListener(v -> {
            createPopUp(v, -1, this);
            return false;
        });
    }

    public void updateReactionUpper(Albums vo) {

        if (!TextUtils.isEmpty(vo.getReactionUserData()) || vo.isLike()) {
            llReactionUpper.setVisibility(View.VISIBLE);
            rlUpperLike.setVisibility(View.VISIBLE);
            tvLikeUpper.setVisibility(View.VISIBLE);
            tvLikeUpper.setText(vo.getReactionUserData());
            if (null != vo.getReactionData()) {
                if (vo.getReactionData().size() > 0) {
                    ivLikeUpper1.setVisibility(View.VISIBLE);
                    Util.showImageWithGlide(ivLikeUpper1, vo.getReactionData().get(0).getImageUrl(), context);
                } else {
                    ivLikeUpper1.setVisibility(View.GONE);
                }
                if (vo.getReactionData().size() > 1) {
                    ivLikeUpper2.setVisibility(View.VISIBLE);
                    Util.showImageWithGlide(ivLikeUpper2, vo.getReactionData().get(1).getImageUrl(), context);
                } else {
                    ivLikeUpper2.setVisibility(View.GONE);
                }
                if (vo.getReactionData().size() > 2) {
                    ivLikeUpper3.setVisibility(View.VISIBLE);
                    Util.showImageWithGlide(ivLikeUpper3, vo.getReactionData().get(2).getImageUrl(), context);
                } else {
                    ivLikeUpper3.setVisibility(View.GONE);
                }
                if (vo.getReactionData().size() > 3) {
                    ivLikeUpper4.setVisibility(View.VISIBLE);
                    Util.showImageWithGlide(ivLikeUpper4, vo.getReactionData().get(3).getImageUrl(), context);
                } else {
                    ivLikeUpper4.setVisibility(View.GONE);
                }
                if (vo.getReactionData().size() > 4) {
                    ivLikeUpper5.setVisibility(View.VISIBLE);
                    Util.showImageWithGlide(ivLikeUpper5, vo.getReactionData().get(4).getImageUrl(), context);
                } else {
                    ivLikeUpper5.setVisibility(View.GONE);
                }
            }
        } else if (vo.getCommentCount() == 0) {
            llReactionUpper.setVisibility(View.GONE);
        } else {
            llReactionUpper.setVisibility(View.VISIBLE);
            rlUpperLike.setVisibility(View.GONE);
            tvLikeUpper.setVisibility(View.INVISIBLE);
        }

        tvCommentUpper.setText(vo.getCommentCount() > 0 ? (vo.getCommentCount() + (vo.getCommentCount() == 1 ? Constant._COMMENT : Constant._COMMENTS)) :
                Constant.EMPTY);
    }

    private void hideLayouts() {
        clickedTimeInMilis = System.currentTimeMillis();
        new Handler().postDelayed(() -> {
            if (isAdded() && (System.currentTimeMillis() - clickedTimeInMilis) > 3990) {
                v.findViewById(R.id.rlHeader).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.llDetail).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.llReaction).setVisibility(View.INVISIBLE);
            }
        }, 4000);
    }

    private long clickedTimeInMilis;

    private void showHideLayouts() {
        clickedTimeInMilis = System.currentTimeMillis();
        if (v.findViewById(R.id.rlHeader).getVisibility() != View.VISIBLE) {
            v.findViewById(R.id.rlHeader).setVisibility(View.VISIBLE);
            if (!isBuySell) {
                v.findViewById(R.id.llDetail).setVisibility(View.VISIBLE);
                if (isUserLoggedIn)
                    v.findViewById(R.id.llReaction).setVisibility(View.VISIBLE);
            }
            new Handler().postDelayed(() -> {
                if (isAdded() && (System.currentTimeMillis() - clickedTimeInMilis) > 3990)
                    showHideLayouts();
            }, 4000);
        } else {
            v.findViewById(R.id.rlHeader).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.llDetail).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.llReaction).setVisibility(View.INVISIBLE);
        }
    }


    private void setupViewPager() {
        albumList = new ArrayList<>();
        adapter = new ImageViewPagerAdapter(getChildFragmentManager(), albumList, this);
        viewPager.setAdapter(adapter);
        //viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectedItem = position;
                setScreenData(albumList.get(selectedItem));

                if (isMorePhotoAvailable && !isLoading && selectedItem > albumList.size() - 2) {
                    loadMore();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void loadMore() {
        /* Send last photo_id in key photo_id and condition = ">"*/
        map.put(Constant.KEY_PHOTO_ID, albumList.get(albumList.size() - 1).getPhotoId());
        map.put(Constant.KEY_CONDITION, ">");
        callMusicAlbumApi(LOAD_MORE);
    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    if (req == 1)
                        showBaseLoader(true);
                    HttpRequestVO request = new HttpRequestVO(url);
                   /* request.params.put(Constant.KEY_PHOTO_ID, photoId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);*/
                    map.put(Constant.KEY_URI, null);
                    request.params.putAll(map);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {

                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        PhotoResponse resp = new Gson().fromJson(response, PhotoResponse.class);
                                        if (req != LOAD_MORE) {
                                            result = resp.getResult();
                                        }
                                        int item = 0;
                                        if (null != resp.getResult().getPhotos()) {
                                            List<Albums> list = resp.getResult().getPhotos();

                                            if (req == REQ_UPDATE_UPPER) {
                                                item = selectedItem;
                                                albumList.clear();
                                            }
                                            for (int i = 0; i < list.size(); i++) {
                                                if (photoId == list.get(i).getPhotoId()) {
                                                    selectedItem = i;
                                                    /*if this is the image of list then load more*/
                                                    if (list.size() == selectedItem - 1) {
                                                        loadMore();
                                                    }
                                                    break;
                                                }
                                            }
                                            albumList.addAll(list);
                                        } else {
                                            isMorePhotoAvailable = false;
                                        }
                                        updateAdapter(req, item);

                                    } else {
                                        Util.showSnackbar(viewPager, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
                                }
                                isLoading = false;
                            } catch (Exception e) {
                                isLoading = false;

                                hideBaseLoader();
                                CustomLog.e(e);
                            }

                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;

                    hideBaseLoader();
                    CustomLog.e(e);
                }
            } else {
                isLoading = false;
                notInternetMsg(v);
            }
        } catch (Exception e) {
            isLoading = false;
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    public void showHideOptionIcon() {
        try {
            v.findViewById(R.id.ivOption).setVisibility((result.getMenus() != null && result.getMenus().size() > 0) ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateAdapter(int req, int item) {
        showHideOptionIcon();
        stats.setReactionPlugin(result.getReactionPlugin());
        adapter.notifyDataSetChanged();
        if (req == REQ_UPDATE_UPPER) {
            setScreenData(albumList.get(item));
        } else if (req != LOAD_MORE) {
            viewPager.setCurrentItem(selectedItem);
            showAllViews();
            hideLayouts();
        }


        //Handling issue of NOT showing data if there is only one image
        if (albumList.size() == 1 || selectedItem == 0)
            setScreenData(albumList.get(0));
    }

    private void showAllViews() {
        viewPager.setVisibility(View.VISIBLE);
        v.findViewById(R.id.rlHeader).setVisibility(View.VISIBLE);

        if (isBuySell) {
            v.findViewById(R.id.ivOption).setVisibility(View.GONE);
            v.findViewById(R.id.ivTag).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.llDetail).setVisibility(View.VISIBLE);
            if (isUserLoggedIn) {
                v.findViewById(R.id.llReaction).setVisibility(View.VISIBLE);
            } else {
                v.findViewById(R.id.llReaction).setVisibility(View.INVISIBLE);
                v.findViewById(R.id.ivOption).setVisibility(View.GONE);
            }
        }
        ivTempImage.setVisibility(View.GONE);
    }

    private void hideAllViews() {
        v.findViewById(R.id.rlHeader).setVisibility(View.GONE);
        v.findViewById(R.id.llDetail).setVisibility(View.GONE);
        v.findViewById(R.id.llReaction).setVisibility(View.GONE);
    }

    private void setScreenData(Albums vo) {
        if (isBuySell) return;//Don't you dare to update anything for buysell images...
        try {
            if (map.get(Constant.KEY_RESOURCES_TYPE) == Constant.ACTIVITY_TYPE_BUY_SELL) {

            }
            resourceId = vo.getPhotoId();
            stats.setIsLike(vo.isLike());
            if (vo.getLike() != null && !TextUtils.isEmpty(vo.getLike().getType())) {
                updateLike(Integer.parseInt(vo.getLike().getType()));
            } else {
                updateLike(vo.isLike() ? 1 : 0);
            }
            ((TextView) v.findViewById(R.id.tvUserTitle)).setText(vo.getUserTitle());
            ((TextView) v.findViewById(R.id.tvDate)).setText(Util.changeDateFormat(context, vo.getCreationDate()));
            String desc = vo.getDescription();
            if (TextUtils.isEmpty(desc)) {
                tvDescription.setVisibility(View.GONE);
            } else {
                tvDescription.setText(vo.getDescription());
                tvDescription.setVisibility(View.VISIBLE);
            }
            if (null != vo.getTags() && vo.getTags().size() > 0) {
                tvTaggedUser.setVisibility(View.VISIBLE);
                String taggedUser = "- " + vo.getTags().get(0).getText();  // "- User
                if (vo.getTags().size() == 2) {
                    taggedUser += Constant._AND_ + vo.getTags().get(1).getText();  //"-User1 and User2"
                } else if (vo.getTags().size() > 2) {
                    //" -User and 4 others"
                    taggedUser += Constant._AND_ + (vo.getTags().size() - 1) + Constant._OTHERS;
                }


                tvTaggedUser.setText(taggedUser);
            } else {
                tvTaggedUser.setVisibility(View.GONE);
            }
            updateReactionUpper(vo);
            v.findViewById(R.id.ivTag).setVisibility(result.isCanTag() ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.setStatusBarColor(Color.BLACK);
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.setStatusBarColor(Util.manipulateColor(Color.parseColor(Constant.colorPrimary)));
    }


    @Override
    public void onClick(View v) {
        clickedTimeInMilis = System.currentTimeMillis();
        //  super.onClick(v);
        switch (v.getId()) {
            case R.id.llLike:
                albumList.get(selectedItem).setLike(!stats.getIsLike());
                reactionType = stats.getIsLike() ? 0 : 1;
                updateLike(reactionType);
                callBottomCommentLikeApi(resourceId, resourceType, Constant.URL_MUSIC_LIKE);
                break;
            case R.id.llShare:
                showPopup(result.getShareOptions(), v, 100);
                break;
            case R.id.tvTaggedUser:
                boolean isOwner = userId != 0 && albumList.get(selectedItem).getOwnerId() == userId;
                goToTagSuggestion(true, isOwner);
                break;
            case R.id.tvUserTitle:
                goToProfileFragment(albumList.get(selectedItem).getOwnerId());
                break;
            case R.id.llReactionUpper:
            case R.id.llComment:
                goToCommentFragment(resourceId, resourceType);
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivOption:
                showPopup(result.getMenus(), v, 10);
                break;
            case R.id.ivTag:
                goToTagSuggestion(false, true);
                break;
        }
    }

    private void goToTagSuggestion(boolean isAddRemove, boolean isOwner) {

        Intent intent2 = new Intent(activity, CommonActivity.class);
        intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.GO_TO_TAGSUGGEST);
        intent2.putExtra(Constant.KEY_ID, albumList.get(selectedItem).getPhotoId());
        intent2.putExtra(Constant.KEY_ISOWENER, isOwner);
        intent2.putExtra(Constant.KEY_ISREMNOVE, isAddRemove);
        startActivityForResult(intent2, EDIT_CHANNEL_ME);
/*
        fragmentManager.beginTransaction()
                .replace(R.id.container, TagSuggestionFragment.newInstance(albumList.get(selectedItem).getPhotoId(), isAddRemove, isOwner))
                .addToBackStack(null)
                .commit();*/
    }


    public void showDeleteDialog(final int albumId) {
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
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_PHOTO);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.DELETE_PHOTO);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(REQ_DELETE_PHOTO, albumId);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int REQ, final int id) {
        try {
            if (isNetworkAvailable(context)) {
                try {
                    String url = Constant.URL_LIGHTBOX_DELETE_PHOTO;
                    if (REQ == REQ_MAKE_PROFILE) {
                        url = Constant.URL_MEMBER_UPLOAD_PHOTO;
                    }

                    if(resourceType.equalsIgnoreCase("sespage_photo")){
                        url = Constant.URL_REMOVE_PAGE_PHOTO2;
                    }

                    HttpRequestVO request = new HttpRequestVO(url);

                    if(resourceType.equalsIgnoreCase("sespage_photo")){
                        request.params.put(Constant.KEY_PAGE_ID, map.get(Constant.KEY_PAGE_ID));
                    }

                    request.params.put(Constant.KEY_PHOTO_ID, id);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        if (REQ == REQ_MAKE_PROFILE) {

                                            /* updating userVo in share preference
                                             * ,so that when user goes back to main activity
                                             * ,his profile pic will be updated
                                             */
                                            UserMaster vo = SPref.getInstance().getUserMasterDetail(context);
                                            vo.setPhotoUrl(albumList.get(selectedItem).getImages().getMain());
                                            SPref.getInstance().saveUserMaster(context, vo, null);
                                            BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                            Util.showSnackbar(v, res.getResult());
                                        } else {
                                            activity.taskPerformed = Constant.TASK_IMAGE_DELETED;
                                            onBackPressed();
                                            //  fragmentManager.popBackStack();
                                        }
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }
                            } catch (Exception e) {
                                hideBaseLoader();
                                CustomLog.e(e);
                            }
                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);
                } catch (Exception e) {
                    hideBaseLoader();
                }
            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {

            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            int itemId = item.getItemId();
            Options opt = null;

            if (itemId > 100) {
                itemId = itemId - 100;
                opt = result.getShareOptions().get(itemId - 1);

            } else {
                itemId = itemId - 10;
                opt = result.getMenus().get(itemId - 1);
            }

            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    String desc = albumList.get(selectedItem).getDescription();
                    DescriptionDialogFragment.newInstance(this, desc).show(fragmentManager, Constant.EMPTY);
                    break;

                case Constant.OptionType.DELETE:
                    showDeleteDialog(albumList.get(selectedItem).getPhotoId());
                    break;

                case Constant.OptionType.REPORT:
                    goToReportFragment();
                    break;
                case Constant.OptionType.MAKE_PROFILE_PHOTO:
                    callDeleteApi(REQ_MAKE_PROFILE, albumList.get(selectedItem).getPhotoId());
                    break;
                case Constant.OptionType.SAVE:
                    //Permission Check
                    askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, albumList.get(selectedItem).getImages().getMain());
                    //***Edit Code new ImageSaveTask(context, albumList.get(selectedItem).getImages().getMain()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    //      showImageRemoveDialog(false, Constant.MSG_PROFILE_IMAGE_DELETE_CONFIRMATION, Constant.URL_MUSIC_REMOVE_PHOTO);
                    break;
                case Constant.OptionType.SHARE_INSIDE:
                    shareInside(albumList.get(selectedItem).getShareData(), true);
                    break;
                case Constant.OptionType.SHARE_OUTSIDE:
                    shareOutside(albumList.get(selectedItem).getShareData());
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    public void askForPermission(String permission, final String imgUrl) {
        try {
            new TedPermission(context)
                    .setPermissionListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted() {
                            downloadImage(imgUrl);
                        }

                        @Override
                        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(permission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    /*public PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {
                //
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };*/

    private void downloadImage(String imageUrl) {
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            String str = albumList.get(selectedItem).getOwner().getTitle();
            //  request.setTitle(Constant.DOWNLOADING_IMAGE);
            // request.setDescription("Downloading " + "Image" + ".png");
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/" + getStrings(R.string.app_name).replace(" ", "") + "/" + str + Util.getCurrentdate(Constant.TIMESTAMP) + ".png");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadManager.enqueue(request);

            Util.showSnackbar(v, "Photo saved successfully.");
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void goToReportFragment() {
        try {
            String type = albumList.get(selectedItem).getResource_type();
            if (TextUtils.isEmpty(type)) {
                type = Constant.ResourceType.ALBUM;
            }
            String guid = type + "_" + albumList.get(selectedItem).getPhotoId();

            Intent intent3 = new Intent(getActivity(), CommonActivity.class);
            intent3.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.GO_TO_REPORT);
            intent3.putExtra(Constant.KEY_TITLE, guid);
            startActivity(intent3);

            // fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void showPopup(List<Options> menus, View v, int idPrefix) {
        try {
            PopupMenu menu = new PopupMenu(context, v);
            for (int index = 0; index < menus.size(); index++) {
                Options s = menus.get(index);
                menu.getMenu().add(1, idPrefix + index + 1, index + 1, s.getLabel());
            }
            menu.show();
            menu.setOnMenuItemClickListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public static GallaryFragment newInstance(Map<String, Object> map) {
        GallaryFragment frag = new GallaryFragment();
        frag.map = map;
        frag.resourceType = (String) frag.map.get(Constant.KEY_TYPE);
        frag.photoId = (Integer) map.get(Constant.KEY_PHOTO_ID);

        String type = (String) map.get(Constant.KEY_TYPE);
        frag.url = Constant.URL_ALBUM_LIGHTBOX;
        if (!TextUtils.isEmpty(type)) {
            if (type.equals(Constant.ACTIVITY_TYPE_BUY_SELL)) {
                frag.url = Constant.BASE_URL + Constant.URL_BUYSELL_LIGHTBOX +
                        map.get(Constant.KEY_ALBUM_ID)
                        + Constant.POST_URL;
                frag.isBuySell = true;
                frag.map.put(Constant.KEY_ALBUM_ID, map.get(Constant.KEY_ALBUM_ID));
                frag.map.put(Constant.KEY_RESOURCES_TYPE, Constant.ACTIVITY_TYPE_BUY_SELL);
                frag.map.put(Constant.KEY_TYPE, null);
            } else if (type.equals(Constant.ResourceType.CHANNEL_PHOTO)) {
                frag.url = Constant.URL_CHANNEL_LIGHTBOX;
                frag.map.put(Constant.KEY_ALBUM_ID, map.get(Constant.KEY_ALBUM_ID));
                frag.map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.CHANNEL_PHOTO);
                frag.map.put(Constant.KEY_TYPE, null);
            } else if (type.equals(Constant.ResourceType.PAGE_PHOTO)) {
                frag.url = Constant.URL_PAGE_LIGHTBOX;
                //  frag.reactionType = Constant.ResourceType.PAGE;
                //     frag.pag = (Integer) map.get(Constant.KEY_PAGE_ID);
                // frag.map.put(Constant.KEY_ALBUM_ID, map.get(Constant.KEY_ALBUM_ID));
                // frag.map.put(Constant.KEY_RESOURCES_TYPE,Constant.ResourceType.PAGE );
                // frag.map.put(Constant.KEY_PHOTO_ID, null);
                frag.map.put(Constant.KEY_TYPE, null);
            } else if (type.equals(Constant.ResourceType.SES_EVENT_PHOTO)) {
                frag.url = Constant.URL_EVENT_LIGHTBOX;
                frag.map.put(Constant.KEY_TYPE, null);
            } else if (type.equals(Constant.ResourceType.GROUP_PHOTO)) {
                frag.url = Constant.URL_GROUP_LIGHTBOX;
                frag.map.put(Constant.KEY_TYPE, null);
            } else if (type.equals(Constant.ResourceType.BUSINESS_PHOTO)) {
                frag.url = Constant.URL_BUSINESS_LIGHTBOX;
                frag.map.put(Constant.KEY_TYPE, null);
            } else if (type.equals(Constant.ResourceType.EVENT_PHOTO)) {
                frag.url = Constant.URL_CEVENT_LIGHTBOX;
                frag.map.put(Constant.KEY_TYPE, null);
            } else if (type.equals(Constant.ResourceType.CORE_GROUP_PHOTO)) {
                frag.url = Constant.URL_CGROUP_LIGHTBOX;
                frag.map.put(Constant.KEY_TYPE, null);
            }
        }
        return frag;
    }

    public void callSubmitDescriptionApi(final String desc) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    setDescription(desc);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_EDIT_PHOTO_DESCRIPTION);
                    request.params.put(Constant.KEY_PHOTO_ID, albumList.get(selectedItem).getPhotoId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
                    request.params.put(Constant.KEY_DESCRIPTION, desc);

                    // request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {

                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }
                            } catch (Exception e) {

                                hideBaseLoader();
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {

                    hideBaseLoader();
                    CustomLog.e(e);
                }
            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void setDescription(String desc) {
        tvDescription.setText(desc);
        tvDescription.setVisibility(View.VISIBLE);
        albumList.get(selectedItem).setDescription(desc);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        closeKeyboard();
        try {
            switch (object1) {
                case Constant.Events.VIEW_LIKED:
                    if (null == albumList.get(selectedItem).getLike()) {
                        albumList.get(selectedItem).setLike(new Like());
                    }
                    /*call like api */
                    albumList.get(selectedItem).getLike().setType("" + stats.getReactionPlugin().get(Integer.parseInt("" + object2)).getReactionId());
                    adapter.notifyDataSetChanged();
                    break;
                case Constant.Events.CONTENT_EDIT:
                    if (postion == 1)
                        callSubmitDescriptionApi("" + object2);
                    break;
                case Constant.Events.ON_DISMISS:
                    onBackPressed();
                    //fragmentManager.popBackStack();
                    break;
                case Constant.Events.IMAGE_1:
                    showHideLayouts();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, object2, postion);
    }

}
