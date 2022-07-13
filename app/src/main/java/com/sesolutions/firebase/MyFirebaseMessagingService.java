package com.sesolutions.firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.responses.MessageInbox;
import com.sesolutions.ui.live.LiveVideoActivity;
import com.sesolutions.responses.PushNotification;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.dashboard.MainActivity;
import com.sesolutions.ui.message.ChatActivity;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by WarFly on 7/3/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Firebase_MSG";
    private final String GROUP_KEY = "GROUP_KEY_RANDOM_NAME";

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        try {
            CustomLog.e("TOKENNYA", mToken);
            UserMaster userVo = SPref.getInstance().getUserMasterDetail(getApplicationContext());
            if (null != userVo && userVo.getUserId() > 0) {
                Map<String, Object> params = params = new HashMap<>();
                params.put("device_id", mToken);
                //params.put("fcm_id", token);
                params.put(Constant.KEY_USER_ID, userVo.getUserId());
                new ApiController(Constant.URL_UPDATE_TOKEN, params, getApplicationContext(), null, -1).execute();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            // If the application is in the foreground handle both data and notification messages here.
            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
            //   CustomLog.e("remotemsg", "" + new Gson().toJson(remoteMessage));
            //           Toast.makeText(this, "notification", Toast.LENGTH_SHORT).show();

            //     RemoteMessage.Notification vo = remoteMessage.getNotification();
            CustomLog.e(TAG, "From: " + remoteMessage.getData().toString());
//            CustomLog.e(TAG, "From: " + new Gson().toJson(remoteMessage.getData()));
            // send push notification always if there are any.
//            if (isAppIsInBackground()) {
                PushNotification pn = new Gson().fromJson(new Gson().toJson(remoteMessage.getData()), PushNotification.class);
                //  String title = ;
                if (pn != null) {
                    sendNotification(pn.getBody(), pn.getTitle(), pn.getSubData());
                }
//            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void sendNotification(String messageBody, String title, PushNotification.SubData data) {
        try {
            NotificationChannel mChannel;
            NotificationManager notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);

            Intent intent = performClick(data);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, data.getObject_id() /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            /*if (notifManager == null) {
                notifManager = (NotificationManager) getSystemService
                        (Context.NOTIFICATION_SERVICE);
            }*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //  Intent intent = new Intent(this, Dashboard.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // PendingIntent pendingIntent;
                // if (mChannel == null) {
                mChannel = new NotificationChannel
                        ("0", title, NotificationManager.IMPORTANCE_HIGH);
                mChannel.setDescription(messageBody);
                mChannel.enableVibration(true);
                notifManager.createNotificationChannel(mChannel);
                //}
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "0");

                pendingIntent = PendingIntent.getActivity(this, data.getObject_id(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentTitle(title)
                        .setSmallIcon(R.drawable.icon_notification_se) // required
                        .setContentText(messageBody)  // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_notification_se))
                        .setBadgeIconType(R.drawable.icon_notification_se)
                        .setContentIntent(pendingIntent)
                        .setGroup(GROUP_KEY)
                        .setGroupSummary(true)
                        .setSound(RingtoneManager.getDefaultUri
                                (RingtoneManager.TYPE_NOTIFICATION));
                Notification notification = builder.build();
                notifManager.notify(data.getObject_id(), notification);
            } else {

                // Intent intent = new Intent(this, Dashboard.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //PendingIntent pendingIntent = null;
                //  pendingIntent = PendingIntent.getActivity(this, data.getObject_id(), intent, PendingIntent.FLAG_ONE_SHOT);
                //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))
                        .setSound(defaultSoundUri)
                        .setSmallIcon(getNotificationIcon())
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(messageBody));

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(data.getObject_id(), notificationBuilder.build());
            }


           /* NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(data.getObject_id(), notificationBuilder.build());*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.icon_notification_se : R.drawable.icon_notification_se;
    }

    public Intent performClick(PushNotification.SubData data) {
        String type = data.getObject_type();
        int id = data.getObject_id();
        String href = data.getHref();
        boolean openComment = data.isCommentLike();
        //in case of any exception ..go to main dashboard
        Intent intent = new Intent(this, MainActivity.class);
        try {
            switch (type) {
                case Constant.ACITIVITY_ACTION:
                    intent = new Intent(this, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_VIEW_FEED);
                    intent.putExtra(Constant.KEY_ACTION_ID, id);
                    intent.putExtra(Constant.KEY_RESOURCE_ID, id);
                    intent.putExtra(Constant.KEY_RESOURCES_TYPE, type);
                    intent.putExtra(Constant.KEY_COMMENT_ID, openComment);
                    break;
                case Constant.ResourceType.LIVE_HOST:
                    intent = new Intent(this, LiveVideoActivity.class);
                    intent.putExtra(Constant.KEY_HOST_ID, data.getHostId());
                    intent.putExtra(Constant.KEY_ACTIVITY_ID, data.getActivityId());
                    break;
                case Constant.FRIEND_REQUEST:
                    intent=new Intent(this, MainActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT,Constant.GoTo.REQUESTS);
                    intent.putExtra(Constant.KEY_RESOURCE_ID, id);
                    intent.putExtra(Constant.KEY_RESOURCES_TYPE, type);
                    break;

                case  Constant.MESSAGE_CONVERSATION:
                    intent = new Intent(this, ChatActivity.class);
                    MessageInbox vo = new MessageInbox();
                    vo.setConversationId(id);
                    vo.setSender(data.getSender());
                    intent.putExtra(Constant.KEY_DATA, (Serializable) vo);
                    break;

                case Constant.ResourceType.PAGE_POLL:
                case Constant.ResourceType.GROUP_POLL:
                case Constant.ResourceType.BUSINESS_POLL:
                    intent = new Intent(this, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.VIEW_POLL);
                    intent.putExtra(Constant.KEY_ID, id);
                    intent.putExtra(Constant.KEY_RESOURCES_TYPE, type);
                    startActivity(intent);

                default:
                    int MODULE = ModuleUtil.getInstance().fetchDestination(type);
                    if (-1 != MODULE) {
                        intent = getDeepLinkIntent(MODULE, Constant.KEY_ID, id);
                    } else if (!TextUtils.isEmpty(href)) {
                        CustomLog.e("FIREBASE_N", "open webview");
                        intent = new Intent(this, CommonActivity.class);
                        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_WEBVIEW);
                        intent.putExtra(Constant.KEY_URI, href);
                        intent.putExtra(Constant.KEY_TITLE, " ");
                        // openWebView(href, Constant.TITLE_ACTIVITY_FEED);
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return intent;
    }

    public Intent getDeepLinkIntent(int goTo, String key, int value) {
        Intent intent = new Intent(this, CommonActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, goTo);
        intent.putExtra(key, value);
        return intent;
    }


    private boolean isAppIsInBackground() {
        Context context = getApplicationContext();
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


}