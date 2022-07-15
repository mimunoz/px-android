package com.mercadopago.android.px.internal.features.payment_result.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.view.MPTextView;
import java.util.ArrayList;
import java.util.List;

public class AccreditationTime extends CompactComponent<AccreditationTime.Props, ActionDispatcher> {

    public AccreditationTime(@NonNull final Props props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public List<AccreditationComment> getAccreditationCommentComponents() {
        final List<AccreditationComment> componentList = new ArrayList<>();

        for (final String comment : props.accreditationComments) {
            final AccreditationComment component = new AccreditationComment(comment, getActions());
            componentList.add(component);
        }

        return componentList;
    }

    private boolean hasAccreditationComments() {
        return props.accreditationComments != null && !props.accreditationComments.isEmpty();
    }

    public static class Props {
        public final String accreditationMessage;
        public final List<String> accreditationComments;

        public Props(@NonNull final String accreditationMessage, final List<String> accreditationComments) {
            this.accreditationMessage = accreditationMessage;
            this.accreditationComments = accreditationComments;
        }

        public Props(@NonNull final Builder builder) {
            accreditationMessage = builder.accreditationMessage;
            accreditationComments = builder.accreditationComments;
        }

        public Builder toBuilder() {
            return new Props.Builder()
                .setAccreditationMessage(accreditationMessage)
                .setAccreditationComments(accreditationComments);
        }

        public static class Builder {
            String accreditationMessage;
            List<String> accreditationComments;

            public Builder setAccreditationMessage(final String accreditationMessage) {
                this.accreditationMessage = accreditationMessage;
                return this;
            }

            public Builder setAccreditationComments(final List<String> accreditationComments) {
                this.accreditationComments = accreditationComments;
                return this;
            }

            public Props build() {
                return new Props(this);
            }
        }
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final View accreditationTimeView = LayoutInflater.from(context).inflate(R.layout.px_accreditation_time, parent);
        final MPTextView messageTextView = accreditationTimeView.findViewById(R.id.mpsdkAccreditationTimeMessage);
        final ViewGroup accreditationCommentsContainer =
            accreditationTimeView.findViewById(R.id.mpsdkAccreditationTimeComments);

        renderAccreditationMessage(messageTextView, context);

        if (hasAccreditationComments()) {
            renderAccreditationComments(accreditationCommentsContainer);
        }

        return accreditationTimeView;
    }

    private void renderAccreditationMessage(@NonNull final MPTextView messageTextView, @NonNull final Context context) {
        final String accreditationMessage = props.accreditationMessage;
        if (accreditationMessage == null || accreditationMessage.isEmpty()) {
            messageTextView.setVisibility(View.GONE);
        } else {
            final CharSequence textSpan = new SpannableStringBuilder("  " + accreditationMessage);

            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.px_time);
            final Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            final PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;
            drawable.setColorFilter(ContextCompat.getColor(context, R.color.px_warm_grey_with_alpha), mode);

            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            messageTextView.setText(textSpan);
            messageTextView.setCompoundDrawablesRelative(drawable, null, null, null);
            messageTextView.setVisibility(View.VISIBLE);
        }
    }

    private void renderAccreditationComments(@NonNull final ViewGroup parent) {
        final List<AccreditationComment> commentComponents = getAccreditationCommentComponents();

        for (final AccreditationComment commentComp : commentComponents) {
            parent.addView(commentComp.render(parent));
        }
    }
}