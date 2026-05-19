package com.fatec.calculadorasimples.model

/**
 * Estado da calculadora com acúmulo de expressão.
 *
 * A expressão completa (ex: "2 + 3 × 4") é construída via [tokens]
 * e só é avaliada quando o usuário pressiona "=".
 */
data class CalculatorState(
    val currentInput: String = "0",
    val tokens: List<String> = emptyList(),
    val resultExpression: String = "",
    val waitingForOperand: Boolean = false,
    val isResult: Boolean = false,
    val isError: Boolean = false
) {
    val expression: String
        get() = resultExpression.ifEmpty { formatTokens() }

    private fun formatTokens(): String {
        if (tokens.isEmpty()) return ""
        return tokens.joinToString(" ") { t ->
            when (t) { "*" -> "×"; "/" -> "÷"; "-" -> "−"; else -> t }
        }
    }
}

fun Double.formatDisplay(): String {
    if (this == toLong().toDouble() && !isInfinite() && !isNaN()) return toLong().toString()
    return "%.10f".format(this).trimEnd('0').trimEnd('.')
}
