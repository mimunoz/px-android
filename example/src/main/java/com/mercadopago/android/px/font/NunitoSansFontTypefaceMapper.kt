package com.mercadopago.android.px.font

import androidx.annotation.FontRes
import com.mercadolibre.android.ui.font.Font
import com.mercadopago.example.R

class NunitoSansFontTypefaceMapper: FontTypefaceMapper() {

    @FontRes
    override fun getFontResource(font: Font): Int {
        return when (font) {
            Font.BLACK -> R.font.nunito_sans_black
            Font.BOLD -> R.font.nunito_sans_bold
            Font.EXTRA_BOLD -> R.font.nunito_sans_extrabold
            Font.REGULAR -> R.font.nunito_sans
            Font.SEMI_BOLD -> R.font.nunito_sans_semibold
            else -> R.font.nunito_sans_light
        }
    }
}