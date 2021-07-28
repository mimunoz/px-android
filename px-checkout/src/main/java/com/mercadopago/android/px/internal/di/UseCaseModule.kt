package com.mercadopago.android.px.internal.di

import com.mercadopago.android.px.addons.BehaviourProvider
import com.mercadopago.android.px.internal.audio.PlaySoundUseCase
import com.mercadopago.android.px.internal.base.use_case.TokenizeUseCase
import com.mercadopago.android.px.internal.domain.CheckoutUseCase
import com.mercadopago.android.px.internal.domain.CheckoutWithNewCardUseCase
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.DisplayDataUseCase
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.SecurityTrackModelUseCase
import com.mercadopago.android.px.internal.features.validation_program.AuthenticateUseCase
import com.mercadopago.android.px.internal.features.validation_program.ValidationProgramUseCase

internal class UseCaseModule(
    private val configurationModule: CheckoutConfigurationModule,
    private val mapperProvider: MapperProvider
) {

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
                mapperProvider.fromSecurityCodeDisplayDataToBusinessSecurityCodeDisplayData,
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
                configurationModule.applicationSelectionRepository,
                authenticateUseCase,
                session.tracker
            )
        }

    private val authenticateUseCase: AuthenticateUseCase
        get() {
            val session = Session.getInstance()
            return AuthenticateUseCase(
                session.tracker,
                BehaviourProvider.getThreeDSBehaviour(),
                session.cardHolderAuthenticationRepository
            )
        }

    val playSoundUseCase: PlaySoundUseCase
        get() {
            val session = Session.getInstance()
            return PlaySoundUseCase(session.tracker, configurationModule.paymentSettings, session.audioPlayer)
        }

    val checkoutUseCase: CheckoutUseCase
        get() {
            val session = Session.getInstance()
            return CheckoutUseCase(session.checkoutRepository, session.tracker)
        }

    val checkoutWithNewCardUseCase: CheckoutWithNewCardUseCase
        get() {
            val session = Session.getInstance()
            return CheckoutWithNewCardUseCase(session.checkoutRepository, session.tracker)
        }
}
