package com.mercadopago.android.px.internal.datasource;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.internal.repository.ExperimentsRepository;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.addons.model.internal.Experiment;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ExperimentsService implements ExperimentsRepository {

    private static final String PREF_EXPERIMENTS = "PREF_EXPERIMENTS";

    @NonNull private final SharedPreferences sharedPreferences;

    @Nullable private List<Experiment> experiments;

    public ExperimentsService(@NonNull final SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void reset() {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear().apply();
        experiments = null;
    }

    @Override
    public void configure(@Nullable final List<Experiment> experiments) {
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(PREF_EXPERIMENTS, JsonUtil.toJson(experiments));
        edit.apply();
    }

    @Override
    @NonNull
    public List<Experiment> getExperiments() {
        if (experiments == null) {
            final Type type = new TypeToken<List<Experiment>>() {
            }.getType();
            final List<Experiment> experimentsFromPreference = JsonUtil
                .fromJson(sharedPreferences.getString(PREF_EXPERIMENTS, new ArrayList<Experiment>().toString()), type);
            return experimentsFromPreference == null ? new ArrayList<>() : experimentsFromPreference;
        } else {
            return experiments;
        }
    }
}
