package com.example.mgalante.mysummerapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Alessandro Barreto on 23/06/2016.
 */
public class ChatFirebaseAdapter extends FirebaseRecyclerAdapter<ChatModel, ChatFirebaseAdapter.MyChatViewHolder> {

    private static final int RIGHT_MSG = 0;
    private static final int LEFT_MSG = 1;
    private static final int RIGHT_MSG_IMG = 2;
    private static final int LEFT_MSG_IMG = 3;

    private ClickListenerChatFirebase mClickListenerChatFirebase;
    private Context mContext;
    private SharedPreferences settings;

    private String userId;


    public ChatFirebaseAdapter(Context context, DatabaseReference ref, String userId, ClickListenerChatFirebase mClickListenerChatFirebase) {
        super(ChatModel.class, R.layout.chat_message_left, ChatFirebaseAdapter.MyChatViewHolder.class, ref);
        this.mContext = context;
        this.userId = userId;
        this.mClickListenerChatFirebase = mClickListenerChatFirebase;
    }

    @Override
    public MyChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == RIGHT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_right, parent, false);
            return new MyChatViewHolder(view);
        } else if (viewType == LEFT_MSG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_left, parent, false);
            return new MyChatViewHolder(view);
        } else if (viewType == RIGHT_MSG_IMG) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_right_img, parent, false);
            return new MyChatViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_left_img, parent, false);
            return new MyChatViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel model = getItem(position);
        SharedPreferences userDetails = mContext.getSharedPreferences("appPreferences", MODE_PRIVATE);
        String UName = userDetails.getString(mContext.getString(R.string.firebase_user_id), null);

        /*if (model.getMapModel() != null) {
            if (model.getUserModel().getName().equals(nameUser)) {
                return RIGHT_MSG_IMG;
            } else {
                return LEFT_MSG_IMG;
            }
        } else if (model.getFile() != null) {
            if (model.getFile().getType().equals("img") && model.getUserModel().getName().equals(nameUser)) {
                return RIGHT_MSG_IMG;
            } else {
                return LEFT_MSG_IMG;
            }
        } else if (model.getUserModel().getName().equals(nameUser)) {
            return RIGHT_MSG;
        } else {
            return LEFT_MSG;
        }*/
        if (model.getUserModel().getUid().equals(userId)) {
            return RIGHT_MSG;
        } else {
            return LEFT_MSG;
        }
    }

    @Override
    protected void populateViewHolder(MyChatViewHolder viewHolder, ChatModel model, int position) {
        viewHolder.setIvUser(model.getUserModel().getPhotoUrl());
        viewHolder.setTxtMessage(model.getMessage());
        viewHolder.setTvTimestamp(model.getTimeStamp());
       /* viewHolder.tvIsLocation(View.GONE);
        if (model.getFile() != null) {
            viewHolder.tvIsLocation(View.GONE);
            viewHolder.setIvChatPhoto(model.getFile().getUrl_file());
        } else if (model.getMapModel() != null) {
            viewHolder.setIvChatPhoto(Util.local(model.getMapModel().getLatitude(), model.getMapModel().getLongitude()));
            viewHolder.tvIsLocation(View.VISIBLE);
        }*/
    }

    public class MyChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTimestamp, tvLocation, txtMessage;
        ImageView ivUser, ivChatPhoto;

        public MyChatViewHolder(View itemView) {
            super(itemView);

            /*LinearLayout messageHolder = (LinearLayout) itemView.findViewById(R.id.main_message_holder);
            final ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
            ImageView userPhoto = (ImageView) convertView.findViewById(R.id.chat_user_img);
            TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
            TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);*/

            tvTimestamp = (TextView) itemView.findViewById(R.id.nameTextView);
            txtMessage = (TextView) itemView.findViewById(R.id.messageTextView);
            //tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            ivChatPhoto = (ImageView) itemView.findViewById(R.id.photoImageView);
            ivUser = (ImageView) itemView.findViewById(R.id.chat_user_img);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ChatModel model = getItem(position);
            if (model.getMapModel() != null) {
                mClickListenerChatFirebase.clickImageMapChat(view, position, model.getMapModel().getLatitude(), model.getMapModel().getLongitude());
            } else {
                mClickListenerChatFirebase.clickImageChat(view, position, model.getUserModel().getName(), model.getUserModel().getPhotoUrl(), model.getFile().getUrl_file());
            }
        }

        public void setTxtMessage(String message) {
            if (txtMessage == null) return;
            txtMessage.setText(message);
        }

        public void setIvUser(String urlPhotoUser) {
            if (ivUser == null) return;
            Glide.with(ivUser.getContext()).load(urlPhotoUser).centerCrop().into(ivUser);
        }

        public void setTvTimestamp(String timestamp) {
            if (tvTimestamp == null) return;
            tvTimestamp.setText(converteTimestamp(timestamp));
        }

        public void setIvChatPhoto(String url) {
            if (ivChatPhoto == null) return;
            Glide.with(ivChatPhoto.getContext()).load(url)
                    .override(100, 100)
                    .fitCenter()
                    .into(ivChatPhoto);
            ivChatPhoto.setOnClickListener(this);
        }

        public void tvIsLocation(int visible) {
            if (tvLocation == null) return;
            tvLocation.setVisibility(visible);
        }

    }

    private CharSequence converteTimestamp(String mileSegundos) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

}
