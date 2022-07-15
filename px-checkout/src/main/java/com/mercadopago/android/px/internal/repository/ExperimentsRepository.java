package com.mercadopago.android.px.internal.repository;

import androidx.annotation.Nullable;
import com.mercadopago.android.px.addons.model.internal.Experiment;
import java.util.List;

public interface ExperimentsRepository {

    void reset();

    void configure(@Nullable final List<Experiment> experiments);

    List<Experiment> getExperiments();
}
