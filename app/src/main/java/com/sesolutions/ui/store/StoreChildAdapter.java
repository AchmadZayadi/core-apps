package com.sesolutions.ui.store;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.responses.store.StoreContent;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.List;

public class StoreChildAdapter extends RecyclerView.Adapter<StoreChildAdapter.ContactHolder> {

    private final List<StoreContent> list;
    private final Context context;
    private final OnUserClickedListener<Integer, Object> listener;
    //   private final OnLoadMoreListener loadListener;
    private final ThemeManager themeManager;
    private final String TXT_BY;
    private final String TXT_IN;
    private final boolean isGrid;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    private Permission permission;

    public StoreChildAdapter(List<StoreContent> list, Context cntxt, OnUserClickedListener<Integer, Object> listenr, boolean isGrid) {
        this.list = list;
        this.context = cntxt;
        this.listener = listenr;
        this.isGrid = isGrid;
        themeManager = new ThemeManager();
        TXT_BY = context.getResources().getString(R.string.TXT_BY);
        TXT_IN = context.getResources().getString(R.string.IN_);
    }

    @NonNull
    @Override
    public StoreChildAdapter.ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(isGrid ? R.layout.item_store : R.layout.item_store_list_view, parent, false);
        return new StoreChildAdapter.ContactHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final StoreChildAdapter.ContactHolder holder, int position) {

        try {
            themeManager.applyTheme((ViewGroup) holder.itemView, context);
            final StoreContent vo = list.get(position);

            holder.ivVerified.setVisibility(vo.getVerified() == 1 ? View.VISIBLE : View.GONE);
            holder.tvTitle.setText(vo.getTitle());
            holder.tvArtist.setText(vo.getOwner_title());
            holder.tvArtist.setVisibility(null != vo.getOwner_title() ? View.VISIBLE : View.GONE);
//            holder.tvStoreDesc.setText(vo.getDescription());
//            holder.tvOff.setText(vo.getOfftheday());

            if (!TextUtils.isEmpty(vo.getDescription())) {
                holder.tvStoreDesc.setVisibility(View.VISIBLE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    holder.tvStoreDesc.setText(Html.fromHtml(vo.getDescription(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    holder.tvStoreDesc.setText(Html.fromHtml(vo.getDescription()));
                }
                holder.tvStoreDesc.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                holder.tvStoreDesc.setVisibility(View.GONE);
            }

            Util.showImageWithGlide(holder.ivImage, vo.getMainImageUrl(), context, R.drawable.placeholder_square);
            holder.tvCategoryName.setText(vo.getCategory_title());
            holder.ivOption.setVisibility(null != vo.getMenus() ? View.VISIBLE : View.GONE);
            holder.ivOption.setOnClickListener(v -> showOptionsPopUp(holder.ivOption, holder.getAdapterPosition(), vo.getMenus()));
            holder.cvMain.setOnClickListener(v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, holder, vo.getStoreId() /*holder.getAdapterPosition()*/));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ContactHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        protected TextView tvArtist, tvOff;
        protected ImageView ivImage;
        protected ImageView ivUser;

        protected TextView tvStoreDesc,tvType,tvCategoryName;
        protected View cvMain,vShadow;
        protected ImageView ivVerified,ivOption;


        public ContactHolder(View itemView) {
            super(itemView);
            try {
                cvMain = itemView.findViewById(R.id.cvMain);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvStoreDesc = itemView.findViewById(R.id.tvStoreDesc);
                tvArtist = itemView.findViewById(R.id.tvArtist);
//                tvOff = itemView.findViewById(R.id.tvOff);
                ivImage = itemView.findViewById(R.id.ivImage);
                tvType = itemView.findViewById(R.id.tvType);
                ivUser = itemView.findViewById(R.id.ivUser);
                vShadow = itemView.findViewById(R.id.vShadow);
                tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
                ivOption = itemView.findViewById(R.id.ivOption);
                ivVerified = itemView.findViewById(R.id.ivVerified);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
    private void showOptionsPopUp(View v, int position, List<Options> options) {
        try {
            FeedOptionPopup popup = new FeedOptionPopup(v.getContext(), position, listener, options);
            // popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            int vertPos = RelativePopupWindow.VerticalPosition.CENTER;
            int horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_LEFT;
            popup.showOnAnchor(v, vertPos, horizPos, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}
