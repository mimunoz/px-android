package com.mercadopago.android.px.internal.features.pay_button

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mercadopago.android.px.any
import com.mercadopago.android.px.argumentCaptor
import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.internal.audio.AudioPlayer
import com.mercadopago.android.px.internal.callbacks.PaymentServiceEventHandler
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.core.ProductIdProvider
import com.mercadopago.android.px.internal.features.PaymentResultViewModelFactory
import com.mercadopago.android.px.internal.features.checkout.PostPaymentUrlsMapper
import com.mercadopago.android.px.internal.features.explode.ExplodeDecorator
import com.mercadopago.android.px.internal.features.express.RenderMode
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModelMapper
import com.mercadopago.android.px.internal.features.payment_result.PaymentResultDecorator
import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesModel
import com.mercadopago.android.px.internal.features.security_code.model.SecurityCodeParams
import com.mercadopago.android.px.internal.livedata.MutableSingleLiveData
import com.mercadopago.android.px.internal.mappers.PayButtonViewModelMapper
import com.mercadopago.android.px.internal.model.SecurityType
import com.mercadopago.android.px.internal.repository.CustomTextsRepository
import com.mercadopago.android.px.internal.repository.PaymentRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentResultType
import com.mercadopago.android.px.model.*
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CongratsResponse
import com.mercadopago.android.px.model.internal.CustomTexts
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.model.internal.remedies.RemediesResponse
import com.mercadopago.android.px.preferences.CheckoutPreference
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.model.Reason
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner
import com.mercadopago.android.px.internal.viewmodel.PayButtonViewModel as PayButtonTexts

@RunWith(MockitoJUnitRunner::class)
internal class PayButtonViewModelTest {

    private lateinit var payButtonViewModel: PayButtonViewModel

    @Mock
    private lateinit var paymentService: PaymentRepository
    @Mock
    private lateinit var productIdProvider: ProductIdProvider
    @Mock
    private lateinit var connectionHelper: ConnectionHelper
    @Mock
    private lateinit var paymentSettingRepository: PaymentSettingRepository
    @Mock
    private lateinit var customTextsRepository: CustomTextsRepository
    @Mock
    private lateinit var payButtonViewModelMapper: PayButtonViewModelMapper
    @Mock
    private lateinit var paymentCongratsMapper: PaymentCongratsModelMapper
    @Mock
    private lateinit var customTexts: CustomTexts
    @Mock
    private lateinit var payButtonTexts: PayButtonTexts
    @Mock
    private lateinit var handler: PayButton.Handler
    @Mock
    private lateinit var buttonTextObserver: Observer<PayButtonTexts>
    @Mock
    private lateinit var uiStateObserver: Observer<PayButtonUiState>
    @Mock
    private lateinit var cvvRequiredObserver: Observer<SecurityCodeParams>
    @Mock
    private lateinit var paymentResultViewModelFactory: PaymentResultViewModelFactory
    @Mock
    private lateinit var state: PayButtonViewModel.State
    @Mock
    private lateinit var postPaymentUrlsMapper: PostPaymentUrlsMapper

    private val paymentErrorLiveData = MutableSingleLiveData<MercadoPagoError>()
    private val paymentFinishedLiveData = MutableSingleLiveData<PaymentModel>()
    private val requireCvvLiveData = MutableSingleLiveData<Pair<Card,Reason>>()
    private val recoverInvalidEscLiveData = MutableSingleLiveData<PaymentRecovery>()
    private val visualPaymentLiveData = MutableSingleLiveData<Unit>()

    /*
    * https://stackoverflow.com/questions/29945087/kotlin-and-new-activitytestrule-the-rule-must-be-public
    * */
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        `when`(customTextsRepository.customTexts).thenReturn(customTexts)
        `when`(payButtonViewModelMapper.map(customTexts)).thenReturn(payButtonTexts)
        `when`(connectionHelper.checkConnection()).thenReturn(true)
        `when`(paymentSettingRepository.checkoutPreference).thenReturn(mock(CheckoutPreference::class.java))
        `when`(paymentSettingRepository.site).thenReturn(Sites.ARGENTINA)
        configurePaymentSettingServiceObservableEvents()

        payButtonViewModel = PayButtonViewModel(
            paymentService,
            productIdProvider,
            connectionHelper,
            paymentSettingRepository,
            customTextsRepository,
            payButtonViewModelMapper,
            paymentCongratsMapper,
            postPaymentUrlsMapper,
            paymentResultViewModelFactory,
            mock(AudioPlayer::class.java),
            mock(MPTracker::class.java))

        payButtonViewModel.stateUILiveData.observeForever(uiStateObserver)
        payButtonViewModel.buttonTextLiveData.observeForever(buttonTextObserver)
        payButtonViewModel.cvvRequiredLiveData.observeForever(cvvRequiredObserver)
        payButtonViewModel.attach(handler)

        `when`(state.paymentConfiguration).thenReturn(mock(PaymentConfiguration::class.java))
        payButtonViewModel.restoreState(state)

        verify(buttonTextObserver).onChanged(any(PayButtonTexts::class.java))
        assertNotNull(handler)
    }

    @Test
    fun preparePaymentWhenNonConnection() {
        `when`(connectionHelper.checkConnection()).thenReturn(false)
        payButtonViewModel.preparePayment()
        verify(uiStateObserver).onChanged(any(UIError.ConnectionError::class.java))
    }

    @Test
    fun preparePaymentWhenHasConnection() {
        val callback = argumentCaptor<PayButton.OnReadyForPaymentCallback>()
        payButtonViewModel.preparePayment()
        verify(handler).prePayment(callback.capture())
        callback.value.call(mock(PaymentConfiguration::class.java))
        verify(uiStateObserver).onChanged(any(UIProgress.FingerprintRequired::class.java))
    }

    @Test
    fun handleBiometricsResultWithoutFrictionAndSuccess() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()

        payButtonViewModel.handleBiometricsResult(isSuccess = true, securityRequested = true)

        verify(uiStateObserver).onChanged(any(UIProgress.ButtonLoadingStarted::class.java))
        verify(paymentSettingRepository).configure(SecurityType.SECOND_FACTOR)
        verify(handler).enqueueOnExploding(callback.capture())
        callback.value.success()
        verify(paymentService).startExpressPayment(any(PaymentConfiguration::class.java))
    }

    @Test
    fun handleBiometricsResultWithoutFrictionAndFailure() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()

        payButtonViewModel.handleBiometricsResult(isSuccess = true, securityRequested = true)

        verify(paymentSettingRepository).configure(SecurityType.SECOND_FACTOR)
        verify(handler).enqueueOnExploding(callback.capture())
        callback.value.failure()
        verify(uiStateObserver).onChanged(any(UIProgress.ButtonLoadingCanceled::class.java))
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsErrorEventAndIsPaymentProcessingError() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val error = mock(MercadoPagoError::class.java)
        val payButtonViewModelSpy = spy(payButtonViewModel)
        `when`(error.isPaymentProcessing).thenReturn(true)
        `when`(paymentSettingRepository.currency).thenReturn(mock(Currency::class.java))
        `when`(paymentService.paymentDataList).thenReturn(mock(MutableList::class.java) as MutableList<PaymentData>)
        `when`(paymentResultViewModelFactory.createPaymentResultDecorator(any())).thenReturn(mock(PaymentResultDecorator::class.java))

        payButtonViewModelSpy.startPayment()
        paymentErrorLiveData.value = error

        verify(handler).enqueueOnExploding(callback.capture())
        callback.value.success()
        verify(payButtonViewModelSpy).onPostPayment(any(PaymentModel::class.java))
        verify(uiStateObserver).onChanged(any(UIProgress.ButtonLoadingFinished::class.java))
        verify(handler).onPaymentError(error)
        verify(uiStateObserver).onChanged(any(UIProgress.ButtonLoadingCanceled::class.java))
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsErrorEventAndIsNoRecoverableError() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val error = mock(MercadoPagoError::class.java)

        `when`(error.isPaymentProcessing).thenReturn(false)

        payButtonViewModel.startPayment()
        paymentErrorLiveData.value = error

        verify(handler).enqueueOnExploding(callback.capture())
        callback.value.success()
        verify(uiStateObserver).onChanged(any(UIError.BusinessError::class.java))
        verify(handler).onPaymentError(error)
        verify(uiStateObserver).onChanged(any(UIProgress.ButtonLoadingCanceled::class.java))
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsVisualPaymentEvent() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()

        payButtonViewModel.startPayment()
        visualPaymentLiveData.value = Unit

        verify(handler).enqueueOnExploding(callback.capture())
        callback.value.success()
        verify(uiStateObserver).onChanged(any(UIResult.VisualProcessorResult::class.java))
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsPaymentFinishedEventAndIsRemedies() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val paymentModel = mock(PaymentModel::class.java)
        `when`(paymentModel.remedies).thenReturn(mock(RemediesResponse::class.java))
        `when`(paymentModel.remedies.hasRemedies()).thenReturn(true)

        payButtonViewModel.startPayment()
        paymentFinishedLiveData.value = paymentModel

        verify(handler).enqueueOnExploding(callback.capture())
        callback.value.success()
        verify(uiStateObserver).onChanged(any(UIProgress.ButtonLoadingFinished::class.java))

        val actual = (payButtonViewModel.stateUILiveData.value as UIProgress.ButtonLoadingFinished)
        assertTrue(ReflectionEquals(actual.explodeDecorator).matches(ExplodeDecorator.from(RemediesModel.DECORATOR)))
    }

    @Test
    fun onResultIconAnimationAndPaymentSuccessThenPlayAudio() {
        val paymentModel = mock(PaymentModel::class.java)
        val paymentResult = mock(PaymentResult::class.java)
        `when`(paymentResult.isApproved).thenReturn(true)
        `when`(paymentModel.paymentResult).thenReturn(paymentResult)
        `when`(state.paymentModel).thenReturn(paymentModel)

        payButtonViewModel.onResultIconAnimation()

        verify(uiStateObserver).onChanged(any(UIProgress.PlayResultAudio::class.java))
        val actual = (payButtonViewModel.stateUILiveData.value as UIProgress.PlayResultAudio)
        actual.sound.assertEquals(AudioPlayer.Sound.SUCCESS)
    }

    @Test
    fun onResultIconAnimationAndPaymentRejectedThenPlayAudio() {
        val paymentModel = mock(PaymentModel::class.java)
        val paymentResult = mock(PaymentResult::class.java)
        `when`(paymentResult.isRejected).thenReturn(true)
        `when`(paymentModel.paymentResult).thenReturn(paymentResult)
        `when`(state.paymentModel).thenReturn(paymentModel)

        payButtonViewModel.onResultIconAnimation()

        verify(uiStateObserver).onChanged(any(UIProgress.PlayResultAudio::class.java))
        val actual = (payButtonViewModel.stateUILiveData.value as UIProgress.PlayResultAudio)
        actual.sound.assertEquals(AudioPlayer.Sound.FAILURE)
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsPaymentFinishedEventAndIsBusiness() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val paymentModel = mock(BusinessPaymentModel::class.java)
        val payment = mock(BusinessPayment::class.java)
        `when`(paymentModel.remedies).thenReturn(mock(RemediesResponse::class.java))
        `when`(paymentModel.remedies.hasRemedies()).thenReturn(false)
        `when`(paymentModel.payment).thenReturn(payment)
        `when`(paymentModel.payment.decorator).thenReturn(BusinessPayment.Decorator.APPROVED)

        payButtonViewModel.startPayment()
        paymentFinishedLiveData.value = paymentModel

        verify(handler).enqueueOnExploding(callback.capture())
        callback.value.success()
        verify(uiStateObserver).onChanged(any(UIProgress.ButtonLoadingFinished::class.java))

        val actual = (payButtonViewModel.stateUILiveData.value as UIProgress.ButtonLoadingFinished)
        assertTrue(ReflectionEquals(actual.explodeDecorator).matches(ExplodeDecorator.from(PaymentResultType.from(payment.decorator))))
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsPaymentFinishedEventAndIsPaymentResult() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val paymentModel = mock(PaymentModel::class.java)
        val paymentResult = mock(PaymentResult::class.java)
        `when`(paymentModel.remedies).thenReturn(mock(RemediesResponse::class.java))
        `when`(paymentModel.remedies.hasRemedies()).thenReturn(false)
        `when`(paymentModel.paymentResult).thenReturn(paymentResult)
        `when`(paymentResultViewModelFactory.createPaymentResultDecorator(any())).thenReturn(mock(PaymentResultDecorator::class.java))

        payButtonViewModel.startPayment()
        paymentFinishedLiveData.value = paymentModel

        verify(handler).enqueueOnExploding(callback.capture())
        callback.value.success()
        verify(uiStateObserver).onChanged(any(UIProgress.ButtonLoadingFinished::class.java))
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsPaymentCvvRequiredEvent() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val paymentConfigurationCaptor = argumentCaptor<PaymentConfiguration>()
        val onChangeCaptor = argumentCaptor<SecurityCodeParams>()
        val reason = mock(Reason::class.java)
        val card = mock(Card::class.java)
        val cvvRequested = mock(PayButton.CvvRequestedModel::class.java)
        `when`(cvvRequested.renderMode).thenReturn(mock(RenderMode::class.java))
        `when`(handler.onCvvRequested()).thenReturn(cvvRequested)

        payButtonViewModel.startPayment()
        requireCvvLiveData.value = Pair(card,reason)

        verify(handler).enqueueOnExploding(callback.capture())
        callback.value.success()
        verify(paymentService).startExpressPayment(paymentConfigurationCaptor.capture())
        verify(handler).onCvvRequested()
        verify(cvvRequiredObserver).onChanged(onChangeCaptor.capture())
        val actualResult = onChangeCaptor.value
        assertTrue(ReflectionEquals(actualResult.paymentConfiguration).matches(paymentConfigurationCaptor.value))
        assertTrue(ReflectionEquals(actualResult.reason).matches(reason))
    }

    @Test
    fun startPaymentAndObserveServiceWhenIsInvalidEscEventAndShouldAskForCvv() {
        val callback = argumentCaptor<PayButton.OnEnqueueResolvedCallback>()
        val paymentConfigurationCaptor = argumentCaptor<PaymentConfiguration>()
        val onChangeCaptor = argumentCaptor<SecurityCodeParams>()
        val paymentRecoveryMock = mock(PaymentRecovery::class.java)
        `when`(paymentRecoveryMock.shouldAskForCvv()).thenReturn(true)
        val cvvRequested = mock(PayButton.CvvRequestedModel::class.java)
        `when`(cvvRequested.renderMode).thenReturn(mock(RenderMode::class.java))
        `when`(handler.onCvvRequested()).thenReturn(cvvRequested)

        payButtonViewModel.startPayment()
        recoverInvalidEscLiveData.value = paymentRecoveryMock

        verify(handler).enqueueOnExploding(callback.capture())
        callback.value.success()
        verify(paymentService).startExpressPayment(paymentConfigurationCaptor.capture())
        verify(handler).onCvvRequested()
        verify(cvvRequiredObserver).onChanged(onChangeCaptor.capture())
        val actualResult = onChangeCaptor.value
        assertTrue(ReflectionEquals(actualResult.paymentConfiguration).matches(paymentConfigurationCaptor.value))
        assertTrue(ReflectionEquals(actualResult.paymentRecovery).matches(paymentRecoveryMock))
    }

    @Test
    fun onFinishPaymentAnimationWithRegularPaymentThenShowCongrats() {
        val callback = argumentCaptor<PayButton.OnPaymentFinishedCallback>()
        val paymentModelCaptor = argumentCaptor<PaymentModel>()
        val paymentModel = mock(PaymentModel::class.java)
        `when`(paymentModel.congratsResponse).thenReturn(mock(CongratsResponse::class.java))
        `when`(state.paymentModel).thenReturn(paymentModel)
        `when`(postPaymentUrlsMapper.map(any(PostPaymentUrlsMapper.Model::class.java)))
            .thenReturn(mock(PostPaymentUrlsMapper.Response::class.java))

        payButtonViewModel.hasFinishPaymentAnimation()

        verify(handler).onPaymentFinished(paymentModelCaptor.capture(), callback.capture())
        callback.value.call()
        verify(uiStateObserver).onChanged(any(UIResult.PaymentResult::class.java))
        val actual = (payButtonViewModel.stateUILiveData.value as UIResult.PaymentResult)
        paymentModelCaptor.value.assertEquals(paymentModel)
        actual.model.assertEquals(paymentModel)
    }

    @Test
    fun onFinishPaymentAnimationWithRedirectUrlThenSkipCongrats() {
        val callback = argumentCaptor<PayButton.OnPaymentFinishedCallback>()
        val paymentModelCaptor = argumentCaptor<PaymentModel>()
        val paymentModel = mock(PaymentModel::class.java)
        `when`(paymentModel.congratsResponse).thenReturn(mock(CongratsResponse::class.java))
        `when`(state.paymentModel).thenReturn(paymentModel)
        `when`(postPaymentUrlsMapper.map(any(PostPaymentUrlsMapper.Model::class.java)))
            .thenReturn(PostPaymentUrlsMapper.Response("redirect_url", null))

        payButtonViewModel.hasFinishPaymentAnimation()

        verify(handler).onPaymentFinished(paymentModelCaptor.capture(), callback.capture())
        callback.value.call()
        verify(uiStateObserver).onChanged(any(UIResult.NoCongratsResult::class.java))
        val actual = (payButtonViewModel.stateUILiveData.value as UIResult.NoCongratsResult)
        paymentModelCaptor.value.assertEquals(paymentModel)
        actual.model.assertEquals(paymentModel)
    }

    @Test
    fun onFinishPaymentAnimationWithBusinessPaymentThenShowCongrats() {
        val callback = argumentCaptor<PayButton.OnPaymentFinishedCallback>()
        val paymentModelCaptor = argumentCaptor<PaymentModel>()
        val paymentModel = mock(BusinessPaymentModel::class.java)
        val congratsModel = mock(PaymentCongratsModel::class.java)
        `when`(paymentModel.congratsResponse).thenReturn(mock(CongratsResponse::class.java))
        `when`(state.paymentModel).thenReturn(paymentModel)
        `when`(postPaymentUrlsMapper.map(any(PostPaymentUrlsMapper.Model::class.java)))
            .thenReturn(mock(PostPaymentUrlsMapper.Response::class.java))
        `when`(paymentCongratsMapper.map(paymentModel)).thenReturn(congratsModel)

        payButtonViewModel.hasFinishPaymentAnimation()

        verify(handler).onPaymentFinished(paymentModelCaptor.capture(), callback.capture())
        callback.value.call()
        verify(uiStateObserver).onChanged(any(UIResult.CongratsPaymentModel::class.java))
        val actual = (payButtonViewModel.stateUILiveData.value as UIResult.CongratsPaymentModel)
        paymentModelCaptor.value.assertEquals(paymentModel)
        actual.model.assertEquals(congratsModel)
    }

    private fun configurePaymentSettingServiceObservableEvents() {
        `when`(paymentService.observableEvents).thenReturn(mock(PaymentServiceEventHandler::class.java))
        `when`(paymentService.isExplodingAnimationCompatible).thenReturn(true)
        `when`(paymentService.observableEvents?.paymentErrorLiveData).thenReturn(paymentErrorLiveData)
        `when`(paymentService.observableEvents?.paymentFinishedLiveData).thenReturn(paymentFinishedLiveData)
        `when`(paymentService.observableEvents?.requireCvvLiveData).thenReturn(requireCvvLiveData)
        `when`(paymentService.observableEvents?.recoverInvalidEscLiveData).thenReturn(recoverInvalidEscLiveData)
        `when`(paymentService.observableEvents?.visualPaymentLiveData).thenReturn(visualPaymentLiveData)
    }
}
