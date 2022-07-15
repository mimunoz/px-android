package com.mercadopago.android.px.internal.features.payer_information;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.model.IdentificationType;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.List;

public interface PayerInformation {

    interface View extends MvpView {

        void cancel();

        void setIdentificationNumberRestrictions(String type);

        void clearErrorIdentificationNumber();

        void clearErrorName();

        void clearErrorLastName();

        void clearErrorBusinessName();

        void showErrorIdentificationNumber();

        void showErrorName();

        void showErrorLastName();

        void setErrorView(String message);

        void clearErrorView();

        void showCardFlowEnd(@PayerInformationFocus String currentFocus);

        void showIdentificationNumberFocus();

        void showIdentificationLastNameFocus();

        void showIdentificationNameFocus();

        void showIdentificationBusinessNameFocus();

        void showError(MercadoPagoError error, String requestOrigin);

        void initializeIdentificationTypes(List<IdentificationType> identificationTypes,
            IdentificationType current);

        void showProgressBar();

        void hideProgressBar();

        void showMissingIdentificationTypesError();

        void showInvalidLengthIdentificationNumberErrorView();

        void showInvalidIdentificationNameErrorView();

        void showInvalidIdentificationLastNameErrorView();

        void setName(final String identificationName);

        void setLastName(final String identificationLastName);

        void setNumber(final String identificationNumber);

        void identificationDraw();

        void showInvalidIdentificationBusinessNameErrorView();

        void showErrorBusinessName();

        void configureCnpjFlow();

        void configureCpfFlow();

        void showInvalidIdentificationNumberErrorView();
    }

    interface Actions {

        void createPayer();

        void validateName();

        void validateBusinessName();

        void validateLastName();

        void validateIdentification();

        void trackAbort();

        void setCurrentFocus(@PayerInformationFocus String currentFocus);

        void configureIdentificationTypeFlow(@NonNull IdentificationType identificationType);

        void validateCurrentEditText();

        void onSaveInstance(@NonNull Bundle bundle);

        void onBackPressed();
    }
}