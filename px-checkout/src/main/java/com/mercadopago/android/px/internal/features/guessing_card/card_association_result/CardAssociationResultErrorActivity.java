package com.mercadopago.android.px.internal.features.guessing_card.card_association_result;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.mercadolibre.android.ui.widgets.MeliButton;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.internal.MercadoPagoCardStorage;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.features.guessing_card.GuessingCardActivity;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.tracking.internal.views.CardAssociationResultViewTrack;

public class CardAssociationResultErrorActivity extends PXActivity {
    private static final String PARAM_ACCESS_TOKEN = "accessToken";
    private static final String PARAM_MERCADO_PAGO_CARD_STORAGE = "mercadoPagoCardStorage";

    /* default */ String accessToken;

    public static void startCardAssociationResultErrorActivity(final Activity callerActivity,
        final String accessToken) {
        final Intent intent = new Intent(callerActivity, CardAssociationResultErrorActivity.class);
        intent.putExtra(PARAM_ACCESS_TOKEN, accessToken);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        callerActivity.startActivity(intent);
    }

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        final Intent intent = getIntent();
        accessToken = intent.getStringExtra(PARAM_ACCESS_TOKEN);

        setContentView(R.layout.px_card_association_result_error);
        ViewUtils.setStatusBarColor(ContextCompat.getColor(this, R.color.ui_components_warning_color), getWindow());

        final MeliButton retryButton = findViewById(R.id.mpsdkCardAssociationResultRetryButton);
        retryButton.setOnClickListener(v -> {
            // Call GuessingCard flow again forwarding the result
            MercadoPagoCardStorage mercadoPagoCardStorage = new MercadoPagoCardStorage.Builder(accessToken).build();
            final Intent guessingCardActivityIntent =
                new Intent(CardAssociationResultErrorActivity.this, GuessingCardActivity.class);
            guessingCardActivityIntent.putExtra(PARAM_MERCADO_PAGO_CARD_STORAGE, mercadoPagoCardStorage);
            guessingCardActivityIntent.putExtra(GuessingCardActivity.PARAM_INCLUDES_PAYMENT, false);
            guessingCardActivityIntent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(guessingCardActivityIntent);

            finish();
        });
        final MeliButton exitButton = findViewById(R.id.mpsdkCardAssociationResultExitButton);
        exitButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        new CardAssociationResultViewTrack(CardAssociationResultViewTrack.Type.ERROR).track();
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PARAM_ACCESS_TOKEN, accessToken);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            accessToken = savedInstanceState.getString(PARAM_ACCESS_TOKEN);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
