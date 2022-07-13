package com.sesolutions.ui.postfeed;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.feed.Tagged;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FlowLayout;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SpanUtil;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TagPeopleFragment extends BaseFragment implements View.OnClickListener, TextWatcher, OnUserClickedListener<Integer, String> {

    private View v;

    EditText etSearch;

    // private String searchKey = "";
    // private CommonResponse.Result result;
    //private StickerAdapter adapter2;

    private ArrayAdapter<String> adapter3;
    private HashMap<Integer, Friends> selectedMap;

    private FlowLayout flowLayout;
    private HttpRequestHandler requestHandler;
    //private AutoCompleteTextView autoCompleteTextView;
    // private AppCompatTextView tvBadge;

    FlowLayout.LayoutParams params;
    private boolean isForMention;
    private TagSuggestionAdapter adapter;
    private List<Friends> friendList;
    private RecyclerView rvTag;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_tag_people, container, false);
        try {
            applyTheme(v);
            init();
            initRecycleView();

            selectedMap = new HashMap<>();

            params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 5, 5);

           /* etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        closeKeyboard();
                        searchKey = etSearch.getText().toString();
                        if (!TextUtils.isEmpty(searchKey)) {

                        }
                        return true;
                    }
                    return false;
                }
            });*/
            prefillTagged();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openKeyboard();
                    etSearch.requestFocus();
                }
            }, 150);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void prefillTagged() {
        try {
            if (activity.activity.getTagged() != null && activity.activity.getTagged().size() > 0) {
                List<Tagged> taggedList = activity.activity.getTagged();
                for (Tagged vo : taggedList) {
                     createChip(SpanUtil.convertTaggedToFriend(vo));
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
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

    @Override
    public void onStop() {
        closeKeyboard();
        super.onStop();
    }

    private void init() {
        flowLayout = v.findViewById(R.id.flowlayout);
        //  autoCompleteTextView = (AutoCompleteTextView) v.findViewById(R.id.searchAutoComplete);

        v.findViewById(R.id.tvDone).setOnClickListener(this);
        v.findViewById(R.id.tvDone).setEnabled(false);
        v.findViewById(R.id.tvDone).setAlpha(0.6f);

        rvTag = v.findViewById(R.id.rvTag);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        etSearch = v.findViewById(R.id.etSearch);
        etSearch.setHint(Constant.TITLE_EMPTY);
        etSearch.addTextChangedListener(this);

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

    private void createChip(Friends vo) {
        try {
            if (!selectedMap.containsKey(vo.getId())) {
                selectedMap.put(vo.getId(), vo);
                 /* selectedMap.put(vo.getId(), vo.getLabel());
                    if (size < selectedMap.size()) {*/
                TextView t = new TextView(context);
                t.setLayoutParams(params);
                t.setPadding(16, 16, 16, 16);
                t.setText(vo.getLabel() + "  X ");
                //t.setText("name");
                t.setTextColor(Color.WHITE);
                t.setTag(vo.getId());
                // t.setId(vo.getId());
                // t.setId();
                t.setBackgroundColor(Color.BLUE);
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        t.setVisibility(View.GONE);
                        selectedMap.remove(t.getTag());
                    }
                });
                flowLayout.addView(t);
            } else {
                CustomLog.e("add", "already added");
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.tvDone:
                    Collection<Friends> vos = selectedMap.values();
                    List<Tagged> tagged = new ArrayList<>();
                    if (vos.size() > 0) {
                        for (Friends vo : vos) {
                            tagged.add(SpanUtil.convertFriendToTagged(vo));
                        }
                        (activity).activity.setTagged(tagged);
                        activity.taskPerformed = Constant.TASK_TAGGING;
                    }
                    onBackPressed();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callSuggestionApi(String value) {
        if (TextUtils.isEmpty(value)) {
            if (null != friendList)
                friendList.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        try {
            if (isNetworkAvailable(context)) {
                try {
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
                                        v.findViewById(R.id.tvDone).setEnabled(true);
                                        v.findViewById(R.id.tvDone).setAlpha(1f);
                                    } else {
                                        v.findViewById(R.id.tvDone).setEnabled(false);
                                        v.findViewById(R.id.tvDone).setAlpha(0.6f);
                                    }
                                    //  initAdapter();
                                    updateAdapter();
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

    private void updateAdapter() {
        adapter.notifyDataSetChanged();
        rvTag.setVisibility(friendList.size() > 0 ? View.VISIBLE : View.GONE);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(getStrings(R.string.MSG_NO_USER));
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onItemClicked(Integer object1, String object2, int position) {
        try {
            // etSearch.setText(Constant.EMPTY);
            // (activity).taskPerformed = Constant.TASK_MENTION;
            // (activity).setFreinds(friendList.get(position));
            // onBackPressed();
            createChip(friendList.get(position));
            rvTag.setVisibility(View.GONE);
            etSearch.setText(Constant.EMPTY);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

   /* public static Fragment newInstance(boolean isForMention) {
        TagPeopleFragment frag = new TagPeopleFragment();
        frag.isForMention = isForMention;
        return frag;
    }*/
}
