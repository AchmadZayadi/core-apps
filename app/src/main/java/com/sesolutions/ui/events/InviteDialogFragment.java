package com.sesolutions.ui.events;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteDialogFragment extends AppCompatDialogFragment implements TextWatcher, OnUserClickedListener<Integer, Object>, View.OnClickListener {
    private View v;
    private OnUserClickedListener<Integer, Object> listener;
    private Context context;
    private Dummy.Result result;
    private List<Options> optionList;
    private List<Options> backUpList = new ArrayList<>();
    private InviteAdapter adapterFeed;
    private Map<String, Object> map;
    private String url;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_invite_member, container, false);
        try {
            context = getContext();
            new ThemeManager().applyTheme((ViewGroup) v, context);
            setFeedUpdateRecycleView(context);
            v.findViewById(R.id.bSubmit).setOnClickListener(this);
            ((AppCompatEditText) v.findViewById(R.id.etSearch)).addTextChangedListener(this);
            ((CheckBox) v.findViewById(R.id.cb_all)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkAll(isChecked ? "1" : "0");
                }
            });
            Map<String, Object> req = new HashMap<>(map);
            req.put(Constant.KEY_GET_FORM, 1);
            new ApiController(url, req, context, this, -1).execute();


           /* colorPrimary = Color.parseColor(Constant.colorPrimary);
            init();
            String pr = SPref.getInstance().getString(getContext(), Constant.KEY_PRIVACY);

            selectedPrivacy = TextUtils.isEmpty(pr) ? Constant.PRIVACY_EVERYONE : pr;//SPref.getInstance().getString(getContext(), Constant.KEY_PRIVACY);
            setSelectedCheckBox();*/

            //   startListening();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void checkAll(String s) {
        for (Options opt : optionList) {
            opt.setValue(s);
        }
        adapterFeed.notifyDataSetChanged();
    }


    private void setFeedUpdateRecycleView(Context context) {
        try {
            optionList = new ArrayList<>();
            RecyclerView recycleViewFeedUpdate = v.findViewById(R.id.rvFeedUpdate);
            recycleViewFeedUpdate.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewFeedUpdate.setLayoutManager(layoutManager);
            adapterFeed = new InviteAdapter(optionList, context, this);
            recycleViewFeedUpdate.setAdapter(adapterFeed);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        // Speech.getInstance().shutdown();
    }

    public void onDismiss() {
        onDismiss(getDialog());
    }

    public static InviteDialogFragment newInstance(OnUserClickedListener<Integer, Object> listener, Map<String, Object> map, String url) {
        InviteDialogFragment frag = new InviteDialogFragment();
        frag.listener = listener;
        frag.map = map;
        frag.url = url;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        if (object1 == -1) {
            try {
                String response = (String) object2;
                CustomLog.e("repsonse", "" + response);
                if (response != null) {
                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                    if (err.isSuccess()) {
                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                        result = vo.getResult();
                        if (null != result && null != result.getFormfields()) {
                            updateUI();
                        } else {
                            Util.showSnackbar(v, result.getMessage());
                            v.findViewById(R.id.pbMain).setVisibility(View.GONE);
                        }
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
        } else if (object1 == -2) {
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
                        v.findViewById(R.id.rvFeedUpdate).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.rlBottom).setVisibility(View.VISIBLE);

                    }
                } else {
                    dismiss();
                }
            } catch (Exception e) {
                CustomLog.e(e);
                dismiss();
            }
        } else {
            optionList.get(postion).setValue(("" + object2).equals("1") ? "0" : "1");
            adapterFeed.notifyItemChanged(postion);
        }
        return false;
    }

    private void updateUI() {
        try {
            v.findViewById(R.id.pbMain).setVisibility(View.GONE);
            v.findViewById(R.id.rvFeedUpdate).setVisibility(View.VISIBLE);
            v.findViewById(R.id.rlBottom).setVisibility(View.VISIBLE);

            Dummy.Formfields vo = result.getFormFielsByName("users");
            if (vo != null) {
                // ((TextView) v.findViewById(R.id.tvHint)).setText(context.getResources().getString(R.string.select_) + vo.getLabel());
                Map<String, String> userMap = vo.getMultiOptions();
                for (Map.Entry<String, String> key : userMap.entrySet()) {
                    optionList.add(new Options(key.getKey(), key.getValue()));
                }

                backUpList.addAll(optionList);
                adapterFeed.notifyDataSetChanged();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bSubmit:
                if (validateFields()){
                    Map<String, Object> req = new HashMap<>(map);
                    for (Options opt : optionList) {
                        if ("1".equals(opt.getValue()))
                            req.put("users[" + opt.getName() + "]", opt.getName());
                    }
                    v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.rvFeedUpdate).setVisibility(View.GONE);
                    v.findViewById(R.id.rlBottom).setVisibility(View.GONE);
                    new ApiController(url, req, context, this, -2).execute();
                }else{
                    Util.showSnackbar(v, getString(R.string.MSG_NO_MEMBER_SELECTED));
                }

                break;
        }
    }
    private boolean validateFields() {
        for (Options option : optionList){
            if (option.getValue().equals("1"))
                return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        try {
            String value = "" + s;
            if (TextUtils.isEmpty(value)) {
                optionList.clear();
                optionList.addAll(backUpList);
                adapterFeed.notifyDataSetChanged();
                v.findViewById(R.id.cb_all).setVisibility(View.VISIBLE);
            } else {
                optionList.clear();
                for (int i = 0; i < backUpList.size(); i++) {
                    Options option = backUpList.get(i);
                    if (option.getLabel().toLowerCase().contains(value.toLowerCase()))
                        optionList.add(option);
                    else
                        optionList.remove(option);
                }

                if (optionList.size() == 0 )
                    v.findViewById(R.id.cb_all).setVisibility(View.GONE);
                adapterFeed.notifyDataSetChanged();
            }

        } catch (Exception e) {
            CustomLog.e("search_member", e.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
