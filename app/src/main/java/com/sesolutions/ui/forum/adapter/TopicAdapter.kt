package com.sesolutions.ui.forum.adapter

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sesolutions.R
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.feed.Options
import com.sesolutions.responses.forum.Post
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.customviews.FeedOptionPopup
import com.sesolutions.ui.customviews.NestedWebView
import com.sesolutions.ui.customviews.RelativePopupWindow
import com.sesolutions.utils.*


class TopicAdapter(private val list: List<Post>,
                   private val context: Context,
                   private val listener: OnUserClickedListener<Int, Any>,
                   private val loadListener: OnLoadMoreListener) : RecyclerView.Adapter<TopicAdapter.CategoryHolder>() {
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



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {

        return CategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic_post, parent, false))

    }

    override fun onBindViewHolder(parentHolder: CategoryHolder, position: Int) {

        themeManager.applyTheme(parentHolder.itemView as ViewGroup, context)
        try {
            val post = list[position]
            parentHolder.tvOwnerTitle.text = post.owner_title

            if (!isUserLoggedIn) {
                parentHolder.llShare.visibility = View.GONE
                parentHolder.llQuote.visibility = View.GONE
                parentHolder.llLike.visibility = View.GONE
                parentHolder.llSayThank.visibility = View.GONE
                parentHolder.ivPostOption.visibility = View.GONE
            } else {

                if (post.isShowAnimation == 1) {
                    post.isShowAnimation = 0

                    parentHolder.ivLike.setImageDrawable(if (post.isContentLike()) dLikeSelected else dLike)
                    parentHolder.tvLike.text = if (post.isContentLike()) "  UnLike  " else "  Like  "
                } else {
                    parentHolder.ivLike.setImageDrawable(if (post.isContentLike()) dLikeSelected else dLike)
                    parentHolder.tvLike.text = if (post.isContentLike()) "  UnLike  " else "  Like  "
                }

                parentHolder.llQuote.visibility = if (post.canPost()) View.VISIBLE else View.GONE
                parentHolder.ivPostOption.visibility = if (post.options.size > 0) View.VISIBLE else View.GONE

                parentHolder.llQuote.setOnClickListener { v -> listener.onItemClicked(Constant.Events.POST_QUOTE, "", parentHolder.adapterPosition) }
                parentHolder.llSayThank.setOnClickListener { v -> listener.onItemClicked(Constant.Events.MUSIC_FAVORITE, post, parentHolder.adapterPosition) }
                parentHolder.llLike.setOnClickListener { v -> listener.onItemClicked(Constant.Events.MUSIC_LIKE, post, parentHolder.adapterPosition) }
                parentHolder.ivPostOption.setOnClickListener { v -> showOptionsPopUp(parentHolder.ivPostOption, parentHolder.adapterPosition, post.options) }
            }

            if (null != post.moderator_label) {
                parentHolder.tvModerator.visibility = View.VISIBLE
                parentHolder.tvModerator.text = post.moderator_label
            }


            parentHolder.tvBody.getSettings().setJavaScriptEnabled(true)
            parentHolder.tvBody.getSettings().setBuiltInZoomControls(false)
            parentHolder.tvBody.getSettings().setSupportZoom(false)
            parentHolder.tvBody.getSettings().setPluginState(WebSettings.PluginState.ON)
            parentHolder.tvBody.getSettings().setAllowFileAccess(true)
            parentHolder.tvBody.getSettings().setDomStorageEnabled(true)
            // for supporting strip payment checkout
            // for supporting strip payment checkout
            parentHolder.tvBody.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/28.0.0.20.16;]")

            if (Build.VERSION.SDK_INT >= 19) {
                parentHolder.tvBody.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            } else if (Build.VERSION.SDK_INT >= 16 /*&& Build.VERSION.SDK_INT < 19*/) {
                parentHolder.tvBody.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }


            if (!TextUtils.isEmpty(post.body)) {
                parentHolder.tvBody.visibility = View.VISIBLE
                if (!TextUtils.isEmpty(post.body)) {
                    parentHolder.tvBody.visibility = View.VISIBLE
                    parentHolder.tvBody.loadData(post.body, "text/html", "utf-8")
                } else {
                    parentHolder.tvBody.visibility = View.GONE
                }
            } else {
                parentHolder.tvBody.visibility = View.GONE
            }


            if (null != post.signature)
                parentHolder.tvSignature.loadData(post.signature, "text/html", "utf-8")
            else
                parentHolder.llSignature.visibility = View.GONE

            parentHolder.tvCreatedOn.text = Util.changeDateFormat(context, post.creation_date)
            parentHolder.tvStats.typeface = iconFont


            val detail = ((if (null != post.thanks) "  \uf118 " + post.thanks_count else "")
                    + (if (null != post.reputations) "  \uf091 " + post.reputations else "")
                    + "  \uf164 " + post.like_count
                    + "  \uf075 " + post.post_count)
            parentHolder.tvStats.text = detail
            parentHolder.llSayThank.visibility = if (post.canThank()) View.VISIBLE else View.GONE

            parentHolder.llShare.setOnClickListener { listener.onItemClicked(Constant.Events.SHARE_FEED, post, parentHolder.adapterPosition) }

            Util.showImageWithGlide(parentHolder.ivImage, post.owner_images, context, R.drawable.placeholder_square)
            parentHolder.ivImage.setOnClickListener{ listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, "", post.user_id) }
            parentHolder.tvOwnerTitle.setOnClickListener{ listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, "", post.user_id) }

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
        var tvLike: TextView = itemView.findViewById(R.id.tvLike)
        var tvCreatedOn: TextView = itemView.findViewById(R.id.tvCreatedOn)
        var tvStats: TextView = itemView.findViewById(R.id.tvStats)
        var ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        var ivLike: ImageView = itemView.findViewById(R.id.ivLike)
        var ivFavorite: ImageView? = null
        var ivPostOption: ImageView = itemView.findViewById(R.id.ivPostOption)

        var llShare: View = itemView.findViewById(R.id.llShare)
        var llSayThank: View = itemView.findViewById(R.id.llSayThank)
        var llLike: View = itemView.findViewById(R.id.llLike)
        var llQuote: View = itemView.findViewById(R.id.llQuote)
        var tvSignature: NestedWebView = itemView.findViewById(R.id.tvSignature)
        var llSignature: LinearLayout = itemView.findViewById(R.id.llSignature)

    }


}
