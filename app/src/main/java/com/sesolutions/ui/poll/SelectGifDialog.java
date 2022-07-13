package com.sesolutions.ui.poll;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.responses.poll.PollResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.contest.join.LinkAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectGifDialog extends AppCompatDialogFragment implements OnUserClickedListener<Integer, Object>, View.OnClickListener {
    //private static final int REQ_LOAD_MORE = 12;
    private static final int REQ_LOAD_MORE_CHILD = 13;
    private View v;
    private OnUserClickedListener<Integer, Object> listener;
    private Context context;
    private List<SearchVo> childList;
    private LinkAdapter adapterChild;
    private int viewTag;
    private PollResponse.Result result;
    private boolean isLoading;
    private boolean wasListEmpty;


    private @NonNull
    String selectedModule;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_contest_link, container, false);
        try {
            context = getContext();
            new ThemeManager().applyTheme((ViewGroup) v, context);
            v.findViewById(R.id.ivBack).setOnClickListener(this);
            ((ImageView) v.findViewById(R.id.ivBack)).setColorFilter(Color.parseColor(Constant.text_color_1));

            ((TextView) v.findViewById(R.id.tvDialogTitle)).setText(R.string.select_image_gif);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_GIF_AVAILABLE);
            setChildRecycleView();
            setupModuleData();
            // Map<String, Object> req = new HashMap<>(map);
            //  req.put(Constant.KEY_GET_FORM, 1);
            callChildApi(1, 1);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private String URL_POLL_GIF;

    private void setupModuleData() {
        switch (selectedModule) {
            case Constant.ResourceType.PAGE:
            case MenuTab.Page.TYPE_BROWSE_POLL:
            case MenuTab.Page.TYPE_PROFILE_POLL:
                URL_POLL_GIF = Constant.URL_PAGE_POLL_GIF;
                break;
            case Constant.ResourceType.GROUP:
            case MenuTab.Group.TYPE_BROWSE_POLL:
            case MenuTab.Group.TYPE_PROFILE_POLL:
                URL_POLL_GIF = Constant.URL_GROUP_POLL_GIF;
                break;

            case Constant.ResourceType.BUSINESS:
            case MenuTab.Business.TYPE_BROWSE_POLL:
            case MenuTab.Business.TYPE_PROFILE_POLL:
                URL_POLL_GIF = Constant.URL_BUSINESS_POLL_GIF;
                break;
        }
    }


    private RecyclerView rvChild;

    private void setChildRecycleView() {
        try {
            childList = new ArrayList<>();
            rvChild = v.findViewById(R.id.rvChild);
            rvChild.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            rvChild.setLayoutManager(layoutManager);
            adapterChild = new LinkAdapter(childList, context, this, true);
            rvChild.setAdapter(adapterChild);
            //  rvChild.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callChildApi(int reqCode, int pageCount) {
        Map<String, Object> req = new HashMap<>();

        if (reqCode == REQ_LOAD_MORE_CHILD) {
            v.findViewById(R.id.pb).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
        }
        req.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
        req.put(Constant.KEY_PAGE, pageCount);
        isLoading = true;
        req.put(Constant.KEY_USER_ID, SPref.getInstance().getLoggedInUserId(context));
        new ApiController(URL_POLL_GIF, req, context, this, reqCode).execute();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        // Speech.getInstance().shutdown();
    }

    public void onDismiss() {
        onDismiss(getDialog());
    }

    public static SelectGifDialog newInstance(OnUserClickedListener<Integer, Object> listener, int viewTag,String selectedModule) {
        SelectGifDialog frag = new SelectGifDialog();
        frag.listener = listener;
        frag.viewTag = viewTag;
        frag.selectedModule = selectedModule;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case REQ_LOAD_MORE_CHILD:
            case 1:
                try {
                    String response = (String) object2;
                    CustomLog.e("repsonse", "" + response);
                    if (response != null) {
                        PollResponse resp = new Gson().fromJson(response, PollResponse.class);
                        if (resp.isSuccess()) {
                            wasListEmpty = childList.size() == 0;

                            result = resp.getResult();
                            if (null != result.getGifs()) {
                                childList.addAll(result.convertGifToSearchVo());
                            }

                            updateChildUI(object1);
                        } else {
                            Util.showSnackbar(v, resp.getErrorMessage());
                        }
                    } else {
                        dismiss();
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                    dismiss();
                }
                break;

            case Constant.Events.MUSIC_MAIN:
                try {
                    dismiss();
                    listener.onItemClicked(Constant.Events.POPUP, childList.get(postion), viewTag);
                    break;

                } catch (Exception e) {
                    CustomLog.e(e);
                }

                break;

            case Constant.Events.LOAD_MORE:
                try {
                    if (isLoading) break;
                    else if (result != null) {
                        if (result.getCurrentPage() < result.getTotalPage()) {
                            callChildApi(REQ_LOAD_MORE_CHILD, result.getNextPage());
                        }
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;
            default:
                // optionList.get(postion).setValue(("" + object2).equals("1") ? "0" : "1");
                // adapterFeed.notifyItemChanged(postion);
                break;
        }
        return false;
    }

    public void runLayoutAnimation(final RecyclerView recyclerView) {
        if (wasListEmpty) {
            final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.anim_fall_down);
            recyclerView.setLayoutAnimation(controller);
            recyclerView.scheduleLayoutAnimation();
        }
    }


    private void updateChildUI(int reqCode) {
        try {
            v.findViewById(R.id.pbMain).setVisibility(View.GONE);
            v.findViewById(R.id.pb).setVisibility(View.GONE);
            v.findViewById(R.id.ivBack).setVisibility(View.VISIBLE);
            adapterChild.notifyDataSetChanged();
            if (childList.size() > 0) {
                runLayoutAnimation(rvChild);
            }
            rvChild.setVisibility(childList.size() > 0 ? View.VISIBLE : View.GONE);

            v.findViewById(R.id.llNoData).setVisibility(childList.size() > 0 ? View.GONE : View.VISIBLE);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onDismiss();
                /*view.setVisibility(View.GONE);
                rvChild.setVisibility(View.GONE);
                v.findViewById(R.id.llNoData).setVisibility(View.GONE);*/

                break;

                /*Map<String, Object> req = new HashMap<>();
                for (Options opt : optionList) {
                    if ("1".equals(opt.getValue()))
                        req.put("users[" + opt.getName() + "]", opt.getName());
                }
                v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
                v.findViewById(R.id.rvFeedUpdate).setVisibility(View.GONE);
                v.findViewById(R.id.rlBottom).setVisibility(View.GONE);
                new ApiController(url, req, context, this, -2).execute();*/

        }
    }


}
