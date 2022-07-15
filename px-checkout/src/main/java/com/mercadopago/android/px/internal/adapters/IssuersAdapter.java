package com.mercadopago.android.px.internal.adapters;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.features.uicontrollers.issuers.IssuersView;
import com.mercadopago.android.px.model.Issuer;
import java.util.ArrayList;
import java.util.List;

public class IssuersAdapter extends RecyclerView.Adapter<IssuersAdapter.ViewHolder> {

    private final List<Issuer> mIssuers;
    /* default */ final OnSelectedCallback<Integer> mCallback;

    public IssuersAdapter(OnSelectedCallback<Integer> callback) {
        mIssuers = new ArrayList<>();
        mCallback = callback;
    }

    public void addResults(List<Issuer> list) {
        mIssuers.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mIssuers.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View adapterView = inflater.inflate(R.layout.px_adapter_issuer, parent, false);
        return new ViewHolder(adapterView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Issuer issuer = mIssuers.get(position);
        holder.mIssuersView.drawIssuer(issuer);
    }

    @Override
    public int getItemCount() {
        return mIssuers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final FrameLayout mIssuerContainer;
        private final IssuersView mIssuersView;

        public ViewHolder(View itemView) {
            super(itemView);
            mIssuerContainer = itemView.findViewById(R.id.mpsdkIssuerAdapterContainer);
            mIssuersView = new IssuersView(itemView.getContext());
            mIssuersView.inflateInParent(mIssuerContainer, true);
            mIssuersView.initializeControls();

            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                        mCallback.onSelected(getLayoutPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
