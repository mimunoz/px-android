package com.mercadopago.android.px.internal.audio

import android.content.Context
import android.media.MediaPlayer

internal class AudioPlayerDefault : AudioPlayer {

    override fun play(context: Context, sound: AudioPlayer.Sound) {
        MediaPlayer.create(context, sound.id).start()
    }
}
