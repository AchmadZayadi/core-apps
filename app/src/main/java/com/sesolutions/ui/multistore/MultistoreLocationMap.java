package com.sesolutions.ui.multistore;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Notifications;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.responses.videos.VideoBrowse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.member.MemberFragment;
import com.sesolutions.ui.video.CategoryAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MultistoreLocationMap extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, String>, SwipeRefreshLayout.OnRefreshListener,
        GoogleMap.OnMarkerClickListener,  GoogleMap.OnMapClickListener {

    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private String searchKey;
    private com.sesolutions.responses.videos.Result result;
    private ProgressBar pb;
    private List<Category> categoryList;
    public View v;
    public MultiStoreParentFragment parent;
    private CategoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    MapView mapView;
    public ArrayList<Notifications> maplist;


    public static MultistoreLocationMap newInstance(MultiStoreParentFragment parent) {
        MultistoreLocationMap frag = new MultistoreLocationMap();
        frag.parent = parent;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.member_user_layout, container, false);
        init();
        applyTheme(v);
      //  checkLocationPermission();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //getMap();

        mapView.onCreate(saveInstanceState);
        mapView.onResume(); // needed to get the map to display immediately


        maplist=new ArrayList<>();
        Notifications notifications =new Notifications();
        notifications.setBody("okkk");
        notifications.setUserName("koushal");
        notifications.setUserImage("https://cdn-icons-png.flaticon.com/512/149/149071.png");
        maplist.add(notifications);
        maplist.add(notifications);
        maplist.add(notifications);
        maplist.add(notifications);
        maplist.add(notifications);
        maplist.add(notifications);
        maplist.add(notifications);

        new GetBitmapFromUrl().execute(maplist);


        return v;
    }

    private void init() {
        mapView = v.findViewById(R.id.mapView);
    }


    @Override
    public void onRefresh() {
     //   callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {


            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
      /*  if (parent != null ) {
            init();
            setRecyclerView();
            callMusicAlbumApi(1);
        } else if (parent == null) {*/
        init();

        //callMusicAlbumApi(1);
        //  }
    }



    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }


    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                //    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, String object2, int postion) {
        switch (object1) {

        }
        return false;
    }

    private GoogleMap googleMap;

    @SuppressLint("MissingPermission")
    public void getMap() {
        mapView.getMapAsync(mMap -> {
            googleMap = mMap;
            googleMap.setMinZoomPreference(0f);
            googleMap.setMaxZoomPreference(20f);
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnMapClickListener(this);
        });

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




    private ArrayList<Double> latitudes, longitudes;

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
                            bmp.add(BitmapFactory.decodeResource(getActivity().getResources(),
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
                View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
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
//                String currentLng=" 75.7873";
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

    public  int LOCATION_PERMISSION_REQUEST = 8;

//    private boolean checkLocationPermission() {
//
////        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
////                != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(getActivity(),
////                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
////                    LOCATION_PERMISSION_REQUEST);
////            return false;
////        } else {
////            return true;
////        }
//    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        animateMap("26.9124","75.7873");
        return false;
    }
}
