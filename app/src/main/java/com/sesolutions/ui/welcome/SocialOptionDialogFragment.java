package com.sesolutions.ui.welcome;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;

public class SocialOptionDialogFragment extends AppCompatDialogFragment implements OnUserClickedListener<Integer, Object> {
    private View v;

    /* private TextView tvResult;
     private TextView tvTitle;
     private TextView tvLanguage;*/
    private OnUserClickedListener<Integer, Object> listener;
    private Context context;
    private int result=-1;
    private List<SearchVo> optionList;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_social_option, container, false);
        try {
            context = getContext();

           /* colorPrimary = Color.parseColor(Constant.colorPrimary);
            init();
            String pr = SPref.getInstance().getString(getContext(), Constant.KEY_PRIVACY);

            selectedPrivacy = TextUtils.isEmpty(pr) ? Constant.PRIVACY_EVERYONE : pr;//SPref.getInstance().getString(getContext(), Constant.KEY_PRIVACY);
            setSelectedCheckBox();*/

            //   startListening();
            setFeedUpdateRecycleView(context);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    private void setFeedUpdateRecycleView(Context context) {
        try {
            RecyclerView recycleViewFeedUpdate = v.findViewById(R.id.rvFeedUpdate);
            recycleViewFeedUpdate.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewFeedUpdate.setLayoutManager(layoutManager);
            SocialOptionAdapter adapterFeed = new SocialOptionAdapter(optionList, context, this);
            recycleViewFeedUpdate.setAdapter(adapterFeed);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        // Speech.getInstance().shutdown();
        if (result > -1) {
            listener.onItemClicked(Constant.Events.CLICKED_OPTION, result, result);
            result = -1;
        }
    }

    public void onDismiss() {
        onDismiss(getDialog());
    }

    public static SocialOptionDialogFragment newInstance(OnUserClickedListener<Integer, Object> listener, List<SearchVo> optionList) {
        SocialOptionDialogFragment frag = new SocialOptionDialogFragment();
        frag.listener = listener;
        frag.optionList = optionList;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        result = postion;
        onDismiss();
        return false;
    }
}
