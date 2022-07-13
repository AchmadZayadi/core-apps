package com.sesolutions.ui.forum

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Handler
import android.os.Message
import androidx.core.app.ShareCompat
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.sesolutions.R
import com.sesolutions.http.HttpRequestHandler
import com.sesolutions.http.HttpRequestVO
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.responses.album.Albums
import com.sesolutions.responses.page.Locations
import com.sesolutions.responses.page.PageContent
import com.sesolutions.responses.page.PageResponse
import com.sesolutions.responses.page.PageVo
import com.sesolutions.responses.videos.Category
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.common.CommentLikeHelper
import com.sesolutions.ui.page.CreateEditPageFragment
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.SPref
import com.sesolutions.utils.Util
import org.apache.http.client.methods.HttpPost
import java.util.HashMap

class ForumHelper<T : androidx.recyclerview.widget.RecyclerView.Adapter<*>> : CommentLikeHelper(), View.OnClickListener {
    private val REQ_REQUEST = 401
    private val REQ_JOIN = 402
    private val REQ_LEAVE = 403
    private val REQ_CANCEL = 403
    var categoryId: Int = 0
    var videoList: MutableList<PageVo>? = null
    var adapter: T? = null
    var categoryList: List<Category>? = null
    var result: PageResponse.Result? = null


    override fun onItemClicked(object1: Int?, screenType: Any, postion: Int): Boolean {

        try {
            when (object1) {
                Constant.Events.CLICKED_HEADER_IMAGE, Constant.Events.CLICKED_HEADER_TITLE -> goToProfileFragment(videoList!![postion].item!!.owner_id)
                Constant.Events.CLICKED_HEADER_LOCATION -> {
                    val la = videoList!![postion].item!!.locationObject
                    if (null != la && la.isJsonObject) {
                        val vo = Gson().fromJson(la, Locations::class.java)
                        if (vo.canShowMap()) {
                            val intent = Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?daddr=" + vo.lat + "," + vo.lng))
                            startActivity(intent)
                        }
                    }
                }
                Constant.Events.MUSIC_LIKE -> {

                    //if likeFollow setting enabled then also call follow api
                    if (videoList!![postion].item!!.hasToChangeFollowLike()) {
                        callLikeApi(REQ_FOLLOW, postion, Constant.URL_PAGE_FOLLOW, -2)
                    }
                    callLikeApi(REQ_LIKE, postion, Constant.URL_PAGE_LIKE, -1)
                }
                Constant.Events.MUSIC_FAVORITE -> callLikeApi(REQ_FAVORITE, postion, Constant.URL_PAGE_FAVORITE, -1)
                Constant.Events.MUSIC_ADD -> {

                    //if likeFollow setting enabled then also call like api
                    if (videoList!![postion].item!!.hasToChangeFollowLike()) {
                        callLikeApi(REQ_LIKE, postion, Constant.URL_PAGE_LIKE, -2)
                    }
                    callLikeApi(REQ_FOLLOW, postion, Constant.URL_PAGE_FOLLOW, -1)
                }
                Constant.Events.MUSIC_MAIN -> openViewPageFragment(videoList!![postion].item!!.page_id)
                Constant.Events.PAGE_SUGGESTION_MAIN -> openViewPageFragment(postion)
                Constant.Events.CATEGORY -> if (categoryId != postion)
                //do not open same category again
                    openViewPageCategoryFragment(postion, "" + screenType)

                Constant.Events.FEED_UPDATE_OPTION -> {

                    //get clicked option
                    val opt = videoList!![Integer.parseInt("" + screenType)].item!!.buttons[postion]

                    //open share dialog if share clicked
                    when (opt.name) {
                        Constant.OptionType.SHARE -> showShareDialog(videoList!![Integer.parseInt("" + screenType)].item!!.share)
                        Constant.OptionType.DELETE -> showDeleteDialog(Integer.parseInt("" + screenType))
                        Constant.TabOption.MAKE_PAYMENT -> openWebView(opt.value, opt.label)
                        Constant.OptionType.CONTACT -> super.openPageContactForm(videoList!![Integer.parseInt("" + screenType)].item!!.owner_id)
                        Constant.OptionType.JOIN_SMOOTHBOX, Constant.OptionType.LEAVE_SMOOTHBOX, Constant.OptionType.REQUEST, Constant.OptionType.CANCEL -> showJoinLeaveDialog(postion, Integer.parseInt("" + screenType))
                        else -> {
                            //check if user has permissoion to view details
                            val openLoginForm = videoList!![Integer.parseInt("" + screenType)].item!!.isShowLoginForm

                            if (openLoginForm) {
                                //open sign-in screen
                                goToWelcome(1)
                            } else {
                                val ownerId = videoList!![Integer.parseInt("" + screenType)].item!!.owner_id
                                performOptionClick(postion, ownerId, Integer.parseInt("" + screenType))
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

        return super.onItemClicked(object1, screenType, postion)
    }

    private fun showJoinLeaveDialog(optionPosition: Int, position: Int) {

        //in case of public user ,send him to sign-in screen
        if (!SPref.getInstance().isLoggedIn(context)) {
            goToWelcome(1)
            return
        }


        val opt = videoList!![position].item!!.buttons[optionPosition]
        var dialogMsg = Constant.EMPTY
        var buttonTxt = Constant.EMPTY
        val url = arrayOf(Constant.EMPTY)
        val req = intArrayOf(0)
        when (opt.name) {
            Constant.OptionType.JOIN_SMOOTHBOX -> {
                dialogMsg = getStrings(R.string.msg_join_page)
                buttonTxt = getStrings(R.string.join_page)
                url[0] = Constant.URL_PAGE_JOIN
                req[0] = REQ_JOIN
            }
            Constant.OptionType.LEAVE_SMOOTHBOX -> {
                dialogMsg = getStrings(R.string.msg_leave_page)
                buttonTxt = getStrings(R.string.leave_page)
                url[0] = Constant.URL_PAGE_LEAVE
                req[0] = REQ_LEAVE
            }
            Constant.OptionType.REQUEST -> {
                dialogMsg = getStrings(R.string.msg_request_membership_page)
                buttonTxt = getStrings(R.string.send_request)
                url[0] = Constant.URL_PAGE_JOIN
                req[0] = REQ_REQUEST
            }
            Constant.OptionType.CANCEL -> {
                dialogMsg = getStrings(R.string.msg_request_cancel_page)
                buttonTxt = getStrings(R.string.cancel_request)
                url[0] = Constant.URL_PAGE_CANCEL_MEMBER
                req[0] = REQ_CANCEL
            }
        }

        try {
            if (null != progressDialog && progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            progressDialog = ProgressDialog.show(context, "", "", true)
            progressDialog.setCanceledOnTouchOutside(true)
            progressDialog.setCancelable(true)
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog.setContentView(R.layout.dialog_message_two)
            ThemeManager().applyTheme(progressDialog.findViewById<View>(R.id.rlDialogMain) as ViewGroup, context)
            val tvMsg = progressDialog.findViewById<View>(R.id.tvDialogText) as TextView
            tvMsg.text = dialogMsg

            val bCamera = progressDialog.findViewById<AppCompatButton>(R.id.bCamera)
            bCamera.text = buttonTxt
            val bGallary = progressDialog.findViewById<AppCompatButton>(R.id.bGallary)
            bGallary.text = getStrings(R.string.CANCEL)

            progressDialog.findViewById<View>(R.id.bCamera).setOnClickListener {
                progressDialog.dismiss()
                callLikeApi(req[0], position, url[0], optionPosition)
            }

            progressDialog.findViewById<View>(R.id.bGallary).setOnClickListener { progressDialog.dismiss() }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    fun performOptionClick(optionPosition: Int, ownerId: Int, position: Int) {
        try {
            val opt = videoList!![position].item!!.buttons[optionPosition]
            when (opt.name) {
                Constant.OptionType.MAIL -> ShareCompat.IntentBuilder.from(activity)
                        .setType("message/rfc822")
                        .addEmailTo(opt.value)
                        .setSubject("")
                        .setText("")
                        //.setHtmlText(body) //If you are using HTML in your body text
                        .setChooserTitle(opt.label)
                        .startChooser()
                Constant.OptionType.WEBSITE -> {
                    var url = opt.value
                    if (!TextUtils.isEmpty(url)) {
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://$url"
                        openWebView(url, opt.value)
                    } else {
                        Util.showSnackbar(v, getStrings(R.string.invalid_url))
                    }
                }
                /*  case Constant.OptionType.SHARE:
                    showShareDialog(opt.getValue(), "");
                    break;*/
                Constant.OptionType.PHONE -> {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + opt.value))
                    startActivity(intent)
                }
                Constant.OptionType.CONTACT -> super.openPageContactForm(ownerId)
                /*  case Constant.OptionType.DELETE:
                    callLikeApi(REQ_DELETE, position, Constant.URL_PAGE_DELETE, videoList.get(position).getItem());
                    break;*/
                Constant.OptionType.EDIT -> {
                    val map = HashMap<String, Any>()
                    map[Constant.KEY_PAGE_ID] = videoList!![position].item!!.page_id
                    fragmentManager.beginTransaction()
                            .replace(R.id.container,
                                    CreateEditPageFragment.newInstance(Constant.FormType.EDIT_PAGE, map, Constant.URL_PAGE_EDIT, null,true))
                            .addToBackStack(null)
                            .commit()
                }

                else -> CustomLog.e("option_name", "" + opt.name)
            }


        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    fun showDeleteDialog(position: Int) {
        try {
            if (null != progressDialog && progressDialog.isShowing) {
                progressDialog.dismiss()
            }
            progressDialog = ProgressDialog.show(context, "", "", true)
            progressDialog.setCanceledOnTouchOutside(true)
            progressDialog.setCancelable(true)
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog.setContentView(R.layout.dialog_message_two)
            ThemeManager().applyTheme(progressDialog.findViewById<View>(R.id.rlDialogMain) as ViewGroup, context)
            val tvMsg = progressDialog.findViewById<View>(R.id.tvDialogText) as TextView
            tvMsg.text = getStrings(R.string.MSG_DELETE_CONFIRMATION_PAGE)

            val bCamera = progressDialog.findViewById<AppCompatButton>(R.id.bCamera)
            bCamera.text = Constant.YES
            val bGallary = progressDialog.findViewById<AppCompatButton>(R.id.bGallary)
            bGallary.text = Constant.NO

            progressDialog.findViewById<View>(R.id.bCamera).setOnClickListener { v ->
                progressDialog.dismiss()
                callLikeApi(REQ_DELETE, position, Constant.URL_PAGE_DELETE, -1)

            }

            progressDialog.findViewById<View>(R.id.bGallary).setOnClickListener { v -> progressDialog.dismiss() }
        } catch (e: Exception) {
            CustomLog.e(e)
        }

    }

    override fun onClick(v: View) {}

    private fun callLikeApi(REQ_CODE: Int, position: Int, url: String, optionPosition: Int) {


        if (isNetworkAvailable(context)) {
            val vo = videoList!![position].item
            if (REQ_CODE >= REQ_DELETE) {/* >= means join,leave,request and delete*/
                showBaseLoader(false)
            } else {
                //update icon and show animation
                updateItemLikeFavorite(REQ_CODE, position, vo, -2 != optionPosition)
            }
            try {

                val request = HttpRequestVO(url)

                request.params[Constant.KEY_ID] = vo!!.page_id
                request.params[Constant.KEY_PAGE_ID] = vo.page_id
                request.params[Constant.KEY_TYPE] = Constant.ResourceType.PAGE
                request.headres[Constant.KEY_COOKIE] = cookie
                request.params[Constant.KEY_AUTH_TOKEN] = SPref.getInstance().getToken(context)
                request.requestMethod = HttpPost.METHOD_NAME
                val callback = Handler.Callback { msg ->
                    hideBaseLoader()
                    try {
                        val response = msg.obj as String

                        CustomLog.e("repsonse1", "" + response)
                        if (response != null) {
                            val err = Gson().fromJson(response, ErrorResponse::class.java)
                            if (TextUtils.isEmpty(err.error)) {
                                if (REQ_CODE == REQ_DELETE) {
                                    videoList!!.removeAt(position)
                                    try {
                                        adapter!!.notifyItemRemoved(position)
                                        adapter!!.notifyItemRangeChanged(position, videoList!!.size)
                                    } catch (e: Exception) {
                                        /*update all items in case of any animation*/
                                        adapter!!.notifyDataSetChanged()
                                    }

                                } else if (REQ_CODE > REQ_DELETE) {
                                    /*> means join,leave,request*/
                                    // JSONArray obj = new JSONObject(response).getJSONObject("result").getJSONArray("menus");
                                    val opt = Gson().fromJson(response, PageResponse::class.java)
                                    videoList!![position].item!!.buttons = opt.result.menus
                                    adapter!!.notifyItemChanged(position)
                                    Util.showSnackbar(v, opt.result.message)
                                }
                                /*if (REQ_CODE == REQ_LIKE) {
                                    videoList.get(position).setContentLike(!vo.isContentLike());
                                } else if (REQ_CODE == REQ_FAVORITE) {
                                    videoList.get(position).setContentFavourite(!vo.isContentFavourite());
                                }
                                adapter.notifyItemChanged(position);*/
                            } else {
                                //revert changes in case of error
                                updateItemLikeFavorite(REQ_CODE, position, vo, false)
                                Util.showSnackbar(v, err.errorMessage)
                            }
                        }
                    } catch (e: Exception) {
                        hideBaseLoader()
                        CustomLog.e(e)
                        Util.showSnackbar(v, getStrings(R.string.msg_something_wrong))
                    }

                    // dialog.dismiss();
                    true
                }
                HttpRequestHandler(activity, Handler(callback)).run(request)

            } catch (e: Exception) {
                hideBaseLoader()
            }

        } else {
            notInternetMsg(v)
        }
    }

    fun updateItemLikeFavorite(REQ_CODE: Int, position: Int, vo: PageContent?, showAnimation: Boolean) {

        if (REQ_CODE == REQ_LIKE) {
            videoList!![position].item!!.isShowAnimation = if (showAnimation) 1 else 0
            videoList!![position].item!!.isContentLike = !vo!!.isContentLike
            adapter!!.notifyItemChanged(position)
        } else if (REQ_CODE == REQ_FAVORITE) {
            videoList!![position].item!!.isShowAnimation = if (showAnimation) 2 else 0
            videoList!![position].item!!.isContentFavourite = !vo!!.isContentFavourite
            adapter!!.notifyItemChanged(position)
        } else if (REQ_CODE == REQ_FOLLOW) {
            videoList!![position].item!!.isShowAnimation = if (showAnimation) 3 else 0
            videoList!![position].item!!.isContentFollow = !vo!!.isContentFollow
            adapter!!.notifyItemChanged(position)
        }

    }


    fun getDetail(album: Albums): String {
        var detail = ""
        detail += ("\uf164 " + album.likeCount// + (album.getLikeCount() != 1 ? " Likes" : " Like")

                + "  \uf075 " + album.commentCount //+ (album.getCommentCount() != 1 ? " Comments" : " Comment")

                + "  \uf004 " + album.favouriteCount //+ (album.getFavouriteCount() != 1 ? " Favorites" : " Favorite")

                + "  \uf06e " + album.viewCount)// + (album.getViewCount() != 1 ? " Views" : " View");
        //+ "  \uf03e " + album.getPhotoCount();// + (album.getSongCount() > 1 ? " Songs" : " Song");

        return detail
    }

    companion object {

        private val REQ_LIKE = 100
        private val REQ_FAVORITE = 200
        private val REQ_FOLLOW = 300
        private val REQ_DELETE = 400
    }


    /*private void callDeleteApi(final int position) {

        try {
            if (isNetworkAvailable(context)) {
                videoList.remove(position);
                adapter.notifyItemRemoved(position);


                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_EVENT + videoList.get(position).getEventId() + Constant.POST_URL);
                    request.params.put(Constant.KEY_EVENT_ID, videoList.get(position).getEventId());
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        Util.showSnackbar(v, new JSONObject(response).getString("result"));
                                       *//* if (REQ == VIEW_BLOG_DELETE) {
                                            onBackPressed();
                                        } else {
                                            videoList.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            Util.showSnackbar(v, new JSONObject(response).getString("result"));
                                        }*//*
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }

                            } catch (Exception e) {
                                hideBaseLoader();
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }
    }*/


}


