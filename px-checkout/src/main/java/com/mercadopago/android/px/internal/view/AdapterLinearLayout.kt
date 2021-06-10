package com.mercadopago.android.px.internal.view

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.widget.Adapter
import androidx.appcompat.widget.LinearLayoutCompat

internal class AdapterLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private var adapter: Adapter? = null
    private val dataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            reloadChildViews()
        }
    }

    fun setAdapter(adapter: Adapter?) {
        if (this.adapter === adapter) {
            return
        }
        this.adapter = adapter
        adapter?.registerDataSetObserver(dataSetObserver)
        reloadChildViews()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        adapter?.unregisterDataSetObserver(dataSetObserver)
    }

    private fun reloadChildViews() {
        removeAllViews()
        adapter?.let {
            for (position in 0 until it.count) {
                it.getView(position, null, this)?.let { view ->
                    addView(view)
                }
            }
        }
        requestLayout()
    }
}
