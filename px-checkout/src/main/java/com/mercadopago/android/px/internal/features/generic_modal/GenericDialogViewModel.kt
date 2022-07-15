package com.mercadopago.android.px.internal.features.generic_modal

import androidx.lifecycle.MutableLiveData
import com.mercadopago.android.px.internal.base.BaseViewModel
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.tracking.internal.events.GenericDialogActionEvent
import com.mercadopago.android.px.tracking.internal.events.GenericDialogDismissEvent
import com.mercadopago.android.px.tracking.internal.events.GenericDialogOpenEvent
import com.mercadopago.android.px.tracking.internal.model.GenericDialogTrackData

internal class GenericDialogViewModel(private val model: GenericDialogItem) : BaseViewModel(), ViewModel {

    val dialogState: MutableLiveData<GenericDialogState> = MutableLiveData()

    init {
        dialogState.value = GenericDialogState.LoadView(model)
    }

    fun trackLoadDialog() {
        GenericDialogOpenEvent(
            GenericDialogTrackData.Open(model.dialogDescription, model.hasSecondaryAction())).track()
    }

    private fun getGenericDialogAction(actionable: Actionable): GenericDialogAction {
        return if (actionable.deepLink.isNotNullNorEmpty()) {
            GenericDialogAction.DeepLinkAction(actionable.deepLink!!)
        } else {
            GenericDialogAction.CustomAction(actionable.action!!)
        }
    }

    override fun onButtonClicked(actionable: Actionable, isMain: Boolean) {
        GenericDialogActionEvent(
            GenericDialogTrackData.Action(
                actionable.deepLink.orEmpty(),
                if (isMain) GenericDialogTrackData.Type.MAIN_ACTION else GenericDialogTrackData.Type.SECONDARY_ACTION,
                model.dialogDescription,
                model.hasSecondaryAction())).track()
        dialogState.value = GenericDialogState.ButtonClicked(getGenericDialogAction(actionable))
    }

    override fun onDialogDismissed() {
        GenericDialogDismissEvent(
            GenericDialogTrackData.Dismiss(model.dialogDescription, model.secondaryAction != null)).track()
    }
}