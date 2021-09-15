package com.mercadopago.android.px.internal.features.express.slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem

internal class CardMiniResFragment : CardFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_fragment_card_mini_res, container, false)
    }

    companion object {
        @JvmStatic
        fun getInstance(model: DrawableFragmentItem) = CardMiniResFragment().also {
            it.storeModel(model)
        }
    }
}