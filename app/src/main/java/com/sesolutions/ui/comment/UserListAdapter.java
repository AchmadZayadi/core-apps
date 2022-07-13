package com.sesolutions.ui.comment;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sesolutions.R;
import com.sesolutions.responses.Friends;
import com.sesolutions.utils.Util;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private List<Friends> userList;
    private onItemClickListener onClickListener;

    public UserListAdapter(List<Friends> userList, onItemClickListener onClickListener) {
        this.userList = userList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_user_list_popup, viewGroup, false);
        return new ViewHolder(view);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

       final Friends user = userList.get(i);
        viewHolder.tvFirstName.setText(user.getLabel());
      //  binding.tvUserName.setText("@".concat(user.getPhantomId()));
        Util.showImageWithGlide123(viewHolder.civUser, user.getPhoto(),  R.drawable.placeholder_3_2);

        viewHolder.rlRow.setOnClickListener(v -> onClickListener.onItemClick(v, viewHolder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        if (userList != null)
            return userList.size();
        else
            return 0;
    }

    public interface onItemClickListener {
        void onItemClick(View v, int position);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFirstName;
        ImageView civUser;
        ConstraintLayout rlRow;

        public ViewHolder(View itemView) {
            super(itemView);
            tvFirstName=itemView.findViewById(R.id.tvFirstName);
            civUser=itemView.findViewById(R.id.civUser);
            rlRow=itemView.findViewById(R.id.rlRow);
        }

    }

}
