package com.eliascoelho911.cookzy.domain.util

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QuantityDerivationTest {

    @Test
    fun `returns integer quantity when present`() {
        val result = deriveQuantity("2 ovos")
        assertEquals(BigDecimal("2"), result?.value)
        assertEquals(0..0, result?.range)
    }

    @Test
    fun `supports decimal numbers with comma`() {
        val result = deriveQuantity("0,75 xícara de leite")
        assertEquals(BigDecimal("0.75"), result?.value)
        assertEquals(0..3, result?.range)
    }

    @Test
    fun `supports unicode fraction`() {
        val result = deriveQuantity("½ colher de sal")
        assertEquals(BigDecimal("0.5"), result?.value)
        assertEquals(0..0, result?.range)
    }

    @Test
    fun `supports mixed number with unicode fraction`() {
        val result = deriveQuantity("1 ¼ xícara de açúcar")
        assertEquals(BigDecimal("1.25"), result?.value)
        assertEquals(0..2, result?.range)
    }

    @Test
    fun `supports simple fraction with truncation`() {
        val result = deriveQuantity("2/3 xícara de açúcar")
        assertEquals(BigDecimal("0.666"), result?.value)
        assertEquals(0..2, result?.range)
    }

    @Test
    fun `ignores surrounding text`() {
        val result = deriveQuantity("aprox. 3 tomates")
        assertEquals(BigDecimal("3"), result?.value)
        assertEquals(7..7, result?.range)
    }

    @Test
    fun `returns null for range`() {
        assertNull(deriveQuantity("1-2 colheres de manteiga"))
    }

    @Test
    fun `returns null for multiplier expressions`() {
        assertNull(deriveQuantity("2 x 200 g de carne"))
    }

    @Test
    fun `returns null when no numeric value`() {
        assertNull(deriveQuantity("a gosto"))
    }

    @Test
    fun `returns null for dangling comma`() {
        assertNull(deriveQuantity("3,"))
    }
}
