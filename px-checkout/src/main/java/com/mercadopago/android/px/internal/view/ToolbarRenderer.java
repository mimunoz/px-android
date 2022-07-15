package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.mercadopago.android.px.R;

public class ToolbarRenderer extends Renderer<ToolbarComponent> {

    @Override
    @CallSuper
    public View render(@NonNull final ToolbarComponent component, @NonNull final Context context, final ViewGroup parent) {
        final View view = inflate(R.layout.px_toolbar_renderer, parent);
        renderToolbar(view, component, context);
        return view;
    }

    private void renderToolbar(final View view, final ToolbarComponent component, final Context context) {

        final AppCompatActivity activity = (AppCompatActivity) context;
        final Toolbar toolbar = view.findViewById(R.id.toolbar);

        if (!component.props.toolbarVisible) {

            toolbar.setVisibility(View.GONE);
        } else {

            final TextView titleView = view.findViewById(R.id.title);

            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            });

            titleView.setText(component.props.toolbarTitle);
        }
    }
}
