package com.mercadopago.android.px.internal.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.experiments.BadgeVariant;
import com.mercadopago.android.px.internal.experiments.PulseVariant;
import com.mercadopago.android.px.internal.experiments.Variant;
import com.mercadopago.android.px.internal.experiments.VariantHandler;
import com.mercadopago.android.px.internal.view.experiments.ExperimentHelper;
import com.mercadopago.android.px.internal.viewmodel.GoingToModel;
import java.util.List;
import org.jetbrains.annotations.NotNull;

import static com.mercadopago.android.px.internal.util.ViewUtils.hasEndedAnim;

public class PaymentMethodHeaderView extends FrameLayout {

    /* default */ final View titleView;

    /* default */ final ImageView helper;

    /* default */ final Animation rotateUp;

    /* default */ final Animation rotateDown;

    private FrameLayout experimentContainer;

    private PulseView pulse;

    private ImageView arrow;

    private final TitlePager titlePager;

    private boolean isDisabled;

    public interface Listener {
        void onDescriptorViewClicked();

        void onDisabledDescriptorViewClick();

        void onInstallmentsSelectorCancelClicked();
    }

    public PaymentMethodHeaderView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentMethodHeaderView(final Context context, @Nullable final AttributeSet attrs,
        final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.px_view_installments_header, this, true);
        rotateUp = AnimationUtils.loadAnimation(context, R.anim.px_rotate_up);
        rotateDown = AnimationUtils.loadAnimation(context, R.anim.px_rotate_down);
        titleView = findViewById(R.id.installments_title);
        experimentContainer = findViewById(R.id.pulse_experiment_container);
        titlePager = findViewById(R.id.title_pager);
        helper = findViewById(R.id.helper);
        titleView.setVisibility(GONE);
    }

    public void updateData(final boolean hasPayerCost, final boolean isDisabled) {
        final boolean isEspandible = hasPayerCost && !isDisabled;
        this.isDisabled = isDisabled;

        showTitlePager(hasPayerCost);
        setArrowVisibility(isEspandible);
        setHelperVisibility(isDisabled);

        setClickable(hasPayerCost || isDisabled);
    }

    public void setListener(final Listener listener) {
        setOnClickListener(v -> {
            if (isDisabled) {
                listener.onDisabledDescriptorViewClick();
            } else if (hasEndedAnim(arrow)) {
                if (titleView.getVisibility() == VISIBLE) {
                    arrow.startAnimation(rotateDown);
                    listener.onInstallmentsSelectorCancelClicked();
                } else {
                    arrow.startAnimation(rotateUp);
                    listener.onDescriptorViewClicked();
                    if (pulse != null) {
                        pulse.stopRippleAnimation();
                    }
                }
            }
        });
    }

    public void configureExperiment(@NonNull final List<Variant> variants) {
        for(Variant variant: variants) {
            variant.process(new VariantHandler() {
                @Override
                public void visit(@NotNull final PulseVariant variant) {
                    configurePulseExperiment(variant);
                }

                @Override
                public void visit(@NotNull final BadgeVariant variant) {
                    configureBadgeExperiment(variant);
                }
            });
        }
    }

    private void configureBadgeExperiment(@NonNull final BadgeVariant variant) {
        titlePager.setBadgeExperimentVariant(variant);
    }

    private void configurePulseExperiment(@NonNull final PulseVariant variant) {
        if (experimentContainer.getChildCount() == 0) {
            ExperimentHelper.INSTANCE.applyExperimentViewBy(experimentContainer, variant);
        }

        pulse = experimentContainer.findViewById(R.id.pulse);
        if (pulse != null) {
            pulse.startRippleAnimation();
        }
        arrow = experimentContainer.findViewById(R.id.arrow);
    }

    public void showInstallmentsListTitle() {
        titleView.setVisibility(VISIBLE);
        titlePager.setVisibility(GONE);
    }

    public void showTitlePager(final boolean isClickable) {
        if (titleView.getVisibility() == VISIBLE) {
            arrow.startAnimation(rotateDown);
        }

        titlePager.setVisibility(VISIBLE);
        titleView.setVisibility(GONE);

        setClickable(isClickable);
    }

    public void trackPagerPosition(float positionOffset, final Model model) {
        if (model.goingTo == GoingToModel.BACKWARDS) {
            positionOffset = 1.0f - positionOffset;
        }

        if (model.currentIsExpandable) {
            if (model.nextIsExpandable) {
                experimentContainer.setAlpha(1.0f);
            } else {
                experimentContainer.setAlpha(1.0f - positionOffset);
            }
        } else {
            if (model.nextIsExpandable) {
                experimentContainer.setAlpha(positionOffset);
            } else {
                experimentContainer.setAlpha(0.0f);
            }
        }
    }

    public void setArrowVisibility(final boolean visible) {
        experimentContainer.setAlpha(visible ? 1.0f : 0.0f);
    }

    public void setHelperVisibility(final boolean visible) {
        helper.setVisibility(visible ? VISIBLE : GONE);
    }

    public static class Model {
        final GoingToModel goingTo;
        final boolean currentIsExpandable;
        final boolean nextIsExpandable;

        public Model(final GoingToModel goingTo, final boolean currentIsExpandable,
            final boolean nextIsExpandable) {
            this.goingTo = goingTo;
            this.currentIsExpandable = currentIsExpandable;
            this.nextIsExpandable = nextIsExpandable;
        }
    }
}