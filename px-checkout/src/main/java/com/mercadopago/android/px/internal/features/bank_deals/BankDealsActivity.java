package com.mercadopago.android.px.internal.features.bank_deals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.adapters.BankDealsAdapter;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.bank_deal_detail.BankDealDetailActivity;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.BankDeal;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.List;

public class BankDealsActivity extends PXActivity<BankDealsPresenter> implements BankDeals.View {

    public static void start(@NonNull final Activity activity) {
        final Intent intent = new Intent(activity, BankDealsActivity.class);
        activity.startActivity(intent);
    }

    public static void start(@NonNull final Activity activity, final int requestCode) {
        final Intent intent = new Intent(activity, BankDealsActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    protected RecyclerView bankDealsRecyclerView;
    protected Toolbar toolbar;

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        setContentView(R.layout.px_activity_bank_deals);
        initializeControls();
        createPresenter();
        presenter.initialize();
    }

    private void createPresenter() {
        final Session session = Session.getInstance();
        presenter = new BankDealsPresenter(session.getBankDealsRepository());
        presenter.attachView(this);
    }

    protected void initializeControls() {
        initializeToolbar();
        bankDealsRecyclerView = findViewById(R.id.mpsdkBankDealsList);
        bankDealsRecyclerView.setHasFixedSize(true);
        bankDealsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void initializeToolbar() {
        toolbar = findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            presenter.attachView(this);
            if (resultCode == Activity.RESULT_OK) {
                presenter.recoverFromFailure();
            } else {
                finishWithCancelResult();
            }
        }
    }

    private void finishWithCancelResult() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void showApiExceptionError(@NonNull final MercadoPagoError error) {
        ErrorUtil.showApiExceptionError(this,
            error.getApiException(),
            ApiUtil.RequestOrigin.GET_BANK_DEALS);
    }

    @Override
    public void showLoadingView() {
        ViewUtils.showProgressLayout(this);
    }

    @Override
    public void showBankDeals(@NonNull final List<BankDeal> bankDeals,
        @NonNull final OnSelectedCallback<BankDeal> onSelectedCallback) {
        initializeAdapter(bankDeals, onSelectedCallback);
        ViewUtils.showRegularLayout(this);
    }

    private void initializeAdapter(@NonNull final List<BankDeal> bankDeals,
        @NonNull final OnSelectedCallback<BankDeal> onSelectedCallback) {
        final BankDealsAdapter bankDealsAdapter = new BankDealsAdapter(bankDeals, onSelectedCallback);
        bankDealsRecyclerView.setAdapter(bankDealsAdapter);
    }

    @Override
    public void showBankDealDetail(@NonNull final BankDeal bankDeal) {
        BankDealDetailActivity.startWithBankDealLegals(this, bankDeal);
    }
}