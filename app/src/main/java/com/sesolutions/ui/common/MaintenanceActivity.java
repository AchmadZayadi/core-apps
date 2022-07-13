package com.sesolutions.ui.common;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.json.JSONObject;

import java.util.HashMap;

public class MaintenanceActivity extends BaseActivity implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    EditText etCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);
        ImageView ivImage = (ImageView) findViewById(R.id.ivImage);
        etCode = findViewById(R.id.etCode);
        findViewById(R.id.bSignIn).setOnClickListener(this);
        Util.showAnimatedImageWithGlide(
                ivImage,
                Constant.MAINTENANCE_IMAGE_URL,
                this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bSignIn:
                if (isNetworkAvailable(this)) {
                    String code = etCode.getText().toString();
                    if (TextUtils.isEmpty(code)) {
                        etCode.setError(" ");
                        etCode.requestFocus();
                        return;
                    }
                    String url = (Constant.BASE_URL + Constant.POST_URL).replace("/?", ("?en4_maint_code=" + code + "&"));
                    new ApiController(url, new HashMap<>(), this, this, 1).execute();
                } else {
                    Util.showSnackbar(etCode, getString(R.string.MSG_NO_INTERNET));
                }
                break;
        }
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        hideBaseLoader();

        if (data != null) {
            try {
                JSONObject json = new JSONObject((String) data);
                if (json.has("message")) {
                    String message = json.optString("message");
                    if ("1".equals(message)) {
                        Constant.SESSION_ID = "PHPSESSID=" + json.getString("session_id") + ";";
                        SPref.getInstance().updateSharePreferences(this, Constant.KEY_COOKIE, "PHPSESSID=" + json.getString("session_id") + ";");
                        Intent intent = new Intent(this, SplashAnimatedActivity.class);
                        intent.putExtra(Constant.KEY_COOKIE, Constant.SESSION_ID);
                       // finish();
                        startActivity(intent);

                    } else {
                        if (json.has("error_message")) {
                            Util.showSnackbar(etCode, json.getString("error_message"));
                        }
                    }
                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
        } else {
            Util.showSnackbar(etCode, getString(R.string.msg_something_wrong));
        }
        return false;
    }
}
