package com.mercadopago.android.px.internal.features.installments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.controllers.PaymentMethodGuessingController;
import com.mercadopago.android.px.internal.features.express.installments.InstallmentRowHolder;
import com.mercadopago.android.px.internal.features.express.installments.InstallmentsAdapter;
import com.mercadopago.android.px.internal.features.uicontrollers.AmountRowController;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.SummaryAmountRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.AmountView;
import com.mercadopago.android.px.internal.viewmodel.mappers.InstallmentViewModelMapper;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.SummaryAmount;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.services.Callback;
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker;
import com.mercadopago.android.px.tracking.internal.views.InstallmentsViewTrack;
import java.util.List;

public class InstallmentsPresenter extends BasePresenter<InstallmentsView> implements
    AmountView.OnClick, InstallmentsAdapter.ItemListener, AmountRowController.AmountRowVisibilityBehaviour {

    @NonNull /* default */ final AmountRepository amountRepository;
    @NonNull /* default */ final PaymentSettingRepository paymentSettingRepository;
    @NonNull /* default */ final UserSelectionRepository userSelectionRepository;
    @NonNull /* default */ final DiscountRepository discountRepository;
    @NonNull private final SummaryAmountRepository summaryAmountRepository;
    @NonNull private final AmountConfigurationRepository amountConfigurationRepository;
    private FailureRecovery failureRecovery;
    private AmountRowController amountRowController;

    //Card Info
    private String bin = TextUtil.EMPTY;

    @Nullable private CardInfo cardInfo;

    public InstallmentsPresenter(@NonNull final AmountRepository amountRepository,
        @NonNull final PaymentSettingRepository configuration,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final SummaryAmountRepository summaryAmountRepository,
        @NonNull final AmountConfigurationRepository amountConfigurationRepository) {
        this.amountRepository = amountRepository;
        this.paymentSettingRepository = configuration;
        this.userSelectionRepository = userSelectionRepository;
        this.discountRepository = discountRepository;
        this.summaryAmountRepository = summaryAmountRepository;
        this.amountConfigurationRepository = amountConfigurationRepository;
    }

    public void initialize() {
        showSiteRelatedInformation();
        resolvePayerCosts();
    }

    private void showSiteRelatedInformation() {
        if (paymentSettingRepository.getSite().shouldWarnAboutBankInterests()) {
            getView().warnAboutBankInterests();
        }
    }

    private void resolvePayerCosts() {
        if (userSelectionRepository.hasCardSelected()) {
            resolveGenericPayerCosts();
            initializeAmountRow();
        } else if (
            userSelectionRepository.getPaymentMethod().getPaymentTypeId().equals(PaymentTypes.DIGITAL_CURRENCY)) {
            resolveGenericPayerCosts();
            initializeAmountRow();
            getView().hideCardContainer();
        } else {
            resolvePayerCostsForGuessedCard();
        }
    }

    private void resolveGenericPayerCosts() {
        getView().hideLoadingView();
        onPayerCosts(amountConfigurationRepository.getCurrentConfiguration().getPayerCosts());
    }

    /* default */ void resolvePayerCostsForGuessedCard() {
        getView().showLoadingView();
        summaryAmountRepository.getSummaryAmount(bin).enqueue(new Callback<SummaryAmount>() {
            @Override
            public void success(final SummaryAmount summaryAmount) {
                final AmountConfiguration amountConfiguration =
                    summaryAmount.getAmountConfiguration(summaryAmount.getDefaultAmountConfiguration());
                discountRepository.addConfigurations(summaryAmount);
                onPayerCosts(amountConfiguration.getPayerCosts());

                if (isViewAttached()) {
                    initializeAmountRow();
                    getView().hideLoadingView();
                }
            }

            @Override
            public void failure(final ApiException apiException) {
                if (isViewAttached()) {
                    getView().hideLoadingView();
                    getView().showApiErrorScreen(apiException, ApiUtil.RequestOrigin.POST_SUMMARY_AMOUNT);
                    setFailureRecovery(() -> resolvePayerCostsForGuessedCard());
                }
            }
        });
    }

    /* default */ void initializeAmountRow() {
        amountRowController = new AmountRowController(this, paymentSettingRepository.getAdvancedConfiguration());
        amountRowController.configure();
    }

    @Override
    public void showAmountRow() {
        getView().showAmount(discountRepository.getCurrentConfiguration(),
            amountRepository.getItemsPlusCharges(userSelectionRepository.getPaymentMethod().getPaymentTypeId()),
            paymentSettingRepository.getCurrency());
    }

    @Override
    public void hideAmountRow() {
        getView().hideAmountRow();
    }

    @Nullable
    public CardInfo getCardInfo() {
        return cardInfo;
    }

    public void setCardInfo(@Nullable final CardInfo cardInfo) {
        this.cardInfo = cardInfo;
        if (this.cardInfo != null) {
            bin = this.cardInfo.getFirstSixDigits();
        }
    }

    public int getCardNumberLength() {
        return PaymentMethodGuessingController.getCardNumberLength(userSelectionRepository.getPaymentMethod(), bin);
    }

    public FailureRecovery getFailureRecovery() {
        return failureRecovery;
    }

    public void setFailureRecovery(final FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
    }

    @Nullable
    public PaymentMethod getPaymentMethod() {
        return userSelectionRepository.getPaymentMethod();
    }

    public String getBin() {
        return bin;
    }

    public void recoverFromFailure() {
        if (failureRecovery != null) {
            failureRecovery.recover();
        }
    }

    @Override
    public void onDetailClicked(@NonNull final DiscountConfigurationModel discountModel) {
        if(discountModel.getDiscountDescription() != null) {
            getView().showDetailDialog(paymentSettingRepository.getCurrency(), discountModel);
        }
    }

    @Override
    public void onClick(final PayerCost payerCostSelected) {
        userSelectionRepository.select(payerCostSelected);
        getView().finishWithResult();
    }

    /* default */ void onPayerCosts(@NonNull final List<PayerCost> payerCosts) {
        if (payerCosts.isEmpty()) {
            getView().showErrorNoPayerCost();
        } else if (payerCosts.size() == 1) {
            onClick(payerCosts.get(0));
        } else {
            new InstallmentsViewTrack(payerCosts, userSelectionRepository).track();
            final List<InstallmentRowHolder.Model> models =
                new InstallmentViewModelMapper(paymentSettingRepository.getCurrency(), null).map(payerCosts);
            getView().showInstallments(models);
        }
    }

    public void removeUserSelection() {
        userSelectionRepository.reset();
    }
}