package com.sesolutions.ui.page;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.page.PageLike;
import com.sesolutions.ui.customviews.MultiSelectionSpinner;
import com.sesolutions.ui.postfeed.FeedPrivacyAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageLikeDialogFragment extends AppCompatDialogFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, AdapterView.OnItemSelectedListener {

    private View v;


    // private int colorPrimary;


    private OnUserClickedListener<Integer, Object> listener;
    private int position;
    private FeedPrivacyAdapter adapter;
    private PageLike pageLike;
    private String url;
    private MultiSelectionSpinner spinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_page_like, container, false);
        try {
            createRoundedFilled();
            createRoundedHolo();
            v.findViewById(R.id.bCancel).setOnClickListener(this);
            v.findViewById(R.id.bSubmit).setOnClickListener(this);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(pageLike.getTitle());
            ((TextView) v.findViewById(R.id.tvMsg)).setText(pageLike.getDescription());
            Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivImage), pageLike.getImageUrl(), getContext(), 1);
            // Spinner element
            spinner = v.findViewById(R.id.spinner);
            spinner.setItems(pageLike.getTitleList());
            spinner.setSelection(new int[]{0});


        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void createRoundedFilled() {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{8, 8, 8, 8, 8, 8, 8, 8});
        shape.setColor(Color.parseColor(Constant.colorPrimary));
        //  shape.setStroke(2, Color.parseColor(Constant.colorPrimary));
        v.findViewById(R.id.bSubmit).setBackground(shape);
    }

    private void createRoundedHolo() {
        GradientDrawable shape2 = new GradientDrawable();
        shape2.setShape(GradientDrawable.RECTANGLE);
        shape2.setCornerRadii(new float[]{8, 8, 8, 8, 8, 8, 8, 8});
        // shape.setColor(colorPrimary);
        shape2.setStroke(2, Color.parseColor(Constant.colorPrimary));
        v.findViewById(R.id.bCancel).setBackground(shape2);
        ((AppCompatButton) v.findViewById(R.id.bCancel)).setTextColor(Color.parseColor(Constant.colorPrimary));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        this.position = position;

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }


    /*@Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }*/

   /* public void onDismiss() {
        onDismiss(getDialog());
    }*/

    public static PageLikeDialogFragment newInstance(PageLike resp, String url, OnUserClickedListener<Integer, Object> listener, String resourceType) {
        PageLikeDialogFragment frag = new PageLikeDialogFragment();
        frag.listener = listener;
        frag.pageLike = resp;
        frag.url = url;
        frag.resourceType = resourceType;
        return frag;
    }


    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.bSubmit:
                    //hide main layout and show progress bar
                    v.findViewById(R.id.rlHeader).setVisibility(View.GONE);
                    v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);

                    //call api


                    Map<String, Object> map = getMapData();
                    // map.put(Constant.KEY_PAGE_ID, pageLike.getPage().get(position).getPageId());
                    map.put(Constant.KEY_TYPE, resourceType);
                    new ApiController(url, map, getContext(), this, Constant.Events.LIKE_AS_PAGE).execute();

                    break;
                case R.id.bCancel:
                    dismiss();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private String resourceType;

    private Map<String, Object> getMapData() {
        int index = 0;
        List<Integer> selecteItem = spinner.getSelectedIndicies();
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ID, pageLike.getId());
        switch (resourceType) {
            case Constant.ResourceType.PAGE:
                for (int i : selecteItem) {
                    map.put("page_ids[" + index + "]", pageLike.getPage().get(i).getPageId());
                    index++;
                }
                break;
            case Constant.ResourceType.BUSINESS:
                for (int i : selecteItem) {
                    map.put("business_ids[" + index + "]", pageLike.getBusiness().get(i).getBusinessId());
                    index++;
                }
                break;
            case Constant.ResourceType.GROUP:

                for (int i : selecteItem) {
                    map.put("group_ids[" + index + "]", pageLike.getGroup().get(i).getGroupId());
                    index++;
                }
                break;
        }

        return map;

    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {

        v.findViewById(R.id.rlHeader).setVisibility(View.VISIBLE);
        v.findViewById(R.id.rl1).setVisibility(View.GONE);
        v.findViewById(R.id.pbMain).setVisibility(View.GONE);
        v.findViewById(R.id.bSubmit).setVisibility(View.GONE);

        try {
            if (object2 != null) {
                ErrorResponse err = new Gson().fromJson((String) object2, ErrorResponse.class);
                if (err.isSuccess()) {
                    ((AppCompatButton) v.findViewById(R.id.bCancel)).setText(R.string.TXT_OK);
                    ((TextView) v.findViewById(R.id.tvTitle)).setText(new JSONObject((String) object2).optJSONObject("result").optString("success_message"));
                } else {
                    ((TextView) v.findViewById(R.id.tvTitle)).setText(err.getMessage());
                }
            } else {
                ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.msg_something_wrong);
            }
        } catch (Exception e) {
            CustomLog.e(e);

        }
        return false;
    }
}
