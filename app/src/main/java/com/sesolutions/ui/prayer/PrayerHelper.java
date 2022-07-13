package com.sesolutions.ui.prayer;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.quote.Quote;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.quotes.QuoteAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;

/**
 * Created by root on 29/11/17.
 */

public class PrayerHelper extends CommentLikeHelper {

    private static final int REQ_LIKE = 100;
    private static final int REQ_FAVORITE = 200;
    private static final int REQ_FOLLOW = 300;
    public PrayerParentFragment parent;
    public int categoryId;
    public List<Quote> videoList;
    public QuoteAdapter adapter;
    public List<Category> categoryList;


    public RelativeLayout hiddenPanel;
    public List<Options> menuItem;

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        try {
            switch (object1) {
                case Constant.Events.CLICKED_HEADER_IMAGE:
                case Constant.Events.CLICKED_HEADER_TITLE:
                    goToProfileFragment(videoList.get(postion).getOwnerId());
                    break;
                case Constant.Events.MUSIC_LIKE:
                    callLikeApi(Integer.valueOf("" + screenType), REQ_LIKE, postion, Constant.URL_MUSIC_LIKE, videoList.get(postion));
                    break;
                case Constant.Events.MUSIC_MAIN:
                    goToViewPrayerFragment(videoList.get(postion).getPrayerId());
                    break;
                case Constant.Events.CATEGORY:
                    if (categoryId != postion) { //do not open same category again
                        openViewPrayerCategoryFragment(postion, "" + screenType, false);

                        // openViewPrayerCategoryFragment((QuoteCategoryAdapter.ContactHolder) screenType);
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, screenType, postion);
    }

   /* public void openViewPrayerCategoryFragment(QuoteCategoryAdapter.ContactHolder holder) {
        int position = holder.getAdapterPosition();

        //adding transition name in image and title
        String transitionName = categoryList.get(position).getLabel();
        //ViewCompat.setTransitionName(holder.ivSongImage, transitionName);
        ViewCompat.setTransitionName(holder.tvTitle, transitionName + Constant.Trans.TEXT);

        //put all transition names in bundle pass to next screen
        Bundle bundle = new Bundle();
        //   bundle.putString(Constant.Trans.IMAGE, transitionName);
        bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
        //   bundle.putString(Constant.Trans.ICON, transitionName + Constant.Trans.ICON);
        //  bundle.putString(Constant.Trans.IMAGE_URL, albumsList.get(position).getImages().getMain());

        try {
            fragmentManager.beginTransaction()
                    // .addSharedElement(holder.ivSongImage, ViewCompat.getTransitionName(holder.ivSongImage))
                    // .addSharedElement(holder.ivFavorite, ViewCompat.getTransitionName(holder.ivFavorite))
                    .addSharedElement(holder.tvTitle, ViewCompat.getTransitionName(holder.tvTitle))
                    .replace(R.id.container, ViewPrayerCategoryFragment.newInstance(categoryId, holder.tvTitle.getText().toString(), false, bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);

            fragmentManager.beginTransaction()
                    .replace(R.id.container, ViewPrayerCategoryFragment.newInstance(categoryId, holder.tvTitle.getText().toString(), false, null))
                    .addToBackStack(null)
                    .commit();
        }
    }*/

  /*  private void performFeedOptionClick(int actionId, Options vo, int actPosition, int position) {
        switch (vo.getName()) {
            case Constant.OptionType.DELETE:
                showDeleteDialog(context, actionId, vo, actPosition, position);
                //callSaveFeedApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);
                break;
            case Constant.OptionType.SAVE:
                callSaveFeedApi(REQ_CODE_OPTION_SAVE, Constant.URL_FEED_SAVE, actionId, vo, actPosition, position);
                break;
        }
    }*/


    public void slideUpDown() {
        if (!isPanelShown()) {
            // Show the panel
            Animation bottomUp = AnimationUtils.loadAnimation(context, R.anim.bootom_up);
            hiddenPanel.startAnimation(bottomUp);
            hiddenPanel.setVisibility(View.VISIBLE);
            // isPanelShown = true;
        } else {
            // Hide the Panel
            Animation bottomDown = AnimationUtils.loadAnimation(context, R.anim.bootom_down);

            hiddenPanel.startAnimation(bottomDown);
            hiddenPanel.setVisibility(View.GONE);
            // isPanelShown = false;
        }
    }

    private boolean isPanelShown() {
        return hiddenPanel.getVisibility() == View.VISIBLE;
    }


   /* private void setFeedUpdateRecycleView(int position) {
        try {
            RecyclerView recycleViewFeedUpdate = v.findViewById(R.id.rvFeedUpdate);
            recycleViewFeedUpdate.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recycleViewFeedUpdate.setLayoutManager(layoutManager);
            FeedUpdateAdapter adapterFeed = new FeedUpdateAdapter(menuItem, context, this, null);
            adapterFeed.setActivityPosition(position);
            recycleViewFeedUpdate.setAdapter(adapterFeed);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/


    private void callLikeApi(int screenType, final int REQ_CODE, final int position, String url, final Quote vo) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                updateItemLikeFavorite(REQ_CODE, position, vo);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_RESOURCE_ID, vo.getPrayerId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PRAYER);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        /*if (REQ_CODE == REQ_LIKE) {
                                            videoList.get(position).setContentLike(!vo.isContentLike());
                                        } else if (REQ_CODE == REQ_FAVORITE) {
                                            videoList.get(position).setContentFavourite(!vo.isContentFavourite());
                                        }
                                        adapter.notifyItemChanged(position);*/
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

                    hideBaseLoader();
                }
            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateItemLikeFavorite(int REQ_CODE, int position, Quote vo) {
        if (REQ_CODE == REQ_LIKE) {
            videoList.get(position).setContentLike(!vo.isContentLike());
            adapter.notifyItemChanged(position);
        } /*else if (REQ_CODE == REQ_FAVORITE) {
            videoList.get(position).setContentFavourite(!vo.isContentFavourite());
            adapter.notifyItemChanged(position);
        }*/

    }


    public String getDetail(Albums album) {
        String detail = "";
        detail += "\uf164 " + album.getLikeCount()// + (album.getLikeCount() != 1 ? " Likes" : " Like")
                + "  \uf075 " + album.getCommentCount() //+ (album.getCommentCount() != 1 ? " Comments" : " Comment")
                + "  \uf004 " + album.getFavouriteCount() //+ (album.getFavouriteCount() != 1 ? " Favorites" : " Favorite")
                + "  \uf06e " + album.getViewCount()// + (album.getViewCount() != 1 ? " Views" : " View");
        ;//+ "  \uf03e " + album.getPhotoCount();// + (album.getSongCount() > 1 ? " Songs" : " Song");

        return detail;
    }


}
