package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mercadopago.android.px.core.CheckoutLazyInit;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.example.R;
import java.util.List;

import static com.mercadopago.android.px.utils.ExamplesUtils.getOptions;
import static com.mercadopago.android.px.utils.ExamplesUtils.resolveCheckoutResult;

public class SelectCheckoutActivity extends AppCompatActivity {

    private static final int REQ_CODE_CHECKOUT = 1;

    View progress;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_checkout);
        progress = findViewById(R.id.mpsdkProgressLayout);
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final DividerItemDecoration dividerItemDecoration =
            new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setAdapter(new SelectionAdapter(getOptions(), this::startCheckout));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void startCheckout(@NonNull final MercadoPagoCheckout.Builder builder) {
        progress.setVisibility(View.VISIBLE);
        final CheckoutLazyInit lazyInit = new CheckoutLazyInit(builder) {
            @Override
            public void fail(@NonNull final MercadoPagoCheckout mercadoPagoCheckout) {
                mercadoPagoCheckout.startPayment(SelectCheckoutActivity.this, REQ_CODE_CHECKOUT);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void success(@NonNull final MercadoPagoCheckout mercadoPagoCheckout) {
                mercadoPagoCheckout.startPayment(SelectCheckoutActivity.this, REQ_CODE_CHECKOUT);
                progress.setVisibility(View.GONE);
            }
        };
        lazyInit.fetch(SelectCheckoutActivity.this);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        resolveCheckoutResult(this, requestCode, resultCode, data, REQ_CODE_CHECKOUT);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ItemHolder> {

        private final List<Pair<String, MercadoPagoCheckout.Builder>> options;
        @NonNull private final Listener listener;

        SelectionAdapter(final List<Pair<String, MercadoPagoCheckout.Builder>> options, @NonNull final Listener listener) {
            this.options = options;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            return new ItemHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.view_option_row, parent, false),
                listener);
        }

        @Override
        public void onBindViewHolder(final ItemHolder holder, final int position) {
            holder.setOption(options.get(position));
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        static class ItemHolder extends RecyclerView.ViewHolder {
            private final TextView text;
            @NonNull private final Listener listener;

            ItemHolder(final View itemView, @NonNull final Listener listener) {
                super(itemView);
                text = (TextView) itemView;
                this.listener = listener;
            }

            void setOption(final Pair<String, MercadoPagoCheckout.Builder> pair) {
                text.setText(pair.first);
                text.setOnClickListener(v -> {
                    assert pair.second != null;
                    listener.onOption(pair.second);
                });
            }
        }

        interface Listener {
            void onOption(@NonNull final MercadoPagoCheckout.Builder builder);
        }
    }
}