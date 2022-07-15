package com.mercadopago.android.px.internal.features.installments;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.internal.features.express.installments.InstallmentRowHolder;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.SummaryAmountRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.viewmodel.mappers.InstallmentViewModelMapper;
import com.mercadopago.android.px.mocks.StubSummaryAmount;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.SummaryAmount;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import com.mercadopago.android.px.utils.ReflectionArgumentMatchers;
import com.mercadopago.android.px.utils.StubFailMpCall;
import com.mercadopago.android.px.utils.StubSuccessMpCall;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@PrepareForTest(MPTracker.class)
@RunWith(PowerMockRunner.class)
public class InstallmentsPresenterTest {

    private InstallmentsPresenter presenter;

    @Mock private AmountRepository amountRepository;
    @Mock private PaymentSettingRepository paymentSettingRepository;
    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private DiscountRepository discountRepository;
    @Mock private SummaryAmountRepository summaryAmountRepository;
    @Mock private AmountConfigurationRepository amountConfigurationRepository;
    @Mock private AdvancedConfiguration advancedConfiguration;
    @Mock private PaymentMethod paymentMethod;
    @Mock private InstallmentsView view;
    @Mock private DiscountConfigurationModel discountConfigurationModel;
    @Mock private AmountConfiguration amountConfiguration;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(MPTracker.class);
        when(MPTracker.getInstance()).thenReturn(mock(MPTracker.class));
        when(paymentSettingRepository.getSite()).thenReturn(Sites.ARGENTINA);
        when(paymentSettingRepository.getAdvancedConfiguration()).thenReturn(advancedConfiguration);
        when(advancedConfiguration.isAmountRowEnabled()).thenReturn(true);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        when(paymentMethod.getPaymentTypeId()).thenReturn(PaymentTypes.CREDIT_CARD);
        when(discountRepository.getCurrentConfiguration()).thenReturn(discountConfigurationModel);
        when(amountConfigurationRepository.getCurrentConfiguration()).thenReturn(amountConfiguration);
        when(amountRepository.getItemsPlusCharges(anyString())).thenReturn(BigDecimal.TEN);
        presenter = new InstallmentsPresenter(amountRepository, paymentSettingRepository, userSelectionRepository,
            discountRepository, summaryAmountRepository, amountConfigurationRepository);
        presenter.attachView(view);
    }

    @Test
    public void whenIsGuessingFlowThenCallSummaryAmount() {
        when(userSelectionRepository.hasCardSelected()).thenReturn(false);
        presetApiError();

        presenter.initialize();

        verify(view).showLoadingView();
        verify(summaryAmountRepository).getSummaryAmount(anyString());
    }

    @Test
    public void whenIsGuessingFlowAndApiCallFailedThenShowErrorScreen() {
        when(userSelectionRepository.hasCardSelected()).thenReturn(false);
        final ApiException apiException = presetApiError();

        presenter.initialize();

        verify(view).showLoadingView();
        verify(view).hideLoadingView();
        verify(view).showApiErrorScreen(apiException, ApiUtil.RequestOrigin.POST_SUMMARY_AMOUNT);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenIsGuessingAndPayerCostIsSelectedFinishWithResult() {
        when(userSelectionRepository.hasCardSelected()).thenReturn(false);
        final SummaryAmount response = StubSummaryAmount.getSummaryAmountTwoPayerCosts();
        when(summaryAmountRepository.getSummaryAmount(anyString())).thenReturn(new StubSuccessMpCall<>(response));
        final List<PayerCost> payerCosts = response.getAmountConfiguration(response.getDefaultAmountConfiguration())
            .getPayerCosts();
        final List<InstallmentRowHolder.Model> models =
            new InstallmentViewModelMapper(paymentSettingRepository.getCurrency(), null).map(payerCosts);

        presenter.initialize();
        presenter.onClick(payerCosts.get(0));

        verify(view).showLoadingView();
        verify(view).hideLoadingView();
        verify(view).showInstallments(ReflectionArgumentMatchers.reflectionEquals(models));
        verify(view).showAmount(discountConfigurationModel, BigDecimal.TEN, paymentSettingRepository.getCurrency());
        verify(view).finishWithResult();
        verify(summaryAmountRepository).getSummaryAmount(anyString());
        verify(userSelectionRepository).select(payerCosts.get(0));
        verifyNoMoreInteractions(view, summaryAmountRepository);
    }

    @Test
    public void whenMCOThenShowBankInterestsNotCoveredWarning() {
        when(paymentSettingRepository.getSite()).thenReturn(Sites.COLOMBIA);
        when(userSelectionRepository.hasCardSelected()).thenReturn(false);
        final SummaryAmount response = StubSummaryAmount.getSummaryAmountTwoPayerCosts();
        when(summaryAmountRepository.getSummaryAmount(anyString())).thenReturn(new StubSuccessMpCall<>(response));

        presenter.initialize();
        verify(view).warnAboutBankInterests();
    }

    @Test
    public void whenCardSelectedThenResolvePayerCosts() {
        final List<PayerCost> payerCosts = Collections.emptyList();
        when(userSelectionRepository.hasCardSelected()).thenReturn(true);
        when(amountConfiguration.getPayerCosts()).thenReturn(payerCosts);

        presenter.initialize();

        verify(view).hideLoadingView();
    }

    @Test
    public void whenCardSelectedAndNoPayerCostsThenShowError() {
        final List<PayerCost> payerCosts = Collections.emptyList();
        when(userSelectionRepository.hasCardSelected()).thenReturn(true);
        when(amountConfiguration.getPayerCosts()).thenReturn(payerCosts);

        presenter.initialize();

        verify(view).hideLoadingView();
        verify(view).showAmount(discountConfigurationModel, BigDecimal.TEN, paymentSettingRepository.getCurrency());
        verify(view).showErrorNoPayerCost();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenCardSelectedAndOnlyOnePayerCostThenSelectAndFinish() {
        final PayerCost selectedPayerCost = mock(PayerCost.class);
        final List<PayerCost> payerCosts = Collections.singletonList(selectedPayerCost);
        when(userSelectionRepository.hasCardSelected()).thenReturn(true);
        when(amountConfiguration.getPayerCosts()).thenReturn(payerCosts);

        presenter.initialize();

        verify(userSelectionRepository).select(selectedPayerCost);
        verify(view).hideLoadingView();
        verify(view).showAmount(discountConfigurationModel, amountRepository.getItemsPlusCharges(anyString()),
            paymentSettingRepository.getCurrency());
        verify(view).finishWithResult();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenCardSelectedAndMultiplePayerCostThenDisplayThem() {
        final List<PayerCost> payerCosts = Arrays.asList(mock(PayerCost.class), mock(PayerCost.class));
        final List<InstallmentRowHolder.Model> models =
            new InstallmentViewModelMapper(paymentSettingRepository.getCurrency(), null).map(payerCosts);
        when(userSelectionRepository.hasCardSelected()).thenReturn(true);
        when(amountConfiguration.getPayerCosts()).thenReturn(payerCosts);

        presenter.initialize();

        verify(view).hideLoadingView();
        verify(view).showAmount(discountConfigurationModel, amountRepository.getItemsPlusCharges(anyString()),
            paymentSettingRepository.getCurrency());
        verify(view).showInstallments(ReflectionArgumentMatchers.reflectionEquals(models));
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenAmountRowIsNotEnabledItShouldBeHidden() {
        when(userSelectionRepository.hasCardSelected()).thenReturn(true);
        when(advancedConfiguration.isAmountRowEnabled()).thenReturn(false);

        presenter.initialize();

        verify(view).hideAmountRow();
    }

    @Test
    public void whenAmountRowIsEnabledItShouldBeSetted() {
        when(userSelectionRepository.hasCardSelected()).thenReturn(true);
        when(advancedConfiguration.isAmountRowEnabled()).thenReturn(true);

        presenter.initialize();

        verify(view).showAmount(discountConfigurationModel, amountRepository.getItemsPlusCharges(anyString()),
            paymentSettingRepository.getCurrency());
    }

    @Test
    public void whenAmountRowIsNotEnabledItShouldBeHiddenWithGuessedCards() {
        when(userSelectionRepository.hasCardSelected()).thenReturn(false);

        final SummaryAmount response = StubSummaryAmount.getSummaryAmountTwoPayerCosts();
        when(summaryAmountRepository.getSummaryAmount(anyString())).thenReturn(new StubSuccessMpCall<>(response));

        when(advancedConfiguration.isAmountRowEnabled()).thenReturn(false);

        presenter.initialize();

        verify(view).hideAmountRow();
    }

    @Test
    public void whenAmountRowIsEnabledItShouldBeSettedWithGuessedCards() {
        when(userSelectionRepository.hasCardSelected()).thenReturn(false);

        final SummaryAmount response = StubSummaryAmount.getSummaryAmountTwoPayerCosts();
        when(summaryAmountRepository.getSummaryAmount(anyString())).thenReturn(new StubSuccessMpCall<>(response));

        when(advancedConfiguration.isAmountRowEnabled()).thenReturn(true);

        presenter.initialize();

        verify(view).showAmount(discountConfigurationModel, amountRepository.getItemsPlusCharges(anyString()),
            paymentSettingRepository.getCurrency());
        verify(view).hideLoadingView();
    }

    @NonNull
    private ApiException presetApiError() {
        final ApiException apiException = new ApiException();
        when(summaryAmountRepository.getSummaryAmount(anyString()))
            .thenReturn(new StubFailMpCall<>(apiException));
        return apiException;
    }
}