package com.mercadopago.android.px.addons;

import androidx.annotation.NonNull;

public final class PXBehaviourConfigurer {

    private SecurityBehaviour securityBehaviour;
    private ESCManagerBehaviour escManagerBehaviour;
    private TrackingBehaviour trackingBehaviour;
    private LocaleBehaviour localeBehaviour;
    private FlowBehaviour flowBehaviour;
    private ThreeDSBehaviour threeDSBehaviour;
    private TokenDeviceBehaviour tokenDeviceBehaviour;
    private AuthenticationBehaviour authenticationBehaviour;

    public PXBehaviourConfigurer with(@NonNull final ESCManagerBehaviour escManagerBehaviour) {
        this.escManagerBehaviour = escManagerBehaviour;
        return this;
    }

    public PXBehaviourConfigurer with(@NonNull final SecurityBehaviour securityBehaviour) {
        this.securityBehaviour = securityBehaviour;
        return this;
    }

    public PXBehaviourConfigurer with(@NonNull final TrackingBehaviour trackingBehaviour) {
        this.trackingBehaviour = trackingBehaviour;
        return this;
    }

    public PXBehaviourConfigurer with(@NonNull final LocaleBehaviour localeBehaviour) {
        this.localeBehaviour = localeBehaviour;
        return this;
    }

    public PXBehaviourConfigurer with(@NonNull final FlowBehaviour flowBehaviour) {
        this.flowBehaviour = flowBehaviour;
        return this;
    }

    public PXBehaviourConfigurer with(@NonNull final ThreeDSBehaviour threeDSBehaviour) {
        this.threeDSBehaviour = threeDSBehaviour;
        return this;
    }

    public PXBehaviourConfigurer with(@NonNull final TokenDeviceBehaviour tokenDeviceBehaviour) {
        this.tokenDeviceBehaviour = tokenDeviceBehaviour;
        return this;
    }

    public PXBehaviourConfigurer with(@NonNull final AuthenticationBehaviour authenticationBehaviour) {
        this.authenticationBehaviour = authenticationBehaviour;
        return this;
    }

    public void configure() {
        BehaviourProvider.set(securityBehaviour);
        BehaviourProvider.set(escManagerBehaviour);
        BehaviourProvider.set(trackingBehaviour);
        BehaviourProvider.set(localeBehaviour);
        BehaviourProvider.set(flowBehaviour);
        BehaviourProvider.set(threeDSBehaviour);
        BehaviourProvider.set(tokenDeviceBehaviour);
        BehaviourProvider.set(authenticationBehaviour);
    }
}