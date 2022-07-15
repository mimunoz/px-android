package com.mercadopago.android.px.internal.features.review_and_confirm.components;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.SummaryModel;
import com.mercadopago.android.px.internal.features.uicontrollers.payercosts.PayerCostColumn;
import com.mercadopago.android.px.internal.util.CurrenciesUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.view.Renderer;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.PaymentTypes;
import java.math.BigDecimal;

/**
 * Created by mromar on 2/28/18.
 */

public class FullSummaryRenderer extends Renderer<FullSummary> {

    @Override
    public View render(@NonNull final FullSummary component, @NonNull final Context context, final ViewGroup parent) {
        final View summaryView = inflate(R.layout.px_full_summary_component, parent);
        final MPTextView totalAmountTextView = summaryView.findViewById(R.id.mpsdkReviewSummaryTotalText);
        final FrameLayout payerCostContainer = summaryView.findViewById(R.id.mpsdkReviewSummaryPayerCostContainer);
        final MPTextView disclaimerTextView = summaryView.findViewById(R.id.mpsdkDisclaimer);
        final LinearLayout summaryDetailsContainer = summaryView.findViewById(R.id.mpsdkSummaryDetails);
        final LinearLayout reviewSummaryPayContainer = summaryView.findViewById(R.id.mpsdkReviewSummaryPay);
        final View firstSeparator = summaryView.findViewById(R.id.mpsdkFirstSeparator);

        final LinearLayout disclaimerLinearLayout = summaryView.findViewById(R.id.disclaimer);

        //summaryDetails list
        for (final AmountDescription amountDescription : component.getAmountDescriptionComponents(context)) {
            final Renderer amountDescriptionRenderer = RendererFactory.create(context, amountDescription);
            final View amountView = amountDescriptionRenderer.render();
            summaryDetailsContainer.addView(amountView);
        }

        if (shouldShowPayerCost(component.props.summaryModel)) {
            //payer cost
            final PayerCostColumn payerCostColumn =
                new PayerCostColumn(context,
                    component.props.summaryModel.getSite(),
                    component.props.summaryModel.getCurrency(),
                    component.props.summaryModel.getInstallmentsRate(),
                    component.props.summaryModel.getInstallmentAmount(),
                    component.props.summaryModel.getInstallments());
            payerCostColumn.inflateInParent(payerCostContainer, true);
            payerCostColumn.initializeControls();
            payerCostColumn.drawPayerCostWithoutTotal();
        } else {
            reviewSummaryPayContainer.setVisibility(View.GONE);
            firstSeparator.setVisibility(View.GONE);
        }

        //disclaimer
        if (shouldShowCftDisclaimer(component.props.summaryModel)) {
            final String disclaimer = getDisclaimer(component, context);
            final Renderer disclaimerRenderer =
                RendererFactory.create(context, component.getDisclaimerComponent(disclaimer));
            final View disclaimerView = disclaimerRenderer.render();
            disclaimerLinearLayout.addView(disclaimerView);
        }

        //total
        setText(totalAmountTextView, getFormattedAmount(component.props.summaryModel.getAmountToPay(),
            component.props.summaryModel.getCurrency()));

        //disclaimer
        setText(disclaimerTextView, component.getSummary(context).getDisclaimerText());
        disclaimerTextView.setTextColor(component.getSummary(context).getDisclaimerColor());

        return summaryView;
    }

    @VisibleForTesting
    boolean shouldShowCftDisclaimer(final SummaryModel props) {
        return PaymentTypes.isCreditCardPaymentType(props.getPaymentTypeId())
            && !TextUtil.isEmpty(props.getCftPercent());
    }

    @VisibleForTesting
    boolean shouldShowPayerCost(final SummaryModel props) {
        return PaymentTypes.isCreditCardPaymentType(props.getPaymentTypeId())
            || PaymentTypes.isDigitalCurrency(props.getPaymentTypeId())
            && props.getInstallments() > 1;
    }

    @Nullable
    private Spanned getFormattedAmount(final BigDecimal amount, @NonNull final Currency currency) {
        return amount != null ? CurrenciesUtil.getSpannedAmountWithCurrencySymbol(amount, currency) : null;
    }

    private String getDisclaimer(final FullSummary component, final Context context) {
        return TextUtil.format(context, R.string.px_installments_cft, component.props.summaryModel.getCftPercent());
    }
}