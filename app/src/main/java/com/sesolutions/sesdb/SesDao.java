package com.sesolutions.sesdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sesolutions.responses.feed.Activity;

import java.util.List;

@Dao
public interface SesDao {

    @Insert
    void saveFeed(Activity feed);

    @Insert
    void saveFeeds(List<Activity> feedList);

    @Query("SELECT * FROM Activity LIMIT :limit")
    Activity fetchFeeds(int limit);

    @Query("SELECT * FROM Activity WHERE actionId= :actionId")
    Activity getFeedById(int actionId);

    @Query("DELETE FROM Activity WHERE actionId= :actionId")
    void deleteFeedById(int actionId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFeed(Activity feed);

    @Delete
    void deleteFeed(Activity feed);


}
