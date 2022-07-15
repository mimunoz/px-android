package com.mercadopago.android.px.internal.features.express.animations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.mercadopago.android.px.R;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public final class BottomSlideAnimation {

    private final View targetView;
    private final Animation slideUp;
    private final Animation slideDown;

    public BottomSlideAnimation(final View view) {
        this(view, null);
    }

    /* default */ BottomSlideAnimation(@NonNull final View view, @Nullable final BottomSlideAnimation nextAnimation) {
        targetView = view;
        slideUp = AnimationUtils.loadAnimation(view.getContext(), R.anim.px_slide_up_in);
        slideUp.setAnimationListener(
            new InOutAnimationListener(view, VISIBLE, nextAnimation != null ? nextAnimation::slideUp : null));
        slideDown = AnimationUtils.loadAnimation(view.getContext(), R.anim.px_slide_down_out);
        slideDown.setAnimationListener(
            new InOutAnimationListener(view, INVISIBLE, nextAnimation != null ? nextAnimation::slideDown : null));
    }

    /**
     * Expand the view is not animating
     */
    public void slideUp() {
        targetView.clearAnimation();
        targetView.startAnimation(slideUp);
    }

    /**
     * Collapse the view is not animating
     */
    public void slideDown() {
        targetView.clearAnimation();
        targetView.startAnimation(slideDown);
    }
}