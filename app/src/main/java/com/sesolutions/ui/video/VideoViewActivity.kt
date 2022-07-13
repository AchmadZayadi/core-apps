package com.sesolutions.ui.video

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.FragmentManager
import com.sesolutions.R
import com.sesolutions.responses.videos.ViewVideo
import com.sesolutions.ui.common.BaseActivity
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog

/**
 * Created by root on 29/1/18.
 */

class VideoViewActivity : BaseActivity() {

    private var fragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        try {
            fragmentManager = supportFragmentManager
            val bundle = intent.extras
            val dest = bundle!!.getInt(Constant.DESTINATION_FRAGMENT)
            if (dest == Constant.GoTo.VIEW_VIDEO_PLAYLIST) {
                goToVideoPlaylistFragment(bundle.getInt(Constant.KEY_ID))
            } else {
                val rcType = bundle.getString(Constant.KEY_TYPE, "")
                if (Constant.ResourceType.CONTEST == rcType) {
                    val url = bundle.getString(Constant.KEY_URI, "")
                    val iFrameData = bundle.getString(Constant.KEY_DATA, "")
                    fragmentManager!!.beginTransaction()
                            .replace(R.id.container, IFrameVideoFragment.newInstance(url, iFrameData))
                            .addToBackStack(null)
                            .commit()
                } else {
                    goToViewVideoFragment(bundle.getInt(Constant.KEY_ID), rcType)
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }


    /*   @Override
    public void onBackPressed() {
        try {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
            } else {
                supportFinishAfterTransition();
            }
        } catch (Exception e) {
            CustomLog.e(e);
            supportFinishAfterTransition();
        }
    }*/

    private fun goToViewVideoFragment(videoId: Int, rcType: String) {
        /*if (Constant.ResourceType.PAGE_VIDEO.equals(rcType)) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container
                            , ViewPageVideoFragment.newInstance(videoId, rcType))
                    .addToBackStack(null)
                    .commit();
        } else {*/
        fragmentManager!!.beginTransaction()
                .replace(R.id.container, ViewVideoFragment.newInstance(videoId, rcType))
                .addToBackStack(null)
                .commit()
        //  }
    }

    private fun goToVideoPlaylistFragment(videoId: Int) {
        var url: String? = null
        var vo: ViewVideo? = null
        if (!TextUtils.isEmpty(Constant.viewVideoPlaylistChannelUrl)) {
            url = Constant.viewVideoPlaylistChannelUrl
            vo = Constant.videoVo
            Constant.viewVideoPlaylistChannelUrl = null
            Constant.videoVo = null
        }
        fragmentManager!!.beginTransaction()
                .replace(R.id.container, ViewPlaylistVideoFragment.newInstance(videoId, url, vo))
                .addToBackStack(null)
                .commit()
    }

    override fun onUserLeaveHint() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.enterPictureInPictureMode()
        }
    }
}
