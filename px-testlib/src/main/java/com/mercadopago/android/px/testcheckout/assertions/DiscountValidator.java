package com.mercadopago.android.px.testcheckout.assertions;

import android.view.View;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.testcheckout.R;
import com.mercadopago.android.px.testcheckout.pages.InstallmentsPage;
import com.mercadopago.android.px.testcheckout.pages.OneTapPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentMethodPage;
import com.mercadopago.android.px.testcheckout.pages.ReviewAndConfirmPage;
import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasTextColor;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public abstract class DiscountValidator extends DefaultValidator {
    @NonNull protected final Campaign campaign;
    @NonNull protected final Discount discount;

    public DiscountValidator(@NonNull final Campaign campaign, @NonNull final Discount discount) {
        this.campaign = campaign;
        this.discount = discount;
    }

    @Override
    public void validate(@NonNull final PaymentMethodPage paymentMethodPage) {
    }

    @Override
    public void validate(@NonNull final InstallmentsPage installmentsPage) {
    }

    @Override
    public void validate(@NonNull final ReviewAndConfirmPage reviewAndConfirmPage) {
        final Matcher<View> discountDescription = withText(R.string.px_review_summary_discount);
        onView(discountDescription).check(matches(hasTextColor(R.color.px_discount_description)));
    }

    @Override
    public void validate(@NonNull final OneTapPage oneTapPage) {
        // TODO implement
    }
}
