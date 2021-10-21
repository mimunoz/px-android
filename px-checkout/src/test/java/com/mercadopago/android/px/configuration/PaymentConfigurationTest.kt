package com.mercadopago.android.px.configuration

import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.core.ScheduledPaymentProcessor
import com.mercadopago.android.px.core.SplitPaymentProcessor
import com.mercadopago.android.px.core.internal.NoOpPaymentProcessor
import com.mercadopago.android.px.model.internal.CheckoutType
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PaymentConfigurationTest {

    @Mock
    private lateinit var splitPaymentProcessor: SplitPaymentProcessor

    @Mock
    private lateinit var paymentProcessorV2: ScheduledPaymentProcessor

    @Test
    fun v1ConstructorShouldSetBothPaymentProcessors() {
        val paymentConfiguration = PaymentConfiguration.Builder(splitPaymentProcessor).build()
        paymentConfiguration.paymentProcessor.assertEquals(splitPaymentProcessor)
        paymentConfiguration.paymentProcessorV2.assertEquals(splitPaymentProcessor)
    }

    @Test
    fun v2ConstructorShouldSetNoOpPaymentProcessorOnV1() {
        val paymentConfiguration = PaymentConfiguration.Builder(paymentProcessorV2).build()
        assertTrue(paymentConfiguration.paymentProcessor is NoOpPaymentProcessor)
        paymentConfiguration.paymentProcessorV2.assertEquals(paymentProcessorV2)
        paymentConfiguration.paymentProcessorV2.assertEquals(paymentProcessorV2)
    }

    @Test
    fun getCheckoutTypeShouldReturnRegularWhenItIsSplitPaymentProcessor() {
        val paymentConfiguration = PaymentConfiguration.Builder(splitPaymentProcessor).build()
        paymentConfiguration.getCheckoutType().assertEquals(CheckoutType.REGULAR)
    }

    @Test
    fun getCheckoutTypeShouldReturnScheduledWhenItIsSplitPaymentProcessor() {
        val paymentConfiguration = PaymentConfiguration.Builder(paymentProcessorV2).build()
        paymentConfiguration.getCheckoutType().assertEquals(CheckoutType.SCHEDULED)
    }
}