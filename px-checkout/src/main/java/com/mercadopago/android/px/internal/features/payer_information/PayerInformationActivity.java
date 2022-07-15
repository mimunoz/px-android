package com.mercadopago.android.px.internal.features.payer_information;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MotionEventCompat;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.adapters.IdentificationTypesAdapter;
import com.mercadopago.android.px.internal.base.PXActivity;
import com.mercadopago.android.px.internal.callbacks.card.TicketIdentificationNameEditTextCallback;
import com.mercadopago.android.px.internal.callbacks.card.TicketIdentificationNumberEditTextCallback;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.card.TicketIdentificationNameTextWatcher;
import com.mercadopago.android.px.internal.features.card.TicketIdentificationNumberTextWatcher;
import com.mercadopago.android.px.internal.features.uicontrollers.identification.IdentificationTicketView;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.MPEditText;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.internal.viewmodel.PayerInformationStateModel;
import com.mercadopago.android.px.model.IdentificationType;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.List;

public class PayerInformationActivity extends PXActivity<PayerInformationPresenter> implements PayerInformation.View {

    private static final String ERROR_STATE = "textview_error";
    private static final String NORMAL_STATE = "textview_normal";

    // Local vars
    private boolean mActivityActive;

    //View controls
    /* default */ ScrollView mScrollView;
    private ViewGroup mProgressLayout;

    //Input controls
    private String mErrorState;
    private LinearLayout mInputContainer;
    private LinearLayout mIdentificationNumberInput;
    private LinearLayout mIdentificationNameInput;
    private LinearLayout mIdentificationLastNameInput;
    private LinearLayout mIdentificationBusinessNameInput;
    private LinearLayout mIdentificationTypeContainer;
    private LinearLayout mButtonContainer;
    private FrameLayout mNextButton;
    private FrameLayout mBackButton;
    private FrameLayout mErrorContainer;
    /* default */ Spinner mIdentificationTypeSpinner;
    /* default */ MPEditText mIdentificationNumberEditText;
    /* default */ MPEditText mIdentificationNameEditText;
    /* default */ MPEditText mIdentificationBusinessNameEditText;
    private MPEditText mIdentificationLastNameEditText;
    private MPTextView mErrorTextView;
    private Toolbar mLowResToolbar;
    private Toolbar mNormalToolbar;

    private boolean mLowResActive;

    /* default */ IdentificationTicketView mIdentificationTicketView;

    public static void start(final Activity activity, final int reqCode) {
        final Intent payerInformationIntent = new Intent(activity, PayerInformationActivity.class);
        activity.startActivityForResult(payerInformationIntent, reqCode);
    }

    @Override
    protected void onCreated(@Nullable final Bundle savedInstanceState) {
        mActivityActive = true;
        analyzeLowRes();
        setContentView();
        initializeControls();
        initializeToolbar();
        final Session session = Session.getInstance();
        presenter =
            new PayerInformationPresenter(PayerInformationStateModel.fromBundle(savedInstanceState),
                session.getConfigurationModule().getPaymentSettings(),
                session.getIdentificationRepository(),
                session.getConfigurationModule().getUserSelectionRepository().getPaymentMethod());
        presenter.attachView(this);
        mErrorState = NORMAL_STATE;
        setListeners();
    }

    @Override
    protected void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (presenter != null) {
            presenter.onSaveInstance(outState);
        }
    }

    private void initializeToolbar() {
        if (mLowResActive) {
            initializeToolbar(mLowResToolbar);
        } else {
            initializeToolbar(mNormalToolbar);
        }
    }

    private void initializeToolbar(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    private void analyzeLowRes() {
        mLowResActive = ScaleUtil.isLowRes(this);
    }

    private void setContentView() {
        if (mLowResActive) {
            setContentViewLowRes();
        } else {
            setContentViewNormal();
        }
    }

    private void setContentViewLowRes() {
        setContentView(R.layout.px_activity_payer_information_lowres);
    }

    private void setContentViewNormal() {
        setContentView(R.layout.px_activity_payer_information_normal);
    }

    private void initializeControls() {
        mInputContainer = findViewById(R.id.mpsdkInputContainer);
        mIdentificationNumberInput = findViewById(R.id.mpsdkCardIdentificationInput);
        mIdentificationNameInput = findViewById(R.id.mpsdkNameInput);
        mIdentificationLastNameInput = findViewById(R.id.mpsdkLastNameInput);
        mIdentificationBusinessNameInput = findViewById(R.id.mpsdkBusinessNameInput);

        mIdentificationNumberEditText = findViewById(R.id.mpsdkCardIdentificationNumber);
        mIdentificationNameEditText = findViewById(R.id.mpsdkName);
        mIdentificationLastNameEditText = findViewById(R.id.mpsdkLastName);
        mIdentificationBusinessNameEditText = findViewById(R.id.mpsdkBusinessName);

        mIdentificationTypeSpinner = findViewById(R.id.mpsdkCardIdentificationType);
        mIdentificationTypeContainer = findViewById(R.id.mpsdkCardIdentificationTypeContainer);

        mNextButton = findViewById(R.id.mpsdkNextButton);
        mBackButton = findViewById(R.id.mpsdkBackButton);

        mScrollView = findViewById(R.id.mpsdkScrollViewContainer);
        mProgressLayout = findViewById(R.id.mpsdkProgressLayout);

        final FrameLayout mIdentificationCardContainer = findViewById(R.id.mpsdkIdentificationCardContainer);

        mIdentificationTicketView = new IdentificationTicketView(this);
        mIdentificationTicketView.inflateInParent(mIdentificationCardContainer, true);
        mIdentificationTicketView.initializeControls();

        mButtonContainer = findViewById(R.id.mpsdkButtonContainer);
        mErrorContainer = findViewById(R.id.mpsdkErrorContainer);
        mErrorTextView = findViewById(R.id.mpsdkErrorTextView);

        if (mLowResActive) {
            mLowResToolbar = findViewById(R.id.mpsdkLowResToolbar);
            final MPTextView mLowResTitleToolbar = findViewById(R.id.mpsdkTitle);
            mLowResTitleToolbar.setText(getResources().getText(R.string.px_fill_your_data));
            mLowResToolbar.setVisibility(View.VISIBLE);
        } else {
            mNormalToolbar = findViewById(R.id.mpsdkTransparentToolbar);
        }

        showProgressBar();
        fullScrollDown();
    }

    @Override
    public void initializeIdentificationTypes(final List<IdentificationType> identificationTypes,
        final IdentificationType current) {
        mIdentificationTicketView.setIdentificationType(current);
        mIdentificationTicketView.drawIdentificationTypeName();
        presenter.configureIdentificationTypeFlow(current);
        mIdentificationTypeSpinner.setAdapter(new IdentificationTypesAdapter(identificationTypes));
        mIdentificationTypeContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgressBar() {
        mInputContainer.setVisibility(View.GONE);
        mButtonContainer.setVisibility(View.GONE);
        mProgressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mInputContainer.setVisibility(View.VISIBLE);
        mButtonContainer.setVisibility(View.VISIBLE);
        mProgressLayout.setVisibility(View.GONE);
    }

    @Override
    public void showMissingIdentificationTypesError() {
        showError(
            MercadoPagoError.createNotRecoverable(getString(R.string.px_error_message_missing_identification_types)),
            ApiUtil.RequestOrigin.GET_IDENTIFICATION_TYPES);
    }

    @Override
    public void showInvalidLengthIdentificationNumberErrorView() {
        setErrorView(getString(R.string.px_invalid_identification_number));
    }

    @Override
    public void showInvalidIdentificationNameErrorView() {
        setErrorView(getString(R.string.px_invalid_identification_name));
    }

    @Override
    public void showInvalidIdentificationLastNameErrorView() {
        setErrorView(getString(R.string.px_invalid_identification_last_name));
    }

    @Override
    public void showInvalidIdentificationBusinessNameErrorView() {
        setErrorView(getString(R.string.px_invalid_identification_business_name));
    }

    @Override
    public void showInvalidIdentificationNumberErrorView() {
        setErrorView(getString(R.string.px_invalid_field));
    }

    @Override
    public void setName(final String identificationName) {
        mIdentificationTicketView.setIdentificationName(identificationName);
    }

    @Override
    public void setLastName(final String identificationLastName) {
        mIdentificationTicketView.setIdentificationLastName(identificationLastName);
    }

    @Override
    public void setNumber(final String identificationNumber) {
        mIdentificationTicketView.setIdentificationNumber(identificationNumber);
    }

    @Override
    public void identificationDraw() {
        mIdentificationTicketView.draw();
    }

    private void setListeners() {
        setIdentificationTypeListeners();
        setIdentificationNumberEditTextListeners();
        setIdentificationNameEditTextListeners();
        setIdentificationLastNameEditTextListeners();
        setIdentificationBusinessNameEditTextListeners();
        setNextButtonListeners();
        setBackButtonListeners();
    }

    private void setIdentificationNumberEditTextListeners() {
        mIdentificationNumberEditText.setOnEditorActionListener((v, actionId, event) -> onNextKey(actionId, event));

        mIdentificationNumberEditText.setOnTouchListener((v, event) -> {
            setAlphaColorText();
            onTouchEditText(mIdentificationNumberEditText, event);
            return true;
        });

        mIdentificationNumberEditText.addTextChangedListener(
            new TicketIdentificationNumberTextWatcher(new TicketIdentificationNumberEditTextCallback() {
                @Override
                public void checkOpenKeyboard() {
                    setAlphaColorText();
                    openKeyboard(mIdentificationNumberEditText);
                }

                @Override
                public void saveIdentificationNumber(final CharSequence string) {
                    presenter.saveIdentificationNumber(string.toString());

                    mIdentificationTicketView.setIdentificationNumber(string.toString());

                    setAlphaColorText();
                    mIdentificationTicketView.draw();
                }

                @Override
                public void changeErrorView() {
                    setAlphaColorText();
                    checkChangeErrorView();
                }

                @Override
                public void toggleLineColorOnError(final boolean toggle) {
                    setAlphaColorText();
                    mIdentificationNumberEditText.toggleLineColorOnError(toggle);
                }
            }));
    }

    /* default */ void setAlphaColorText() {
        mIdentificationTicketView.configureAlphaColorNameText();
        mIdentificationTicketView.configureAlphaColorLastNameText();
    }

    private void setIdentificationNameEditTextListeners() {
        mIdentificationNameEditText.setFilters(new InputFilter[] { new InputFilter.AllCaps() });

        mIdentificationNameEditText.setOnEditorActionListener((v, actionId, event) -> onNextKey(actionId, event));

        mIdentificationNameEditText.setOnTouchListener((v, event) -> {
            onTouchEditText(mIdentificationNameEditText, event);
            return true;
        });

        mIdentificationNameEditText.addTextChangedListener(
            new TicketIdentificationNameTextWatcher(new TicketIdentificationNameEditTextCallback() {
                @Override
                public void checkOpenKeyboard() {
                    openKeyboard(mIdentificationNameEditText);
                }

                @Override
                public void saveIdentificationName(final CharSequence string) {
                    presenter.saveIdentificationName(string.toString());

                    mIdentificationTicketView.setIdentificationName(string.toString());
                    mIdentificationTicketView.draw();
                }

                @Override
                public void changeErrorView() {
                    checkChangeErrorView();
                }

                @Override
                public void toggleLineColorOnError(final boolean toggle) {
                    mIdentificationNameEditText.toggleLineColorOnError(toggle);
                }
            }));
    }

    private void setIdentificationLastNameEditTextListeners() {
        mIdentificationLastNameEditText.setFilters(new InputFilter[] { new InputFilter.AllCaps() });

        mIdentificationLastNameEditText.setOnEditorActionListener((v, actionId, event) -> onNextKey(actionId, event));

        mIdentificationLastNameEditText.setOnTouchListener((v, event) -> {
            onTouchEditText(mIdentificationLastNameEditText, event);
            return true;
        });

        mIdentificationLastNameEditText.addTextChangedListener(
            new TicketIdentificationNameTextWatcher(new TicketIdentificationNameEditTextCallback() {
                @Override
                public void checkOpenKeyboard() {
                    openKeyboard(mIdentificationLastNameEditText);
                }

                @Override
                public void saveIdentificationName(final CharSequence string) {
                    presenter.saveIdentificationLastName(string.toString());

                    mIdentificationTicketView.setIdentificationLastName(string.toString());
                    mIdentificationTicketView.draw();
                }

                @Override
                public void changeErrorView() {
                    checkChangeErrorView();
                }

                @Override
                public void toggleLineColorOnError(final boolean toggle) {
                    mIdentificationLastNameEditText.toggleLineColorOnError(toggle);
                }
            }));
    }

    private void setIdentificationBusinessNameEditTextListeners() {
        mIdentificationBusinessNameEditText.setFilters(new InputFilter[] { new InputFilter.AllCaps() });

        mIdentificationBusinessNameEditText.setOnEditorActionListener(
            (v, actionId, event) -> onNextKey(actionId, event));

        mIdentificationBusinessNameEditText.setOnTouchListener((v, event) -> {
            onTouchEditText(mIdentificationBusinessNameEditText, event);
            return true;
        });

        mIdentificationBusinessNameEditText.addTextChangedListener(
            new TicketIdentificationNameTextWatcher(new TicketIdentificationNameEditTextCallback() {
                @Override
                public void checkOpenKeyboard() {
                    openKeyboard(mIdentificationBusinessNameEditText);
                }

                @Override
                public void saveIdentificationName(final CharSequence string) {
                    presenter.saveIdentificationBusinessName(string.toString());

                    mIdentificationTicketView.setIdentificationName(string.toString());
                    mIdentificationTicketView.draw();
                }

                @Override
                public void changeErrorView() {
                    checkChangeErrorView();
                }

                @Override
                public void toggleLineColorOnError(final boolean toggle) {
                    mIdentificationBusinessNameEditText.toggleLineColorOnError(toggle);
                }
            }));
    }

    public void setIdentificationTypeListeners() {
        mIdentificationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
                final IdentificationType identificationType =
                    (IdentificationType) mIdentificationTypeSpinner.getSelectedItem();

                mIdentificationTicketView.setIdentificationType(identificationType);
                mIdentificationTicketView.drawIdentificationTypeName();
                presenter.configureIdentificationTypeFlow(identificationType);
                clearInputs();

                presenter.saveIdentificationType(identificationType);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                //Do something
            }
        });
        mIdentificationTypeSpinner.setOnTouchListener((v, event) -> {
            openKeyboard(mIdentificationNumberEditText);
            return false;
        });
    }

    /* default */ void clearInputs() {
        mIdentificationBusinessNameEditText.setText(TextUtil.EMPTY);
        mIdentificationLastNameEditText.setText(TextUtil.EMPTY);
        mIdentificationNameEditText.setText(TextUtil.EMPTY);
        mIdentificationNumberEditText.setText(TextUtil.EMPTY);
    }

    private void setNextButtonListeners() {
        mNextButton.setOnClickListener(v -> presenter.validateCurrentEditText());
    }

    private void setBackButtonListeners() {
        mBackButton.setOnClickListener(v -> presenter.onBackPressed());
    }

    @Override
    public void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        presenter.trackAbort();
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    public void setIdentificationNumberRestrictions(final String type) {
        setInputMaxLength(mIdentificationNumberEditText, presenter.getIdentificationNumberMaxLength());
        if ("number".equals(type)) {
            mIdentificationNumberEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            mIdentificationNumberEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        if (!mIdentificationNumberEditText.getText().toString().isEmpty()) {
            presenter.validateIdentification();
        }
    }

    private void setInputMaxLength(final MPEditText text, final int maxLength) {
        final InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        text.setFilters(fArray);
    }

    /* default */ boolean onNextKey(final int actionId, final KeyEvent event) {
        if (isNextKey(actionId, event)) {
            presenter.validateCurrentEditText();
            return true;
        }
        return false;
    }

    private boolean isNextKey(final int actionId, final KeyEvent event) {
        return actionId == EditorInfo.IME_ACTION_NEXT ||
            (event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
    }

    private void onTouchEditText(final MPEditText editText, final MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_DOWN) {
            openKeyboard(editText);
        }
    }

    /* default */ void openKeyboard(final MPEditText ediText) {
        ediText.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ediText, InputMethodManager.SHOW_IMPLICIT);
        fullScrollDown();
    }

    private void fullScrollDown() {
        final Runnable r = () -> mScrollView.fullScroll(View.FOCUS_DOWN);
        mScrollView.post(r);
        r.run();
    }

    @Override
    public void showCardFlowEnd(@NonNull final String currentFocus) {
        if (currentFocus.equals(PayerInformationFocus.LAST_NAME_INPUT)) {
            mIdentificationLastNameInput.setVisibility(View.GONE);
        } else {
            mIdentificationBusinessNameEditText.setVisibility(View.GONE);
        }
        showFinishCardFlow();
    }

    @Override
    public void showIdentificationNumberFocus() {
        mIdentificationNumberInput.setVisibility(View.VISIBLE);
        mIdentificationLastNameInput.setVisibility(View.GONE);
        mIdentificationNameInput.setVisibility(View.GONE);
        mIdentificationBusinessNameInput.setVisibility(View.GONE);
        presenter.setCurrentFocus(PayerInformationFocus.NUMBER_INPUT);
        openKeyboard(mIdentificationNumberEditText);
    }

    @Override
    public void showIdentificationLastNameFocus() {
        mIdentificationLastNameInput.setVisibility(View.VISIBLE);
        mIdentificationNameInput.setVisibility(View.GONE);
        mIdentificationBusinessNameInput.setVisibility(View.GONE);
        mIdentificationNumberInput.setVisibility(View.GONE);
        presenter.setCurrentFocus(PayerInformationFocus.LAST_NAME_INPUT);
        openKeyboard(mIdentificationLastNameEditText);
    }

    @Override
    public void showIdentificationNameFocus() {
        mIdentificationNameInput.setVisibility(View.VISIBLE);
        mIdentificationNumberInput.setVisibility(View.GONE);
        mIdentificationBusinessNameInput.setVisibility(View.GONE);
        mIdentificationLastNameInput.setVisibility(View.GONE);
        presenter.setCurrentFocus(PayerInformationFocus.NAME_INPUT);
        openKeyboard(mIdentificationNameEditText);
    }

    @Override
    public void showIdentificationBusinessNameFocus() {
        mIdentificationNumberInput.setVisibility(View.GONE);
        mIdentificationNameInput.setVisibility(View.GONE);
        mIdentificationLastNameInput.setVisibility(View.GONE);
        mIdentificationBusinessNameInput.setVisibility(View.VISIBLE);
        presenter.setCurrentFocus(PayerInformationFocus.BUSINESS_NAME_INPUT);
        openKeyboard(mIdentificationBusinessNameEditText);
    }

    private void showFinishCardFlow() {
        ViewUtils.hideKeyboard(this);
        showProgressBar();
        presenter.createPayer();
        finishWithPayer();
    }

    @Override
    public void showError(final MercadoPagoError error, final String requestOrigin) {
        if (error.isApiException()) {
            showApiException(error.getApiException(), requestOrigin);
        } else {
            ErrorUtil.startErrorActivity(this, error);
        }
    }

    public void showApiException(final ApiException apiException, final String requestOrigin) {
        if (mActivityActive) {
            ErrorUtil.showApiExceptionError(this, apiException, requestOrigin);
        }
    }

    /* default */ void checkChangeErrorView() {
        if (ERROR_STATE.equals(mErrorState)) {
            clearErrorView();
        }
    }

    @Override
    public void clearErrorView() {
        mButtonContainer.setVisibility(View.VISIBLE);
        mErrorContainer.setVisibility(View.GONE);
        mErrorTextView.setText(TextUtil.EMPTY);
        setErrorState(NORMAL_STATE);
    }

    @Override
    public void setErrorView(final String message) {
        mButtonContainer.setVisibility(View.GONE);
        mErrorContainer.setVisibility(View.VISIBLE);
        mErrorTextView.setText(message);
        setErrorState(ERROR_STATE);
    }

    private void setErrorState(final String mErrorState) {
        this.mErrorState = mErrorState;
    }

    @Override
    public void clearErrorIdentificationNumber() {
        mIdentificationNumberEditText.toggleLineColorOnError(false);
    }

    @Override
    public void clearErrorName() {
        mIdentificationNameEditText.toggleLineColorOnError(false);
    }

    @Override
    public void clearErrorLastName() {
        mIdentificationLastNameEditText.toggleLineColorOnError(false);
    }

    @Override
    public void clearErrorBusinessName() {
        mIdentificationBusinessNameEditText.toggleLineColorOnError(false);
    }

    @Override
    public void showErrorIdentificationNumber() {
        ViewUtils.openKeyboard(mIdentificationNumberEditText);
        mIdentificationNumberEditText.toggleLineColorOnError(true);
        mIdentificationNumberEditText.requestFocus();
    }

    @Override
    public void showErrorName() {
        ViewUtils.openKeyboard(mIdentificationNameEditText);
        mIdentificationNameEditText.toggleLineColorOnError(true);
        mIdentificationNameEditText.requestFocus();
    }

    @Override
    public void showErrorLastName() {
        ViewUtils.openKeyboard(mIdentificationLastNameEditText);
        mIdentificationLastNameEditText.toggleLineColorOnError(true);
        mIdentificationLastNameEditText.requestFocus();
    }

    @Override
    public void showErrorBusinessName() {
        ViewUtils.openKeyboard(mIdentificationBusinessNameEditText);
        mIdentificationLastNameEditText.toggleLineColorOnError(true);
        mIdentificationLastNameEditText.requestFocus();
    }

    @Override
    public void configureCpfFlow() {
        mIdentificationTicketView.drawNamePlaceholder();
        mIdentificationTicketView.drawCpfIdentificationNumberPlaceholder();
    }

    @Override
    public void configureCnpjFlow() {
        mIdentificationTicketView.drawBusinessNamePlaceholder();
        mIdentificationTicketView.drawCnpjIdentificationNumberPlaceholder();
    }

    private void finishWithPayer() {
        setResult(RESULT_OK);
        finish();
    }
}