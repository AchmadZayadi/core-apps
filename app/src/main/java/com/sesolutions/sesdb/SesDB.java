package com.sesolutions.sesdb;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.sesolutions.BuildConfig;
import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.responses.feed.Activity;

@Database(entities = {Activity.class, CommentData.class, Theme.class}, version = 3, exportSchema = false)
@TypeConverters(SesConverter.class)
public abstract class SesDB extends RoomDatabase {
    private static SesDB instance;

    abstract SesDao getDao();

    abstract CommentDao getCommentDao();

    abstract ThemeDao getThemeDao();

    public static SesDB getInstance(Context context) {

        if (null == instance) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SesDB.class, BuildConfig.DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public static SesDao daoInstance(Context context) {
        return getInstance(context).getDao();
    }

    public static CommentDao commentDao(Context context) {
        return getInstance(context).getCommentDao();
    }

    public static ThemeDao themeDao(Context context) {

        return  getInstance(context).getThemeDao();
    }

}