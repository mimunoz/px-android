package com.mercadopago.android.px.internal.di

import com.mercadopago.android.px.internal.base.use_case.TokenizeUseCase
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.DisplayDataUseCase
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.SecurityTrackModelUseCase
import com.mercadopago.android.px.internal.features.security_code.mapper.BusinessSecurityCodeDisplayDataMapper
import com.mercadopago.android.px.internal.features.validation_program.AuthenticateUseCase
import com.mercadopago.android.px.internal.features.validation_program.ValidationProgramUseCase
import com.mercadopago.android.px.internal.util.ThreeDSWrapper

internal class UseCaseModule(val configurationModule: CheckoutConfigurationModule) {

    val tokenizeUseCase: TokenizeUseCase
        get() {
            val session = Session.getInstance()
            return TokenizeUseCase(
                session.cardTokenRepository,
                session.mercadoPagoESC,
                configurationModule.paymentSettings,
                session.tracker
            )
        }

    val displayDataUseCase: DisplayDataUseCase
        get() {
            val session = Session.getInstance()
            return DisplayDataUseCase(
                BusinessSecurityCodeDisplayDataMapper(),
                session.tracker,
                session.oneTapItemRepository)
        }

    val securityTrackModelUseCase: SecurityTrackModelUseCase
        get() {
            val session = Session.getInstance()
            return SecurityTrackModelUseCase(session.tracker)
        }

    val validationProgramUseCase: ValidationProgramUseCase
        get() {
            val session = Session.getInstance()
            return ValidationProgramUseCase(
                session.tracker,
                authenticateUseCase
            )
        }

    private val authenticateUseCase: AuthenticateUseCase
        get() {
            val session = Session.getInstance()
            return AuthenticateUseCase(
                session.tracker,
                ThreeDSWrapper,
                session.cardHolderAuthenticationRepository
            )
        }
}