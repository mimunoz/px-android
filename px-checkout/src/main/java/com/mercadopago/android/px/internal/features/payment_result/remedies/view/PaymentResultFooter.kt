package com.mercadopago.android.px.internal.features.payment_result.remedies.view

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.mercadolibre.android.ui.widgets.MeliButton
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemedyButton

internal class PaymentResultFooter(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr) {

    init {
        configureView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private lateinit var loudButton: MeliButton
    private lateinit var quietButton: MeliButton
    private lateinit var payButtonContainer: View

    private fun configureView(context: Context) {
        orientation = VERTICAL
        inflate(context, R.layout.px_payment_result_footer, this)
        loudButton = findViewById(R.id.action_loud)
        quietButton = findViewById<MeliButton>(R.id.action_quiet).apply {
            background = ContextCompat.getDrawable(context, R.drawable.px_quiet_button_selector)
            text = resources.getString(R.string.px_change_payment)
        }
        payButtonContainer = findViewById(R.id.pay_button)
    }

    fun init(model: Model, listener: Listener) {
        applyButtonConfig(loudButton, model.loudButton, listener::onLoudButtonClicked)
        applyButtonConfig(quietButton, model.quietButton, listener::onQuietButtonClicked)
        payButtonContainer.visibility = if (model.showPayButton) View.VISIBLE else View.GONE
    }

    fun hideQuietButton() {
        quietButton.visibility = View.GONE
    }

    fun showQuietButton() {
        quietButton.visibility = View.VISIBLE
    }

    private fun applyButtonConfig(button: MeliButton, buttonModel: RemedyButton?,
        listener: (action: RemedyButton.Action) -> Unit) {
        with(button) {
            buttonModel?.let {model ->
                visibility = View.VISIBLE
                text = model.label.get(context)
                setOnClickListener { listener.invoke(model.action) }
            } ?: apply { visibility = View.GONE }
        }
    }

    interface Listener {
        fun onLoudButtonClicked(action: RemedyButton.Action)
        fun onQuietButtonClicked(action: RemedyButton.Action)
    }

    internal data class Model(val loudButton: RemedyButton?, val quietButton: RemedyButton?,
        val showPayButton: Boolean = true) : Parcelable {
        constructor(parcel: Parcel) : this(parcel.readParcelable(RemedyButton::class.java.classLoader),
            parcel.readParcelable(RemedyButton::class.java.classLoader))

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeParcelable(loudButton, flags)
            parcel.writeParcelable(quietButton, flags)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<Model> {
            override fun createFromParcel(parcel: Parcel) = Model(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Model>(size)
        }
    }
}