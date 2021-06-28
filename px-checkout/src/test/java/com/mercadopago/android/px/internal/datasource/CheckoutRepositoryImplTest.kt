package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.configuration.DiscountParamsConfiguration
import com.mercadopago.android.px.configuration.PaymentConfiguration
import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.internal.adapters.NetworkApi
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.features.FeatureProvider
import com.mercadopago.android.px.internal.repository.*
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.mocks.CheckoutResponseStub
import com.mercadopago.android.px.model.ApiExceptionTest
import com.mercadopago.android.px.model.CardMetadata
import com.mercadopago.android.px.model.PaymentTypes
import com.mercadopago.android.px.model.Site
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule.Companion.createChargeFreeRule
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.CheckoutFeatures
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.preferences.CheckoutPreference
import com.mercadopago.android.px.tracking.internal.MPTracker
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
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
import java.math.BigDecimal
import java.util.*

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
//
//    @Mock
//    private lateinit var configurationProvider: ConfigurationProvider
//
//    @Mock
//    private lateinit var tokenDeviceBehaviour: TokenDeviceBehaviour
//
//    @Mock
//    private lateinit var checkoutService: CheckoutService

    @Mock
    private lateinit var checkoutResponse: CheckoutResponse

    @Before
    fun setUp() {
//        Dispatchers.setMain(testDispatcher)
        val site: Site = mock {
            on { id }.thenReturn("MLA")
        }
        whenever(paymentSettingRepository.currency).thenReturn(mock())
        whenever(paymentSettingRepository.site).thenReturn(site)
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
    fun testCheckout() {
        runBlocking {
            val checkoutResponse = CheckoutResponseStub.FULL.get()
            val apiResponse = ApiResponse.Success(checkoutResponse)
            val captor = argumentCaptor<suspend (api: CheckoutService) -> Response<CheckoutResponse>>()
            whenever(networkApi.apiCallForResponse(any(), captor.capture())).thenReturn(apiResponse)
            val response = checkoutRepository.checkout()
            assertTrue(ReflectionEquals(apiResponse).matches(response))
        }
    }

    @Test
    fun testCheckoutWithoutPreferenceId() {
        runBlocking {
            whenever(paymentSettingRepository.checkoutPreferenceId).thenReturn(null)
            whenever(networkApi.apiCallForResponse(CheckoutService::class.java){
                it.checkout(paymentSettingRepository.checkoutPreferenceId, paymentSettingRepository.privateKey, any())
            }).thenReturn(mock())
            checkoutRepository.checkout()
        }
    }

    @Test
    fun testConfigureWithoutPreferenceId() {
        runBlocking {
            checkoutRepository.configure(mock())
        }
    }

    @Test
    fun testConfigure() {
        runBlocking {
            whenever(checkoutResponse.preference).thenReturn(Mockito.mock(CheckoutPreference::class.java))
            checkoutRepository.configure(checkoutResponse)
        }
    }

    @Test
    fun testSortByPrioritizedCardId() {
        var oneTapItems: List<OneTapItem> = listOf(accountMoneyOneTapItem, cardOneTapItem)
        val cardCustomOptionId = "visa"
        val accountMoneyCustomOptionId = "account_money"

        val card: CardMetadata = mock {
            on { id }.thenReturn(cardCustomOptionId)
        }

        val cardPaymentMethod: Application.PaymentMethod = mock {
            on { type }.thenReturn("credit_card")
        }

        val cardApplication: Application = mock {
            on { this.paymentMethod }.thenReturn(cardPaymentMethod)
        }

        val accountMoneyPaymentMethod: Application.PaymentMethod = mock {
            on { id }.thenReturn(accountMoneyCustomOptionId)
            on { type }.thenReturn(accountMoneyCustomOptionId)
        }

        val accountMoneyApplication: Application = mock {
            on { this.paymentMethod }.thenReturn(accountMoneyPaymentMethod)
        }

        whenever(cardOneTapItem.isCard).thenReturn(true)
        whenever(cardOneTapItem.card).thenReturn(card)
        whenever(cardOneTapItem.getApplications()).thenReturn(listOf(cardApplication))
        whenever(accountMoneyOneTapItem.paymentMethodId).thenReturn(accountMoneyCustomOptionId)
        whenever(accountMoneyOneTapItem.getApplications()).thenReturn(listOf(accountMoneyApplication))

        runBlocking {
            checkoutRepository.sortByPrioritizedCardId(oneTapItems, "1234567890")
        }
    }
}
