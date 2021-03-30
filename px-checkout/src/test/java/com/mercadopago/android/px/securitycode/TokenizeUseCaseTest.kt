package com.mercadopago.android.px.securitycode

import com.mercadopago.android.px.CallbackTest
import com.mercadopago.android.px.TestContextProvider
import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.internal.base.use_case.TokenizeParams
import com.mercadopago.android.px.internal.base.use_case.TokenizeUseCase
import com.mercadopago.android.px.internal.callbacks.MPCall
import com.mercadopago.android.px.internal.callbacks.TaggedCallback
import com.mercadopago.android.px.internal.repository.CardTokenRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.model.*
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class TokenizeUseCaseTest {

    @Mock
    private lateinit var cardTokenRepository: CardTokenRepository

    @Mock
    private lateinit var escManagerBehaviour: ESCManagerBehaviour

    @Mock
    private lateinit var settingRepository: PaymentSettingRepository

    @Mock
    private lateinit var success: CallbackTest<Token>

    @Mock
    private lateinit var failure: CallbackTest<MercadoPagoError>

    @Mock
    private lateinit var mpCallCreateToken: MPCall<Token>
    private lateinit var tokenizeUseCase: TokenizeUseCase
    private lateinit var contextProvider: TestContextProvider

    @Before
    fun setUp() {
        contextProvider = TestContextProvider()
        tokenizeUseCase = TokenizeUseCase(
            cardTokenRepository,
            escManagerBehaviour,
            settingRepository,
            mock(),
            contextProvider)
    }

    @Test
    fun whenIsSecurityCodeAndSuccess() {
        val cardMock = mock<Card> {
            on { paymentMethod }.thenReturn(mock())
        }
        val params = TokenizeParams("123", cardMock)
        val tokenMock = mock<Token>()
        val captor = argumentCaptor<TaggedCallback<Token>>()
        whenever(cardTokenRepository.createToken(any<SavedCardToken>())).thenReturn(mpCallCreateToken)

        tokenizeUseCase.execute(params, success::invoke, failure::invoke)

        verify(mpCallCreateToken).enqueue(captor.capture())
        captor.firstValue.onSuccess(tokenMock)
        verify(settingRepository).configure(tokenMock)
        verify(success).invoke(tokenMock)
        verifyNoInteractions(failure)
    }

    @Test
    fun whenIsSecurityCodeAndFail() {
        val params = TokenizeParams("123", mock())

        tokenizeUseCase.execute(params, success::invoke, failure::invoke)

        verify(failure).invoke(any())
        verifyNoInteractions(success)
    }

    @Test
    fun whenIsPaymentRecoveryAndSuccess() {
        val cvv = "123"
        val securityCode = mock<SecurityCode> {
            on { length }.thenReturn(cvv.length)
        }
        val cardMock = mock<Card> {
            on { id }.thenReturn("456")
            on { this.securityCode }.thenReturn(securityCode)
        }
        val paymentRecovery = mock<PaymentRecovery> {
            on { card }.thenReturn(cardMock)
            on { paymentMethod }.thenReturn(mock())
        }
        val params = TokenizeParams(cvv, cardMock, paymentRecovery)
        val tokenResultMock = mock<Token>()
        val captor = argumentCaptor<TaggedCallback<Token>>()
        whenever(cardTokenRepository.createToken(any())).thenReturn(mpCallCreateToken)
        whenever(escManagerBehaviour.isESCEnabled).thenReturn(true)

        tokenizeUseCase.execute(params, success::invoke, failure::invoke)

        verify(mpCallCreateToken).enqueue(captor.capture())
        captor.firstValue.onSuccess(tokenResultMock)
        verify(settingRepository).configure(tokenResultMock)
        verify(success).invoke(tokenResultMock)
        verifyNoInteractions(failure)
    }

    @Test
    fun whenIsPaymentRecoveryAndFail() {
        val params = TokenizeParams("123", mock(), mock())

        tokenizeUseCase.execute(params, success::invoke, failure::invoke)

        verify(failure).invoke(any())
        verifyNoInteractions(success)
    }
}
