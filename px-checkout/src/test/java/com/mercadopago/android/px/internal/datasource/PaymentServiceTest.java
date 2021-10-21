package com.mercadopago.android.px.internal.datasource;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.core.internal.CheckoutData;
import com.mercadopago.android.px.core.internal.PaymentWrapper;
import com.mercadopago.android.px.core.v2.PaymentProcessor;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.core.FileManager;
import com.mercadopago.android.px.internal.datasource.mapper.FromPayerPaymentMethodToCardMapper;
import com.mercadopago.android.px.internal.features.validation_program.ValidationProgramUseCase;
import com.mercadopago.android.px.internal.mappers.PaymentMethodMapper;
import com.mercadopago.android.px.internal.model.SecurityType;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.ApplicationSelectionRepository;
import com.mercadopago.android.px.internal.repository.CongratsRepository;
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.EscPaymentManager;
import com.mercadopago.android.px.internal.repository.PayerCostSelectionRepository;
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodKey;
import com.mercadopago.android.px.internal.repository.PaymentMethodRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.TokenRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.PaymentConfigurationUtil;
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState;
import com.mercadopago.android.px.mocks.CheckoutResponseStub;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardMetadata;
import com.mercadopago.android.px.model.CustomSearchItem;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethods;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.Application;
import com.mercadopago.android.px.model.internal.CheckoutResponse;
import com.mercadopago.android.px.model.internal.FromExpressMetadataToPaymentConfiguration;
import com.mercadopago.android.px.model.internal.OneTapItem;
import com.mercadopago.android.px.model.internal.PaymentConfiguration;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.internal.model.Reason;
import com.mercadopago.android.px.utils.StubFailMpCall;
import java.util.List;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.junit.MockitoJUnitRunner;

import static com.mercadopago.android.px.utils.ReflectionArgumentMatchers.reflectionEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

    private static final String CARD_ID_ESC_APPROVED = "122232111";
    private static final String CARD_ID_ESC_REJECTED = "113210123";
    private static final String CARD_ID_ESC_NOT_AVAILABLE = "113210124";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private PaymentSettingRepository paymentSettingRepository;
    @Mock private DisabledPaymentMethodRepository disabledPaymentMethodRepository;
    @Mock private DiscountRepository discountRepository;
    @Mock private AmountRepository amountRepository;
    @Mock private PaymentProcessor paymentProcessor;
    @Mock private Context context;
    @Mock private EscPaymentManager escPaymentManager;
    @Mock private ESCManagerBehaviour escManagerBehaviour;
    @Mock private TokenRepository tokenRepository;
    @Mock private AmountConfigurationRepository amountConfigurationRepository;
    @Mock private CongratsRepository congratsRepository;
    @Mock private SplitSelectionState splitSelectionState;
    @Mock private PayerCostSelectionRepository payerCostSelectionRepository;
    @Mock private ApplicationSelectionRepository applicationSelectionRepository;

    @Mock private OneTapItem node;
    @Mock private PayerCost payerCost;
    @Mock private PaymentMethod paymentMethod;
    @Mock private Application application;
    @Mock private FileManager fileManager;
    @Mock private FromPayerPaymentMethodToCardMapper fromPayerPaymentMethodToCardMapper;
    @Mock private PaymentMethodMapper paymentMethodMapper;
    @Mock private PaymentMethodRepository paymentMethodRepository;
    @Mock private ValidationProgramUseCase validationProgramUseCase;
    @Mock private CustomOptionIdSolver customOptionIdSolver;

    private PaymentService paymentService;

    private static final DiscountConfigurationModel WITHOUT_DISCOUNT =
        new DiscountConfigurationModel(null, null, false);

    @Before
    public void setUp() {
        paymentService = new PaymentService(userSelectionRepository,
            paymentSettingRepository,
            disabledPaymentMethodRepository,
            discountRepository,
            amountRepository,
            context,
            escPaymentManager,
            escManagerBehaviour,
            tokenRepository,
            amountConfigurationRepository,
            congratsRepository,
            fileManager,
            fromPayerPaymentMethodToCardMapper,
            paymentMethodMapper,
            paymentMethodRepository,
            validationProgramUseCase
        );

        application = mock(Application.class);
        final Application.PaymentMethod applicationPaymentMethod = mock(Application.PaymentMethod.class);
        when(applicationPaymentMethod.getId()).thenReturn(PaymentMethods.ARGENTINA.AMEX);
        when(applicationPaymentMethod.getType()).thenReturn(PaymentTypes.CREDIT_CARD);
        when(application.getPaymentMethod()).thenReturn(applicationPaymentMethod);
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(mock(CheckoutPreference.class));
        when(paymentSettingRepository.getPaymentConfiguration())
            .thenReturn(mock(com.mercadopago.android.px.configuration.PaymentConfiguration.class));
        when(PaymentConfigurationUtil.getPaymentProcessor(paymentSettingRepository.getPaymentConfiguration()))
            .thenReturn(paymentProcessor);
        when(discountRepository.getCurrentConfiguration()).thenReturn(WITHOUT_DISCOUNT);
        when(userSelectionRepository.getPaymentMethod()).thenReturn(paymentMethod);
        when(paymentMethod.getId()).thenReturn(PaymentMethods.ARGENTINA.AMEX);
        when(paymentMethod.getPaymentTypeId()).thenReturn(PaymentTypes.CREDIT_CARD);
        //noinspection unchecked
        when(paymentMethodMapper.map((Pair<String, String>) any())).thenReturn(paymentMethod);
    }

    private PaymentConfiguration mockPaymentConfiguration(@NonNull final OneTapItem oneTapItem,
        @Nullable final PayerCost payerCost) {
        final AmountConfiguration amountConfiguration = mock(AmountConfiguration.class);
        when(amountConfigurationRepository.getConfigurationSelectedFor(anyString())).thenReturn(amountConfiguration);
        when(amountConfiguration.getCurrentPayerCost(anyBoolean(), anyInt())).thenReturn(payerCost);
        when(applicationSelectionRepository.get(oneTapItem)).thenReturn(application);
        return new FromExpressMetadataToPaymentConfiguration(amountConfigurationRepository, splitSelectionState,
            payerCostSelectionRepository, applicationSelectionRepository, customOptionIdSolver).map(oneTapItem);
    }

    @Test
    public void whenStorePayment() {
        paymentService.storePayment(mock(IPaymentDescriptor.class));

        verify(fileManager).writeToFile(any(), any(PaymentWrapper.class));
    }

    @Test
    public void whenRemoveStorePayment() {
        paymentService.reset();

        verify(fileManager).removeFile(any());
    }

    @Test
    public void whenOneTapPaymentIsCardSelectCard() {
        final Card card = creditCardPresetMock(CARD_ID_ESC_APPROVED);

        when(amountConfigurationRepository.getCurrentConfiguration()).thenThrow(IllegalStateException.class);
        paymentService.startExpressPayment(mockPaymentConfiguration(node, payerCost));

        verify(userSelectionRepository).select(reflectionEquals(card), any());
    }

    @Test
    public void whenOneTapPaymentIsCardSelectPayerCost() {
        creditCardPresetMock(CARD_ID_ESC_APPROVED);
        when(amountConfigurationRepository.getCurrentConfiguration()).thenThrow(IllegalStateException.class);
        paymentService.startExpressPayment(mockPaymentConfiguration(node, payerCost));

        verify(userSelectionRepository).select(payerCost);
    }

    @Test
    public void whenOneTapPaymentIsCardPayerCostAndCardSet() {
        final Card card = creditCardPresetMock(CARD_ID_ESC_APPROVED);
        when(amountConfigurationRepository.getCurrentConfiguration()).thenThrow(IllegalStateException.class);
        paymentService.startExpressPayment(mockPaymentConfiguration(node, payerCost));

        verify(userSelectionRepository).select(reflectionEquals(card), any());
        verify(userSelectionRepository).select(payerCost);
    }

    @Test
    public void whenSavedCardAndESCSavedThenAskTokenButFailApiCallThenCVVIsRequiered() {
        final Observer<Pair<Card, Reason>> cvvRequiredObserver = mock(Observer.class);
        final Card card = savedCreditCardOneTapPresent(CARD_ID_ESC_APPROVED);
        when(escPaymentManager.hasEsc(card)).thenReturn(true);
        when(tokenRepository.createToken(card)).thenReturn(new StubFailMpCall(mock(ApiException.class)));
        when(escManagerBehaviour.isESCEnabled()).thenReturn(true);

        paymentService.startExpressPayment(mockPaymentConfiguration(node, payerCost));
        paymentService.getObservableEvents().getRequireCvvLiveData().observeForever(cvvRequiredObserver);
        verify(escPaymentManager).hasEsc(card);
        verifyNoMoreInteractions(escPaymentManager);

        verify(tokenRepository).createToken(card);
        verifyNoMoreInteractions(tokenRepository);

        // if api call to tokenize fails, then ask for CVV.
        verify(cvvRequiredObserver).onChanged(any(Pair.class));
        verifyNoMoreInteractions(cvvRequiredObserver);
    }

    @Test
    public void whenOneTapPaymentWhenSavedCardAndESCSavedThenAskTokenSuccess() {
        final Card card = savedCreditCardOneTapPresent(CARD_ID_ESC_APPROVED);
        when(escPaymentManager.hasEsc(card)).thenReturn(true);
        final MPCall<Token> tokenMPCall = mock(MPCall.class);
        when(tokenRepository.createToken(card)).thenReturn(tokenMPCall);
        when(escManagerBehaviour.isESCEnabled()).thenReturn(true);

        paymentService.startExpressPayment(mockPaymentConfiguration(node, payerCost));

        verify(escPaymentManager).hasEsc(card);
        verifyNoMoreInteractions(escPaymentManager);
        verify(tokenRepository).createToken(card);
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    public void whenOneTapPaymentWhenNotSavedCardAndESCSavedThenAskCVV() {
        final Card card = savedCreditCardOneTapPresent(CARD_ID_ESC_APPROVED);
        when(escPaymentManager.hasEsc(card)).thenReturn(false);
        when(escManagerBehaviour.isESCEnabled()).thenReturn(true);

        paymentService.startExpressPayment(mockPaymentConfiguration(node, payerCost));

        verify(escPaymentManager).hasEsc(card);
        verifyNoMoreInteractions(escPaymentManager);
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    public void whenOneTapPaymentWhenCapExceededThenAskCVV() {
        final Observer<Pair<Card, Reason>> cvvRequiredObserver = mock(Observer.class);
        final Card card = savedCreditCardOneTapPresent(CARD_ID_ESC_REJECTED);
        when(escPaymentManager.hasEsc(card)).thenReturn(true);
        when(escManagerBehaviour.isESCEnabled()).thenReturn(true);

        paymentService.startExpressPayment(mockPaymentConfiguration(node, payerCost));
        paymentService.getObservableEvents().getRequireCvvLiveData().observeForever(cvvRequiredObserver);

        verify(escPaymentManager).hasEsc(card);
        verify(cvvRequiredObserver).onChanged(any(Pair.class));
        verifyNoMoreInteractions(cvvRequiredObserver);
        verifyNoMoreInteractions(escPaymentManager);
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    public void whenOneTapPaymentWhenCapNotExceededAndNotApproved() {
        final Card card = savedCreditCardOneTapPresent(CARD_ID_ESC_NOT_AVAILABLE);
        final MPCall<Token> tokenMPCall = mock(MPCall.class);
        when(tokenRepository.createToken(card)).thenReturn(tokenMPCall);
        when(escPaymentManager.hasEsc(card)).thenReturn(true);
        when(escManagerBehaviour.isESCEnabled()).thenReturn(true);

        paymentService.startExpressPayment(mockPaymentConfiguration(node, payerCost));
        verify(escPaymentManager).hasEsc(card);
        verifyNoMoreInteractions(escPaymentManager);
        verify(tokenRepository).createToken(card);
        verifyNoMoreInteractions(tokenRepository);
    }

    @Test
    public void whenOneTapStartPaymentAndPaymentError() {
        final Observer<MercadoPagoError> errorObserver = mock(Observer.class);
        when(userSelectionRepository.getPaymentMethod().getPaymentTypeId()).thenReturn(PaymentTypes.CREDIT_CARD);
        when(userSelectionRepository.getPayerCost()).thenReturn(null);
        when(amountConfigurationRepository.getCurrentConfiguration()).thenReturn(mock(AmountConfiguration.class));

        paymentService.handlerWrapper.createTransactionLiveData();
        paymentService.getObservableEvents().getPaymentErrorLiveData().observeForever(errorObserver);
        paymentService.startPayment();

        verify(errorObserver).onChanged(any());
    }

    @Test
    public void whenOneTapStartPaymentAndShouldShowVisualPayment() {
        final Observer<Unit> visualPaymentObserver = mock(Observer.class);
        when(userSelectionRepository.getCard()).thenReturn(mock(Card.class));
        when(userSelectionRepository.getPayerCost()).thenReturn(mock(PayerCost.class));
        when(paymentSettingRepository.hasToken()).thenReturn(true);
        when(paymentSettingRepository.getSecurityType()).thenReturn(SecurityType.SECOND_FACTOR);
        when(paymentProcessor.shouldShowFragmentOnPayment(any(CheckoutPreference.class))).thenReturn(true);

        paymentService.handlerWrapper.createTransactionLiveData();
        paymentService.getObservableEvents().getVisualPaymentLiveData().observeForever(visualPaymentObserver);
        paymentService.startPayment();

        verify(visualPaymentObserver).onChanged(any());
    }

    @Test
    public void whenOneTapPaymentWhenHasTokenAndPaymentSuccess() {
        final ArgumentCaptor<CheckoutData> checkoutDataCaptor =
            ArgumentCaptor.forClass(CheckoutData.class);

        final ArgumentCaptor<Function1> validationProgramSuccessCaptor =
            ArgumentCaptor.forClass(Function1.class);

        savedCreditCardOneTapPresent(CARD_ID_ESC_NOT_AVAILABLE);
        when(paymentSettingRepository.hasToken()).thenReturn(true);
        when(paymentSettingRepository.getSecurityType()).thenReturn(SecurityType.SECOND_FACTOR);
        when(paymentProcessor.shouldShowFragmentOnPayment(any(CheckoutPreference.class))).thenReturn(false);
        when(amountConfigurationRepository.getCurrentConfiguration()).thenReturn(mock(AmountConfiguration.class));

        final PaymentConfiguration configuration = mockPaymentConfiguration(node, payerCost);
        paymentService.startExpressPayment(configuration);
        verify(validationProgramUseCase).execute(any(), validationProgramSuccessCaptor.capture());
        final Function1<String, Unit> mockExecute = validationProgramSuccessCaptor.getValue();
        mockExecute.invoke(null);
        verify(paymentProcessor).startPayment(any(), checkoutDataCaptor.capture(), any());

        final PaymentMethod actualPm = checkoutDataCaptor.getValue().paymentDataList.get(0).getPaymentMethod();
        final PayerCost actualPc = checkoutDataCaptor.getValue().paymentDataList.get(0).getPayerCost();

        assertEquals(actualPm.getId(), configuration.component1());
        assertEquals(actualPm.getPaymentTypeId(), configuration.component2());
        assertTrue(new ReflectionEquals(actualPc).matches(payerCost));
    }

    @NonNull
    private Card savedCreditCardOneTapPresent(final String cardId) {
        final Card card = creditCardPresetMock(cardId);
        when(userSelectionRepository.getPayerCost()).thenReturn(mock(PayerCost.class));
        when(userSelectionRepository.getCard()).thenReturn(card);
        when(paymentMethod.getId()).thenReturn(PaymentMethods.ARGENTINA.AMEX);
        when(paymentMethod.getPaymentTypeId()).thenReturn(PaymentTypes.CREDIT_CARD);
        return card;
    }

    private Card getCardById(@NonNull final String cardId) {
        final CheckoutResponse checkoutResponse = CheckoutResponseStub.FULL.get();
        for (final CustomSearchItem customSearchItem : checkoutResponse.getPayerPaymentMethods()) {
            if (customSearchItem.getId().equals(cardId)) {
                final PaymentMethod paymentMethod = getPaymentMethodById(
                    checkoutResponse.getAvailablePaymentMethods(),
                    customSearchItem.getPaymentMethodId());
                final Card card = new Card();
                card.setId(cardId);
                card.setSecurityCode(paymentMethod != null ? paymentMethod.getSecurityCode() : null);
                card.setPaymentMethod(paymentMethod);
                card.setFirstSixDigits(customSearchItem.getFirstSixDigits());
                card.setLastFourDigits(customSearchItem.getLastFourDigits());
                card.setIssuer(customSearchItem.getIssuer());
                card.setEscStatus(customSearchItem.getEscStatus());
                return card;
            }
        }
        return null;
    }

    @Nullable
    private PaymentMethod getPaymentMethodById(
        @NonNull final List<PaymentMethod> paymentMethods,
        @Nullable final String paymentMethodId) {
        for (final PaymentMethod paymentMethod : paymentMethods) {
            if (paymentMethod.getId().equals(paymentMethodId)) {
                return paymentMethod;
            }
        }
        return null;
    }

    private Card creditCardPresetMock(final String cardId) {
        final Card card = getCardById(cardId);
        final CardMetadata cardMetadata = mock(CardMetadata.class);
        when(node.getCard()).thenReturn(cardMetadata);
        when(customOptionIdSolver.get(node)).thenReturn(cardId);
        when(fromPayerPaymentMethodToCardMapper
            .map(new PayerPaymentMethodKey(cardId, PaymentTypes.CREDIT_CARD)))
            .thenReturn(card);
        return card;
    }
}