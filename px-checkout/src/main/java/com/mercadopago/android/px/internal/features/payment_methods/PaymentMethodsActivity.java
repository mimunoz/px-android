package com.mercadopago.android.px.internal.features.payment_methods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.adapters.PaymentMethodsAdapter;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.bank_deals.BankDealsActivity;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.PaymentPreference;
import java.util.List;

public class PaymentMethodsActivity extends PXActivity<PaymentMethodsPresenter> implements PaymentMethods.View {

    private static final String EXTRA_PAYMENT_PREFERENCE = "paymentPreference";
    public static final String EXTRA_PAYMENT_METHOD = "paymentMethod";

    protected RecyclerView mRecyclerView;
    protected Toolbar toolbar;
    protected TextView bankDealsTextView;
    protected TextView title;

    private PaymentMethodsPresenter mPresenter;

    public static void start(@NonNull final Activity activity, final int requestCode,
        final PaymentPreference paymentPreference) {
        final Intent paymentMethodsIntent = new Intent(activity, PaymentMethodsActivity.class);
        paymentMethodsIntent.putExtra(EXTRA_PAYMENT_PREFERENCE, (Parcelable) paymentPreference);

        activity.startActivityForResult(paymentMethodsIntent, requestCode);
    }

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        final Session session = Session.getInstance();
        mPresenter =
            new PaymentMethodsPresenter(session.getConfigurationModule().getUserSelectionRepository(),
                session.getPaymentMethodsRepository());

        try {
            getActivityParameters();
            onValidStart();
        } catch (final IllegalStateException exception) {
            onInvalidStart(exception.getMessage());
        }
    }

    private void getActivityParameters() {
        final PaymentPreference paymentPreference = getIntent().getParcelableExtra(EXTRA_PAYMENT_PREFERENCE);
        mPresenter.setPaymentPreference(paymentPreference);
    }

    protected void setContentView() {
        setContentView(R.layout.px_activity_payment_methods);
    }

    protected void initializeControls() {
        mRecyclerView = findViewById(R.id.mpsdkPaymentMethodsList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initializeToolbar();
    }

    protected void onValidStart() {
        mPresenter.attachView(this);

        setContentView();
        initializeControls();
        mPresenter.start();
    }

    protected void onInvalidStart(final String message) {
        ErrorUtil.startErrorActivity(this, message, false);
    }

    private void initializeToolbar() {
        toolbar = findViewById(R.id.mpsdkToolbar);
        bankDealsTextView = findViewById(R.id.mpsdkBankDeals);

        title = findViewById(R.id.mpsdkToolbarTitle);
        title.setText(R.string.px_title_activity_payment_methods);

        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
        }
    }

    protected void recoverFromFailure() {
        mPresenter.recoverFromFailure();
    }

    @Override
    public void showPaymentMethods(final List<PaymentMethod> paymentMethods) {
        mRecyclerView.setAdapter(new PaymentMethodsAdapter(this, paymentMethods, view -> {
            // Return to parent
            final Intent returnIntent = new Intent();
            final PaymentMethod selectedPaymentMethod = (PaymentMethod) view.getTag();
            returnIntent.putExtra(EXTRA_PAYMENT_METHOD, (Parcelable) selectedPaymentMethod);
            setResult(RESULT_OK, returnIntent);
            finish();
        }));
    }

    @Override
    public void showProgress() {
        ViewUtils.showProgressLayout(this);
    }

    @Override
    public void hideProgress() {
        ViewUtils.showRegularLayout(this);
    }

    @Override
    public void showError(final MercadoPagoError exception) {
        ErrorUtil.startErrorActivity(this, exception);
    }

    @Override
    public void showBankDeals() {
        bankDealsTextView.setVisibility(View.VISIBLE);
        bankDealsTextView.setOnClickListener(v -> BankDealsActivity.start(this));
    }
}