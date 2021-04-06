package com.mercadopago.android.px.addons.validator.internal

import com.mercadopago.android.px.addons.ESCManagerBehaviour
import com.mercadopago.android.px.addons.model.EscValidationData
import com.mercadopago.android.px.addons.model.SecurityValidationData
import com.mercadopago.android.px.addons.validator.SecurityValidator
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class SecurityValidatorTest {

    @Mock
    private lateinit var escManagerBehaviour: ESCManagerBehaviour
    private lateinit var securityValidator: SecurityValidator

    @Before
    fun setUp() {
        securityValidator = SecurityValidator(escManagerBehaviour)
    }

    @Test
    fun validationWhenEscValidationDataIsNull() {
        val securityValidationData = mock<SecurityValidationData> {  }
        Assert.assertTrue(securityValidator.validate(securityValidationData))
    }

    @Test
    fun validationWhenEscValidationDataIsNotNull() {
        val escValidationData: EscValidationData = mock {
            on { isCard }.thenReturn(false)
            on { isEscEnable }.thenReturn(true)
            on { cardId }.thenReturn("123456")
        }
        val securityValidationData = mock<SecurityValidationData> {
            on { getEscValidationData() }.thenReturn(escValidationData)
        }

        whenever(escManagerBehaviour.getESC("123456", null, null)).thenReturn("987654")
        Assert.assertTrue(securityValidator.validate(securityValidationData))
    }
}