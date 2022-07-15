package com.mercadopago.android.px.internal.features.payment_vault;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.adapters.PaymentMethodSearchItemAdapter;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.controllers.CheckoutTimer;
import com.mercadopago.android.px.internal.datasource.PaymentVaultTitleSolverImpl;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.cardvault.CardVaultActivity;
import com.mercadopago.android.px.internal.features.disable_payment_method.DisabledPaymentMethodDetailDialog;
import com.mercadopago.android.px.internal.features.installments.InstallmentsActivity;
import com.mercadopago.android.px.internal.features.payer_information.PayerInformationActivity;
import com.mercadopago.android.px.internal.features.payment_methods.PaymentMethodsActivity;
import com.mercadopago.android.px.internal.font.FontHelper;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.AmountView;
import com.mercadopago.android.px.internal.view.DiscountDetailDialog;
import com.mercadopago.android.px.internal.view.GridSpacingItemDecoration;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.viewmodel.PaymentMethodViewModel;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod;
import com.mercadopago.android.px.preferences.PaymentPreference;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import static com.mercadopago.android.px.core.MercadoPagoCheckout.EXTRA_ERROR;
import static com.mercadopago.android.px.internal.features.Constants.RESULT_SILENT_ERROR;
import static com.mercadopago.android.px.internal.features.payment_methods.PaymentMethodsActivity.EXTRA_PAYMENT_METHOD;

public class PaymentVaultActivity extends PXActivity<PaymentVaultPresenter> implements PaymentVaultView {

    public static final int COLUMN_SPACING_DP_VALUE = 20;
    public static final int COLUMNS = 2;
    private static final int REQ_CODE_PAYER_INFORMATION = 22;
    private static final int REQ_CODE_INSTALLMENTS = 23;
    private static final int REQ_CODE_PAYMENT_METHODS = 666;
    private static final int REQ_CARD_VAULT = 102;
    private static final int REQ_CODE_MYSELF = 10;
    private static final String EXTRA_SELECTED_SEARCH_ITEM = "selectedSearchItem";
    private static final String EXTRA_AUTOMATIC_SELECTION = "automaticSelection";
    private static final String MISMATCHING_PAYMENT_METHOD_ERROR = "Payment method in search not found";
    private static final String EXTRA_TOKEN = "token";
    private static final String EXTRA_ISSUER = "issuer";
    private static final String EXTRA_CARD = "card";
    private final PaymentMethodSearchItemAdapter groupsAdapter = new PaymentMethodSearchItemAdapter();
    // Local vars
    protected boolean mActivityActive;
    protected Token mToken;
    protected Issuer mSelectedIssuer;
    protected Card mSelectedCard;
    protected Context mContext;
    // Controls
    protected RecyclerView mSearchItemsRecyclerView;
    protected AppBarLayout mAppBar;
    protected CollapsingToolbarLayout mAppBarLayout;
    protected MPTextView mTimerTextView;
    protected View mProgressLayout;
    private AmountView amountView;
    private boolean automaticSelection;

    public static void start(@NonNull final Activity from, final int requestCode) {
        final Intent intent = new Intent(from, PaymentVaultActivity.class);
        from.startActivityForResult(intent, requestCode);
        from.overridePendingTransition(R.anim.px_slide_right_to_left_in, R.anim.px_slide_right_to_left_out);
    }

    public static void startWithPaymentMethodSelected(@NonNull final Activity from, final int requestCode,
        @NonNull final PaymentMethodSearchItem item) {
        final Intent intent = new Intent(from, PaymentVaultActivity.class);
        intent.putExtra(EXTRA_SELECTED_SEARCH_ITEM, item);
        from.startActivityForResult(intent, requestCode);
        from.overridePendingTransition(R.anim.px_slide_right_to_left_in, R.anim.px_slide_right_to_left_out);
    }

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        final Session session = Session.getInstance();
        final PaymentSettingRepository configuration = session.getConfigurationModule().getPaymentSettings();
        presenter = new PaymentVaultPresenter(configuration,
            session.getConfigurationModule().getUserSelectionRepository(),
            session.getConfigurationModule().getDisabledPaymentMethodRepository(),
            session.getDiscountRepository(),
            session.getInitRepository(),
            session.getMercadoPagoESC(),
            new PaymentVaultTitleSolverImpl(getApplicationContext(),
                configuration.getAdvancedConfiguration().getCustomStringConfiguration()));

        getActivityParameters();
        configurePresenter();
        setContentView();
        initializeControls();

        //Avoid automatic selection if activity restored on back pressed from next step
        if (savedInstanceState != null) {
            automaticSelection = savedInstanceState.getBoolean(EXTRA_AUTOMATIC_SELECTION);
        }
        if (!automaticSelection) {
            initialize();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_AUTOMATIC_SELECTION, automaticSelection);
    }

    @Override
    public void saveAutomaticSelection(final boolean automaticSelection) {
        this.automaticSelection = automaticSelection;
    }

    private void configurePresenter() {
        presenter.attachView(this);
    }

    protected void setContentView() {
        setContentView(R.layout.px_activity_payment_vault);
    }

    protected void getActivityParameters() {
        final Intent intent = getIntent();
        final PaymentMethodSearchItem item =
            (PaymentMethodSearchItem) intent.getSerializableExtra(EXTRA_SELECTED_SEARCH_ITEM);
        if (item != null) {
            presenter.setSelectedSearchItem(item);
        }
    }

    protected void initializeControls() {
        mTimerTextView = findViewById(R.id.mpsdkTimerTextView);

        amountView = findViewById(R.id.amount_view);
        mProgressLayout = findViewById(R.id.mpsdkProgressLayout);

        initializePaymentOptionsRecyclerView();
        mAppBar = findViewById(R.id.mpsdkAppBar);
        mAppBarLayout = findViewById(R.id.mpsdkCollapsingToolbar);
        initializeToolbar();
    }

    protected void initialize() {
        showTimer();
        presenter.initialize();
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            mTimerTextView.setVisibility(View.VISIBLE);
            mTimerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    private void initializeToolbar() {
        final Toolbar toolbar = findViewById(R.id.mpsdkToolbar);
        setSupportActionBar(toolbar);
        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayShowTitleEnabled(false);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        FontHelper.setFont(mAppBarLayout, PxFont.REGULAR);
    }

    protected void initializePaymentOptionsRecyclerView() {
        final int columns = COLUMNS;
        mSearchItemsRecyclerView = findViewById(R.id.mpsdkGroupsList);
        mSearchItemsRecyclerView.setLayoutManager(new GridLayoutManager(this, columns));
        mSearchItemsRecyclerView.addItemDecoration(
            new GridSpacingItemDecoration(columns, ScaleUtil.getPxFromDp(COLUMN_SPACING_DP_VALUE, this), true));
        mSearchItemsRecyclerView.setAdapter(groupsAdapter);
    }

    @Override
    public void showSelectedItem(final PaymentMethodSearchItem item) {
        startWithPaymentMethodSelected(this, REQ_CODE_MYSELF, item);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.initializeAmountRow();
        if (requestCode == REQ_CARD_VAULT) {
            resolveCardRequest(resultCode, data);
        } else if (requestCode == REQ_CODE_PAYMENT_METHODS) {
            presenter.onPaymentMethodReturned();
        } else if (requestCode == REQ_CODE_MYSELF) {
            resolvePaymentVaultRequest(resultCode, data);
        } else if (requestCode == REQ_CODE_PAYER_INFORMATION) {
            resolvePayerInformationRequest(resultCode, data);
        } else if (requestCode == REQ_CODE_INSTALLMENTS) {
            resolveDigitalCurrency(resultCode, data);
        } else if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            resolveErrorRequest(resultCode, data);
            overrideTransitionOut();
        }
    }

    private void resolveErrorRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            recoverFromFailure();
        } else if (presenter.isItemSelected()) {
            hideProgress();
        } else {
            setResult(resultCode, data);
            finish();
        }
    }

    private void recoverFromFailure() {
        presenter.recoverFromFailure();
    }

    private void resolvePaymentVaultRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        } else if (resultCode == RESULT_SILENT_ERROR) {
            setResult(RESULT_SILENT_ERROR);
            finish();
        } else if (resultCode == RESULT_CANCELED && data != null && data.hasExtra(EXTRA_ERROR)) {
            setResult(Activity.RESULT_CANCELED, data);
            finish();
        } else {
            //When it comes back from payment vault "children" view
            presenter.onActivityResultNotOk(data);
        }
    }

    protected void resolveCardRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            showProgress();
            mToken = (Token) data.getSerializableExtra(EXTRA_TOKEN);
            mSelectedIssuer = (Issuer) data.getSerializableExtra(EXTRA_ISSUER);
            mSelectedCard = (Card) data.getSerializableExtra(EXTRA_CARD);
            finishWithCardResult();
        } else if (resultCode == RESULT_SILENT_ERROR) {
            setResult(RESULT_SILENT_ERROR);
            finish();
        } else {
            presenter.onActivityResultNotOk(data);
        }
    }

    private void resolvePayerInformationRequest(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            presenter.onPayerInformationReceived();
        } else {
            presenter.onActivityResultNotOk(data);
        }
    }

    protected void resolveDigitalCurrency(final int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            setResult(Activity.RESULT_OK, data);
            finish();
        } else {
            presenter.onActivityResultNotOk(data);
        }
    }

    @Override
    public void finishPaymentMethodSelection(final PaymentMethod paymentMethod) {
        finishWith(paymentMethod);
    }

    private void finishWith(final PaymentMethod paymentMethod) {
        final Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_PAYMENT_METHOD, (Parcelable) paymentMethod);
        finishWithResult(returnIntent);
    }

    protected void finishWithCardResult() {
        final Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_TOKEN, mToken);
        if (mSelectedIssuer != null) {
            returnIntent.putExtra(EXTRA_ISSUER, (Serializable) mSelectedIssuer);
        }
        returnIntent.putExtra(EXTRA_CARD, (Parcelable) mSelectedCard);
        finishWithResult(returnIntent);
    }

    private void finishWithResult(final Intent returnIntent) {
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        overrideTransitionIn();
    }

    @Override
    public void showProgress() {
        mProgressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressLayout.setVisibility(View.GONE);
    }

    @Override
    public void showSearchItems(final List<PaymentMethodViewModel> searchItems) {
        groupsAdapter.setItems(searchItems);
        groupsAdapter.notifyDataSetChanged();
    }

    @Override
    public void setTitle(final String title) {
        if (mAppBarLayout != null) {
            mAppBarLayout.setTitle(title);
        }
    }

    @Override
    public void showInstallments() {
        InstallmentsActivity.start(this, REQ_CODE_INSTALLMENTS);
        overrideTransitionIn();
    }

    @Override
    public void startCardFlow() {
        CardVaultActivity.startActivity(this, REQ_CARD_VAULT);
        overrideTransitionIn();
    }

    @Override
    public void startPaymentMethodsSelection(final PaymentPreference paymentPreference) {
        PaymentMethodsActivity.start(this, REQ_CODE_PAYMENT_METHODS, paymentPreference);
    }

    public void showApiException(final ApiException apiException, final String requestOrigin) {
        if (mActivityActive) {
            ErrorUtil.showApiExceptionError(this, apiException, requestOrigin);
        }
    }

    @Override
    public void showError(final MercadoPagoError error, final String requestOrigin) {
        if (error.isApiException()) {
            showApiException(error.getApiException(), requestOrigin);
        } else {
            ErrorUtil.startErrorActivity(this, error);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        presenter.trackOnBackPressed();
        finish();
        overrideTransitionOut();
    }

    @Override
    protected void onResume() {
        mActivityActive = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mActivityActive = false;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mActivityActive = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        mActivityActive = false;
        super.onStop();
    }

    @Override
    public void collectPayerInformation() {
        overrideTransitionIn();
        PayerInformationActivity.start(this, REQ_CODE_PAYER_INFORMATION);
    }

    @Override
    public void showDetailDialog(@NonNull final Currency currency,
        @NonNull final DiscountConfigurationModel discountModel) {
        DiscountDetailDialog.showDialog(getSupportFragmentManager(), discountModel);
    }

    @Override
    public void showEmptyPaymentMethodsError() {
        final String errorMessage = getString(R.string.px_no_payment_methods_found);
        showError(MercadoPagoError.createNotRecoverable(errorMessage), TextUtil.EMPTY);
    }

    @Override
    public void showMismatchingPaymentMethodError() {
        final String errorMessage = getString(R.string.px_standard_error_message);
        showError(MercadoPagoError.createNotRecoverable(errorMessage, MISMATCHING_PAYMENT_METHOD_ERROR),
            TextUtil.EMPTY);
    }

    @Override
    public void showAmount(@NonNull final DiscountConfigurationModel discountModel,
        @NonNull final BigDecimal totalAmount,
        @NonNull final Currency currency) {
        amountView.setOnClickListener(presenter);
        amountView.show(discountModel, totalAmount, currency);
    }

    @Override
    public void hideAmountRow() {
        amountView.setVisibility(View.GONE);
    }

    @Override
    public void showDisabledPaymentMethodDetailDialog(@NonNull final DisabledPaymentMethod disabledPaymentMethod) {
        DisabledPaymentMethodDetailDialog
            .showDialog(getSupportFragmentManager(), disabledPaymentMethod.getPaymentStatusDetail(), null);
    }

    @Override
    public void cancel(final Intent data) {
        setResult(Activity.RESULT_CANCELED, data);
        finish();
    }

    @Override
    public void overrideTransitionInOut() {
        overridePendingTransition(R.anim.px_slide_left_to_right_in, R.anim.px_slide_left_to_right_out);
    }
}