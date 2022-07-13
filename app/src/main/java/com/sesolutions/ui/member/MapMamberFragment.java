package com.sesolutions.ui.member;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.databinding.MemberUserLayoutBinding;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Notifications;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

public class MapMamberFragment extends BaseFragment implements View.OnClickListener,
        GoogleMap.OnMarkerClickListener,  GoogleMap.OnMapClickListener {


    public static MapMamberFragment newInstance(int userId,List<Notifications> alumlist) {
        MapMamberFragment frag = new MapMamberFragment();
        frag.userId = userId;
        frag.albumsList= (ArrayList<Notifications>) alumlist;
        return frag;
    }

    private GoogleMap googleMap;
    private Activity mActivity;
    private MemberUserLayoutBinding binding;
    private String activityFilterIds = "", languageFilterIds = "", transportFilterIds = "", genderFilter = "",
            min_price = "0", max_price = "100", latitude = "", longitude = "", address = "";
    private Boolean electric_status=false,security_status=false;
    private boolean isFilterSet = false;
    private int currentGuide;
    private ArrayList<Double> latitudes, longitudes;
    private String API_AFTER_REFRESH_TOKEN = "";
    int Bit_map_icon=250;
    private Bitmap boat,boat_express,skateboard_express,skateboard,cycle_express,cycle,scotor,scotor_express;

    public ArrayList<Notifications> albumsList;
    public int userId;
    public CommonResponse.Result result;
    public boolean isLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    public static void setStatusBarGradient(Activity activity, int drawable) {
        Window window = activity.getWindow();
        Drawable background = activity.getResources().getDrawable(drawable);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.member_user_layout, container, false);


        binding.llFilter.setOnClickListener(this);
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.onResume(); // needed to get the map to display immediately

        BitmapDrawable bitmap_yellow = (BitmapDrawable) getResources().getDrawable(R.drawable.cycle_express);
        Bitmap b = bitmap_yellow.getBitmap();
        boat = Bitmap.createScaledBitmap(b, Bit_map_icon, Bit_map_icon, false);

        BitmapDrawable bitmap_green = (BitmapDrawable) getResources().getDrawable(R.drawable.cycle_express);
        Bitmap b1 = bitmap_green.getBitmap();
        boat_express = Bitmap.createScaledBitmap(b1, Bit_map_icon, Bit_map_icon, false);

        BitmapDrawable bitmap_red = (BitmapDrawable) getResources().getDrawable(R.drawable.cycle_express);
        Bitmap b2 = bitmap_red.getBitmap();
        skateboard_express = Bitmap.createScaledBitmap(b2, Bit_map_icon,Bit_map_icon, false);

        BitmapDrawable bitmap_red_sk = (BitmapDrawable) getResources().getDrawable(R.drawable.cycle_express);
        Bitmap b2_sk = bitmap_red_sk.getBitmap();
        skateboard = Bitmap.createScaledBitmap(b2_sk, Bit_map_icon,Bit_map_icon, false);

        BitmapDrawable bitmap_red_cye = (BitmapDrawable) getResources().getDrawable(R.drawable.cycle_express);
        Bitmap b2_cye = bitmap_red_cye.getBitmap();
        cycle_express = Bitmap.createScaledBitmap(b2_cye, Bit_map_icon,Bit_map_icon, false);

        BitmapDrawable bitmap_red_cy = (BitmapDrawable) getResources().getDrawable(R.drawable.cycle_express);
        Bitmap b2_cy = bitmap_red_cy.getBitmap();
        cycle = Bitmap.createScaledBitmap(b2_cy, Bit_map_icon,Bit_map_icon, false);

        BitmapDrawable bitmap_red_sc = (BitmapDrawable) getResources().getDrawable(R.drawable.cycle_express);
        Bitmap b2_sc = bitmap_red_sc.getBitmap();
        scotor = Bitmap.createScaledBitmap(b2_sc, Bit_map_icon,Bit_map_icon, false);

        BitmapDrawable bitmap_red_sce = (BitmapDrawable) getResources().getDrawable(R.drawable.cycle_express);
        Bitmap b2_sce = bitmap_red_sce.getBitmap();
        scotor_express = Bitmap.createScaledBitmap(b2_sce, Bit_map_icon,Bit_map_icon, false);

        try {
            MapsInitializer.initialize(mActivity.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //getMap();
      //  callMusicAlbumApi(1);
        new GetBitmapFromUrl().execute(albumsList);

        return binding.getRoot();
    }



    @SuppressLint("MissingPermission")
    public void getMap() {

        binding.mapView.getMapAsync(mMap -> {
            googleMap = mMap;
            googleMap.setMinZoomPreference(0f);
            googleMap.setMaxZoomPreference(20f);
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnMapClickListener(this);
        });

    }

//    private boolean checkLocationPermission() {
//
////        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
////                != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
////            return false;
////        } else {
////            return true;
////        }
//    }

    public  int LOCATION_PERMISSION_REQUEST = 8;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //getMap();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }


    @Override
    public boolean onMarkerClick(Marker marker) {

         binding.llFilter.setVisibility(View.GONE);
         currentGuide = (int) marker.getZIndex();
         animateMap("26.9124","75.7873");
        return false;
    }

    private void animateMap(String latitude, String longitude) {

        if (latitude == null || latitude.isEmpty())
            latitude = "0.0";

        if (longitude == null || longitude.isEmpty())
            longitude = "0.0";

        LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(13).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onMapClick(LatLng latLng) {

        binding.llFilter.setVisibility(View.VISIBLE);
    }




    @SuppressLint("StaticFieldLeak")
    class GetBitmapFromUrl extends AsyncTask<ArrayList<Notifications>, Void, ArrayList<Bitmap>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final ArrayList<Bitmap> doInBackground(ArrayList<Notifications>... arrayLists) {
            URL url;
            ArrayList<Bitmap> bmp = new ArrayList<>();
            latitudes = new ArrayList<>();
            longitudes = new ArrayList<>();
            for (int i = 0; i < arrayLists[0].size(); i++) {
                Notifications guideInfo = arrayLists[0].get(i);
                if (guideInfo != null) {
                        latitudes.add(26.9+i);
                        longitudes.add(75.7873-(i/2));
                    try {
                        if (guideInfo.getUserImage().isEmpty()) {
                            bmp.add(BitmapFactory.decodeResource(mActivity.getResources(),
                                    R.drawable.circle_bg_grey));
                        } else {
                            url = new URL(guideInfo.getUserImage());
                            bmp.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmap) {
            super.onPostExecute(bitmap);

            for (int i = 0; i < bitmap.size(); i++) {
                View customMarkerView = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
                ImageView markerImageView = customMarkerView.findViewById(R.id.ivUserImage);

                markerImageView.setImageBitmap(bitmap.get(i));
                customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
                customMarkerView.buildDrawingCache();
                Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(returnedBitmap);
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
                Drawable drawable = customMarkerView.getBackground();
                if (drawable != null)
                    drawable.draw(canvas);
                customMarkerView.draw(canvas);

                LatLng latLng = new LatLng(latitudes.get(i), longitudes.get(i));

                googleMap.addMarker(new MarkerOptions().position(latLng).zIndex(i).icon(BitmapDescriptorFactory.fromBitmap(returnedBitmap)));
            }
//            if (checkLocationPermission()) {
//
//                // For showing a move to my location button
//                googleMap.setMyLocationEnabled(false);
//
//                String currentLat="26.9124";
//                 String currentLng=" 75.7873";
//
//                LatLng currentLatLng = null;
//                if (currentLat != null && !currentLat.isEmpty() && currentLng != null && !currentLng.isEmpty())
//                    currentLatLng = new LatLng(Double.parseDouble(currentLat), Double.parseDouble(currentLng));
//
//                if (currentLatLng != null) {
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLatLng).zoom(13).build();
//                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                }
//            }
        }
    }


    private void GetBitMapList(ArrayList<Notifications> guideList) {

        for (int i = 0; i < guideList.size(); i++) {
            View customMarkerView = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
            ImageView markerImageView = customMarkerView.findViewById(R.id.ivUserImage);
            String file_name=getFileName(""+guideList.get(i).getMainImageUrl());
            if(file_name.equalsIgnoreCase("scotor_express")){
                markerImageView.setImageBitmap(scotor_express);
            }else if(file_name.equalsIgnoreCase("scotor")){
                markerImageView.setImageBitmap(scotor);
            }else if(file_name.equalsIgnoreCase("skateboard_express")){
                markerImageView.setImageBitmap(skateboard_express);
            }else if(file_name.equalsIgnoreCase("skateboard")){
                markerImageView.setImageBitmap(skateboard);
            }else if(file_name.equalsIgnoreCase("cycle_express")){
                markerImageView.setImageBitmap(cycle_express);
            }else if(file_name.equalsIgnoreCase("cycle")){
                markerImageView.setImageBitmap(cycle);
            }else if(file_name.equalsIgnoreCase("boat_express")){
                markerImageView.setImageBitmap(boat_express);
            }else if(file_name.equalsIgnoreCase("boat")){
                markerImageView.setImageBitmap(boat);
            }

            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
            customMarkerView.buildDrawingCache();
            Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Drawable drawable = customMarkerView.getBackground();
            if (drawable != null)
                drawable.draw(canvas);
            customMarkerView.draw(canvas);
            LatLng latLng=null;
            try {
                latLng = new LatLng(Double.parseDouble(guideList.get(i).getPro_latitude()), Double.parseDouble(guideList.get(i).getPro_longitude()));
                googleMap.addMarker(new MarkerOptions().position(latLng).zIndex(i).icon(BitmapDescriptorFactory.fromBitmap(returnedBitmap)));
            }catch (Exception e){
                e.printStackTrace();
            }

        }
//        if (checkLocationPermission()) {
//
//            // For showing a move to my location button
//            googleMap.setMyLocationEnabled(true);
//
//            String currentLat="26.9124";
//            String currentLng="75.7873";
//
//            LatLng currentLatLng = null;
//            if (currentLat != null && !currentLat.isEmpty() && currentLng != null && !currentLng.isEmpty())
//                currentLatLng = new LatLng(Double.parseDouble(currentLat), Double.parseDouble(currentLng));
//
//            if (currentLatLng != null) {
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLatLng).zoom(13).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            }
//        }

    }

    public static String getFileName(String url) {
        String fileName;
        int slashIndex = url.lastIndexOf("/");
        int qIndex = url.lastIndexOf("?");
        if (qIndex > slashIndex) {//if has parameters
            fileName = url.substring(slashIndex + 1, qIndex);
        } else {
            fileName = url.substring(slashIndex + 1);
        }
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }

        return fileName;
    }







}
