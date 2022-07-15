package com.mercadopago.android.px.internal.features.review_and_confirm;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ItemsModel;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.LineSeparatorType;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ReviewAndConfirmViewModel;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.SummaryModel;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.TermsAndConditionsModel;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Currency;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.display_info.LinkableText;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.util.List;

public class ReviewAndConfirmBuilder {

    private Boolean hasExtraPaymentMethods;

    public ReviewAndConfirmBuilder setHasExtraPaymentMethods(final boolean hasExtraPaymentMethods) {
        this.hasExtraPaymentMethods = hasExtraPaymentMethods;
        return this;
    }

    public Intent getIntent(@NonNull final Context context) {
        final Resources resources = context.getResources();
        final Session session = Session.getInstance();
        final ConfigurationModule configurationModule = session.getConfigurationModule();
        final UserSelectionRepository userSelectionRepository = configurationModule.getUserSelectionRepository();
        final Issuer issuer = userSelectionRepository.getIssuer();
        final PaymentSettingRepository paymentSettings = configurationModule.getPaymentSettings();
        final Site site = paymentSettings.getSite();
        final Currency currency = paymentSettings.getCurrency();
        final Token token = paymentSettings.getToken();
        final Card card =  userSelectionRepository.getCard();
        final AmountRepository amountRepository = session.getAmountRepository();
        final PaymentMethod paymentMethod = userSelectionRepository.getPaymentMethod();
        final CheckoutPreference checkoutPreference = paymentSettings.getCheckoutPreference();
        final DiscountConfigurationModel discountModel = session.getDiscountRepository().getCurrentConfiguration();
        final List<Item> items = checkoutPreference.getItems();

        final String title = SummaryModel.resolveTitle(items, resources);

        final boolean termsAndConditionsEnabled = TextUtil.isEmpty(paymentSettings.getPrivateKey());

        final TermsAndConditionsModel mercadoPagoTermsAndConditions =
            termsAndConditionsEnabled ? new TermsAndConditionsModel(site.getTermsAndConditionsUrl(),
                resources.getString(R.string.px_terms_and_conditions_message),
                resources.getString(R.string.px_terms_and_conditions) + TextUtil.DOT,
                LineSeparatorType.TOP_LINE_SEPARATOR) : null;

        final TermsAndConditionsModel discountTermsAndConditions =
            discountModel.getCampaign() != null
                ? new TermsAndConditionsModel(discountModel.getCampaign().getLegalTermsUrl(),
                resources.getString(R.string.px_discount_terms_and_conditions_message),
                resources.getString(R.string.px_discount_terms_and_conditions_linked_message),
                LineSeparatorType.BOTTOM_LINE_SEPARATOR) : null;

        final String lastFourDigits = card != null ? card.getLastFourDigits() :
            token != null ? token.getLastFourDigits() : TextUtil.EMPTY;

        final ReviewAndConfirmViewModel reviewAndConfirmViewModel =
            new ReviewAndConfirmViewModel(paymentMethod, lastFourDigits, issuer, hasExtraPaymentMethods);

        final PayerCost payerCost = userSelectionRepository.getPayerCost();
        final SummaryModel summaryModel =
            new SummaryModel(amountRepository.getAmountToPay(paymentMethod.getPaymentTypeId(), payerCost),
                paymentMethod, site, currency, payerCost, discountModel.getDiscount(), title,
                checkoutPreference.getTotalAmount(),
                amountRepository.getAppliedCharges(paymentMethod.getPaymentTypeId(), payerCost));

        final ItemsModel itemsModel = new ItemsModel(currency, items);

        LinkableText linkableText = null;
        if (paymentMethod.getDisplayInfo() != null) {
            linkableText = paymentMethod.getDisplayInfo().getTermsAndConditions();
        }

        return ReviewAndConfirmActivity.getIntentForAction(context,
            mercadoPagoTermsAndConditions,
            linkableText,
            reviewAndConfirmViewModel,
            summaryModel,
            itemsModel,
            discountTermsAndConditions);
    }
}