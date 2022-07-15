package com.mercadopago.android.px.internal.features.business_result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Button;
import com.mercadopago.android.px.internal.view.Footer;
import com.mercadopago.android.px.model.ExitAction;

public final class BusinessResultLegacyRenderer {

    private BusinessResultLegacyRenderer() {
    }

    public static void render(@NonNull final ViewGroup parent, @NonNull final ActionDispatcher callback,
        @NonNull final BusinessPaymentResultViewModel model) {
        final Button.Props primaryButtonProps = getButtonProps(model.primaryAction);
        final Button.Props secondaryButtonProps = getButtonProps(model.secondaryAction);
        final Footer footer =
            new Footer(new Footer.Props(primaryButtonProps, secondaryButtonProps), callback);
        final View footerView = footer.render(parent);
        parent.addView(footerView);
    }

    @Nullable
    private static Button.Props getButtonProps(@Nullable final ExitAction action) {
        if (action != null) {
            final String label = action.getName();
            return new Button.Props(label, action);
        }
        return null;
    }
}