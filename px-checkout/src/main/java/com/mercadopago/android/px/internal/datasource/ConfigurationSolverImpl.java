package com.mercadopago.android.px.internal.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.AmountConfiguration;
import com.mercadopago.android.px.model.CustomSearchItem;
import java.util.List;

public class ConfigurationSolverImpl implements ConfigurationSolver {

    @NonNull private final String defaultSelectedAmountConfiguration;
    @NonNull private final List<CustomSearchItem> customSearchItems;

    public ConfigurationSolverImpl(
        @NonNull final String defaultSelectedAmountConfiguration,
        @NonNull final List<CustomSearchItem> customSearchItems) {
        this.defaultSelectedAmountConfiguration = defaultSelectedAmountConfiguration;
        this.customSearchItems = customSearchItems;
    }

    @Override
    @NonNull
    public String getDefaultSelectedAmountConfiguration() {
        return defaultSelectedAmountConfiguration;
    }

    @Override
    @NonNull
    public String getConfigurationHashFor(@NonNull final String customOptionId) {
        for (final CustomSearchItem customSearchItem : customSearchItems) {
            if (customSearchItem.getId().equalsIgnoreCase(customOptionId)) {
                return customSearchItem.getDefaultAmountConfiguration();
            }
        }
        return TextUtil.EMPTY;
    }

    @Override
    @Nullable
    public AmountConfiguration getAmountConfigurationFor(@NonNull final String customOptionId) {
        for (final CustomSearchItem customSearchItem : customSearchItems) {
            if (customSearchItem.getId().equalsIgnoreCase(customOptionId)) {
                return customSearchItem.getAmountConfiguration(customSearchItem.getDefaultAmountConfiguration());
            }
        }
        return null;
    }

    @Override
    @Nullable
    public AmountConfiguration getAmountConfigurationFor(@NonNull final String customOptionId,
        @NonNull final String configurationHash) {
        for (final CustomSearchItem customSearchItem : customSearchItems) {
            if (customSearchItem.getId().equalsIgnoreCase(customOptionId)) {
                return customSearchItem.getAmountConfiguration(configurationHash);
            }
        }
        return null;
    }
}
