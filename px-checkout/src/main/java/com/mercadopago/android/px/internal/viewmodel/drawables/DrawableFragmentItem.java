package com.mercadopago.android.px.internal.viewmodel.drawables;

import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.meli.android.carddrawer.model.SwitchModel;
import com.mercadopago.android.px.internal.features.generic_modal.GenericDialogItem;
import com.mercadopago.android.px.internal.util.KParcelable;
import com.mercadopago.android.px.model.Reimbursement;
import com.mercadopago.android.px.model.internal.Text;
import java.io.Serializable;

public abstract class DrawableFragmentItem implements KParcelable, Serializable {

    private final String id;
    private final DrawableFragmentCommons.ByApplication commonsByApplication;
    private final Text bottomDescription;
    private final Reimbursement reimbursement;
    private final String description;
    private final String issuerName;
    private final GenericDialogItem genericDialogItem;
    private final SwitchModel switchModel;

    protected DrawableFragmentItem(@NonNull final Parameters parameters) {
        id = parameters.id;
        commonsByApplication = parameters.commonsByApplication;
        bottomDescription = parameters.bottomDescription;
        reimbursement = parameters.reimbursement;
        description = parameters.description;
        issuerName = parameters.issuerName;
        genericDialogItem = parameters.genericDialogItem;
        switchModel = parameters.switchModel;
    }

    protected DrawableFragmentItem(final Parcel in) {
        id = in.readString();
        commonsByApplication = in.readParcelable(DrawableFragmentCommons.ByApplication.class.getClassLoader());
        bottomDescription = in.readParcelable(Text.class.getClassLoader());
        reimbursement = in.readParcelable(Reimbursement.class.getClassLoader());
        description = in.readString();
        issuerName = in.readString();
        genericDialogItem = in.readParcelable(GenericDialogItem.class.getClassLoader());
        switchModel = in.readParcelable(SwitchModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(id);
        dest.writeParcelable(commonsByApplication, flags);
        dest.writeParcelable(bottomDescription, flags);
        dest.writeParcelable(reimbursement, flags);
        dest.writeString(description);
        dest.writeString(issuerName);
        dest.writeParcelable(genericDialogItem, flags);
        dest.writeParcelable(switchModel, flags);
    }

    public abstract Fragment draw(@NonNull final PaymentMethodFragmentDrawer drawer);

    public String getId() {
        return id;
    }

    public DrawableFragmentCommons.ByApplication getCommonsByApplication() {
        return commonsByApplication;
    }

    @Nullable
    public Text getBottomDescription() {
        return bottomDescription;
    }

    @Nullable
    public Reimbursement getReimbursement() {
        return reimbursement;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getIssuerName() {
        return issuerName;
    }

    public boolean shouldHighlightBottomDescription() {
        return bottomDescription == null;
    }

    @Nullable
    public GenericDialogItem getGenericDialogItem() {
        return genericDialogItem;
    }

    @Nullable
    public SwitchModel getSwitchModel() {
        return switchModel;
    }

    public static final class Parameters {
        /* default */ @NonNull final String id;
        /* default */ @NonNull final DrawableFragmentCommons.ByApplication commonsByApplication;
        /* default */ @Nullable final Text bottomDescription;
        /* default */ @Nullable final Reimbursement reimbursement;
        /* default */ @NonNull final String description;
        /* default */ @NonNull final String issuerName;
        /* default */ @NonNull final GenericDialogItem genericDialogItem;
        /* default */ @Nullable final SwitchModel switchModel;

        /* default */ Parameters(@NonNull final String id,
            @NonNull final DrawableFragmentCommons.ByApplication commonsByApplication,
            @Nullable final Text bottomDescription, @Nullable final Reimbursement reimbursement,
            @NonNull final String description, @NonNull final String issuerName,
            @Nullable final GenericDialogItem genericDialogItem, @Nullable final SwitchModel switchModel) {
            this.id = id;
            this.commonsByApplication = commonsByApplication;
            this.bottomDescription = bottomDescription;
            this.reimbursement = reimbursement;
            this.description = description;
            this.issuerName = issuerName;
            this.genericDialogItem = genericDialogItem;
            this.switchModel = switchModel;
        }
    }
}
