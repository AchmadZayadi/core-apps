package com.sesolutions.ui.games;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.video.VideoHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.Map;

public class ViewGameFragment extends VideoHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private boolean isLoading;
    private final int REQ_LOAD_MORE = 2;
    public String searchKey;
    public com.sesolutions.responses.videos.Result result;
    private ProgressBar pb;
    public String txtNoData = Constant.MSG_NO_CHANNEL_CREATED;
    public SwipeRefreshLayout swipeRefreshLayout;
    int game_id=0;
    ImageView gameicon,coverid,ivBack;
    RelativeLayout realtiveid1;
    TextView descriptionid,tvTitle;
    Button playBtn;
    LinearLayoutCompat llLike,llFavorite,llComment;
    TextView tvLike,tvComment;
    ImageView ivImageLike;
    int messagecount=0;
    boolean islike=false;

    public static ViewGameFragment newInstance(OnUserClickedListener<Integer, Object> parent, int game_id) {
        ViewGameFragment frag = new ViewGameFragment();
        frag.listener = parent;
        frag.game_id = game_id;
        return frag;
    }

    String Web_Url="";

    @Override
    public void onResume() {
        super.onResume();
        initScreenData();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_game_view, container, false);
        gameicon=v.findViewById(R.id.gameicon);
        coverid=v.findViewById(R.id.coverid);
        ivImageLike=v.findViewById(R.id.ivImageLike);
        tvComment=v.findViewById(R.id.tvComment);
        tvLike=v.findViewById(R.id.tvLike);
        playBtn=v.findViewById(R.id.playBtn);
        ivBack=v.findViewById(R.id.ivBack);
        tvTitle=v.findViewById(R.id.tvTitle);
        llLike=v.findViewById(R.id.llLike);
        llFavorite=v.findViewById(R.id.llFavorite);
        llComment=v.findViewById(R.id.llComment);
        realtiveid1=v.findViewById(R.id.realtiveid1);
        descriptionid=v.findViewById(R.id.descriptionid);

        v.findViewById(R.id.llFavorite).setVisibility(View.GONE);

        applyTheme(v);
        String resourceType="egames_game";
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        llComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCommentFragment(game_id, resourceType);
            }
        });


        llLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              showBaseLoader(true);
              callBottomCommentLikeApi(game_id, resourceType, Constant.URL_MUSIC_LIKE);
                Handler handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        if (islike) {
                            islike=false;
                            messagecount =messagecount-1;
                            if(messagecount>0){
                                if(messagecount>1){
                                    tvLike.setText(""+(messagecount)+" likes");
                                }else {
                                    tvLike.setText(""+(messagecount)+" like");
                                }
                            }else {
                                tvLike.setText("like");
                            }
                            tvLike.setTextColor(Color.parseColor(Constant.text_color_1));
                            ivImageLike.setColorFilter(Color.parseColor(Constant.text_color_1));
                        }
                        else {
                            messagecount =messagecount+1;
                            if(messagecount>1){
                                tvLike.setText(""+(messagecount)+" likes");
                            }else {
                                tvLike.setText(""+(messagecount)+" like");
                            }
                            islike=true;
                            ivImageLike.setColorFilter(Color.parseColor(Constant.colorPrimary));
                            tvLike.setTextColor(Color.parseColor(Constant.colorPrimary));
                        }
                    }
                };
                handler.postDelayed(r, 1000);


            }
        });




        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Web_Url.length()>0){
                    Intent  intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_WEBVIEW);
                    intent.putExtra(Constant.KEY_URI, Web_Url);
                    intent.putExtra(Constant.KEY_TITLE,"");
                    startActivity(intent);
                }else {
                    Util.showSnackbar(v, "Don't have any game url");
                }

            }
        });




        return v;
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
        result = null;
        callMusicAlbumApi(1);
    }



    public void callMusicAlbumApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        if (req != Constant.REQ_CODE_REFRESH)
                            showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_GAME_VIEW);


                    Map<String, Object> map = activity.filteredMap;
                    if (null != map) {
                        request.params.putAll(map);
                    }
                    request.params.put(Constant.KEY_GAME_ID, ""+game_id);

                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(Constant.KEY_SEARCH, searchKey);
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
                            try {
                                String response = (String) msg.obj;
                                isLoading = false;
                                setRefreshing(swipeRefreshLayout, false);
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                       }
                                        GameViewModel resp = new Gson().fromJson(response, GameViewModel.class);

                                        Util.showImageWithGlide(gameicon, resp.getResult().getGame().getGame_images().getMain(), context, R.drawable.placeholder_square);
                                        Util.showImageWithGlide(coverid, resp.getResult().getGame().getGame_images().getMain(), context, R.drawable.placeholder_square);
                                        descriptionid.setText(Html.fromHtml(""+resp.getResult().getGame().getDescription()));
                                        tvTitle.setText(Html.fromHtml(""+resp.getResult().getGame().getTitle()));
                                        tvTitle.setTextColor(Color.parseColor(Constant.text_color_1));
                                        Web_Url=resp.getResult().getGame().getUrl();
                                        playBtn.setVisibility(View.VISIBLE);
                                        v.findViewById(R.id.llReaction).setVisibility(View.VISIBLE);
                                        messagecount=resp.getResult().getGame().getContent_like_count();

                                        Log.e("messagecount",""+messagecount);
                                        if(messagecount>1){
                                            tvLike.setText(""+(messagecount)+" likes");
                                        }else if(messagecount==1){
                                            tvLike.setText(""+(1)+" like");
                                        }else {
                                            tvLike.setText("like");
                                        }

                                        if (resp.getResult().getGame().isIs_content_like()) {
                                                    ivImageLike.setColorFilter(Color.parseColor(Constant.colorPrimary));
                                                    tvLike.setTextColor(Color.parseColor(Constant.colorPrimary));
                                            islike=true;
                                         } else {
                                            tvLike.setTextColor(Color.parseColor(Constant.text_color_1));
                                            ivImageLike.setColorFilter(Color.parseColor(Constant.text_color_1));
                                            islike=false;
                                        }
                                        tvComment.setText(resp.getResult().getGame().getComment_count() + " " + (resp.getResult().getGame().getComment_count() == 1 ? Constant.TXT_COMMENT : Constant.TXT_COMMENTS));



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

   /* public boolean onItemClicked(Integer object1, String object2, int postion) {
        switch (object1) {
            case Constant.Events.MUSIC_MAIN:
                goToViewFragment(postion);
                break;

        }
        return super.onItemClicked(object1, object2, postion);
    }

    private void goToViewFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewMusicAlbumFragment.newInstance(videoList.get(postion).getAlbumId()))
                .addToBackStack(null)
                .commit();
    }
*/
}
