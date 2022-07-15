package com.mercadopago.android.px.internal.features.review_and_confirm.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.Item;
import java.util.ArrayList;
import java.util.List;

public class ItemsModel implements Parcelable {

    public final List<ItemModel> itemsModelList;

    public ItemsModel(@NonNull final Currency currency, @NonNull final List<Item> itemList) {
        itemsModelList = parseItems(itemList, currency);
    }

    /* default */ ItemsModel(final Parcel in) {
        itemsModelList = in.createTypedArrayList(ItemModel.CREATOR);
    }

    public static final Creator<ItemsModel> CREATOR = new Creator<ItemsModel>() {
        @Override
        public ItemsModel createFromParcel(final Parcel in) {
            return new ItemsModel(in);
        }

        @Override
        public ItemsModel[] newArray(final int size) {
            return new ItemsModel[size];
        }
    };

    @NonNull
    private List<ItemModel> parseItems(final List<Item> itemList, final Currency currency) {
        final List<ItemModel> toReturn = new ArrayList<>();

        for (final Item item : itemList) {
            addItemToList(toReturn, item, itemList.size() > 1, currency);
        }

        return toReturn;
    }

    private void addItemToList(final List<ItemModel> toReturn,
        final Item item,
        final boolean hasMultipleItems,
        final Currency currency) {
        if (hasMultipleItems || TextUtil.isNotEmpty(item.getDescription()) || item.getQuantity() > 1) {
            toReturn.add(createItemModel(item, hasMultipleItems, currency));
        }
    }

    private ItemModel createItemModel(final Item item,
        final boolean hasMultipleItems,
        final Currency currency) {
        return new ItemModel(item.getPictureUrl(),
            hasMultipleItems ? item.getTitle() : item.getDescription(),
            hasMultipleItems ? item.getDescription() : null,
            item.getQuantity(),
            currency,
            hasMultipleItems || item.hasCardinality() ? item.getUnitPrice() : null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeTypedList(itemsModelList);
    }

    public boolean hasUniqueItem() {
        return itemsModelList != null && itemsModelList.size() == 1;
    }
}