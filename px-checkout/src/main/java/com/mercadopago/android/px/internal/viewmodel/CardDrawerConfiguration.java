package com.mercadopago.android.px.internal.viewmodel;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView;
import com.meli.android.carddrawer.configuration.FontType;
import com.meli.android.carddrawer.configuration.SecurityCodeLocation;
import com.meli.android.carddrawer.model.CardAnimationType;
import com.meli.android.carddrawer.model.CardUI;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.model.CardDisplayInfo;
import java.io.Serializable;

public final class CardDrawerConfiguration implements CardUI, Parcelable, Serializable {

    private static final int NUMBER_SEC_CODE = 3;

    private final CardDisplayInfo info;
    @ColorInt private int color;
    @ColorInt private int fontColor;
    @Nullable private final DisableConfiguration disableConfiguration;
    private boolean disabled;

    public static final Creator<CardDrawerConfiguration> CREATOR = new Creator<CardDrawerConfiguration>() {
        @Override
        public CardDrawerConfiguration createFromParcel(final Parcel in) {
            return new CardDrawerConfiguration(in);
        }

        @Override
        public CardDrawerConfiguration[] newArray(final int size) {
            return new CardDrawerConfiguration[size];
        }
    };

    public CardDrawerConfiguration(final CardDisplayInfo info,
        @Nullable final DisableConfiguration disableConfiguration) {
        this.info = info;
        color = Color.parseColor(info.color);
        fontColor = Color.parseColor(info.fontColor);
        this.disableConfiguration = disableConfiguration;
        disabled = false;
    }

    protected CardDrawerConfiguration(final Parcel in) {
        info = in.readParcelable(CardDisplayInfo.class.getClassLoader());
        color = in.readInt();
        fontColor = in.readInt();
        disableConfiguration = in.readParcelable(DisableConfiguration.class.getClassLoader());
        disabled = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(info, flags);
        dest.writeInt(color);
        dest.writeInt(fontColor);
        dest.writeParcelable(disableConfiguration, flags);
        dest.writeByte((byte) (disabled ? 1 : 0));
    }

    @Override
    public int[] getCardNumberPattern() {
        return info.cardPattern;
    }

    @Override
    public String getNamePlaceHolder() {
        return "";
    }

    @Override
    public String getExpirationPlaceHolder() {
        return "";
    }

    @Override
    @FontType
    public String getFontType() {
        return disabled ? FontType.NONE : info.fontType == null ? FontType.LIGHT_TYPE : info.fontType;
    }

    @Override
    public String getAnimationType() {
        return CardAnimationType.NONE;
    }

    @Override
    public int getBankImageRes() {
        return 0;
    }

    @Override
    public int getCardLogoImageRes() {
        return 0;
    }

    @Override
    public String getSecurityCodeLocation() {
        return SecurityCodeLocation.BACK;
    }

    @Override
    public int getCardFontColor() {
        return (disabled && disableConfiguration != null) ? disableConfiguration.getFontColor() : fontColor;
    }

    @Override
    public int getCardBackgroundColor() {
        return (disabled && disableConfiguration != null) ? disableConfiguration.getBackgroundColor() : color;
    }

    @Override
    public int getSecurityCodePattern() {
        return NUMBER_SEC_CODE;
    }

    @Override
    public void setCardLogoImage(@NonNull final ImageView cardLogo) {
        toGrayScaleIfDisabled(cardLogo);
    }

    @Override
    public void setBankImage(@NonNull final ImageView bankImage) {
        toGrayScaleIfDisabled(bankImage);
    }

    private void toGrayScaleIfDisabled(@NonNull final ImageView imageView) {
        if (disabled) {
            ViewUtils.grayScaleView(imageView);
        } else {
            imageView.clearColorFilter();
        }
    }

    public String getName() {
        return info.cardholderName;
    }

    public String getDate() {
        return info.expiration;
    }

    public String getNumber() {
        return info.getCardPattern();
    }

    @Nullable
    @Override
    public String getBankImageUrl() {
        return info.issuerImageUrl;
    }

    @Nullable
    @Override
    public String getCardLogoImageUrl() {
        return info.paymentMethodImageUrl;
    }

    public void disable() {
        disabled = true;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}