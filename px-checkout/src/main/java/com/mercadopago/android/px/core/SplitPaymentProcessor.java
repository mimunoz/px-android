package com.mercadopago.android.px.core;

import android.content.Context;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.fragment.app.Fragment;
import com.mercadopago.android.px.internal.model.SecurityType;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.util.List;

@SuppressWarnings("unused")
public interface SplitPaymentProcessor extends Parcelable {

    /* default */ final class CheckoutData {

        @NonNull public final CheckoutPreference checkoutPreference;
        @NonNull public final List<PaymentData> paymentDataList;
        @NonNull public final String securityType;

        /**
         * @deprecated use the one with security type
         */
        @Deprecated
        public CheckoutData(@NonNull @Size(min = 1) final List<PaymentData> paymentData,
            @NonNull final CheckoutPreference checkoutPreference) {
            this(paymentData, checkoutPreference, SecurityType.NONE.getValue());
        }

        public CheckoutData(@NonNull @Size(min = 1) final List<PaymentData> paymentData,
            @NonNull final CheckoutPreference checkoutPreference, @NonNull final String securityType) {
            paymentDataList = paymentData;
            this.checkoutPreference = checkoutPreference;
            this.securityType = securityType;
        }
    }

    /**
     * Method that we will call if {@link #shouldShowFragmentOnPayment(CheckoutPreference)} is false. we will place a
     * loading for you meanwhile we call this method.
     *
     * @param data            checkout data to the moment it's called.
     * @param context         that you may need to fill information.
     * @param paymentListener when you have processed your payment you should call {@link OnPaymentListener}
     */
    void startPayment(@NonNull final Context context, @NonNull final CheckoutData data,
        @NonNull final OnPaymentListener paymentListener);

    /**
     * If you how much time will take the request timeout you can tell us to optimize the loading UI. will only works if
     * {@link #shouldShowFragmentOnPayment(CheckoutPreference)} is false.
     *
     * @return time in milliseconds
     */
    int getPaymentTimeout(@NonNull final CheckoutPreference checkoutPreference);

    /**
     * method to know if the payment processor should pay through a fragment or do it through background execution. will
     * be called on runtime.
     *
     * @return if it should show view
     */
    boolean shouldShowFragmentOnPayment(@NonNull final CheckoutPreference checkoutPreference);

    /**
     * Method used as a flag to know if we should offer split payment or not to the user.
     *
     * @return if it should show view
     */
    boolean supportsSplitPayment(@Nullable final CheckoutPreference checkoutPreference);

    /**
     * Fragment that will appear if {@link #shouldShowFragmentOnPayment(CheckoutPreference)} is true when user clicks
     * this payment method.
     * <p>
     * inside {@link android.support.v4.app.Fragment#onAttach(Context)} context will be an instance of {@link
     * OnPaymentListener}
     *
     * @param data    checkout data to the moment it's called.
     * @param context that you may need to fill information.
     * @return fragment
     */
    @Nullable
    Fragment getFragment(@NonNull final CheckoutData data, @NonNull final Context context);

    interface OnPaymentListener {

        void onPaymentFinished(@NonNull final IPaymentDescriptor payment);

        void onPaymentError(@NonNull final MercadoPagoError error);
    }

    interface BackHandler {

        /**
         * The implementation of this method can tell you if the back action is enabled or disabled. In certain cases
         * you want to block UI, disabling the back button. This occurs for toolbar back button, display screen back
         * button and swipe back gestures.
         */
        boolean isBackEnabled();
    }

    /**
     * If the boolean is true payment processor's fragment will be showed instead review and confirm screen
     *
     * @return if fragment should be showed
     */
    default boolean shouldSkipUserConfirmation() {
        return false;
    }
}
