package com.sesolutions.sesdb;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.comment.AttachmentComment;
import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.responses.comment.Link;
import com.sesolutions.responses.feed.ActivityType;
import com.sesolutions.responses.feed.Attachment;
import com.sesolutions.responses.feed.Attribution;
import com.sesolutions.responses.feed.CommunityHiddenData;
import com.sesolutions.responses.feed.Feelings;
import com.sesolutions.responses.feed.Item_user;
import com.sesolutions.responses.feed.Like;
import com.sesolutions.responses.feed.LocationActivity;
import com.sesolutions.responses.feed.Mention;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.PeopleSuggestion;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.feed.Tagged;
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.responses.videos.Tags;

import java.util.List;

public class SesConverter {

    /*@TypeConverter
    public static <T> List<T> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<T>>() {
        }.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static <T> String someObjectListToString(List<T> someObjects) {
        return new Gson().toJson(someObjects);
    }*/

    @TypeConverter
    public static List<CommentData> toList22(String data) {
        return new Gson().fromJson(data, new TypeToken<List<CommentData>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString22(List<CommentData> someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static List<Tags> toList(String data) {
        return new Gson().fromJson(data, new TypeToken<List<Tags>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString2(List<Tags> someObjects) {
        return new Gson().toJson(someObjects);
    }


    @TypeConverter
    public static List<ReactionPlugin> toList9(String data) {
        return new Gson().fromJson(data, new TypeToken<List<ReactionPlugin>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString21(List<AttachmentComment> someObjects) {
        return new Gson().toJson(someObjects);
    }


    @TypeConverter
    public static List<AttachmentComment> toList10(String data) {
        return new Gson().fromJson(data, new TypeToken<List<AttachmentComment>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString3(List<ReactionPlugin> someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static List<Tagged> toList8(String data) {
        return new Gson().fromJson(data, new TypeToken<List<Tagged>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString4(List<Tagged> someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static List<ActivityType> toList7(String data) {
        return new Gson().fromJson(data, new TypeToken<List<ActivityType>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString5(List<ActivityType> someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static List<Mention> toList6(String data) {
        return new Gson().fromJson(data, new TypeToken<List<Mention>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString6(List<Mention> someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static List<String> toList5(String data) {
        return new Gson().fromJson(data, new TypeToken<List<String>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString7(List<String> someObjects) {
        return new Gson().toJson(someObjects);
    }


    @TypeConverter
    public static List<Item_user> toList4(String data) {
        return new Gson().fromJson(data, new TypeToken<List<Item_user>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString8(List<Item_user> someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static List<PeopleSuggestion> toList3(String data) {
        return new Gson().fromJson(data, new TypeToken<List<PeopleSuggestion>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString9(List<PeopleSuggestion> someObjects) {
        return new Gson().toJson(someObjects);
    }


    @TypeConverter
    public static List<Attachment> toList2(String data) {
        return new Gson().fromJson(data, new TypeToken<List<Attachment>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString1(List<Attachment> someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static List<Options> toList1(String data) {
        return new Gson().fromJson(data, new TypeToken<List<Options>>() {
        }.getType());
    }

    @TypeConverter
    public static String listToString(List<Options> someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static CommunityHiddenData stringToData(String data) {
        return new Gson().fromJson(data, CommunityHiddenData.class);
    }

    @TypeConverter
    public static String dataToString(CommunityHiddenData someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static Attachment stringToData8(String data) {
        return new Gson().fromJson(data, Attachment.class);
    }

    @TypeConverter
    public static String dataToString(Attachment someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static CommentData stringToData7(String data) {
        return new Gson().fromJson(data, CommentData.class);
    }

    @TypeConverter
    public static String dataToString(CommentData someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static Like stringToData6(String data) {
        return new Gson().fromJson(data, Like.class);
    }

    @TypeConverter
    public static String dataToString(Like someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static Share stringToData5(String data) {
        return new Gson().fromJson(data, Share.class);
    }

    @TypeConverter
    public static String dataToString(Share someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static Feelings stringToData1(String data) {
        return new Gson().fromJson(data, Feelings.class);
    }

    @TypeConverter
    public static String dataToString(Feelings someObjects) {
        return new Gson().toJson(someObjects);
    }


    @TypeConverter
    public static LocationActivity stringToData3(String data) {
        return new Gson().fromJson(data, LocationActivity.class);
    }

    @TypeConverter
    public static String dataToString(LocationActivity someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static Attribution stringToData2(String data) {
        return new Gson().fromJson(data, Attribution.class);
    }

    @TypeConverter
    public static String dataToString(Attribution someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static Poll stringToData9(String data) {
        return new Gson().fromJson(data, Poll.class);
    }

    @TypeConverter
    public static String dataToString(Poll someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static Item_user stringToData10(String data) {
        return new Gson().fromJson(data, Item_user.class);
    }

    @TypeConverter
    public static String dataToString(Item_user someObjects) {
        return new Gson().toJson(someObjects);
    }

    @TypeConverter
    public static Link stringToData11(String data) {
        return new Gson().fromJson(data, Link.class);
    }

    @TypeConverter
    public static String dataToString(Link someObjects) {
        return new Gson().toJson(someObjects);
    }


}
