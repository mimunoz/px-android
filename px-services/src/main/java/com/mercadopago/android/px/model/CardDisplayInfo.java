package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.io.Serializable;

public class CardDisplayInfo implements Parcelable, Serializable {

    public final String cardholderName;
    public final String expiration;
    public final String color;
    public final String fontColor;
    public final long issuerId;
    public int[] cardPattern;
    public final String lastFourDigits;
    public final String paymentMethodImage;
    public final String issuerImage;
    public final String fontType;
    public final String paymentMethodImageUrl;
    public final String issuerImageUrl;
    //public final String firstSixDigits;

    protected CardDisplayInfo(final Parcel in) {
        cardholderName = in.readString();
        expiration = in.readString();
        color = in.readString();
        fontColor = in.readString();
        issuerId = in.readLong();
        cardPattern = in.createIntArray();
        lastFourDigits = in.readString();
        in.readIntArray(cardPattern);
        paymentMethodImage = in.readString();
        issuerImage = in.readString();
        fontType = in.readString();
        paymentMethodImageUrl = in.readString();
        issuerImageUrl = in.readString();
        //firstSixDigits = in.readString();
    }

    public static final Creator<CardDisplayInfo> CREATOR = new Creator<CardDisplayInfo>() {
        @Override
        public CardDisplayInfo createFromParcel(final Parcel in) {
            return new CardDisplayInfo(in);
        }

        @Override
        public CardDisplayInfo[] newArray(final int size) {
            return new CardDisplayInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(cardholderName);
        dest.writeString(expiration);
        dest.writeString(color);
        dest.writeString(fontColor);
        dest.writeLong(issuerId);
        dest.writeIntArray(cardPattern);
        dest.writeString(lastFourDigits);
        dest.writeIntArray(cardPattern);
        dest.writeString(paymentMethodImage);
        dest.writeString(issuerImage);
        dest.writeString(fontType);
        dest.writeString(paymentMethodImageUrl);
        dest.writeString(issuerImageUrl);
        //dest.writeString(firstSixDigits);
    }

    @NonNull
    public String getCardPattern() {
        final StringBuilder genericPatternBuilder = new StringBuilder();
//        int firstSixDigitsCount = 0;
//        final int firstSixDigitsLength = firstSixDigits == null ? 0 : firstSixDigits.length();

        for (int i = 0; i < cardPattern.length; i++) {
            for (int j = 0; j < cardPattern[i]; j++) {
//                if (firstSixDigitsCount < firstSixDigitsLength) {
//                    genericPatternBuilder.append(firstSixDigits.charAt(firstSixDigitsCount));
//                    firstSixDigitsCount++;
//                } else {
                genericPatternBuilder.append('*');
//                }
            }
            //Prevent last space
            if (i != cardPattern.length - 1) {
                genericPatternBuilder.append(' ');
            }
        }

        //Handle last four
        int toProcessLastFour = lastFourDigits == null ? -1 : lastFourDigits.length() - 1;

        final char[] chars = genericPatternBuilder.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            if (toProcessLastFour >= 0 && chars[i] != ' ') {
                chars[i] = lastFourDigits.toCharArray()[toProcessLastFour];
                toProcessLastFour--;
            }
        }

        return String.valueOf(chars);
    }

    public long getIssuerId() {
        return issuerId;
    }
}
