package com.sesolutions.ui.music_core;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.core.view.ViewCompat;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.music.AlbumView;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.music_album.AddToPlaylistFragment;
import com.sesolutions.ui.music_album.ViewMusicAlbumFragment;
import com.sesolutions.ui.music_album.ViewSongFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 29/11/17.
 */

public class CMusicHelper extends CommentLikeHelper {

    public OnUserClickedListener<Integer, Object> parent;
    public List<Albums> albumsList;
    public CMusicAdapter adapter;
    protected String selectedScreen;


    public void applyTheme() {
        if (null != v) {
            new ThemeManager().applyTheme((ViewGroup) v, context);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        try {
            switch (object1) {
                case Constant.Events.MUSIC_ADD:
                    goToFormFragment(Integer.valueOf("" + screenType), postion);
                    break;
                case Constant.Events.MUSIC_FAB_PLAY:
                    playMusic(albumsList.get(postion));
                    break;
                case Constant.Events.MUSIC_LIKE:
                    callLikeApi(Constant.Events.MUSIC_LIKE, Integer.valueOf("" + screenType), postion, Constant.URL_MUSIC_LIKE, albumsList.get(postion));
                    break;
                case Constant.Events.MUSIC_MAIN:
                    goToNextScreen(postion, (CMusicAdapter.ContactHolder) screenType);
                    break;
            }
            return super.onItemClicked(object1, screenType, postion);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void goToFormFragment(int screenType, int position) {
        Map<String, Object> map = new HashMap<>();
        int type;
        if (screenType == Constant.FormType.TYPE_MUSIC_ALBUM) {
            type = Constant.FormType.TYPE_ADD_ALBUM;
            map.put(Constant.KEY_ALBUM_ID, albumsList.get(position).getAlbumId());
        } else {
            type = Constant.FormType.TYPE_ADD_SONG;
            map.put(Constant.KEY_SONG_ID, albumsList.get(position).getSongId());
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        AddToPlaylistFragment.newInstance(type, map, Constant.URL_CREATE_PLAYLIST))
                .addToBackStack(null)
                .commit();
    }


    private void goToNextScreen(int screenType, CMusicAdapter.ContactHolder holder) {
        int position = holder.getAdapterPosition();

        //adding transition name in image and title
        String transitionName = screenType == Constant.FormType.TYPE_ARTISTS ? albumsList.get(position).getName() : albumsList.get(position).getTitle();
        ViewCompat.setTransitionName(holder.ivSongImage, transitionName);
        ViewCompat.setTransitionName(holder.tvSongTitle, transitionName + Constant.Trans.TEXT);
        ViewCompat.setTransitionName(holder.ivFavorite, transitionName + Constant.Trans.ICON);

        //put all transition names in bundle pass to next screen
        Bundle bundle = new Bundle();
        bundle.putString(Constant.Trans.IMAGE, transitionName);
        bundle.putString(Constant.Trans.TEXT, transitionName + Constant.Trans.TEXT);
        bundle.putString(Constant.Trans.ICON, transitionName + Constant.Trans.ICON);
        bundle.putString(Constant.Trans.IMAGE_URL, albumsList.get(position).getImageUrl());

        switch (screenType) {
            case Constant.FormType.TYPE_SONGS:
                //Dont open song view page in case of SE plugin
                Map<String, Object> map = new HashMap<>();
                break;

            case Constant.FormType.TYPE_MUSIC_ALBUM:
                break;
            case Constant.FormType.TYPE_PLAYLIST:
                goToViewPlaylistFragment(position, holder, bundle);
                break;
        }
    }


    private void goToViewMusicAlbumFragment(int position, CMusicAdapter.ContactHolder holder, Bundle bundle) {

        try {
            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivSongImage, ViewCompat.getTransitionName(holder.ivSongImage))
                    .addSharedElement(holder.tvSongTitle, ViewCompat.getTransitionName(holder.tvSongTitle))
                    .replace(R.id.container, ViewMusicAlbumFragment.newInstance(albumsList.get(position).getAlbumId(), bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ViewMusicAlbumFragment.newInstance(albumsList.get(position).getAlbumId()))
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void goToViewPlaylistFragment(int postion, CMusicAdapter.ContactHolder holder, Bundle bundle) {
        try {
            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivSongImage, ViewCompat.getTransitionName(holder.ivSongImage))
                    .addSharedElement(holder.ivFavorite, ViewCompat.getTransitionName(holder.ivFavorite))
                    .addSharedElement(holder.tvSongTitle, ViewCompat.getTransitionName(holder.tvSongTitle))
                    .replace(R.id.container, ViewCMusicFragment.newInstance(albumsList.get(postion).getPlaylistId(), bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            fragmentManager.beginTransaction()
                    .replace(R.id.container
                            , ViewCMusicFragment.newInstance(albumsList.get(postion).getPlaylistId()))
                    .addToBackStack(null)
                    .commit();
        }
    }


    private void goToSongsView(Map<String, Object> map, int songId, String resourceType, CMusicAdapter.ContactHolder holder, Bundle bundle) {

        try {
            fragmentManager.beginTransaction()
                    .addSharedElement(holder.ivSongImage, ViewCompat.getTransitionName(holder.ivSongImage))
                    //   .addSharedElement(holder.llMain, ViewCompat.getTransitionName(holder.llMain))
                    .addSharedElement(holder.tvSongTitle, ViewCompat.getTransitionName(holder.tvSongTitle))
                    .replace(R.id.container, ViewSongFragment.newInstance(map, songId, resourceType, bundle)).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ViewSongFragment.newInstance(map, songId, resourceType))
                    .addToBackStack(null)
                    .commit();
        }


    }

    private void playMusic(Albums albums) {
        ((CommonActivity) activity).showMusicLayout();
        ((CommonActivity) activity).songPicked(albums);

    }


    private void callLikeApi(final int REQ_CODE, final int screenType, final int position, String url, final Albums vo) {

        try {
            if (isNetworkAvailable(context)) {
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    int resourceId = vo.getAlbumId();
                    if (screenType == Constant.FormType.TYPE_PLAYLIST) {
                        resourceId = vo.getPlaylistId();
                    } else if (screenType == Constant.FormType.TYPE_SONGS) {
                        resourceId = vo.getSongId();
                    } else if (screenType == Constant.FormType.TYPE_LYRICS) {
                        resourceId = vo.getSongId();
                    } else if (screenType == Constant.FormType.TYPE_ARTISTS) {
                        resourceId = vo.getArtistId();
                    }
                    request.params.put(Constant.KEY_RESOURCE_ID, resourceId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, vo.getResourceType());
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    if (REQ_CODE == Constant.Events.MUSIC_LIKE) {
                                        albumsList.get(position).toggleLike();
                                    } else {
                                        albumsList.get(position).toggleFavorite();
                                    }
                                    adapter.notifyItemChanged(position);
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

    public String getDetail(AlbumView album) {
        String detail = "";
        detail += "\uf164 " + context.getResources().getQuantityString(R.plurals.like_count, album.getLikeCount(), album.getLikeCount())
                + "  \uf075 " + context.getResources().getQuantityString(R.plurals.comment_count, album.getCommentCount(), album.getCommentCount())
                + "  \uf06e " + context.getResources().getQuantityString(R.plurals.views_count, album.getViewCount(), album.getViewCount());
        //  + "  \uf075 " + album.getCommentCount() + (album.getCommentCount() != 1 ? " Comments" : " Comment")
        // + "  \uf06e " + album.getViewCount() + (album.getViewCount() != 1 ? " Views" : " View");

        return detail;
    }
}
