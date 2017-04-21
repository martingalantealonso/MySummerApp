package com.example.mgalante.mysummerapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by mgalante on 21/04/17.
 */

public class PaymentsFirebaseAdapter extends FirebaseRecyclerAdapter<PaymentModel, PaymentsFirebaseAdapter.MyPaymentViewHolder> {

    private ClickListenerPayment mClickListenerPayment;
    private Context mContext;
    private String userId;

    public PaymentsFirebaseAdapter(Context context, DatabaseReference ref, String userId, ClickListenerPayment mClickListenerPayment) {
        super(PaymentModel.class, R.layout.item_payment, PaymentsFirebaseAdapter.MyPaymentViewHolder.class, ref);
        this.mContext = context;
        this.userId = userId;
        this.mClickListenerPayment = mClickListenerPayment;
    }

    @Override
    public MyPaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new MyPaymentViewHolder(view);
    }

    @Override
    protected void populateViewHolder(MyPaymentViewHolder viewHolder, PaymentModel model, int position) {
        if (TextUtils.equals(model.getUserModel().uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) { //Get only the payments of the current user
            viewHolder.setmPaymentTitle(model.getTitle());
            viewHolder.setmPaymentDescription(model.getDescription());
            viewHolder.setmPaymentAmount(String.valueOf(model.getAmount() + " €"));
            if (model.getFile() == null) {
                viewHolder.hidemPaymentImage();
            }
        } else {
            viewHolder.hideMainHolder();
        }
    }

    class MyPaymentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout mMainHolder;
        TextView mPaymentTitle, mPaymentDescription, mPaymentAmount;
        ImageView mPaymentImage;

        public MyPaymentViewHolder(View itemView) {
            super(itemView);
            mMainHolder = (LinearLayout) itemView.findViewById(R.id.grid_main_holder);
            mPaymentTitle = (TextView) itemView.findViewById(R.id.payment_detail_title);
            mPaymentDescription = (TextView) itemView.findViewById(R.id.payment_detail_description);
            mPaymentAmount = (TextView) itemView.findViewById(R.id.payment_detail_amount);
            mPaymentImage = (ImageView) itemView.findViewById(R.id.payment_detail_image);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            PaymentModel model = getItem(position);
            mClickListenerPayment.clickListenerPayment(v, position, model.getUserModel().getName(), model.getUserModel().getPhotoUrl(), model.getFile().getUrl_file());
        }

        void setmPaymentTitle(String title) {
            if (title == null) return;
            mPaymentTitle.setText(title);
        }

        void setmPaymentDescription(String description) {
            if (description == null) return;
            mPaymentDescription.setText(description);
        }

        void setmPaymentAmount(String amount) {
            mPaymentAmount.setText(String.valueOf(amount));
        }

        void hidemPaymentImage() {
            mPaymentImage.setVisibility(View.GONE);
        }

        void hideMainHolder() {
            mMainHolder.setVisibility(View.GONE);
        }
    }

}
