package com.sesolutions.ui.crowdfunding;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rd.PageIndicatorView;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.fund.FundContent;
import com.sesolutions.responses.fund.FundResponse;
import com.sesolutions.responses.page.NestedOptions;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.customviews.NestedWebView;
import com.sesolutions.ui.page.PagePhotoAdapter;
import com.sesolutions.ui.photo.UploadPhotoFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FundDescriptionFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private LinearLayoutCompat llMain;
    private FundContent resp;
    private int mPageId;
    private Options button;
    private List<Albums> photoList;

    public static FundDescriptionFragment newInstance(int userId) {
        FundDescriptionFragment frag = new FundDescriptionFragment();
        frag.mPageId = userId;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_crowd_description, container, false);
        try {
            applyTheme(v);
            /*if (!showToolbar) {
                v.findViewById(R.id.appBar).setVisibility(View.GONE);
            } else {
                initScreenData();
            }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void initScreenData() {
        initPhoto();
        callMusicAlbumApi();
    }

    private void updateButton() {
        if (null != button) {
            v.findViewById(R.id.cvCreate).setVisibility(View.VISIBLE);
            ((ImageView) v.findViewById(R.id.ivPost)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cam_ic_edit));
            ((TextView) v.findViewById(R.id.tvPost)).setText(button.getLabel());
            v.findViewById(R.id.cvCreate).setOnClickListener(this);
        } else {
            v.findViewById(R.id.cvCreate).setVisibility(View.GONE);
        }
    }

    private PagePhotoAdapter adapterPhoto;

    private void initPhoto() {
        RecyclerView rvPhotos = v.findViewById(R.id.rvPhotos);
        photoList = new ArrayList<Albums>();
        //photoList.add(new Albums(result.getCampaign().getImages()));
        rvPhotos.setHasFixedSize(true);
        //final LinearLayoutManager layoutManager       = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        //rvPhotos.setLayoutManager(layoutManager);
        adapterPhoto = new PagePhotoAdapter(photoList, context, this, true);
        rvPhotos.setAdapter(adapterPhoto);
        rvPhotos.setNestedScrollingEnabled(false);

    }

    private void init() {
        try {
            llMain = v.findViewById(R.id.llInfo);
            if (resp != null) {
                //show main layout
                llMain.setVisibility(View.VISIBLE);
                updateButton();
                //setBasicInfoData();
                // setOpenHoursData();

                setDetail();
                setStory();
                // setContactInfoData();

                //addPeopleLists(getStrings(R.string.people_liked), resp.getLikePeople(), R.drawable.music_like_selected, resp.getTotalLikePeople(), "like");
                // addPeopleLists(getStrings(R.string.people_followed), resp.getFollowedPeople(), R.drawable.follow_artist_selected, resp.getTotalFollowedPeople(), "follow");
                // addPeopleLists(getStrings(R.string.people_favorite), resp.getFavouritePeople(), R.drawable.music_favourite_selected, resp.getTotalFavouritePeople(), "favourite");
                //addPeopleLists(getStrings(R.string.pages_liked), resp.getLikePages(), R.drawable.music_like_selected, resp.getTotalLikePages(), "page");

                applyTheme(llMain);
            } else {
                //hide mail lauout in case of invalid valid response
                llMain.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setDetail() {
        //set "detail" data
        if (!TextUtils.isEmpty(resp.getShortDescription())) {
            v.findViewById(R.id.cvDetail).setVisibility(View.VISIBLE);
            ((NestedWebView) v.findViewById(R.id.nwvDetail)).loadData(resp.getShortDescription(), null, null);
            ((NestedWebView) v.findViewById(R.id.nwvDetail)).setNestedScrollingEnabled(false);

        } else {
            v.findViewById(R.id.cvDetail).setVisibility(View.GONE);
        }
    }

    private void setStory() {
        //set "detail" data
        if (!TextUtils.isEmpty(resp.getDescription())) {
            v.findViewById(R.id.cvStory).setVisibility(View.VISIBLE);
            ((NestedWebView) v.findViewById(R.id.nwvStory)).loadData(resp.getDescription(), null, null);
            ((NestedWebView) v.findViewById(R.id.nwvStory)).setNestedScrollingEnabled(false);

        } else {
            v.findViewById(R.id.cvStory).setVisibility(View.GONE);
        }
    }

    /*private void setBasicInfoData() {
        //setting basic info items
        try {
            LinearLayoutCompat llBasic = v.findViewById(R.id.basicInfo);
            llBasic.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
            if (resp != null) {
                llBasic.setVisibility(View.VISIBLE);
                for (Options opt : resp) {
                    View view = getLayoutInflater().inflate(R.layout.layout_text_horizontal, (ViewGroup) llBasic, false);
                    ((TextView) view.findViewById(R.id.tv1)).setText(opt.getLabel());
                    ((TextView) view.findViewById(R.id.tv2)).setText(opt.getValue());
                    llBasic.addView(view);
                }
                applyTheme(llBasic);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

    /*  private void setContactInfoData() {
          LinearLayoutCompat llAbout = v.findViewById(R.id.contactInfo);
          // llAbout.removeAllViews();
          if (resp.getContactInformation() != null) {
              llAbout.setVisibility(View.VISIBLE);
              //add about layout items
              for (final NestedOptions opt : resp.getContactInformation()) {
                  switch (opt.getName()) {
                      case Constant.OptionType.SEE_ALL:
                          View view = getLayoutInflater().inflate(R.layout.textview_seeall, (ViewGroup) llAbout, false);
                          view.setOnClickListener(v -> performAboutOptionClick(opt));
                          llAbout.addView(view);
                          break;
                      case Constant.OptionType.CREATE_DATE:
                          view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                          ((TextView) view.findViewById(R.id.tvOptionText)).setText(Util.changeDate(opt.getValueString()));
                          ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.edit_post));
                          llAbout.addView(view);
                          break;
                      default:
                          view = getLayoutInflater().inflate(R.layout.layout_text_image_horizontal, (ViewGroup) llAbout, false);
                          ((TextView) view.findViewById(R.id.tvOptionText)).setText(opt.getLabel());
                          ((ImageView) view.findViewById(R.id.ivOptionImage)).setImageDrawable(ContextCompat.getDrawable(context, getDrawableId(opt.getName())));
                          view.setOnClickListener(v -> performAboutOptionClick(opt));
                          llAbout.addView(view);
                          break;
                  }
              }
              applyTheme(llAbout);
          } else {
              llAbout.setVisibility(View.GONE);
          }
      }
  */
    private void performAboutOptionClick(NestedOptions opt) {
        switch (opt.getName()) {
            case Constant.OptionType.CATEGORY:
                //openViewCategory();
                break;
            case Constant.OptionType.WEBSITE:
                openWebView(opt.getValueString(), opt.getValueString());
                break;
            case Constant.OptionType.PHONE:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + opt.getValueString()));
                startActivity(intent);
                break;
            case Constant.OptionType.MAIL:
                ShareCompat.IntentBuilder.from(activity)
                        .setType("message/rfc822")
                        .addEmailTo(opt.getValueString())
                        .setSubject("")
                        .setText("")
                        //.setHtmlText(body) //If you are using HTML in your body text
                        .setChooserTitle(opt.getLabel())
                        .startChooser();
                break;
            case Constant.OptionType.TAG:
                //TODO goto view feed with selected feed
                break;
            case Constant.OptionType.SEE_ALL:
                break;
            default:
                if (null != opt.getValueString() && opt.getValueString().startsWith("http")) {
                    openWebView(opt.getValueString(), opt.getValueString());
                }
                break;

        }
    }

    private void callMusicAlbumApi() {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {
                    showView(v.findViewById(R.id.pbMain));
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FUND_DESCRIPTION);
                    request.params.put(Constant.KEY_FUND_ID, mPageId);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = msg -> {
                        hideView(v.findViewById(R.id.pbMain));
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                FundResponse res = new Gson().fromJson(response, FundResponse.class);
                                if (TextUtils.isEmpty(res.getError())) {
                                    button = res.getResult().getButton();
                                    if (null != res.getResult().getCampaign()) {
                                        resp = res.getResult().getCampaign();
                                    }
                                    if (null != res.getResult().getSlides()) {
                                        photoList.clear();
                                        photoList.addAll(res.getResult().getSlides());
                                        updatePhotoAdapter();
                                    }
                                } else {
                                    Util.showSnackbar(v, res.getErrorMessage());
                                }
                            }
                            init();
                        } catch (Exception e) {
                            hideView(v.findViewById(R.id.pbMain));
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideView(v.findViewById(R.id.pbMain));
                    CustomLog.e(e);
                }
            } else {

                notInternetMsg(v);
            }

        } catch (Exception e) {

            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updatePhotoAdapter() {
        if (photoList.size() > 0) {
            v.findViewById(R.id.rvPhotos).setVisibility(View.VISIBLE);
            adapterPhoto.notifyDataSetChanged();
            ((PageIndicatorView)v.findViewById(R.id.pageIndicatorView)).setCount(adapterPhoto.getItemCount());
            v.findViewById(R.id.pageIndicatorView).setVisibility(photoList.size() > 1 ? View.VISIBLE : View.GONE);
        } else {
            v.findViewById(R.id.pageIndicatorView).setVisibility(View.GONE);
            v.findViewById(R.id.rvPhotos).setVisibility(View.GONE);
        }

    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.cvCreate:
                    Map<String, Object> map = new HashMap<>();

                    map.put(Constant.KEY_FUND_ID, mPageId);
                    //  map.put(Constant.KEY_RESOURCE_ID, albumId);
                    //map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.ALBUM);

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, UploadPhotoFragment.newInstance(map,
                                    Constant.URL_FUND_UPLOAD_PHOTO, getString(R.string.TITLE_UPLOAD_PHOTOS)))
                            .addToBackStack(null)
                            .commit();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        switch (eventType) {
            case Constant.Events.IMAGE_1:
                openSinglePhotoFragment((ImageView) data, photoList.get(position).getPhotoUrl(), "tagname" + position);
                break;
        }
        return false;
    }
}
