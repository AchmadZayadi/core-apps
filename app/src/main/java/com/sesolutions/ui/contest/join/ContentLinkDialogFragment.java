package com.sesolutions.ui.contest.join;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.responses.music.MusicView;
import com.sesolutions.responses.music.ResultView;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentLinkDialogFragment extends AppCompatDialogFragment implements OnUserClickedListener<Integer, Object>, View.OnClickListener {
    private static final int REQ_LOAD_MORE = 12;
    private static final int REQ_LOAD_MORE_CHILD = 13;
    private View v;
    private OnUserClickedListener<Integer, Object> listener;
    private Context context;
    private List<SearchVo> searchList;
    private List<SearchVo> childList;
    private LinkAdapter adapter;
    private LinkAdapter adapterChild;

    private String contestType;
    private CommonResponse.Result result;
    private CommonResponse.Result resultChild1;
    private ResultView resultChild2;
    private boolean isLoading;
    private int albumId;
    private boolean wasListEmpty;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_contest_link, container, false);
        try {
            context = getContext();
            new ThemeManager().applyTheme((ViewGroup) v, context);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            ((ImageView) v.findViewById(R.id.ivBack)).setColorFilter(Color.parseColor(Constant.text_color_1));
            setFeedUpdateRecycleView(context);

            // Map<String, Object> req = new HashMap<>(map);
            //  req.put(Constant.KEY_GET_FORM, 1);
            callApi(1, 1);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private RecyclerView rvParent;

    private void setFeedUpdateRecycleView(Context context) {
        try {
            searchList = new ArrayList<>();
            rvParent = v.findViewById(R.id.rvParent);
            rvParent.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvParent.setLayoutManager(layoutManager);
            adapter = new LinkAdapter(searchList, context, this, false);
            rvParent.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private RecyclerView rvChild;

    private void setChildRecycleView() {
        try {
            childList = new ArrayList<>();
            rvChild = v.findViewById(R.id.rvChild);
            rvChild.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            rvChild.setLayoutManager(layoutManager);
            adapterChild = new LinkAdapter(childList, context, this, true);
            rvChild.setAdapter(adapterChild);
            //  rvChild.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callChildApi(int reqCode, int pageCount) {
        String url = null;
        Map<String, Object> req = new HashMap<>();
        switch (contestType) {
            case "2":
                //my album url
                // ((TextView) v.findViewById(R.id.tvDialogTitle)).setText(R.string.select_photo_text);
                // ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_ALBUM_CREATED);
                url = Constant.BASE_URL + "album/view/" + albumId + Constant.POST_URL;
                break;
            case "4":
                // ((TextView) v.findViewById(R.id.tvDialogTitle)).setText(R.string.select_music);
                // ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_MUSIC_ALBUM_AVAILABLE);
                url = Constant.URL_MUSIC_ALBUM_VIEW;
                req.put(Constant.KEY_ALBUM_ID, albumId);


                break;
        }

        if (reqCode == REQ_LOAD_MORE_CHILD) {
            v.findViewById(R.id.pb).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
        }
        req.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
        req.put(Constant.KEY_PAGE, pageCount);
        isLoading = true;
        req.put(Constant.KEY_USER_ID, SPref.getInstance().getLoggedInUserId(context));

        // v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
        // v.findViewById(R.id.rvFeedUpdate).setVisibility(View.GONE);

        new ApiController(url, req, context, this, reqCode).execute();
    }

    private void callApi(int reqCode, int pageCount) {
        String url = null;
        switch (contestType) {
            case "1":
                //my blog url
                ((TextView) v.findViewById(R.id.tvDialogTitle)).setText(R.string.select_blog);
                ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_BLOG_CREATED_YOU);
                url = Constant.URL_BLOG_BROWSE;
                break;
            case "2":
                //my album url
                ((TextView) v.findViewById(R.id.tvDialogTitle)).setText(R.string.select_photo_text);
                ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_ALBUM_CREATED);
                url = Constant.URL_MY_ALBUM;
                break;
            case "3":
                // my video url
                ((TextView) v.findViewById(R.id.tvDialogTitle)).setText(R.string.select_video);
                ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_VIDEO_BY_YOU);
                url = Constant.URL_VIDEO_BROWSE;
                break;
            case "4":
                ((TextView) v.findViewById(R.id.tvDialogTitle)).setText(R.string.select_music);
                ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_MUSIC_ALBUM_AVAILABLE);
                url = Constant.URL_BROWSE_MUSIC_ALBUM;
                break;
        }

        if (reqCode == REQ_LOAD_MORE) {
            v.findViewById(R.id.pb).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
        }
        if (reqCode == REQ_LOAD_MORE_CHILD) {
            v.findViewById(R.id.pb).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
        }

        isLoading = true;
        Map<String, Object> req = new HashMap<>();
        req.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
        req.put(Constant.KEY_PAGE, pageCount);
        req.put(Constant.KEY_USER_ID, SPref.getInstance().getLoggedInUserId(context));

        // v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
        v.findViewById(R.id.rvParent).setVisibility(View.GONE);

        new ApiController(url, req, context, this, -1).execute();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        // Speech.getInstance().shutdown();
    }

    public void onDismiss() {
        onDismiss(getDialog());
    }

    public static ContentLinkDialogFragment newInstance(OnUserClickedListener<Integer, Object> listener, String contestType) {
        ContentLinkDialogFragment frag = new ContentLinkDialogFragment();
        frag.listener = listener;
        frag.contestType = contestType;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case REQ_LOAD_MORE_CHILD:
            case -3:
                try {
                    String response = (String) object2;
                    CustomLog.e("repsonse", "" + response);
                    if (response != null) {
                        ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                        if (err.isSuccess()) {
                            wasListEmpty = childList.size() == 0;
                            if ("2".equals(contestType)) {
                                CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                resultChild1 = resp.getResult();
                                if (null != resultChild1.getPhotos()) {
                                    childList.addAll(resultChild1.convertPhotoToSearchVo());
                                }
                            } else {
                                MusicView resp = new Gson().fromJson(response, MusicView.class);
                                resultChild2 = resp.getResult();
                                if (null != resultChild2.getSongs()) {
                                    childList.addAll(resultChild2.convertSongToSearchVo());
                                }
                            }
                            /*if (null != result.getVideos()) {
                                searchList.addAll(result.convertVideoToSearchVo());
                            }
                            if (null != result.getAlbums()) {
                                searchList.addAll(result.convertAlbumToSearchVo());
                            }*/


                            updateChildUI(object1);
                        } else {
                            Util.showSnackbar(v, err.getErrorMessage());
                        }
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                    dismiss();
                }
                break;
            case REQ_LOAD_MORE:
            case -1:
                try {
                    String response = (String) object2;
                    CustomLog.e("repsonse", "" + response);
                    if (response != null) {
                        ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                        if (err.isSuccess()) {

                            CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                            result = resp.getResult();
                            wasListEmpty = searchList.size() == 0;
                            if (null != result.getBlogs()) {
                                searchList.addAll(result.convertBlogToSearchVo());
                            }
                            if (null != result.getNewsList()) {
                                searchList.addAll(result.convertNewsToSearchVo());
                            }
                            if (null != result.getVideos()) {
                                searchList.addAll(result.convertVideoToSearchVo());
                            }
                            if (null != result.getAlbums()) {
                                searchList.addAll(result.convertAlbumToSearchVo());
                            }


                            updateUI(object1);
                        } else {
                            Util.showSnackbar(v, err.getErrorMessage());
                        }
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                    dismiss();
                }
                break;
            case -2:
                try {
                    String response = (String) object2;
                    CustomLog.e("repsonse", "" + response);
                    if (response != null) {
                        ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                        if (err.isSuccess()) {
                            Util.showToast(context, new JSONObject(response).optJSONObject("result").optString("success_message"));
                            dismiss();
                        } else {
                            Util.showSnackbar(v, err.getErrorMessage());
                            v.findViewById(R.id.pbMain).setVisibility(View.GONE);
                            v.findViewById(R.id.rvParent).setVisibility(View.VISIBLE);

                        }
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                    dismiss();
                }
                break;
            case Constant.Events.MUSIC_MAIN:
                try {
                    switch (contestType) {
                        case "2":
                        case "4":
                            if (Boolean.parseBoolean("" + object2)) {
                                dismiss();
                                listener.onItemClicked(Constant.Events.POPUP, childList.get(postion), -1);
                            } else {
                                rvParent.setVisibility(View.GONE);
                                setChildRecycleView();
                                albumId = searchList.get(postion).getId();
                                callChildApi(-3, 1);
                            }
                            break;
                        default:
                            dismiss();
                            listener.onItemClicked(Constant.Events.POPUP, searchList.get(postion), -1);
                            break;
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }

                break;

            case Constant.Events.LOAD_MORE:
                try {
                    if (isLoading) break;
                    if (Boolean.parseBoolean("" + object2)) {
                        if ("2".equals(contestType) && resultChild1 != null) {
                            if (resultChild1.getCurrentPage() < resultChild1.getTotalPage()) {
                                callChildApi(REQ_LOAD_MORE_CHILD, resultChild1.getNextPage());
                            }
                        } else if (resultChild2 != null) {
                            if (resultChild2.getCurrentPage() < resultChild2.getTotalPage()) {
                                callChildApi(REQ_LOAD_MORE_CHILD, resultChild2.getNextPage());
                            }
                        }
                    } else if (result != null) {
                        if (result.getCurrentPage() < result.getTotalPage()) {
                            callApi(REQ_LOAD_MORE, result.getNextPage());
                        }
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;
            default:
                // optionList.get(postion).setValue(("" + object2).equals("1") ? "0" : "1");
                // adapterFeed.notifyItemChanged(postion);
                break;
        }
        return false;
    }

    public void runLayoutAnimation(final RecyclerView recyclerView) {
        if (wasListEmpty) {
            final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.anim_fall_down);
            recyclerView.setLayoutAnimation(controller);
            recyclerView.scheduleLayoutAnimation();
        }
    }


    private void updateUI(int reqCode) {
        try {
            v.findViewById(R.id.pbMain).setVisibility(View.GONE);
            v.findViewById(R.id.pb).setVisibility(View.GONE);

            rvParent.setVisibility(searchList.size() > 0 ? View.VISIBLE : View.GONE);
            adapter.notifyDataSetChanged();
            if (searchList.size() > 0) {
                runLayoutAnimation(rvParent);
            }
            v.findViewById(R.id.llNoData).setVisibility(searchList.size() > 0 ? View.GONE : View.VISIBLE);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateChildUI(int reqCode) {
        try {
            v.findViewById(R.id.pbMain).setVisibility(View.GONE);
            v.findViewById(R.id.pb).setVisibility(View.GONE);
            v.findViewById(R.id.ivBack).setVisibility(View.VISIBLE);
            rvParent.setVisibility(View.GONE);
            adapterChild.notifyDataSetChanged();
            if (childList.size() > 0) {
                runLayoutAnimation(rvChild);
            }
            rvChild.setVisibility(childList.size() > 0 ? View.VISIBLE : View.GONE);

            v.findViewById(R.id.llNoData).setVisibility(childList.size() > 0 ? View.GONE : View.VISIBLE);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                view.setVisibility(View.GONE);
                rvChild.setVisibility(View.GONE);
                v.findViewById(R.id.llNoData).setVisibility(View.GONE);
                rvParent.setVisibility(View.VISIBLE);
                break;

                /*Map<String, Object> req = new HashMap<>();
                for (Options opt : optionList) {
                    if ("1".equals(opt.getValue()))
                        req.put("users[" + opt.getName() + "]", opt.getName());
                }
                v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
                v.findViewById(R.id.rvFeedUpdate).setVisibility(View.GONE);
                v.findViewById(R.id.rlBottom).setVisibility(View.GONE);
                new ApiController(url, req, context, this, -2).execute();*/

        }
    }


}
