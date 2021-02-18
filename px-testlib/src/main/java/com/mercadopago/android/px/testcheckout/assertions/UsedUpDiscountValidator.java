package com.mercadopago.android.px.testcheckout.assertions;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.test.espresso.matcher.ViewMatchers;
import com.mercadopago.android.px.testcheckout.pages.DiscountDetailPage;
import com.mercadopago.android.px.testcheckout.pages.InstallmentsPage;
import com.mercadopago.android.px.testcheckout.pages.OneTapPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentMethodPage;
import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class UsedUpDiscountValidator extends DefaultValidator {
    @Override
    public void validate(@NonNull final DiscountDetailPage discountDetailPage) {
        final Matcher<View> summary = withId(com.mercadopago.android.px.R.id.summary);
        final Matcher<View> description = withId(com.mercadopago.android.px.R.id.description);
        final Matcher<View> detail = withId(com.mercadopago.android.px.R.id.detail);
        onView(summary).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(description).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(detail).check(matches(withText(com.mercadopago.android.px.R.string.px_used_up_discount_detail)));
    }

    @Override
    public void validate(@NonNull final PaymentMethodPage paymentMethodPage) {
    }

    @Override
    public void validate(@NonNull final InstallmentsPage installmentsPage) {
    }

    @Override
    public void validate(@NonNull final OneTapPage oneTapPage) {
        // TODO implement.
    }
}
