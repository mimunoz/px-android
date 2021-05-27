package com.mercadopago.android.px.internal.audio

import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.tracking.internal.MPTracker

internal class PlaySoundUseCase(
    tracker: MPTracker,
    private val paymentSettingRepository: PaymentSettingRepository,
    private val audioPlayer: AudioPlayer,
    override val contextProvider: CoroutineContextProvider = CoroutineContextProvider())
    : UseCase<AudioPlayer.Sound, Unit>(tracker) {

    override suspend fun doExecute(param: AudioPlayer.Sound): Response<Unit, MercadoPagoError> {
        if (paymentSettingRepository.configuration.sonicBrandingEnabled()) {
            audioPlayer.play(param)
        }
        return Response.Success(Unit)
    }
}