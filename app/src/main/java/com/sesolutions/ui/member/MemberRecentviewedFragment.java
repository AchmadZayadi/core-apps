package com.sesolutions.ui.member;


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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Notifications;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.TTSDialogFragment;
import com.sesolutions.ui.profile.ViewProfileFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MemberRecentviewedFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    private static final int REQ_LOAD_MORE = 100;
    public View v;
    public List<Notifications> albumsList;
    public RecyclerView recyclerView,rvHome;
    public MemberGridAdapter adapter;
     public boolean isLoading;
    public CommonResponse.Result result;
    public ProgressBar pb;
    public int userId;
    public String title = Constant.TITLE_MEMBER;
    public String search;
    private SwipeRefreshLayout swipeRefreshLayout;
    ImageView listviewid;
    ImageView gridviewid;
    ImageView mapviewid;
    TextView total_mammbercount;
    private boolean showToolbar = true;
    private int flagintent = 0;
    public ArrayList<Notifications> maplist;
    RelativeLayout rltabs;

    public static MemberRecentviewedFragment newInstance(int userId, boolean showToolbar) {
        MemberRecentviewedFragment frag = new MemberRecentviewedFragment();
        frag.userId = userId;
        frag.showToolbar = showToolbar;
        return frag;
    }

    public static MemberRecentviewedFragment newInstance(int userId, boolean showToolbar,int flagintent) {
        MemberRecentviewedFragment frag = new MemberRecentviewedFragment();
        frag.userId = userId;
        frag.showToolbar = showToolbar;
        frag.flagintent = flagintent;
        return frag;
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


    private int currentGuide;
    Bundle saveinstanse;
    int flag_state=1;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_group, container, false);
        applyTheme(v);
        saveinstanse=saveInstanceState;
        rltabs=v.findViewById(R.id.rltabs);

        rltabs.setVisibility(View.GONE);
        try {
            if (showToolbar) {
                initScreenData();
            } else {
                v.findViewById(R.id.appBar).setVisibility(View.GONE);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }


    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public void initScreenData() {
        init();
        setRecyclerView(0);
        callMusicAlbumApi(1);
    }


    @Override
    public void onRefresh() {
        if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }



    public void init() {
        try {
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            listviewid = v.findViewById(R.id.listviewid);
            gridviewid = v.findViewById(R.id.gridviewid);
            mapviewid = v.findViewById(R.id.mapview);
            total_mammbercount = v.findViewById(R.id.total_mammbercount);
            total_mammbercount.setVisibility(View.GONE);
            swipeRefreshLayout.setOnRefreshListener(this);
            if (!showToolbar) {
                v.findViewById(R.id.appBar).setVisibility(View.GONE);
                swipeRefreshLayout.setEnabled(false);
            }
           // title = (userId > 0) ? Constant.TITLE_FRIENDS : Constant.TITLE_MEMBER;

            if(flagintent==1){
                title="Recently Viewed";
            }else {
                title="Recently Viewed";
            }

            updateTitle(title);
            pb = v.findViewById(R.id.pb);
            recyclerView = v.findViewById(R.id.recyclerView);
            rvHome = v.findViewById(R.id.rvHome);

            v.findViewById(R.id.ivSearch).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivSearch).setOnClickListener(this);
            v.findViewById(R.id.ivBack).setOnClickListener(this);

            listviewid.setColorFilter(ContextCompat.getColor(context, R.color.gray), PorterDuff.Mode.MULTIPLY);
            gridviewid.setColorFilter(ContextCompat.getColor(context, R.color.follow_blue), PorterDuff.Mode.MULTIPLY);
            mapviewid.setColorFilter(ContextCompat.getColor(context, R.color.gray), PorterDuff.Mode.MULTIPLY);


            recyclerView.setVisibility(View.VISIBLE);
            rvHome.setVisibility(View.GONE);



        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();



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



    public void setRecyclerView2(int accounttype) {
        try {
            if(accounttype==100){
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new MemberGridAdapter(albumsList, context, this, this,0);
            }else {
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new MemberGridAdapter(albumsList, context, this, this,1);
            }
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            // recyclerView.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }




    private void updateTitle(String title) {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
    }

    public void setRecyclerView(int listtype) {
        try {
            albumsList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MemberGridAdapter(albumsList, context, this, this,listtype);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            // recyclerView.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callGutterApi(final boolean isTabbed, final String url, int userId, final int position) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(true);

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_USER_ID, userId);
                    request.params.put(Constant.KEY_GUTTER, 1);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            showBaseLoader(true);
                            try {
                                String response = (String) msg.obj;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        if (url.contains("block")) {
                                            BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                            Util.showSnackbar(v, res.getResult());
                                            // onBackPressed();


                                        }

                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }
                            } catch (Exception e) {
                               CustomLog.e(e);
                            }
                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    CustomLog.e(e);
                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }




    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivSearch:
                    goToSearchMember();
                    break;
                case R.id.ivFilter:
                    openForm();
                    break;
                case R.id.ivMic:
                    closeKeyboard();
                    TTSDialogFragment.newInstance(this).show(fragmentManager, "tts");
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void openForm() {
        fragmentManager.beginTransaction().replace(R.id.container,
                MemnerFilterFormFragment.newInstance(Constant.FormType.FILTER_MEMBER, null, Constant.URL_MEMBER_SEARCH_FILTER))
                .addToBackStack(null)
                .commit();
    }

    private void goToSearchMember() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, new SearchMemberFragment())
                .addToBackStack(null)
                .commit();
    }

    public void callMusicAlbumApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;

                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req != Constant.REQ_CODE_REFRESH) {
                        showBaseLoader(false);
                    }
                    HttpRequestVO request =null;

                    if(flagintent==1){
                        request = new HttpRequestVO(Constant.URL_MEMBER_RECENT_VIEWED_BY_ME+userId+Constant.POST_URL);
                    }else {
                        request = new HttpRequestVO(Constant.URL_MEMBER_RECENT_VIEWED_ME+userId+Constant.POST_URL);
                    }

                    Map<String, Object> map = activity.filteredMap;
                    if (null != map) {
                        request.params.putAll(map);
                    }

                    if (!TextUtils.isEmpty(search)) {
                        request.params.put(Constant.KEY_SEARCH_TEXT, search);
                    }

                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            setRefreshing(swipeRefreshLayout, false);
                            try {
                                String response = (String) msg.obj;
                                isLoading = false;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            albumsList.clear();
                                        }
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        result = resp.getResult();

                                        if (null != result.getNotification())
                                            albumsList.addAll(result.getNotification());

                                        updateAdapter();
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
                                }

                            } catch (Exception e) {
                                hideBaseLoader();

                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();
                    CustomLog.e(e);
                }
            } else {
                setRefreshing(swipeRefreshLayout, false);
                isLoading = false;
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    private void callMemberStatusApi(final String url, final int position, int userId) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    showBaseLoader(true);

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_USER_ID, userId);

                    if (ModuleUtil.getInstance().isCorePlugin(context, "user"))
                        request.params.put(Constant.KEY_BROWSE, 1);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                isLoading = false;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        // result = resp.getResult();
                                        if (null != resp.getResult().getMember()) {
                                         /*   Notifications vo = albumsList.get(position);
                                            vo.setMembership(resp.getResult().getMember().getMembership());
                                            albumsList.add_create(position, vo);*/
                                            if (url.contains("follow")) {
                                                albumsList.get(position).setFollow(resp.getResult().getMember().getFollow());
                                            } else {
                                                albumsList.get(position).setMembership(resp.getResult().getMember().getMembership());
                                            }

                                            adapter.notifyItemChanged(position);
                                          //  Memeberadapter.notifyItemChanged(position);
                                        }
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }

                            } catch (Exception e) {
                                hideBaseLoader();
                                onRefresh();
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();
                    CustomLog.e(e);
                }

            } else {
                isLoading = false;

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }
    private void callAddblockStatusApi(final String url, final int position, int userId) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    showBaseLoader(true);

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_USER_ID, userId);

                    if (ModuleUtil.getInstance().isCorePlugin(context, "user"))
                        request.params.put(Constant.KEY_BROWSE, 1);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                isLoading = false;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        AddMeberResponse resp = new Gson().fromJson(response, AddMeberResponse.class);
                                        // result = resp.getResult();
                                        if (null != resp.getResult()) {

                                                albumsList.get(position).setBlock(resp.getResult().getNotification().getBlock());
                                                adapter.notifyItemChanged(position);
                                                //Memeberadapter.notifyItemChanged(position);
                                        }
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }

                            } catch (Exception e) {
                                hideBaseLoader();
                                onRefresh();
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();
                    CustomLog.e(e);
                }

            } else {
                isLoading = false;

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateAdapter() {
        try {
            isLoading = false;
            pb.setVisibility(View.GONE);

            //  swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
            runLayoutAnimation(recyclerView);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_MEMBER);
            v.findViewById(R.id.tvNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);
            v.findViewById(R.id.llNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);
            if (result.getTotal() > 0) {
                updateTitle(title + " (" + result.getTotal() + ")");
                total_mammbercount.setText(result.getTotal()+" Member found.");
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {

        try {
            if (null != swipeRefreshLayout && swipeRefreshLayout.isRefreshing()) return false;
            switch (object1) {
                case Constant.Events.MEMBER_ADD:
                    String type = albumsList.get(postion).getMembership().getAction();
                    String url = Constant.EMPTY;
                    if (type.equals("cancel")) {
                        url = Constant.URL_MEMBER_REJECT;
                    } else if (type.equals("add")) {
                        url = Constant.URL_MEMBER_ADD;
                    } else if (type.equals("remove")) {
                        url = Constant.URL_MEMBER_REMOVE;
                    } else if (type.equals("confirm")) {
                        url = Constant.URL_MEMBER_CONFIRM;
                    }

                    callMemberStatusApi(url, postion, albumsList.get(postion).getUserId());
                    break;

                case Constant.Events.MEMBER_BLOCK:
                    //  type = albumsList.get(postion).getFollow().getAction();
                    callAddblockStatusApi(Constant.URL_MEMBER_BLOCK, postion, albumsList.get(postion).getUserId());
                    break;
                case Constant.Events.MEMBER_UNBLOCK:
                    //  type = albumsList.get(postion).getFollow().getAction();
                    callAddblockStatusApi(Constant.URL_MEMBER_UNBLOCK, postion, albumsList.get(postion).getUserId());
                    break;
                case Constant.Events.MEMBER_FOLLOW:
                    //  type = albumsList.get(postion).getFollow().getAction();
                    callMemberStatusApi(Constant.URL_FOLLOW_MEMBER, postion, albumsList.get(postion).getUserId());
                    break;

                case Constant.Events.CLICKED_HEADER_IMAGE:

                    goToProfileFragment(albumsList.get(postion).getUserId(), (MemberGridAdapter.ContactHolder) object2, postion);
                    break;
                case Constant.Events.CLICKED_HEADER_IMAGE2:

                    goToProfileFragment(albumsList.get(postion).getUserId(), (MemberGridMapAdapter.ContactHolder) object2, postion);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }


    public void goToProfileFragment(int userId, MemberGridMapAdapter.ContactHolder holder, int position) {
        try {
            String transitionName = albumsList.get(position).getTitle();
            ViewCompat.setTransitionName(holder.ivImage, transitionName);
            ViewCompat.setTransitionName(holder.tvName, transitionName + Constant.Trans.TEXT);
            //  ViewCompat.setTransitionName(holder.llMain, transitionName + Constant.Trans.LAYOUT);


            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, albumsList.get(position).getUserImage());
            //  bundle.putString(Constant.Trans.LAYOUT, transitionName + Constant.Trans.LAYOUT);

            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivImage, ViewCompat.getTransitionName(holder.ivImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    .addSharedElement(holder.tvName, ViewCompat.getTransitionName(holder.tvName))
                    .replace(R.id.container, ViewProfileFragment.newInstance(userId, bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            goToProfileFragment(userId);
        }
    }

    public void goToProfileFragment(int userId, MemberGridAdapter.ContactHolder holder, int position) {
        try {
            String transitionName = albumsList.get(position).getTitle();
            ViewCompat.setTransitionName(holder.ivImage, transitionName);
            ViewCompat.setTransitionName(holder.tvName, transitionName + Constant.Trans.TEXT);
            //  ViewCompat.setTransitionName(holder.llMain, transitionName + Constant.Trans.LAYOUT);


            Bundle bundle = new Bundle();
            bundle.putString(Constant.Trans.IMAGE, transitionName);
            bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
            bundle.putString(Constant.Trans.IMAGE_URL, albumsList.get(position).getUserImage());
            //  bundle.putString(Constant.Trans.LAYOUT, transitionName + Constant.Trans.LAYOUT);

            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivImage, ViewCompat.getTransitionName(holder.ivImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    .addSharedElement(holder.tvName, ViewCompat.getTransitionName(holder.tvName))
                    .replace(R.id.container, ViewProfileFragment.newInstance(userId, bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            goToProfileFragment(userId);
        }
    }

    private GoogleMap googleMap;


}
