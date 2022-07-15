package com.mercadopago.android.px.model.display_info;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.util.ParcelableUtil;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class LinkablePhrase implements Parcelable, Serializable {

    private final String phrase;
    private final String textColor;
    private final String link;
    private final String html;
    private final Map<String, String> installments;

    public String getPhrase() {
        return phrase;
    }

    public String getTextColor() {
        return textColor;
    }

    public String getLink() {
        return link;
    }

    public String getHtml() {
        return html;
    }

    public Map<String, String> getInstallments() {
        return installments != null ? installments : Collections.emptyMap();
    }

    @Nullable
    public String getLinkId(final int installments) {
        return this.getInstallments().get(String.valueOf(installments));
    }

    protected LinkablePhrase(final Parcel in) {
        phrase = in.readString();
        textColor = in.readString();
        link = in.readString();
        html = in.readString();
        installments = new HashMap<>();
        ParcelableUtil.readSerializableMap(installments, in, String.class, String.class);
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(phrase);
        dest.writeString(textColor);
        dest.writeString(link);
        dest.writeString(html);
        ParcelableUtil.writeSerializableMap(dest, installments != null ? installments : Collections.emptyMap());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LinkablePhrase> CREATOR = new Creator<LinkablePhrase>() {
        @Override
        public LinkablePhrase createFromParcel(final Parcel in) {
            return new LinkablePhrase(in);
        }

        @Override
        public LinkablePhrase[] newArray(final int size) {
            return new LinkablePhrase[size];
        }
    };
}