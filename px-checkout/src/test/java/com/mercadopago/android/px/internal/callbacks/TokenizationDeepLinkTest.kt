package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.internal.util.JsonUtil.fromJson
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TokenizationDeepLinkTest : BasicRobolectricTest() {

    @Test
    fun whenReceiveIntentWithDataThenReturnStateSuccess() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=tokenization\"&response={\"result\":\"success\"}")
        val response: String = uri.getQueryParameter("response")!!
        val tokenizationResponse: TokenizationResponse = fromJson(response, TokenizationResponse::class.java)!!

        assertEquals(TokenizationResponse.State.SUCCESS, tokenizationResponse.result)
    }

    @Test
    fun whenReceiveIntentWithDataThenReturnStatePending() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=tokenization\"&response={\"result\":\"pending\"}")
        val response: String = uri.getQueryParameter("response")!!
        val tokenizationResponse: TokenizationResponse = fromJson(response, TokenizationResponse::class.java)!!

        assertEquals(TokenizationResponse.State.PENDING, tokenizationResponse.result)
    }

    @Test
    fun whenReceiveIntentWithDataThenReturnStateError() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=tokenization\"&response={\"result\":\"error\"}")
        val response: String = uri.getQueryParameter("response")!!
        val tokenizationResponse: TokenizationResponse = fromJson(response, TokenizationResponse::class.java)!!

        assertEquals(TokenizationResponse.State.ERROR, tokenizationResponse.result)
    }
}
