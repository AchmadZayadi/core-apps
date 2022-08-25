package com.sesolutions.ui.price;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpImageNotificationRequest;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SuccessResponse;
import com.sesolutions.responses.page.PageLike;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.dashboard.HomeFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

public class PriceActivity extends BaseFragment {

    RecyclerView recyclerView;
    View view;
    public OnUserClickedListener<Integer, Object> parent;

    public static PriceActivity newInstance(OnUserClickedListener<Integer, Object> parent) {
        PriceActivity frag = new PriceActivity();
        frag.parent = parent;
        return frag;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View root = inflater.inflate(R.layout.fragment_price, container, false);


        recyclerView = root.findViewById(R.id.rv_price);
        getApiPrice();

        return root;
    }

    private void getApiPrice() {
        if (isNetworkAvailable(context)) {
            try {
                showBaseLoader(true);
                HttpRequestVO request = new HttpRequestVO(Constant.URL_PRICE);
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = msg -> {
                    hideBaseLoader();
                    try {
                        String response = (String) msg.obj;
                        JSONObject jresponse = new JSONObject("harga");
                        String respon = jresponse.getString("harga");

                        CustomLog.d("hasilnyaa",respon + "  ayee22");
                        if (null != response) {
                            SuccessResponse err = new Gson().fromJson(response, SuccessResponse.class);
                            if (err.isSuccess()) {
                                JSONObject resp = new JSONObject((String) response);

                                CustomLog.d("hasilnyaa",resp.toString() + "  ayee");
                                Util.showSnackbar(view, err.getResult().getSuccessMessage());
                                activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                onBackPressed();
                            } else {
                                Util.showSnackbar(view, err.getErrorMessage());
                            }
                        }

                    } catch (Exception e) {
                        somethingWrongMsg(view);
                        CustomLog.e(e);
                    }
                    return true;
                };
                new HttpImageNotificationRequest(activity, new Handler(callback), true).run(request);

            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {
            notInternetMsg(view);
        }

    }

}
