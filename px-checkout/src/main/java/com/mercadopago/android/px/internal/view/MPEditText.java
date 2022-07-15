package com.mercadopago.android.px.internal.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.font.FontHelper;
import com.mercadopago.android.px.internal.font.PxFont;

public class MPEditText extends AppCompatEditText {

    private int mErrorColor;

    public MPEditText(final Context context) {
        this(context, null);
    }

    public MPEditText(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MPEditText(final Context context, @Nullable final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setErrorColor(context, attrs, defStyle);
        if (!isInEditMode()) {
            FontHelper.setFont(this, PxFont.REGULAR);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public int getAutofillType() {
        return AUTOFILL_TYPE_NONE;
    }

    private void setErrorColor(final Context context, @Nullable final AttributeSet attrs, final int defStyle) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs,
            R.styleable.MPEditText, defStyle, 0);
        final String errorColor = typedArray.getString(R.styleable.MPEditText_errorColor);
        if (errorColor != null) {
            mErrorColor = Color.parseColor(errorColor);
        }
        typedArray.recycle();
    }

    public void toggleLineColorOnError(final boolean error) {
        if (mErrorColor == 0) {
            return;
        }
        if (error) {
            getBackground().setColorFilter(mErrorColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            getBackground().setColorFilter(null);
        }
    }
}