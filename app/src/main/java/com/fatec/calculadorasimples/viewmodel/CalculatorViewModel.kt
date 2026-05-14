package com.fatec.calculadorasimples.viewmodel

import androidx.lifecycle.ViewModel
import com.fatec.calculadorasimples.model.Calculator
import com.fatec.calculadorasimples.model.CalculatorState
import com.fatec.calculadorasimples.model.formatDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel da Calculadora — camada intermediária entre a UI e a lógica de negócio.
 *
 * Responsabilidades:
 * - Manter o estado da UI via [StateFlow] (padrão recomendado pelo Google).
 * - Processar eventos do usuário (clique nos botões).
 * - Delegar operações matemáticas ao [Calculator] (Model).
 *
 * A UI (Compose) observa [uiState] e recompõe apenas quando o estado muda.
 */
class CalculatorViewModel : ViewModel() {

    // Estado interno mutável — privado para garantir encapsulamento
    private val _uiState = MutableStateFlow(CalculatorState())

    /**
     * Estado público imutável observado pela UI.
     * Uso na Composable: `val state by viewModel.uiState.collectAsStateWithLifecycle()`
     */
    val uiState: StateFlow<CalculatorState> = _uiState.asStateFlow()

    // ─────────────────────────────────────────────
    // Dispatcher central — ponto de entrada único da UI
    // ─────────────────────────────────────────────

    /**
     * Processa o clique de qualquer botão da calculadora.
     *
     * @param label O texto do botão pressionado (ex: "7", "+", "=", "C", ".")
     */
    fun onButtonClick(label: String) {
        when (label) {
            "C"             -> onClear()
            "="             -> onEquals()
            "."             -> onDecimalPoint()
            "+", "-", "*", "/" -> onOperator(label)
            else            -> onDigit(label)   // dígitos 0–9
        }
    }

    // ─────────────────────────────────────────────
    // Handlers de cada tipo de entrada
    // ─────────────────────────────────────────────

    /**
     * Acumula um dígito no número sendo digitado.
     * Se o visor está mostrando um resultado, começa um novo número.
     */
    private fun onDigit(digit: String) {
        _uiState.update { state ->
            val newInput = when {
                // Após resultado ou erro, começa do zero
                state.isResult || state.isError -> digit
                // Evita múltiplos zeros à esquerda
                state.currentInput == "0"       -> digit
                // Limite de 12 dígitos para caber no visor
                state.currentInput.length >= 12 -> state.currentInput
                else                             -> state.currentInput + digit
            }
            state.copy(currentInput = newInput, isResult = false, isError = false)
        }
    }

    /**
     * Adiciona ponto decimal ao número atual, se ainda não existir.
     */
    private fun onDecimalPoint() {
        _uiState.update { state ->
            if ("." in state.currentInput) state   // já tem ponto, ignora
            else state.copy(
                currentInput = if (state.isResult) "0." else "${state.currentInput}.",
                isResult = false
            )
        }
    }

    /**
     * Registra o operador selecionado pelo usuário.
     * Se já havia um operador pendente, calcula o resultado intermediário antes.
     */
    private fun onOperator(op: String) {
        _uiState.update { state ->
            val currentValue = state.currentInput.toDoubleOrNull() ?: 0.0

            // Encadeamento: "5 + 3 *" calcula "5 + 3 = 8" antes de armazenar "*"
            if (state.operator.isNotEmpty() && !state.isResult) {
                val intermediate = calculate(state.firstOperand, currentValue, state.operator)
                state.copy(
                    firstOperand = intermediate ?: state.firstOperand,
                    operator = op,
                    currentInput = (intermediate ?: currentValue).formatDisplay(),
                    isResult = false,
                    isError = intermediate == null
                )
            } else {
                state.copy(
                    firstOperand = if (state.isResult) currentValue else currentValue,
                    operator = op,
                    currentInput = "0",
                    isResult = false
                )
            }
        }
    }

    /**
     * Calcula o resultado da operação acumulada ao pressionar "=".
     */
    private fun onEquals() {
        _uiState.update { state ->
            if (state.operator.isEmpty()) return@update state

            val secondOperand = state.currentInput.toDoubleOrNull() ?: 0.0
            val result = calculate(state.firstOperand, secondOperand, state.operator)

            if (result != null) {
                state.copy(
                    currentInput = result.formatDisplay(),
                    operator = "",
                    firstOperand = result,
                    isResult = true,
                    isError = false
                )
            } else {
                state.copy(
                    currentInput = "Erro",
                    operator = "",
                    isResult = false,
                    isError = true
                )
            }
        }
    }

    /**
     * Reinicia o estado para o valor inicial (zera tudo).
     */
    private fun onClear() {
        _uiState.value = CalculatorState()
    }

    // ─────────────────────────────────────────────
    // Utilitários privados
    // ─────────────────────────────────────────────

    /**
     * Executa o cálculo usando o [Calculator] (Model).
     *
     * @return O resultado como [Double], ou `null` se ocorrer ArithmeticException.
     */
    private fun calculate(a: Double, b: Double, op: String): Double? {
        return try {
            when (op) {
                "+" -> Calculator.add(a, b)
                "-" -> Calculator.subtract(a, b)
                "*" -> Calculator.multiply(a, b)
                "/" -> Calculator.divide(a, b)
                else -> b
            }
        } catch (e: ArithmeticException) {
            null   // null indica erro (divisão por zero)
        }
    }
}
