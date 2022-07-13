package com.sesolutions.ui.credit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.databinding.ActivityPurchingFormBinding;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.Map;
import java.util.regex.Pattern;


public class PurchingFormActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityPurchingFormBinding binding;
    String sescredit_purchase_type="",sescredit_number_point="",sescredit_site_offers="",submitURL="";

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(0);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(Util.manipulateColor(Color.parseColor("#03A9F4" )));//"#A6118010")));


        binding = DataBindingUtil.setContentView(this, R.layout.activity_purching_form);

        if(getIntent().hasExtra("sescredit_purchase_type")){
            sescredit_purchase_type=getIntent().getStringExtra("sescredit_purchase_type");
         }
        if(getIntent().hasExtra("sescredit_number_point")){
            sescredit_number_point=getIntent().getStringExtra("sescredit_number_point");
        }
        if(getIntent().hasExtra("sescredit_site_offers")){
            sescredit_site_offers=getIntent().getStringExtra("sescredit_site_offers");
        }


        binding.tvTitle.setText("Cashfree payment");

        callSignUpApi();

        binding.formElementButton.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);



    }


    public boolean isNetworkAvailable(Context context) {
        boolean result = false;
        try {
            result = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return result;
    }

    public String getCookie() {
        return TextUtils.isEmpty(Constant.SESSION_ID) ? SPref.getInstance().getCookie(this) : Constant.SESSION_ID;
    }


    public Map<String, Object> map;
    PurchesModelForm vo=new PurchesModelForm();

    private void callSignUpApi() {
        try {
            if (isNetworkAvailable(this)) {
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.CREDIT_PURCHASE_FORM);
                    if (null != map) {
                        request.params.putAll(map);
                    }


                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(this));
                    request.params.put("sescredit_purchase_type", sescredit_purchase_type);
                    request.params.put("sescredit_number_point", sescredit_number_point);
                    if(sescredit_site_offers.length()>0){
                        request.params.put("sescredit_site_offers",sescredit_site_offers);
                    }

                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, this.getCookie());
                    Handler.Callback callback = msg -> {
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                 vo = new Gson().fromJson(response, PurchesModelForm.class);

                                try {
                                    for(int k=0;k<vo.getResult().getFormFields().size();k++){
                                        if(vo.getResult().getFormFields().get(k).getName().equals("customerEmail")){
                                            binding.etEmail.setText(""+vo.getResult().getFormFields().get(k).getValue());
                                        }
                                        if(vo.getResult().getFormFields().get(k).getName().equals("customerName")){
                                            binding.etName.setText(""+vo.getResult().getFormFields().get(k).getValue());
                                        }

                                        if(vo.getResult().getFormFields().get(k).getName().equals("customerPhone")){
                                            binding.etPhone.setText(""+vo.getResult().getFormFields().get(k).getValue());
                                        }

                                        if(vo.getResult().getFormFields().get(k).getName().equals("submit")){
                                            binding.formElementButton.setText(""+vo.getResult().getFormFields().get(k).getLabel());
                                        }

                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                                try {
                                    submitURL=vo.getResult().getCustomParams().getSubmitURL();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }


                            } else {
                                notInternetMsg(binding.mainlayout);
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(this, new Handler(callback)).run(request);
                } catch (Exception e) {

                }
            } else {
                notInternetMsg(binding.mainlayout);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void notInternetMsg(View v) {
        Util.showSnackbar(v,getString(R.string.MSG_NO_INTERNET));
    }

    public void messgeerror(View v,String msg) {
        Util.showSnackbar(v,msg);
    }


    public static void hideKeyBoard(Context context,View mainLayout) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.formElementButton:

               hideKeyBoard(this,binding.mainlayout);

                if(binding.etPhone.getText().toString().length()>9){
                    if(binding.etEmail.getText().toString().length()>0 && isValidEmailId(binding.etEmail.getText().toString())){
                        String url = submitURL;

                        try {
                            for(int k=0;k<vo.getResult().getFormFields().size();k++){
                                if(vo.getResult().getFormFields().get(k).getName().equals("customerEmail")){
                                    url = url + "&customerEmail=" + binding.etEmail.getText().toString();
                                }
                               else if(vo.getResult().getFormFields().get(k).getName().equals("customerName")){
                                    url = url + "&customerName=" + binding.etName.getText().toString();
                                }
                               else if(vo.getResult().getFormFields().get(k).getName().equals("customerPhone")){
                                    url = url + "&customerPhone=" + binding.etPhone.getText().toString();
                                }else {
                                         url = url + "&"+vo.getResult().getFormFields().get(k).getName()+"=" + vo.getResult().getFormFields().get(k).getValue();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        UserMaster userVo = SPref.getInstance().getUserMasterDetail(this);
                        url = url + "&user_id=" + userVo.getUserId();
                        openWebView(url, "");
                    }
                    else {
                        messgeerror(binding.mainlayout,"Please enter valid email!");
                    }
                }else {
                    messgeerror(binding.mainlayout,"Please enter 10 digit number!");
                }


                break;
            case R.id.ivBack:
                finish();
                break;
        }
    }

    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }


    public void openWebView(String url, String title) {
            Intent intent = new Intent(this, CommonActivity.class);
            intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_WEBVIEW);
            intent.putExtra(Constant.KEY_URI, url);
            intent.putExtra(Constant.KEY_TITLE, title);
            startActivity(intent);
    }

}