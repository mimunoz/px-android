package com.mercadopago.android.px.internal.features.payer_information;

import androidx.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({
    PayerInformationFocus.NUMBER_INPUT,
    PayerInformationFocus.NAME_INPUT,
    PayerInformationFocus.LAST_NAME_INPUT,
    PayerInformationFocus.BUSINESS_NAME_INPUT
})
public @interface PayerInformationFocus {
    String NUMBER_INPUT = "number";
    String NAME_INPUT = "name";
    String LAST_NAME_INPUT = "last_name";
    String BUSINESS_NAME_INPUT = "business_name";
}