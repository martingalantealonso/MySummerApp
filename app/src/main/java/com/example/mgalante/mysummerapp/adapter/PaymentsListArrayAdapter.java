package com.example.mgalante.mysummerapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.PaymentModel;

import java.util.List;

/**
 * Created by mgalante on 20/04/17.
 */

public class PaymentsListArrayAdapter extends RecyclerView.Adapter<PaymentsListArrayAdapter.MyPaymentViewHolder> {

    Context mContext;
    List<PaymentModel> payments;

    public PaymentsListArrayAdapter() {
    }

    public PaymentsListArrayAdapter(Context mContext, List<PaymentModel> payments) {
        this.mContext = mContext;
        this.payments = payments;
    }

    @Override
    public MyPaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new MyPaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyPaymentViewHolder holder, int position) {
        final PaymentModel payment = payments.get(position);

        holder.mPaymentTitle.setText(payment.getTitle());
        holder.mPaymentAmount.setText(String.valueOf(payment.getAmount() + " €"));
        holder.mPaymentDescription.setText(payment.getDescription());
        if (payment.getFile() == null) {
            holder.mPaymentImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    public class MyPaymentViewHolder extends RecyclerView.ViewHolder {

        public TextView mPaymentTitle, mPaymentDescription, mPaymentAmount;
        public ImageView mPaymentImage;

        public MyPaymentViewHolder(View itemView) {
            super(itemView);
            mPaymentTitle = (TextView) itemView.findViewById(R.id.payment_detail_title);
            mPaymentDescription = (TextView) itemView.findViewById(R.id.payment_detail_description);
            mPaymentAmount = (TextView) itemView.findViewById(R.id.payment_detail_amount);
            mPaymentImage = (ImageView) itemView.findViewById(R.id.payment_detail_image);
        }
    }
}
