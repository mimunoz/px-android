package com.mercadopago.android.px.tracking.internal.views;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.OfflinePaymentMethod;
import com.mercadopago.android.px.model.OfflinePaymentType;
import com.mercadopago.android.px.model.OfflinePaymentTypesMetadata;
import com.mercadopago.android.px.tracking.internal.model.AvailableOfflineMethod;
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel;
import java.util.ArrayList;
import java.util.Collection;

/* default */ final class OfflineMethodsData extends TrackingMapModel {

    /* default */ final Collection<AvailableOfflineMethod> availableMethods = new ArrayList<>();

    public static OfflineMethodsData createFrom(@NonNull final OfflinePaymentTypesMetadata metadata) {
        final OfflineMethodsData instance = new OfflineMethodsData();

        for (final OfflinePaymentType offlinePaymentType : metadata.getPaymentTypes()) {
            for (final OfflinePaymentMethod offlinePaymentMethod : offlinePaymentType.getPaymentMethods()) {
                instance.availableMethods
                    .add(new AvailableOfflineMethod(offlinePaymentType.getId(), offlinePaymentMethod.getId()));
            }
        }
        return instance;
    }
}