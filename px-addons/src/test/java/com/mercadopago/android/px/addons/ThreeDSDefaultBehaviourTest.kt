package com.mercadopago.android.px.addons

import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ThreeDSDefaultBehaviourTest {

    @Test
    fun testThreeDSDefaultBehaviour() {
        assertNull(BehaviourProvider.getThreeDSBehaviour().getAuthenticationParameters())
    }
}