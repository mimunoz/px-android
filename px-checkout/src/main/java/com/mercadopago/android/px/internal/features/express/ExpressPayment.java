package com.mercadopago.android.px.internal.features.express;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.core.DynamicDialogCreator;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.experiments.Variant;
import com.mercadopago.android.px.internal.features.express.installments.InstallmentRowHolder;
import com.mercadopago.android.px.internal.features.express.slider.HubAdapter;
import com.mercadopago.android.px.internal.features.generic_modal.ActionType;
import com.mercadopago.android.px.internal.features.generic_modal.GenericDialogItem;
import com.mercadopago.android.px.internal.features.pay_button.PayButton;
import com.mercadopago.android.px.internal.util.CardFormWithFragmentWrapper;
import com.mercadopago.android.px.internal.view.ElementDescriptorView;
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction;
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState;
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.OfflinePaymentTypesMetadata;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.StatusMetadata;
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod;
import java.util.List;

public interface ExpressPayment {

    interface View extends MvpView {

        void clearAdapters();

        void configureAdapters(@NonNull final Site site, @NonNull final Currency currency);

        void updateAdapters(@NonNull HubAdapter.Model model);

        void updatePaymentMethods(@NonNull List<DrawableFragmentItem> items);

        void cancel();

        void updateViewForPosition(final int paymentMethodIndex,
            final int payerCostSelected,
            @NonNull final SplitSelectionState splitSelectionState);

        void showInstallmentsList(final int selectedIndex, @NonNull List<InstallmentRowHolder.Model> models);

        void showToolbarElementDescriptor(@NonNull final ElementDescriptorView.Model elementDescriptorModel);

        void collapseInstallmentsSelection();

        void showDiscountDetailDialog(@NonNull final Currency currency,
            @NonNull final DiscountConfigurationModel discountModel);

        void showDisabledPaymentMethodDetailDialog(@NonNull final DisabledPaymentMethod disabledPaymentMethod,
            @NonNull final StatusMetadata currentStatus);

        void setPagerIndex(final int index);

        void showDynamicDialog(@NonNull final DynamicDialogCreator creatorFor,
            @NonNull final DynamicDialogCreator.CheckoutData checkoutData);

        void showOfflineMethods(@NonNull final OfflinePaymentTypesMetadata offlineMethods);

        void updateBottomSheetStatus(final boolean hasToExpand);

        void showGenericDialog(@NonNull GenericDialogItem item);

        void startAddNewCardFlow(
            final CardFormWithFragmentWrapper cardFormWithFragmentWrapper);

        void startDeepLink(@NonNull String deepLink);

        void onDeepLinkReceived();

        void showLoading();

        void hideLoading();

        void configurePaymentMethodHeader(@NonNull final List<Variant> variant);
    }

    interface Actions {

        void onFreshStart();

        void cancel();

        void onBack();

        void loadViewModel();

        void onInstallmentsRowPressed();

        void onInstallmentSelectionCanceled();

        void onSliderOptionSelected(final int paymentMethodIndex);

        void onPayerCostSelected(final PayerCost payerCostSelected);

        void onSplitChanged(boolean isChecked);

        void onHeaderClicked();

        void onOtherPaymentMethodClicked(@NonNull final OfflinePaymentTypesMetadata offlineMethods);

        void onOtherPaymentMethodClickableStateChanged(boolean state);

        void handlePrePaymentAction(@NonNull final PayButton.OnReadyForPaymentCallback callback);

        void handleGenericDialogAction(@NonNull @ActionType final String type);

        void handleDeepLink();

        void onPostPaymentAction(@NonNull final PostPaymentAction postPaymentAction);

        void onCardFormResult();
    }
}