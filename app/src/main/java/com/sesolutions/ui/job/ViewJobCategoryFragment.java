package com.sesolutions.ui.job;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.CommonResponse2;
import com.sesolutions.responses.CommonResponse3;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.ui.blogs.BrowseBlogsFragment;
import com.sesolutions.ui.blogs.SearchBlogFragment;
import com.sesolutions.ui.bookings.adapters.ProfessionalAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;

public class ViewJobCategoryFragment extends BrowseJobsFragment implements View.OnClickListener, OnLoadMoreListener {


    private String title;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_blog_category, container, false);
        applyTheme();
        init();
        updateTitle(title);
        setRecyclerView();
        callMusicAlbumApi21(1);
        return v;
    }

    public void init() {
        super.init();
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.ivSearch).setOnClickListener(this);

    }

    public void updateTitle(String title) {
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
    }

    private void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchBlogFragment()).addToBackStack(null).commit();
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callMusicAlbumApi21(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {
                    if (req == 1) {
                        showView(v.findViewById(R.id.pbMain));
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + "/sesjob/index/category-view" + Constant.POST_URL);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_CATEGORY_ID, categoryId);

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideView(v.findViewById(R.id.pbMain));
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        showView(v.findViewById(R.id.cvDetail));
                                        CommonResponse2 resp = new Gson().fromJson(response, CommonResponse2.class);
                                    //    result = resp.getResult();
                                   ///     jobsResponse = result.getJobs();
                                        if(resp.getResult()!=null && resp.getResult().getJobs()!=null){
                                            videoList.addAll(resp.getResult().getJobs());
                                            adapter.notifyDataSetChanged();
                                            v.findViewById(R.id.llNoData).setVisibility(View.GONE);
                                        }else {
                                            v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
                                            ((TextView)v.findViewById(R.id.tvNoData)).setText(getStrings(R.string.MSG_NO_CATEGORIES));
                                        }

                                  //      setupViewPager();

                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
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
                    hideView(v.findViewById(R.id.pbMain));
                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }




    public static ViewJobCategoryFragment newInstance(int categoryId, String categoryName) {
        ViewJobCategoryFragment frag = new ViewJobCategoryFragment();
        frag.parent = null;
        frag.categoryId = categoryId;
        frag.loggedinId = 0;
        frag.title = categoryName;
        return frag;
    }
}
