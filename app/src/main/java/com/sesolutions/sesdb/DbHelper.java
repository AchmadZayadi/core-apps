package com.sesolutions.sesdb;

import android.content.Context;

import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.ui.welcome.NameValue;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.SPref;

import java.util.List;

public class DbHelper {

    public static void saveComments(Context context, final int rId, final String resourceType, final List<CommentData> list) {
        new Thread(() -> {
            for (CommentData commentData : list) {
                commentData.setRType(resourceType);
                commentData.setRId(rId);
                if (null != SesDB.commentDao(context).getCommentById(commentData.getCommentId())) {
                    SesDB.commentDao(context).updateComment(commentData);
                } else {
                    SesDB.commentDao(context).saveComment(commentData);
                }
            }
        }).run();
    }

    public static void saveComment(Context context, final int rId, final String resourceType, final CommentData commentData) {
        new Thread(() -> {
                commentData.setRType(resourceType);
                commentData.setRId(rId);
                if (null != SesDB.commentDao(context).getCommentById(commentData.getCommentId())) {
                    SesDB.commentDao(context).updateComment(commentData);
                } else {
                    SesDB.commentDao(context).saveComment(commentData);
                }
        }).run();
    }

   /* public static void saveThemeColors(Context context, List<NameValue> list) {


        new Thread(() -> {

            Constant.THEME_STYLES_JSON = SPref.getInstance().createThemeColors(true, list);

            if (SesDB.themeDao(context).getTheme().size() == 0) {

                SesDB.themeDao(context).savetheme(new Theme(1, Constant.THEME_STYLES_JSON));
            }

        }).run();
    }*/

}
