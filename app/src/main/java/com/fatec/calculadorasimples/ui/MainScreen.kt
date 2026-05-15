package com.fatec.calculadorasimples.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fatec.calculadorasimples.model.CalculatorState
import com.fatec.calculadorasimples.ui.theme.CalcBackground
import com.fatec.calculadorasimples.ui.theme.CalcDisplayBg
import com.fatec.calculadorasimples.ui.theme.CalcDisplaySubText
import com.fatec.calculadorasimples.ui.theme.CalcDisplayText
import com.fatec.calculadorasimples.ui.theme.CalcEqualsBtn
import com.fatec.calculadorasimples.ui.theme.CalcEqualsText
import com.fatec.calculadorasimples.ui.theme.CalcFuncBtn
import com.fatec.calculadorasimples.ui.theme.CalcFuncText
import com.fatec.calculadorasimples.ui.theme.CalcNumericBtn
import com.fatec.calculadorasimples.ui.theme.CalcNumericText
import com.fatec.calculadorasimples.ui.theme.CalcOperatorBtn
import com.fatec.calculadorasimples.ui.theme.CalcOperatorText
import com.fatec.calculadorasimples.ui.theme.CalculadorafatecTheme
import com.fatec.calculadorasimples.viewmodel.CalculatorViewModel

// ─────────────────────────────────────────────────────────────────────────────
// Definição do layout de botões
// Cada linha contém: label exibido, label enviado ao ViewModel, tipo de botão
// ─────────────────────────────────────────────────────────────────────────────
private enum class BtnType { FUNC, NUMERIC, OPERATOR, EQUALS }

private data class ButtonDef(
    val display: String,  // Texto exibido no botão
    val action: String,   // Label enviado ao ViewModel
    val type: BtnType,
    val weight: Float = 1f  // Peso de largura (0 wide para "0")
)

private val buttonRows = listOf(
    listOf(
        ButtonDef("C",   "C",   BtnType.FUNC),
        ButtonDef("±",   "±",   BtnType.FUNC),
        ButtonDef("%",   "%",   BtnType.FUNC),
        ButtonDef("÷",   "/",   BtnType.OPERATOR)
    ),
    listOf(
        ButtonDef("7",   "7",   BtnType.NUMERIC),
        ButtonDef("8",   "8",   BtnType.NUMERIC),
        ButtonDef("9",   "9",   BtnType.NUMERIC),
        ButtonDef("×",   "*",   BtnType.OPERATOR)
    ),
    listOf(
        ButtonDef("4",   "4",   BtnType.NUMERIC),
        ButtonDef("5",   "5",   BtnType.NUMERIC),
        ButtonDef("6",   "6",   BtnType.NUMERIC),
        ButtonDef("−",   "-",   BtnType.OPERATOR)
    ),
    listOf(
        ButtonDef("1",   "1",   BtnType.NUMERIC),
        ButtonDef("2",   "2",   BtnType.NUMERIC),
        ButtonDef("3",   "3",   BtnType.NUMERIC),
        ButtonDef("+",   "+",   BtnType.OPERATOR)
    ),
    listOf(
        ButtonDef("0",   "0",   BtnType.NUMERIC, weight = 2f),  // Botão duplo
        ButtonDef(".",   ".",   BtnType.NUMERIC),
        ButtonDef("=",   "=",   BtnType.EQUALS)
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// Tela principal (stateful)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Tela principal da Calculadora.
 *
 * Padrão MVVM: coleta o estado do [viewModel] e repassa para [CalculatorContent]
 * (stateless) junto com o callback de eventos.
 */
@Composable
fun MainScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CalculatorContent(
        state   = uiState,
        onEvent = viewModel::onButtonClick
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Conteúdo da tela (stateless — facilita preview e testes)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Composable stateless que renderiza o layout completo da calculadora.
 *
 * @param state   Estado atual da calculadora.
 * @param onEvent Callback chamado ao pressionar qualquer botão.
 */
@Composable
fun CalculatorContent(
    state: CalculatorState,
    onEvent: (String) -> Unit
) {
    Scaffold(
        containerColor = CalcBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CalcBackground),
            verticalArrangement = Arrangement.Bottom
        ) {
            // ── Visor ────────────────────────────────────────────────────
            DisplaySection(
                expression   = state.expression,
                currentInput = state.currentInput,
                isError      = state.isError
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Grid de botões ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (row in buttonRows) {
                    Row(
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (btn in row) {
                            val (bg, fg) = btn.buttonColors()
                            CalculatorButton(
                                label           = btn.display,
                                backgroundColor = bg,
                                textColor       = fg,
                                modifier        = Modifier.weight(btn.weight),
                                isWide          = btn.weight > 1f,
                                onClick         = { onEvent(btn.action) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Visor
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Seção de visor: mostra a expressão acumulada (pequena) e o número atual (grande).
 */
@Composable
private fun DisplaySection(
    expression: String,
    currentInput: String,
    isError: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CalcDisplayBg)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier          = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            // Linha de expressão acumulada (ex: "12 ×")
            Text(
                text     = expression,
                style    = MaterialTheme.typography.displayMedium,
                color    = CalcDisplaySubText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Número / resultado principal
            Text(
                text      = if (isError) "Erro" else currentInput,
                style     = MaterialTheme.typography.displayLarge,
                color     = if (isError) Color(0xFFFF453A) else CalcDisplayText,
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier  = Modifier.fillMaxWidth()
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Botão da calculadora
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Botão circular reutilizável com animação de escala ao pressionar.
 *
 * @param label           Texto exibido.
 * @param backgroundColor Cor de fundo do botão.
 * @param textColor       Cor do texto.
 * @param modifier        Modifier externo (usado para `weight`).
 * @param isWide          Se verdadeiro, o botão ocupa largura dupla (botão "0").
 * @param onClick         Callback de clique.
 */
@Composable
fun CalculatorButton(
    label: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    isWide: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Animação de escala suave ao pressionar
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "buttonScale"
    )

    val buttonShape = if (isWide) RoundedCornerShape(50) else CircleShape

    Box(
        modifier = modifier
            .aspectRatio(if (isWide) 2f else 1f)  // 2:1 para o "0", 1:1 para os demais
            .scale(scale)
            .shadow(
                elevation     = 4.dp,
                shape         = buttonShape,
                ambientColor  = backgroundColor.copy(alpha = 0.3f),
                spotColor     = backgroundColor.copy(alpha = 0.4f)
            )
            .clip(buttonShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.titleLarge,
            color = textColor
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Utilitários
// ─────────────────────────────────────────────────────────────────────────────

/** Retorna o par (backgroundColor, textColor) conforme o tipo do botão. */
private fun ButtonDef.buttonColors(): Pair<Color, Color> = when (type) {
    BtnType.FUNC     -> Pair(CalcFuncBtn,     CalcFuncText)
    BtnType.NUMERIC  -> Pair(CalcNumericBtn,  CalcNumericText)
    BtnType.OPERATOR -> Pair(CalcOperatorBtn, CalcOperatorText)
    BtnType.EQUALS   -> Pair(CalcEqualsBtn,   CalcEqualsText)
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
fun MainScreenPreview() {
    CalculadorafatecTheme {
        CalculatorContent(
            state   = CalculatorState(currentInput = "42"),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1C1E)
@Composable
fun MainScreenErrorPreview() {
    CalculadorafatecTheme {
        CalculatorContent(
            state   = CalculatorState(currentInput = "Erro", isError = true),
            onEvent = {}
        )
    }
}
