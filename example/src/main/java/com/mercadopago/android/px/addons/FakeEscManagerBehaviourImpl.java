package com.mercadopago.android.px.addons;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import com.mercadopago.android.px.addons.model.EscDeleteReason;
import com.mercadopago.android.px.internal.util.TextUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FakeEscManagerBehaviourImpl implements ESCManagerBehaviour {

    private static final String TAG = FakeEscManagerBehaviourImpl.class.getSimpleName();
    private final Map<String, String> storage = new HashMap<>();

    @Override
    public void setSessionId(@NonNull final String sessionId) {
    }

    @Override
    public void setFlow(@NonNull final String flow) {
    }

    @Nullable
    @Override
    public String getESC(@Nullable final String cardId, @Nullable final String firstDigits,
        @Nullable final String lastDigits) {
        if (cardId != null && !cardId.isEmpty()) {
            Log.d(TAG, "get esc with card id: " + cardId);
            return storage.get(cardId);
        } else if (TextUtil.isNotEmpty(firstDigits) && TextUtil.isNotEmpty(lastDigits)) {
            Log.d(TAG, "get esc with first digits: " + firstDigits + " and last digits: " + lastDigits);
            return storage.get(getKey(firstDigits, lastDigits));
        } else {
            Log.d(TAG, "get esc with invalid parameters");
            return TextUtil.EMPTY;
        }
    }

    @Override
    public boolean saveESCWith(@NonNull final String cardId, @NonNull final String esc) {
        if (TextUtil.isNotEmpty(cardId) && TextUtil.isNotEmpty(esc)) {
            storage.put(cardId, esc);
            return true;
        } else if (TextUtil.isNotEmpty(cardId) && TextUtil.isEmpty(esc)) {
            Log.d(TAG, "save esc with invalid esc");
            deleteESCWith(cardId, EscDeleteReason.NO_ESC, null);
        } else {
            Log.d(TAG, "save esc with invalid key");
        }
        return false;
    }

    @Override
    public boolean saveESCWith(@NonNull final String firstDigits, @NonNull final String lastDigits,
        @NonNull final String esc) {
        storage.put(getKey(firstDigits, lastDigits), esc);
        return true;
    }

    @Deprecated
    @Override
    public void deleteESCWith(@NonNull final String cardId) {
        throw new RuntimeException("Use the method with Reason and detail params");
    }

    @Deprecated
    @Override
    public void deleteESCWith(@NonNull final String firstDigits, @NonNull final String lastDigits) {
        throw new RuntimeException("There are no cases where we need to delete this ESCs");
    }

    @Override
    public void deleteESCWith(@NonNull final String cardId, @NonNull final EscDeleteReason reason,
        @Nullable final String detail) {
        Log.d(TAG, "delete esc for key: " + cardId + " with reason: " + reason.name() + " with detail: " + detail);
        storage.remove(cardId);
    }

    private String getKey(@NonNull final String firstDigits, @NonNull final String lastDigits) {
        return firstDigits + "_" + lastDigits;
    }

    @NonNull
    @Override
    public Set<String> getESCCardIds() {
        return storage.keySet();
    }

    @Override
    public boolean isESCEnabled() {
        return true;
    }
}