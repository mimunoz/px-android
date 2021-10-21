package com.mercadopago.android.px.internal.features.express

import com.mercadopago.android.px.TestContextProvider
import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.configuration.DynamicDialogConfiguration
import com.mercadopago.android.px.core.DynamicDialogCreator
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.core.AuthorizationProvider
import com.mercadopago.android.px.internal.datasource.CustomOptionIdSolver
import com.mercadopago.android.px.internal.domain.CheckoutUseCase
import com.mercadopago.android.px.internal.domain.CheckoutWithNewCardUseCase
import com.mercadopago.android.px.internal.features.AmountDescriptorViewModelFactory
import com.mercadopago.android.px.internal.features.express.slider.HubAdapter
import com.mercadopago.android.px.internal.mappers.ElementDescriptorMapper
import com.mercadopago.android.px.internal.mappers.PaymentMethodDescriptorMapper
import com.mercadopago.android.px.internal.mappers.SummaryInfoMapper
import com.mercadopago.android.px.internal.repository.*
import com.mercadopago.android.px.internal.tracking.TrackingRepository
import com.mercadopago.android.px.internal.view.ElementDescriptorView
import com.mercadopago.android.px.internal.view.SummaryDetailDescriptorMapper
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem
import com.mercadopago.android.px.internal.viewmodel.drawables.PaymentMethodDrawableItemMapper
import com.mercadopago.android.px.mocks.CurrencyStub
import com.mercadopago.android.px.mocks.SiteStub
import com.mercadopago.android.px.model.*
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.Application
import com.mercadopago.android.px.model.internal.DisabledPaymentMethod
import com.mercadopago.android.px.model.internal.OneTapItem
import com.mercadopago.android.px.model.internal.SummaryInfo
import com.mercadopago.android.px.preferences.CheckoutPreference
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.AbortEvent
import com.mercadopago.android.px.tracking.internal.events.BackEvent
import com.mercadopago.android.px.tracking.internal.events.InstallmentsEventTrack
import com.mercadopago.android.px.tracking.internal.mapper.FromApplicationToApplicationInfo
import com.mercadopago.android.px.tracking.internal.views.OneTapViewTracker
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ExpressPaymentPresenterTest {

    @Mock
    private lateinit var view: ExpressPayment.View

    @Mock
    private lateinit var paymentRepository: PaymentRepository

    @Mock
    private lateinit var paymentSettingRepository: PaymentSettingRepository

    @Mock
    private lateinit var disabledPaymentMethodRepository: DisabledPaymentMethodRepository

    @Mock
    private lateinit var payerCostSelectionRepository: PayerCostSelectionRepository

    @Mock
    private lateinit var checkoutRepository: CheckoutRepository

    @Mock
    private lateinit var discountRepository: DiscountRepository

    @Mock
    private lateinit var amountConfigurationRepository: AmountConfigurationRepository

    @Mock
    private lateinit var amountRepository: AmountRepository

    @Mock
    private lateinit var oneTapItem: OneTapItem

    @Mock
    private lateinit var amountConfiguration: AmountConfiguration

    @Mock
    private lateinit var discountConfigurationModel: DiscountConfigurationModel

    @Mock
    private lateinit var advancedConfiguration: AdvancedConfiguration

    @Mock
    private lateinit var dynamicDialogConfiguration: DynamicDialogConfiguration

    @Mock
    private lateinit var chargeRepository: ChargeRepository

    @Mock
    private lateinit var escManagerBehaviour: ESCManagerBehaviour

    @Mock
    private lateinit var paymentMethodDrawableItemMapper: PaymentMethodDrawableItemMapper

    @Mock
    private lateinit var experimentsRepository: ExperimentsRepository

    @Mock
    private lateinit var payerComplianceRepository: PayerComplianceRepository

    @Mock
    private lateinit var applicationSelectionRepository: ApplicationSelectionRepository

    @Mock
    private lateinit var trackingRepository: TrackingRepository

    @Mock
    private lateinit var cardMetadata: CardMetadata

    @Mock
    private lateinit var tracker: MPTracker

    @Mock
    private lateinit var oneTapItemRepository: OneTapItemRepository

    @Mock
    private lateinit var payerPaymentMethodRepository: PayerPaymentMethodRepository

    @Mock
    private lateinit var modalRepository: ModalRepository

    @Mock
    private lateinit var summaryDetailDescriptorMapper: SummaryDetailDescriptorMapper

    @Mock
    private lateinit var summaryInfoMapper: SummaryInfoMapper

    @Mock
    private lateinit var elementDescriptorMapper: ElementDescriptorMapper

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var customOptionIdSolver: CustomOptionIdSolver

    @Mock
    private lateinit var amountDescriptorViewModelFactory: AmountDescriptorViewModelFactory

    @Mock
    private lateinit var authorizationProvider: AuthorizationProvider

    private lateinit var checkoutUseCase : CheckoutUseCase
    private lateinit var checkoutWithNewCardUseCase : CheckoutWithNewCardUseCase

    private lateinit var expressPaymentPresenter: ExpressPaymentPresenter

    @Before
    fun setUp() {
        //This is needed for the presenter constructor
        val preference = mock(CheckoutPreference::class.java)
        val applicationPaymentMethod = mock(Application.PaymentMethod::class.java)
        val item = mock(Item::class.java)
        checkoutUseCase = CheckoutUseCase(checkoutRepository, tracker, TestContextProvider())
        checkoutWithNewCardUseCase = CheckoutWithNewCardUseCase(checkoutRepository, tracker, TestContextProvider())
        `when`(application.paymentMethod).thenReturn(Application.PaymentMethod("id", "type"))
        `when`(preference.items).thenReturn(listOf(item))
        `when`(paymentSettingRepository.site).thenReturn(SiteStub.MLA.get())
        `when`(paymentSettingRepository.currency).thenReturn(CurrencyStub.MLA.get())
        `when`(paymentSettingRepository.checkoutPreference).thenReturn(preference)
        `when`(paymentSettingRepository.advancedConfiguration).thenReturn(advancedConfiguration)
        `when`(advancedConfiguration.dynamicDialogConfiguration).thenReturn(dynamicDialogConfiguration)
        `when`(oneTapItem.isCard).thenReturn(true)
        `when`(oneTapItem.card).thenReturn(cardMetadata)
        `when`(cardMetadata.displayInfo).thenReturn(mock(CardDisplayInfo::class.java))
        `when`(cardMetadata.id).thenReturn("123")
        `when`(customOptionIdSolver[oneTapItem]).thenReturn("123")
        `when`(oneTapItem.status).thenReturn(mock(StatusMetadata::class.java))
        `when`(discountRepository.getCurrentConfiguration()).thenReturn(discountConfigurationModel)
        `when`(amountConfigurationRepository.getConfigurationSelectedFor("123")).thenReturn(amountConfiguration)
        `when`(oneTapItemRepository.value).thenReturn(listOf(oneTapItem))
        `when`(disabledPaymentMethodRepository.value).thenReturn(hashMapOf())
        `when`(applicationPaymentMethod.type).thenReturn("credit_card")
        `when`(application.paymentMethod).thenReturn(applicationPaymentMethod)
        `when`(applicationSelectionRepository[oneTapItem]).thenReturn(application)
        `when`(applicationSelectionRepository[CustomOptionIdSolver.defaultCustomOptionId(oneTapItem)]).thenReturn(application)
        `when`(summaryInfoMapper.map(preference)).thenReturn(mock(SummaryInfo::class.java))
        `when`(elementDescriptorMapper.map(any(SummaryInfo::class.java))).thenReturn(mock(ElementDescriptorView.Model::class.java))
        expressPaymentPresenter = ExpressPaymentPresenter(
            paymentSettingRepository,
            disabledPaymentMethodRepository,
            payerCostSelectionRepository,
            applicationSelectionRepository,
            discountRepository,
            amountRepository,
            checkoutUseCase,
            checkoutWithNewCardUseCase,
            amountConfigurationRepository,
            chargeRepository,
            escManagerBehaviour,
            experimentsRepository,
            payerComplianceRepository,
            trackingRepository,
            mock(CustomTextsRepository::class.java),
            oneTapItemRepository,
            payerPaymentMethodRepository,
            modalRepository,
            customOptionIdSolver,
            paymentMethodDrawableItemMapper,
            mock(PaymentMethodDescriptorMapper::class.java),
            summaryDetailDescriptorMapper,
            summaryInfoMapper,
            elementDescriptorMapper,
            mock(FromApplicationToApplicationInfo::class.java),
            authorizationProvider,
            amountDescriptorViewModelFactory,
            tracker
        )
        verifyAttachView()
    }

    @Test
    fun whenFailToRetrieveCheckoutThenShowError() {
        runBlocking {
            whenever(checkoutRepository.checkout()).thenReturn(Response.Failure(mock(MercadoPagoError::class.java)))
        }
        expressPaymentPresenter.handleDeepLink()
        runBlocking{
            verify(checkoutRepository).checkout()
        }
        verify(view).showError(any(MercadoPagoError::class.java))
    }

    @Test
    fun whenBackThenTrackAbort() {
        expressPaymentPresenter.onFreshStart()
        expressPaymentPresenter.onBack()

        verify(tracker).track(any(OneTapViewTracker::class.java))
        verify(tracker).track(any(AbortEvent::class.java))
        verifyNoMoreInteractions(tracker)
    }

    @Test
    fun whenCanceledThenCancelAndTrack() {
        expressPaymentPresenter.onFreshStart()
        expressPaymentPresenter.cancel()

        verify(view).cancel()
        verifyNoMoreInteractions(view)
        verify(tracker).track(any(OneTapViewTracker::class.java))
        verify(tracker).track(any(BackEvent::class.java))
        verifyNoMoreInteractions(tracker)
    }

    @Test
    fun whenInstallmentsRowPressedShowInstallments() {
        val selectedPayerCostIndex = 2
        `when`(amountConfiguration.getCurrentPayerCostIndex(anyBoolean(), anyInt())).thenReturn(selectedPayerCostIndex)

        expressPaymentPresenter.onInstallmentsRowPressed()

        verify(view).updateInstallmentsList(eq(selectedPayerCostIndex), anyList())
        verify(view).animateInstallmentsList()
        verify(tracker).track(any(InstallmentsEventTrack::class.java))
        verifyNoMoreInteractions(tracker)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun whenInstallmentsSelectionCancelledThenCollapseInstallments() {
        val paymentMethodIndex = 0
        val splitSelectionState = mock(SplitSelectionState::class.java)
        val state = mock(ExpressPaymentState::class.java)
        `when`(state.paymentMethodIndex).thenReturn(paymentMethodIndex)
        `when`(state.splitSelectionState).thenReturn(splitSelectionState)
        val payerCostIndex = 2
        `when`(payerCostSelectionRepository.get(customOptionIdSolver[oneTapItem])).thenReturn(payerCostIndex)

        expressPaymentPresenter.restoreState(state)
        expressPaymentPresenter.onInstallmentSelectionCanceled()

        verify(view).updateViewForPosition(paymentMethodIndex, payerCostIndex, splitSelectionState, application)
        verify(view).collapseInstallmentsSelection()
    }

    @Test
    fun whenViewIsResumedThenPaymentRepositoryIsAttached() {
        verifyNoMoreInteractions(paymentRepository)
        verifyNoMoreInteractions(view)
        verifyNoMoreInteractions(dynamicDialogConfiguration)
    }

    @Test
    fun whenElementDescriptorViewClickedAndHasCreatorThenShowDynamicDialog() {
        val dynamicDialogCreatorMock = mock(DynamicDialogCreator::class.java)
        `when`(dynamicDialogConfiguration.hasCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER))
            .thenReturn(true)
        `when`(dynamicDialogConfiguration.getCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER))
            .thenReturn(dynamicDialogCreatorMock)

        expressPaymentPresenter.onHeaderClicked()

        verify(dynamicDialogConfiguration).hasCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER)
        verify(dynamicDialogConfiguration).getCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER)
        verify(view).showDynamicDialog(eq(dynamicDialogCreatorMock), any(DynamicDialogCreator.CheckoutData::class.java))
        verifyNoMoreInteractions(view)
        verifyNoMoreInteractions(dynamicDialogConfiguration)
    }

    @Test
    fun whenElementDescriptorViewClickedAndHasNotCreatorThenDoNotShowDynamicDialog() {
        expressPaymentPresenter.onHeaderClicked()

        verify(dynamicDialogConfiguration).hasCreatorFor(DynamicDialogConfiguration.DialogLocation.TAP_ONE_TAP_HEADER)
        verifyNoMoreInteractions(view)
        verifyNoMoreInteractions(dynamicDialogConfiguration)
    }

    @Test
    fun whenDisabledDescriptorViewClickThenShowDisabledDialog() {
        val disabledPaymentMethod = mock(DisabledPaymentMethod::class.java)
        val statusMetadata = mock(StatusMetadata::class.java)
        `when`(disabledPaymentMethodRepository[any()]).thenReturn(disabledPaymentMethod)
        `when`(applicationSelectionRepository[any<String>()]).thenReturn(application)
        `when`(application.status).thenReturn(statusMetadata)

        expressPaymentPresenter.onDisabledDescriptorViewClick()

        verify(view).showDisabledPaymentMethodDetailDialog(disabledPaymentMethod, statusMetadata)
    }

    @Test
    fun whenSliderOptionSelectedThenShowInstallmentsRow() {
        `when`(payerCostSelectionRepository[anyString()]).thenReturn(PayerCost.NO_SELECTED)
        val currentElementPosition = 0

        expressPaymentPresenter.onSliderOptionSelected(currentElementPosition)

        verify(view).updateViewForPosition(eq(currentElementPosition), eq(PayerCost.NO_SELECTED), any(), any())
        verifyNoMoreInteractions(view)
    }

    @Test
    fun whenPayerCostSelectedThenItsReflectedOnView() {
        val paymentMethodIndex = 0
        val selectedPayerCostIndex = 1
        val payerCostList = mockPayerCosts(selectedPayerCostIndex)

        expressPaymentPresenter.onPayerCostSelected(payerCostList[selectedPayerCostIndex])

        verify(view).updateViewForPosition(eq(paymentMethodIndex), eq(selectedPayerCostIndex), any(), any())
        verify(view).collapseInstallmentsSelection()
        verifyNoMoreInteractions(view)
    }

    private fun mockPayerCosts(selectedPayerCostIndex: Int): List<PayerCost> {
        `when`(payerCostSelectionRepository[anyString()]).thenReturn(selectedPayerCostIndex)
        val firstPayerCost = mock(PayerCost::class.java)
        val payerCostList = listOf(mock(PayerCost::class.java), firstPayerCost, mock(PayerCost::class.java))
        `when`(amountConfiguration.getAppliedPayerCost(false)).thenReturn(payerCostList)
        return payerCostList
    }

    private fun verifyAttachView() {
        expressPaymentPresenter.attachView(view)
        verify(view).configurePayButton(any())
        verify(view).configurePaymentMethodHeader(anyList())
        verify(view).showToolbarElementDescriptor(any(ElementDescriptorView.Model::class.java))
        verify(view).updateAdapters(any(HubAdapter.Model::class.java))
        verify(view).updateViewForPosition(
            anyInt(), anyInt(), any(SplitSelectionState::class.java), any(Application::class.java))
        verify(view).configureRenderMode(any())
        verify(view).configureAdapters(any(Site::class.java), any(Currency::class.java))
        verify(view).updatePaymentMethods(anyListOf(DrawableFragmentItem::class.java))
    }
}
