package com.mercadopago.android.px.internal.features.express.slider

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mercadopago.android.px.internal.features.express.RenderMode
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodFragmentDrawer

class PaymentMethodFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private var items: List<DrawableFragmentItem> = emptyList()
    private var drawer: PaymentMethodFragmentDrawer = PaymentMethodHighResDrawer()
    private var renderMode = RenderMode.HIGH_RES
    private var currentInstallment = 0

    fun setItems(items: List<DrawableFragmentItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun createFragment(position: Int): Fragment = items[position].draw(drawer)

    /*override fun setPrimaryItem(container: ViewGroup, position: Int, item: Any) {
        if (item is ConsumerCreditsFragment) {
            item.updateInstallment(currentInstallment)
        }
        super.setPrimaryItem(container, position, item)
    }*/

    fun updateInstallment(installmentSelected: Int) {
        currentInstallment = installmentSelected
    }

    override fun getItemCount() = items.size

    fun setRenderMode(renderMode: RenderMode) {
        if (this.renderMode != renderMode && renderMode == RenderMode.LOW_RES) {
            this.renderMode = renderMode
            drawer = PaymentMethodLowResDrawer()
            notifyDataSetChanged()
        }
    }
}
