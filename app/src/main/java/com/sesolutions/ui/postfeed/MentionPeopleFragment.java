package com.sesolutions.ui.postfeed;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Friends;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.member.SomeDrawable;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class MentionPeopleFragment extends BaseFragment implements View.OnClickListener, TextWatcher, OnUserClickedListener<Integer, String> {

    private View v;
    //private HashMap<Integer, Friends> selectedMap;

    //private FlowLayout flowLayout;
    private HttpRequestHandler requestHandler;
    private AppCompatEditText etSearch;
    private boolean isForMention;
    private RecyclerView rvTag;
    private TagSuggestionAdapter adapter;
    private List<Friends> friendList;
    private String searchKey;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_mention_people, container, false);
        try {
            applyTheme(v);
            init();
            initRecycleView();
            etSearch.requestFocus();
            etSearch.setFocusable(true);
            etSearch.setEnabled(true);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    etSearch.setHint("");
                    etSearch.setText(searchKey);
                   // openKeyboard();
                   // etSearch.requestFocus();
                    requestFocus(etSearch,activity);
                }
            }, 500);

            // openKeyboard();
            // etSearch.requestFocus();
           // requestFocus(etSearch,activity);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }




    void requestFocus(View editText, Activity activity)
    {
        try {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initRecycleView() {
        try {
            friendList = new ArrayList<>();
            rvTag.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            rvTag.setLayoutManager(layoutManager);
            adapter = new TagSuggestionAdapter(friendList, context, this);
            rvTag.setAdapter(adapter);
            //  rvAttach1.setNestedScrollingEnabled(false);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void init() {

        try {
            etSearch = v.findViewById(R.id.etSearch);
            try {
                SomeDrawable drawable21 = new SomeDrawable(Color.parseColor(Constant.text_color_1),Color.parseColor(Constant.text_color_1),Color.parseColor(Constant.text_color_1),1,Color.parseColor(Constant.text_color_1),0);
                etSearch.setBackgroundDrawable(drawable21);
                etSearch.setTextColor(Color.parseColor(Constant.backgroundColor));
                etSearch.setHintTextColor(Color.parseColor(Constant.backgroundColor));
                 }catch (Exception ex){
                ex.printStackTrace();
            }

            etSearch.addTextChangedListener(this);
            rvTag = v.findViewById(R.id.rvTag);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (null != requestHandler && !requestHandler.isCancelled()) {
            requestHandler.cancel(true);
        }
        callSuggestionApi("" + s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    (activity).taskPerformed = Constant.TASK_NOMENTION;
                    onBackPressedtag();
                    break;
                case R.id.icDone:
                    onBackPressedtag();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onBackPressed() {
        (activity).taskPerformed = Constant.TASK_NOMENTION;
        onBackPressedtag();
    }

    public void onBackPressedtag() {
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


    private void callSuggestionApi(String value) {
        if (TextUtils.isEmpty(value)) {
            if (null != friendList)
                friendList.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_SUGGEST);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_VALUE, value);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {

                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (null != response) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (null != friendList)
                                        friendList.clear();
                                    List<Friends> list = resp.getResult().getFriends();
                                    if (null != list && list.size() > 0) {
                                        friendList.addAll(list);
                                    }
                                    // adapter3.notifyDataSetChanged();
                                    // initAdapter();
                                    adapter.notifyDataSetChanged();
                                }

                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    requestHandler = new HttpRequestHandler(activity, new Handler(callback));
                    requestHandler.execute(request);

                } catch (Exception e) {

                }

            } else {
                // Util.showSnackbar(drawerLayout, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    /* @Override
     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         CustomLog.e("stri2", "" + parent.getAdapter().getItem(position));
         // createChip(mapFriend.get(parent.getAdapter().getItem(position)));
         etSearch.setText(Constant.EMPTY);
         (activity).taskPerformed = Constant.TASK_MENTION;
         (activity).setFreinds(mapFriend.get(parent.getAdapter().getItem(position)));
         onBackPressed();
     }
 */
    @Override
    public boolean onItemClicked(Integer object1, String object2, int position) {
        try {
            // etSearch.setText(Constant.EMPTY);
            (activity).taskPerformed = Constant.TASK_MENTION;
            (activity).setFreinds(friendList.get(position));
            // onBackPressed();
            fragmentManager.popBackStack();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    public static MentionPeopleFragment newInstance(String sequence) {
        MentionPeopleFragment frag = new MentionPeopleFragment();
        frag.searchKey = sequence;
        return frag;
    }



   /* public static Fragment newInstance(boolean isForMention) {
        TagPeopleFragment frag = new TagPeopleFragment();
        frag.isForMention = isForMention;
        return frag;
    }*/
}
