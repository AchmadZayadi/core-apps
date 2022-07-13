package com.sesolutions.ui.contest;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.contest.ContestResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class EntryFragment extends ContestFragment implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {


    //variable used on View Contest -> Entry tab for showing entry of particular contest
    private int contestId;
    private List<Options> optList;


    public static EntryFragment newInstance(String TYPE, OnUserClickedListener listener, int contestId) {
        EntryFragment frag = new EntryFragment();
        frag.contestId = contestId;
        frag.selectedScreen = TYPE;
        frag.loggedinId = -1;
        frag.categoryId = -1;
        return frag;
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_contest_entry, container, false);
        txtNoData = R.string.MSG_NO_ENTRY_CREATED;
        applyTheme(v);



        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        // url = Constant.URL_CONTEST_ENTRIES;
        switch (selectedScreen) {
            case "winners":
                url = Constant.URL_CONTEST_WINNERS;
                break;
           /* case Constant.TabOption.ENTRIES:
                url = Constant.URL_CONTEST_ENTRIES;
                break;*/

            default:
                url = Constant.URL_CONTEST_ENTRIES;
                break;
        }
    }

    private void setSpinnerData() {

        AppCompatSpinner spinner = v.findViewById(R.id.spinner);
        if (null != result.getOptions()) {
            optList = result.getOptions();
            List<String> graphOptionsList = new ArrayList<>();
            for (int i = 0; i < optList.size(); i++) {
                graphOptionsList.add(optList.get(i).getLabel());
            }
            ArrayAdapter<String> graphOptionsAdapter = new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_spinner_item, graphOptionsList);
            // Drop down layout style - list view with radio button
            graphOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            spinner.setAdapter(graphOptionsAdapter);
            spinner.setSelection(0, true);
            spinner.setOnItemSelectedListener(this);

            v.findViewById(R.id.llSpinner).setVisibility(View.VISIBLE);

        } else {
            v.findViewById(R.id.llSpinner).setVisibility(View.GONE);
        }
    }


    public void callMusicAlbumApi(final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                }
                HttpRequestVO request = new HttpRequestVO(url); //url will change according to screenType
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);


                if (!TextUtils.isEmpty(mFilter)) {
                    request.params.put("search_filter", mFilter);
                }

                if (req == Constant.REQ_CODE_REFRESH) {
                    request.params.put(Constant.KEY_PAGE, 1);
                } else {
                    request.params.put(Constant.KEY_PAGE, null != result && req != 1 ? result.getNextPage() : 1);
                }

                request.params.put(Constant.KEY_CONTEST_ID, contestId);
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
                            setRefreshing(swipeRefreshLayout, false);
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    if (null != listener) {
                                        listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 0);
                                    }
                                    ContestResponse resp = new Gson().fromJson(response, ContestResponse.class);

                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        contestList.clear();
                                    }

                                    wasListEmpty = contestList.size() == 0;
                                    result = resp.getResult();


                                    if (null != result.getEntries()) {
                                        contestList.addAll(result.getEntryList(selectedScreen));
                                    }

                                    if (null != result.getWinners()) {
                                        contestList.addAll(result.getWinnerList(selectedScreen));
                                    }

                                    updateAdapter();
                                    if (null == optList) {
                                        setSpinnerData();
                                    }

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

    private void updateAdapter() {
        hideLoaders();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(contestList.size() > 0 ? View.GONE : View.VISIBLE);
        if (listener != null) {
            listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mFilter = optList.get(position).getName();
        onRefresh();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
