package com.mercadopago.android.px.internal.features.express.animations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import java.util.Iterator;

public final class BottomSlideAnimationSet {

    @Nullable private BottomSlideAnimation firstAnimation;

    public void initialize(@NonNull final Iterable<View> views) {
        firstAnimation = createAnimation(views.iterator());
    }

    private BottomSlideAnimation createAnimation(@NonNull final Iterator<View> iterator) {
        if (iterator.hasNext()) {
            final View view = iterator.next();
            if (view != null) {
                final BottomSlideAnimation nextAnimation = createAnimation(iterator);
                return new BottomSlideAnimation(view, nextAnimation);
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * Expand the view is not animating
     */
    public void slideUp() {
        if (firstAnimation != null) {
            firstAnimation.slideUp();
        }
    }

    /**
     * Collapse the view is not animating
     */
    public void slideDown() {
        if (firstAnimation != null) {
            firstAnimation.slideDown();
        }
    }
}