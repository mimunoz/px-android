package com.mercadopago.android.px.internal.features.express.slider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.meli.android.carddrawer.model.CardDrawerView
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.extensions.isNotNull
import com.mercadopago.android.px.internal.viewmodel.drawables.AccountMoneyDrawableFragmentItem

internal open class AccountMoneyFragment : PaymentMethodFragment<AccountMoneyDrawableFragmentItem>() {

    private lateinit var cardView: CardDrawerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayout(), container, false)
        cardView = view.findViewById(R.id.card)
        with(model) {
            when {
                cardConfiguration.isNotNull() -> cardView.show(cardConfiguration)
                cardStyle.isNotNull() -> cardView.setStyle(cardStyle)
            }
        }
        return view
    }

    protected open fun getLayout() = R.layout.px_fragment_account_money

    override fun disable() {
        super.disable()
        cardView.isEnabled = false
    }

    override fun enable() {
        super.enable()
        cardView.isEnabled = true
    }

    override fun getAccessibilityContentDescription() = model.description

    companion object {
        @JvmStatic fun getInstance(model: AccountMoneyDrawableFragmentItem): Fragment {
            return AccountMoneyFragment().also {
                it.storeModel(model)
            }
        }
    }
}
