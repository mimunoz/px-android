package com.mercadopago.android.px.internal.features.generic_modal

import androidx.annotation.StringDef

sealed class GenericDialogAction {
    class DeepLinkAction(val deepLink: String) : GenericDialogAction()
    class CustomAction(@ActionType val type: String) : GenericDialogAction()
}

@Retention(AnnotationRetention.SOURCE)
@StringDef(ActionType.PAY_WITH_OTHER_METHOD, ActionType.PAY_WITH_OFFLINE_METHOD, ActionType.ADD_NEW_CARD)
annotation class ActionType {
    companion object {
        const val PAY_WITH_OTHER_METHOD = "pay_with_other_method"
        const val PAY_WITH_OFFLINE_METHOD = "pay_with_offline_method"
        const val ADD_NEW_CARD = "add_new_card"
    }
}
