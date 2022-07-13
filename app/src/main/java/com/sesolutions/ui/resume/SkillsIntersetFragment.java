package com.sesolutions.ui.resume;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.video.VideoHelper;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SkillsIntersetFragment extends VideoHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

        private RecyclerView recyclerView;
        private boolean isLoading;
        private final int REQ_LOAD_MORE = 2;
        public String searchKey;
        private ProgressBar pb;
        public String txtNoData = Constant.MSG_NO_REFERENCE;
        public SwipeRefreshLayout swipeRefreshLayout;
        SkillAdpterParent AcivementsAdpterParent;
        public List<SkillParentModel> achivementParentModels;


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
            // activity.setTitle(title);
            if (v != null) {
                return v;
            }
            v = inflater.inflate(R.layout.fragment_list_common_offset_refresh, container, false);
            applyTheme(v);

            return v;
        }

        public void init() {
            recyclerView = v.findViewById(R.id.recyclerview);
            pb = v.findViewById(R.id.pb);
        }

        public void setRecyclerView() {
            try {
                achivementParentModels = new ArrayList<>();
                recyclerView.setHasFixedSize(true);
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                AcivementsAdpterParent = new SkillAdpterParent(achivementParentModels, context, this, this);
                recyclerView.setAdapter(AcivementsAdpterParent);
                swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
                swipeRefreshLayout.setOnRefreshListener(this);
            } catch (Exception e) {
                CustomLog.e(e);
            }
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
            init();
            setRecyclerView();
             callMusicAlbumApi(1,true);
        }


        public void callMusicAlbumApi(final int req,boolean isloadershow) {
            try {
                if (isNetworkAvailable(context)) {
                    isLoading = true;
                    try {

                        if(req!= Constant.REQ_CODE_REFRESH){
                            if(isloadershow)
                                showBaseLoader(true);
                        }


                        HttpRequestVO request = new HttpRequestVO(Constant.CREDIT_RESUME_SKILLS);
                        Map<String, Object> map = activity.filteredMap;
                        if (null != map) {
                            request.params.putAll(map);
                        }
                        if (!TextUtils.isEmpty(searchKey)) {
                            request.params.put(Constant.KEY_SEARCH, searchKey);
                        }
                        request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                        request.params.put("resume_id", resumeid);
                       // request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
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
                                try {
                                    String response = (String) msg.obj;
                                    isLoading = false;
                                    setRefreshing(swipeRefreshLayout, false);
                                    CustomLog.e("repsonse1", "" + response);
                                    if (response != null) {
                                        ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                        if (TextUtils.isEmpty(err.getError())) {
                                            achivementParentModels.clear();
                                            try {
                                                SkillParentModel resp = new Gson().fromJson(response, SkillParentModel.class);
                                                 if(resp.getResult()!=null)
                                                    achivementParentModels.add(resp);
                                            }catch (Exception ex){
                                                ex.printStackTrace();
                                            }
                                            updateAdapter();
                                        } else {
                                            Util.showSnackbar(v, err.getErrorMessage());
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

                    }

                } else {
                    isLoading = false;
                    setRefreshing(swipeRefreshLayout, false);

                    pb.setVisibility(View.GONE);
                    notInternetMsg(v);
                }

            } catch (Exception e) {
                hideLoaders();
                CustomLog.e(e);
                hideBaseLoader();
            }
        }

        public void hideLoaders() {
            isLoading = false;
            setRefreshing(swipeRefreshLayout, false);
            pb.setVisibility(View.GONE);
        }

        private void updateAdapter() {
            hideLoaders();
            AcivementsAdpterParent.notifyDataSetChanged();
            runLayoutAnimation(recyclerView);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
          //  v.findViewById(R.id.llNoData).setVisibility(workexperincelist.size() > 0 ? View.GONE : View.VISIBLE);
         //   v.findViewById(R.id.tvNoData).setVisibility(workexperincelist.size() > 0 ? View.GONE : View.VISIBLE);

        }

        int resumeid;
        public static SkillsIntersetFragment newInstance(OnUserClickedListener<Integer, Object> parent, String selectedScreen, int resumid) {
            SkillsIntersetFragment frag = new SkillsIntersetFragment();
            frag.listener = parent;
            frag.selectedScreen = selectedScreen;
            frag.resumeid = resumid;
            return frag;
        }

        @Override
        public void onLoadMore() {
            try {
              /*  if (result != null && !isLoading) {
                    if (result.getCurrentPage() < result.getTotalPage()) {
                        callMusicAlbumApi(REQ_LOAD_MORE);
                    }
                }*/
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
                callMusicAlbumApi(Constant.REQ_CODE_REFRESH,true);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }



    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1){
            case Constant.Events.SUCCESS:
                callMusicAlbumApi(1,true);
                break;
            case Constant.Events.CLICKED_HEADER_EDIT:

                if(postion==0){
                    SkillParentModel.ResultBean.SkillsBean skillsBean= (SkillParentModel.ResultBean.SkillsBean) object2;
                    updatetitlteSkilldata(""+skillsBean.getSkillname(),true,skillsBean.getSkill_id(),"Edit Skills","Enter your Skills","Update",0,skillsBean.getRating());
                }else if(postion==1){
                    SkillParentModel.ResultBean.InterestsBean interestsBean= (SkillParentModel.ResultBean.InterestsBean) object2;
                    updatetitlteSkill(""+interestsBean.getInterestname(),true,interestsBean.getInterest_id(),"Edit Interests","Enter your Interests","Update",1);
                }else if(postion==2){
                    SkillParentModel.ResultBean.StrengthsBean strengthsBean= (SkillParentModel.ResultBean.StrengthsBean) object2;
                    updatetitlteSkill(""+strengthsBean.getStrengthname(),true,strengthsBean.getStrength_id(),"Edit Strengths","Enter your Strengths","Update",2);
                }else {
                    SkillParentModel.ResultBean.HobbiesBean hobbiesBean= (SkillParentModel.ResultBean.HobbiesBean) object2;
                    updatetitlteSkill(""+hobbiesBean.getHobbiename(),true,hobbiesBean.getHobbie_id(),"Edit Hobbies","Enter your Hobbies","Update",3);
                }


            /*    Intent intent = new Intent(activity, CommonActivity.class);
                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CREATE_RESUME_REFERENCE_EDIT);
                intent.putExtra(Constant.KEY_ID, resumeid);
                intent.putExtra(Constant.KEY_Achievement_ID, achievementsBean.getAchievement_id());
                startActivityForResult(intent, EDIT_CHANNEL_ME);*/

                break;
            case Constant.Events.CLICKED_HEADER_DELETE:
                if(postion==0){
                    SkillParentModel.ResultBean.SkillsBean skillsBean= (SkillParentModel.ResultBean.SkillsBean) object2;
                    showDeleteDialog(context,skillsBean.getResume_id(),skillsBean.getSkill_id(),0);
                }else if(postion==1){
                    SkillParentModel.ResultBean.InterestsBean interestsBean= (SkillParentModel.ResultBean.InterestsBean) object2;
                    showDeleteDialog(context,interestsBean.getResume_id(),interestsBean.getInterest_id(),1);
                }else if(postion==2){
                    SkillParentModel.ResultBean.StrengthsBean strengthsBean= (SkillParentModel.ResultBean.StrengthsBean) object2;
                    showDeleteDialog(context,strengthsBean.getResume_id(),strengthsBean.getStrength_id(),2);

                 }else {
                    SkillParentModel.ResultBean.HobbiesBean hobbiesBean= (SkillParentModel.ResultBean.HobbiesBean) object2;
                    showDeleteDialog(context,hobbiesBean.getResume_id(),hobbiesBean.getHobbie_id(),3);
               }


                break;


            case Constant.Events.CLICKED_HEADER_ADD:


                HttpRequestVO request;
                if(postion==0){
                    request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_SKILLS);
               }else  if(postion==1){
                    request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_INTEREST);
                }else  if(postion==2){
                    request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_STRENGTH);
                }else {
                    request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_HOBBIE);
                }
                request.params.put("resume_id", resumeid);

                calltitleresumeAdd(false,request,postion);

                 break;

        }
        return false;
    }

    private void calldeletetitleCurr(boolean showLoader,String url,int resume_id,int curricular_id) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put("resume_id", resume_id);
                    request.params.put("curricular_id", curricular_id);
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                           // hideBaseLoader();
                            callMusicAlbumApi(1,false);
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

        } catch (
                Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }

    }
    private void calldeletetitleAChive(boolean showLoader,String url,int resume_id,int achievement_id,int parenttab) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put("resume_id", resume_id);
                     if(parenttab==0){
                        request.params.put("skill_id", achievement_id);
                    }else  if(parenttab==1){
                        request.params.put("interest_id", achievement_id);
                    }else  if(parenttab==2){
                        request.params.put("strength_id", achievement_id);
                    }else {
                        request.params.put("hobbie_id", achievement_id);
                    }

                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                           // hideBaseLoader();
                            callMusicAlbumApi(1,false);
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

        } catch (
                Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }

    }

    private AlertDialog.Builder dialog;
    private void updatetitlteSkill(String title,Boolean editable,int skillid,String title_sub,String sub_msg,String btn_message,int parenttab) {
        dialog = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.resume_title_layout,null);
        EditText edittitle=view.findViewById(R.id.edittitle);
        TextView titleview=view.findViewById(R.id.titleview);
        Button updateUserBtn = view.findViewById(R.id.updateUserBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);

        if(editable){
            edittitle.setText(title);
            titleview.setText(title_sub);
            edittitle.setHint(sub_msg);
            updateUserBtn.setText(btn_message);
        }else {
            titleview.setText(title_sub);
            edittitle.setHint(sub_msg);
            updateUserBtn.setText(btn_message);
        }
        AlertDialog alertDialog = dialog.create();
        alertDialog.setView(view);
        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edittitle.getText().toString().length()>0){
                    alertDialog.dismiss();
                    if(editable){
                        HttpRequestVO request;

                        if(parenttab==0){
                             request = new HttpRequestVO(Constant.CREDIT_RESUME_EDIT_SKILLS);
                             request.params.put("skill_id", skillid);
                             request.params.put("skillname", edittitle.getText().toString());

                        }else  if(parenttab==1){
                             request = new HttpRequestVO(Constant.CREDIT_RESUME_EDIT_INTEREST);
                             request.params.put("interestname", edittitle.getText().toString());
                             request.params.put("interest_id", skillid);
                        }else  if(parenttab==2){
                             request = new HttpRequestVO(Constant.CREDIT_RESUME_EDIT_STRENGTH);
                            request.params.put("strengthname", edittitle.getText().toString());
                             request.params.put("strength_id", skillid);
                        }else {
                             request = new HttpRequestVO(Constant.CREDIT_RESUME_EDIT_HOBBIE);
                             request.params.put("hobbie_id", skillid);
                             request.params.put("hobbiename", edittitle.getText().toString());
                        }
                        request.params.put("resume_id", resumeid);
                        calltitleresume(false,edittitle.getText().toString(),skillid,request);
                    }else {
                        HttpRequestVO request;
                        if(parenttab==0){
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_SKILLS);
                            request.params.put("skillname", edittitle.getText().toString());
                        }else  if(parenttab==1){
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_INTEREST);
                            request.params.put("interestname", edittitle.getText().toString());
                        }else  if(parenttab==2){
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_STRENGTH);
                            request.params.put("strengthname", edittitle.getText().toString());

                        }else {
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_HOBBIE);
                            request.params.put("hobbiename", edittitle.getText().toString());
                        }
                        request.params.put("resume_id", resumeid);

                        calltitleresume(false,edittitle.getText().toString(),skillid,request);
                    }

                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void updatetitlteSkilldata(String title,Boolean editable,int skillid,String title_sub,String sub_msg,String btn_message,int parenttab,int rating) {
        dialog = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.resume_title_layout_skill,null);
        EditText edittitle=view.findViewById(R.id.edittitle);
        TextView titleview=view.findViewById(R.id.titleview);
        Button updateUserBtn = view.findViewById(R.id.updateUserBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);
        RatingBar ratingBar = (RatingBar)view.findViewById(R.id.ratingBar2);

        if(editable){
            edittitle.setText(title);
            titleview.setText(title_sub);
            edittitle.setHint(sub_msg);
            updateUserBtn.setText(btn_message);
        }else {
            titleview.setText(title_sub);
            edittitle.setHint(sub_msg);
            updateUserBtn.setText(btn_message);
        }
        AlertDialog alertDialog = dialog.create();
        alertDialog.setView(view);

        ratingBar.setRating(rating);

     /*   ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                Toast.makeText(context,
                        "Rating changed, current rating "+ ratingBar.getRating(),
                        Toast.LENGTH_SHORT).show();
            }
        });
*/
        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edittitle.getText().toString().length()>0){
                    alertDialog.dismiss();
                    if(editable){
                        HttpRequestVO request;

                        if(parenttab==0){
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_EDIT_SKILLS);
                            request.params.put("skill_id", skillid);
                            request.params.put("skillname", edittitle.getText().toString());
                            try {
                                int rat= (int) ratingBar.getRating();
                                request.params.put("rate_value", rat);
                            }catch (Exception ex){
                                ex.printStackTrace();
                                request.params.put("rate_value", 0);

                            }


                        }else  if(parenttab==1){
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_EDIT_INTEREST);
                            request.params.put("interestname", edittitle.getText().toString());
                            request.params.put("interest_id", skillid);
                        }else  if(parenttab==2){
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_EDIT_STRENGTH);
                            request.params.put("strengthname", edittitle.getText().toString());
                            request.params.put("strength_id", skillid);
                        }else {
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_EDIT_HOBBIE);
                            request.params.put("hobbie_id", skillid);
                            request.params.put("hobbiename", edittitle.getText().toString());
                        }
                        request.params.put("resume_id", resumeid);
                        calltitleresume(false,edittitle.getText().toString(),skillid,request);
                    }else {
                        HttpRequestVO request;

                        if(parenttab==0){
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_SKILLS);
                            request.params.put("skillname", edittitle.getText().toString());
                            try {
                                int rat= (int) ratingBar.getRating();
                                request.params.put("rate_value", rat);
                            }catch (Exception ex){
                                ex.printStackTrace();
                                request.params.put("rate_value", 0);

                            }
                        }else  if(parenttab==1){
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_INTEREST);
                            request.params.put("interestname", edittitle.getText().toString());
                        }else  if(parenttab==2){
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_STRENGTH);
                            request.params.put("strengthname", edittitle.getText().toString());

                        }else {
                            request = new HttpRequestVO(Constant.CREDIT_RESUME_ADD_HOBBIE);
                            request.params.put("hobbiename", edittitle.getText().toString());
                        }
                        request.params.put("resume_id", resumeid);

                        calltitleresume(false,edittitle.getText().toString(),skillid,request);
                    }

                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }



    private void calltitleresume(boolean showLoader, String title, int skillid, HttpRequestVO request) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    request.headres.put(Constant.KEY_COOKIE, getCookie());


                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                           // hideBaseLoader();
                            callMusicAlbumApi(1,false);

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

        } catch (
                Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }

    }
    private void calltitleresumeAdd(boolean showLoader, HttpRequestVO request, int postiontag) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    request.params.put(Constant.KEY_GET_FORM, 1);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());


                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    Dummy vo = new Gson().fromJson(response, Dummy.class);
                                    if (TextUtils.isEmpty(vo.getError())) {
                                        if(postiontag==0){
                                            updatetitlteSkilldata("",false,postiontag,"Add Skills","Enter your Skills","Save",postiontag,0);
                                        }else if (postiontag==1){
                                            updatetitlteSkill("",false,postiontag,"Add Interests","Enter your Interests","Save",postiontag);
                                        }else if (postiontag==2){
                                            updatetitlteSkill("",false,postiontag,"Add Strengths","Enter your Strengths","Save",postiontag);
                                        }else{
                                            updatetitlteSkill("",false,postiontag,"Add Hobbies","Enter your Hobbies","Save",postiontag);
                                        }
                                    } else {
                                        Util.showSnackbar(v, vo.getErrorMessage());
                                     }
                                } else {
                                    notInternetMsg(v);
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

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

        } catch (
                Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }

    }


    public void showDeleteDialog(final Context context, final int rsumeidnew, int itemid, int postflag) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            if(postflag==0) {
                tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_SKILL);
            }else if(postflag==1) {
                tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_INTERESTS);
            }else if(postflag==2) {
                tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_STRENGTHS);
            }else {
                tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_HOBBIES);
            }
            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.delete);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                if(postflag==0){
                    calldeletetitleAChive(false, Constant.CREDIT_RESUME_DELETE_SKILLS,rsumeidnew,itemid,0);

                }else if(postflag==1){
                    calldeletetitleAChive(false, Constant.CREDIT_RESUME_DELETE_INTEREST,rsumeidnew,itemid,1);
                }else if(postflag==2){
                    calldeletetitleAChive(false, Constant.CREDIT_RESUME_DELETE_STRENGTH,rsumeidnew,itemid,2);
                }else {
                    calldeletetitleAChive(false, Constant.CREDIT_RESUME_DELETE_HOBBIE,rsumeidnew,itemid,3);
                }

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }




}
