package com.sesolutions.ui.live

import android.content.Context
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sesolutions.R
import com.sesolutions.responses.comment.CommentData
import com.sesolutions.utils.Util
import de.hdodenhof.circleimageview.CircleImageView
import org.apache.commons.lang.StringEscapeUtils

import java.util.ArrayList

class MessageAdapter(private val mContext: Context, private val mMsg: ArrayList<CommentData>) : androidx.recyclerview.widget.RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.msg_content, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = mMsg[position]

        Util.showImageWithGlide(holder.image, msg.userImage, mContext)

        if (msg.body == "user_Joined") {
            holder.cvComment.visibility = View.GONE
            holder.tvJoined.visibility = View.VISIBLE
            holder.tvJoined.text = msg.userTitle + "  joined"
        } else {
            holder.tvUserName.text = msg.userTitle
            try {
                val fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(msg.body)
                holder.tvBodyComment.text = fromServerUnicodeDecoded
            }catch (ex:Exception){
                ex.printStackTrace()
            }
         }
    }

    override fun getItemCount(): Int {
        return mMsg.size
    }

    override fun getItemId(position: Int): Long {
        return mMsg[position].hashCode().toLong()
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        internal var image: CircleImageView
        internal var tvUserName: TextView
        internal var tvBodyComment: TextView
        internal var tvJoined: TextView
        internal var cvComment: androidx.cardview.widget.CardView

        init {
            cvComment = itemView.findViewById(R.id.cvComment)
            image = itemView.findViewById(R.id.ivUserImage)
            tvUserName = itemView.findViewById(R.id.tvUserName)
            tvBodyComment = itemView.findViewById(R.id.tvBodyComment)
            tvJoined = itemView.findViewById(R.id.tvJoined)
        }
    }
}
