package com.sesolutions.ui.crowdfunding;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.fund.FundResponse;
import com.sesolutions.responses.page.NestedOptions;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FundInfoFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private LinearLayoutCompat llMain;
    private List<Options> infoList;
    private int mPageId;
    private Options button;

    public static FundInfoFragment newInstance(int userId) {
        FundInfoFragment frag = new FundInfoFragment();
        frag.mPageId = userId;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_page_info, container, false);
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

    private void init() {
        try {
            llMain = v.findViewById(R.id.llInfo);
            if (infoList != null) {
                //show main layout
                llMain.setVisibility(View.VISIBLE);
                updateButton();
                setBasicInfoData();
                // setOpenHoursData();
                // setDetail();
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


    /*private void addPeopleLists(String s, List<Albums> list, int dImage, int total, String type) {
        try {
            if (list == null) {
                //do not set data in case of null response
                return;
            }
            LinearLayoutCompat llInfo = v.findViewById(R.id.llInfo);
            View view = getLayoutInflater().inflate(R.layout.layout_page_info_bottom, (ViewGroup) llInfo, false);
            ((TextView) view.findViewById(R.id.tvItemTitle)).setText(s);
            ((ImageView) view.findViewById(R.id.ivTitleImage)).setImageDrawable(ContextCompat.getDrawable(context, dImage));
            if (list.size() > 0) {
                View item1 = view.findViewById(R.id.item1);
                item1.setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.tvItemText)).setText(list.get(0).getName());
                Util.showImageWithGlide((ImageView) view.findViewById(R.id.ivItemImage), list.get(0).getImages().getMain(), context, 1);
                final int userId = list.get(0).getUserId();
                final int pageId = list.get(0).getPageId();
                item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userId > 0) {
                            goToProfileFragment(userId);
                        } else {
                            openViewPageFragment(pageId);
                        }
                    }
                });

            }
            if (list.size() > 1) {
                View item1 = view.findViewById(R.id.item2);
                item1.setVisibility(View.VISIBLE);
                ((TextView) item1.findViewById(R.id.tvItemText)).setText(list.get(1).getName());
                Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(1).getImages().getMain(), context, 1);
                final int userId = list.get(1).getUserId();
                final int pageId = list.get(1).getPageId();
                item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userId > 0) {
                            goToProfileFragment(userId);
                        } else {
                            openViewPageFragment(pageId);
                        }
                    }
                });
                if (list.get(1).getImages() != null)
                    Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(1).getImages().getMain(), context, 1);
                // llInfo.addView(view);
            }
            if (list.size() > 2) {
                View item1 = view.findViewById(R.id.item3);
                item1.setVisibility(View.VISIBLE);
                ((TextView) item1.findViewById(R.id.tvItemText)).setText(list.get(2).getName());
                Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(2).getImages().getMain(), context, 1);
                final int userId = list.get(2).getUserId();
                final int pageId = list.get(2).getPageId();
                item1.setOnClickListener(v -> {
                    if (userId > 0) {
                        goToProfileFragment(userId);
                    } else {
                        openViewPageFragment(pageId);
                    }
                });
            }
            if (list.size() > 3) {
                View item1 = view.findViewById(R.id.item4);
                item1.setVisibility(View.VISIBLE);
                ((TextView) item1.findViewById(R.id.tvItemText)).setText(list.get(3).getName());
                Util.showImageWithGlide((ImageView) item1.findViewById(R.id.ivItemImage), list.get(3).getImages().getMain(), context, 1);
                final int userId = list.get(3).getUserId();
                final int pageId = list.get(3).getPageId();
                item1.setOnClickListener(v -> {
                    if (userId > 0) {
                        goToProfileFragment(userId);
                    } else {
                        openViewPageFragment(pageId);
                    }
                });
                //show count if total count is not 0
                if (total > 0) {
                    item1.findViewById(R.id.vItem).setVisibility(View.VISIBLE);
                    ((TextView) item1.findViewById(R.id.tvItemCount)).setText("+" + total);
                    item1.findViewById(R.id.tvItemCount).setVisibility(View.VISIBLE);
                    item1.setOnClickListener(v -> {
                        openMoreMemberFragment(type);
                    });
                } else {
                    item1.setOnClickListener(v -> {
                        if (userId > 0) {
                            goToProfileFragment(userId);
                        } else {
                            openViewPageFragment(pageId);
                        }
                    });
                }
            }

            llInfo.addView(view);
            applyTheme(llInfo);
        } catch (Exception e) {
            CustomLog.e(e);
        }


    }*/

   /* private void openMoreMemberFragment(String type) {
        int id;
        switch (type) {
            case "like":
                id = R.string.people_liked;
                break;
            case "follow":
                id = R.string.people_followed;
                break;
            case "favourite":
                id = R.string.people_favorite;
                break;
            case "page":
                id = R.string.pages_liked;
                break;
            default:
                id = R.string.people_liked;
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_MODULE,MenuTab.Page.INFO);
        bundle.putString(Constant.KEY_TITLE, getStrings(id));
        bundle.putString(Constant.KEY_TYPE, type);
        bundle.putInt(Constant.KEY_ID, mPageId);
        fragmentManager.beginTransaction().replace(R.id.container, MoreMemberFragment.newInstance(bundle)).addToBackStack(null).commit();
    }*/

   /* private void setDetail() {
        //set "detail" data
        if (!TextUtils.isEmpty(resp.getDescription())) {
            v.findViewById(R.id.llDetail).setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ((TextView) v.findViewById(R.id.tvDetail)).setText(Html.fromHtml(resp.getDescription(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                ((TextView) v.findViewById(R.id.tvDetail)).setText(Html.fromHtml(resp.getDescription()));
            }
            ((TextView) v.findViewById(R.id.tvDetail)).setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            v.findViewById(R.id.llDetail).setVisibility(View.GONE);
        }
    }*/

    private void setBasicInfoData() {
        //setting basic info items
        try {
            LinearLayoutCompat llBasic = v.findViewById(R.id.basicInfo);
            llBasic.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
            if (infoList != null) {
                llBasic.setVisibility(View.VISIBLE);
                for (Options opt : infoList) {
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
    }

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
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FUND_ABOUT);
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

                                    if (null != res.getResult().getAbout()) {
                                        infoList = res.getResult().getAbout();
                                        button = res.getResult().getButton();
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


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.cvCreate:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_FUND_ID, mPageId);
                    openFormFragment(Constant.FormType.EDIT_CONTACT, map, Constant.URL_FUND_CONTACT);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
