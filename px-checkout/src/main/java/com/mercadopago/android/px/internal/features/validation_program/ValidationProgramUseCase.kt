package com.mercadopago.android.px.internal.features.validation_program

import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.Application.KnownValidationProgram
import com.mercadopago.android.px.tracking.internal.MPTracker
import java.util.*

internal class ValidationProgramUseCase @JvmOverloads constructor(
    tracker: MPTracker,
    private val authenticateUseCase: AuthenticateUseCase,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider()
) : UseCase<List<PaymentData>?, String?>(tracker) {

    override suspend fun doExecute(param: List<PaymentData>?): Response<String?, MercadoPagoError> {
        val mainPaymentData = param?.firstOrNull() ?: throw IllegalStateException("No payment data available")
        val payerPaymentMethodId = mainPaymentData.token?.cardId ?: mainPaymentData.paymentMethod.id
        val validationProgram = KnownValidationProgram[payerPaymentMethodId]
        when (validationProgram) {
            KnownValidationProgram.STP -> authenticateUseCase.execute(mainPaymentData)
        }
        return Response.Success(validationProgram?.toString()?.toLowerCase(Locale.ROOT))
    }
}