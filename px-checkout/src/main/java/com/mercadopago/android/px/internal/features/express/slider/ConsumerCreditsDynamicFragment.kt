package com.mercadopago.android.px.internal.features.express.slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.view.LinkableTextView
import com.mercadopago.android.px.internal.viewmodel.drawables.ConsumerCreditsDrawableFragmentItem
import com.mercadopago.android.px.model.ConsumerCreditsDisplayInfo

class ConsumerCreditsDynamicFragment : ConsumerCreditsFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.px_fragment_consumer_credits_dynamic, container, false)
    }

    companion object {
        fun getInstance(model: ConsumerCreditsDrawableFragmentItem) = ConsumerCreditsDynamicFragment().also {
            it.storeModel(model)
        }
    }
}