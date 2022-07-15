package com.mercadopago.android.px.internal.features.uicontrollers.card;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.MPAnimationUtils;
import com.mercadopago.android.px.internal.util.MPCardMaskUtil;
import com.mercadopago.android.px.internal.util.ResourceUtil;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.model.Bin;
import com.mercadopago.android.px.model.PaymentMethod;

public class FrontCardView {

    public static final String BASE_NUMBER_CARDHOLDER = "•••• •••• •••• ••••";
    public static final String BASE_FRONT_SECURITY_CODE = "••••";

    public static final int CARD_NUMBER_MAX_LENGTH = 16;
    public static final int CARD_SECURITY_CODE_DEFAULT_LENGTH = 4;

    public static final int CARD_DEFAULT_AMOUNT_SPACES = 3;
    public static final int CARD_AMEX_DINERS_AMOUNT_SPACES = 2;
    public static final int CARD_NUMBER_MAESTRO_SETTING_2_AMOUNT_SPACES = 1;

    public static final int CARD_NUMBER_AMEX_LENGTH = 15;
    public static final int CARD_NUMBER_DINERS_LENGTH = 14;
    public static final int CARD_NUMBER_MAESTRO_SETTING_1_LENGTH = 18;
    public static final int CARD_NUMBER_MAESTRO_SETTING_2_LENGTH = 19;

    public static final int EDITING_TEXT_VIEW_ALPHA = 255;

    private final Context context;
    private View view;
    private String mode;
    private String size;

    //Card info
    private PaymentMethod paymentMethod;
    private int cardNumberLength;
    private int securityCodeLength;
    private String lastFourDigits;

    //View controls
    private FrameLayout cardContainer;
    private ImageView cardBorder;
    private MPTextView cardNumberTextView;
    private MPTextView cardholderNameTextView;
    private MPTextView cardExpiryMonthTextView;
    private MPTextView cardSecurityCodeTextView;
    private FrameLayout baseImageCard;
    private ImageView imageCardContainer;
    private ImageView cardImageView;
    private Animation animFadeIn;

    public FrontCardView(final Context context, final String mode) {
        this.context = context;
        this.mode = mode;
        cardNumberLength = CARD_NUMBER_MAX_LENGTH;
        securityCodeLength = CARD_SECURITY_CODE_DEFAULT_LENGTH;
    }

    public void setPaymentMethod(@Nullable final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setSize(final String size) {
        this.size = size;
    }

    public void setCardNumberLength(final int cardNumberLength) {
        this.cardNumberLength = cardNumberLength;
    }

    public void setSecurityCodeLength(final int securityCodeLength) {
        this.securityCodeLength = securityCodeLength;
    }

    public void hasToShowSecurityCode(final boolean show) {
        if (show) {
            showEmptySecurityCode();
        } else {
            hideSecurityCode();
        }
    }

    public void setLastFourDigits(final String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public void initializeControls() {
        cardContainer = view.findViewById(R.id.mpsdkCardFrontContainer);
        cardBorder = view.findViewById(R.id.mpsdkCardShadowBorder);
        animFadeIn = AnimationUtils.loadAnimation(context, R.anim.px_fade_in);
        cardNumberTextView = view.findViewById(R.id.mpsdkCardNumberTextView);
        cardholderNameTextView = view.findViewById(R.id.mpsdkCardholderNameView);
        cardExpiryMonthTextView = view.findViewById(R.id.mpsdkCardHolderExpiryMonth);
        cardSecurityCodeTextView = view.findViewById(R.id.mpsdkCardSecurityCodeViewFront);
        baseImageCard = view.findViewById(R.id.mpsdkBaseImageCard);
        imageCardContainer = view.findViewById(R.id.mpsdkImageCardContainer);
        cardImageView = view.findViewById(R.id.mpsdkCardImageView);

        if (size != null) {
            resize();
        }
    }

    public void hide() {
        cardContainer.setVisibility(View.GONE);
    }

    public void show() {
        cardContainer.setVisibility(View.VISIBLE);
    }

    public View inflateInParent(final ViewGroup parent, final boolean attachToRoot) {
        view = LayoutInflater.from(context)
            .inflate(R.layout.px_card_front, parent, attachToRoot);
        return view;
    }

    public View getView() {
        return view;
    }

    public void decorateCardBorder(final int borderColor) {
        final GradientDrawable cardShadowRounded =
            (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.px_card_shadow_rounded);
        cardShadowRounded.setStroke(ScaleUtil.getPxFromDp(6, context), borderColor);
        cardBorder.setImageDrawable(cardShadowRounded);
    }

    public void draw() {
        if (mode == null) {
            mode = CardRepresentationModes.SHOW_EMPTY_FRONT_ONLY;
        }
        if (mode.equals(CardRepresentationModes.SHOW_EMPTY_FRONT_ONLY)) {
            drawEmptyCard();
        } else if (mode.equals(CardRepresentationModes.SHOW_FULL_FRONT_ONLY)) {
            drawFullCard();
        } else if (mode.equals(CardRepresentationModes.EDIT_FRONT)) {
            drawEmptyCard();
        }
    }

    public void drawEditingCard(final String cardNumber, final String cardholderName, final String expiryMonth,
        final String expiryYear, final String securityCode) {
        onPaymentMethodSet();
        drawEditingCardNumber(cardNumber);
        drawEditingCardHolderName(cardholderName);
        if (securityCode != null) {
            drawEditingSecurityCode(securityCode);
        }
        drawEditingExpiryMonth(expiryMonth);
        drawEditingExpiryYear(expiryYear);
    }

    public void drawEditingCardNumber(final String cardNumber) {
        if (cardNumber == null || cardNumber.length() == 0) {
            cardNumberTextView.setText(BASE_NUMBER_CARDHOLDER);
        } else if (cardNumber.length() < Bin.BIN_LENGTH || paymentMethod == null) {
            cardNumberTextView.setText(MPCardMaskUtil.buildNumberWithMask(CARD_NUMBER_MAX_LENGTH, cardNumber));
        } else {
            cardNumberTextView.setText(MPCardMaskUtil.buildNumberWithMask(cardNumberLength, cardNumber));
        }
        enableEditingFontColor(cardNumberTextView);
        disableEditingFontColor(cardholderNameTextView);
        disableEditingFontColor(cardExpiryMonthTextView);
        disableEditingFontColor(cardSecurityCodeTextView);
    }

    public void updateCardNumberMask(final String cardNumber) {
        cardNumberTextView.setText(MPCardMaskUtil.buildNumberWithMask(cardNumberLength, cardNumber));
    }

    public void drawEditingCardHolderName(final String cardholderName) {
        if (cardholderName == null || cardholderName.length() == 0) {
            cardholderNameTextView.setText(context.getResources().getString(R.string.px_cardholder_name_short));
        } else {
            cardholderNameTextView.setText(cardholderName.toUpperCase());
        }
        enableEditingFontColor(cardholderNameTextView);
        disableEditingFontColor(cardNumberTextView);
        disableEditingFontColor(cardExpiryMonthTextView);
        disableEditingFontColor(cardSecurityCodeTextView);
    }

    public void drawEditingExpiryMonth(final String cardMonth) {
        final String separator = "/";

        if (cardMonth == null || cardMonth.length() == 0) {
            cardExpiryMonthTextView.setText(context.getResources().getString(R.string.px_card_expiry_date));
        } else {
            final String message = cardMonth + separator +
                context.getResources().getString(R.string.px_card_expiry_date).split(separator)[1];
            cardExpiryMonthTextView.setText(message);
        }

        enableEditingFontColor(cardExpiryMonthTextView);
        disableEditingFontColor(cardholderNameTextView);
        disableEditingFontColor(cardNumberTextView);
        disableEditingFontColor(cardSecurityCodeTextView);
    }

    public void drawEditingExpiryYear(final String cardYear) {
        final String separator = "/";

        if (cardYear == null || cardYear.length() == 0) {
            final String message = cardExpiryMonthTextView.getText().toString().split(separator)[0] + separator +
                context.getResources().getString(R.string.px_card_expiry_date).split(separator)[1];
            cardExpiryMonthTextView.setText(message);
        } else {
            final String message =
                cardExpiryMonthTextView.getText().toString().split(separator)[0] + separator + cardYear;
            cardExpiryMonthTextView.setText(message);
        }

        enableEditingFontColor(cardExpiryMonthTextView);
        disableEditingFontColor(cardholderNameTextView);
        disableEditingFontColor(cardNumberTextView);
        disableEditingFontColor(cardSecurityCodeTextView);
    }

    public void drawEditingSecurityCode(final String securityCode) {
        if (securityCode == null || securityCode.length() == 0) {
            cardSecurityCodeTextView.setText(BASE_FRONT_SECURITY_CODE);
        } else {
            cardSecurityCodeTextView.setText(MPCardMaskUtil.buildSecurityCode(securityCodeLength, securityCode));
        }
        enableEditingFontColor(cardSecurityCodeTextView);
        disableEditingFontColor(cardNumberTextView);
        disableEditingFontColor(cardholderNameTextView);
        disableEditingFontColor(cardExpiryMonthTextView);
    }

    public void transitionPaymentMethodSet() {
        final String pmId = paymentMethod == null ? "" : paymentMethod.getId();
        fadeInColor(ResourceUtil.getCardColor(paymentMethod.getId(), context));
        final int fontColor =
            ResourceUtil.getCardFontColor(pmId, context);
        setFontColor(fontColor, cardNumberTextView);
        setFontColor(fontColor, cardholderNameTextView);
        setFontColor(fontColor, cardExpiryMonthTextView);
        setFontColor(fontColor, cardSecurityCodeTextView);
        enableEditingFontColor(cardNumberTextView);
        transitionImage(ResourceUtil.getCardImage(context, pmId), true);
    }

    public void fillCardHolderName(final String cardholderName) {
        if (TextUtil.isEmpty(cardholderName)) {
            cardholderNameTextView.setText(context.getResources().getString(R.string.px_cardholder_name_short));
        } else {
            cardholderNameTextView.setText(cardholderName.toUpperCase());
        }
    }

    public void transitionClearPaymentMethod() {
        paymentMethod = null;
        fadeOutColor(ResourceUtil.NEUTRAL_CARD_COLOR);
        clearCardImage();
        setFontColor(ResourceUtil.FULL_TEXT_VIEW_COLOR, cardNumberTextView);
        setFontColor(ResourceUtil.FULL_TEXT_VIEW_COLOR, cardholderNameTextView);
        setFontColor(ResourceUtil.FULL_TEXT_VIEW_COLOR, cardExpiryMonthTextView);
        setFontColor(ResourceUtil.FULL_TEXT_VIEW_COLOR, cardSecurityCodeTextView);
        enableEditingFontColor(cardNumberTextView);
        cardSecurityCodeTextView.setText("");
    }

    private void drawEmptyCard() {
        String number = BASE_NUMBER_CARDHOLDER;
        cardNumberTextView.setText(number);
        cardholderNameTextView.setText(context.getResources().getString(R.string.px_cardholder_name_short));
        cardExpiryMonthTextView.setText(context.getResources().getString(R.string.px_card_expiry_date));
        cardSecurityCodeTextView.setText("");
        clearImage();
    }

    private void clearImage() {
        baseImageCard.clearAnimation();
        imageCardContainer.clearAnimation();
        imageCardContainer.setVisibility(View.INVISIBLE);
        if (baseImageCard.getVisibility() == View.INVISIBLE) {
            baseImageCard.setVisibility(View.VISIBLE);
            baseImageCard.startAnimation(animFadeIn);
        }
    }

    public void drawFullCard() {
        if (lastFourDigits == null || paymentMethod == null) {
            return;
        }
        cardNumberTextView.setText(MPCardMaskUtil.getCardNumberHidden(cardNumberLength, lastFourDigits));
        cardholderNameTextView.setVisibility(View.GONE);
        cardExpiryMonthTextView.setVisibility(View.GONE);
        onPaymentMethodSet();
    }

    private void setCardColor(final int color) {
        MPAnimationUtils.setImageViewColor(cardImageView, color);
    }

    private void setCardImage(final int image) {
        transitionImage(image, false);
    }

    private void setFontColor(final int color, final TextView textView) {
        textView.setTextColor(ContextCompat.getColor(context, color));
    }

    private void transitionImage(final int image, final boolean animate) {
        baseImageCard.clearAnimation();
        imageCardContainer.clearAnimation();
        baseImageCard.setVisibility(View.INVISIBLE);
        imageCardContainer.setImageResource(image);
        imageCardContainer.setVisibility(View.VISIBLE);
        if (animate) {
            imageCardContainer.startAnimation(animFadeIn);
        }
    }

    private void resize() {
        if (size == null) {
            return;
        }
        if (size.equals(CardRepresentationModes.MEDIUM_SIZE)) {
            resizeCard(cardContainer, R.dimen.px_card_size_medium_height, R.dimen.px_card_size_medium_width,
                CardRepresentationModes.CARD_HOLDER_NAME_SIZE_MEDIUM,
                CardRepresentationModes.CARD_EXPIRY_DATE_SIZE_MEDIUM,
                CardRepresentationModes.CARD_SECURITY_CODE_FRONT_SIZE_MEDIUM);
        } else if (size.equals(CardRepresentationModes.BIG_SIZE)) {
            resizeCard(cardContainer, R.dimen.px_card_size_big_height, R.dimen.px_card_size_big_width,
                CardRepresentationModes.CARD_HOLDER_NAME_SIZE_BIG,
                CardRepresentationModes.CARD_EXPIRY_DATE_SIZE_BIG,
                CardRepresentationModes.CARD_SECURITY_CODE_FRONT_SIZE_BIG);
        } else if (size.equals(CardRepresentationModes.EXTRA_BIG_SIZE)) {
            resizeCard(cardContainer, R.dimen.px_card_size_extra_big_height,
                R.dimen.px_card_size_extra_big_width,
                CardRepresentationModes.CARD_HOLDER_NAME_SIZE_EXTRA_BIG,
                CardRepresentationModes.CARD_EXPIRY_DATE_SIZE_EXTRA_BIG,
                CardRepresentationModes.CARD_SECURITY_CODE_FRONT_SIZE_EXTRA_BIG);
        }
    }

    private void resizeCard(final ViewGroup cardViewContainer, final int cardHeight, final int cardWidth,
        final int cardHolderNameFontSize, final int cardExpiryDateSize, final int cardSecurityCodeSize) {
        ViewUtils.resizeViewGroupLayoutParams(cardViewContainer, cardHeight, cardWidth);

        cardholderNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, cardHolderNameFontSize);
        cardExpiryMonthTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, cardExpiryDateSize);
        cardSecurityCodeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, cardSecurityCodeSize);
    }

    private void showEmptySecurityCode() {
        cardSecurityCodeTextView.setText(BASE_FRONT_SECURITY_CODE);
    }

    private void hideSecurityCode() {
        cardSecurityCodeTextView.setText("");
    }

    public void enableEditingCardNumber() {
        enableEditingFontColor(cardNumberTextView);
    }

    private void enableEditingFontColor(final TextView textView) {
        final int alpha = EDITING_TEXT_VIEW_ALPHA;

        final int fontColor =
            ResourceUtil.getCardFontColor(paymentMethod == null ? "" : paymentMethod.getId(), context);
        final int color = ContextCompat.getColor(context, fontColor);
        final int newColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
        textView.setTextColor(newColor);
    }

    private void disableEditingFontColor(final TextView textView) {
        final int fontColor =
            ResourceUtil.getCardFontColor(paymentMethod == null ? "" : paymentMethod.getId(), context);
        setFontColor(fontColor, textView);
    }

    private void onPaymentMethodSet() {
        if (paymentMethod == null) {
            return;
        }
        final String pmId = paymentMethod.getId();
        setCardColor(ResourceUtil.getCardColor(pmId, context));
        setCardImage(ResourceUtil.getCardImage(context, pmId));
        final int fontColor =
            ResourceUtil.getCardFontColor(paymentMethod == null ? "" : paymentMethod.getId(), context);
        setFontColor(fontColor, cardNumberTextView);
    }

    private void fadeInColor(final int color) {
        MPAnimationUtils.fadeIn(color, cardImageView);
    }

    private void fadeOutColor(final int color) {
        MPAnimationUtils.fadeOut(color, cardImageView);
    }

    private void clearCardImage() {
        baseImageCard.clearAnimation();
        imageCardContainer.clearAnimation();
        imageCardContainer.setVisibility(View.INVISIBLE);
        if (baseImageCard.getVisibility() == View.INVISIBLE) {
            baseImageCard.setVisibility(View.VISIBLE);
            baseImageCard.startAnimation(animFadeIn);
        }
    }
}
