package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.internal.util.JsonUtil.fromJson
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TokenizationResponseTest : BasicRobolectricTest() {

    @Test
    fun whenReceiveIntentWithDataThenReturnStateSuccessWithMessage() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=tokenization&response={\"result\":\"success\",\"message\":\"successful tokenization\"}")
        val response: String = uri.getQueryParameter("response")!!
        val tokenizationResponse: TokenizationResponse = fromJson(response, TokenizationResponse::class.java)!!

        assertEquals(TokenizationResponse.State.SUCCESS, tokenizationResponse.result)
        assertEquals("successful tokenization", tokenizationResponse.message)
    }

    @Test
    fun whenReceiveIntentWithDataThenReturnStatePendingWithMessage() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=tokenization&response={\"result\":\"pending\",\"message\":\"pending tokenization\"}")
        val response: String = uri.getQueryParameter("response")!!
        val tokenizationResponse: TokenizationResponse = fromJson(response, TokenizationResponse::class.java)!!

        assertEquals(TokenizationResponse.State.PENDING, tokenizationResponse.result)
        assertEquals("pending tokenization", tokenizationResponse.message)
    }

    @Test
    fun whenReceiveIntentWithDataThenReturnStateErrorWithMessage() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=tokenization&response={\"result\":\"error\",\"message\":\"error tokenization\"}")
        val response: String = uri.getQueryParameter("response")!!
        val tokenizationResponse: TokenizationResponse = fromJson(response, TokenizationResponse::class.java)!!

        assertEquals(TokenizationResponse.State.ERROR, tokenizationResponse.result)
        assertEquals("error tokenization", tokenizationResponse.message)
    }

    @Test
    fun whenReceiveIntentWithDataThenReturnFromTokenization() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=tokenization&response={\"result\":\"error\",\"message\":\"error tokenization\"}")
        val from: String = uri.getQueryParameter("from")!!

        assertEquals(From.TOKENIZATION.value, from)
    }

    @Test
    fun whenReceiveIntentWithDataThenReturnFromDefault() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=none&response={\"result\":\"error\",\"message\":\"error tokenization\"}")
        val from: String = uri.getQueryParameter("from")!!

        assertEquals(From.NONE.value, from)
    }
}
