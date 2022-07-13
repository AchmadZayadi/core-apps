package com.sesolutions.ui.dashboard;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.feed.Activity;
import com.sesolutions.responses.feed.ActivityType;
import com.sesolutions.responses.feed.Mention;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.postfeed.MentionPeopleFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Typeface.BOLD;


public class PostEditFragment extends BaseFragment implements View.OnClickListener, TextWatcher {

    private View v;
    private List<Friends> friendList;
    private int bodyLength;

    private int index;
    private AppCompatEditText etBody;
    private int mentionStart;
    private int mentionEnd;
    private boolean selectorShown;
    private String body;
    private Activity vo;
    private int selectedIndex;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_post_edit, container, false);
        try {
            applyTheme(v);
            (activity).activity = Constant.ACTIVITY;
            vo = Constant.ACTIVITY;
            Constant.ACTIVITY = null;
            init();
            // (activity).taskPerformed = Constant.TASK_MENTION;
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void refreshText() {
        String body = null;
        SpannableString span = null;
        // List<CustomClickableSpan> spanList = new ArrayList<>();
        if (vo.getActivityType() != null) {
            List<ActivityType> actTypelIst = vo.getActivityType();
            if (actTypelIst.size() > 0) {
                for (ActivityType type : actTypelIst) {
                    if (type.getKey().equals(Constant.KEY_SPECIAL_BODY)) {
                        //body = StringEscapeUtils.unescapeHtml4(type.getValue());
                        body=unecodeStr(type.getValue());
                        try {
                            if (vo.getMention() != null) {
                                List<Mention> mentionList = vo.getMention();
                                List<Mention> list2 = new ArrayList<>();
                                for (Mention men : mentionList) {
                                    body = body.replace(men.getWord(), men.getTitle());
                                    int startMention = body.indexOf(men.getTitle());
                                    int endMention = men.getTitle().length();
                                    men.setStartIndex(startMention);
                                    men.setEndIndex(startMention + endMention);
                                    list2.add(men);
                                }
                                CustomLog.e("body", body);
                                span = new SpannableString(body);
                                for (final Mention men : list2) {
                                    Friends fr = new Friends();
                                    fr.setUserId(men.getUserId());
                                    fr.setId(men.getUserId());
                                    fr.setStartIndex(men.getStartIndex());
                                    fr.setEndIndex(men.getEndIndex());
                                    fr.setLabel(men.getTitle());
                                    friendList.add(fr);
                                    // body = body.replace(men.getWord(), men.getTitle());
                                    if (men.getStartIndex() > -1) {
                                        span.setSpan(new StyleSpan(BOLD), men.getStartIndex(), men.getEndIndex(), 0);
                                    }
                                }
                            }

                            if (span == null) {
                                span = new SpannableString(body);
                            }

                            if (vo.getHashTags() != null) {
                                List<String> hashList = vo.getHashTags();

                                CustomLog.e("body", body);

                                for (final String men : hashList) {
                                    int startMention = body.indexOf(men);
                                    int endMention = startMention + men.length();
                                    // body = body.replace(men.getWord(), men.getTitle());
                                    if (startMention > -1) {
                                        span.setSpan(new StyleSpan(BOLD), startMention, endMention, 0);
                                    }
                                }
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        break;
                    }
                }
            }
        }


        if (!TextUtils.isEmpty(body)) {
            etBody.setText(span);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateBodyText();
    }

    private void updateBodyText() {
        try {
            int task = (activity).taskPerformed;
            if (task == Constant.TASK_MENTION) {
                (activity).taskPerformed = 0;
                String body = etBody.getText().toString();
                if (!TextUtils.isEmpty(body)) {
                    Friends vo = activity.getFreinds();

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
                        body = body.substring(0, body.length() - 1); //deleting "@"from last
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

                    etBody.setText(getSpan(body));

                    final int len = body.length();
                    new Handler().postDelayed(() -> {
                        try {
                            etBody.setSelection(len);
                            openKeyboard();
                            etBody.requestFocus();
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                    }, 100);

                }
            } else if (task == Constant.TASK_MENTION_CANCEL) {
                etBody.setSelection(etBody.getText().length());
                openKeyboard();
                etBody.requestFocus();
            }

            etBody.addTextChangedListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
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
                            i++; // case for if hash is last word and there is no
                            // space after word
                        }


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


    private String unecodeStr(String escapedString) {
        try {
            return StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeJava(escapedString));
        } catch (Exception e) {
            CustomLog.d("warnning", "emoji parsing error at " + escapedString);
        }

        return escapedString;
    }


    private void init() {
        friendList = new ArrayList<>();
        ((TextView) v.findViewById(R.id.tvTitle)).setText(Constant.TITLE_EDIT_POST);
        etBody = v.findViewById(R.id.etPost);
        etBody.setText(unecodeStr(body));
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.tvDone).setOnClickListener(this);
        mentionStart = 0;
        mentionEnd = 0;
        refreshText();
        /*etBody.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if (src.equals("")) { // for backspace
                            return src;
                        }
                        if (src.toString().matches("[\\x00-\\x7F]+")) {
                            return " " + src;
                        }
                        return src;
                    }
                }
        });*/
    }

    @Override
    public void afterTextChanged(Editable s) {

     /*   try {
            etBody.removeTextChangedListener(this);

//            etBody.setText(getSpan(s.toString()));
            etBody.setSelection(index + 1);
            etBody.addTextChangedListener(this);

        } catch (Exception e) {
            CustomLog.e("PostFeed", "IndexOutofbound _index" + index + "__s.length()" + s.length());
            etBody.setSelection(s.length());
            etBody.addTextChangedListener(this);
        }*/
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        CustomLog.e("beforeTextChanged", s + " start__:" + start + " before__:" + after + " count__:" + count);
        bodyLength = s.length();


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            CustomLog.e("onTextChanged", s + " start__:" + start + " before__:" + before + " count__:" + count);
            //current index
            index = start + before;
            selectedIndex = etBody.getSelectionStart();
            String mention = getMentioningSequence(s, start, count);
            if (null != mention) {
                checkIfMentioning(mention, index);
            } else {
                int remove = -1;

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
                        CustomLog.e("friendList", "__index=" + i
                                + "__" + friendList.get(i).getStartIndex()
                                + "," + friendList.get(i).getEndIndex());
                    }
                }
                if (remove > -1) {
                    friendList.remove(remove);
                    etBody.setText(getSpan(unecodeStr(s.toString())));
                    etBody.setSelection(index);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void checkIfMentioning(String mentionSequence, int index) {
        if (mentionSequence != null) {
            if (!selectorShown) {
                OnMentionStarted(mentionSequence);
            }
        }
        if (mentionSequence == null && selectorShown) {
            OnMentionFinished();
        }
    }

    private void OnMentionStarted(String sequence) {
        try {
            CustomLog.e("start", sequence);
            (activity).taskPerformed = 0;
            etBody.removeTextChangedListener(this);
            // String s = etBody.getText().toString();
            //   etBody.setText(s.substring(0, s.length() - 1));
            //  etBody.setText(textBeforeMention);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, MentionPeopleFragment.newInstance(sequence)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void OnMentionFinished() {
        CustomLog.e("finish", "finish");
    }

    private String getMentioningSequence(CharSequence s, int start, int count) {


        Pattern pattern = Pattern.compile("(?<=\\s|^)@([a-z|A-Z|\\.|\\-|\\_|0-9]*)(?=\\s|$)");
        Matcher matcher = pattern.matcher(s.toString());
        String mention = null;
        while (matcher.find()) {
            if (matcher.start(1) <= start + count &&
                    start + count <= matcher.end(1)
            ) {
                mentionStart = matcher.start(1);
                mentionEnd = matcher.end(1);
                mention = matcher.group(1);
                break;
            }
        }
        return mention;
    }

    private void callPostSubmitApi(Map<String, Object> params) {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {
                    showBaseLoader(false);
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_EDIT_FEED);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.putAll(params);
                    // request.params.put(Constant.KEY_C_TYPE, Constant.VALUE_C_TYPE);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;



                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);

                            if (null != response) {
                                BaseResponse<Object> resp = new Gson().fromJson(response.toString(), BaseResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    Constant.TASK_POST_EDIT = true;
                                    onBackPressed();
                                } else {
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                }

                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    };
                    // requestHandler = new HttpRequestHandler(this, new Handler(callback));
                    //requestHandler.execute(request);
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }
    }

    private void sendPost() {
        try {

            if (TextUtils.isEmpty(etBody.getText().toString())) {
                Util.showSnackbar(v, Constant.MSG_EMPTY_POST);
                return;
            }


            Map<String, Object> params = new HashMap<>();
            String body = etBody.getText().toString();
            // String body = StringEscapeUtils.escapeHtml4(etBody.getText().toString());


            if (!TextUtils.isEmpty(body)) {
                //check if user someone,
                // If yes then rplace name with "@_user_id"
                if (friendList.size() > 0) {
                    //  Collections.reverse(friendList);
                    for (Friends vo : friendList) {
                        body = body.substring(0, vo.getStartIndex()) + "@_user_" + vo.getId() + " " + body.substring(vo.getEndIndex());
                    }
                    CustomLog.e("body", body);
                }

                params.put("body", body);
            }

            params.put(Constant.KEY_ACTIVITY_ID, vo.getActionId());
            //params.put("debug", 1);
            callPostSubmitApi(params);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.tvDone:
                    sendPost();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static PostEditFragment newInstance(String body) {
        PostEditFragment frag = new PostEditFragment();
        frag.body = body;
        return frag;
    }
}
