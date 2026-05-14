package com.fatec.calculadorasimples.model

/**
 * Representa o estado imutável da UI da calculadora em um dado instante.
 *
 * @property currentInput   O número sendo digitado no momento (ex: "123" ou "3.14").
 * @property operator       O operador selecionado ("+", "-", "*", "/") ou vazio se nenhum.
 * @property firstOperand   O primeiro operando armazenado antes do operador ser pressionado.
 * @property isResult       Indica se o visor está mostrando um resultado final (após "=").
 * @property isError        Indica se ocorreu um erro (ex: divisão por zero).
 */
data class CalculatorState(
    val currentInput: String = "0",
    val operator: String = "",
    val firstOperand: Double = 0.0,
    val isResult: Boolean = false,
    val isError: Boolean = false
) {
    /**
     * Retorna a expressão acumulada exibida acima do visor principal.
     * Exemplo: "8 ×", "12.5 +"
     */
    val expression: String
        get() = if (operator.isNotEmpty()) {
            val displayOp = when (operator) {
                "*" -> "×"
                "/" -> "÷"
                "-" -> "−"
                else -> operator
            }
            "${firstOperand.formatDisplay()} $displayOp"
        } else ""
}

/**
 * Formata um Double para exibição: remove ".0" de números inteiros.
 * Exemplo: 10.0 → "10", 3.14 → "3.14"
 */
fun Double.formatDisplay(): String =
    if (this == toLong().toDouble()) toLong().toString() else toString()
