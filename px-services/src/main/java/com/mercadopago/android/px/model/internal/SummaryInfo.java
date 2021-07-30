package com.mercadopago.android.px.model.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class SummaryInfo {

    @NonNull private final String title;
    @Nullable private final String subtitle;
    @Nullable private final String imageUrl;
    @Nullable private final String purpose;

    public SummaryInfo(@NonNull final String title, @Nullable final String imageUrl) {
        this(title, null, imageUrl, null);
    }

    private SummaryInfo(@NonNull final String title, @Nullable final String subtitle, @Nullable final String imageUrl,
        @Nullable final String purpose) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.purpose = purpose;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getSubtitle() {
        return subtitle;
    }

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    @Nullable
    public String getPurpose() {
        return purpose;
    }

}