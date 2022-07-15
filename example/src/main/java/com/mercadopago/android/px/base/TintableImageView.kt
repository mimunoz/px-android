package com.mercadopago.android.px.base

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class TintableImageView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatImageView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private var tint: ColorStateList? = null

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (tint?.isStateful == true) updateTintColor()
    }

    fun setColorFilter(tint: ColorStateList) {
        this.tint = tint
        super.setColorFilter(tint.getColorForState(drawableState, 0))
    }

    private fun updateTintColor() {
        val color: Int = tint?.getColorForState(drawableState, 0) ?: 0
        setColorFilter(color)
    }
}