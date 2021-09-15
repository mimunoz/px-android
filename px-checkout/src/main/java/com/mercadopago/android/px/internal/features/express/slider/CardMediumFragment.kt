package com.mercadopago.android.px.internal.features.express.slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem

internal class CardMediumFragment : CardFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_fragment_card_medium, container, false)
    }

    companion object {
        @JvmStatic
        fun getInstance(model: DrawableFragmentItem) = CardMediumFragment().also {
            it.storeModel(model)
        }
    }
}