package com.mercadopago.android.px.internal.viewmodel.drawables;

import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.model.ConsumerCreditsMetadata;

public class ConsumerCreditsDrawableFragmentItem extends DrawableFragmentItem {

    @NonNull public final ConsumerCreditsMetadata metadata;

    public static final Creator<ConsumerCreditsDrawableFragmentItem> CREATOR =
        new Creator<ConsumerCreditsDrawableFragmentItem>() {
            @Override
            public ConsumerCreditsDrawableFragmentItem createFromParcel(final Parcel in) {
                return new ConsumerCreditsDrawableFragmentItem(in);
            }

            @Override
            public ConsumerCreditsDrawableFragmentItem[] newArray(final int size) {
                return new ConsumerCreditsDrawableFragmentItem[size];
            }
        };

    public ConsumerCreditsDrawableFragmentItem(@NonNull final Parameters parameters,
        @NonNull final ConsumerCreditsMetadata metadata) {
        super(parameters);
        this.metadata = metadata;
    }

    protected ConsumerCreditsDrawableFragmentItem(final Parcel in) {
        super(in);
        metadata = in.readParcelable(ConsumerCreditsMetadata.class.getClassLoader());
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(metadata, flags);
    }

    @Override
    public Fragment draw(@NonNull final PaymentMethodFragmentDrawer drawer) {
        return drawer.draw(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}