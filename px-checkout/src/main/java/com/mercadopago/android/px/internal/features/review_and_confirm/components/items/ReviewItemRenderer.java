package com.mercadopago.android.px.internal.features.review_and_confirm.components.items;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.mercadolibre.android.picassodiskcache.PicassoDiskLoader;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.CircleTransform;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.view.Renderer;
import com.squareup.picasso.Picasso;
import java.util.Locale;

public class ReviewItemRenderer extends Renderer<ReviewItem> {

    @Override
    public View render(@NonNull final ReviewItem component, @NonNull final Context context,
        @Nullable final ViewGroup parent) {

        // Cannot use raw inflate because it replaces the old item by the new one, instead of attaching both to the parent
        final View itemView =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.px_review_item, parent, false);

        final ImageView itemImage = itemView.findViewById(R.id.item_image);
        final MPTextView itemTitle = itemView.findViewById(R.id.item_title);
        final MPTextView itemSubtitle = itemView.findViewById(R.id.item_subtitle);
        final MPTextView itemQuantity = itemView.findViewById(R.id.item_quantity);
        final MPTextView itemPrice = itemView.findViewById(R.id.item_price);

        if (component.hasItemImage()) {
            drawItemFromUrl(itemImage, component, context);
        } else if (component.hasIcon()) {
            drawItemFromResource(itemImage, component.props.icon);
        }

        setText(itemTitle, component.props.itemModel.title);
        setText(itemSubtitle, component.props.itemModel.subtitle);
        drawProductQuantity(itemQuantity, component.props, context);
        drawProductPrice(itemPrice, component.props, context);

        parent.addView(itemView);

        return itemView;
    }

    private void drawItemFromUrl(final ImageView itemImage, final ReviewItem component, final Context context) {
        final int dimen =
            ScaleUtil.getPxFromDp((int) context.getResources().getDimension(R.dimen.px_m_height), context);
        PicassoDiskLoader.get(context)
            .load(component.props.itemModel.imageUrl)
            .transform(new CircleTransform())
            .resize(dimen, dimen)
            .centerInside()
            .placeholder(component.props.icon)
            .error(component.props.icon)
            .into(itemImage);
    }

    private void drawItemFromResource(final ImageView itemImage,
        @DrawableRes final int resource) {
        itemImage.setImageResource(resource);
    }

    private void drawProductQuantity(final MPTextView itemQuantity, final ReviewItem.Props props,
        final Context context) {

        if (props.itemModel.hasToShowQuantity()) {
            //Show quantity
            String productQuantityText;
            if (TextUtil.isNotEmpty(props.quantityLabel)) {
                productQuantityText = String.format(Locale.getDefault(),
                    "%s %s",
                    props.quantityLabel,
                    props.itemModel.quantity);
            } else {
                productQuantityText = String.format(Locale.getDefault(),
                    "%s %d",
                    context.getResources().getString(R.string.px_review_item_quantity),
                    props.itemModel.quantity);
            }
            itemQuantity.setText(productQuantityText);
        } else {
            //Hide quantity
            itemQuantity.setVisibility(View.GONE);
        }
    }

    private void drawProductPrice(final MPTextView itemPrice, final ReviewItem.Props props, final Context context) {
        if (props.itemModel.hasToShowPrice()) {
            //Show price
            String priceText;
            if (TextUtil.isNotEmpty(props.unitPriceLabel)) {
                priceText = String.format(Locale.getDefault(),
                    "%s %s",
                    props.unitPriceLabel,
                    props.itemModel.getPrice());
            } else {
                priceText = TextUtil.format(context, R.string.px_review_product_price, props.itemModel.getPrice());
            }
            itemPrice.setText(priceText);

            //Add margin top if quantity is gone
            if (!props.itemModel.hasToShowQuantity()) {
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, (int) context.getResources().getDimension(R.dimen.px_s_margin), 0, 0);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                itemPrice.setLayoutParams(params);
            }
        } else {
            //Hide price
            itemPrice.setVisibility(View.GONE);
        }
    }
}
