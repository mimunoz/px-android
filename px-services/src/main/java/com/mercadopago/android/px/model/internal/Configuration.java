package com.mercadopago.android.px.model.internal;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public final class Configuration implements Serializable {

    private boolean sonicBranding;
    private List<String> escBlacklistedStatus;

    public boolean sonicBrandingEnabled() {
        return sonicBranding;
    }

    public List<String> getEscBlacklistedStatus() {
        return escBlacklistedStatus != null ? escBlacklistedStatus : Collections.emptyList();
    }
}
