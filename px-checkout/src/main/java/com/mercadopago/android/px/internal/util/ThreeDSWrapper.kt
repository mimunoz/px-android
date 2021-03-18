package com.mercadopago.android.px.internal.util

import android.util.Log
import com.mercadopago.android.px.internal.di.Session
import com.nds.nudetect.EMVAuthenticationRequestParameters
import com.nds.threeds.core.*

object ThreeDSWrapper {

    lateinit var threeDSSDK: ThreeDSSDK
    lateinit var threeDS2Service: EMVThreeDS2Service

    fun initialize() {
        val configParameters = EMVConfigParameters()
        configParameters.addDirectoryServer(EMVDirectoryServer(
            "A000000044",
            "{\"kty\":\"RSA\",\"e\":\"AQAB\",\"kid\":\"dc7abe14-2c18-477d-8232-c51173ef45c1\",\"n\":\"2EPLWFIep-_pa0mXKEcljP9tSwTLC0yXf4NayVjh9-FmmOjN84Y2j0Hx8eSNfxCyqbu4pewiVZYWsZz95XzxASh16NGq7qOlYVBizUg4q3ONaomfzaeaUGp9usSh2VaSYjdcU0-2fhB2TWpyPOZ3_r9-895X91BJz2fY4mbJI1RVaTxVJI-ueSLHA13-GT90biIigy00fjT0WTDouSMJP-tafVYpeLzGClhlUSflfOZkVv9Uw2J6MgHPDyIj19RHLGGxaa46EZ_hMONlPh-mi3bB_0fecMTNXopzzxWwG_qGvCPpV08Iim2AvNDhWLrZ9D2t4Vsckrej3PBIHpnZmw\"}",
            "-----BEGIN CERTIFICATE-----\n" +
                "MIIFzjCCA7agAwIBAgIRAO1jVXkQsXKwwm83tAnIHVMwDQYJKoZIhvcNAQELBQAw\n" +
                "fzELMAkGA1UEBhMCVVMxEzARBgNVBAoTCk1hc3RlckNhcmQxKDAmBgNVBAsTH01h\n" +
                "c3RlckNhcmQgSWRlbnRpdHkgQ2hlY2sgR2VuIDMxMTAvBgNVBAMTKFZhbEZhYyBN\n" +
                "YXN0ZXJDYXJkIElkZW50aXR5IENoZWNrIFJvb3QgQ0EwHhcNMTYwNzE0MDcxMjAw\n" +
                "WhcNMzAwNzE1MDgxMDAwWjB/MQswCQYDVQQGEwJVUzETMBEGA1UEChMKTWFzdGVy\n" +
                "Q2FyZDEoMCYGA1UECxMfTWFzdGVyQ2FyZCBJZGVudGl0eSBDaGVjayBHZW4gMzEx\n" +
                "MC8GA1UEAxMoVmFsRmFjIE1hc3RlckNhcmQgSWRlbnRpdHkgQ2hlY2sgUm9vdCBD\n" +
                "QTCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAMHjJpH1vy2MgK7CWy1b\n" +
                "1RbDIKnqKiz/38g9pUbvcjrg3S2kc7D7IjuwZSlzeDg+lqM4P4iWdFL5viNNbUOm\n" +
                "ABlxLZ+EZyaMMnq8usWHbMAd4mQY/KKsEl9o1tK4FJlOPi5qHIS9PUmmkgRsbJHg\n" +
                "skS4pIDZtuTJ1xOVBv+0gX/fKi+pu4PAx9GZSwnMEpf1DTqoA3OO6yBA5ROrxMD6\n" +
                "HnjpLANs7kke56qdgKytfhfdCQuvF+FCGgbqZis422vihBiJkD51AGOuxUCe21rj\n" +
                "FaXxsJpFSXoJ91eGeHG5gqKtBuNUPDCm+ShyHQN89jfRjMsNNH2xShmEPnS+Oitr\n" +
                "RIUiJykYEbAZvx++zac8rJYzTxTAJS6VxLhG/UsMgls2JAbHng4kGGlubRKsExUl\n" +
                "4eXZrgjbgEQlhCdUBnt8gz73jaz2L4G0j3t2RQENkxTjuLCObOWRTxUQTYGyn64z\n" +
                "BhdQCmSC49it17ECUsNBY3+Yso7dnCjN/4TIw42+iX/FBocfcn9lUgYPlwgGXrU2\n" +
                "+PJ76D+4FFbPCepiGWJnzMNjbsPFgSUqUQxC7bj77c3Wea65kz3Y7SDoPaFsfHBR\n" +
                "DU30AxbBEwk4q3KZtzLV5sBJ/z6duXxq01ckHy0UiGBr2zY+ScNippbdCELipanM\n" +
                "10b34u7tfDlYpHLTnuT61LajAgMBAAGjRTBDMA4GA1UdDwEB/wQEAwIBhjASBgNV\n" +
                "HRMBAf8ECDAGAQH/AgEBMB0GA1UdDgQWBBSnEPJoAOCMunB5/OW44ISGR8YK6TAN\n" +
                "BgkqhkiG9w0BAQsFAAOCAgEAl4/YrKfsaUbNFrnSbMQlEwWUuESbfL+uv/eCwAoP\n" +
                "dE5FbFZtkfX+BWPA5BEc7pOTUFBJiWwzs+ngLz5k97iyAYYOzQr27yj5A8aOj6Na\n" +
                "0239T9NMI2t/rEARTfMP6fPzOpimgofTbLuiNtkCiwJfs9A6N0SvLxe+r++aYYfP\n" +
                "x0/pmQnWIF7Uliskr8jTXiaIK19XTkFmsF4gZHuKBxUrtacTfSqClLJAuXl6avNV\n" +
                "vDuaH4ebcNx7XZwHuWz8JOwDNaT/HD45Ku1FqohtUvF7o/PV/goaKAqiUChavqoq\n" +
                "JgYQ15UED7lNgLcT5ZpchCTFlUSflZnAbDiopU1NQxQPPdbWWKY1Q9SbGEOIyfoV\n" +
                "Xi8OcmEGPRz49vaGSq9JPX9aDp8g1PpJLjK+uwHZ/QZO4CZETSwXvxVvE0eI/gnh\n" +
                "UAe4zC0SQmaRwMIVN0Ivf5fvSQsPQMbKHxqCWb0FWi6GCiZryR6oJ8z7OEuq1yxg\n" +
                "3fas0NxK3GvnSOH3xpzoTuUijQ0Xu1pQqmGkXINmcKAbX/CxEssdgEx9EasfFifb\n" +
                "izJpnTnSu05bRBAP5RV5l+GVuxe01w8uYtDIKmKXsMtzZ88tV5GIb0s/THzLvDr6\n" +
                "E/khD+pVNsME4VurwW1Rgk831sXS+TRUQIxvA0qH3IyasIjPmtyXyGStiJ58gh8o\n" +
                "+Uw=\n" +
                "-----END CERTIFICATE-----",
            "be8c1e689514a8033ed3d959f005c9f9885dfc37")
        )

        threeDSSDK = ThreeDSSDK.Builder().configParameters(configParameters).build(Session.getInstance().applicationContext)
        threeDSSDK.initialize(object : ThreeDSInitializationCallback {
            override fun error(e: Exception?) {
                // do nothing
                Log.v("CRIS 4", e.toString())
            }

            override fun success() {
                threeDS2Service = threeDSSDK.threeDS2Service()
            }
        })
    }

    fun getAuthenticationParameters(): EMVAuthenticationRequestParameters {
        val transaction = threeDS2Service.createTransaction("A000000044", "2.1.0")
        val authenticationRequestParameters = transaction.authenticationRequestParameters
        Log.v("CRIS 3", authenticationRequestParameters.deviceData)
        return authenticationRequestParameters
    }
}