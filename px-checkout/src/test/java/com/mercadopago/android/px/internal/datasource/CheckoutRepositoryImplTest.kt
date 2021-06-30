package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.TestContextProvider
import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.configuration.DiscountParamsConfiguration
import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.internal.adapters.NetworkApi
import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.features.FeatureProvider
import com.mercadopago.android.px.internal.mappers.OneTapItemToDisabledPaymentMethodMapper
import com.mercadopago.android.px.internal.repository.*
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.mocks.CheckoutResponseStub
import com.mercadopago.android.px.model.CardMetadata
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.Site
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule.Companion.createChargeFreeRule
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.internal.*
import com.mercadopago.android.px.preferences.CheckoutPreference
import com.mercadopago.android.px.tracking.internal.MPTracker
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import retrofit2.Response
import retrofit2.Retrofit
import java.math.BigDecimal
import java.util.*
import kotlin.coroutines.CoroutineContext

@RunWith(MockitoJUnitRunner::class)
class CheckoutRepositoryImplTest {

    @Spy
    private lateinit var checkoutRepository: CheckoutRepository

    @Mock
    private lateinit var paymentSettingRepository: PaymentSettingRepository

    @Mock
    private lateinit var paymentConfiguration: PaymentConfiguration

    @Mock
    private lateinit var experimentsRepository: ExperimentsRepository

    @Mock
    private lateinit var disabledPaymentMethodRepository: DisabledPaymentMethodRepository

    @Mock
    private lateinit var escManagerBehaviour: ESCManagerBehaviour

    @Mock
    private lateinit var networkApi: NetworkApi

    @Mock
    private lateinit var trackingRepository: TrackingRepository

    @Mock
    private lateinit var tracker: MPTracker

    @Mock
    private lateinit var payerPaymentMethodRepository: PayerPaymentMethodRepository

    @Mock
    private lateinit var oneTapItemRepository: OneTapItemRepository

    @Mock
    private lateinit var paymentMethodRepository: PaymentMethodRepository

    @Mock
    private lateinit var modalRepository: ModalRepository

    @Mock
    private lateinit var payerComplianceRepository: PayerComplianceRepository

    @Mock
    private lateinit var amountConfigurationRepository: AmountConfigurationRepository

    @Mock
    private lateinit var discountRepository: DiscountRepository

    @Mock
    private lateinit var featureProvider: FeatureProvider

    @Mock
    private lateinit var splitPaymentProcessor: SplitPaymentProcessor

    @Mock
    private lateinit var cardOneTapItem: OneTapItem

    @Mock
    private lateinit var accountMoneyOneTapItem: OneTapItem

    @Mock
    private lateinit var checkoutResponse: CheckoutResponse

    private val accountMoneyCustomOptionId = "account_money"

    @Before
    fun setUp() {
        whenever(paymentSettingRepository.publicKey).thenReturn("987654321")
        whenever(paymentSettingRepository.checkoutPreferenceId).thenReturn("123456789")
        whenever(paymentSettingRepository.checkoutPreference).thenReturn(
            Mockito.mock(
                CheckoutPreference::class.java
            )
        )

        val chargeRules = ArrayList<PaymentTypeChargeRule>()
        chargeRules.add(PaymentTypeChargeRule(PaymentTypes.DIGITAL_CURRENCY, BigDecimal.TEN))
        chargeRules.add(createChargeFreeRule(PaymentTypes.ACCOUNT_MONEY, "account money"))

        val label: Set<String> = emptySet<String>()
        val para = emptyMap<String, String>()
        val advancedConfiguration = AdvancedConfiguration.Builder()
            .setDiscountParamsConfiguration(
                DiscountParamsConfiguration.Builder()
                    .setProductId("")
                    .addAdditionalParams(para)
                    .setLabels(label).build()
            )
            .build()

        paymentConfiguration = PaymentConfiguration.Builder(
            splitPaymentProcessor
        ).addChargeRules(chargeRules)
            .build()

        whenever(paymentSettingRepository.paymentConfiguration).thenReturn(paymentConfiguration)
        whenever(paymentSettingRepository.advancedConfiguration).thenReturn(advancedConfiguration)

        whenever(featureProvider.availableFeatures).thenReturn(CheckoutFeatures.Builder().build())

        checkoutRepository = CheckoutRepositoryImpl(
            paymentSettingRepository,
            experimentsRepository,
            disabledPaymentMethodRepository,
            escManagerBehaviour,
            networkApi,
            trackingRepository,
            tracker,
            payerPaymentMethodRepository,
            oneTapItemRepository,
            paymentMethodRepository,
            modalRepository,
            payerComplianceRepository,
            amountConfigurationRepository,
            discountRepository,
            featureProvider
        )
    }

    @Test
    fun testCheckoutWithPreferenceIdResultSuccess() {
        runBlocking {
            val checkoutResponse = CheckoutResponseStub.FULL.get()
            val apiResponse = ApiResponse.Success(checkoutResponse)
            val captor =
                argumentCaptor<suspend (api: CheckoutService) -> Response<CheckoutResponse>>()
            whenever(networkApi.apiCallForResponse(any(), captor.capture())).thenReturn(apiResponse)
            val response = checkoutRepository.checkout()
            assertTrue(ReflectionEquals(apiResponse).matches(response))
        }
    }

    @Test
    fun testCheckoutWithPreferenceIdResultFailure() {
        val apiExceptionMsg = "test message"
        val apiException = ApiException().apply { message = apiExceptionMsg }

        runBlocking {
            val apiResponse = ApiResponse.Failure(apiException)
            val captor =
                argumentCaptor<suspend (api: CheckoutService) -> Response<CheckoutResponse>>()
            whenever(networkApi.apiCallForResponse(any(), captor.capture())).thenReturn(apiResponse)
            val response = checkoutRepository.checkout()
            assertTrue(ReflectionEquals(apiResponse).matches(response))
        }
    }

    @Test
    fun testCheckoutWithoutPreferenceIdResultSuccess() {
        runBlocking {
            val checkoutResponse = CheckoutResponseStub.FULL.get()
            val apiResponse = ApiResponse.Success(checkoutResponse)
            val captor =
                argumentCaptor<suspend (api: CheckoutService) -> Response<CheckoutResponse>>()
            whenever(networkApi.apiCallForResponse(any(), captor.capture())).thenReturn(apiResponse)
            val response = checkoutRepository.checkout()
            assertTrue(ReflectionEquals(apiResponse).matches(response))
        }
    }

    @Test
    fun testCheckoutWithoutPreferenceIdFailure() {
        val apiExceptionMsg = "test message"
        val apiException = ApiException().apply { message = apiExceptionMsg }

        runBlocking {
            val apiResponse = ApiResponse.Failure(apiException)
            val captor =
                argumentCaptor<suspend (api: CheckoutService) -> Response<CheckoutResponse>>()
            whenever(networkApi.apiCallForResponse(any(), captor.capture())).thenReturn(apiResponse)
            val response = checkoutRepository.checkout()
            assertTrue(ReflectionEquals(apiResponse).matches(response))
        }
    }

    @Test
    fun testConfigureWithoutPreferenceId() {
        runBlocking {
            checkoutRepository.configure(mock())

            verify(paymentSettingRepository).configure(checkoutResponse.site)
            verify(paymentSettingRepository).configure(checkoutResponse.currency)
            verify(paymentSettingRepository).configure(checkoutResponse.configuration)
            verify(experimentsRepository).configure(checkoutResponse.experiments)
            verify(payerPaymentMethodRepository).configure(checkoutResponse.payerPaymentMethods)
            verify(oneTapItemRepository).configure(checkoutResponse.oneTapItems)
            verify(paymentMethodRepository).configure(checkoutResponse.availablePaymentMethods)
            verify(modalRepository).configure(checkoutResponse.modals)
            verify(payerComplianceRepository).configure(checkoutResponse.payerCompliance)
            verify(amountConfigurationRepository).configure(checkoutResponse.defaultAmountConfiguration)
            verify(discountRepository).configure(checkoutResponse.discountsConfigurations)
            verify(disabledPaymentMethodRepository).configure(OneTapItemToDisabledPaymentMethodMapper().map(checkoutResponse.oneTapItems))
            verify(tracker).setExperiments(experimentsRepository.experiments)
        }
    }

    @Test
    fun testConfigureWithPreferenceId() {
        runBlocking {
            whenever(checkoutResponse.preference).thenReturn(Mockito.mock(CheckoutPreference::class.java))
            checkoutRepository.configure(checkoutResponse)

            verify(paymentSettingRepository).configure(checkoutResponse.preference)
            verify(paymentSettingRepository).configure(checkoutResponse.site)
            verify(paymentSettingRepository).configure(checkoutResponse.currency)
            verify(paymentSettingRepository).configure(checkoutResponse.configuration)
            verify(experimentsRepository).configure(checkoutResponse.experiments)
            verify(payerPaymentMethodRepository).configure(checkoutResponse.payerPaymentMethods)
            verify(oneTapItemRepository).configure(checkoutResponse.oneTapItems)
            verify(paymentMethodRepository).configure(checkoutResponse.availablePaymentMethods)
            verify(modalRepository).configure(checkoutResponse.modals)
            verify(payerComplianceRepository).configure(checkoutResponse.payerCompliance)
            verify(amountConfigurationRepository).configure(checkoutResponse.defaultAmountConfiguration)
            verify(discountRepository).configure(checkoutResponse.discountsConfigurations)
            verify(disabledPaymentMethodRepository).configure(OneTapItemToDisabledPaymentMethodMapper().map(checkoutResponse.oneTapItems))
            verify(tracker).setExperiments(experimentsRepository.experiments)
        }
    }

    @Test
    fun testSortByPrioritizedCardId() {
        val accountMoneyPaymentMethod: Application.PaymentMethod = mock {
            on { id }.thenReturn(accountMoneyCustomOptionId)
            on { type }.thenReturn(accountMoneyCustomOptionId)
        }

        val accountMoneyApplication: Application = mock {
            on { this.paymentMethod }.thenReturn(accountMoneyPaymentMethod)
        }

        whenever(accountMoneyOneTapItem.getApplications()).thenReturn(listOf(accountMoneyApplication))

        val card: CardMetadata = mock {
            on { id }.thenReturn("master")
        }

        val disablePaymentMethod: DisabledPaymentMethod = mock()

        whenever(disabledPaymentMethodRepository.value).thenReturn(mutableMapOf(
            PayerPaymentMethodKey("master", "credit_card") to disablePaymentMethod
        ))

        val disableItem: OneTapItem = mock {
            on { isCard }.thenReturn(true)
            on { this.card }.thenReturn(card)
        }

        val actual = mutableListOf(accountMoneyOneTapItem, cardOneTapItem, disableItem)

        runBlocking {
            checkoutRepository.sortByPrioritizedCardId(actual, card.id)
            assertTrue(actual.first().card.id == card.id)
        }
    }
}

