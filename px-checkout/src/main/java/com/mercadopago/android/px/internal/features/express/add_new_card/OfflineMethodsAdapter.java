package com.mercadopago.android.px.internal.features.express.add_new_card;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mercadopago.android.px.R;
import java.util.List;

/* default */ final class OfflineMethodsAdapter extends RecyclerView.Adapter<OfflineMethodsRowHolder> {

    private final List<OfflineMethodItem> offlineItems;
    private OfflineMethodsRowHolder lastHolder;
    private int lastPositionSelected = -1;
    private OfflineMethodsFragment.OnMethodSelectedListener onMethodSelectedListener;

    public OfflineMethodsAdapter(@NonNull final List<OfflineMethodItem> offlineItems,
        final OfflineMethodsFragment.OnMethodSelectedListener onMethodSelectedListener) {
        this.offlineItems = offlineItems;
        this.onMethodSelectedListener = onMethodSelectedListener;
    }

    @NonNull
    @Override
    public OfflineMethodsRowHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int i) {
        final View offlineMethodView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.px_view_offline_item, parent, false);

        return new OfflineMethodsRowHolder(offlineMethodView);
    }

    @Override
    public void onBindViewHolder(@NonNull final OfflineMethodsRowHolder holder,
        @SuppressLint("RecyclerView") final int position) {
        holder.setChecked(lastPositionSelected >= 0 && lastPositionSelected == position);

        holder.populate(offlineItems.get(position), () -> {
            if (lastHolder != null) {
                lastHolder.setChecked(false);
            }

            holder.setChecked(true);
            lastHolder = holder;
            lastPositionSelected = position;

            onMethodSelectedListener.onItemSelected(offlineItems.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return offlineItems.size();
    }

    interface OnItemClicked {
        void onClick();
    }
}
