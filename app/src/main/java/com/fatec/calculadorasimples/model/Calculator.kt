package com.fatec.calculadorasimples.model

/**
 * Objeto singleton que encapsula a lógica pura das operações aritméticas.
 *
 * Segue o princípio de responsabilidade única (SRP): nenhuma dependência
 * de Android ou UI — facilita testes unitários isolados.
 */
object Calculator {

    /**
     * Retorna a soma de [a] e [b].
     */
    fun add(a: Double, b: Double): Double = a + b

    /**
     * Retorna a diferença de [a] menos [b].
     */
    fun subtract(a: Double, b: Double): Double = a - b

    /**
     * Retorna o produto de [a] por [b].
     */
    fun multiply(a: Double, b: Double): Double = a * b

    /**
     * Retorna a divisão de [a] por [b].
     *
     * @throws ArithmeticException se [b] for zero.
     */
    fun divide(a: Double, b: Double): Double {
        if (b == 0.0) throw ArithmeticException("Divisão por zero")
        return a / b
    }
}
