package com.mercadopago.android.px.internal.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class CardHolderAuthenticatorBody(
    @SerializedName("userID") val userId: Long,
    val data: Data,
    val protocol: String = "3DS",
    @SerializedName("sessionID") val sessionId: String = "5e72317508813b0017cc482d",
    val deviceChannel: String = "APP",
    val version: String = "2.0") {

    data class Data(
        val purchaseAmount: String,
        val siteId: String,
        val purchaseDate: Date,
        val card: Card,
        val purchaseCurrency: String,
        val purchaseExponent: Int,
        @SerializedName("sdkAppID") val sdkAppId: String,
        val sdkEncData: String,
        val sdkEphemPubKey: SdkEphemPubKey,
        val sdkReferenceNumber: String,
        @SerializedName("sdkTransID") val sdkTransId: String,
        val sdkMaxTimeout: String = "06",
        val deviceRenderOptions: DeviceRenderOption = DeviceRenderOption(),
        @SerializedName("mechantCountryCode") val merchantCountryCode: String = "BR",
        val processingMode: String = "dataonly",
        @SerializedName("AuthTransaction") val authTransaction: String = "01",
        val profileId: String = "mobile_stp",
        val shipIndicator: String = "07",
        val riskRouting: String = "0"
    )

    data class Card(
        @SerializedName("cardholderName") val cardHolderName: String?,
        val paymentMethod: String?
    )

    data class SdkEphemPubKey(
        val kty: String,
        val crv: String,
        val x: String,
        val y: String)

    data class DeviceRenderOption(
        val sdkInterface: String = "01",
        val sdkUiType: List<String> = listOf("01", "02", "03")
    )
}