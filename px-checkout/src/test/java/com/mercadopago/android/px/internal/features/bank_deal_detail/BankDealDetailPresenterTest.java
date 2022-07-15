package com.mercadopago.android.px.internal.features.bank_deal_detail;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.tracking.internal.MPTracker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@PrepareForTest(MPTracker.class)
@RunWith(PowerMockRunner.class)
public class BankDealDetailPresenterTest {
    private BankDealDetailPresenter presenter;

    @Mock private BankDealDetail.View view;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(MPTracker.class);
        when(MPTracker.getInstance()).thenReturn(mock(MPTracker.class));
        presenter = getPresenter();
    }

    @NonNull
    private BankDealDetailPresenter getBasePresenter(final BankDealDetail.View view) {
        final BankDealDetailPresenter presenter = new BankDealDetailPresenter();
        presenter.attachView(view);
        return presenter;
    }

    @NonNull
    private BankDealDetailPresenter getPresenter() {
        return getBasePresenter(view);
    }

    @Test
    public void whenGetViewCallbackOnSuccessThenHideLogoName(){
        presenter.onSuccess();

        verify(view).hideLogoName();
        verifyNoMoreInteractions(view);
    }

    @Test
    public void whenGetViewCallbackOnErrorThenHideLogo(){
        presenter.onError(mock(RuntimeException.class));

        verify(view).hideLogo();
        verifyNoMoreInteractions(view);
    }
}