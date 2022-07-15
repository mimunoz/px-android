package com.mercadopago.android.px.internal.features.review_payment_methods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.adapters.ReviewPaymentMethodsAdapter;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.ArrayList;
import java.util.List;

public class ReviewPaymentMethodsActivity extends PXActivity<ReviewPaymentMethodsPresenter>
    implements ReviewPaymentMethods.View {

    private static final String EXTRA_PAYMENT_METHODS = "EXTRA_PAYMENT_METHODS";
    private RecyclerView recyclerView;

    public static void start(final Activity activity, final List<PaymentMethod> paymentMethods,
        final int reqCode) {
        final Intent intent = new Intent(activity, ReviewPaymentMethodsActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_PAYMENT_METHODS, (ArrayList<? extends Parcelable>) paymentMethods);
        activity.startActivityForResult(intent, reqCode);
    }

    @Override
    protected void onCreated(@Nullable final Bundle savedInstance) {
        setContentView(R.layout.px_activity_review_payment_methods);
        recyclerView = findViewById(R.id.mpsdkReviewPaymentMethodsView);
        final FrameLayout mTryOtherCardButton = findViewById(R.id.tryOtherCardButton);
        mTryOtherCardButton.setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.px_no_change_animation, R.anim.px_slide_down_activity);
        });
        initPresenter();
    }

    protected void initPresenter() {
        List<PaymentMethod> supportedPaymentMethods = null;
        try {
            supportedPaymentMethods = getIntent().getParcelableArrayListExtra(EXTRA_PAYMENT_METHODS);
        } catch (final Exception ex) {
            showError(new MercadoPagoError(getString(R.string.px_standard_error_message), false), "");
        }

        if (supportedPaymentMethods != null && !supportedPaymentMethods.isEmpty()) {
            final ReviewPaymentMethodsPresenter presenter = new ReviewPaymentMethodsPresenter(supportedPaymentMethods);
            presenter.attachView(this);
            presenter.initialize();
        } else {
            //Invalid data.
            finish();
        }
    }

    @Override
    public void showError(final MercadoPagoError error, final String requestOrigin) {
        ErrorUtil.startErrorActivity(this, error);
    }

    @Override
    public void initializeSupportedPaymentMethods(
        @Size(min = 1) @NonNull final List<PaymentMethod> supportedPaymentMethods) {
        final ReviewPaymentMethodsAdapter mAdapter = new ReviewPaymentMethodsAdapter(supportedPaymentMethods);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
