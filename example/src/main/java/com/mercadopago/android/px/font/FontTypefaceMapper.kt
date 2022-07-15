package com.mercadopago.android.px.font

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.mercadolibre.android.ui.font.Font

abstract class FontTypefaceMapper {

    fun getTypeface(context: Context, font: Font): Typeface? {
        return ResourcesCompat.getFont(context, getFontResource(font))
    }

    @FontRes
    abstract fun getFontResource(font: Font): Int
}