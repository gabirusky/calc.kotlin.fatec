package com.fatec.calculadorasimples

import com.fatec.calculadorasimples.model.Calculator
import com.fatec.calculadorasimples.model.formatDisplay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * Testes unitários da camada Model — [Calculator] e [formatDisplay].
 *
 * Executar com: `./gradlew test`
 *
 * Não requerem emulador ou dispositivo (testes JVM puros).
 */
class CalculatorTest {

    // ─── Adição ───────────────────────────────────────────────────────────

    @Test
    fun `add dois positivos retorna soma correta`() {
        assertEquals(5.0, Calculator.add(2.0, 3.0), 0.0001)
    }

    @Test
    fun `add com negativo retorna valor correto`() {
        assertEquals(-1.0, Calculator.add(-4.0, 3.0), 0.0001)
    }

    // ─── Subtração ────────────────────────────────────────────────────────

    @Test
    fun `subtract retorna diferenca correta`() {
        assertEquals(6.0, Calculator.subtract(10.0, 4.0), 0.0001)
    }

    @Test
    fun `subtract resultado negativo`() {
        assertEquals(-3.0, Calculator.subtract(2.0, 5.0), 0.0001)
    }

    // ─── Multiplicação ────────────────────────────────────────────────────

    @Test
    fun `multiply retorna produto correto`() {
        assertEquals(12.0, Calculator.multiply(3.0, 4.0), 0.0001)
    }

    @Test
    fun `multiply por zero retorna zero`() {
        assertEquals(0.0, Calculator.multiply(99.0, 0.0), 0.0001)
    }

    // ─── Divisão ─────────────────────────────────────────────────────────

    @Test
    fun `divide retorna quociente correto`() {
        assertEquals(5.0, Calculator.divide(10.0, 2.0), 0.0001)
    }

    @Test
    fun `divide por zero lanca ArithmeticException`() {
        assertThrows(ArithmeticException::class.java) {
            Calculator.divide(10.0, 0.0)
        }
    }

    @Test
    fun `divide resultado decimal`() {
        assertEquals(0.5, Calculator.divide(1.0, 2.0), 0.0001)
    }

    // ─── Formatação de Display ────────────────────────────────────────────

    @Test
    fun `formatDisplay remove ponto zero de inteiro`() {
        assertEquals("10", 10.0.formatDisplay())
    }

    @Test
    fun `formatDisplay mantém decimal significativo`() {
        assertEquals("3.14", 3.14.formatDisplay())
    }

    @Test
    fun `formatDisplay zero`() {
        assertEquals("0", 0.0.formatDisplay())
    }

    @Test
    fun `formatDisplay negativo inteiro`() {
        assertEquals("-5", (-5.0).formatDisplay())
    }
}
