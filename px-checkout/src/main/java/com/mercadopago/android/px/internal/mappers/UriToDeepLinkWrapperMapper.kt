package com.mercadopago.android.px.internal.mappers

import android.net.Uri
import com.mercadopago.android.px.internal.callbacks.DeepLinkWrapper
import com.mercadopago.android.px.internal.callbacks.DefaultWrapper
import com.mercadopago.android.px.internal.callbacks.From
import com.mercadopago.android.px.internal.callbacks.TokenizationResponseWrapper
import com.mercadopago.android.px.internal.di.MapperProvider

internal class UriToDeepLinkWrapperMapper : Mapper<Uri, DeepLinkWrapper>() {
    override fun map(value: Uri): DeepLinkWrapper =
        when (MapperProvider.uriToFromMapper.map(value)) {
            From.TOKENIZATION -> TokenizationResponseWrapper()
            From.NONE -> DefaultWrapper()
        }
}
