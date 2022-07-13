package com.sesolutions.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.sesolutions.BuildConfig;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.customviews.CircularProgressBar;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Util {
    private static final String KEY_SIMPLE_DATE_FORMAT = "SimpleDateFormat";
    private static RequestOptions requestOptions;

    public Util() {

    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getBodyWebContent(String url) {

        String baseurl=BuildConfig.BASE_URL;
        String preurl="<link href=";
        String posturl="/application/modules/Sesapi/externals/styles/tinymce.css\" type=\"text/css\" rel=\"stylesheet\">";

        String information=preurl+baseurl+posturl+""+url;
        return information;
    }

    private static RequestOptions initPlaceholder() {
        try {
            if (requestOptions == null) {
                requestOptions = new RequestOptions().dontAnimate().dontTransform().placeholder(R.drawable.placeholder_3_2);
                //  requestOptions.error(R.drawable.placeholder_3_2);
            }
        } catch (Exception e) {
            CustomLog.e(e);
            return requestOptions = new RequestOptions();

        }
        return requestOptions;
    }

    @NonNull
    @TargetApi(26)
    public synchronized String createChannel(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelName = BuildConfig.APP_NAME.replace(" ", "");
        String channelId = BuildConfig.APP_NAME.replace(" ", "");
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
        return channelId;
    }

    /**
     * Check if service is running or not
     *
     * @param serviceName
     * @param context
     * @return
     */
    public static boolean isServiceRunning(String serviceName, Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceName.equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (SecurityException e) {
            CustomLog.e(e);
        }
        return false;
    }

    public static void showOptionsPopUp(View v, int position, List<Options> options, OnUserClickedListener<Integer, Object> listener) {
        try {
            FeedOptionPopup popup = new FeedOptionPopup(v.getContext(), position, listener, options);
            int vertPos = RelativePopupWindow.VerticalPosition.CENTER;
            int horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_LEFT;
            popup.showOnAnchor(v, vertPos, horizPos, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static void showImageWithProgress(ImageView imageView, CircularProgressBar progressBar, String url, int index, OnUserClickedListener<Integer, Object> listener) {
        new GlideImageLoader(imageView, progressBar, index, listener).load(url, new RequestOptions().priority(Priority.HIGH));
    }

    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        return sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean currentVersionSupportLockScreenControls() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        return sdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }


    /*public static int lighten(int color, double fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int number = Color.argb(125, red, green, blue);
        Log.e("ACT", String.format("#%x", number)); // use lower case x for
        // lowercase hex
        Log.e("ACT", "#" + Integer.toHexString(number));
        return number;
    }*/

   /* private static int lightenColor(int color, double fraction) {
        return (int) Math.min(color + (color * fraction), 255);
    }*/

    public static int manipulateColor(int color) {
        // lighten(color, 0.8f);
        return manipulateColor(color, 0.8f);
    }

    public static int manipulateColor(int color, float factor) {

        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    public static String getCurrentdate(String dateFormat) {
        String dateCurrent = "";
        try {
            Calendar c = Calendar.getInstance();
            Date date = c.getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat);
            dateCurrent = dateFormatter.format(date);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return dateCurrent;
    }

    public static Date getDateFromString(String date, String format) {
        Date startDate = new Date();
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat(format);
            startDate = df.parse(date);
        } catch (ParseException e) {
            CustomLog.e(e);
        }
        return startDate;
    }

    public static Date convertStringToDate(String dateString) {
        Date date = null;
        try {
            Calendar c = Calendar.getInstance();
            String[] arr = dateString.split("-");
            c.set(Integer.parseInt(arr[2]), Integer.parseInt(arr[1]), Integer.parseInt(arr[0]));
            date = c.getTime();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return date;
    }

    public static String changeDateFormat(Context context, String dateString) {
        if (null == dateString)
            return " ";
        return getDateDiff(context, dateString);
    }

    public static String changeFormat(String dateString) {
        //String date_s = " 2011-01-18 00:00:00.0";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = dt.parse(dateString);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt1 = new SimpleDateFormat("MMM dd, yyyy");
            dateString = dt1.format(date);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return dateString;
    }

    public static String changeFormatDonation(String dateString) {
        //String date_s = " 2011-01-18 00:00:00.0";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = dt.parse(dateString);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt1 = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
            // @SuppressLint("SimpleDateFormat") SimpleDateFormat dt2 = new SimpleDateFormat("hh:mm a");
            dateString = dt1.format(date);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return dateString;
    }

    public static String changeFormatStory(String dateString) {
        /*DateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        dateFormat.parse("Tue Jul 13 00:00:00 CEST 2011");
        System.out.println(dateFormat.format(new Date()));*/
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = dt.parse(dateString);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt1 = new SimpleDateFormat("EEE MMM dd HH:mm a");
            dateString = dt1.format(date);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return dateString;
    }

    public static String changeDate(String dateString) {
        String dateFormat = "MMM dd";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = dt.parse(dateString);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt1 = new SimpleDateFormat(dateFormat);
            dateString = dt1.format(date);
        } catch (ParseException e) {
            CustomLog.e(e);
        }
        return dateString;
    }

    public static String changeDate2(String dateString) {
        String dateFormat = "MMM dd, yyyy";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = dt.parse(dateString);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt1 = new SimpleDateFormat(dateFormat);
            dateString = dt1.format(date);
        } catch (ParseException e) {
            CustomLog.e(e);
        }
        return dateString;
    }


    public static String getEventDate(String startTime, String endTime) {
        return changeDate(startTime) + " - " + changeDate(endTime);
    }

    public static String getSesEventDate(String startTime, String endTime) {


        return changeDateFormat(startTime, Constant.DATE_FROMAT_EVENT)
                + " - " +
                changeDateFormat(endTime, Constant.DATE_FROMAT_EVENT);
    }

    public static String getPostedOnDate(String dateString) {
        //String date_s = " 2011-01-18 00:00:00.0";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = dt.parse(dateString);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat dt1 = new SimpleDateFormat("MMM dd");
            dateString = dt1.format(date);
        } catch (ParseException e) {
            CustomLog.e(e);
        }
        return Constant.POSTED + " " + dateString;
    }


    public static String getCurrentdate(Date date) {
        return getCurrentdate(date, Constant.DATE_FROMAT_POST_FEED);
       /* String dateCurrent = "";
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(Constant.DATE_FROMAT_POST_FEED);
            dateCurrent = dateFormatter.format(date);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return dateCurrent;*/
    }

    public static String getCurrentdate(Date date, String format) {
        String dateCurrent = "";
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
            dateCurrent = dateFormatter.format(date);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return dateCurrent;
    }

    public static String changeToEventFormat(String dateCurrent) {
        return changeDateFormat(dateCurrent, Constant.DATE_FROMAT_EVENT);
    }

    public static String changeDateFormat(String dateCurrent, String format) {
        try {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = dt.parse(dateCurrent);
            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
                dateCurrent = dateFormatter.format(date);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        } catch (ParseException e) {
            CustomLog.e(e);
        }
        return dateCurrent;
    }


    public static String getCurrentdate() {
        String dateAsString = "";
        try {
            Calendar c = Calendar.getInstance();
            Date date = c.getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
            //   SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
            dateAsString = dateFormatter.format(date);
            //  String timeAsString = timeFormatter.format(date);
            // dateCurrent = dateAsString/* + " at " + timeAsString*/;
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return dateAsString;
    }

    /**
     * get System 24 hour next date time in Date format
     */
    public static Date getNext24HourDateTime() {
        Calendar cal = Calendar.getInstance();
        // cal.setTimeZone(TimeZone.getTimeZone(Constant.TIME_ZONE));
        cal.add(Calendar.HOUR, 0);
        cal.add(Calendar.MINUTE, 0);
        cal.add(Calendar.HOUR, 24);
        // cal.add_create(Calendar.MINUTE, 1);
        return cal.getTime();
    }

    public static long getCurrentDateInMillis() {
        long dateCurrent = 0;
        try {
            Calendar c = Calendar.getInstance();
            Date date;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
            String dateAsString = dateFormatter.format(c.getTime());
            date = dateFormatter.parse(dateAsString);
            dateCurrent = date.getTime();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return dateCurrent;
    }

    public static long getDateStringInMillis(String dateAsString) {
        long dateCurrent = 0;
        try {
            Calendar c = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            //  String dateAsString = dateFormatter.format(c.getTime());
            Date date = dateFormatter.parse(dateAsString);
            dateCurrent = date.getTime();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return dateCurrent;
    }


    public static String getPreviousMonthDate() {
        long unixtime = 0;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmm");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));//Specify your timezone
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30);
        try {
            unixtime = dateFormatter.parse(dateFormatter.format(cal.getTime())).getTime();//cal.getTime();
            unixtime = unixtime / 1000;
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return "" + unixtime;
    }
    public static String stripHtml(String str) {
        return StringEscapeUtils.unescapeHtml4(str.replaceAll("<[A-Za-z/].*?>", "")).trim();
    }

    public static String getDateDiff(Context context, String dateCreated) {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sourceFormat = new SimpleDateFormat(Constant.DATE_FROMAT_FEED);
            sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String dateCurrent = sourceFormat.format(new Date());

            Date d1 = sourceFormat.parse(dateCreated);
            Date d2 = sourceFormat.parse(dateCurrent);
            Long diff = d2.getTime() - d1.getTime();
            @SuppressWarnings("NumericOverflow")
            Long diffDays = diff / (24 * 60 * 60 * 1000);
            if (diffDays == 0) {
                Long diffHours = diff / (60 * 60 * 1000) % 24;
                if (diffHours > 0) {
                    return context.getResources().getQuantityString(R.plurals.hours_ago, diffHours.intValue(), diffHours.intValue());
                } else {
                    Long diffMinutes = diff / (60 * 1000) % 60;
                    if (diffMinutes > 0)
                        return context.getResources().getQuantityString(R.plurals.minutes_ago, diffMinutes.intValue(), diffMinutes.intValue());
                    else {
                        return context.getString(R.string.few_seconds_ago);
                    }
                }
            } else if (diffDays == 1) {
                return context.getResources().getQuantityString(R.plurals.days_ago, diffDays.intValue(), diffDays.intValue());
            } else {
                return changeFormat(dateCreated);
            }
        } catch (Exception e) {
            CustomLog.e(e);
            return "";
        }
    }

    public static String getDateDifference(Context context, String dateCreated) {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sourceFormat = new SimpleDateFormat(Constant.DATE_FROMAT_FEED);
            sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String dateCurrent = sourceFormat.format(new Date());

            Date d1 = sourceFormat.parse(dateCreated);
            Date d2 = sourceFormat.parse(dateCurrent);
            Long diff = d2.getTime() - d1.getTime();
            @SuppressWarnings("NumericOverflow")
            Long diffDays = diff / (24 * 60 * 60 * 1000);
            if (diffDays == 0) {
                Long diffHours = diff / (60 * 60 * 1000) % 24;
                if (diffHours > 0) {
                    return diffHours.intValue() + "h";
                } else {
                    Long diffMinutes = diff / (60 * 1000) % 60;
                    if (diffMinutes > 0)
                        return diffMinutes.intValue() + "m";
                    else {
                        return context.getString(R.string.just_now);
                    }
                }
            } else if (diffDays > 0) {
                return diffDays.intValue() + "d";
            } else {
                return changeDate(dateCreated);
            }
        } catch (Exception e) {
            CustomLog.e(e);
            return "";
        }
    }


    public static boolean isDateExpired(String dateCreated, String dateFormat) {
        boolean result = false;
        try {
            @SuppressLint({KEY_SIMPLE_DATE_FORMAT, "SimpleDateFormat"}) SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            if (!dateCreated.equalsIgnoreCase(sdf.format(new Date()))) {
                result = sdf.parse(sdf.format(new Date())).after(sdf.parse(dateCreated));
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return result;
    }

    public static int isDateToday(String dateCreated, String dateFormat) {
        int result = 1;
        try {
            @SuppressLint({KEY_SIMPLE_DATE_FORMAT, "SimpleDateFormat"}) SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            @SuppressLint(KEY_SIMPLE_DATE_FORMAT) SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy");
            result = sdf2.parse(sdf2.format(new Date())).compareTo(sdf2.parse(sdf2.format(sdf.parse(dateCreated))));
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return result;
    }

    public static boolean isDateGreater(String dateFirst, String dateSecond, String dateFormat) {
        boolean result = false;
        try {
            @SuppressLint({KEY_SIMPLE_DATE_FORMAT, "SimpleDateFormat"}) SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            result = sdf.parse(dateFirst).after(sdf.parse(dateSecond));
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return result;
    }

    public static void showToast(Context context, String text) {
        try {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static void showSnackbar(View view, String text) {
        try {
            if (null != view) {
                Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                        .setAction("Ok", null).setDuration(3000).show();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static String getKeyFromValue(Map hm, Object value) {
        String s = "";
        String[] arrStr = value.toString().trim().split(",");
        for (String i : arrStr) {
            for (Object o : hm.keySet()) {
                if (hm.get(o).equals(i.trim())) {
                    if (i.equalsIgnoreCase(arrStr[arrStr.length - 1]))
                        s += (String) o;
                    else
                        s += (String) o + ", ";
                }
            }
        }
        return s;
    }

    public static String getKeyFromValue2(Map hm, Object value) {
        String s = "";
        String[] arrStr = value.toString().trim().split(",");
        for (String i : arrStr) {
            for (Object o : hm.keySet()) {
                if (hm.get(o).equals(i.trim())) {
                    if (i.equalsIgnoreCase(arrStr[arrStr.length - 1])){
                        s += (String) o;
                        break;
                    }
                    else{
                        s += o + ", ";
                        break;
                    }
                }
            }
        }
        return s;
    }

    public static String getKeyFromValue215(Map hm, Object value) {
        String s = "";
        //  String[] arrStr = value.toString().trim().split(",");
        for (Object o : hm.keySet()) {
            String str1= (String) hm.get(o);
            String str2= (String) value.toString().trim();
            String finalst=str1.trim();
            if(finalst.length()>0 && str2.length()>0){
                if (finalst.contains(str2)){
                    s += (String) o;
                }
            }
        }
        return s;
    }

    public static String getKeyFromValue216(Map hm, Object value) {
        String s = "";
        //  String[] arrStr = value.toString().trim().split(",");
        for (Object o : hm.keySet()) {
            String str1= (String) hm.get(o);
            String str2= (String) value.toString().trim();
            String finalst=str1.trim();
            if(finalst.length()>0 && str2.length()>0){
                if (finalst.equalsIgnoreCase(str2)){
                    s += (String) o;
                }
            }
        }
        return s;
    }




    public static String getKeyFromValue21(Map hm, Object value) {
        String s = "";
        //  String[] arrStr = value.toString().trim().split(",");
        for (Object o : hm.keySet()) {
            String str1= (String) hm.get(o);
            String str2= (String) value.toString().trim();
            if (str1.equalsIgnoreCase(str2)){
                s += (String) o;
            }
        }
        return s;
    }



    public static void showImageWithGlide(ImageView ivUserImage, String path, Context context, int drawable) {
        try {
            if (("" + path).endsWith(".gif")) {
                Util.showAnimatedImageWithGlide(ivUserImage, path, context);
                return;
            }
            Glide.with(ivUserImage.getContext()).setDefaultRequestOptions(initPlaceholder().placeholder(drawable)).load(path).into(ivUserImage);
        } catch (Exception e) {
            CustomLog.e("GLIDE", "error loading url : " + path);
            // CustomLog.e(e);
        }
    }

    public static void showImageWithGlide123(ImageView ivUserImage, String path, int drawable) {
        try {
            Glide.with(ivUserImage.getContext()).setDefaultRequestOptions(initPlaceholder().placeholder(drawable)).load(path).into(ivUserImage);
        } catch (Exception e) {
            CustomLog.e("GLIDE", "error loading url : " + path);
            // CustomLog.e(e);
        }
    }
    public static void showImageWithGlideGIF(ImageView ivUserImage, String path, Context context, int drawable) {
        try {
                Util.showAnimatedImageWithGlide(ivUserImage, path, context);
            } catch (Exception e) {
            CustomLog.e("GLIDE", "error loading url : " + path);
        }
    }

    public static void showImageWithGlide(ImageView ivUserImage, String path, int drawable) {
        try {
            Glide.with(ivUserImage.getContext()).setDefaultRequestOptions(initPlaceholder().placeholder(drawable)).load(path).into(ivUserImage);
        } catch (Exception e) {
            CustomLog.e("GLIDE", "error loading url : " + path);
            // CustomLog.e(e);
        }
    }

    public static void showImageWithGlide(ImageView ivUserImage, String path, Context context) {
        try {
            if (("" + path).endsWith(".gif")) {
                Util.showAnimatedImageWithGlide(ivUserImage, path, context);
                return;
            }
            Glide.with(context).load(path)/*.transition(withCrossFade())*/.into(ivUserImage);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static void showImageWithGlide(ImageView ivUserImage, String path) {
        showImageWithGlide(ivUserImage, path, ivUserImage.getContext());
    }

    public static void showImageTintedPlaceholder(ImageView ivUserImage, String path, @DrawableRes int drawableRes) {

        Drawable drawable = ContextCompat.getDrawable(ivUserImage.getContext(), drawableRes).mutate();
        // new LightingColorFilter( Color.BLACK, Color.BLACK);
        drawable.setColorFilter(SesColorUtils.getPrimaryColor(ivUserImage.getContext()), PorterDuff.Mode.MULTIPLY);
        Glide.with(ivUserImage.getContext()).setDefaultRequestOptions(new RequestOptions().dontAnimate().dontTransform().placeholder(drawable)).load(path)./*thumbnail(0.1f).*/into(ivUserImage);
    }

    public static void showImageWithGlide(ImageView ivImage, File path, Context context) {
        try {
            if (("" + path).endsWith(".gif")) {
                Glide.with(context)
                        .setDefaultRequestOptions(new RequestOptions()
                                .placeholder(R.drawable.placeholder_3_2)
                                .error(R.drawable.placeholder_3_2))
                        .load(path)

                        .into(ivImage);
                return;
            }
            Glide.with(context).load(path).apply(new RequestOptions().override(ivImage.getWidth(), ivImage.getWidth())).into(ivImage);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static void showAnimatedImageWithGlide(ImageView ivImage, String path, Context context) {
        try {
            Glide.with(context)
                    .setDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.placeholder_3_2)
                            .error(R.drawable.placeholder_3_2))
                    .load(path)
                    .into(ivImage);

        } catch (Exception e) {
            CustomLog.e(e);
            //Glide.with(context).load(path).into(ivImage);
        }
    }

    public static void cacheImageWithGlide(String path, Context context) {
        new Thread(() -> {
            try {
                Glide.with(context).load(path).preload();
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }).run();
    }

    public static void cacheImageWithGlide(List<String> paths, Context context) {
        new Thread(() -> {
            try {
                if (null != paths) {
                    RequestManager glide = Glide.with(context);
                    for (String path : paths) {
                        glide.load(path).preload();
                    }
                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }).run();
    }

    public static String getCode(String name, boolean isForColorCode) {
        String fontIconName = "";
        String colorCode = "#ababab";
        try {
            switch (name) {

                case "elivestreaming":
                    fontIconName = "\uf03d";
                    colorCode = Constant.ColorHex.CHECKIN;
                    break;

                case "addPhoto":
                    fontIconName = "\uf03e";
                    colorCode = Constant.ColorHex.Photo;
                    break;

                case "addPoll":
                    fontIconName = "\uf080";
                    colorCode = Constant.ColorHex.POLL;
                    break;

                case "addVideo":

                    fontIconName = "\uf03d";
                    colorCode = Constant.ColorHex.VIDEO;

                    break;
                case "checkIn":
                    fontIconName = "\uf041";
                    colorCode = Constant.ColorHex.CHECKIN;

                    break;
                case "addFile":
                    fontIconName = "\uf15b";
                    colorCode = Constant.ColorHex.Photo;

                    break;
                case "addLink":

                    fontIconName = "\uf0c1";
                    colorCode = Constant.ColorHex.LINK;

                    break;
                case "sellSomething":
                    fontIconName = "\uf02b";
                    colorCode = Constant.ColorHex.VIDEO;

                    break;
                case "scheduledPost":
                    fontIconName = "\uf017";
                    colorCode = Constant.ColorHex.SCHEDULE_POST;
                    break;
                case "tagPeople":
                    fontIconName = "\uf007";
                    colorCode = Constant.ColorHex.TAG_PEOPLE;

                    break;
                case "emotions":
                    fontIconName = "\uf118";
                    colorCode = Constant.ColorHex.FEELING_ACTIVITY;

                    break;
                case "addMusic":
                    fontIconName = "\uf001";
                    colorCode = Constant.ColorHex.CHECKIN;
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return isForColorCode ? colorCode : fontIconName;
    }


    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     */
    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */
    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        try {
            totalDuration = totalDuration / 1000;
            currentDuration = (int) ((((double) progress) / 100) * totalDuration);
        } catch (Exception e) {
            CustomLog.e(e);
        }

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public static void createDirectory(String imagePath) {
        File dir = new File(imagePath);
        try {
            if (dir.mkdir()) {
                CustomLog.d("mkdir", "directory created");
            } else {
                CustomLog.d("mkdir", "directory creation unsuccessfull");
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static String getAppFolderPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + BuildConfig.APP_NAME.replace(" ", "") + "/";
    }

    // Custom method to get a darker color
    protected int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 0.8f * hsv[2];
        return Color.HSVToColor(hsv);
    }

    // Custom method to get a lighter color
    protected int getLighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 0.2f + 0.8f * hsv[2];
        return Color.HSVToColor(hsv);
    }





    /*  public static Spannable getSpannedText(String text, String searchedQuery, int color) {
          Spannable highlight = (Spannable) Html.fromHtml(text);
          try {
              if (Util.isNotNullOrEmpty(searchedQuery)) {
                  String[] words = searchedQuery.split(" ");
                  if (words.length > 0) {
                      String titleStr = Html.fromHtml(text).toString();
                      for (String word : words) {
                          word = " " + word.replace("(", "").replace(")", "").replace(".", "").trim() + " ";
                          Pattern pattern = Pattern.compile("(?i)" + word);
                          Matcher matcher = pattern.matcher(" " + titleStr + " ");
                          while (matcher.find()) {
                              highlight.setSpan(
                                      new ForegroundColorSpan(color),
                                      matcher.start(),
                                      matcher.end() - 2,
                                      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                          }
                      }
                  }
              }
          } catch (Exception e) {
              CustomLog.e(e);
          }
          return highlight;
      }*/
}
