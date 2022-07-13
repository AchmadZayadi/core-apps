package com.sesolutions.ui.credit;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.credit.CreditParams;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.Map;

import me.riddhimanadib.formmaster.model.BaseFormElement;

public class PurchaseFormagment extends FormHelper implements View.OnClickListener {//}, ParserCallbackInterface {

    private Dummy.Result result;
    private CreditParams customParam;

   /* public static CreateEditPageFragment newInstance(int formType, String url, int categoryId) {
        CreateEditPageFragment frag = new CreateEditPageFragment();
        frag.url = url;
        frag.FORM_TYPE = formType;
        frag.listener = null;
        return frag;
    }*/

    public static PurchaseFormagment newInstance(int type, Map<String, Object> map, String url) {
        PurchaseFormagment fragment = new PurchaseFormagment();
        fragment.FORM_TYPE = type;
        fragment.url = url;
        fragment.map = map;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        try {
            applyTheme(v);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void initScreenData() {
        init();
        /*calling api only if no response is coming from previous screen*/
        this.callSignUpApi();
    }

    private void init() {
        ((SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout)).setEnabled(false);
        //((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.purchase_points);
        // ((TextView) v.findViewById(R.id.tvTitle)).setText(getStrings(FORM_TYPE == Constant.FormType.CREATE_FUND ? R.string.title_create_fund : R.string.title_edit_fund));
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
    }

    @Override
    public void onValueChanged(BaseFormElement baseFormElement) {
        super.onValueChanged(baseFormElement);
        try {

            if (baseFormElement.getName().equals("sescredit_purchase_type")) {
                String key = Util.getKeyFromValue(commonMap.get(baseFormElement.getName()), baseFormElement.getValue());


                boolean hideShow = "0".equals(key);
                for (int i = 0; i < tagList.size(); i++) {
                    int tag = 1011 + i;
                    //key = tagList.get(i);
                    String name = tagList.get(i);
                    if (name.equals("sescredit_site_offers")) {
                        mFormBuilder.getAdapter().setHiddenAtTag(tag, hideShow);
                    } else if (name.equals("sescredit_number_point") || name.equals("sescredit_number_point_value")) {
                        mFormBuilder.getAdapter().setHiddenAtTag(tag, !hideShow);
                    }
                }
                mFormBuilder.getAdapter().notifyDataSetChanged();
            } else if ("sescredit_number_point".equals(baseFormElement.getName())) {
                mFormBuilder.getAdapter().setValueByName("sescredit_number_point_value", customParam.calculatePrice(baseFormElement.getValue()));
            } /*else if ("sescredit_site_offers".equals(baseFormElement.getName())) {
                baseFormElement.setRequired(true);
            }
*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void callSignUpApi(Map<String, Object> params) {
        if ("0".equals(params.get("sescredit_purchase_type")) ) {
            if (TextUtils.isEmpty("" + params.get("sescredit_number_point"))) {
                Util.showSnackbar(v, getString(R.string.invalid_option));
                return;
            }
        } else if (("1".equals(params.get("sescredit_purchase_type")) )){
            if (TextUtils.isEmpty("" + params.get("sescredit_site_offers"))) {
                Util.showSnackbar(v, getString(R.string.invalid_option));
                return;
            }
        }

        String url = customParam.getAction() + "?sescredit_purchase_type=" + params.get("sescredit_purchase_type");
        url = url + "&sescredit_number_point=" + params.get("sescredit_number_point");
        url = url + "&gateway_id=" + customParam.getGatewayId();
        url = url + "&sesapi_credit=1";
        url = url + "&sescredit_site_offers=" + params.get("sescredit_site_offers");

        //    openWebView(url, "");



        Intent intent=new Intent(getActivity(),PurchingFormActivity.class);
        intent.putExtra("sescredit_number_point",""+params.get("sescredit_number_point"));
        intent.putExtra("sescredit_purchase_type",""+params.get("sescredit_purchase_type"));
        intent.putExtra("sescredit_site_offers",""+params.get("sescredit_site_offers"));
        startActivity(intent);

    }

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

    private void hideInitially() {
        for (int i = 0; i < tagList.size(); i++) {
            int tag = 1011 + i;
            //key = tagList.get(i);
            String name = tagList.get(i);
            if (name.equals("sescredit_site_offers") /*|| name.equals("member_title_plural")*/) {
                mFormBuilder.getAdapter().setHiddenAtTag(tag, true);
            }
        }
        mFormBuilder.getAdapter().notifyDataSetChanged();
    }

    private void callSignUpApi() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    if (null != map) {
                        request.params.putAll(map);
                    }

                    request.params.put(Constant.KEY_GET_FORM, 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                Dummy vo = new Gson().fromJson(response, Dummy.class);
                                result = vo.getResult();
                                applyCustomChange(result);
                                createFormUi(result);
                                hideInitially();
                            } else {
                                notInternetMsg(v);
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);
                } catch (Exception e) {

                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void applyCustomChange(Dummy.Result result) {
        customParam = result.getCustomParams(CreditParams.class);

        CustomLog.e("gateway_id", ""+customParam.getGatewayId());
        if (result.getFormfields() != null) {
            for (Dummy.Formfields fld : result.getFormfields()) {
                switch (fld.getName()) {
                    case "sescredit_number_point_value":
                        fld.setType(Constant.TEXT_FIXED_CENTER);
                        fld.setStringValue(fld.getDescription());
                        break;
                    case "gatewayButton":
                        fld.setType(Constant.HIDDEN);
                        break;
                    case "sescredit_number_point":
                        fld.setType(Constant.NUMBER);
                        break;
                }
            }
        }
    }


    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }


}
