package com.sesolutions.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.UserModel;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    public static final String TABLE_APP_VERSION = "AppVersion";


    private FirebaseHelper() {
    }

    // static variable single_instance of type Singleton
    private static FirebaseHelper mClass = null;

    // private constructor restricted to this class itself

    // static method to create instance of Singleton class
    public static FirebaseHelper getInstance() {
        if (mClass == null)
            mClass = new FirebaseHelper();

        return mClass;
    }

    public void getFirebaseId(OnUserClickedListener<Integer, Object> listener) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            Constant.GCM_DEVICE_ID = instanceIdResult.getToken();
            CustomLog.d("Token", Constant.GCM_DEVICE_ID);
            if (null != listener) {
                listener.onItemClicked(Constant.Events.GCM_FETCHED, Constant.EMPTY, 0);
            }
//kancy
        });
    }

    public void getAppVersion(OnUserClickedListener<Integer, Object> listener) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(TABLE_APP_VERSION);

        Map<String, UserModel> users = new HashMap<>();
        users.put("alanisawesome", new UserModel("June 23", "koushal_Rathor"));
        users.put("gracehop", new UserModel("December 1906", "Grace Hopper"));
        database.setValue(users);


        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    AppVersion user = dataSnapshot.getValue(AppVersion.class);
                    listener.onItemClicked(Constant.Events.APP_VERSION_CHECK, user, 0);
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
