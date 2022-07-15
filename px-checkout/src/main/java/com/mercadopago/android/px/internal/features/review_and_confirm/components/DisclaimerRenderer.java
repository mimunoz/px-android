package com.mercadopago.android.px.internal.features.review_and_confirm.components;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.view.Renderer;

/**
 * Created by mromar on 2/28/18.
 */

public class DisclaimerRenderer extends Renderer<DisclaimerComponent> {

    @Override
    protected View render(@NonNull DisclaimerComponent component, @NonNull Context context,
        @Nullable ViewGroup parent) {
        final View disclaimerView = inflate(R.layout.px_disclaimer, parent);
        final MPTextView disclaimerTextView = disclaimerView.findViewById(R.id.cftText);

        setText(disclaimerTextView, component.props.disclaimer);

        return disclaimerView;
    }
}
