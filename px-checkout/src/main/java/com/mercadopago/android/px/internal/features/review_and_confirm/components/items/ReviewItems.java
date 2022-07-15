package com.mercadopago.android.px.internal.features.review_and_confirm.components.items;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ItemModel;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ItemsModel;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.internal.view.Renderer;
import com.mercadopago.android.px.internal.view.RendererFactory;

public class ReviewItems extends CompactComponent<ReviewItems.Props, Void> {

    public static final class Props {
        /* default */ final ItemsModel itemsModel;
        /* default */ final Integer collectorIcon;
        /* default */ final String quantityLabel;
        /* default */ final String unitPriceLabel;

        public Props(final ItemsModel itemsModel, @Nullable @DrawableRes final Integer collectorIcon,
            final String quantityLabel,
            final String unitPriceLabel) {
            this.itemsModel = itemsModel;
            this.collectorIcon = collectorIcon;
            this.quantityLabel = quantityLabel;
            this.unitPriceLabel = unitPriceLabel;
        }

        /* default */ ItemsModel getItemsModel() {
            return itemsModel;
        }

        @Nullable
        /* default */ Integer getCollectorIcon() {
            return collectorIcon;
        }
    }

    public ReviewItems(@NonNull final Props props) {
        super(props);
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {
        final Context context = parent.getContext();
        final LinearLayout linearLayout = createMainLayout(context);

        for (final ItemModel itemModel : props.getItemsModel().itemsModelList) {
            addReviewItem(new ReviewItem(new ReviewItem.Props(
                    itemModel,
                    getIcon(props),
                    props.quantityLabel,
                    props.unitPriceLabel)),
                linearLayout);
        }
        return linearLayout;
    }

    @VisibleForTesting
    @DrawableRes
    int getIcon(final ReviewItems.Props props) {
        if (props.getItemsModel().hasUniqueItem()) {
            return props.getCollectorIcon() == null ? R.drawable.px_review_item_default : props.getCollectorIcon();
        } else {
            return R.drawable.px_review_item_default;
        }
    }

    @NonNull
    private LinearLayout createMainLayout(@NonNull final Context context) {
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    private void addReviewItem(@NonNull final ReviewItem reviewItem, final ViewGroup container) {
        final Renderer renderer = RendererFactory.create(container.getContext(), reviewItem);
        renderer.render(container);
    }
}