package com.mercadopago.android.px.internal.audio

import android.content.Context

internal class AudioPlayerMuted : AudioPlayer {

    override fun play(context: Context, sound: AudioPlayer.Sound) {
        //Does nothing
    }
}
