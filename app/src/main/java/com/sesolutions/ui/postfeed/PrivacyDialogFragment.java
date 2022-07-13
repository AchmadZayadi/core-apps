package com.sesolutions.ui.postfeed;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseDialogFragment;
import com.sesolutions.ui.dashboard.composervo.PrivacyOptions;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import java.util.List;

public class PrivacyDialogFragment extends BaseDialogFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;

    /* AppCompatCheckBox cb1;
     AppCompatCheckBox cb2;
     AppCompatCheckBox cb3;
     AppCompatCheckBox cb4;
     TextView tv1;
     TextView tv2;
     TextView tv3;
     TextView tv4;*/
    private int colorPrimary;
/*    LinearLayoutCompat ll1;
    LinearLayoutCompat ll2;
    LinearLayoutCompat ll3;
    LinearLayoutCompat ll4;*/

    private OnUserClickedListener<Integer, Object> listener;
    private String selectedPrivacy;
    private int position;
    private List<PrivacyOptions> privacyList;
    private FeedPrivacyAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_privacy, container, false);
        applyTheme(v);
        try {
            colorPrimary = Color.parseColor(Constant.colorPrimary);
            String pr = SPref.getInstance().getString(getContext(), Constant.KEY_PRIVACY);
            selectedPrivacy = TextUtils.isEmpty(pr) ? privacyList.get(0).getName() : pr;//SPref.getInstance().getString(getContext(), Constant.KEY_PRIVACY);
            init();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {

        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.llPrivacyHeader).setBackgroundColor(colorPrimary);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FeedPrivacyAdapter(privacyList, getContext(), this);
        adapter.setSelectedPosition(selectedPrivacy);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onItemClicked(Constant.Events.PRIVACY_CHANGED, selectedPrivacy, position);
    }

    public void onDismiss() {
        onDismiss(getDialog());
    }

    public static PrivacyDialogFragment newInstance(List<PrivacyOptions> list, OnUserClickedListener<Integer, Object> listener) {
        PrivacyDialogFragment frag = new PrivacyDialogFragment();
        frag.listener = listener;
        frag.privacyList = list;
        return frag;
    }


    @Override
    public void onClick(View v) {
        onDismiss();
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        position = postion;
        adapter.setSelectedPosition(privacyList.get(postion).getName());
        adapter.notifyDataSetChanged();
        onDismiss();
        return false;
    }
}
