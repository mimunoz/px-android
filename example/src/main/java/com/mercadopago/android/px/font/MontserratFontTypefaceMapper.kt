package com.mercadopago.android.px.font

import androidx.annotation.FontRes
import com.mercadolibre.android.ui.font.Font
import com.mercadopago.example.R

class MontserratFontTypefaceMapper: FontTypefaceMapper() {

    @FontRes
    override fun getFontResource(font: Font): Int {
        return when (font) {
            Font.BLACK -> R.font.monserrat_black
            Font.BOLD -> R.font.monserrat_bold
            Font.EXTRA_BOLD -> R.font.monserrat_extrabold
            Font.REGULAR -> R.font.monserrat_regular
            Font.SEMI_BOLD -> R.font.monserrat_semibold
            Font.THIN -> R.font.monserrat_thin
            Font.MEDIUM -> R.font.monserrat_medium
            else -> R.font.monserrat_light
        }
    }
}