package com.sesolutions.responses.forum

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.responses.PaginationHelper
import com.sesolutions.responses.feed.Images
import java.util.*

class ForumResponse2 : ErrorResponse() {


    @Expose
    @SerializedName("session_id")
    val session_id: String? = null
    @Expose
    @SerializedName("result")
    val result: Result? = null

    inner class Result : PaginationHelper() {

        @Expose
        @SerializedName("stats")
        val stats: Stats? = null
        @Expose
        @SerializedName("dashboard_url")
        val dashboardUrl: String? = null

        @SerializedName("category_description")
        val categoryDesc: String? = null

        @SerializedName("categories")
        val categories: List<Category>? = null

        @SerializedName("subcat")
        val subcat: List<Category>? = null

        @SerializedName("subsubcat")
        val subsubcat: List<Category>? = null

        @SerializedName("topics")
        val topics: List<Topic>? = null

        @SerializedName("button")
        val button: TopicContent.Buttons? = null

        @SerializedName("forums")
        val forums: List<ForumContent>? = null

        @SerializedName("posts")
        val posts: List<Post>? = null

        @SerializedName("moderators")
        val moderators: List<Moderator>? = null

        @SerializedName("topic_content")
        val topicContent: TopicContent? = null

        fun getCategoriesList(screenType: String): List<ForumVo> {
            val result = ArrayList<ForumVo>()
            when {
                categories != null -> for (vo in categories) {
                    result.add(ForumVo(screenType, vo))
                }
                subcat != null -> for (vo in subcat) {
                    result.add(ForumVo(screenType, vo))
                }
                subsubcat != null -> for (vo in subsubcat) {
                    result.add(ForumVo(screenType, vo))
                }
            }
            return result
        }

        fun getForumList(screenType: String): List<ForumVo> {
            val result = ArrayList<ForumVo>()
            if (forums != null) {
                for (vo in forums) {
                    result.add(ForumVo(screenType, vo))
                }
            }
            return result
        }

        fun getTopicList(screenType: String): List<ForumVo> {
            val result = ArrayList<ForumVo>()
            if (topics != null) {
                for (vo in topics) {
                    result.add(ForumVo(screenType, vo))
                }
            }
            return result
        }
    }

    data class Category(
            @SerializedName("cat_icon")
            val catIcon: String?,
            @SerializedName("category_id")
            val categoryId: Int?,
            @SerializedName("category_name")
            val categoryName: String?,
            @SerializedName("creation_date")
            val creationDate: String?,
            val description: String?,
            @SerializedName("modified_date")
            val modifiedDate: String?,
            val order: Int?,
            val privacy: String?,
            @SerializedName("sesforum_count")
            val sesforumCount: Int?,
            val slug: String?,
            val subcat: List<Category>?,
            val forums : List<ForumContent>?,
            val subsubcat: List<Category>,
            @SerializedName("subcat_id")
            val subcatId: Int?,
            @SerializedName("subsubcat_id")
            val subsubcatId: Int?,
            val title: String?,
            val type: String
    )

    inner class Stats {
        @Expose
        @SerializedName("total_active_users")
        val total_active_users: Int = 0
        @Expose
        @SerializedName("total_users")
        val total_users: Int = 0
        @Expose
        @SerializedName("post_count")
        val post_count: String? = null
        @Expose
        @SerializedName("topic_count")
        val topic_count: String? = null
        @Expose
        @SerializedName("forum_count")
        val forum_count: Int = 0
    }

    inner class Moderator {
        @Expose
        @SerializedName("label")
        val label: String? = null
        @Expose
        @SerializedName("moderators")
        val moderators: String? = null
        @Expose
        @SerializedName("topic_create")
        val topic_create: String? = null
        @Expose
        @SerializedName("forum_title")
        val forumTitle: String? = null
    }

    inner class ForumContent {
        @Expose
        @SerializedName("last_post")
        val last_post: List<LastPost>? = null
        @Expose
        @SerializedName("like_count")
        val like_count: Int = 0
        @SerializedName("forum_icon")
        val forumIcon: String? = null
        @SerializedName("icon")
        val forumIconcore: String? = null
        @Expose
        @SerializedName("lastposter_id")
        val lastposter_id: Int = 0
        @Expose
        @SerializedName("lastpost_id")
        val lastpost_id: Int = 0
        @Expose
        @SerializedName("post_count")
        val post_count: Int = 0
        @Expose
        @SerializedName("topic_count")
        val topic_count: Int = 0
        @Expose
        @SerializedName("view_count")
        val view_count: Int = 0
        @Expose
        @SerializedName("file_id")
        val file_id: Int = 0
        @Expose
        @SerializedName("order")
        val order: Int = 0
        @Expose
        @SerializedName("modified_date")
        val modified_date: String? = null
        @Expose
        @SerializedName("creation_date")
        val creation_date: String? = null
        @Expose
        @SerializedName("description")
        val description: String? = null
        @Expose
        @SerializedName("title")
        val title: String? = null
        @Expose
        @SerializedName("category_id")
        val category_id: Int = 0
        @Expose
        @SerializedName("forum_id")
        val forum_id: Int = 0
    }

    inner class LastPost {
        @Expose
        @SerializedName("topic_id")
        val topic_id: Int = 0
        @Expose
        @SerializedName("topic_title")
        val topic_title: String? = null
        @Expose
        @SerializedName("post_creation_date")
        val post_creation_date: String? = null
        @Expose
        @SerializedName("user_title")
        val user_title: String? = null
        @Expose
        @SerializedName("user_id")
        val user_id: Int = 0
        @Expose
        @SerializedName("user_images")
        val user_images: String? = null
    }

    inner class Topic {

        @SerializedName("topic_title")
        val topicTitle: String? = null
        @SerializedName("thanks_count")
        val thankCount: String? = null
        @Expose
        @SerializedName("images")
        val images: Images? = null
        @Expose
        @SerializedName("last_post")
        val last_post: List<LastPost>? = null
        @Expose
        @SerializedName("resource_type")
        val resource_type: String? = null
        @Expose
        @SerializedName("owner_image")
        val owneImages: OwnerImage? = null
        @Expose
        @SerializedName("description")
        val description: String? = null
        @Expose
        @SerializedName("owner_title")
        val ownerTitle: String? = null
        @Expose
        @SerializedName("rating")
        val rating: Float = 0.toFloat()
        @Expose
        @SerializedName("show_rating")
        val isShowRating: Boolean = false
        @Expose
        @SerializedName("like_count")
        val like_count: String? = null
        @Expose
        @SerializedName("lastposter_id")
        val lastposter_id: Int = 0
        @Expose
        @SerializedName("lastpost_id")
        val lastpost_id: Int = 0
        @Expose
        @SerializedName("view_count")
        val view_count: Int = 0
        @Expose
        @SerializedName("post_count")
        val post_count: Int = 0
        @Expose
        @SerializedName("closed")
        val closed: Int = 0
        @Expose
        @SerializedName("sticky")
        val sticky: Int = 0
        @Expose
        @SerializedName("modified_date")
        val modified_date: String? = null
        @Expose
        @SerializedName("creation_date")
        val creation_date: String? = null
        @Expose
        @SerializedName("title")
        val title: String? = null
        @Expose
        @SerializedName("user_id")
        val user_id: Int = 0
        @Expose
        @SerializedName("forum_id")
        val forum_id: Int = 0
        @Expose
        @SerializedName("topic_id")
        val topic_id: Int = 0

        inner class LastPost {

            @Expose
            @SerializedName("creation_date")
            val creation_date: String? = null
            @Expose
            @SerializedName("user_title")
            val user_title: String? = null
            @Expose
            @SerializedName("user_id")
            val user_id: Int = 0
            @Expose
            @SerializedName("user_images")
            val user_images: String? = null
        }

        inner class OwnerImage {
            val icon: String? = null
            val main: String? = null
            val normal: String? = null
            val normalMain: String? = null
        }
    }

}
