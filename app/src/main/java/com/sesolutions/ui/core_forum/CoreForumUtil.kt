package com.sesolutions.ui.core_forum


import com.sesolutions.R

object CoreForumUtil {

    var dashboardUrl: String? = null

    fun openCoreForumHomeFragment(fragmentManager: androidx.fragment.app.FragmentManager) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, CoreForumHomeFragment())
                .addToBackStack(null)
                .commit()
    }

    fun openCoreSearchForumFragment(fragmentManager: androidx.fragment.app.FragmentManager){
        fragmentManager.beginTransaction()
                .replace(R.id.container, CoreSearchForumFragment())
                .addToBackStack(null)
                .commit()
    }

    fun openViewForumFragment(fragmentManager: androidx.fragment.app.FragmentManager, forumId: Int) {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        ViewCoreForumFragment.newInstance(forumId)).addToBackStack(null)
                .commit()
    }
    fun openCoreViewTopicFragment(fragmentManager: androidx.fragment.app.FragmentManager, topicId : Int) {

        fragmentManager.beginTransaction()
                .replace(R.id.container, ViewCoreTopicFragment.newInstance(topicId))
                .addToBackStack(null)
                .commit()

    }
}