package com.mercadopago.android.px.internal.features.payment_vault;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.datasource.PaymentVaultTitleSolver;
import com.mercadopago.android.px.internal.features.uicontrollers.AmountRowController;
import com.mercadopago.android.px.internal.navigation.DefaultPayerInformationDriver;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.AmountView;
import com.mercadopago.android.px.internal.viewmodel.PaymentMethodViewModel;
import com.mercadopago.android.px.internal.viewmodel.mappers.CustomSearchItemToCardMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.CustomSearchOptionViewModelMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.PaymentMethodSearchOptionViewModelMapper;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.PaymentMethods;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.services.Callback;
import com.mercadopago.android.px.tracking.internal.views.SelectMethodChildView;
import com.mercadopago.android.px.tracking.internal.views.SelectMethodView;
import java.util.List;

import static com.mercadopago.android.px.internal.util.ErrorUtil.isErrorResult;

public class PaymentVaultPresenter extends BasePresenter<PaymentVaultView> implements AmountView.OnClick,
    PaymentVault.Actions, AmountRowController.AmountRowVisibilityBehaviour, SearchItemOnClickListenerHandler {

    @NonNull
    private final PaymentSettingRepository paymentSettingRepository;
    @NonNull
    private final UserSelectionRepository userSelectionRepository;

    private final DiscountRepository discountRepository;
    @NonNull private final InitRepository initRepository;
    @NonNull private final ESCManagerBehaviour escManagerBehaviour;
    @NonNull private final PaymentVaultTitleSolver titleSolver;
    /* default */ PaymentMethodSearch initResponse;
    @NonNull private DisabledPaymentMethodRepository disabledPaymentMethodRepository;
    private PaymentMethodSearchItem selectedSearchItem;
    private FailureRecovery failureRecovery;
    private AmountRowController amountRowController;

    public PaymentVaultPresenter(@NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final DisabledPaymentMethodRepository disabledPaymentMethodRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final InitRepository initRepository,
        @NonNull final ESCManagerBehaviour escManagerBehaviour,
        @NonNull final PaymentVaultTitleSolver titleSolver) {
        this.paymentSettingRepository = paymentSettingRepository;
        this.userSelectionRepository = userSelectionRepository;
        this.discountRepository = discountRepository;
        this.initRepository = initRepository;
        this.disabledPaymentMethodRepository = disabledPaymentMethodRepository;
        this.escManagerBehaviour = escManagerBehaviour;
        this.titleSolver = titleSolver;
    }

    public void initialize() {
        try {
            initPaymentVaultFlow();
            getView().setTitle(titleSolver.solveTitle());
        } catch (final IllegalStateException exception) {
            getView().showError(MercadoPagoError.createNotRecoverable(exception.getMessage()), TextUtil.EMPTY);
        }
    }

    public void initPaymentVaultFlow() {
        initializeAmountRow();

        initRepository.init().enqueue(new Callback<InitResponse>() {
            @Override
            public void success(final InitResponse initResponse) {
                if (isViewAttached()) {
                    PaymentVaultPresenter.this.initResponse = initResponse;
                    initPaymentMethodSearch();
                }
            }

            @Override
            public void failure(final ApiException apiException) {
                getView()
                    .showError(MercadoPagoError
                            .createNotRecoverable(apiException, ApiException.ErrorCodes.PAYMENT_METHOD_NOT_FOUND),
                        ApiException.ErrorCodes.PAYMENT_METHOD_NOT_FOUND);
                setFailureRecovery(() -> initPaymentVaultFlow());
            }
        });
    }

    /* default */ void setFailureRecovery(final FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
    }

    public void recoverFromFailure() {
        if (failureRecovery != null) {
            failureRecovery.recover();
        }
    }

    public void initializeAmountRow() {
        if (isViewAttached()) {
            amountRowController = new AmountRowController(this, paymentSettingRepository.getAdvancedConfiguration());
            amountRowController.configure();
        }
    }

    @Override
    public void showAmountRow() {
        getView().showAmount(discountRepository.getCurrentConfiguration(),
            paymentSettingRepository.getCheckoutPreference().getTotalAmount(),
            paymentSettingRepository.getCurrency());
    }

    @Override
    public void hideAmountRow() {
        getView().hideAmountRow();
    }

    public void onPayerInformationReceived() {
        getView().finishPaymentMethodSelection(userSelectionRepository.getPaymentMethod());
    }

    public boolean isItemSelected() {
        return selectedSearchItem != null;
    }

    /* default */ void initPaymentMethodSearch() {
        showPaymentMethodGroup();
    }

    private void showPaymentMethodGroup() {
        if (isItemSelected()) {
            showSelectedItemChildren();
        } else {
            resolveAvailablePaymentMethods();
        }
    }

    private void showSelectedItemChildren() {
        trackScreen();
        getView().setTitle(selectedSearchItem.getChildrenHeader());
        getView().showSearchItems(
            new PaymentMethodSearchOptionViewModelMapper(this).map(selectedSearchItem.getChildren()));
        getView().hideProgress();
    }

    private void resolveAvailablePaymentMethods() {
        if (noPaymentMethodsAvailable()) {
            getView().showEmptyPaymentMethodsError();
        } else if (isOnlyOneItemAvailable() && !isDiscountAvailable()) {
            /**
             * isOnlyOneItemAvailable counts both lists, groups and customSearchItems
             * We need to show discount
             */
            if (!initResponse.getGroups().isEmpty()) {
                getView().saveAutomaticSelection(true);
                selectItem(initResponse.getGroups().get(0));
            } else {
                getView().saveAutomaticSelection(true);
                selectItem(initResponse.getCustomSearchItems().get(0));
            }
        } else {
            showAvailableOptions();
            getView().hideProgress();
        }
    }

    @Override
    public void selectItem(@NonNull final PaymentMethodSearchItem item) {
        userSelectionRepository.select((Card) null, null);
        if (item.hasChildren()) {
            getView().showSelectedItem(item);
        } else if (item.isPaymentType()) {
            startNextStepForPaymentType(item);
        } else if (item.isPaymentMethod()) {
            resolvePaymentMethodSelection(item);
        }
    }

    private void showAvailableOptions() {
        final List<PaymentMethodViewModel> searchItemViewModels =
            new CustomSearchOptionViewModelMapper(this, disabledPaymentMethodRepository)
                .map(initResponse.getCustomSearchItems());
        searchItemViewModels
            .addAll(new PaymentMethodSearchOptionViewModelMapper(this).map(initResponse.getGroups()));
        getView().showSearchItems(searchItemViewModels);
        trackScreen();
    }

    @Override
    public void selectItem(@NonNull final CustomSearchItem item) {
        if (PaymentTypes.isCardPaymentType(item.getType())) {
            userSelectionRepository.select(getCardWithPaymentMethod(item), null);
            getView().startCardFlow();
        } else if (PaymentTypes.isAccountMoney(item.getType())) {
            final PaymentMethod paymentMethod = initResponse.getPaymentMethodById(item.getPaymentMethodId());
            userSelectionRepository.select(paymentMethod, null);
            getView().finishPaymentMethodSelection(paymentMethod);
        } else if (PaymentMethods.CONSUMER_CREDITS.equals(item.getPaymentMethodId())) {
            final PaymentMethod paymentMethod = initResponse.getPaymentMethodById(item.getPaymentMethodId());
            userSelectionRepository.select(paymentMethod, null);
            getView().showInstallments();
        }
    }

    @Override
    public void showDisabledPaymentMethodDetailDialog(@NonNull final DisabledPaymentMethod disabledPaymentMethod) {
        getView().showDisabledPaymentMethodDetailDialog(disabledPaymentMethod);
    }

    private Card getCardWithPaymentMethod(final CustomSearchItem searchItem) {
        final PaymentMethod paymentMethod = initResponse.getPaymentMethodById(searchItem.getPaymentMethodId());
        final Card selectedCard = new CustomSearchItemToCardMapper().map(searchItem);
        if (paymentMethod != null) {
            selectedCard.setPaymentMethod(paymentMethod);
            if (selectedCard.getSecurityCode() == null && paymentMethod.getSettings() != null &&
                paymentMethod.getSettings().get(0) != null) {
                selectedCard.setSecurityCode(paymentMethod.getSettings().get(0).getSecurityCode());
            }
        }
        return selectedCard;
    }

    private Card getCardById(final Iterable<Card> savedCards, final String cardId) {
        Card foundCard = null;
        for (final Card card : savedCards) {
            if (card.getId().equals(cardId)) {
                foundCard = card;
                break;
            }
        }
        return foundCard;
    }

    private void startNextStepForPaymentType(final PaymentMethodSearchItem item) {
        final String itemId = item.getId();
        if (PaymentTypes.isCardPaymentType(itemId)) {
            userSelectionRepository.select(itemId);
            getView().startCardFlow();
        } else {
            getView().startPaymentMethodsSelection(
                paymentSettingRepository.getCheckoutPreference().getPaymentPreference());
        }
    }

    private void resolvePaymentMethodSelection(final PaymentMethodSearchItem item) {
        final PaymentMethod selectedPaymentMethod = initResponse.getPaymentMethodBySearchItem(item);
        userSelectionRepository.select(selectedPaymentMethod, null);
        setSelectedSearchItem(item);
        if (selectedPaymentMethod == null) {
            getView().showMismatchingPaymentMethodError();
        } else {
            handleCollectPayerInformation(selectedPaymentMethod);
        }
    }

    private void handleCollectPayerInformation(final PaymentMethod selectedPaymentMethod) {
        new DefaultPayerInformationDriver(paymentSettingRepository.getCheckoutPreference().getPayer(),
            selectedPaymentMethod).drive(new DefaultPayerInformationDriver.PayerInformationDriverCallback() {
            @Override
            public void driveToNewPayerData() {
                getView().collectPayerInformation();
            }

            @Override
            public void driveToReviewConfirm() {
                getView().finishPaymentMethodSelection(selectedPaymentMethod);
            }
        });
    }

    private boolean isOnlyOneItemAvailable() {
        return initResponse.getGroups().size() + initResponse.getCustomSearchItems().size() == 1;
    }

    private boolean noPaymentMethodsAvailable() {
        return initResponse.getGroups().isEmpty() && initResponse.getCustomSearchItems().isEmpty();
    }

    public void setSelectedSearchItem(final PaymentMethodSearchItem mSelectedSearchItem) {
        selectedSearchItem = mSelectedSearchItem;
    }

    private void trackScreen() {
        // Do not remove check initResponse, sometimes in recovery status is null.
        if (initResponse != null) {
            if (selectedSearchItem == null) {
                trackInitialScreen();
            } else {
                trackChildScreen();
            }
        }
    }

    private void trackInitialScreen() {
        final SelectMethodView selectMethodView =
            new SelectMethodView(initResponse, escManagerBehaviour.getESCCardIds(),
                paymentSettingRepository.getCheckoutPreference(),
                disabledPaymentMethodRepository.getDisabledPaymentMethods().size());
        setCurrentViewTracker(selectMethodView);
    }

    private void trackChildScreen() {
        final SelectMethodChildView selectMethodChildView =
            new SelectMethodChildView(initResponse, selectedSearchItem,
                paymentSettingRepository.getCheckoutPreference(),
                disabledPaymentMethodRepository.getDisabledPaymentMethods().size());
        setCurrentViewTracker(selectMethodChildView);
    }

    @Override
    public void trackOnBackPressed() {
        if (selectedSearchItem == null) {
            tracker.trackAbort();
        } else {
            tracker.trackBack();
        }
    }

    @Override
    public void onActivityResultNotOk(@Nullable final Intent data) {
        trackScreen();
        final boolean shouldFinishOnBack =
            isErrorResult(data) || selectedSearchItem == null || !selectedSearchItem.hasChildren() ||
                selectedSearchItem.getChildren().size() == 1;
        if (shouldFinishOnBack) {
            getView().cancel(data);
        } else {
            getView().overrideTransitionInOut();
        }
    }

    @Override
    public void onDetailClicked(@NonNull final DiscountConfigurationModel discountModel) {
        if (discountModel.getDiscountDescription() != null) {
            getView().showDetailDialog(paymentSettingRepository.getCurrency(), discountModel);
        }
    }

    public void onPaymentMethodReturned() {
        getView().finishPaymentMethodSelection(userSelectionRepository.getPaymentMethod());
    }

    private boolean isDiscountAvailable() {
        return discountRepository.getCurrentConfiguration().getDiscount() != null;
    }
}