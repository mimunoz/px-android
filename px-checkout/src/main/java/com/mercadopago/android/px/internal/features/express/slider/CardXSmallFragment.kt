package com.mercadopago.android.px.internal.features.express.slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem

internal class CardXSmallFragment : CardFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_fragment_card_xsmall_res, container, false)
    }

    companion object {
        @JvmStatic
        fun getInstance(model: DrawableFragmentItem) = CardXSmallFragment().also {
            it.storeModel(model)
        }
    }
}