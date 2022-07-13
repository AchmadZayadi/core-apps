package com.sesolutions.ui.qna;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

public class AddPollOptionsDialog extends AppCompatDialogFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    LinearLayoutCompat llQuestions;
    private int MAX_OPTION_COUNT;

    private OnUserClickedListener<Integer, List<String>> listener;
    private int position;
    private AddOptionAdapter adapter;
    private List<String> list;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_add_options, container, false);
        try {
            //   colorPrimary = Color.parseColor(Constant.colorPrimary);


            v.findViewById(R.id.bCancel).setOnClickListener(this);
            v.findViewById(R.id.bSubmit).setOnClickListener(this);
            v.findViewById(R.id.rlAddQuestion).setOnClickListener(this);
            llQuestions = v.findViewById(R.id.llQuestions);
            // svOptions = v.findViewById(R.id.svOptions);
            //by default show atleast 2 options
            if (list.size() == 0) {
                list.add("");
                list.add("");
            }
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

        for (String fld : list) {
            if (null != fld) {
                View view = getLayoutInflater().inflate(R.layout.layout_group_question_item, (ViewGroup) llQuestions, false);
                ((AppCompatEditText) view.findViewById(R.id.etBody)).setText(fld);
                view.findViewById(R.id.ivRemove).setOnClickListener(view1 -> {
                    llQuestions.removeView(view);
                    v.findViewById(R.id.rlAddQuestion).setVisibility(View.VISIBLE);
                });
                llQuestions.addView(view);
            }
        }

        if (llQuestions.getChildCount() == MAX_OPTION_COUNT) {
            v.findViewById(R.id.rlAddQuestion).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.rlAddQuestion).setVisibility(View.VISIBLE);
        }
    }

    private void addQuestionItem() {


        if (MAX_OPTION_COUNT > llQuestions.getChildCount()) {
            View view = getLayoutInflater().inflate(R.layout.layout_group_question_item, (ViewGroup) llQuestions, false);
            // ((AppCompatEditText) view.findViewById(R.id.etBody)).setText(s);
            view.findViewById(R.id.ivRemove).setOnClickListener(view1 -> {
                llQuestions.removeView(view);
                v.findViewById(R.id.rlAddQuestion).setVisibility(View.VISIBLE);
            });
            llQuestions.addView(view);
            //svOptions.smoothScrollTo(0, view.getBottom());
            if (llQuestions.getChildCount() == MAX_OPTION_COUNT) {
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

    public static AddPollOptionsDialog newInstance(List<String> resp, OnUserClickedListener<Integer, List<String>> listener, int position, final int MAX_COUNT) {
        AddPollOptionsDialog frag = new AddPollOptionsDialog();
        frag.listener = listener;
        frag.list = resp;
        frag.position = position;
        frag.MAX_OPTION_COUNT = MAX_COUNT;
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

            if (childCount < 2) {
                Util.showSnackbar(v, getString(R.string.msg_poll_minimum_option_count));
                return;
            }
            list.clear();
            //if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                String text = ((AppCompatEditText) llQuestions.getChildAt(i).findViewById(R.id.etBody)).getText().toString();
                list.add(text);
            }
            // }
            /*else {
                for (int i = 0; i < list.size(); i++) {
                    list.set(i, null);
                }
            }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

   /* public void setRecyclerView() {
        try {
            RecyclerView recyclerView = v.findViewById(R.id.rvOptions);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new AddOptionAdapter(list, getContext(), this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.DELETE:

                list.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, list.size());

                v.findViewById(R.id.rlAddQuestion).setVisibility(View.VISIBLE);
                break;
        }
        return false;
    }
}
