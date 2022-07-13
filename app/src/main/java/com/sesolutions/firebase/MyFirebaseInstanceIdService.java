package com.sesolutions.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.SPref;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService{

    private static final String TAG = "MyFirebaseIIDService";
    private static final String FRIENDLY_ENGAGE_TOPIC = "Chat_Topic";

    /**
     * The Application's current Instance ID token is no longer valid and thus a new one must be requested.
     */

    @Override
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or after a refresh this is
        // where you should do that.
        String token = FirebaseInstanceId.getInstance().getToken();
        UserMaster userVo = SPref.getInstance().getUserMasterDetail(getApplicationContext());
        if (null != userVo && userVo.getUserId() > 0) {
            Map<String, Object> params = params = new HashMap<>();
            params.put("device_id", token);
            //params.put("fcm_id", token);
            params.put(Constant.KEY_USER_ID, userVo.getUserId());
            new ApiController(Constant.URL_UPDATE_TOKEN, params, getApplicationContext(), null, -1).execute();
        }
        Log.e(TAG, "FCM Token: " + token);

        // Once a token is generated, we subscribe to topic.
        // FirebaseMessaging.getInstance().subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);
    }
}
