package com.sesolutions.ui.contest.join;


import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContestJoinRegistrationFragment extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {

    private OnUserClickedListener<Integer, Object> listener;
    private List<Dummy.Formfields> registration;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // postponeEnterTransition();
            // setEnterTransition(new AutoTransition());
            //setExitTransition(new AutoTransition());
            /*setEnterTransition(new Slide(Gravity.END));
            setExitTransition(new Slide(Gravity.START));*/
            setEnterTransition(new Fade(Fade.IN));
            setExitTransition(new Fade(Fade.OUT));
            // setSharedElementEnterTransition(new DetailsTransition());
            //  setSharedElementReturnTransition(new DetailsTransition());
            // setAllowEnterTransitionOverlap(false);
            // setAllowReturnTransitionOverlap(false);
        }
    }

    public static ContestJoinRegistrationFragment newInstance(List<Dummy.Formfields> registration, OnUserClickedListener<Integer, Object> listener) {
        ContestJoinRegistrationFragment frag = new ContestJoinRegistrationFragment();
        frag.FORM_TYPE = Constant.FormType.JOIN;
        frag.listener = listener;
        frag.registration = registration;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_contest_registraion, container, false);
        try {
            applyTheme(v);
            initScreenData();

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void initScreenData() {
        init();
        /*calling api only if null response is coming from previous screen*/
        Dummy.Result result = new Dummy.Result();
        result.setFormfields(registration);
        createFormUi(result);

    }

    private void init() {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        listener.onItemClicked(Constant.Events.UPDATE_NEXT, getString(R.string.next), -1);
        listener.onItemClicked(Constant.Events.UPDATE_PREV, " ", -1);
    }

   /* @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        super.onValueChanged(baseFormElement);
        try {
            if (BaseFormElement.TYPE_PICKER_SINGLE == baseFormElement.getType()) {
                if (((FormElementPickerSingle) baseFormElement).getName().equals("vote_type")) {
                    String key = Util.getKeyFromValue(commonMap.get(((FormElementPickerSingle) baseFormElement).getName()), baseFormElement.getValue());

                    if (null != key) {
                        boolean hideShow = key.equals("1");
                        for (int i = 0; i < tagList.size(); i++) {
                            int tag = 1011 + i;
                            //key = tagList.get(i);
                            String name = tagList.get(i);
                            if (name.equals("result_date") || name.equals("result_time")) {
                                mFormBuilder.getAdapter().setHiddenAtTag(tag, !hideShow);
                            }
                            //CustomLog.d("tag1", "" + tag);
                        }
                    }

                    mFormBuilder.getAdapter().notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

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

   /* private void hideInitially() {
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            //key = tagList.get(i);
            String name = tagList.get(i);
            if (name.equals("result_date") || name.equals("result_time")) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            }
        }
        mFormBuilder.getAdapter().notifyDataSetChanged();
    }*/


    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        // (List<String>) result;
        if (null != result) {
            String filePath = ((List<String>) result).get(0);
            mFormBuilder.getAdapter().setValueAtTag(clickedFilePostion, filePath);
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }

    public void onPrevClick() {
        listener.onItemClicked(Constant.Events.DECLINE, processData(), 1);
    }


    public void onNextClick() {
        listener.onItemClicked(Constant.Events.NEXT, processData(), -1);


    }

    private Map<String, Dummy.Formfields> processData() {
        closeKeyboard();
        Map<String, Object> map = fetchFormValue();
        Map<String, Dummy.Formfields> temp = new HashMap<>();
        for (Dummy.Formfields vo : registration) {
            if ("entry_photo".equals(vo.getName())) {
                if (map.containsKey(Constant.FILE_TYPE + "entry_photo")) {
                    vo.setStringValue(map.get(Constant.FILE_TYPE + "entry_photo"));
                    temp.put(vo.getName(), vo);
                }
            } else {
                if (map.containsKey(vo.getName())) {
                    vo.setStringValue(map.get(vo.getName()));
                    temp.put(vo.getName(), vo);
                }
            }
        }
        return temp;
    }
}
