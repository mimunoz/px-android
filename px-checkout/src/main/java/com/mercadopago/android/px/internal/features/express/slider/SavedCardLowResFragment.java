package com.mercadopago.android.px.internal.features.express.slider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.viewmodel.drawables.SavedCardDrawableFragmentItem;

public class SavedCardLowResFragment extends SavedCardFragment {

    @SuppressWarnings("TypeMayBeWeakened")
    @NonNull
    public static Fragment getInstance(final SavedCardDrawableFragmentItem model) {
        final SavedCardLowResFragment instance = new SavedCardLowResFragment();
        instance.storeModel(model);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.px_fragment_saved_card_low_res, container, false);
    }
}