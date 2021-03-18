package com.mercadopago.android.px.addons;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.internal.ESCManagerDefaultBehaviour;
import com.mercadopago.android.px.addons.internal.FlowDefaultBehaviour;
import com.mercadopago.android.px.addons.internal.SecurityDefaultBehaviour;
import com.mercadopago.android.px.addons.internal.ThreeDSDefaultBehaviour;
import com.mercadopago.android.px.addons.internal.TrackingDefaultBehaviour;

public final class BehaviourProvider {

    private static SecurityBehaviour securityBehaviour;
    private static ESCManagerBehaviour escManagerBehaviour;
    private static TrackingBehaviour trackingBehaviour;
    private static FlowBehaviour flowBehaviour;
    private static ThreeDSBehaviour threeDSBehaviour;

    private BehaviourProvider() {
    }

    /* default */
    static void set(final SecurityBehaviour securityBehaviour) {
        BehaviourProvider.securityBehaviour = securityBehaviour;
    }

    /* default */
    static void set(final ESCManagerBehaviour escManagerBehaviour) {
        BehaviourProvider.escManagerBehaviour = escManagerBehaviour;
    }

    /* default */
    static void set(final TrackingBehaviour trackingBehaviour) {
        BehaviourProvider.trackingBehaviour = trackingBehaviour;
    }

    /* default */
    @Deprecated
    static void set(final LocaleBehaviour localeBehaviour) {
    }

    /* default */
    static void set(final FlowBehaviour flowBehaviour) {
        BehaviourProvider.flowBehaviour = flowBehaviour;
    }

    /* default */
    static void set(final ThreeDSBehaviour threeDSBehaviour) {
        BehaviourProvider.threeDSBehaviour = threeDSBehaviour;
    }

    @NonNull
    public static SecurityBehaviour getSecurityBehaviour() {
        return securityBehaviour != null ? securityBehaviour : new SecurityDefaultBehaviour();
    }

    /**
     * @param session    session id for tracking purpose
     * @param escEnabled indicates if current flow works with esc or not
     * @return EscManagerBehaviour implementation.
     * @deprecated use {@link #getEscManagerBehaviour(String, String)} instead
     */
    @Deprecated
    @NonNull
    public static ESCManagerBehaviour getEscManagerBehaviour(@NonNull final String session, final boolean escEnabled) {
        final ESCManagerBehaviour escManagerBehaviour = resolveEscImplementation(escEnabled);
        escManagerBehaviour.setSessionId(session);
        return escManagerBehaviour;
    }

    /**
     * @param session session id for tracking purpose
     * @return EscManagerBehaviour implementation.
     * @deprecated use {@link #getEscManagerBehaviour(String, String)} instead
     */
    @Deprecated
    @NonNull
    public static ESCManagerBehaviour getEscManagerBehaviour(@NonNull final String session) {
        final ESCManagerBehaviour escManagerBehaviour = resolveEscImplementation(true);
        escManagerBehaviour.setSessionId(session);
        return escManagerBehaviour;
    }

    /**
     * @param session session id for tracking purpose
     * @param flow    flow name for tracking purpose
     * @return EscManagerBehaviour implementation.
     */
    @NonNull
    public static ESCManagerBehaviour getEscManagerBehaviour(@NonNull final String session,
        @NonNull final String flow) {
        final ESCManagerBehaviour escManagerBehaviour = resolveEscImplementation(true);
        escManagerBehaviour.setSessionId(session);
        escManagerBehaviour.setFlow(flow);
        return escManagerBehaviour;
    }

    /**
     * @deprecated use {@link #getTrackingBehaviour()} instead
     */
    @Deprecated
    @NonNull
    public static TrackingBehaviour getTrackingBehaviour(@NonNull final String applicationContext) {
        if (trackingBehaviour != null) {
            trackingBehaviour.setApplicationContext(applicationContext);
            return trackingBehaviour;
        } else {
            return TrackingDefaultBehaviour.INSTANCE;
        }
    }

    @NonNull
    public static TrackingBehaviour getTrackingBehaviour() {
        return trackingBehaviour != null ? trackingBehaviour : TrackingDefaultBehaviour.INSTANCE;
    }

    @NonNull
    @Deprecated
    public static LocaleBehaviour getLocaleBehaviour() {
        return null;
    }

    @NonNull
    public static FlowBehaviour getFlowBehaviour() {
        return flowBehaviour != null ? flowBehaviour : new FlowDefaultBehaviour();
    }

    public static ThreeDSBehaviour getThreeDSBehaviour() {
        return threeDSBehaviour != null ? threeDSBehaviour : new ThreeDSDefaultBehaviour();
    }

    @NonNull
    private static ESCManagerBehaviour resolveEscImplementation(final boolean escEnabled) {
        if (escEnabled) {
            return escManagerBehaviour != null ? escManagerBehaviour : new ESCManagerDefaultBehaviour();
        } else {
            return new ESCManagerDefaultBehaviour();
        }
    }
}