package com.sesolutions.ui.core_forum.adapters

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sesolutions.R
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.feed.Options
import com.sesolutions.responses.forum.Post
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.customviews.FeedOptionPopup
import com.sesolutions.ui.customviews.RelativePopupWindow
import com.sesolutions.utils.*
import org.apache.commons.lang.StringEscapeUtils
import java.net.URI
import java.net.URISyntaxException

class CoreTopicAdapter(private val list: List<Post>,
                       private val context: Context,
                       private val listener: OnUserClickedListener<Int, Any>,
                       private val loadListener: OnLoadMoreListener) : androidx.recyclerview.widget.RecyclerView.Adapter<CoreTopicAdapter.CategoryHolder>() {
    private val iconFont: Typeface = FontManager.getTypeface(context, FontManager.FONTAWESOME)
    val VT_GRID_VIEW = "-4"
    val VT_CATEGORIES = "-3"
    val VT_CATEGORY = "-2"
    val VT_SUGGESTION = "-1"
    private val themeManager: ThemeManager = ThemeManager()
    private val isUserLoggedIn: Boolean = SPref.getInstance().isLoggedIn(context)
    private val dLike: Drawable? = ContextCompat.getDrawable(context, R.drawable.like_quote)
    private val dLikeSelected: Drawable? = ContextCompat.getDrawable(context, R.drawable.music_like_selected)

    override fun onViewAttachedToWindow(holder: CategoryHolder) {
        super.onViewAttachedToWindow(holder)
        if (list.size - 1 == holder.adapterPosition) {
            loadListener.onLoadMore()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {

        return CategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_core_topic_post, parent, false))

    }

    override fun onBindViewHolder(parentHolder: CategoryHolder, position: Int) {

        themeManager.applyTheme(parentHolder.itemView as ViewGroup, context)

        try {
            val post = list[position]
            parentHolder.tvOwnerTitle.text = post.owner_title

            if (!isUserLoggedIn) {
//                parentHolder.llShare.visibility = View.GONE
                parentHolder.llQuote.visibility = View.GONE
                parentHolder.ivPostOption.visibility = View.GONE
            } else {

//                if (post.isShowAnimation == 1) {
//                    post.isShowAnimation = 0
//                    parentHolder.sbvLike.likeAnimation()
//                    parentHolder.ivLike.setImageDrawable(if (post.isContentLike()) dLikeSelected else dLike)
//                    parentHolder.tvLike.text = if (post.isContentLike()) "  UnLike  " else "  Like  "
//                } else {
//                    parentHolder.ivLike.setImageDrawable(if (post.isContentLike()) dLikeSelected else dLike)
//                    parentHolder.tvLike.text = if (post.isContentLike()) "  UnLike  " else "  Like  "
//                }

                parentHolder.llQuote.visibility = if (post.canPost()) View.VISIBLE else View.GONE
                parentHolder.ivPostOption.visibility = if (post.options.size > 0) View.VISIBLE else View.GONE

                parentHolder.llQuote.setOnClickListener { v -> listener.onItemClicked(Constant.Events.POST_QUOTE, "", parentHolder.adapterPosition) }
                parentHolder.ivPostOption.setOnClickListener { v -> showOptionsPopUp(parentHolder.ivPostOption, parentHolder.adapterPosition, post.options) }
            }
            if (null != post.moderator_label) {
                parentHolder.tvModerator.visibility = View.VISIBLE
                parentHolder.tvModerator.text = post.moderator_label
            }

            if (!TextUtils.isEmpty(post.body)) {
                parentHolder.tvBody.visibility = View.VISIBLE
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                    parentHolder.tvBody.text = Html.fromHtml(post.body, Html.FROM_HTML_MODE_LEGACY)
//                } else {
//                    parentHolder.tvBody.text = Html.fromHtml(post.body)
//                }
//                parentHolder.tvBody.movementMethod = LinkMovementMethod.getInstance()

                val fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(post.body)
                parentHolder.tvBody.loadDataWithBaseURL(Constant.BASE_URL, fromServerUnicodeDecoded, "text/html", "UTF-8", null)

                parentHolder.tvBody.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

                        CustomLog.e("Profile_link", url)
                        if (url.contains("profile")) {
                            try {
                                val uri = URI(url)
                                val path = uri.path
                                val idStr = path.substring(path.lastIndexOf('/') + 1)
                                listener.onItemClicked(Constant.Events.USER_SELECT, ""+idStr, -1)
                            } catch (e: URISyntaxException) {
                                e.printStackTrace()
                            } catch (e: Exception) {
                                CustomLog.e(e)
                            }
                        } else
                            listener.onItemClicked(Constant.Events.WEBVIEW, url, -1)
                        return true
                    }
                }

            } else {
                parentHolder.tvBody.visibility = View.GONE
            }

            parentHolder.tvCreatedOn.text = Util.changeDateFormat(context, post.creation_date)
            parentHolder.tvUserPostcount.text = if (post.post_count == 0 || post.post_count == 1) {
                "" + post.post_count + " post"
            } else {
                "" + post.post_count + " posts"
            }
//            parentHolder.tvStats.typeface = iconFont


            val detail = ((if (null != post.thanks) "  \uf118 " + post.thanks_count else "")
                    + (if (null != post.reputations) "  \uf091 " + post.reputations else "")
                    + "  \uf164 " + post.like_count
                    + "  \uf075 " + post.post_count)
//            parentHolder.tvStats.text = detail
//            parentHolder.llShare.setOnClickListener { listener.onItemClicked(Constant.Events.SHARE_FEED, post, parentHolder.adapterPosition) }

            Util.showImageWithGlide(parentHolder.ivImage, post.owner_images, context, R.drawable.placeholder_square)
            parentHolder.ivImage.setOnClickListener { listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, "", post.user_id) }

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun showOptionsPopUp(v: View, position: Int, options: List<Options>) {
        try {
            val popup = FeedOptionPopup(v.context, position, listener, options)
            // popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            val vertPos = RelativePopupWindow.VerticalPosition.CENTER
            val horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_LEFT
            popup.showOnAnchor(v, vertPos, horizPos, true)
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class CategoryHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var cvMain: View = itemView.findViewById(R.id.cvMain)
        var tvOwnerTitle: TextView = itemView.findViewById(R.id.tvOwnerTitle)
        var tvModerator: TextView = itemView.findViewById(R.id.tvModerator)
        var tvBody: WebView = itemView.findViewById(R.id.tvBody)
        //        var tvLike: TextView = itemView.findViewById(R.id.tvLike)
        var tvCreatedOn: TextView = itemView.findViewById(R.id.tvCreatedOn)
        var tvUserPostcount: TextView = itemView.findViewById(R.id.tvUserPosts)
        //        var tvStats: TextView = itemView.findViewById(R.id.tvStats)
        var ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        //        var ivLike: ImageView = itemView.findViewById(R.id.ivLike)
//        var ivFavorite: ImageView? = null
        var ivPostOption: ImageView = itemView.findViewById(R.id.ivPostOption)
        //        var sbvFavorite: SmallBangView? = null
//        var sbvLike: SmallBangView = itemView.findViewById(R.id.sbvLike)
//        var llShare: View = itemView.findViewById(R.id.llShare)
        var llQuote: View = itemView.findViewById(R.id.llQuote)

    }

}
