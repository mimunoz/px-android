package com.mercadopago.android.px.internal.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.mappers.AmountDescriptorMapper;
import com.mercadopago.android.px.internal.mappers.Mapper;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.util.ChargeRuleHelper;
import com.mercadopago.android.px.internal.viewmodel.AmountLocalized;
import com.mercadopago.android.px.internal.viewmodel.ChargeLocalized;
import com.mercadopago.android.px.internal.viewmodel.DiscountDetailColor;
import com.mercadopago.android.px.internal.viewmodel.IDetailColor;
import com.mercadopago.android.px.internal.viewmodel.ItemLocalized;
import com.mercadopago.android.px.internal.viewmodel.SummaryViewDefaultColor;
import com.mercadopago.android.px.internal.viewmodel.SummaryViewDetailDrawable;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.DiscountOverview;
import com.mercadopago.android.px.model.internal.SummaryInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class SummaryDetailDescriptorMapper extends Mapper<
    SummaryDetailDescriptorMapper.Model,
    List<AmountDescriptorView.Model>> {

    @NonNull private final AmountRepository amountRepository;
    @NonNull private final SummaryInfo summaryInfo;
    @NonNull private final Currency currency;
    @NonNull private final AmountDescriptorMapper amountDescriptorMapper;

    public SummaryDetailDescriptorMapper(
        @NonNull final AmountRepository amountRepository,
        @NonNull final SummaryInfo summaryInfo, @NonNull final Currency currency,
        @NonNull final AmountDescriptorMapper amountDescriptorMapper) {
        this.amountRepository = amountRepository;
        this.summaryInfo = summaryInfo;
        this.currency = currency;
        this.amountDescriptorMapper = amountDescriptorMapper;
    }

    private void addDiscountRow(@NonNull final Model value,
        @NonNull final Collection<AmountDescriptorView.Model> list) {
        final DiscountConfigurationModel discountModel = value.discountModel;
        final AmountConfiguration amountConfiguration = value.amountConfiguration;
        final DiscountOverview discountOverview = discountModel.getDiscountOverview();
        final IDetailColor detailColor = new DiscountDetailColor();
        final boolean hasSplit = amountConfiguration != null && amountConfiguration.allowSplit();

        if (discountOverview != null) {
            list.add(new AmountDescriptorView.Model(amountDescriptorMapper.map(discountOverview), detailColor,
                hasSplit)
                .setDetailDrawable(new SummaryViewDetailDrawable(), detailColor)
                .setListener(v -> value.onClickListener.onDiscountAmountDescriptorClicked(discountModel)));
        }
    }

    private void addChargesRow(@NonNull final Model value,
        @NonNull final Collection<AmountDescriptorView.Model> list) {
        final PaymentTypeChargeRule chargeRule = Objects.requireNonNull(value.chargeRule);
        final AmountDescriptorView.OnClickListener onClickListener = value.onClickListener;
        final AmountDescriptorView.Model model =
            new AmountDescriptorView.Model(new ChargeLocalized(chargeRule.getLabel()),
                new AmountLocalized(chargeRule.charge(), currency), new SummaryViewDefaultColor());
        if (chargeRule.hasDetailModal()) {
            model.setDetailDrawable(new SummaryViewDetailDrawable(), new SummaryViewDefaultColor())
                .setListener(v -> onClickListener.onChargesAmountDescriptorClicked(chargeRule.getDetailModal()));
        }
        list.add(model);
    }

    private void addPurchaseRow(@NonNull final List<AmountDescriptorView.Model> list) {
        if (!list.isEmpty()) {
            list.add(0, new AmountDescriptorView.Model(new ItemLocalized(summaryInfo),
                new AmountLocalized(amountRepository.getItemsAmount(), currency), new SummaryViewDefaultColor()));
        }
    }

    @Override
    public List<AmountDescriptorView.Model> map(@NonNull final Model value) {
        final List<AmountDescriptorView.Model> list = new ArrayList<>();

        addDiscountRow(value, list);
        if (value.chargeRule != null && !ChargeRuleHelper.isHighlightCharge(value.chargeRule)) {
            addChargesRow(value, list);
        }
        addPurchaseRow(list);

        return list;
    }

    public static class Model {
        @NonNull final DiscountConfigurationModel discountModel;
        @Nullable final PaymentTypeChargeRule chargeRule;
        @Nullable final AmountConfiguration amountConfiguration;
        @NonNull final AmountDescriptorView.OnClickListener onClickListener;

        public Model(
            @NonNull final DiscountConfigurationModel discountModel,
            @Nullable final PaymentTypeChargeRule chargeRule,
            @Nullable final AmountConfiguration amountConfiguration,
            @NonNull final AmountDescriptorView.OnClickListener onClickListener
        ) {
            this.discountModel = discountModel;
            this.chargeRule = chargeRule;
            this.amountConfiguration = amountConfiguration;
            this.onClickListener = onClickListener;
        }
    }
}