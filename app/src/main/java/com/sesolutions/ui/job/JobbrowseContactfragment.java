package com.sesolutions.ui.job;


import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.Bookings.ProfessionalResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.jobs.JobsResponse;
import com.sesolutions.ui.bookings.BookingHelper;
import com.sesolutions.ui.bookings.adapters.ProfessionalAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

import static com.sesolutions.utils.URL.URL_PROFESSIONAL_BROWSE;

public class JobbrowseContactfragment extends BookingHelper<ProfessionalAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String TYPE_HOME = "eclassroom_main_home";
    public static final String TYPE_BROWSE = "eclassroom_main_browse";
    public static final String TYPE_CATEGORY = "eclassroom_main_categories";
    public static final String TYPE_FAVOURITE = "2";
    public static final String TYPE_LOCATIONS = "3";
    public static final String TYPE_FEATURED = "eclassroom_main_featured";
    public static final String TYPE_VERIFIED = "eclassroom_main_verified";
    public static final String TYPE_SPONSORED = "eclassroom_main_sponsored";
    public static final String TYPE_HOT = "eclassroom_main_hot";
    public static final String TYPE_MANAGE = "sesbusiness_main_manage";
    public static final String TYPE_PACKAGE = "sesbusiness_main_manage_package";
    public static final String TYPE_ALBUM_HOME = "9";
    public static final String TYPE_ALBUM_BROWSE = "sesbusiness_main_businessalbumbrowse";
    public static final String TYPE_ALBUM_BROWSE_CLASS = "eclassroom_main_albumbrowse";
    public static final String TYPE_VIDEO_BROWSE = "sesbusinessvideo_main_browsehome";
    public static final String TYPE_ASSOCIATE = "11";
    public static final String TYPE_CATEGORY_VIEW = "12";
    public static final String TYPE_SEARCH = "13";
    public static final String TYPE_SEARCH_MANAGE = "14";
    public static final String TYPE_CREATE = "eclassroom_main_create";
    public static final String TYPE_REVIEW_BROWSE = "eclassroom_main_review";
    public static final String TYPE_SEARCH2 = "eclassroom_main_albumbrowse";

    public String jobdescrption = "";
    public String searchKey;
    public int loggedinId;
    public int txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public ProgressBar pb;
    public RecyclerView rvQuotesCategory;
    public boolean isTag;
    public String url;

    //variable used when called from business view -> associated
    private int mBusinessId;
    TextView txt1,txt1_value,txt2,txt2_value,txt3,txt3_value,txt4_value;
    RelativeLayout llNoData;
    JobsResponse jobsResponse;
    TextView txtData;
    LinearLayout linerid1,linerid2,linerid3,linerid4;

    public static JobbrowseContactfragment newInstance(JobbrowseContactfragment parent, int loggedInId, int categoryId) {
        JobbrowseContactfragment frag = new JobbrowseContactfragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static JobbrowseContactfragment newInstance(String loggedInIdlkjk, JobbrowseContactfragment parent) {
        return newInstance( loggedInIdlkjk, parent);
    }



    public static JobbrowseContactfragment newInstance(String TYPE, int businessId, JobsResponse jobsResponse) {
        JobbrowseContactfragment frag = newInstance(null, -1, -1);
        frag.jobdescrption = TYPE;
        frag.mBusinessId = businessId;
        frag.jobsResponse = jobsResponse;
        return frag;
    }


    public static JobbrowseContactfragment newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.job_contact_layout, container, false);
        applyTheme(v);
        init();
        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        linerid1 = v.findViewById(R.id.linerid1);
        linerid2 = v.findViewById(R.id.linerid2);
        linerid3 = v.findViewById(R.id.linerid3);
        linerid4 = v.findViewById(R.id.linerid4);
        txt1_value = v.findViewById(R.id.txt1_value);
        txt2_value = v.findViewById(R.id.txt2_value);
        txt3_value = v.findViewById(R.id.txt3_value);
        txt4_value = v.findViewById(R.id.txt4_value);
        txt1 = v.findViewById(R.id.txt1);
        txt2 = v.findViewById(R.id.txt2);
        txt3 = v.findViewById(R.id.txt3);
        txtData = v.findViewById(R.id.tvNoData);
        llNoData = v.findViewById(R.id.llNoData);
        txtNoData = R.string.MSG_NO_CONTACT_FOUND;
        Log.e("Job descrption",""+jobdescrption);
        if(jobdescrption.length()>0){
          //  titledesc.setText(""+jobdescrption);
            //titledesc.setText(Html.fromHtml(jobdescrption));


            if(jobsResponse.getJob_contact_name()!=null && jobsResponse.getJob_contact_name().length()>0){
                txt1_value.setText(""+jobsResponse.getJob_contact_name());
                linerid1.setVisibility(View.VISIBLE);
            }else {
                linerid1.setVisibility(View.GONE);
            }

            if(jobsResponse.getJob_contact_phone()!=null && jobsResponse.getJob_contact_phone().length()>0){
                txt2_value.setText(""+jobsResponse.getJob_contact_phone());
                linerid2.setVisibility(View.VISIBLE);
            }else {
                linerid2.setVisibility(View.GONE);
            }

            if(jobsResponse.getCompany_websiteurl()!=null && jobsResponse.getCompany_websiteurl().length()>0){
                txt3_value.setText(""+jobsResponse.getCompany_websiteurl());
                linerid3.setVisibility(View.VISIBLE);
            }else {
                linerid3.setVisibility(View.GONE);
            }

            if(jobsResponse.getJob_contact_email()!=null && jobsResponse.getJob_contact_email().length()>0){
                txt4_value.setText(""+jobsResponse.getJob_contact_email());
                linerid4.setVisibility(View.VISIBLE);
            }else {
                linerid4.setVisibility(View.GONE);
            }


           // titledesc.setVisibility(View.VISIBLE);
            llNoData.setVisibility(View.GONE);

          /*  "job_contact_name": "Brenda Hogan",
                    "job_contact_email": "hockmarlecia@gmail.com",
                    "job_contact_phone": "07896524123",
                    "job_contact_website": "",
                    "job_contact_facebook": "",*/

         //   titleheader.setVisibility(View.VISIBLE);
        }else {
           // titledesc.setVisibility(View.GONE);
            txtData.setText(""+getStrings(R.string.MSG_NO_CONTACT_FOUND));
            llNoData.setVisibility(View.VISIBLE);
            txtData.setVisibility(View.VISIBLE);
         //   titleheader.setVisibility(View.GONE);
        }

     }

    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            adapter = new ProfessionalAdapter(videoList, context, this, this);
            adapter.setType(jobdescrption);
           /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void initScreenData() {
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    public void callMusicAlbumApi(final int req) {


        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(URL_PROFESSIONAL_BROWSE); //url will change according to screenType
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                if (loggedinId > 0) {
                    request.params.put(Constant.KEY_USER_ID, loggedinId);
                }

                if (null != jobdescrption) {
                    request.params.put("filter_sort", "booking_main_professionals");
                }

                // used when this screen called from business view -> associated
                if (mBusinessId > 0) {
                    request.params.put(Constant.KEY_BUSINESS_ID, mBusinessId);
                }

                if (!TextUtils.isEmpty(searchKey)) {
                    request.params.put(Constant.KEY_SEARCH, searchKey);
                } else if (categoryId > 0) {
                    request.params.put(Constant.KEY_CATEGORY_ID, categoryId);
                }

                Map<String, Object> map = activity.filteredMap;
                if (null != map) {
                    request.params.putAll(map);
                }
                request.params.put(Constant.KEY_PAGE, null != result && req != 1 ? result.getNextPage() : 1);
                if (req == Constant.REQ_CODE_REFRESH) {
                    request.params.put(Constant.KEY_PAGE, 1);
                }

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    hideBaseLoader();
                    try {
                        String response = (String) msg.obj;
                        isLoading = false;
                        setRefreshing(swipeRefreshLayout, false);
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (TextUtils.isEmpty(err.getError())) {
                                if (null != parent) {
                                    parent.onItemClicked(Constant.Events.SET_LOADED, TYPE_BROWSE, 1);
                                }
                                ProfessionalResponse resp = new Gson().fromJson(response, ProfessionalResponse.class);
                                //if screen is refreshed then clear previous data
                                if (req == Constant.REQ_CODE_REFRESH) {
                                    videoList.clear();
                                }

                                wasListEmpty = videoList.size() == 0;
                                result = resp.getResult();

                                if (null != result.getProfessionals()) {
                                    videoList.addAll(result.getProfessionals(jobdescrption));
                                }
                                updateAdapter();
                            } else {
                                Util.showSnackbar(v, err.getErrorMessage());
                                goIfPermissionDenied(err.getError());
                            }
                        }

                    } catch (Exception e) {
                        hideBaseLoader();
                        CustomLog.e(e);
                        somethingWrongMsg(v);
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
        }
    }


    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    public void updateAdapter() {
        hideLoaders();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            parent.onItemClicked(Constant.Events.UPDATE_TYPE, jobdescrption, result.getTotal());
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
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
