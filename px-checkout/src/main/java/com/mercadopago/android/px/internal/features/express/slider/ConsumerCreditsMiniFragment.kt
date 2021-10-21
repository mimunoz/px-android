package com.mercadopago.android.px.internal.features.express.slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.viewmodel.drawables.ConsumerCreditsDrawableFragmentItem

internal class ConsumerCreditsMiniFragment : ConsumerCreditsFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.px_fragment_consumer_credits_mini, container, false)
    }

    companion object {
        @JvmStatic
        fun getInstance(model: ConsumerCreditsDrawableFragmentItem) = ConsumerCreditsMiniFragment().also {
            it.storeModel(model)
        }
    }
}