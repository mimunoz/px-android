package com.mercadopago.android.px.securitycode;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.internal.features.SecurityCodeActivityView;
import com.mercadopago.android.px.internal.features.SecurityCodePresenter;
import com.mercadopago.android.px.internal.repository.CardTokenRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.mocks.PaymentMethodStub;
import com.mercadopago.android.px.mocks.Tokens;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.SavedCardToken;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.SecurityCode;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.CardTokenException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import com.mercadopago.android.px.tracking.internal.model.Reason;
import com.mercadopago.android.px.utils.StubFailMpCall;
import com.mercadopago.android.px.utils.StubSuccessMpCall;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@PrepareForTest(MPTracker.class)
@RunWith(PowerMockRunner.class)
public class SecurityCodePresenterTest {

    public static final String DUMMY_CVV = "123";
    public static final String DUMMY_CARD_ID = "12356";
    @Mock private PaymentSettingRepository paymentSettingRepository;
    @Mock private CardTokenRepository cardTokenRepository;
    @Mock private SecurityCodeActivityView view;
    @Mock private ESCManagerBehaviour escManagerBehaviour;
    @Mock private Card card;
    @Mock private PaymentRecovery paymentRecovery;
    @Mock private CardInfo cardInfo;
    @Mock private Reason reason;

    private Token stubToken;
    private PaymentMethod stubPaymentMethod;
    private SecurityCodePresenter presenter;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(MPTracker.class);
        when(MPTracker.getInstance()).thenReturn(mock(MPTracker.class));
        stubToken = Tokens.getVisaToken();
        stubPaymentMethod = PaymentMethodStub.VISA_CREDIT.get();
        presenter = getPresenter();
    }

    @Test
    public void whenCardAndTokenNotSetThenShowError() {
        final SecurityCodePresenter presenter = getBasePresenter(view);

        presenter.initialize();

        verify(view).showStandardErrorMessage();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenCardSetButNoTokenThenShowError() {
        final SecurityCodePresenter presenter = getBasePresenter(view);

        presenter.setCard(card);
        presenter.initialize();

        verify(view).showStandardErrorMessage();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenTokenSetButNoCardThenShowError() {
        final SecurityCodePresenter presenter = getBasePresenter(view);

        presenter.setToken(stubToken);
        presenter.initialize();

        verify(view).showStandardErrorMessage();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenCardAndTokenSetButNoRecoveryThenShowError() {
        final SecurityCodePresenter presenter = getBasePresenter(view);

        presenter.setCard(card);
        presenter.setToken(stubToken);
        presenter.initialize();

        verify(view).showStandardErrorMessage();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenPaymentMethodNotSetThenShowError() {
        final SecurityCodePresenter presenter = getBasePresenter(view);

        presenter.setCard(card);
        presenter.setToken(stubToken);
        presenter.setPaymentRecovery(paymentRecovery);
        presenter.initialize();

        verify(view).showStandardErrorMessage();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenCardInfoNotSetThenShowError() {
        final SecurityCodePresenter presenter = getBasePresenter(view);

        presenter.setCard(card);
        presenter.setToken(stubToken);
        presenter.setPaymentRecovery(paymentRecovery);
        presenter.setPaymentMethod(stubPaymentMethod);
        presenter.initialize();

        verify(view).showStandardErrorMessage();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenStartedWithValidParamsThenInitializeAndShowTimer() {
        presenter.initialize();

        verify(view).initialize();
        verify(view).showTimer();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenInitializedWithoutSecurityCodeSettingsThenSetDefaultSecurityCodeInputMaxLength() {
        final PaymentMethod paymentMethod = mock(PaymentMethod.class);

        presenter.setPaymentMethod(paymentMethod);
        presenter.initializeSettings();

        verify(view).setSecurityCodeInputMaxLength(Card.CARD_DEFAULT_SECURITY_CODE_LENGTH);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenCallForAuthRecoveryThenCloneTokenPutSecurityCodeAndFinishWithResult() {
        when(paymentRecovery.isStatusDetailCallForAuthorize()).thenReturn(true);
        when(cardTokenRepository.cloneToken(stubToken.getId())).thenReturn(new StubSuccessMpCall<>(stubToken));
        when(cardTokenRepository.putSecurityCode(anyString(), anyString())).thenReturn(new StubSuccessMpCall<>(
            stubToken));

        presenter.saveSecurityCode(DUMMY_CVV);
        presenter.validateSecurityCodeInput();

        verify(view).clearErrorView();
        verify(view).showLoadingView();
        verify(view).finishWithResult();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenCallForAuthRecoveryAndCloneTokenFailsThenShowError() {
        final ApiException apiException = mock(ApiException.class);
        when(paymentRecovery.isStatusDetailCallForAuthorize()).thenReturn(true);
        when(cardTokenRepository.cloneToken(stubToken.getId())).thenReturn(new StubFailMpCall<>(apiException));

        presenter.saveSecurityCode(DUMMY_CVV);
        presenter.validateSecurityCodeInput();

        verify(view).clearErrorView();
        verify(view).showLoadingView();
        verify(view).stopLoadingView();
        verify(view).showError(any(MercadoPagoError.class), anyString());
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenCallForAuthRecoveryCloneTokenAndPutSecurityCodeFailsThenShowError() {
        final ApiException apiException = mock(ApiException.class);
        when(paymentRecovery.isStatusDetailCallForAuthorize()).thenReturn(true);
        when(cardTokenRepository.cloneToken(stubToken.getId())).thenReturn(new StubSuccessMpCall<>(stubToken));
        when(cardTokenRepository.putSecurityCode(anyString(), anyString()))
            .thenReturn(new StubFailMpCall<>(apiException));

        presenter.saveSecurityCode(DUMMY_CVV);
        presenter.validateSecurityCodeInput();

        verify(view).clearErrorView();
        verify(view).showLoadingView();
        verify(view).stopLoadingView();
        verify(view).showError(any(MercadoPagoError.class), anyString());
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenCallForAuthRecoveryAndSecurityCodeInputIsNotValidThenShowError() {
        when(paymentRecovery.isStatusDetailCallForAuthorize()).thenReturn(true);

        presenter.validateSecurityCodeInput();

        verify(view).setErrorView(any(CardTokenException.class));
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenSaveCardWithESCEnabledThenCreateESCToken() {
        final SecurityCode securityCode = mock(SecurityCode.class);
        when(securityCode.getLength()).thenReturn(DUMMY_CVV.length());
        when(escManagerBehaviour.isESCEnabled()).thenReturn(true);
        when(card.getSecurityCode()).thenReturn(securityCode);
        when(cardTokenRepository.createToken(any(SavedESCCardToken.class)))
            .thenReturn(new StubSuccessMpCall<>(stubToken));
        final Token token = mock(Token.class);
        when(token.getCardId()).thenReturn("123");

        presenter.setToken(token);
        presenter.saveSecurityCode(DUMMY_CVV);
        presenter.validateSecurityCodeInput();

        mockClearCap(card.getId());

        verify(view).showLoadingView();
        verify(view).finishWithResult();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenSaveCardWithESCEnabledAndESCTokenCreationFailsThenShowError() {
        final ApiException apiException = mock(ApiException.class);
        final SecurityCode securityCode = mock(SecurityCode.class);
        when(securityCode.getLength()).thenReturn(DUMMY_CVV.length());
        when(escManagerBehaviour.isESCEnabled()).thenReturn(true);
        when(card.getSecurityCode()).thenReturn(securityCode);
        when(cardTokenRepository.createToken(any(SavedESCCardToken.class)))
            .thenReturn(new StubFailMpCall<>(apiException));
        final Token token = mock(Token.class);
        when(token.getCardId()).thenReturn("123");

        presenter.setToken(token);
        presenter.saveSecurityCode(DUMMY_CVV);
        presenter.validateSecurityCodeInput();

        verify(view).showLoadingView();
        verify(view).stopLoadingView();
        verify(view).showError(any(MercadoPagoError.class), anyString());
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenESCRecoverFromPaymentThenCreateESCToken() {
        final SecurityCode securityCode = mock(SecurityCode.class);
        when(securityCode.getLength()).thenReturn(DUMMY_CVV.length());
        when(card.getId()).thenReturn(DUMMY_CARD_ID);
        when(card.getSecurityCode()).thenReturn(securityCode);
        when(paymentRecovery.isStatusDetailInvalidESC()).thenReturn(true);
        when(cardTokenRepository.createToken(any(SavedESCCardToken.class)))
            .thenReturn(new StubSuccessMpCall<>(stubToken));
        final Token token = mock(Token.class);
        when(token.getCardId()).thenReturn("123");

        presenter.setToken(token);
        presenter.saveSecurityCode(DUMMY_CVV);
        presenter.validateSecurityCodeInput();

        mockClearCap(card.getId());

        verify(view).showLoadingView();
        verify(view).finishWithResult();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenSavedCardWithoutESCEnabledThenCreateToken() {
        final SecurityCode securityCode = mock(SecurityCode.class);
        when(securityCode.getLength()).thenReturn(DUMMY_CVV.length());
        when(card.getId()).thenReturn(DUMMY_CARD_ID);
        when(card.getSecurityCode()).thenReturn(securityCode);
        when(escManagerBehaviour.isESCEnabled()).thenReturn(false);
        when(cardTokenRepository.createToken(any(SavedCardToken.class))).thenReturn(new StubSuccessMpCall<>(stubToken));
        final Token token = mock(Token.class);
        when(token.getCardId()).thenReturn("123");

        presenter.setToken(token);
        presenter.saveSecurityCode(DUMMY_CVV);
        presenter.validateSecurityCodeInput();

        verify(view).showLoadingView();
        verify(view).finishWithResult();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenSavedCardWithoutESCEnabledAndCreateTokenFailsThenShowError() {
        final ApiException apiException = mock(ApiException.class);
        final SecurityCode securityCode = mock(SecurityCode.class);
        when(securityCode.getLength()).thenReturn(DUMMY_CVV.length());
        when(card.getId()).thenReturn(DUMMY_CARD_ID);
        when(card.getSecurityCode()).thenReturn(securityCode);
        when(escManagerBehaviour.isESCEnabled()).thenReturn(false);
        when(cardTokenRepository.createToken(any(SavedCardToken.class))).thenReturn(new StubFailMpCall<>(apiException));
        final Token token = mock(Token.class);
        when(token.getCardId()).thenReturn("123");

        presenter.setToken(token);
        presenter.saveSecurityCode(DUMMY_CVV);
        presenter.validateSecurityCodeInput();

        verify(view).showLoadingView();
        verify(view).stopLoadingView();
        verify(view).showError(any(MercadoPagoError.class), anyString());
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenESCRecoverFromPaymentWithPaymentResultIntegrationThenCreateESCToken() {
        final Token token = mock(Token.class);
        when(token.getCardId()).thenReturn(UUID.randomUUID().toString());
        when(paymentRecovery.isStatusDetailInvalidESC()).thenReturn(true);
        when(cardTokenRepository.createToken(any(SavedESCCardToken.class)))
            .thenReturn(new StubSuccessMpCall<>(token));
        presenter.setCard(null);
        presenter.setToken(token);
        presenter.saveSecurityCode(DUMMY_CVV);
        presenter.validateSecurityCodeInput();

        mockClearCap(token.getCardId());

        verify(view).showLoadingView();
        verify(view).clearErrorView();
        verify(view).finishWithResult();
        verifyNoMoreInteractions(view);
    }

    private void mockClearCap(@NonNull final String cardId) {
        final ArgumentCaptor<CardTokenRepository.ClearCapCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(
            CardTokenRepository.ClearCapCallback.class);
        verify(cardTokenRepository).clearCap(eq(cardId), callbackArgumentCaptor.capture());
        final CardTokenRepository.ClearCapCallback value = callbackArgumentCaptor.getValue();
        value.execute();
    }

// --------- Helper methods ----------- //

    private SecurityCodePresenter getPresenter() {
        final SecurityCodePresenter presenter = getBasePresenter(view);

        presenter.setCard(card);
        presenter.setToken(stubToken);
        presenter.setPaymentRecovery(paymentRecovery);
        presenter.setPaymentMethod(stubPaymentMethod);
        presenter.setCardInfo(cardInfo);
        presenter.setReason(reason);

        return presenter;
    }

    private SecurityCodePresenter getBasePresenter(final SecurityCodeActivityView view) {
        final SecurityCodePresenter presenter = new SecurityCodePresenter(paymentSettingRepository, cardTokenRepository,
            escManagerBehaviour);
        presenter.attachView(view);
        return presenter;
    }
}