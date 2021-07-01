package com.mercadopago.android.px.internal.adapters

import com.mercadopago.android.px.ITestService
import com.mercadopago.android.px.TestService
import com.mercadopago.android.px.internal.base.CoroutineContextProvider
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.mocks.CheckoutResponseStub
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.SocketTimeoutApiException
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
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

    @Test
    fun whenApiResponseIsNotSuccessfulAndHasNoErrorBodyThenItShouldReturnGenericApiFailure() {
        whenever(connectionHelper.hasConnection()).thenReturn(true)
        runBlocking {
            val result = networkApi.apiCallForResponse(ITestService::class.java) {
                Response.error<Any>(400, ResponseBody.create(null, ""))
            }
            assertTrue(result is ApiResponse.Failure)
        }
    }

    @Test
    fun whenApiResponseIsNotSuccessfulAndHasErrorBodyThenItShouldReturnMappedApiException() {
        val apiException = ApiException().also {
            it.message = "test"
            it.error = "error"
            it.status = 400
        }
        whenever(connectionHelper.hasConnection()).thenReturn(true)
        runBlocking {
            val result = networkApi.apiCallForResponse(ITestService::class.java) {
                Response.error<Any>(
                    400, ResponseBody.create(
                        null, "{ 'message':'${apiException.message}', " +
                            "'error':'${apiException.error}', " +
                            "'status':${apiException.status} }"
                    )
                )
            }
            assertTrue(result is ApiResponse.Failure)
            assertTrue(ReflectionEquals(apiException).matches((result as ApiResponse.Failure).exception))
        }
    }

    @Test
    fun whenApiResponseIsSocketTimeoutThenItShouldNotRetry() {
        whenever(connectionHelper.hasConnection()).thenReturn(true)
        val testServiceMock = mock<TestService>()
        runBlocking {
            whenever(retrofitClient.create(ITestService::class.java))
                .thenReturn(testServiceMock)
            whenever(testServiceMock.apiCall(ITestService.Responses.SOCKET_EX)).thenCallRealMethod()
            val result = networkApi.apiCallForResponse(ITestService::class.java) {
                it.apiCall(ITestService.Responses.SOCKET_EX)
            }
            assertTrue(result is ApiResponse.Failure)
            assertTrue((result as ApiResponse.Failure).exception is SocketTimeoutApiException)
        }
        runBlocking {
            verify(testServiceMock).apiCall(any())
        }
    }

    @Test
    fun whenApiResponseIsOtherExceptionThanSocketTimeoutThenItShouldRetry() {
        whenever(connectionHelper.hasConnection()).thenReturn(true)
        val testServiceMock = mock<TestService>()
        runBlocking {
            whenever(retrofitClient.create(ITestService::class.java))
                .thenReturn(testServiceMock)
            whenever(testServiceMock.apiCall(ITestService.Responses.GENERIC_EX)).thenCallRealMethod()
            val result = networkApi.apiCallForResponse(ITestService::class.java) {
                it.apiCall(ITestService.Responses.GENERIC_EX)
            }
            assertTrue(result is ApiResponse.Failure)
        }
        runBlocking {
            verify(testServiceMock, atLeast(2)).apiCall(any())
        }
    }

    @Test
    fun whenApiResponseIsFailureAndThenSuccessThenItShouldRetryFirstAndThenReturnSuccess() {
        whenever(connectionHelper.hasConnection()).thenReturn(true)
        var apiCallsCounter = 1
        val testServiceMock = mock<TestService>()
        runBlocking {
            whenever(retrofitClient.create(ITestService::class.java))
                .thenReturn(testServiceMock)
            whenever(testServiceMock.apiCall(any())).thenCallRealMethod()
            val result = networkApi.apiCallForResponse(ITestService::class.java) {
                if (apiCallsCounter == 1) {
                    apiCallsCounter++
                    it.apiCall(ITestService.Responses.GENERIC_EX)
                } else {
                    it.apiCall(ITestService.Responses.SUCCESS)
                }
            }
            assertTrue(result is ApiResponse.Success)
        }
        runBlocking {
            verify(testServiceMock, times(2)).apiCall(any())
        }
    }
}
