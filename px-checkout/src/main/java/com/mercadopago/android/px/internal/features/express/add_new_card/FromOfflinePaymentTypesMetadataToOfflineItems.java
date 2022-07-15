package com.mercadopago.android.px.internal.features.express.add_new_card;

import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.ResourceUtil;
import com.mercadopago.android.px.model.OfflinePaymentMethod;
import com.mercadopago.android.px.model.OfflinePaymentType;
import com.mercadopago.android.px.model.OfflinePaymentTypesMetadata;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.android.px.internal.util.TextUtil.EMPTY;
import static com.mercadopago.android.px.internal.util.TextUtil.UNDERSCORE;
import static com.mercadopago.android.px.internal.util.TextUtil.isNotEmpty;

/* default */ final class FromOfflinePaymentTypesMetadataToOfflineItems {

    private final Context context;

    /* default */ FromOfflinePaymentTypesMetadataToOfflineItems(final Context context) {
        this.context = context;
    }

    @NonNull
    public List<OfflineMethodItem> map(@NonNull final OfflinePaymentTypesMetadata metadata) {
        final List<OfflineMethodItem> offlineMethodItems = new ArrayList<>();

        for (final OfflinePaymentType offlinePaymentType : metadata.getPaymentTypes()) {
            offlineMethodItems.add(new OfflineMethodItem(offlinePaymentType.getName()));
            for (final OfflinePaymentMethod offlinePaymentMethod : offlinePaymentType.getPaymentMethods()) {
                if (offlinePaymentMethod.getStatus().isEnabled()) {
                    final String iconId = offlinePaymentMethod.getId() +
                        (offlinePaymentMethod.getInstructionId().equals(offlinePaymentType.getId()) ?
                            EMPTY : UNDERSCORE + offlinePaymentMethod.getInstructionId());
                    offlineMethodItems.add(
                        new OfflineMethodItem(offlinePaymentMethod.getId(), offlinePaymentMethod.getInstructionId(),
                            offlinePaymentMethod.getName(), offlinePaymentMethod.getDescription(),
                            getIconResourceId(iconId), offlinePaymentMethod.isAdditionalInfoNeeded()));
                }
            }
        }

        return offlineMethodItems;
    }

    private int getIconResourceId(@NonNull final String methodId) {
        if (isNotEmpty(methodId)) {
            return ResourceUtil.getIconResource(context, methodId);
        } else {
            return 0;
        }
    }
}