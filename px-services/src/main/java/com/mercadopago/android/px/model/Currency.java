package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class Currency implements Parcelable {

    private String id;
    private String description;
    private String symbol;
    private int decimalPlaces;
    private Character decimalSeparator;
    private Character thousandsSeparator;

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(final Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(final int size) {
            return new Currency[size];
        }
    };

    public Currency() {
    }

    public Currency(final String id, final String description, final String symbol,
        final int decimalPlaces, final Character decimalSeparator, final Character thousandsSeparator) {
        this.id = id;
        this.description = description;
        this.symbol = symbol;
        this.decimalPlaces = decimalPlaces;
        this.decimalSeparator = decimalSeparator;
        this.thousandsSeparator = thousandsSeparator;
    }

    protected Currency(final Parcel in) {
        id = in.readString();
        description = in.readString();
        symbol = in.readString();
        decimalPlaces = in.readInt();
        int tmpDecimalSeparator = in.readInt();
        decimalSeparator = tmpDecimalSeparator != Integer.MAX_VALUE ? (char) tmpDecimalSeparator : null;
        int tmpThousandsSeparator = in.readInt();
        thousandsSeparator = tmpThousandsSeparator != Integer.MAX_VALUE ? (char) tmpThousandsSeparator : null;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeString(symbol);
        dest.writeInt(decimalPlaces);
        dest.writeInt(decimalSeparator != null ? (int) decimalSeparator : Integer.MAX_VALUE);
        dest.writeInt(thousandsSeparator != null ? (int) thousandsSeparator : Integer.MAX_VALUE);
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(final int decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public Character getDecimalSeparator() {
        return decimalSeparator;
    }

    public void setDecimalSeparator(final Character decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }

    public Character getThousandsSeparator() {
        return thousandsSeparator;
    }

    public void setThousandsSeparator(final Character thousandsSeparator) {
        this.thousandsSeparator = thousandsSeparator;
    }

    @NonNull
    @Override
    public String toString() {
        return "Currency [id=" + id + ", description=" + description
            + ", symbol=" + symbol + ", decimalPlaces=" + decimalPlaces
            + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }
}