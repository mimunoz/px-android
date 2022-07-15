package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.util.DiscountHelper;
import com.mercadopago.android.px.internal.util.textformatter.TextFormatter;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.Reason;
import java.math.BigDecimal;

import static com.mercadopago.android.px.internal.util.TextUtil.isNotEmpty;

public class AmountView extends LinearLayoutCompat {

    /* default */
    @Nullable
    OnClick callback;

    private MPTextView amountDescription;
    private View amountContainer;
    private TextView amountBeforeDiscount;
    private TextView maxCouponAmount;
    private TextView finalAmount;
    private View line;
    private View arrow;
    private View mainContainer;

    public interface OnClick {
        void onDetailClicked(@NonNull final DiscountConfigurationModel discountModel);
    }

    public AmountView(@NonNull final Context context) {
        super(context);
        init();
    }

    public void setOnClickListener(final OnClick callback) {
        this.callback = callback;
    }

    public AmountView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AmountView(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void show(@NonNull final DiscountConfigurationModel discountModel, @NonNull final BigDecimal totalAmount,
        @NonNull final Currency currency) {
        if (!discountModel.isAvailable()) {
            showNotAvailableDiscount(discountModel, totalAmount, currency);
        } else if (discountModel.hasValidDiscount()) {
            showWithDiscount(discountModel, totalAmount, currency);
        } else {
            show(totalAmount, currency);
        }
    }

    private void init() {
        inflate(getContext(), R.layout.px_amount_layout, this);
        mainContainer = findViewById(R.id.main_container);
        line = findViewById(R.id.line);
        amountDescription = findViewById(R.id.amount_description);
        amountBeforeDiscount = findViewById(R.id.amount_before_discount);
        finalAmount = findViewById(R.id.final_amount);
        maxCouponAmount = findViewById(R.id.max_coupon_amount);
        arrow = findViewById(R.id.blue_arrow);
        amountContainer = findViewById(R.id.amount_container);
        amountBeforeDiscount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        configureElevation();
    }

    private void configureElevation() {
        setElevation(getContext().getResources().getDimension(R.dimen.px_xxs_margin));
        line.setVisibility(GONE);
    }

    private void showWithDiscount(@NonNull final DiscountConfigurationModel discountModel,
        @NonNull final BigDecimal totalAmount, @NonNull final Currency currency) {
        showDiscount(discountModel, totalAmount, currency);
        showEffectiveAmount(totalAmount.subtract(discountModel.getDiscount().getCouponAmount()), currency);
    }

    private void showNotAvailableDiscount(@NonNull final DiscountConfigurationModel discountModel,
        @NonNull final BigDecimal totalAmount, @NonNull final Currency currency) {
        configureViewsVisibilityWhenNotAvailableDiscount(discountModel);
        final Reason reason = discountModel.getReason();
        amountDescription.setTextColor(getResources().getColor(R.color.px_form_text));
        if (reason != null) {
            amountDescription.setText(reason.getSummary());
        } else {
            amountDescription.setText(R.string.px_used_up_discount_row);
        }
        showEffectiveAmount(totalAmount, currency);
    }

    private void show(@NonNull final BigDecimal totalAmount, @NonNull final Currency currency) {
        final String totalDescriptionText =
            Session.getInstance().getConfigurationModule().getPaymentSettings().getAdvancedConfiguration()
                .getCustomStringConfiguration().getTotalDescriptionText();

        configureViewsVisibilityDefault();

        amountDescription.setText(isNotEmpty(totalDescriptionText) ?
            totalDescriptionText : getContext().getString(R.string.px_total_to_pay));

        amountDescription.setTextColor(getResources().getColor(R.color.px_form_text));
        showEffectiveAmount(totalAmount, currency);
    }

    private void configureViewsVisibilityWhenNotAvailableDiscount(
        @NonNull final DiscountConfigurationModel discountModel) {
        amountBeforeDiscount.setVisibility(GONE);
        maxCouponAmount.setVisibility(GONE);
        arrow.setVisibility(VISIBLE);
        configureOnOnDetailClickedEvent(discountModel);
    }

    private void configureViewsVisibilityDefault() {
        amountBeforeDiscount.setVisibility(GONE);
        maxCouponAmount.setVisibility(GONE);

        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) amountContainer.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);

        amountContainer.setLayoutParams(params);
        arrow.setVisibility(GONE);
    }

    private void showDiscount(@NonNull final DiscountConfigurationModel discountModel,
        @NonNull final BigDecimal totalAmount, @NonNull final Currency currency) {
        configureDiscountAmountDescription(discountModel.getDiscount(), discountModel.getCampaign());
        configureViewsVisibilityWhenDiscount(totalAmount, currency);
        configureOnOnDetailClickedEvent(discountModel);
    }

    private void configureOnOnDetailClickedEvent(@NonNull final DiscountConfigurationModel discountModel) {
        mainContainer.setOnClickListener(v -> {
            if (callback != null) {
                callback.onDetailClicked(discountModel);
            }
        });
    }

    private void configureViewsVisibilityWhenDiscount(@NonNull final BigDecimal totalAmount,
        @NonNull final Currency currency) {
        arrow.setVisibility(VISIBLE);
        amountBeforeDiscount.setVisibility(VISIBLE);
        TextFormatter.withCurrency(currency)
            .withSpace()
            .amount(totalAmount)
            .normalDecimals()
            .into(amountBeforeDiscount);

        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) amountContainer.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.removeRule(RelativeLayout.ALIGN_PARENT_END);

        amountContainer.setLayoutParams(params);
    }

    private void configureDiscountAmountDescription(final Discount discount, final Campaign campaign) {
        amountDescription.setVisibility(VISIBLE);
        amountDescription.setTextColor(getResources().getColor(R.color.px_discount_description));
        configureDiscountOffMessage(discount);
        configureMaxCouponAmountMessage(campaign);
    }

    private void showEffectiveAmount(@NonNull final BigDecimal totalAmount, @NonNull final Currency currency) {
        TextFormatter.withCurrency(currency)
            .withSpace()
            .amount(totalAmount)
            .normalDecimals()
            .into(finalAmount);
    }

    private void configureMaxCouponAmountMessage(final Campaign campaign) {
        if (campaign.hasMaxCouponAmount()) {
            maxCouponAmount.setVisibility(VISIBLE);
            maxCouponAmount.setText(R.string.px_with_max_coupon_amount);
        } else {
            maxCouponAmount.setVisibility(GONE);
        }
    }

    private void configureDiscountOffMessage(final Discount discount) {
        amountDescription.setText(DiscountHelper.getDiscountDescription(getContext(), discount));
    }
}
