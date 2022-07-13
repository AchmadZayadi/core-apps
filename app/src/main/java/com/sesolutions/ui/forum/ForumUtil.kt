package com.sesolutions.ui.forum

import androidx.fragment.app.FragmentManager

import com.sesolutions.R
import com.sesolutions.responses.forum.ForumResponse

object ForumUtil {

    var dashboardUrl: String? = null

    fun openForumHomeFragment(fragmentManager: androidx.fragment.app.FragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        ForumHomeFragment()).addToBackStack(null)
                .commit()
    }

    fun openViewForumFragment(fragmentManager: androidx.fragment.app.FragmentManager, forumId: Int) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        ViewForumFragment.newInstance(forumId)).addToBackStack(null)
                .commit()
    }

    fun openViewForumCategoryFragment(fragmentManager: androidx.fragment.app.FragmentManager, title: String, categoryId: Int) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        ForumCategoryViewFragment.newInstance(title, categoryId)).addToBackStack(null)
                .commit()
    }

    fun openViewTopicFragment(fragmentManager: androidx.fragment.app.FragmentManager, topicId: Int) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        ViewTopicFragment.newInstance(topicId)).addToBackStack(null)
                .commit()
    }

    fun openViewGroupTopicFragment(fragmentManager: androidx.fragment.app.FragmentManager, topicId: Int) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        ViewGroupTopicFragment.newInstance(topicId)).addToBackStack(null)
                .commit()
    }

    fun gotoSearchFragment(fragmentManager: androidx.fragment.app.FragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        SearchForumFragment()).addToBackStack(null)
                .commit()
    }

    fun clickSayThank(fragmentManager: androidx.fragment.app.FragmentManager, topicId: Int) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        ViewTopicFragment.newInstance(topicId)).addToBackStack(null)
                .commit()
    }
}
