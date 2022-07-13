package com.sesolutions.ui.music_album;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;
import java.util.Map;

import me.riddhimanadib.formmaster.model.FormElementMusicFile;

public class FormFragment extends FormHelper implements View.OnClickListener {
    private AppCompatTextView tvTitle;
    public boolean loadWhenVisible = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_signup, container, false);
        try {
            applyTheme(v);
            if (loadWhenVisible) {
                init();
                callSignUpApi();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void initScreenData() {
        init();
        this.callSignUpApi();
    }


    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        tvTitle = v.findViewById(R.id.tvTitle);
        setTitle();
        mRecyclerView = v.findViewById(R.id.recyclerView);
    }

    private void setTitle() {
        int title = R.string.EMPTY;
        switch (FORM_TYPE) {
            case Constant.FormType.EDIT_ALBUM:
            case Constant.FormType.EDIT_ALBUM_OTHERS:
                title = R.string.TITLE_EDIT_ALBUM;
                break;
            case Constant.FormType.EDIT_QUOTE:
                title = R.string.TITLE_EDIT_QUOTE;
                break;
            case Constant.FormType.POINT_PURCHASE:
                title = R.string.send_point_friend;
                break;
            case Constant.FormType.EDIT_PRAYER:
                title = R.string.TITLE_EDIT_PRAYER;
                break;
            case Constant.FormType.EDIT_WISH:
                title = R.string.title_edit_wish;
                break;
            case Constant.FormType.STORY_ARCHIVE:
                title = R.string.title_story_setting;
                break;
            case Constant.FormType.REPLY_TOPIC:
            case Constant.FormType.REPLY_FORUM_TOPIC:
                title = R.string.reply;
                break;
            case Constant.FormType.CREATE_WISH:
                title = R.string.title_create_wish;
                break;
            case Constant.FormType.RESET_PASSWORD:
                title = R.string.title_reset_password;
                break;
            case Constant.FormType.EDIT_THOUGHT:
                title = R.string.TITLE_EDIT_THOUGHT;
                break;
            case Constant.FormType.CREATE_REVIEW:
                title = R.string.add_review;
                break;
            case Constant.FormType.EDIT_REVIEW:
                title = R.string.edit_review;
                break;
            case Constant.FormType.EDIT_MUSIC_ALBUM:
                title = R.string.TITLE_EDIT_MUSIC_ALBUM;
                break;
            case Constant.FormType.KEY_EDIT_LECTURE:
                title = R.string.EDIT_LECTURE;
                break;
            case Constant.FormType.EDIT_ANNOUNCEMENT:
            case Constant.FormType.CREATE_ANNOUNCEMENT:
                title = R.string.crowdfunding_announcement;
                break;
            case Constant.FormType.EDIT_MUSIC:
                title = R.string.edit_song;
                break;
            case Constant.FormType.CREATE_DISCUSSTION:
                title = R.string.post_discussion;
                break;
            case Constant.FormType.CREATE_FORUM_TOPIC:
                title = R.string.post_topic;
                break;
            case Constant.FormType.EDIT_TOPIC:
                title = R.string.TITLE_EDIT_POST;
                break;
            case Constant.FormType.RENAME_FORUM_TOPIC:
                title = R.string.rename_topic;
                break;
            case Constant.FormType.MOVE_FORUM_TOPIC:
                title = R.string.move_topic;
                break;
            case Constant.FormType.QUOTE_POST:
                title = R.string.post_quote;
                break;
            case Constant.FormType.CREATE_TEST:
                title = R.string.CREATE_TEST;
                break;
            case Constant.FormType.CREATE_COURSE:
                title = R.string.CREATE_COURSE;
                break;
            case Constant.FormType.CREATE_LECTURE:
                title = R.string.CREATE_LECTURE;
                break;
            case Constant.FormType.BECOME_PROFESSIONAL:
                title = R.string.BECOME_PROFESSIONAL;
                break;
            case Constant.FormType.CREATE_CLASSROOM:
                title = R.string.CREATE_CLASSROOM;
                break;
            case Constant.FormType.EDIT_ANSWER:
                title = R.string.edit_answer;
                break;
            case Constant.FormType.CREATE_MUSIC:
                title = R.string.add_songs;
                break;
            case Constant.FormType.FILTER_MUSIC_PLAYLIST:
                title = R.string.TXT_SERACH_PLAYLIST;
                break;
            case Constant.FormType.ADD_EVENT_LIST:
                title = R.string.add_event_list;
                break;
            case Constant.FormType.INVITE:
                title = R.string.invite;
                break;
            case Constant.FormType.EDIT_MUSIC_PLAYLIST:
            case Constant.FormType.TYPE_PLAYLIST:
            case Constant.FormType.TYPE_PLAYLIST_MUSIC:
            case Constant.FormType.TYPE_PLAYLIST_VIDEO:
                title = R.string.TITLE_EDIT_PLAYLIST;
                break;
            case Constant.FormType.TYPE_SONGS:
                title = R.string.TITLE_EDIT_SONG;
                break;
            case Constant.FormType.TYPE_ADD_SONG:
                title = R.string.TITLE_ADD_SONG;
                break;
            case Constant.FormType.PAGE_CONTACT:
                title = R.string.contact;
                break;
            case Constant.FormType.TYPE_ADD_ALBUM:
                title = R.string.TITLE_ADD_ALBUM;
                break;
            case Constant.FormType.FILTER_VIDEO: {
                if (url.equalsIgnoreCase(Constant.URL_CHANNEL_SEARCH_FORM))
                    title = R.string.TITLE_FILTER_CHANNEL;
                else if (url.equalsIgnoreCase(Constant.URL_FILTER_VIDEO_PLAYLIST))
                    title = R.string.TITLE_FILTER_PLAYLIST;
                else
                    title = R.string.TITLE_FILTER_VIDEO;
            }
            break;
            case Constant.FormType.FILTER_PAGE_REVIEW:
            case Constant.FormType.FILTER_GROUP_REVIEW:
            case Constant.FormType.FILTER_BUSINESS_REVIEW:
            case Constant.FormType.FILTER_STORE:
                title = R.string.TITLE_FILTER_STORE;
                break;
            case Constant.FormType.FILTER_QA:
                title = R.string.TITLE_FILTER_QA;
                break;
            case Constant.FormType.FILTER_PAGE:
                title = R.string.TITLE_FILTER_PAGE;
                break;
            case Constant.FormType.FILTER_PAGE_POLL:
                title = R.string.TITLE_FILTER_PAGE_POLL;
                break;
            case Constant.FormType.FILTER_PRODUCT:
                title = R.string.TITLE_FILTER_PRODUCT;
                break;
            case Constant.FormType.FILTER_CORE:
            case Constant.FormType.FILTER_BLOG:
            case Constant.FormType.FILTER_CLASSIFIED:
            case Constant.FormType.FILTER_GROUP:
            case Constant.FormType.FILTER_ARTICLE:
            case Constant.FormType.FILTER_MEMBER:
            case Constant.FormType.FILTER_QUOTE:
            case Constant.FormType.FILTER_PRAYER:
            case Constant.FormType.FILTER_THOUGHT:
            case Constant.FormType.FILTER_WISH:
            case Constant.FormType.FILTER_EVENT:
            case Constant.FormType.FILTER_POLL:
            case Constant.FormType.FILTER_BUSINESS:
            case Constant.FormType.FILTER_MUSIC_ALBUM:
            case Constant.FormType.FILTER_MUSIC_SONG:
                title = (R.string.TITLE_FILTER_SEARCH);
                break;
            case Constant.FormType.ADD_CHANNEL:
                title = (R.string.TITLE_ADD_CHANNEL);
                break;
            case Constant.FormType.TYPE_EDIT_CHANNEL:
                title = (R.string.TITLE_EDIT_CHANNEL);
                break;
            case Constant.FormType.KEY_EDIT_VIDEO:
                title = (R.string.TITLE_EDIT_VIDEO);
                break;
            case Constant.FormType.ADD_VIDEO:
                title = (R.string.TITLE_ADD_VIDEO);
                break;
            case Constant.FormType.CREATE_VIDEO:
                title = (R.string.TITLE_ADD_NEW_VIDEO);
                break;
            case Constant.FormType.TYPE_BLOG_EDIT:
                title = (R.string.TITLE_EDIT_BLOG);
                break;
            case Constant.FormType.TYPE_NEWS_EDIT:
                title = (R.string.TITLE_EDIT_NEWS);
                break;
            case Constant.FormType.TYPE_RSS_EDIT:
                title = (R.string.TITLE_EDIT_RSS);
                break;

            case Constant.FormType.TYPE_RECIPE_EDIT:
                title = (R.string.TITLE_EDIT_RECIPE);
                break;

            case Constant.FormType.EDIT_GROUP:
                title = (R.string.title_edit_group);
                break;
            case Constant.FormType.CREATE_GROUP:
                title = (R.string.title_create_group);
                break;
            case Constant.FormType.EDIT_CLASSIFIED:
                title = (R.string.title_edit_classified);
                break;
            case Constant.FormType.EDIT_CORE_POLL:
                title = (R.string.title_edit_core_poll);
                break;
            case Constant.FormType.TYPE_ARTICLE_EDIT:
                title = (R.string.TITLE_EDIT_ARTICLE);
                break;
            case Constant.FormType.FILTER_ALBUM:
                title = (R.string.TITLE_ALBUM_SEARCH);
                break;
            case Constant.FormType.FILTER_PHOTO:
                title = (R.string.TXT_SERACH_PHOTO);
                break;
            case Constant.FormType.EDIT_ALBUM_SETTING:
                title = (R.string.TITLE_EDIT_ALBUM_SETTING);
                break;
            case Constant.FormType.AWARD:
                title = R.string.change_award;
                break;
            case Constant.FormType.RULES:
                title = R.string.rules;
                break;
            case Constant.FormType.EDIT_ENTRY:
                title = R.string.edit_entry;
                break;
            case Constant.FormType.EDIT_CONTACT:
                title = R.string.information;
                break;
            case Constant.FormType.OVERVIEW:
                title = R.string.change_overview;
                break;
            case Constant.FormType.SEO:
                title = R.string.add_seo;
                break;
            case Constant.FormType.EDIT_HOST:
            case Constant.FormType.EDIT_USER:
                title = (R.string.TITLE_EDIT_PROFILE);
                break;
        }
        tvTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callSignUpApi() {

        if (isNetworkAvailable(context)) {
            showBaseLoader(false);
            try {

                HttpRequestVO request = new HttpRequestVO(url);
                if (null != map) {
                    request.params.putAll(map);
                }
                if (FORM_TYPE == Constant.FormType.EDIT_USER) {
                    request.params.put(Constant.KEY_GET_FORM, "fields");
                } else {
                    request.params.put(Constant.KEY_GET_FORM, 1);
                }
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (err.isSuccess()) {
                                    Dummy vo = new Gson().fromJson(response, Dummy.class);
                                    createFormUi(vo.getResult());
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            } else {
                                somethingWrongMsg(v);
                            }
                        } catch (Exception e) {
                            somethingWrongMsg(v);
                            CustomLog.e(e);
                        }
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        } else {
            notInternetMsg(v);
        }
    }

    @Override
    public void onResponseSuccess(int reqCode, Object result) {


        if (reqCode == REQ_CODE_MUSIC) {
            try {
                if (null != result) {
                    //  String filePath = ((List<String>) result).get(0);
                    FormElementMusicFile element = ((FormElementMusicFile) mFormBuilder.getFormElement(clickedFilePostion));
                    element.setMusicList((List<String>) result);
                    mFormBuilder.getAdapter().notifyDataSetChanged();
                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }else
        {
            if (null != result) {
                String filePath = ((List<String>) result).get(0);
                mFormBuilder.getAdapter().setValueAtTag(clickedFilePostion, filePath);
            }

        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }

    public static FormFragment newInstance(int editAlbum, Map<String, Object> map, String url, int albumId) {
        FormFragment fragment = new FormFragment();
        fragment.FORM_TYPE = editAlbum;
        fragment.url = url;
        fragment.map = map;
        if (albumId == -1) {
            fragment.loadWhenVisible = false;
        }
        return fragment;
    }

    public static FormFragment newInstance(int editAlbum, Map<String, Object> map, String url, int albumId, OnUserClickedListener<Integer, Object> listener) {
        FormFragment fragment = new FormFragment();
        fragment.FORM_TYPE = editAlbum;
        fragment.url = url;
        fragment.map = map;
        if (albumId == -1) {
            fragment.loadWhenVisible = false;
        }
        fragment.listener = listener;
        return fragment;
    }

    public static FormFragment newInstance(int editAlbum, Map<String, Object> map, String url) {
        return newInstance(editAlbum, map, url, 0);
    }

    public static FormFragment newInstance(int editAlbum, Map<String, Object> map, String url,OnUserClickedListener<Integer, Object> listener) {
        return newInstance(editAlbum, map, url, 0,listener);
    }


}
