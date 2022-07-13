package com.sesolutions.responses.story

import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.PaginationHelper
import com.sesolutions.responses.feed.Options
import com.sesolutions.ui.storyview.StoryModel

class Result : PaginationHelper() {
    val stories: List<StoryModel>? = null
    val story: StoryModel? = null
    @SerializedName("my_story")
    val myStory: StoryModel? = null
    val option: Options? = null
    val viewers: List<StoryModel>? = null
}
