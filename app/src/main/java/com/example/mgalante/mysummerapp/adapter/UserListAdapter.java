package com.example.mgalante.mysummerapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mgalante on 13/04/17.
 */

public class UserListAdapter extends FirebaseRecyclerAdapter<User, UserListAdapter.MyUserViewHolder> {

    private ClickListenerChatFirebase mClickListenerChatFirebase;
    private Context mContext;

    public UserListAdapter(Context context, DatabaseReference ref, ClickListenerChatFirebase mClickListenerChatFirebase) {
        super(User.class, R.layout.user_calculator_resource, UserListAdapter.MyUserViewHolder.class, ref);
        this.mContext = context;
        this.mClickListenerChatFirebase = mClickListenerChatFirebase;
    }

    @Override
    public UserListAdapter.MyUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_calculator_resource, parent, false);
        return new MyUserViewHolder(view);
    }

    @Override
    protected void populateViewHolder(UserListAdapter.MyUserViewHolder viewHolder, User model, int position) {

        viewHolder.setUserMoney(model.getName());
        //TODO check if photo is stored in cache
        viewHolder.setUserPhoto(model.getPhotoUrl());
    }

    public class MyUserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView mUserPhoto;
        TextView tvUserMoney;

        public MyUserViewHolder(View itemView) {
            super(itemView);
            mUserPhoto = (CircleImageView) itemView.findViewById(R.id.user_avatar);
            tvUserMoney = (TextView) itemView.findViewById(R.id.user_money);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            User user = getItem(position);
            mClickListenerChatFirebase.clickUserDetail(v, position, user);
        }

        public void setUserPhoto(String url) {
            if (mUserPhoto != null) {
                Glide.with(mUserPhoto.getContext()).load(url)
                        .into(mUserPhoto);
                mUserPhoto.setOnClickListener(this);
            }
        }

        public void setUserMoney(String amount) {
            tvUserMoney.setText(amount);
        }
    }

}
