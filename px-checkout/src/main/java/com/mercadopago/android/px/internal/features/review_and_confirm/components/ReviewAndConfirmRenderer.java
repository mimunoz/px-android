package com.mercadopago.android.px.internal.features.review_and_confirm.components;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.configuration.DynamicFragmentConfiguration;
import com.mercadopago.android.px.core.DynamicFragmentCreator;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.review_and_confirm.components.actions.ChangePaymentMethodAction;
import com.mercadopago.android.px.internal.features.review_and_confirm.components.items.ReviewItems;
import com.mercadopago.android.px.internal.features.review_and_confirm.components.payer_information.PayerInformationComponent;
import com.mercadopago.android.px.internal.features.review_and_confirm.components.payment_method.PaymentMethodComponent;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ReviewAndConfirmViewModel;
import com.mercadopago.android.px.internal.navigation.DefaultPayerInformationDriver;
import com.mercadopago.android.px.internal.util.FragmentUtil;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Renderer;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.internal.view.TermsAndConditionsComponent;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.preferences.CheckoutPreference;

public class ReviewAndConfirmRenderer extends Renderer<ReviewAndConfirmContainer> {

    @Override
    protected View render(@NonNull final ReviewAndConfirmContainer component,
        @NonNull final Context context,
        @Nullable final ViewGroup parent) {

        final LinearLayout linearLayout = createMainLayout(context);

        addSummary(component, linearLayout);

        if (component.hasDiscountTermsAndConditions()) {
            addDiscountTermsAndConditions(component, linearLayout);
        }

        if (component.hasItemsEnabled()) {
            addReviewItems(component, linearLayout);
        }

        if (component.props.preferences.hasCustomTopView()) {
            FragmentUtil.addFragmentInside(linearLayout,
                R.id.px_fragment_container_top,
                component.props.preferences.getTopFragment());
        }

        final Session session = Session.getInstance();
        final ConfigurationModule configurationModule = session.getConfigurationModule();
        final PaymentMethod paymentMethod = configurationModule.getUserSelectionRepository().getPaymentMethod();

        final DefaultPayerInformationDriver defaultPayerInformationDriver =
            new DefaultPayerInformationDriver(component.props.payer, paymentMethod);
        if (defaultPayerInformationDriver.hasToShowPayer()) {
            addPayerInformation(component.props.payer, linearLayout);
        }

        final CheckoutPreference checkoutPreference =
            configurationModule.getPaymentSettings().getCheckoutPreference();

        final DynamicFragmentCreator.CheckoutData data =
            new DynamicFragmentCreator.CheckoutData(checkoutPreference,
                session.getPaymentRepository().getPaymentDataList());

        if (component.props.dynamicFragments
            .hasCreatorFor(DynamicFragmentConfiguration.FragmentLocation.TOP_PAYMENT_METHOD_REVIEW_AND_CONFIRM)) {

            final DynamicFragmentCreator fragmentCreator = component.props.dynamicFragments.getCreatorFor(
                DynamicFragmentConfiguration.FragmentLocation.TOP_PAYMENT_METHOD_REVIEW_AND_CONFIRM);

            if (fragmentCreator.shouldShowFragment(context, data)) {
                FragmentUtil.addFragmentInside(linearLayout,
                    R.id.px_fragment_container_dynamic_top,
                    fragmentCreator.create(context, data));
            }
        }

        addPaymentMethod(component.props.reviewAndConfirmViewModel, component.getDispatcher(), linearLayout);

        if (component.props.preferences.hasCustomBottomView()) {
            FragmentUtil.addFragmentInside(linearLayout,
                R.id.px_fragment_container_bottom,
                component.props.preferences.getBottomFragment());
        }

        if (component.props.dynamicFragments
            .hasCreatorFor(DynamicFragmentConfiguration.FragmentLocation.BOTTOM_PAYMENT_METHOD_REVIEW_AND_CONFIRM)) {

            final DynamicFragmentCreator fragmentCreator = component.props.dynamicFragments.getCreatorFor(
                DynamicFragmentConfiguration.FragmentLocation.BOTTOM_PAYMENT_METHOD_REVIEW_AND_CONFIRM);

            if (fragmentCreator.shouldShowFragment(context, data)) {
                FragmentUtil.addFragmentInside(linearLayout,
                    R.id.px_fragment_container_dynamic_bottom,
                    fragmentCreator.create(context, data));
            }
        }

        if (component.hasMercadoPagoTermsAndConditions()) {
            addTermsAndConditions(component, linearLayout);
        }

        parent.addView(linearLayout);

        return parent;
    }

    private void addPayerInformation(final Payer payer, final LinearLayout linearLayout) {
        final PayerInformationComponent payerInformationComponent = new PayerInformationComponent(payer);
        final View payerView = payerInformationComponent.render(linearLayout);
        linearLayout.addView(payerView);
    }

    private void addSummary(@NonNull final ReviewAndConfirmContainer component, final LinearLayout linearLayout) {
        final Renderer summary = RendererFactory.create(linearLayout.getContext(),
            new SummaryComponent(SummaryComponent.SummaryProps.createFrom(component.props.summaryModel,
                component.props.preferences)));
        summary.render(linearLayout);
    }

    private void addDiscountTermsAndConditions(@NonNull final ReviewAndConfirmContainer component,
        final ViewGroup parent) {

        final TermsAndConditionsComponent discountTermsAndConditionsComponent =
            new TermsAndConditionsComponent(component.props.discountTermsAndConditionsModel);

        final View discountTermsAndConditionsView = discountTermsAndConditionsComponent.render(parent);
        parent.addView(discountTermsAndConditionsView);
    }

    @NonNull
    private LinearLayout createMainLayout(@NonNull final Context context) {
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    private void addPaymentMethod(final ReviewAndConfirmViewModel reviewAndConfirmViewModel,
        final ActionDispatcher dispatcher,
        final ViewGroup parent) {
        final PaymentMethodComponent paymentMethodComponent =
            new PaymentMethodComponent(reviewAndConfirmViewModel, () -> dispatcher.dispatch(new ChangePaymentMethodAction()));

        final View paymentView = paymentMethodComponent.render(parent);
        parent.addView(paymentView);
    }

    private void addReviewItems(@NonNull final ReviewAndConfirmContainer component, final ViewGroup parent) {
        final ReviewItems reviewItems = new ReviewItems(
            new ReviewItems.Props(
                component.props.itemsModel,
                component.props.preferences.getCollectorIcon(),
                component.props.preferences.getQuantityLabel(),
                component.props.preferences.getUnitPriceLabel()));
        final View view = reviewItems.render(parent);
        parent.addView(view);
    }

    private void addTermsAndConditions(@NonNull final ReviewAndConfirmContainer component,
        final ViewGroup parent) {

        final TermsAndConditionsComponent termsAndConditionsComponent =
            new TermsAndConditionsComponent(component.props.mercadoPagoTermsAndConditionsModel);

        final View termsAndConditionsView = termsAndConditionsComponent.render(parent);
        parent.addView(termsAndConditionsView);
    }
}