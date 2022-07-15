package com.mercadopago.android.px.internal.features.express.slider;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.view.PaymentMethodDescriptorView;
import com.mercadopago.android.px.internal.view.PaymentMethodHeaderView;
import com.mercadopago.android.px.internal.viewmodel.DisabledPaymentMethodDescriptorModel;
import com.mercadopago.android.px.internal.viewmodel.GoingToModel;
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState;
import java.util.List;

public class PaymentMethodHeaderAdapter
    extends HubableAdapter<List<PaymentMethodDescriptorView.Model>, PaymentMethodHeaderView> {

    private static final int NO_SELECTED = -1;

    private int currentIndex = NO_SELECTED;

    public PaymentMethodHeaderAdapter(@NonNull final PaymentMethodHeaderView view) {
        super(view);
    }

    @Override
    public void showInstallmentsList() {
        view.showInstallmentsListTitle();
    }

    @Override
    public void updateData(final int currentIndex, final int payerCostSelected,
        @NonNull final SplitSelectionState splitSelectionState) {
        this.currentIndex = currentIndex;
        final PaymentMethodDescriptorView.Model currentModel = data.get(currentIndex);
        view.updateData(currentModel.hasPayerCostList(), currentModel instanceof DisabledPaymentMethodDescriptorModel);
    }

    @Override
    public void updatePosition(final float positionOffset, final int position) {
        final GoingToModel goingTo = position < currentIndex ? GoingToModel.BACKWARDS : GoingToModel.FORWARD;
        final int nextIndex = goingTo == GoingToModel.BACKWARDS ? currentIndex - 1 : currentIndex + 1;
        if (nextIndex >= 0 && nextIndex < data.size()) {
            final PaymentMethodDescriptorView.Model currentModel = data.get(currentIndex);
            final PaymentMethodDescriptorView.Model nextModel = data.get(nextIndex);
            final PaymentMethodHeaderView.Model viewModel =
                new PaymentMethodHeaderView.Model(goingTo, currentModel.hasPayerCostList(),
                    nextModel.hasPayerCostList());
            view.trackPagerPosition(positionOffset, viewModel);
        }
    }

    @Override
    public List<PaymentMethodDescriptorView.Model> getNewModels(final HubAdapter.Model model) {
        return model.paymentMethodDescriptorModels;
    }
}