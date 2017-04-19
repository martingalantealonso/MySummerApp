package com.example.mgalante.mysummerapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.users.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mgalante on 13/04/17.
 */

public class UserListArrayAdapter extends RecyclerView.Adapter<UserListArrayAdapter.MyUserViewHolder> {

    Context mContext;
    ClickListenerChatFirebase mItemClickListener;
    List<User> users;

    public UserListArrayAdapter(Context mContext, ClickListenerChatFirebase mClickListenerChatFirebase, List<User> users) {
        this.mContext = mContext;
        this.mItemClickListener = mClickListenerChatFirebase;
        this.users = users;
    }

    @Override
    public MyUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_calculator_resource, parent, false);
        return new MyUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyUserViewHolder holder, final int position) {
        final User user = users.get(position);

        if (holder.mUserPhoto != null) {

            if (!String.valueOf(user.getPhotoUrl()).equals("") || !String.valueOf(user.getPhotoUrl()).equals("null")) {

                Glide.with(holder.mUserPhoto.getContext()).load(user.getPhotoUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        holder.mUserPhoto.setImageBitmap(resource);
                   /* Palette palete = Palette.from(resource).generate();
                    holder.mUserHolder.setBackgroundColor(palete.getDarkMutedColor(Color.DKGRAY));*/
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {

                    }
                });
            } else {
                holder.mUserPhoto.setImageResource(R.drawable.vd_user);
            }
        }

        // !String.valueOf(user.getPhotoUrl()).equals("") &&  !String.valueOf(user.getPhotoUrl()).equals("null")  ? String.valueOf(user.getPhotoUrl()) : Util.DEFAULT_NULL_IMAGE)
        if (String.valueOf(user.getName()).equals("null")) {
            holder.tvUserName.setText(mContext.getString(R.string.not_found_user_name));
        } else if (!String.valueOf(user.getName()).equals("")) {
            holder.tvUserName.setText(user.getName());
        }
        holder.tvUserMoney.setText(String.valueOf(user.getPaymentsSum() + " €"));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyUserViewHolder extends RecyclerView.ViewHolder implements ClickListenerChatFirebase {

        public LinearLayout mUserHolder;
        public CircleImageView mUserPhoto;
        public TextView tvUserMoney, tvUserName;

        public MyUserViewHolder(View itemView) {
            super(itemView);
            mUserHolder = (LinearLayout) itemView.findViewById(R.id.main_information_holder);
            mUserPhoto = (CircleImageView) itemView.findViewById(R.id.user_avatar);
            tvUserName = (TextView) itemView.findViewById(R.id.user_name);
            tvUserMoney = (TextView) itemView.findViewById(R.id.user_money);
        }

        @Override
        public void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {

        }

        @Override
        public void clickImageMapChat(View view, int position, String latitude, String longitude) {

        }

        @Override
        public void clickUserDetail(View view, int position, User user) {
            Toast.makeText(mContext, user.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
