package com.mercadopago.android.px.internal.features.installments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.controllers.CheckoutTimer;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.express.installments.InstallmentRowHolder;
import com.mercadopago.android.px.internal.features.express.installments.InstallmentsAdapter;
import com.mercadopago.android.px.internal.features.uicontrollers.card.CardRepresentationModes;
import com.mercadopago.android.px.internal.features.uicontrollers.card.FrontCardView;
import com.mercadopago.android.px.internal.font.FontHelper;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.AmountView;
import com.mercadopago.android.px.internal.view.DiscountDetailDialog;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.math.BigDecimal;
import java.util.List;

public class InstallmentsActivity extends PXActivity<InstallmentsPresenter> implements InstallmentsView {

    private static final int TOOLBAR_TEXT_SIZE = 19;
    private static final int TIMER_TEXT_SIZE = 17;
    private static final String EXTRA_CARD_INFO = "cardInfo";
    //Local vars
    protected boolean activityActive;
    //ViewMode
    protected boolean lowResActive;
    //Low Res View
    protected Toolbar lowResToolbar;
    protected MPTextView lowResTitleToolbar;
    //Normal View
    protected CollapsingToolbarLayout collapsingToolbar;
    protected AppBarLayout appBar;
    protected FrameLayout cardContainer;
    protected Toolbar normalToolbar;
    protected FrontCardView frontCardView;
    protected MPTextView timerTextView;
    private RecyclerView installmentsRecyclerView;
    private AmountView amountView;
    private PaymentSettingRepository configuration;

    public static void start(@NonNull final Activity activity, final int requestCode,
        @NonNull final CardInfo cardInfo) {
        final Intent intent = new Intent(activity, InstallmentsActivity.class);
        intent.putExtra(EXTRA_CARD_INFO, cardInfo);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void start(@NonNull final Activity activity, final int requestCode) {
        final Intent intent = new Intent(activity, InstallmentsActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        final Session session = Session.getInstance();
        final ConfigurationModule configurationModule = session.getConfigurationModule();
        configuration = configurationModule.getPaymentSettings();
        presenter = new InstallmentsPresenter(session.getAmountRepository(), configuration,
            configurationModule.getUserSelectionRepository(), session.getDiscountRepository(),
            session.getSummaryAmountRepository(), session.getAmountConfigurationRepository());

        getActivityParameters();
        presenter.attachView(this);

        activityActive = true;
        lowResActive = ScaleUtil.isLowRes(this);
        setContentView();
        initializeControls();
        initializeView();

        presenter.initialize();
    }

    private void getActivityParameters() {
        final Intent intent = getIntent();
        presenter.setCardInfo((CardInfo) intent.getSerializableExtra(EXTRA_CARD_INFO));
    }

    public void setContentView() {
        if (lowResActive) {
            setContentViewLowRes();
        } else {
            setContentViewNormal();
        }
    }

    public void setContentViewLowRes() {
        setContentView(R.layout.px_activity_installments_lowres);
    }

    public void setContentViewNormal() {
        setContentView(R.layout.px_activity_installments_normal);
    }

    private void initializeControls() {
        installmentsRecyclerView = findViewById(R.id.mpsdkActivityInstallmentsView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        installmentsRecyclerView.setLayoutManager(linearLayoutManager);
        installmentsRecyclerView
            .addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
        timerTextView = findViewById(R.id.mpsdkTimerTextView);
        amountView = findViewById(R.id.amount_view);

        if (lowResActive) {
            initializeLowResControls();
        } else {
            initializeNormalControls();
        }
    }

    private void initializeLowResControls() {
        lowResToolbar = findViewById(R.id.mpsdkRegularToolbar);
        lowResTitleToolbar = findViewById(R.id.mpsdkTitle);

        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            final Toolbar.LayoutParams marginParams =
                new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginParams.setMargins(0, 0, 0, 6);
            lowResTitleToolbar.setLayoutParams(marginParams);
            lowResTitleToolbar.setTextSize(TOOLBAR_TEXT_SIZE);
            timerTextView.setTextSize(TIMER_TEXT_SIZE);
        }

        lowResToolbar.setVisibility(View.VISIBLE);
    }

    private void initializeNormalControls() {
        collapsingToolbar = findViewById(R.id.mpsdkCollapsingToolbar);
        appBar = findViewById(R.id.mpsdkInstallmentesAppBar);
        cardContainer = findViewById(R.id.mpsdkActivityCardContainer);
        normalToolbar = findViewById(R.id.mpsdkRegularToolbar);
        normalToolbar.setVisibility(View.VISIBLE);
    }

    private void initializeView() {
        loadViews();
        hideHeader();
        showTimer();
    }

    public void loadViews() {
        if (lowResActive) {
            loadLowResViews();
        } else {
            loadNormalViews();
        }
    }

    @Override
    public void warnAboutBankInterests() {
        final FrameLayout mNoInstallmentsRate = findViewById(R.id.mpsdkNoInstallmentsRate);
        mNoInstallmentsRate.setVisibility(View.VISIBLE);
        final MPTextView mNoInstallmentsRateTextView = findViewById(R.id.mpsdkNoInstallmentsRateTextView);
        mNoInstallmentsRateTextView.setVisibility(View.VISIBLE);
        mNoInstallmentsRateTextView.setText(R.string.px_interest_label);
    }

    public void loadLowResViews() {
        loadToolbarArrow(lowResToolbar);
        lowResTitleToolbar.setText(getString(R.string.px_card_installments_title));
    }

    public void loadNormalViews() {
        loadToolbarArrow(normalToolbar);
        normalToolbar.setTitle(getString(R.string.px_card_installments_title));
        setCustomFontNormal();

        frontCardView = new FrontCardView(this, CardRepresentationModes.SHOW_FULL_FRONT_ONLY);
        frontCardView.setSize(CardRepresentationModes.MEDIUM_SIZE);
        frontCardView.setPaymentMethod(presenter.getPaymentMethod());
        if (presenter.getCardInfo() != null) {
            frontCardView.setCardNumberLength(presenter.getCardNumberLength());
            frontCardView.setLastFourDigits(presenter.getCardInfo().getLastFourDigits());
        }
        frontCardView.inflateInParent(cardContainer, true);
        frontCardView.initializeControls();
        frontCardView.draw();
        frontCardView.enableEditingCardNumber();
    }

    private void loadToolbarArrow(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void setCustomFontNormal() {
        FontHelper.setFont(collapsingToolbar, PxFont.REGULAR);
    }

    private void hideHeader() {
        if (lowResActive) {
            lowResToolbar.setVisibility(View.GONE);
        } else {
            normalToolbar.setTitle("");
        }
    }

    private void showTimer() {
        if (CheckoutTimer.getInstance().isTimerEnabled()) {
            timerTextView.setVisibility(View.VISIBLE);
            timerTextView.setText(CheckoutTimer.getInstance().getCurrentTime());
        }
    }

    private void showHeader() {
        if (lowResActive) {
            lowResToolbar.setVisibility(View.VISIBLE);
        } else {
            normalToolbar.setTitle(getString(R.string.px_card_installments_title));
            setCustomFontNormal();
        }
    }

    @Override
    public void showApiErrorScreen(final ApiException apiException, final String requestOrigin) {
        ErrorUtil.showApiExceptionError(this, apiException, requestOrigin);
    }

    @Override
    public void showInstallments(final List<InstallmentRowHolder.Model> models) {
        showHeader();
        final InstallmentsAdapter installmentsAdapter = new InstallmentsAdapter(presenter);
        installmentsAdapter.setModels(models);
        installmentsRecyclerView.setAdapter(installmentsAdapter);
    }

    @Override
    public void showLoadingView() {
        installmentsRecyclerView.setVisibility(View.GONE);
        ViewUtils.showProgressLayout(this);
    }

    @Override
    public void hideLoadingView() {
        installmentsRecyclerView.setVisibility(View.VISIBLE);
        ViewUtils.showRegularLayout(this);
    }

    @Override
    public void hideCardContainer() {
        cardContainer.setVisibility(View.GONE);
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.px_collapsing_toolbar_text_large);
        final ViewGroup.LayoutParams params = appBar.getLayoutParams();
        params.height = (int) (getResources().getDimension(R.dimen.px_appbar_height));
        appBar.setLayoutParams(params);
        collapsingToolbar.setExpandedTitleMarginBottom(0);
        collapsingToolbar.setExpandedTitleMarginTop(0);
        collapsingToolbar.setExpandedTitleGravity(Gravity.CENTER);
    }

    @Override
    public void showErrorNoPayerCost() {
        ErrorUtil.startErrorActivity(this, new MercadoPagoError(getString(R.string.px_standard_error_message),
            getString(R.string.px_error_message_detail_no_payer_cost_found), false));
    }

    @Override
    public void finishWithResult() {
        final Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
        animateTransitionSlideInSlideOut();
    }

    public void animateTransitionSlideInSlideOut() {
        overridePendingTransition(R.anim.px_slide_right_to_left_in, R.anim.px_slide_right_to_left_out);
    }

    @Override
    public void onBackPressed() {
        presenter.removeUserSelection();
        setResult(RESULT_CANCELED);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                presenter.recoverFromFailure();
            } else {
                setResult(resultCode, data);
                finish();
            }
        } else {
            setResult(resultCode, data);
            finish();
        }
    }

    @Override
    public void showAmount(@NonNull final DiscountConfigurationModel discountModel,
        @NonNull final BigDecimal itemsPlusCharges, @NonNull final Currency currency) {
        amountView.setVisibility(View.VISIBLE);
        amountView.setOnClickListener(presenter);
        amountView.show(discountModel, itemsPlusCharges, currency);
    }

    @Override
    public void hideAmountRow() {
        amountView.setVisibility(View.GONE);
    }

    @Override
    public void showDetailDialog(@NonNull final Currency currency,
        @NonNull final DiscountConfigurationModel discountModel) {
        DiscountDetailDialog.showDialog(getSupportFragmentManager(), discountModel);
    }
}
