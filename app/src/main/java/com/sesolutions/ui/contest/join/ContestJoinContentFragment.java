package com.sesolutions.ui.contest.join;


import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.ui.customviews.NestedWebView;
import com.sesolutions.ui.dashboard.ApiHelper;
import com.sesolutions.ui.editor.EditorExampleActivity;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContestJoinContentFragment extends ApiHelper implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private static final int REQ_EDITOR = 100;
    private View v;
    private OnUserClickedListener<Integer, Object> listener;
    private List<Dummy.Formfields> contentUpload;
    Map<String, Dummy.Formfields> map;
    private String contestType;
    private NestedWebView wb;
    private SearchVo searchVo;
    private String enabledList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterTransition(new Fade(Fade.IN));
            setExitTransition(new Fade(Fade.OUT));
        }
    }


    public static ContestJoinContentFragment newInstance(List<Dummy.Formfields> contentUpload, OnUserClickedListener<Integer, Object> listener, String type, SearchVo vo, String enabledList) {
        ContestJoinContentFragment fragment = new ContestJoinContentFragment();
        fragment.listener = listener;
        fragment.contentUpload = contentUpload;
        fragment.contestType = type;
        fragment.searchVo = vo;
        fragment.enabledList = enabledList;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_contest_content_upload, container, false);
        try {
            applyTheme(v);
            MAX_COUNT = 1;
            initScreenData();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void initScreenData() {
        init();
        setUIData();
    }

    private void init() {
        map = new HashMap<>();
        for (Dummy.Formfields fld : contentUpload) {
            map.put(fld.getName(), fld);
        }

        v.findViewById(R.id.rlMedia).setOnClickListener(this);
        v.findViewById(R.id.ivCancel).setOnClickListener(this);
        v.findViewById(R.id.cvCancel).setOnClickListener(this);
        v.findViewById(R.id.cvLink).setOnClickListener(this);
        v.findViewById(R.id.cvPost).setOnClickListener(this);
        wb = v.findViewById(R.id.wbWrite);
        wb.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.cvPost:
                case R.id.rlMedia:
                    switch (contestType) {
                        case "4":
                            if (isOptionEnabled("uploadmusic"))
                                showAudioChooser(false);
                            break;
                        case "1":
                            startEditorActivity();
                            break;
                        case "3":
                            showImageDialog(getString(R.string.MSG_SELECT_IMAGE_SOURCE), isOptionEnabled("record"), isOptionEnabled("uploadvideo"));

                            break;
                        default:
//                            showImageDialog(getString(R.string.MSG_SELECT_IMAGE_SOURCE), isOptionEnabled("capture"), isOptionEnabled("uploadphoto"));
                            askForPermission(Manifest.permission.CAMERA);
                            break;
                    }
                    break;

                /*//cancel attached music file
                case R.id.cvCancel:
                    map.get("sescontest_audio_file").setStringValue("");
                    searchVo = null;
                    updateAudioView();
                    break;*/
                //cancel attached photo or video file
                case R.id.cvCancel:
                case R.id.ivCancel:
                    if ("2".equals(contestType)) {
                        map.get("photo").setStringValue("");
                        map.get("sescontest_link_id").setStringValue("");
                        searchVo = null;
                        updatePhotoView();
                    } else if ("3".equals(contestType)) {
                        map.get("video").setStringValue("");
                        map.get("sescontest_link_id").setStringValue("");
                        searchVo = null;
                        updateVideoView();
                    } else if ("4".equals(contestType)) {
                        map.get("sescontest_audio_file").setStringValue("");
                        map.get("sescontest_link_id").setStringValue("");
                        searchVo = null;
                        updateAudioView();
                    }
                    break;

                case R.id.cvLink:
                    ContentLinkDialogFragment.newInstance(this, contestType).show(fragmentManager, "social");
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void setUIData() {

        switch (contestType) {
            case "1":
                v.findViewById(R.id.llTextContent).setVisibility(View.VISIBLE);
                if (isOptionEnabled("write") && map.containsKey("contest_description")) {
                    View view = v.findViewById(R.id.cvPost);
                    view.setVisibility(View.VISIBLE);
                    ((ImageView) view.findViewById(R.id.ivPost)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cam_ic_edit));
                    ((TextView) view.findViewById(R.id.tvPost)).setText(R.string.txt_write);

                }
                if (isOptionEnabled("linkblog")) {
                    View view = v.findViewById(R.id.cvLink);
                    view.setVisibility(View.VISIBLE);
                    ((ImageView) view.findViewById(R.id.ivLink)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ses_link));
                    ((TextView) view.findViewById(R.id.tvLink)).setText(R.string.txt_link_my_blog);
                }
                updateTextView();

                break;
            case "2":
                v.findViewById(R.id.rlMedia).setVisibility(View.VISIBLE);
                if (!map.containsKey("photo")) {
                    Dummy.Formfields fld = new Dummy.Formfields();
                    fld.setType(Constant.FILE);
                    fld.setName("photo");
                    map.put("photo", fld);
                }

                if ((isOptionEnabled("capture") || isOptionEnabled("uploadphoto")) && map.containsKey("photo")) {

                    updatePhotoView();
                    View view = v.findViewById(R.id.cvPost);
                    view.setVisibility(View.VISIBLE);
                    ((ImageView) view.findViewById(R.id.ivPost)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ses_upload));
                    ((TextView) view.findViewById(R.id.tvPost)).setText(R.string.ses_upload);
                }

            /*if (map.containsKey("fromurl")) {
                View view = getLayoutInflater().inflate(R.layout.layout_image_option, (ViewGroup) llHeader, false);
                view.setVisibility(View.VISIBLE);
                ((ImageView) view.findViewById(R.id.ivPost)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ses_link));
                ((TextView) view.findViewById(R.id.tvPost)).setText(R.string.contest_from_url);
                view.setOnClickListener(this);
                llHeader.addView(view);
            }*/

                if (isOptionEnabled("linkphoto") && map.containsKey("sescontest_link_id")) {
                    View view = v.findViewById(R.id.cvLink);
                    view.setVisibility(View.VISIBLE);
                    ((ImageView) view.findViewById(R.id.ivLink)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.camera));
                    ((TextView) view.findViewById(R.id.tvLink)).setText(R.string.contest_link_my_photo);

                }
                break;

            case "3":

                super.isVideoSelected = true;

                updateVideoView();

                v.findViewById(R.id.rlMedia).setVisibility(View.VISIBLE);
                ((ImageView) v.findViewById(R.id.icMedia)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cam_ic_video));
                ((TextView) v.findViewById(R.id.tvMedia)).setText(R.string.txt_upload_a_video);

                if ((isOptionEnabled("uploadvideo") || isOptionEnabled("record")) && map.containsKey("video")) {
                    View view1 = v.findViewById(R.id.cvPost);
                    view1.setVisibility(View.VISIBLE);
                    ((ImageView) view1.findViewById(R.id.ivPost)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ses_upload));
                    ((TextView) view1.findViewById(R.id.tvPost)).setText(R.string.txt_upload_video);
                    // view1.setOnClickListener(view2 -> ((TextView) v.findViewById(R.id.tvMedia)).setText(R.string.txt_upload_a_video));
                }

                if (isOptionEnabled("linkvideo") && map.containsKey("sescontest_link_id")) {
                    View view = v.findViewById(R.id.cvLink);
                    view.setVisibility(View.VISIBLE);
                    ((ImageView) view.findViewById(R.id.ivLink)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ses_link));
                    ((TextView) view.findViewById(R.id.tvLink)).setText(R.string.txt_link_my_video);
                    //view.setOnClickListener(view2 -> ((TextView) v.findViewById(R.id.tvMedia)).setText(R.string.txt_select_a_video));
                    //  llHeader.addView(view);
                }

                break;
            case "4":
                updateAudioView();
                v.findViewById(R.id.rlMedia).setVisibility(View.VISIBLE);
                ((ImageView) v.findViewById(R.id.icMedia)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.music_player));
                ((TextView) v.findViewById(R.id.tvMedia)).setText(R.string.txt_upload_music);

                if (isOptionEnabled("uploadmusic")) {
                    View view1 = v.findViewById(R.id.cvPost);
                    view1.setVisibility(View.VISIBLE);
                    ((ImageView) view1.findViewById(R.id.ivPost)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ses_upload));
                    ((TextView) view1.findViewById(R.id.tvPost)).setText(R.string.txt_upload_audio);
                    //view1.setOnClickListener(view2 -> ((TextView) v.findViewById(R.id.tvMedia)).setText(R.string.txt_upload_a_video));

                }
                if (isOptionEnabled("linkmusic") && map.containsKey("sescontest_link_id")) {
                    View view = v.findViewById(R.id.cvLink);
                    view.setVisibility(View.VISIBLE);
                    ((ImageView) view.findViewById(R.id.ivLink)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ses_link));
                    ((TextView) view.findViewById(R.id.tvLink)).setText(R.string.txt_link_my_audio);
                    // view.setOnClickListener(view2 -> ((TextView) v.findViewById(R.id.tvMedia)).setText(R.string.txt_select_a_video));

                }
                break;
        }

        //set Button text
        if (map.containsKey("submit")) {
            listener.onItemClicked(Constant.Events.UPDATE_NEXT, map.get("submit").getLabel(), -1);
            listener.onItemClicked(Constant.Events.UPDATE_PREV, " ", -1);
            // ((AppCompatButton) v.findViewById(R.id.bSubmit)).setText(map.get("submit").getLabel());
        }
    }

    private boolean isOptionEnabled(String optionName) {
        return null != enabledList && enabledList.contains(optionName);
    }

    private void updateTextView() {

        try {
            Dummy.Formfields fld = map.get("contest_description");
            ((TextView) v.findViewById(R.id.tvWrite)).setText(fld.getLabel());
            ((TextView) v.findViewById(R.id.tvWriteDescription)).setText(fld.getDescription());
             wb.loadData(fld.getValue(), null, null);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        switch (reqCode) {

            case REQ_CODE_IMAGE:
                List<String> resultList = (List<String>) result;

                if (resultList != null && resultList.size() > 0) {
                    Dummy.Formfields fld = map.get("photo");
                    fld.setStringValue(resultList.get(0));
                    searchVo = null;
                    map.put("photo", fld);
                    updatePhotoView();
                }
                break;
            case REQ_CODE_VIDEO:

                resultList = ((List<String>) result);
                if (resultList != null && resultList.size() > 0) {
                    map.get("video").setStringValue(resultList.get(0));
                    /*if (canShowThumbnail) {
                        map.get("sescontest_video_file").setStringValue();
                    } else {
                        map.get("sescontest_video_file").setStringValue(resultList.get(0));
                    }*/
                    listener.onItemClicked(Constant.Events.ENTRY, null, -1);
                    searchVo = null;
                    updateVideoView();
                    Constant.videoUri = null;
                }
                break;
            case REQ_CODE_MUSIC:

                resultList = ((List<String>) result);
                if (resultList != null && resultList.size() > 0) {
                    map.get("sescontest_audio_file").setStringValue(resultList.get(0));
                    searchVo = null;
                    updateAudioView();
                    Constant.videoUri = null;
                }
                break;
        }

    }

    private void updatePhotoView() {
        Dummy.Formfields fld = map.get("photo");
        if (!TextUtils.isEmpty(map.get("photo").getValue())) {
            ((ImageView) v.findViewById(R.id.ivImage)).setImageDrawable(Drawable.createFromPath(fld.getValue()));
            v.findViewById(R.id.ivImage).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivCancel).setVisibility(View.VISIBLE);
        } else if (null != searchVo && !TextUtils.isEmpty(map.get("sescontest_link_id").getValue())) {
            Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivImage), searchVo.getImages(), context, R.drawable.placeholder_square);
            //   v.findViewById(R.id.ivMediaType).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivImage).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivCancel).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.ivImage).setVisibility(View.GONE);
            v.findViewById(R.id.ivCancel).setVisibility(View.GONE);
        }
    }

    private void updateAudioView() {
        Dummy.Formfields fld = map.get("sescontest_audio_file");
        if (fld.getValue()!=null && !TextUtils.isEmpty(fld.getValue())) {
            v.findViewById(R.id.rlMedia).setVisibility(View.GONE);
            v.findViewById(R.id.ivImage).setVisibility(View.GONE);
            v.findViewById(R.id.cvChip).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivMediaType).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.tvSongName)).setText(fld.getValue().substring(fld.getValue().lastIndexOf("/") + 1));
        } else if (fld.getValue()!=null && null != searchVo && !TextUtils.isEmpty(map.get("sescontest_link_id").getValue())) {
            Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivImage), searchVo.getImages(), context, R.drawable.placeholder_square);
            ((ImageView) v.findViewById(R.id.ivMediaType)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.music_player));
            v.findViewById(R.id.ivMediaType).setVisibility(View.VISIBLE);
            v.findViewById(R.id.rlMedia).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivImage).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivCancel).setVisibility(View.VISIBLE);
            v.findViewById(R.id.cvChip).setVisibility(View.GONE);
            v.findViewById(R.id.ivMediaType).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.rlMedia).setVisibility(View.VISIBLE);
            v.findViewById(R.id.cvChip).setVisibility(View.GONE);
            v.findViewById(R.id.ivImage).setVisibility(View.GONE);
            v.findViewById(R.id.ivCancel).setVisibility(View.GONE);
            v.findViewById(R.id.ivMediaType).setVisibility(View.GONE);
        }
    }

    private void updateVideoView() {

        try {
            if (!TextUtils.isEmpty(map.get("video").getValue())) {
                // if (canShowThumbnail)
                // ((ImageView) v.findViewById(R.id.ivImage)).setImageDrawable(Drawable.createFromPath(getThumbnailPathForLocalFile(activity, getImageContentUri(context, new File(fld.getValue())))));
                // else
                ((ImageView) v.findViewById(R.id.ivImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.placeholder_video));
                // ((ImageView) v.findViewById(R.id.ivImage)).setColorFilter(Color.parseColor(Constant.text_color_2));
                v.findViewById(R.id.ivMediaType).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivImage).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCancel).setVisibility(View.VISIBLE);
            } else if (null != searchVo && !TextUtils.isEmpty(map.get("sescontest_link_id").getValue())) {

                Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivImage), searchVo.getImages(), context, R.drawable.placeholder_square);

                v.findViewById(R.id.ivMediaType).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivImage).setVisibility(View.VISIBLE);
                v.findViewById(R.id.ivCancel).setVisibility(View.VISIBLE);
            } else {
                v.findViewById(R.id.ivMediaType).setVisibility(View.GONE);
                v.findViewById(R.id.ivImage).setVisibility(View.GONE);
                v.findViewById(R.id.ivCancel).setVisibility(View.GONE);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }

    public void startEditorActivity() {
        Intent intent = new Intent(context, EditorExampleActivity.class);
        Dummy.Formfields element = map.get("contest_description");
        String value = element.getValue();
        String title = element.getLabel();
        String description = element.getDescription();
        Bundle bundle = new Bundle();
        bundle.putString(EditorExampleActivity.TITLE_PARAM, title);
        bundle.putString(EditorExampleActivity.CONTENT_PARAM, value);
        bundle.putInt(Constant.TAG, 0);
        bundle.putString(EditorExampleActivity.TITLE_PLACEHOLDER_PARAM, title);
        bundle.putString(EditorExampleActivity.CONTENT_PLACEHOLDER_PARAM, description);
        bundle.putInt(EditorExampleActivity.EDITOR_PARAM, EditorExampleActivity.USE_NEW_EDITOR);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQ_EDITOR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case REQ_EDITOR:
                    if (resultCode == -1) {
                        if (data != null) {
                            CustomLog.e("desc", "not null");
                            String desc = data.getStringExtra(Constant.TEXT);
                            CustomLog.e("desc", desc);
                            Dummy.Formfields element = map.get("contest_description");
                            element.setStringValue(desc);
                            map.put("contest_description", element);
                            updateTextView();
                        } else {
                            CustomLog.e("desc", "null");
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.POPUP:
                switch (contestType) {
                    case "1":
                        Dummy.Formfields element = map.get("contest_description");
                        element.setStringValue(((SearchVo) object2).getDescription());
                        map.put("contest_description", element);
                        updateTextView();
                        break;
                    case "2":
                        map.get("photo").setStringValue("");
                        element = map.get("sescontest_link_id");
                        element.setStringValue(((SearchVo) object2).getFileId());
                        map.put("sescontest_link_id", element);
                        listener.onItemClicked(Constant.Events.ENTRY, object2, 1);
                        searchVo = (SearchVo) object2;
                        updatePhotoView();
                        break;
                    case "3":
                        map.get("video").setStringValue("");
                        element = map.get("sescontest_link_id");
                        element.setStringValue(((SearchVo) object2).getFileId());
                        map.put("sescontest_link_id", element);
                        listener.onItemClicked(Constant.Events.ENTRY, object2, 1);
                        searchVo = (SearchVo) object2;
                        updateVideoView();
                        break;
                    case "4":
                        map.get("sescontest_audio_file").setStringValue("");
                        element = map.get("sescontest_link_id");
                        element.setStringValue(((SearchVo) object2).getFileId());
                        map.put("sescontest_link_id", element);
                        listener.onItemClicked(Constant.Events.ENTRY, object2, 1);
                        searchVo = (SearchVo) object2;
                        updateAudioView();
                        break;

                }
                break;
        }
        return false;
    }

    public void onPrevClick() {
        listener.onItemClicked(Constant.Events.ACCEPT, map, -1);
    }

    public void onNextClick() {
          /*  Map<String, Dummy.Formfields> temp = new HashMap<>();
                    temp.put(map.get("contest_description"))*/
        listener.onItemClicked(Constant.Events.MUSIC_MAIN, map, -1);
    }
}
