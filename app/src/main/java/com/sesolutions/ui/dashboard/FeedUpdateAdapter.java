package com.sesolutions.ui.dashboard;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.List;


public class FeedUpdateAdapter extends RecyclerView.Adapter<FeedUpdateAdapter.ContactHolder> {

    private final List<Options> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    private final int text1;
    private final int foregroundColor;
    // private final int text2;
    // private final int foregroundColor;
    // private final ThemeManager thememanager;
    // private final OnLoadMoreListener loadListener;
    private int activityPosition;


    public FeedUpdateAdapter(List<Options> list, Context cntxt, OnUserClickedListener<Integer, Object> listener, OnLoadMoreListener loadListener) {
        this.list = list;
        this.context = cntxt;
        this.listener = listener;
        text1 = Color.parseColor(Constant.text_color_1);
        foregroundColor = Color.parseColor(Constant.foregroundColor);
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed_update_option, parent, false);
        return new ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ContactHolder holder, int position) {

        try {
            final Options vo = list.get(position);
            holder.cvMain.setCardBackgroundColor(foregroundColor);
            holder.tvFeedText.setText(TextUtils.isEmpty(vo.getLabel()) ? vo.getValue() : vo.getLabel());
            holder.tvFeedText.setTextColor(text1);
            holder.ivIcon.setImageDrawable(ContextCompat.getDrawable(context, getDrawableByName(vo.getName(), vo)));
            holder.ivIcon.setColorFilter(text1);
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.FEED_UPDATE_OPTION, "" + activityPosition, holder.getAdapterPosition()));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //method returns image drawable id as per option key
    private int getDrawableByName(String name, Options opt) {
        int id;
        switch (name) {
            case Constant.OptionType.SAVE:
            case Constant.OptionType.UNSAVE:
                return R.drawable.option_save;

            case Constant.OptionType.DISABLE_COMMENT:
            case Constant.OptionType.ENABLE_COMMENT:
                return R.drawable.option_comment;

            case Constant.OptionType.REMOVE:
            case "removemember":
                return R.drawable.remove;
            case "ad_useful":
                return R.drawable.star_unfilled;
            case "hide_ad":
                return R.drawable.option_hide_feed;
            case Constant.OptionType.CLOSE:
                if (opt.getClose() == 1)
                    return R.drawable.lock;
                else if (opt.getClose() == 0)
                    return R.drawable.open_lock;
            case Constant.OptionType.RENAME:
                return R.drawable.rename;
            case Constant.OptionType.MOVE:
                return R.drawable.move;
            case Constant.OptionType.STICKY:
                if (opt.getSticky() == 1)
                    return R.drawable.make_sticky;
                else if (opt.getSticky() == 0)
                    return R.drawable.remove_sticky;
            case Constant.OptionType.REPUTATION:
                return R.drawable.trophy;
            case "close":
                if(opt.getClosed()==1) {
                    return R.drawable.open_lock;
                }
                else{
                    return R.drawable.lock;
                }
           /* case Constant.OptionType.PHONE:
                id = R.drawable.page_phone;
                break;
            case Constant.OptionType.MAIL:
                id = R.drawable.envelope;
                break;
            case Constant.OptionType.CATEGORY:
                id = R.drawable.page_category;
                break;
            case Constant.OptionType.TAG:
                id = R.drawable.info_tag;
                break;
            case Constant.OptionType.POST_REPLY:
                id = R.drawable.reply;
                break;*/

            default:
                id = context.getResources().getIdentifier("option_" + name, "drawable", context.getPackageName());
                if (id <= 0) {
                    id = R.drawable.circle_holo;
                }
                break;

        }
        return id;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setActivityPosition(int activityPosition) {
        this.activityPosition = activityPosition;
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        TextView tvFeedText;
        ImageView ivIcon;
        CardView cvMain;


        ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvFeedText = itemView.findViewById(R.id.tvFeedText);
                ivIcon = itemView.findViewById(R.id.ivIcon);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}
