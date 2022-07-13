package com.sesolutions.ui.groups;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.customviews.MultiSelectionSpinner;
import com.sesolutions.ui.postfeed.FeedPrivacyAdapter;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;

public class AddQuestionDialogFragment extends AppCompatDialogFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    LinearLayoutCompat llQuestions;


    // private int colorPrimary;


    private OnUserClickedListener<Integer, List<Dummy.Formfields>> listener;
    private int position;
    private FeedPrivacyAdapter adapter;
    private List<Dummy.Formfields> list;
    private MultiSelectionSpinner spinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_group_question, container, false);
        try {
            //   colorPrimary = Color.parseColor(Constant.colorPrimary);


            v.findViewById(R.id.bCancel).setOnClickListener(this);
            v.findViewById(R.id.bSubmit).setOnClickListener(this);
            v.findViewById(R.id.rlAddQuestion).setOnClickListener(this);
            llQuestions = v.findViewById(R.id.llQuestions);
            showSavedData();
            /*createRoundedFilled();
            createRoundedHolo();

            ((TextView) v.findViewById(R.id.tvTitle)).setText(pageLike.getTitle());
            ((TextView) v.findViewById(R.id.tvMsg)).setText(pageLike.getDescription());
            Util.showImageWithGlide((ImageView) v.findViewById(R.id.ivImage), pageLike.getImageUrl(), getContext(), 1);
            // Spinner element
            spinner = (MultiSelectionSpinner) v.findViewById(R.id.spinner);
            spinner.setItems(pageLike.getPageTitleList());
            spinner.setSelection(new int[]{0});*/


        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void showSavedData() {

        for (Dummy.Formfields fld : list) {
            if (!TextUtils.isEmpty(fld.getValue())) {
                View view = getLayoutInflater().inflate(R.layout.layout_group_question_item, llQuestions, false);
                ((AppCompatEditText) view.findViewById(R.id.etBody)).setText(fld.getValue());
                view.findViewById(R.id.ivRemove).setOnClickListener(view1 -> {
                    llQuestions.removeView(view);
                    v.findViewById(R.id.rlAddQuestion).setVisibility(View.VISIBLE);
                });
                llQuestions.addView(view);
            }
        }

        if (llQuestions.getChildCount() == list.size()) {
            v.findViewById(R.id.rlAddQuestion).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.rlAddQuestion).setVisibility(View.VISIBLE);
        }
    }

    private void addQuestionItem() {


        if (list.size() > llQuestions.getChildCount()) {
            View view = getLayoutInflater().inflate(R.layout.layout_group_question_item, llQuestions, false);
            // ((AppCompatEditText) view.findViewById(R.id.etBody)).setText(s);
            view.findViewById(R.id.ivRemove).setOnClickListener(view1 -> {
                llQuestions.removeView(view);
                v.findViewById(R.id.rlAddQuestion).setVisibility(View.VISIBLE);
            });
            llQuestions.addView(view);
            if (llQuestions.getChildCount() == list.size()) {
                v.findViewById(R.id.rlAddQuestion).setVisibility(View.GONE);
            } else {
                v.findViewById(R.id.rlAddQuestion).setVisibility(View.VISIBLE);
            }
        }
    }


    /*@Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }*/

   /* public void onDismiss() {
        onDismiss(getDialog());
    }*/

    public static AddQuestionDialogFragment newInstance(List<Dummy.Formfields> resp, OnUserClickedListener<Integer, List<Dummy.Formfields>> listener, int position) {
        AddQuestionDialogFragment frag = new AddQuestionDialogFragment();
        frag.listener = listener;
        frag.list = resp;
        frag.position = position;
        return frag;
    }


    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.bSubmit:
                    //hide main layout and show progress bar
                    saveFilledData();
                    listener.onItemClicked(Constant.Events.POPUP, list, position);
                    dismiss();
                    break;
                case R.id.bCancel:
                    dismiss();
                    break;
                case R.id.rlAddQuestion:
                    addQuestionItem();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void saveFilledData() {
        try {
            int childCount = llQuestions.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    String text = ((AppCompatEditText) llQuestions.getChildAt(i).findViewById(R.id.etBody)).getText().toString();
                    list.get(i).setStringValue(text);
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setStringValue("");
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        return false;
    }
}
