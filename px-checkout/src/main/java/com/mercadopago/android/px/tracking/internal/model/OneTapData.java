package com.mercadopago.android.px.tracking.internal.model;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.ExpressMetadata;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.internal.mapper.FromExpressMetadataToAvailableMethods;
import com.mercadopago.android.px.tracking.internal.mapper.FromItemToItemInfo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
@Keep
public class OneTapData extends SelectMethodData {

    @Nullable private final DiscountInfo discount;

    public OneTapData(@NonNull final List<AvailableMethod> availableMethods, @NonNull final BigDecimal totalAmount,
        @Nullable final DiscountInfo discount, @NonNull final List<ItemInfo> items, final int disabledMethodsQuantity) {
        super(availableMethods, items, totalAmount, disabledMethodsQuantity);
        this.discount = discount;
    }

    @NonNull
    public static OneTapData createFrom(final Iterable<ExpressMetadata> expressMetadataList,
        final CheckoutPreference checkoutPreference, final DiscountConfigurationModel discountModel,
        @NonNull final Set<String> cardsWithEsc, @NonNull final Set<String> cardsWithSplit,
        final int disabledMethodsQuantity) {

        final List<ItemInfo> itemInfoList = new FromItemToItemInfo().map(checkoutPreference.getItems());

        final DiscountInfo discountInfo =
            DiscountInfo.with(discountModel.getDiscount(), discountModel.getCampaign(), discountModel.isAvailable());

        return new OneTapData(
            new FromExpressMetadataToAvailableMethods(cardsWithEsc, cardsWithSplit).map(expressMetadataList),
            checkoutPreference.getTotalAmount(), discountInfo, itemInfoList, disabledMethodsQuantity);
    }
}