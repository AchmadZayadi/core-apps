package com.sesolutions.ui.clickclick.me;

import android.view.ViewGroup;

import com.sesolutions.responses.music.AlbumView;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.utils.CustomLog;

import java.util.List;

/**
 * Created by root on 29/11/17.
 */

public class MeHelper extends CommentLikeHelper {

    public List<Videos> albumsList;
    public MeAdapter adapter;
    public MeAdapter2 adapter2;
    public FollowerAdapter Followeradapter;


    public void applyTheme() {
        if (null != v) {
            new ThemeManager().applyTheme((ViewGroup) v, context);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        try {
            switch (object1) {


            }
            return super.onItemClicked(object1, screenType, postion);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }




    public String getDetail(AlbumView album) {
        String detail = "";
        detail += "\uf164 " + album.getLikeCount() + (album.getLikeCount() != 1 ? " Likes" : " Like")
                + "  \uf075 " + album.getCommentCount() + (album.getCommentCount() != 1 ? " Comments" : " Comment")
                // + "  \uf004 " + album.getFavouriteCount() + (album.getFavouriteCount() != 1 ? " Favorites" : " Favorite")
                + "  \uf06e " + album.getViewCount() + (album.getViewCount() != 1 ? " Views" : " View");
        // + "  \uf001 " + album.getSongCount() + (album.getSongCount() > 1 ? " Songs" : " Song");

        return detail;
    }
}
