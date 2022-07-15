package com.mercadopago.android.px.addons.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.EscDeleteReason;
import java.util.Collections;
import java.util.Set;

public final class ESCManagerDefaultBehaviour implements ESCManagerBehaviour {

    @Override
    public void setSessionId(@NonNull final String sessionId) {
        // do nothing
    }

    @Override
    public void setFlow(@NonNull final String flow) {
        // do nothing
    }

    @Nullable
    @Override
    public String getESC(@Nullable final String cardId, @Nullable final String firstDigits,
        @Nullable final String lastDigits) {
        return null;
    }

    @Override
    public boolean saveESCWith(@NonNull final String cardId, @NonNull final String esc) {
        return false;
    }

    @Override
    public boolean saveESCWith(@NonNull final String firstDigits, @NonNull final String lastDigits,
        @NonNull final String esc) {
        return false;
    }

    /**
     *
     * @deprecated use the one with Reason and detail parameters
     */
    @Deprecated
    @Override
    public void deleteESCWith(@NonNull final String cardId) {
        // do nothing
    }

    /**
     *
     * @deprecated there are no cases where we need to delete this ESCs
     */
    @Deprecated
    @Override
    public void deleteESCWith(@NonNull final String firstDigits, @NonNull final String lastDigits) {
        // do nothing
    }

    @Override
    public void deleteESCWith(@NonNull final String cardId, @NonNull final EscDeleteReason reason,
        @Nullable final String detail) {
        // do nothing
    }

    @NonNull
    @Override
    public Set<String> getESCCardIds() {
        return Collections.emptySet();
    }

    @Override
    public boolean isESCEnabled() {
        return false;
    }
}