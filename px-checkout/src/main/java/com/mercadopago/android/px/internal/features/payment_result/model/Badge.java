package com.mercadopago.android.px.internal.features.payment_result.model;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by vaserber on 10/26/17.
 */

public class Badge {

    //armar componente Badge que va como hijo
    public static final String DEFAULT_BADGE_IMAGE = "px_badge_pending";
    public static final String ERROR_BADGE_IMAGE = "px_badge_error";
    public static final String WARNING_BADGE_IMAGE = "px_badge_warning";
    public static final String CHECK_BADGE_IMAGE = "px_badge_check";
    public static final String PENDING_BADGE_IMAGE = "px_badge_pending";

    public Badge(@Badges String value) {
    }

    @StringDef({ DEFAULT_BADGE_IMAGE, ERROR_BADGE_IMAGE, WARNING_BADGE_IMAGE, CHECK_BADGE_IMAGE, PENDING_BADGE_IMAGE })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Badges {
    }

    @StringDef({ CHECK_BADGE_IMAGE, PENDING_BADGE_IMAGE })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ApprovedBadges {
    }
}
