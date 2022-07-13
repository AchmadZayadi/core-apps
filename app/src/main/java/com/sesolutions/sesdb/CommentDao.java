package com.sesolutions.sesdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sesolutions.responses.comment.CommentData;

import java.util.List;

@Dao
public interface CommentDao {

    @Insert
    void saveComments(List<CommentData> commentDataList);

    @Insert
    void saveComment(CommentData commentData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateComment(CommentData comment);

    @Query("SELECT * FROM CommentData WHERE commentId= :commentId")
    CommentData getCommentById(int commentId);

    @Query("DELETE FROM CommentData WHERE commentId= :commentId")
    void deleteCommentById(int commentId);

    @Delete
    void deleteComment(CommentData comment);

    @Query("SELECT * FROM CommentData WHERE rId= :resourceId AND rType=:resourceType ORDER BY commentId DESC LIMIT :limit OFFSET :index")
    LiveData<List<CommentData>> fetchComments(int resourceId, String resourceType, int limit, int index);

    @Query("SELECT * FROM CommentData WHERE rId= :resourceId AND rType=:resourceType ORDER BY commentId DESC LIMIT :limit OFFSET :index")
    List<CommentData> fetchCommentList(int resourceId, String resourceType, int limit, int index);

    @Query("SELECT COUNT(*) FROM CommentData WHERE rId= :resourceId AND rType=:resourceType")
    int fetchCommentCount(int resourceId, String resourceType);

    @Query("SELECT * FROM CommentData")
    LiveData<List<CommentData>> fetchComments();

}
