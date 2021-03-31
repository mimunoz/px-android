package com.mercadopago.android.px.internal.audio

import android.content.Context
import com.mercadopago.android.px.R

internal interface AudioPlayer {

    fun play(context: Context, sound: Sound)

    enum class Sound(val id: Int) {
        SUCCESS(R.raw.congrats_success),
        FAILURE(R.raw.congrats_failure)
    }
}
