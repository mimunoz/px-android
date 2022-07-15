package com.mercadopago.android.px.internal.features.uicontrollers.identification;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.model.IdentificationType;

public abstract class IdentificationView {

    public static final int NORMAL_TEXT_VIEW_COLOR = R.color.px_form_text;
    public static final int ALPHA_TEXT_VIEW_COLOR = R.color.px_gray;

    protected Context mContext;
    protected View mView;

    //View controls
    protected FrameLayout mCardContainer;
    protected ImageView mCardBorder;
    protected MPTextView mIdentificationNumberTextView;
    protected MPTextView mBaseIdNumberView;

    //Identification Info
    protected String mIdentificationNumber;
    protected IdentificationType mIdentificationType;

    public IdentificationView(Context context) {
        mContext = context;
    }

    public abstract View inflateInParent(ViewGroup parent, boolean attachToRoot);

    public abstract void draw();

    public void initializeControls() {
        mCardContainer = mView.findViewById(R.id.mpsdkIdentificationCardContainer);
        mCardBorder = mView.findViewById(R.id.mpsdkCardShadowBorder);
        mBaseIdNumberView = mView.findViewById(R.id.mpsdkIdentificationCardholderContainer);
        mIdentificationNumberTextView = mView.findViewById(R.id.mpsdkIdNumberView);
    }

    public void setIdentificationNumber(@Nullable String number) {
        mIdentificationNumber = number;
    }

    public void setIdentificationType(IdentificationType identificationType) {
        mIdentificationType = identificationType;
    }

    public void show() {
        mCardContainer.setVisibility(View.VISIBLE);
    }

    public void hide() {
        mCardContainer.setVisibility(View.GONE);
    }

    public void decorateCardBorder(int borderColor) {
        GradientDrawable cardShadowRounded =
            (GradientDrawable) ContextCompat.getDrawable(mContext, R.drawable.px_card_shadow_rounded);
        cardShadowRounded.setStroke(ScaleUtil.getPxFromDp(6, mContext), borderColor);
        mCardBorder.setImageDrawable(cardShadowRounded);
    }

    public View getView() {
        return mView;
    }
}
