package com.sesolutions.ui.currency;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.dashboard.DashboardFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sesolutions.utils.URL.URL_CHANGE_CURRENCY;
import static com.sesolutions.utils.URL.URL_GET_CURRENCY;

public class CurrencyDialog extends DialogFragment implements OnUserClickedListener<Integer, Object> {
    private View v;
    private OnUserClickedListener<Integer, Object> listener;
    private Context context;
    private Dummy.Result result;
    private List<Options> optionList;
    private List<Options> backUpList = new ArrayList<>();
    private CurrencyAdapter adapterFeed;
    private Map<String, Object> map;
    private String url = URL_GET_CURRENCY;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_currency, container, false);
        try {
            context = getContext();
            new ThemeManager().applyTheme((ViewGroup) v, context);
            setFeedUpdateRecycleView(context);

            new ApiController(url, null, context, this, -1).execute();


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

    private void setFeedUpdateRecycleView(Context context) {
        try {
            optionList = new ArrayList<>();
            RecyclerView recycleViewFeedUpdate = v.findViewById(R.id.rvFeedUpdate);
            recycleViewFeedUpdate.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewFeedUpdate.setLayoutManager(layoutManager);
            adapterFeed = new CurrencyAdapter(optionList, context, this);
            recycleViewFeedUpdate.setAdapter(adapterFeed);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) parentFragment).onDismiss(dialog);
        }
    }

    public void onDismiss() {
        onDismiss(getDialog());
    }

    public static CurrencyDialog newInstance(OnUserClickedListener<Integer, Object> listener) {
        CurrencyDialog frag = new CurrencyDialog();
        frag.listener = listener;
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
                        CustomLog.e("keys:", "" + result.getenabledCurrencies().keySet());
                        CustomLog.e("keys:", "" + result.getenabledCurrencies().values());
                        if (null != result) {
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
        } else if (object1 == -5) {
            try {
                String response = (String) object2;
                CustomLog.e("repsonse", "" + response);
                if (response != null) {
                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                    if (err.isSuccess()) {
                        String selectedCurr = new JSONObject(response).optJSONObject("result").getString("default_currency");
                        Util.showToast(context, "The Default Currency has been set to " + selectedCurr);
                        AppConfiguration.DEFAULT_CURRENCY = selectedCurr;
                        try {
                            DashboardFragment.icCurrrency.setText(""+ AppConfiguration.DEFAULT_CURRENCY);
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
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
        } else if (object1 == Constant.Events.MENU_MAIN) {
            String selectedCurrency = object2.toString();
            CustomLog.e("val: ", "" + object2 + "   " + postion);
            if (object2.toString().equalsIgnoreCase(AppConfiguration.DEFAULT_CURRENCY)) {
                Util.showSnackbar(v, "You have already selected " + selectedCurrency + " as your currency..");
            } else {
                final Map<String, Object> map = new HashMap<>();
                map.put("currency", selectedCurrency);
                v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
                v.findViewById(R.id.rvFeedUpdate).setVisibility(View.GONE);
                v.findViewById(R.id.rlBottom).setVisibility(View.GONE);
                new ApiController(URL_CHANGE_CURRENCY, map, context, this, -5).execute();
            }
        }
        return false;
    }

    private void updateUI() {
        try {
            v.findViewById(R.id.pbMain).setVisibility(View.GONE);
            v.findViewById(R.id.rvFeedUpdate).setVisibility(View.VISIBLE);
            v.findViewById(R.id.rlBottom).setVisibility(View.VISIBLE);
            if (AppConfiguration.DEFAULT_CURRENCY != null) {
                v.findViewById(R.id.llCurrency).setVisibility(View.VISIBLE);
                ((AppCompatTextView) v.findViewById(R.id.tvcurrentCurrency)).setText(AppConfiguration.DEFAULT_CURRENCY);
            }
            Map<String, String> userMap = result.getenabledCurrencies();
            for (Map.Entry<String, String> key : userMap.entrySet()) {
                optionList.add(new Options(key.getKey(), key.getValue()));
                backUpList.addAll(optionList);
                adapterFeed.notifyDataSetChanged();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


}
