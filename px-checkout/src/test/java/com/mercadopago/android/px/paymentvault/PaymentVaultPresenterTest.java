package com.mercadopago.android.px.paymentvault;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.internal.datasource.PaymentVaultTitleSolver;
import com.mercadopago.android.px.internal.features.payment_vault.PaymentVaultPresenter;
import com.mercadopago.android.px.internal.features.payment_vault.PaymentVaultView;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.viewmodel.PaymentMethodViewModel;
import com.mercadopago.android.px.internal.viewmodel.mappers.CustomSearchOptionViewModelMapper;
import com.mercadopago.android.px.internal.viewmodel.mappers.PaymentMethodSearchOptionViewModelMapper;
import com.mercadopago.android.px.mocks.CurrencyStub;
import com.mercadopago.android.px.mocks.InitResponseStub;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.DiscountDescription;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearchItem;
import com.mercadopago.android.px.model.PaymentMethods;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.preferences.PaymentPreference;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import com.mercadopago.android.px.utils.ReflectionArgumentMatchers;
import com.mercadopago.android.px.utils.StubFailMpCall;
import com.mercadopago.android.px.utils.StubSuccessMpCall;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@PrepareForTest(MPTracker.class)
@RunWith(PowerMockRunner.class)
public class PaymentVaultPresenterTest {

    private PaymentVaultPresenter presenter;
    private final StubView stubView = new StubView();

    @Mock private PaymentSettingRepository paymentSettingRepository;
    @Mock private CheckoutPreference checkoutPreference;
    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private DisabledPaymentMethodRepository disabledPaymentMethodRepository;
    @Mock private DiscountRepository discountRepository;
    @Mock private InitRepository initRepository;
    @Mock private AdvancedConfiguration advancedConfiguration;
    @Mock private PaymentVaultView view;
    @Mock private PaymentVaultTitleSolver paymentVaultTitleSolver;

    private static final String CUSTOM_PAYMENT_VAULT_TITLE = "CUSTOM_PAYMENT_VAULT_TITLE";
    private static final DiscountConfigurationModel WITHOUT_DISCOUNT =
        new DiscountConfigurationModel(null, null, false);

    @Before
    public void setUp() {
        PowerMockito.mockStatic(MPTracker.class);
        when(MPTracker.getInstance()).thenReturn(mock(MPTracker.class));
        when(checkoutPreference.getTotalAmount()).thenReturn(new BigDecimal(100));
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(paymentSettingRepository.getAdvancedConfiguration()).thenReturn(advancedConfiguration);

        presenter = getPresenter();
    }

    @NonNull
    private PaymentVaultPresenter getBasePresenter(
        final PaymentVaultView view) {

        final PaymentVaultPresenter presenter =
            new PaymentVaultPresenter(paymentSettingRepository, userSelectionRepository,
                disabledPaymentMethodRepository, discountRepository, initRepository, mock(ESCManagerBehaviour.class),
                paymentVaultTitleSolver);
        presenter.attachView(view);

        return presenter;
    }

    @NonNull
    private PaymentVaultPresenter getPresenter() {
        return getBasePresenter(view);
    }

    @Test
    public void whenNoPaymentMethodsAvailableThenShowError() {
        final InitResponse initResponse = new InitResponse();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        presenter.initialize();

        verify(view).showEmptyPaymentMethodsError();
    }

    @Test
    public void whenPaymentMethodSearchHasItemsShowThem() {
        final InitResponse initResponse = InitResponseStub.NO_CUSTOM_OPTIONS.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        presenter.initialize();

        final List<PaymentMethodViewModel> models =
            new PaymentMethodSearchOptionViewModelMapper(presenter).map(initResponse.getGroups());

        verify(view).showSearchItems(ReflectionArgumentMatchers.reflectionEquals(models));
    }

    @Test
    public void whenPaymentMethodSearchHasPayerCustomOptionsShowThem() {
        final InitResponse initResponse = InitResponseStub.FULL.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        presenter.initialize();

        final List<PaymentMethodViewModel> models =
            new CustomSearchOptionViewModelMapper(presenter, disabledPaymentMethodRepository)
                .map(initResponse.getCustomSearchItems());
        models.addAll(new PaymentMethodSearchOptionViewModelMapper(presenter).map(initResponse.getGroups()));

        verify(view).showSearchItems(ReflectionArgumentMatchers.reflectionEquals(models));
    }

    //Automatic selections

    @Test
    public void whenOnlyUniqueSearchItemAvailableRestartWithItSelected() {
        final InitResponse initResponse = InitResponseStub.ONLY_TICKET_MLA.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));
        when(discountRepository.getCurrentConfiguration()).thenReturn(WITHOUT_DISCOUNT);

        presenter.initialize();

        verify(view).showSelectedItem(initResponse.getGroups().get(0));
    }

    @Test
    public void whenOnlyCardPaymentTypeAvailableStartCardFlow() {
        final InitResponse initResponse = InitResponseStub.ONLY_NEW_CREDIT_CARD.get();
        final PaymentVaultPresenter presenter = getPresenter();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));
        when(discountRepository.getCurrentConfiguration()).thenReturn(WITHOUT_DISCOUNT);
        when(advancedConfiguration.isAmountRowEnabled()).thenReturn(true);
        when(paymentVaultTitleSolver.solveTitle()).thenReturn(CUSTOM_PAYMENT_VAULT_TITLE);
        when(paymentSettingRepository.getCurrency()).thenReturn(initResponse.getCurrency());

        presenter.initialize();

        verify(view).showAmount(discountRepository.getCurrentConfiguration(),
            checkoutPreference.getTotalAmount(), paymentSettingRepository.getCurrency());
        verify(view).saveAutomaticSelection(true);
        verify(view).startCardFlow();
        verify(paymentSettingRepository, atLeastOnce()).getCheckoutPreference();
        verify(userSelectionRepository, times(1)).select(PaymentTypes.CREDIT_CARD);
        verify(view).setTitle(paymentVaultTitleSolver.solveTitle());
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenOnlyCardPaymentTypeAvailableAndAccountMoneyAvailableThenDoNotSelectAutomatically() {
        final InitResponse initResponse = InitResponseStub.ONE_GROUP_ONE_PPM.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));
        when(advancedConfiguration.isAmountRowEnabled()).thenReturn(true);
        when(paymentSettingRepository.getCurrency()).thenReturn(initResponse.getCurrency());

        presenter.initialize();

        verifyDoNotSelectCustomOptionAutomatically(initResponse);
    }

    @Test
    public void whenOnlyOffPaymentTypeAvailableAndAccountMoneyAvailableThenDoNotSelectAutomatically() {
        final InitResponse initResponse = InitResponseStub.ONLY_TICKET_AND_AM_MLA.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));
        when(advancedConfiguration.isAmountRowEnabled()).thenReturn(true);
        when(paymentSettingRepository.getCurrency()).thenReturn(initResponse.getCurrency());

        presenter.initialize();

        verifyDoNotSelectCustomOptionAutomatically(initResponse);
    }

    //User selections

    @Test
    public void whenItemWithChildrenSelectedThenShowChildren() {
        final InitResponse initResponse = InitResponseStub.FULL.get();
        final PaymentMethodSearchItem selectedSearchItem = initResponse.getGroups().get(1);
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        presenter.setSelectedSearchItem(selectedSearchItem);

        presenter.initialize();

        verify(view).setTitle(selectedSearchItem.getChildrenHeader());
        final List<PaymentMethodViewModel> models =
            new PaymentMethodSearchOptionViewModelMapper(presenter).map(selectedSearchItem.getChildren());

        verify(view).showSearchItems(ReflectionArgumentMatchers.reflectionEquals(models));
        verify(view).hideProgress();
    }

    @Test
    public void whenCardPaymentTypeSelectedThenStartCardFlow() {
        final PaymentVaultPresenter presenter = getBasePresenter(stubView);
        final InitResponse initResponse = InitResponseStub.FULL.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        presenter.initialize();
        presenter.selectItem(initResponse.getGroups().get(0));

        Assert.assertTrue(stubView.cardFlowStarted);
    }

    @Test
    public void whenSavedCardSelectedThenStartSavedCardFlow() {
        final PaymentVaultPresenter presenter = getBasePresenter(stubView);
        final InitResponse initResponse = InitResponseStub.FULL.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        presenter.initialize();
        presenter.selectItem(initResponse.getCustomSearchItems().get(1));

        Assert.assertTrue(stubView.cardFlowStarted);
    }

    @Ignore("ignored due to potentially not reachable flow")
    @Test
    public void whenPaymentMethodSearchItemIsNotCardAndDoesNotHaveChildrenThenStartPaymentMethodsSelection() {
        final PaymentVaultPresenter presenter = getBasePresenter(stubView);
        final InitResponse initResponse = InitResponseStub.CREDIT_CARD_AND_PAGOFACIL.get();

        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        presenter.initialize();

        presenter.selectItem(initResponse.getGroups().get(1));
        Assert.assertTrue(stubView.paymentMethodSelectionStarted);
    }

    @Test
    public void whenPaymentMethodTypeSelectedThenSelectPaymentMethod() {
        final PaymentVaultPresenter presenter = getBasePresenter(stubView);
        final InitResponse initResponse = InitResponseStub.CREDIT_CARD_AND_PAGOFACIL.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        presenter.initialize();
        presenter.selectItem(initResponse.getGroups().get(1));

        Assert.assertEquals(initResponse.getGroups().get(1).getId(), stubView.selectedPaymentMethod.getId());
    }

    @Test
    public void whenMaxSavedCardNotSetThenDoNotLimitCardsShown() {
        final PaymentVaultPresenter presenter = getBasePresenter(stubView);
        final InitResponse initResponse = InitResponseStub.FULL.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        presenter.initialize();

        assertEquals(initResponse.getCustomSearchItems().size() + initResponse.getGroups().size(),
            stubView.searchItemsShown.size());
    }

    //Discounts
    @Test
    public void whenDetailClickedThenShowDetailDialog() {
        final Currency currency = CurrencyStub.MLA.get();
        final DiscountConfigurationModel model = mock(DiscountConfigurationModel.class);
        when(paymentSettingRepository.getCurrency()).thenReturn(currency);
        when(model.getDiscountDescription()).thenReturn(mock(DiscountDescription.class));
        presenter.onDetailClicked(model);
        verify(view).showDetailDialog(eq(currency), any(DiscountConfigurationModel.class));
    }

    @Test
    public void whenGroupsRetrievalReturnsAPiExceptionThenShowError() {
        final ApiException apiException = new ApiException();
        when(initRepository.init()).thenReturn(new StubFailMpCall<>(apiException));

        presenter.initialize();

        verify(view).showError(any(MercadoPagoError.class), anyString());
    }

    @Test
    public void whenResourcesRetrievalFailedAndRecoverRequestedThenRepeatRetrieval() {
        final InitResponse initResponse = InitResponseStub.FULL.get();
        final ApiException apiException = new ApiException();
        when(initRepository.init()).thenReturn(new StubFailMpCall<>(apiException));

        presenter.initialize();

        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        //Presenter gets resources, fails
        presenter.recoverFromFailure();

        final List<PaymentMethodViewModel> models =
            new CustomSearchOptionViewModelMapper(presenter, disabledPaymentMethodRepository)
                .map(initResponse.getCustomSearchItems());
        models.addAll(new PaymentMethodSearchOptionViewModelMapper(presenter).map(initResponse.getGroups()));

        verify(view).showSearchItems(ReflectionArgumentMatchers.reflectionEquals(models));
    }

    @Test
    public void whenPaymentMethodSearchSetAndHasItemsThenShowThem() {
        final InitResponse initResponse = InitResponseStub.NO_CUSTOM_OPTIONS.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        presenter.initialize();

        final List<PaymentMethodViewModel> models =
            new PaymentMethodSearchOptionViewModelMapper(presenter).map(initResponse.getGroups());

        verify(view).showSearchItems(ReflectionArgumentMatchers.reflectionEquals(models));
    }

    @Test
    public void whenHasCustomItemsThenShowThemAll() {
        final InitResponse initResponse = InitResponseStub.FULL.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));

        presenter.initialize();

        final List<PaymentMethodViewModel> models =
            new CustomSearchOptionViewModelMapper(presenter, disabledPaymentMethodRepository)
                .map(initResponse.getCustomSearchItems());
        models.addAll(new PaymentMethodSearchOptionViewModelMapper(presenter).map(initResponse.getGroups()));

        verify(view).showSearchItems(ReflectionArgumentMatchers.reflectionEquals(models));
    }

    @Test
    public void whenBoletoSelectedThenCollectPayerInformation() {
        final PaymentVaultPresenter presenter = getPresenter();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(InitResponseStub.ONLY_BOLBRADESCO_MLB.get()));
        when(discountRepository.getCurrentConfiguration()).thenReturn(WITHOUT_DISCOUNT);

        presenter.initialize();

        verify(view).collectPayerInformation();
    }

    @Test
    public void whenPayerInformationReceivedThenFinishWithPaymentMethodSelection() {
        final PaymentVaultPresenter presenter = getPresenter();

        presenter.onPayerInformationReceived();

        verify(view).finishPaymentMethodSelection(userSelectionRepository.getPaymentMethod());
    }

    @Test
    public void whenHasConsumerCreditsVerifyIsShown() {
        final InitResponse initResponse = InitResponseStub.FULL.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));
        presenter.initialize();

        final List<PaymentMethodViewModel> models =
            new CustomSearchOptionViewModelMapper(presenter, disabledPaymentMethodRepository)
                .map(initResponse.getCustomSearchItems());
        models.addAll(new PaymentMethodSearchOptionViewModelMapper(presenter).map(initResponse.getGroups()));

        verify(view).showSearchItems(ReflectionArgumentMatchers.reflectionEquals(models));

        verify(view).hideAmountRow();
        verify(view).hideProgress();
        verify(view).setTitle(any());
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenConsumerCreditsIsSelectedVerifyFlow() {
        final InitResponse initResponse = InitResponseStub.FULL.get();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(initResponse));
        presenter.initialize();

        final List<PaymentMethodViewModel> models =
            new CustomSearchOptionViewModelMapper(presenter, disabledPaymentMethodRepository)
                .map(initResponse.getCustomSearchItems());
        models.addAll(new PaymentMethodSearchOptionViewModelMapper(presenter).map(initResponse.getGroups()));

        verify(view).showSearchItems(ReflectionArgumentMatchers.reflectionEquals(models));

        final CustomSearchItem consumerCreditsItem = initResponse.getCustomSearchItems()
            .stream()
            .filter(item -> item.getPaymentMethodId().equals(PaymentMethods.CONSUMER_CREDITS))
            .findAny().orElse(null);

        Assert.assertNotNull(consumerCreditsItem);
        presenter.selectItem(consumerCreditsItem);

        Assert.assertTrue(PaymentTypes.isDigitalCurrency(consumerCreditsItem.getType()));

        verify(view, times(1)).showInstallments();

        verify(view).hideAmountRow();
        verify(view).hideProgress();
        verify(view).setTitle(any());
        verifyNoMoreInteractions(view);
    }
    // --------- Helper methods ----------- //

    private void verifyInitializeWithGroups(final InitResponse initResponse) {

        verify(view, atLeastOnce()).showAmount(discountRepository.getCurrentConfiguration(),
            paymentSettingRepository.getCheckoutPreference().getTotalAmount(),
            paymentSettingRepository.getCurrency());
        verify(view, atLeastOnce()).hideProgress();
    }

    private void verifyDoNotSelectCustomOptionAutomatically(final InitResponse initResponse) {
        verifyInitializeWithGroups(initResponse);
        final List<PaymentMethodViewModel> models =
            new CustomSearchOptionViewModelMapper(presenter, disabledPaymentMethodRepository)
                .map(initResponse.getCustomSearchItems());
        models.addAll(new PaymentMethodSearchOptionViewModelMapper(presenter).map(initResponse.getGroups()));
        verify(view).showSearchItems(ReflectionArgumentMatchers.reflectionEquals(models));
        verify(view).setTitle(paymentVaultTitleSolver.solveTitle());
        verifyNoMoreInteractions(view);
    }

    /* default */ static class StubView implements PaymentVaultView {

        /* default */ List<PaymentMethodViewModel> searchItemsShown;
        /* default */ boolean cardFlowStarted = false;
        /* default */ PaymentMethod selectedPaymentMethod;
        /* default */ boolean paymentMethodSelectionStarted = false;

        @Override
        public void showInstallments() {

        }

        @Override
        public void showSelectedItem(final PaymentMethodSearchItem item) {
            searchItemsShown = new PaymentMethodSearchOptionViewModelMapper(any()).map(item.getChildren());
        }

        @Override
        public void showProgress() {
            //Not yet tested
        }

        @Override
        public void hideProgress() {
            //Not yet tested
        }

        @Override
        public void showSearchItems(final List<PaymentMethodViewModel> searchItems) {
            searchItemsShown = searchItems;
        }

        @Override
        public void showError(final MercadoPagoError mpException, final String requestOrigin) {
            //Not yet tested
        }

        @Override
        public void setTitle(final String title) {
            //Not yet tested
        }

        @Override
        public void startCardFlow() {
            cardFlowStarted = true;
        }

        @Override
        public void startPaymentMethodsSelection(final PaymentPreference paymentPreference) {
            paymentMethodSelectionStarted = true;
        }

        @Override
        public void finishPaymentMethodSelection(final PaymentMethod selectedPaymentMethod) {
            this.selectedPaymentMethod = selectedPaymentMethod;
        }

        @Override
        public void showAmount(@NonNull final DiscountConfigurationModel discountModel,
            @NonNull final BigDecimal totalAmount,
            @NonNull final Currency currency) {
            //Not yet tested
        }

        @Override
        public void hideAmountRow() {
            //Do nothing
        }

        @Override
        public void collectPayerInformation() {
            //Not yet tested
        }

        @Override
        public void showDetailDialog(@NonNull final Currency currency,
            @NonNull final DiscountConfigurationModel discountModel) {
            //Do nothing
        }

        @Override
        public void showEmptyPaymentMethodsError() {
            //Not yet tested
        }

        @Override
        public void showMismatchingPaymentMethodError() {
            //Not yet tested
        }

        @Override
        public void showDisabledPaymentMethodDetailDialog(@NonNull final DisabledPaymentMethod disabledPaymentMethod) {
            //Do nothing
        }

        @Override
        public void cancel(@Nullable final Intent data) {
            //Do nothing
        }

        @Override
        public void overrideTransitionInOut() {
            //Do nothing
        }

        @Override
        public void saveAutomaticSelection(final boolean automaticSelection) {
            //Not yet tested
        }
    }
}