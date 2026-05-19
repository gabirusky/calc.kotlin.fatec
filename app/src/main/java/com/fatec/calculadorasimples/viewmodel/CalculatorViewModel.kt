package com.fatec.calculadorasimples.viewmodel

import androidx.lifecycle.ViewModel
import com.fatec.calculadorasimples.model.CalculatorState
import com.fatec.calculadorasimples.model.formatDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel com acúmulo de expressão.
 *
 * A expressão "2+2+2+2" é armazenada em tokens e só avaliada ao pressionar "=".
 * Suporta precedência de operadores (* e / antes de + e -).
 */
class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorState())
    val uiState: StateFlow<CalculatorState> = _uiState.asStateFlow()

    fun onButtonClick(label: String) {
        when (label) {
            "C"                    -> onClear()
            "="                    -> onEquals()
            "."                    -> onDecimalPoint()
            "±"                    -> onToggleSign()
            "%"                    -> onPercent()
            "+", "-", "*", "/"     -> onOperator(label)
            else                   -> onDigit(label)
        }
    }

    private fun onDigit(digit: String) {
        _uiState.update { s ->
            when {
                s.isError -> CalculatorState(currentInput = digit)
                s.isResult -> CalculatorState(currentInput = digit)
                s.waitingForOperand -> s.copy(currentInput = digit, waitingForOperand = false)
                s.currentInput == "0" -> s.copy(currentInput = digit)
                s.currentInput.replace("-", "").length >= 12 -> s
                else -> s.copy(currentInput = s.currentInput + digit)
            }
        }
    }

    private fun onDecimalPoint() {
        _uiState.update { s ->
            when {
                s.isError -> s
                s.waitingForOperand || s.isResult -> s.copy(
                    currentInput = "0.",
                    tokens = if (s.isResult) emptyList() else s.tokens,
                    resultExpression = "",
                    waitingForOperand = false,
                    isResult = false
                )
                "." in s.currentInput -> s
                else -> s.copy(currentInput = s.currentInput + ".")
            }
        }
    }

    /**
     * Operador: acumula currentInput + operador nos tokens.
     * - Troca operador se pressionado duas vezes seguidas.
     * - Após resultado: usa o resultado como primeiro token da nova expressão.
     */
    private fun onOperator(op: String) {
        _uiState.update { s ->
            if (s.isError) return@update s

            when {
                // Troca de operador (ex: "5 +" → "5 −")
                s.waitingForOperand && s.tokens.isNotEmpty() -> {
                    val newTokens = s.tokens.toMutableList()
                    newTokens[newTokens.lastIndex] = op
                    s.copy(tokens = newTokens)
                }
                // Após resultado: inicia nova expressão com o resultado
                s.isResult -> s.copy(
                    tokens = listOf(s.currentInput, op),
                    resultExpression = "",
                    waitingForOperand = true,
                    isResult = false
                )
                // Normal: acumula número + operador
                else -> s.copy(
                    tokens = s.tokens + s.currentInput + op,
                    waitingForOperand = true,
                    resultExpression = ""
                )
            }
        }
    }

    /**
     * Avalia a expressão completa acumulada nos tokens + currentInput.
     * Respeita precedência: * e / antes de + e -.
     */
    private fun onEquals() {
        _uiState.update { s ->
            if (s.isError) return@update s
            if (s.tokens.isEmpty()) return@update s

            val fullTokens = if (s.waitingForOperand) {
                s.tokens + s.currentInput  // "5 + =" → "5 + 5"
            } else {
                s.tokens + s.currentInput
            }

            // Monta expressão de exibição: "2 + 3 × 4 ="
            val displayExpr = fullTokens.joinToString(" ") { t ->
                when (t) { "*" -> "×"; "/" -> "÷"; "-" -> "−"; else -> t }
            } + " ="

            val result = evaluate(fullTokens)

            if (result != null) {
                s.copy(
                    currentInput = result.formatDisplay(),
                    tokens = emptyList(),
                    resultExpression = displayExpr,
                    waitingForOperand = false,
                    isResult = true,
                    isError = false
                )
            } else {
                s.copy(
                    currentInput = "Erro",
                    tokens = emptyList(),
                    resultExpression = "",
                    waitingForOperand = false,
                    isResult = false,
                    isError = true
                )
            }
        }
    }

    private fun onToggleSign() {
        _uiState.update { s ->
            if (s.isError) return@update s
            val v = s.currentInput.toDoubleOrNull() ?: return@update s
            if (v == 0.0) return@update s
            s.copy(currentInput = (-v).formatDisplay(), isResult = false)
        }
    }

    private fun onPercent() {
        _uiState.update { s ->
            if (s.isError) return@update s
            val v = s.currentInput.toDoubleOrNull() ?: return@update s
            s.copy(currentInput = (v / 100.0).formatDisplay(), waitingForOperand = false, isResult = false)
        }
    }

    private fun onClear() {
        _uiState.value = CalculatorState()
    }

    // ─────────────────────────────────────────────
    // Avaliador de expressão com precedência
    // ─────────────────────────────────────────────

    /**
     * Avalia tokens ["2", "+", "3", "*", "4"] → 14.
     * Passo 1: resolve * e /. Passo 2: resolve + e -.
     */
    private fun evaluate(parts: List<String>): Double? {
        if (parts.isEmpty() || parts.size % 2 == 0) return null

        val nums = mutableListOf<Double>()
        val ops = mutableListOf<String>()

        for (i in parts.indices) {
            if (i % 2 == 0) {
                nums.add(parts[i].toDoubleOrNull() ?: return null)
            } else {
                ops.add(parts[i])
            }
        }

        // Passo 1: * e /
        var i = 0
        while (i < ops.size) {
            when (ops[i]) {
                "*" -> {
                    nums[i] = nums[i] * nums[i + 1]
                    nums.removeAt(i + 1); ops.removeAt(i)
                }
                "/" -> {
                    if (nums[i + 1] == 0.0) return null
                    nums[i] = nums[i] / nums[i + 1]
                    nums.removeAt(i + 1); ops.removeAt(i)
                }
                else -> i++
            }
        }

        // Passo 2: + e -
        var result = nums[0]
        for (j in ops.indices) {
            result = when (ops[j]) {
                "+" -> result + nums[j + 1]
                "-" -> result - nums[j + 1]
                else -> return null
            }
        }

        return result
    }
}
