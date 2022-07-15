package com.mercadopago.android.px.tracking.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.addons.BehaviourProvider;
import com.mercadopago.android.px.addons.model.Track;
import com.mercadopago.android.px.addons.model.internal.Experiment;
import com.mercadopago.android.px.internal.core.FlowIdProvider;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.util.Logger;
import com.mercadopago.android.px.model.CheckoutType;
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.mercadopago.android.px.internal.util.TextUtil.isEmpty;

public final class MPTracker {

    private static final String TAG = "PXTracker";
    private static final String ATTR_EXTRA_INFO = "extra_info";
    private static final String ATTR_FLOW_DETAIL = "flow_detail";
    private static final String ATTR_FLOW_NAME = "flow";
    private static final String ATTR_SESSION_ID = "session_id";
    private static final String ATTR_SESSION_TIME = "session_time";
    private static final String ATTR_CHECKOUT_TYPE = "checkout_type";
    private static final String ATTR_SECURITY_ENABLED = "security_enabled";
    private static final String ATTR_EXPERIMENTS = "experiments";

    private static MPTracker trackerInstance;

    /**
     * Added in 4.3.0 version - temporal replacement for tracking additional params.
     */
    @Nullable private Map<String, ?> flowDetail;

    @Nullable private String sessionId;

    @CheckoutType @Nullable private String checkoutType;

    private long initSessionTimestamp;

    private boolean securityEnabled;

    @NonNull private List<Experiment> experiments = Collections.emptyList();

    @NonNull private FlowIdProvider flowIdProvider = Session.getInstance().getNetworkModule().getFlowIdProvider();

    private MPTracker() {
        // do nothing
    }

    public static synchronized MPTracker getInstance() {
        if (trackerInstance == null) {
            trackerInstance = new MPTracker();
        }
        return trackerInstance;
    }

    /**
     * Set a map to add information to the library's screen and event tracks.
     *
     * @param flowDetail A map with extra information about the flow in your app that uses the checkout.
     */
    public void setFlowDetail(@NonNull final Map<String, ?> flowDetail) {
        this.flowDetail = flowDetail;
    }

    /**
     * Set a session id to identify differents user's session.
     *
     * @param sessionId The id that identifies a session
     */
    public void setSessionId(@Nullable final String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Set if the user will be challenged with security validation or not
     *
     * @param securityEnabled indicates if the user will be challenged with fingerprint/pin/pattern when pays
     */
    public void setSecurityEnabled(final boolean securityEnabled) {
        this.securityEnabled = securityEnabled;
    }

    /**
     * Set all A/B testing experiments that are active.
     *
     * @param experiments The active A/B testing experiments.
     */
    public void setExperiments(@NonNull final List<Experiment> experiments) {
        this.experiments = experiments;
    }

    public void track(@NonNull final Track track) {
        // Event friction case needs to add flow detail in a different way. We ignore this case for now.
        if (!FrictionEventTracker.PATH.equals(track.getPath())) {
            addAdditionalFlowInfo(track.getData());
        } else {
            addAdditionalFlowIntoExtraInfo(track.getData());
        }
        BehaviourProvider.getTrackingBehaviour().track(track);
        Logger.debug(TAG, "Type: " + track.getType().name() + " - Path: " + track.getPath());
        Logger.debug(TAG, track.getData());
    }

    private void addAdditionalFlowIntoExtraInfo(@NonNull final Map<String, Object> data) {
        if (data.containsKey(ATTR_EXTRA_INFO)) {
            final Object o = data.get(ATTR_EXTRA_INFO);
            try {
                final Map<String, Object> value = (Map<String, Object>) o;
                value.put(ATTR_FLOW_NAME, flowIdProvider.getFlowId());
                value.put(ATTR_SESSION_ID, sessionId);
                value.put(ATTR_SESSION_TIME, getSecondsAfterInit());
                value.put(ATTR_CHECKOUT_TYPE, checkoutType);
                value.put(ATTR_SECURITY_ENABLED, securityEnabled);
                value.put(ATTR_EXPERIMENTS, getExperimentsLabel());
            } catch (final ClassCastException e) {
                // do nothing.
            }
        }
    }

    private void addAdditionalFlowInfo(@NonNull final Map<String, Object> data) {
        data.put(ATTR_FLOW_DETAIL, flowDetail);
        data.put(ATTR_FLOW_NAME, flowIdProvider.getFlowId());
        data.put(ATTR_SESSION_ID, sessionId);
        data.put(ATTR_SESSION_TIME, getSecondsAfterInit());
        data.put(ATTR_CHECKOUT_TYPE, checkoutType);
        data.put(ATTR_SECURITY_ENABLED, securityEnabled);
        data.put(ATTR_EXPERIMENTS, getExperimentsLabel());
    }

    private String getExperimentsLabel() {
        final StringBuilder label = new StringBuilder();

        for (final Experiment experiment : experiments) {
            if (!isEmpty(label)) {
                label.append(",");
            }

            label.append(experiment.getName());
            label.append(" - ");
            label.append(experiment.getVariant().getName());
        }

        return label.toString();
    }

    private long getSecondsAfterInit() {
        if (initSessionTimestamp == 0) {
            initializeSessionTime();
        }
        final long milliseconds = Calendar.getInstance().getTime().getTime() - initSessionTimestamp;
        return TimeUnit.MILLISECONDS.toSeconds(milliseconds);
    }

    public void initializeSessionTime() {
        initSessionTimestamp = Calendar.getInstance().getTime().getTime();
    }

    public void hasExpressCheckout(final boolean hasExpressCheckout) {
        if (hasExpressCheckout) {
            checkoutType = CheckoutType.ONE_TAP;
        } else {
            checkoutType = CheckoutType.TRADITIONAL;
        }
    }
}