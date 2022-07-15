package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.view.MPTextView;

public class AccreditationComment extends CompactComponent<String, ActionDispatcher> {

    /* default */ AccreditationComment(@NonNull final String comment, @NonNull final ActionDispatcher dispatcher) {
        super(comment, dispatcher);
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final View accreditationCommentView =
            LayoutInflater.from(context).inflate(R.layout.px_accreditation_comment, parent);
        final MPTextView commentTextView = accreditationCommentView.findViewById(R.id.mpsdkAccreditationTimeComment);
        ViewUtils.loadOrGone(props, commentTextView);
        return accreditationCommentView;
    }
}