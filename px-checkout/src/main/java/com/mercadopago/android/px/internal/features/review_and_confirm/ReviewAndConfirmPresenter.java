package com.mercadopago.android.px.internal.features.review_and_confirm;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.configuration.DynamicDialogConfiguration;
import com.mercadopago.android.px.core.DynamicDialogCreator;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.features.pay_button.PayButton;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.internal.PaymentConfiguration;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.internal.events.ChangePaymentMethodEvent;
import com.mercadopago.android.px.tracking.internal.model.ConfirmData;
import com.mercadopago.android.px.tracking.internal.views.ReviewAndConfirmViewTracker;
import java.util.Set;

/* default */ final class ReviewAndConfirmPresenter extends BasePresenter<ReviewAndConfirm.View>
    implements ReviewAndConfirm.Presenter {

    @NonNull private final PaymentSettingRepository paymentSettings;
    @NonNull private final UserSelectionRepository userSelectionRepository;
    @NonNull /* default */ final PaymentRepository paymentRepository;
    private final ReviewAndConfirmViewTracker reviewAndConfirmViewTracker;
    private final ESCManagerBehaviour escManagerBehaviour;

    /* default */ ReviewAndConfirmPresenter(@NonNull final PaymentRepository paymentRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final PaymentSettingRepository paymentSettings,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final ESCManagerBehaviour escManagerBehaviour) {
        this.paymentRepository = paymentRepository;
        this.paymentSettings = paymentSettings;
        this.userSelectionRepository = userSelectionRepository;
        this.escManagerBehaviour = escManagerBehaviour;
        final Set<String> escCardIds = escManagerBehaviour.getESCCardIds();
        reviewAndConfirmViewTracker =
            new ReviewAndConfirmViewTracker(escCardIds, userSelectionRepository, paymentSettings,
                discountRepository.getCurrentConfiguration());

        setCurrentViewTracker(reviewAndConfirmViewTracker);
    }

    @Override
    public void attachView(final ReviewAndConfirm.View view) {
        super.attachView(view);
        resolveDynamicDialog(DynamicDialogConfiguration.DialogLocation.ENTER_REVIEW_AND_CONFIRM);
    }

    @Override
    public void onChangePaymentMethod() {
        new ChangePaymentMethodEvent().track();
        doChangePaymentMethod();
    }

    private void resolveDynamicDialog(@NonNull final DynamicDialogConfiguration.DialogLocation location) {
        final CheckoutPreference checkoutPreference = paymentSettings.getCheckoutPreference();
        final DynamicDialogConfiguration dynamicDialogConfiguration =
            paymentSettings.getAdvancedConfiguration().getDynamicDialogConfiguration();
        final DynamicDialogCreator.CheckoutData checkoutData =
            new DynamicDialogCreator.CheckoutData(checkoutPreference, paymentRepository.getPaymentDataList());
        if (dynamicDialogConfiguration.hasCreatorFor(location)) {
            getView().showDynamicDialog(dynamicDialogConfiguration.getCreatorFor(location), checkoutData);
        }
    }

    @Override
    public void onPrePayment(@NonNull final PayButton.OnReadyForPaymentCallback callback) {
        final PaymentMethod paymentMethod = userSelectionRepository.getPaymentMethod();
        final String paymentTypeId = paymentMethod.getPaymentTypeId();
        final String paymentMethodId = paymentMethod.getId();
        final Card card = userSelectionRepository.getCard();
        final PayerCost payerCost = userSelectionRepository.getPayerCost();
        final boolean isCard = PaymentTypes.isCardPaymentType(paymentTypeId);
        //Card is null on guessing card flow
        final String customOptionId = isCard ? (card != null ? card.getId() : "") : paymentMethodId;
        callback.call(
            new PaymentConfiguration(
                paymentMethodId,
                paymentTypeId,
                customOptionId,
                isCard,
                false,
                payerCost
                ),
            ConfirmData.from(
                escManagerBehaviour.getESCCardIds(),
                userSelectionRepository
            )
        );
    }

    @Override
    public void onBackPressed() {
        userSelectionRepository.reset();
    }

    @Override
    public void onPostPaymentAction(@NonNull final PostPaymentAction postPaymentAction) {
        postPaymentAction.execute(new PostPaymentAction.ActionController() {
            @Override
            public void recoverPayment(@NonNull final PostPaymentAction postPaymentAction) {
                //Handled on pay button
            }

            @Override
            public void onChangePaymentMethod() {
                doChangePaymentMethod();
            }
        });
    }

    /* default */ void doChangePaymentMethod() {
        userSelectionRepository.reset();
        getView().finishAndChangePaymentMethod();
    }
}