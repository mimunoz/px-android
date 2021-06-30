package com.mercadopago.android.px.internal.adapters

import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.mocks.CheckoutResponseStub
import com.mercadopago.android.px.model.exceptions.ApiException
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import retrofit2.Response
import retrofit2.Retrofit

@RunWith(MockitoJUnitRunner::class)
class NetworkApiTest {

    @Mock
    private lateinit var retrofitClient: Retrofit

    @Mock
    private lateinit var connectionHelper: ConnectionHelper

    @Mock
    private lateinit var networkApi: NetworkApi

    private var contextProvider: CoroutineContextProvider = CoroutineContextProvider()

    @Before
    fun setUp() {
        networkApi = NetworkApi(retrofitClient, connectionHelper, contextProvider)
    }

    @Test
    fun testApiCallForWithPreferenceIdResponseSuccess() {
        runBlocking {
            val checkoutResponse = CheckoutResponseStub.FULL.get()
            val apiResponse = ApiResponse.Success(checkoutResponse)

            whenever(connectionHelper.hasConnection()).thenReturn(true)
            val result = networkApi.apiCallForResponse(CheckoutService::class.java) {
                Response.success(checkoutResponse)
            }
            assertTrue(ReflectionEquals(apiResponse).matches(result))
        }
    }

    @Test
    fun testApiCallForWithPreferenceIdResponseFailure() {
        val apiExceptionMsg = "No connection"
        val apiException = ApiException().apply { message = apiExceptionMsg }

        runBlocking {
            val apiResponse = ApiResponse.Failure(apiException)

            whenever(connectionHelper.hasConnection()).thenReturn(false)
            val result = networkApi.apiCallForResponse(CheckoutService::class.java) {
                Response.success(apiResponse)
            } as ApiResponse.Failure

            assertTrue(ReflectionEquals(apiResponse.exception).matches(result.exception))
        }
    }
}
