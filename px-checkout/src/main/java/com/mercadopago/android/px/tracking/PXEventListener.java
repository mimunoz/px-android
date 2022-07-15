package com.mercadopago.android.px.tracking;

import androidx.annotation.NonNull;
import java.util.Map;

/**
 * @deprecated Deprecated due to new tracking implementation standards. Use {@link com.mercadopago.android.px.tracking.PXTrackingListener}
 * instead.
 */
@Deprecated
public interface PXEventListener<T> {

    /**
     * This method is called when a new screen is shown to the user.
     *
     * @param screenId Id of the screen that is shown. Screen Ids start with prefixes that are described in
     * TrackingUtil.java, under the key SCREEN_ID.
     * @param extraParams Map containing information that the screen is showing. It also contains information about
     * errors if the screen launched is the Error screen. The keys of the map are the ones described under the key
     * @deprecated Deprecated due to new tracking implementation standards. Use {@link com.mercadopago.android.px.tracking.PXTrackingListener#onView(String,
     * Map)} instead.
     */
    @Deprecated
    void onScreenLaunched(@NonNull final String screenId, @NonNull final Map<String, String> extraParams);

    /**
     * This method is called when an important event happens that needs tracking. Events: Checkout initialization,
     * Confirm payment button pressed.
     *
     * @param event Information of the event
     * @deprecated Deprecated due to new tracking implementation standards. Use {@link com.mercadopago.android.px.tracking.PXTrackingListener#onEvent(String,
     * Map)} instead.
     */
    @Deprecated
    void onEvent(@NonNull final T event);
}