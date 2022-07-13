package com.sesolutions.ui.message;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Attachments;
import com.sesolutions.responses.MessageInbox;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.himanshusoni.chatmessageview.ChatMessageView;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private final List<MessageInbox> list;
    private final Context context;
    private final OnUserClickedListener listener;
    private final ThemeManager themeManager;
    private final int colorPrimary;
    private final int greyLight;
    private int lastPosition;


    public ChatAdapter(Context cntxt, List<MessageInbox> list, OnUserClickedListener listner) {
        this.list = list;
        this.context = cntxt;
        this.lastPosition = -1;
        this.listener = listner;
        colorPrimary = Color.parseColor(Constant.colorPrimary);
        greyLight = Color.parseColor(Constant.grey_light);
        themeManager = new ThemeManager();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder viewHolder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) viewHolder.itemView, context);
            final MessageInbox vo = list.get(position);
            if (vo.getMine() > 0) {
                    /* this means your msg*/
                viewHolder.llMine.setVisibility(View.VISIBLE);
                viewHolder.llOther.setVisibility(View.GONE);
                viewHolder.tvMsgMine.setText(Html.fromHtml(vo.getBody()));
              //  viewHolder.tvMsgMine.setText(vo.getBody());
//                viewHolder.tvUserMine.setText(vo.getTitle());
                // viewHolder.cmMine.setBackgroundColors(colorPrimary, colorPrimary);

              //  viewHolder.tvTimeMine.setText(Util.changeDateFormat(context,vo.getDate()));
                viewHolder.tvTimeMine.setText(Util.changeDateFormat(context,vo.getDate()));
                Attachments attachments = vo.getAttachments();
                if (attachments != null) {
                    //CustomLog.e("attachments", "" + new Gson().toJson(attachments));
                    String type = attachments.getAttachmentType();
                    switch (type) {
                        case Constant.TYPE_IMAGE:
                            viewHolder.ivAttachemntMine.setVisibility(View.VISIBLE);
                            viewHolder.llLink.setVisibility(View.GONE);
                            viewHolder.llVideoMine.setVisibility(View.GONE);
                            Util.showImageWithGlide(viewHolder.ivAttachemntMine, attachments.getAttachmentPhoto(), context, R.drawable.placeholder_3_2);

                            // Glide.with(context).load(attachments.getAttachmentPhoto()).into(viewHolder.ivAttachemntMine);
                            break;
                        case Constant.TYPE_LINK:
                            viewHolder.ivAttachemntMine.setVisibility(View.GONE);
                            viewHolder.llLink.setVisibility(View.VISIBLE);
                            viewHolder.llVideoMine.setVisibility(View.GONE);
                            Util.showImageWithGlide(viewHolder.ivLinkImage, attachments.getAttachmentPhoto(), context, R.drawable.placeholder_3_2);

                            //  Glide.with(context).load(attachments.getAttachmentPhoto()).into(viewHolder.ivLinkImage);
                            viewHolder.tvLinkDesc.setText(attachments.getAttachmentDescription());
                            viewHolder.tvLinkTitle.setText(attachments.getAttachmentTitle());

                            break;
                        case Constant.TYPE_VIDEO:
                            viewHolder.ivAttachemntMine.setVisibility(View.GONE);
                            viewHolder.llLink.setVisibility(View.GONE);
                            viewHolder.llVideoMine.setVisibility(View.VISIBLE);

                            //Glide.with(context).load(attachments.getAttachmentPhoto()).into(viewHolder.ivVideoMine);
                            Util.showImageWithGlide(viewHolder.ivVideoMine, attachments.getAttachmentPhoto(), context, R.drawable.placeholder_3_2);

                            if (TextUtils.isEmpty(attachments.getAttachmentTitle())) {
                                viewHolder.tvVideoTitleMine.setVisibility(View.GONE);
                            } else {
                                viewHolder.tvVideoTitleMine.setVisibility(View.VISIBLE);
                                viewHolder.tvVideoTitleMine.setText(attachments.getAttachmentTitle());
                            }
                            break;
                    }

                } else {
                    viewHolder.ivAttachemntMine.setVisibility(View.GONE);
                    viewHolder.llLink.setVisibility(View.GONE);
                    viewHolder.llVideoMine.setVisibility(View.GONE);

                }

                viewHolder.cmMine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(null, null, position);
                    }
                });


            } else {
                        /*this means other user msg*/
                viewHolder.llMine.setVisibility(View.GONE);
                viewHolder.llOther.setVisibility(View.VISIBLE);
                viewHolder.tvMsgOther.setText(Html.fromHtml(vo.getBody()));
                //viewHolder.tvMsgOther.setText(vo.getBody());
//                viewHolder.tvUserOther.setText(vo.getTitle());
                //  viewHolder.cmOther.setBackgroundColors(colorPrimary, colorPrimary);
                viewHolder.tvTimeOther.setText(Util.changeDateFormat(context,vo.getDate()));
                Util.showImageWithGlide(viewHolder.ivImageOther, vo.getUserImage(), context, R.drawable.placeholder_3_2);
                // Glide.with(context).load(vo.getUserImage()).into(viewHolder.ivImageOther);
                Attachments attachments = vo.getAttachments();

                if (attachments != null) {
                    //CustomLog.e("attachments", "" + new Gson().toJson(attachments));
                    String type = attachments.getAttachmentType();
                    switch (type) {
                        case Constant.TYPE_IMAGE:
                            viewHolder.ivAttachemntOther.setVisibility(View.VISIBLE);
                            viewHolder.llLinkOther.setVisibility(View.GONE);
                            viewHolder.llVideoOther.setVisibility(View.GONE);
                            //Glide.with(context).load(attachments.getAttachmentPhoto()).into(viewHolder.ivAttachemntOther);
                            Util.showImageWithGlide(viewHolder.ivAttachemntOther, attachments.getAttachmentPhoto(), context, R.drawable.placeholder_3_2);

                            break;
                        case Constant.TYPE_LINK:
                            viewHolder.ivAttachemntOther.setVisibility(View.GONE);
                            viewHolder.llLinkOther.setVisibility(View.VISIBLE);
                            viewHolder.llVideoOther.setVisibility(View.GONE);

                            Util.showImageWithGlide(viewHolder.ivLinkImageOther, attachments.getAttachmentPhoto(), context, R.drawable.placeholder_3_2);

                            viewHolder.tvLinkDescOther.setText(attachments.getAttachmentDescription());
                            viewHolder.tvLinkTitleOther.setText(attachments.getAttachmentTitle());

                            break;
                        case Constant.TYPE_VIDEO:
                            viewHolder.ivAttachemntOther.setVisibility(View.GONE);
                            viewHolder.llLinkOther.setVisibility(View.GONE);
                            viewHolder.llVideoOther.setVisibility(View.VISIBLE);
                            Util.showImageWithGlide(viewHolder.ivVideoOther, attachments.getAttachmentPhoto(), context, R.drawable.placeholder_3_2);
                            viewHolder.tvVideoTitleOther.setText(attachments.getAttachmentTitle());
                            // viewHolder.tvVideoDescriptionOther.setText(attachments.getAttachmentDescription());
                            if (TextUtils.isEmpty(attachments.getAttachmentTitle())) {
                                viewHolder.tvVideoTitleOther.setVisibility(View.GONE);
                            } else {
                                viewHolder.tvVideoTitleOther.setVisibility(View.VISIBLE);
                                viewHolder.tvVideoTitleOther.setText(attachments.getAttachmentTitle());
                            }
                            break;
                    }

                } else {
                    viewHolder.ivAttachemntMine.setVisibility(View.GONE);
                    viewHolder.llLink.setVisibility(View.GONE);

                }
                viewHolder.cmOther.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClicked(null, null, position);
                    }
                });
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        //return 10;
        return list.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVideoTitleOther;
        private AppCompatImageView ivVideoOther;
        private LinearLayoutCompat llLinkOther;
        private TextView tvVideoTitleMine;
        private TextView tvVideoDescriptionMine;
        private TextView tvVideoDescriptionOther;
        private AppCompatImageView ivVideoMine;
        private LinearLayoutCompat llLink;
        private TextView tvMsgMine;
        private TextView tvMsgOther;
        private CircleImageView ivImageOther;
        private AppCompatImageView ivAttachemntOther;
        private AppCompatImageView ivAttachemntMine;
        private AppCompatImageView ivLinkImage;
        private AppCompatImageView ivLinkImageOther;
        private TextView tvTimeMine;
        private TextView tvTimeOther;
//        private TextView tvUserMine;
//        private TextView tvUserOther;
        private TextView tvLinkTitleOther;
        private TextView tvLinkDescOther;
        private TextView tvLinkTitle;
        private TextView tvLinkDesc;
        private LinearLayoutCompat llMine;
        private LinearLayoutCompat llOther;
        private LinearLayoutCompat llVideoMine;
        private LinearLayoutCompat llVideoOther;
        private LinearLayoutCompat llMain;
        private ChatMessageView cmOther;
        private ChatMessageView cmMine;

        public MessageViewHolder(View v) {
            super(v);
            tvMsgMine = (TextView) itemView.findViewById(R.id.tvMsgMine);
            tvMsgOther = (TextView) itemView.findViewById(R.id.tvMsgOther);
            ivImageOther = (CircleImageView) itemView.findViewById(R.id.ivImageOther);
            ivAttachemntOther = (AppCompatImageView) itemView.findViewById(R.id.ivAttachemntOther);
            ivAttachemntMine = itemView.findViewById(R.id.ivAttachemntMine);
            tvTimeMine = (TextView) itemView.findViewById(R.id.tvTimeMine);
            tvTimeOther = (TextView) itemView.findViewById(R.id.tvTimeOther);
//            tvUserMine = (TextView) itemView.findViewById(R.id.tvUserMine);
//            tvUserOther = (TextView) itemView.findViewById(R.id.tvUserOther);

            tvVideoTitleMine = (TextView) itemView.findViewById(R.id.tvVideoTitleMine);
            tvVideoDescriptionMine = (TextView) itemView.findViewById(R.id.tvVideoDescriptionMine);
            ivVideoMine = (AppCompatImageView) itemView.findViewById(R.id.ivVideoMine);

            tvVideoTitleOther = (TextView) itemView.findViewById(R.id.tvVideoTitleOther);
            tvVideoDescriptionOther = (TextView) itemView.findViewById(R.id.tvVideoDescriptionOther);
            ivVideoOther = (AppCompatImageView) itemView.findViewById(R.id.ivVideoOther);

            tvLinkDesc = (TextView) itemView.findViewById(R.id.tvLinkDesc);
            ivLinkImage = itemView.findViewById(R.id.ivLink);
            llLink = (LinearLayoutCompat) itemView.findViewById(R.id.llLink);
            tvLinkTitle = (TextView) itemView.findViewById(R.id.tvLinkTitle);

            tvLinkTitleOther = (TextView) itemView.findViewById(R.id.tvLinkTitleOther);
            tvLinkDescOther = (TextView) itemView.findViewById(R.id.tvLinkDescOther);
            ivLinkImageOther = itemView.findViewById(R.id.ivLinkOther);
            llLinkOther = (LinearLayoutCompat) itemView.findViewById(R.id.llLinkOther);


            llMine = (LinearLayoutCompat) itemView.findViewById(R.id.llMine);
            llOther = (LinearLayoutCompat) itemView.findViewById(R.id.llOther);
            llVideoMine = (LinearLayoutCompat) itemView.findViewById(R.id.llVideoMine);
            llVideoOther = (LinearLayoutCompat) itemView.findViewById(R.id.llVideoOther);
            llMain = (LinearLayoutCompat) itemView.findViewById(R.id.llMain);
            cmMine = (ChatMessageView) itemView.findViewById(R.id.cmMine);
            cmOther = (ChatMessageView) itemView.findViewById(R.id.cmOther);

        }
    }
}
