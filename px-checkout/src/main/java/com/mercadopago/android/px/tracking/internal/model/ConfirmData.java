package com.mercadopago.android.px.tracking.internal.model;

import android.os.Parcel;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.tracking.internal.events.ConfirmEvent;

import com.mercadopago.android.px.tracking.internal.mapper.FromUserSelectionToAvailableMethod;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
@Keep
public class ConfirmData extends AvailableMethod {

    private final String reviewType;
    private int paymentMethodSelectedIndex;

    public static final Creator<ConfirmData> CREATOR = new Creator<ConfirmData>() {
        @Override
        public ConfirmData createFromParcel(final Parcel in) {
            return new ConfirmData(in);
        }

        @Override
        public ConfirmData[] newArray(final int size) {
            return new ConfirmData[size];
        }
    };

    public static ConfirmData from(final String paymentTypeId, final String paymentMethodId, final boolean isCompliant, final boolean hasAdditionalInfoNeeded) {
        final Map<String, Object> extraInfo = new HashMap<>();
        extraInfo.put("has_payer_information", isCompliant);
        extraInfo.put("additional_information_needed", hasAdditionalInfoNeeded);
        return new ConfirmData(ConfirmEvent.ReviewType.ONE_TAP, new AvailableMethod(paymentMethodId, paymentTypeId, extraInfo));
    }

    public static ConfirmData from(@NonNull final Set<String> cardsWithEsc,
        @NonNull final UserSelectionRepository userSelectionRepository) {
        final AvailableMethod ava = new FromUserSelectionToAvailableMethod(cardsWithEsc).map(userSelectionRepository);
        return new ConfirmData(ConfirmEvent.ReviewType.TRADITIONAL, ava);
    }

    public ConfirmData(@NonNull final ConfirmEvent.ReviewType reviewType, final int paymentMethodSelectedIndex,
        @NonNull final AvailableMethod availableMethod) {
        super(availableMethod.paymentMethodId, availableMethod.paymentMethodType, availableMethod.extraInfo);
        this.reviewType = reviewType.value;
        this.paymentMethodSelectedIndex = paymentMethodSelectedIndex;
    }

    public ConfirmData(@NonNull final ConfirmEvent.ReviewType reviewType,
        @NonNull final AvailableMethod availableMethod) {
        super(availableMethod.paymentMethodId, availableMethod.paymentMethodType, availableMethod.extraInfo);
        this.reviewType = reviewType.value;
    }

    @SuppressWarnings("WeakerAccess")
    protected ConfirmData(final Parcel in) {
        super(in);
        reviewType = in.readString();
        paymentMethodSelectedIndex = in.readInt();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(reviewType);
        dest.writeInt(paymentMethodSelectedIndex);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}