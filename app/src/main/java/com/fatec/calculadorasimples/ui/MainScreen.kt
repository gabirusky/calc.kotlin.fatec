package com.fatec.calculadorasimples.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.fatec.calculadorasimples.ui.theme.MatrixGreen
import com.fatec.calculadorasimples.ui.theme.MatrixGreenDim
import com.fatec.calculadorasimples.viewmodel.CalculatorViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

// ─────────────────────────────────────────────────────────────────────────────
// Definição do layout de botões
// ─────────────────────────────────────────────────────────────────────────────
private enum class BtnType { FUNC, NUMERIC, OPERATOR, EQUALS }

private data class ButtonDef(
    val display: String,
    val action: String,
    val type: BtnType,
    val weight: Float = 1f
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
        ButtonDef("0",   "0",   BtnType.NUMERIC, weight = 2f),
        ButtonDef(".",   ".",   BtnType.NUMERIC),
        ButtonDef("=",   "=",   BtnType.EQUALS)
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// Dados de cada "coluna" da Matrix Rain
// ─────────────────────────────────────────────────────────────────────────────
private data class RainColumn(
    val x: Float,
    var y: Float,
    val speed: Float,
    val chars: MutableList<Char>,
    val length: Int
)

// ─────────────────────────────────────────────────────────────────────────────
// Tela principal (stateful)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Tela principal da Calculadora.
 * Padrão MVVM: coleta o estado do [viewModel] e repassa para [CalculatorContent].
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
// Conteúdo da tela (stateless)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Composable stateless que renderiza o layout completo da calculadora Matrix.
 */
@Composable
fun CalculatorContent(
    state: CalculatorState,
    onEvent: (String) -> Unit
) {
    Scaffold(
        containerColor = CalcBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CalcBackground)
        ) {
            // ── Fundo animado Matrix Rain ─────────────────────────────────
            MatrixRainBackground()

            // ── Conteúdo da calculadora sobre o fundo ──────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)), // overlay escuro semitransparente
                verticalArrangement = Arrangement.Bottom
            ) {
                // ── Visor ─────────────────────────────────────────────────
                DisplaySection(
                    expression   = state.expression,
                    currentInput = state.currentInput,
                    isError      = state.isError
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ── Grid de botões ─────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (row in buttonRows) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
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
                                    isEquals        = btn.type == BtnType.EQUALS,
                                    onClick         = { onEvent(btn.action) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Matrix Rain Background — API Compose nativa (sem nativeCanvas)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Renderiza o efeito "Matrix rain" como fundo animado usando drawText do Compose.
 * Usa [TextMeasurer] para medir e desenhar cada caractere com cor/opacidade variável,
 * sem depender de nenhuma API nativa do Android.
 */
@Composable
private fun MatrixRainBackground() {
    val matrixChars = remember {
        "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ@#\$%&"
    }

    val textMeasurer = rememberTextMeasurer()
    val columns      = remember { mutableStateListOf<RainColumn>() }
    var canvasWidth  by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }

    val charFontSize = 14.sp
    val charSizePx   = 20f   // altura aproximada em pixels para espaçamento
    val colSpacing   = 22f

    // Inicializa colunas quando as dimensões do canvas ficarem disponíveis
    LaunchedEffect(canvasWidth, canvasHeight) {
        if (canvasWidth <= 0f || canvasHeight <= 0f) return@LaunchedEffect
        if (columns.isNotEmpty()) return@LaunchedEffect

        val numCols = (canvasWidth / colSpacing).toInt().coerceAtLeast(1)
        repeat(numCols) { i ->
            val length = Random.nextInt(8, 22)
            columns.add(
                RainColumn(
                    x      = i * colSpacing,
                    y      = Random.nextFloat() * canvasHeight,
                    speed  = Random.nextFloat() * 4f + 2f,
                    chars  = MutableList(length) { matrixChars.random() },
                    length = length
                )
            )
        }
    }

    // Loop de animação — avança colunas e troca caracteres aleatoriamente
    LaunchedEffect(columns.size) {
        if (columns.isEmpty()) return@LaunchedEffect
        while (true) {
            delay(60L)
            columns.forEachIndexed { idx, col ->
                col.y += col.speed
                if (col.y > canvasHeight + col.length * charSizePx) {
                    columns[idx] = col.copy(
                        y     = -(col.length * charSizePx),
                        speed = Random.nextFloat() * 4f + 2f,
                        chars = MutableList(col.length) { matrixChars.random() }
                    )
                }
                // Aleatoriza um caractere do rastro a cada tick
                if (col.chars.isNotEmpty()) {
                    col.chars[Random.nextInt(col.chars.size)] = matrixChars.random()
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        // Captura dimensões na primeira composição
        if (canvasWidth != size.width || canvasHeight != size.height) {
            canvasWidth  = size.width
            canvasHeight = size.height
        }

        columns.forEach { col ->
            col.chars.forEachIndexed { i, ch ->
                val charY = col.y - i * charSizePx
                if (charY < -charSizePx || charY > size.height) return@forEachIndexed

                // Head branco brilhante → rastro verde com opacidade decrescente
                val alpha = if (i == 0) 1f else ((1f - i.toFloat() / col.length) * 0.6f).coerceAtLeast(0f)
                val color = if (i == 0) Color.White.copy(alpha = alpha)
                            else        Color(0xFF00FF41).copy(alpha = alpha)

                val measured = textMeasurer.measure(
                    text  = ch.toString(),
                    style = TextStyle(
                        color      = color,
                        fontSize   = charFontSize,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Normal
                    )
                )
                drawText(
                    textLayoutResult = measured,
                    topLeft          = Offset(col.x, charY)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Visor
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Seção de visor com efeito Matrix:
 * - Brilho pulsante no texto principal
 * - Cursor terminal piscante
 * - Bordas neon verdes
 */
@Composable
private fun DisplaySection(
    expression: String,
    currentInput: String,
    isError: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "displayGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.7f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val displayColor = if (isError) Color(0xFFFF0040) else CalcDisplayText.copy(alpha = glowAlpha)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF050F05), CalcDisplayBg)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MatrixGreen.copy(alpha = 0.5f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            // Expressão acumulada (ex: "12 ×")
            Text(
                text       = expression,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal,
                fontSize   = 24.sp,
                color      = CalcDisplaySubText.copy(alpha = 0.8f),
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Número principal com brilho pulsante
            Text(
                text       = if (isError) "[ ERRO ]" else currentInput,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize   = 64.sp,
                lineHeight = 72.sp,
                color      = displayColor,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
                textAlign  = TextAlign.End,
                modifier   = Modifier.fillMaxWidth()
            )

            // Cursor piscante estilo terminal
            MatrixCursor()
        }
    }
}

/**
 * Cursor piscante estilo terminal Matrix ( _ ).
 */
@Composable
private fun MatrixCursor() {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 0f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )
    Text(
        text       = "_",
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize   = 24.sp,
        color      = MatrixGreen.copy(alpha = cursorAlpha),
        modifier   = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        textAlign  = TextAlign.End
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Botão da calculadora — estilo Matrix
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Botão Matrix com borda neon, glow ao pressionar e animação de escala.
 *
 * @param label           Texto exibido.
 * @param backgroundColor Cor de fundo do botão.
 * @param textColor       Cor do texto.
 * @param modifier        Modifier externo.
 * @param isWide          Botão de largura dupla (botão "0").
 * @param isEquals        Se é o botão "=" (recebe destaque especial).
 * @param onClick         Callback de clique.
 */
@Composable
fun CalculatorButton(
    label: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    isWide: Boolean = false,
    isEquals: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.90f else 1f,
        animationSpec = tween(durationMillis = 80),
        label         = "buttonScale"
    )

    val glowIntensity by animateFloatAsState(
        targetValue   = if (isPressed) 1f else 0f,
        animationSpec = tween(durationMillis = 120),
        label         = "buttonGlow"
    )

    val buttonShape = if (isWide) RoundedCornerShape(50) else CircleShape

    val borderColor = when {
        isEquals  -> MatrixGreen
        isPressed -> Color(0xFF39FF14)
        else      -> MatrixGreenDim.copy(alpha = 0.7f)
    }

    val bgBrush = if (isEquals) {
        Brush.radialGradient(colors = listOf(Color(0xFF00CC33), Color(0xFF007A1F)))
    } else {
        Brush.radialGradient(
            colors = listOf(
                backgroundColor.copy(alpha = 0.9f),
                backgroundColor.copy(alpha = 0.6f)
            )
        )
    }

    Box(
        modifier = modifier
            .aspectRatio(if (isWide) 2f else 1f)
            .scale(scale)
            .shadow(
                elevation    = if (isPressed) 12.dp else 4.dp,
                shape        = buttonShape,
                ambientColor = MatrixGreen.copy(alpha = 0.3f + glowIntensity * 0.4f),
                spotColor    = MatrixGreen.copy(alpha = 0.5f + glowIntensity * 0.5f)
            )
            .clip(buttonShape)
            .background(brush = bgBrush)
            .border(
                width = if (isEquals || isPressed) 2.dp else 1.dp,
                color = borderColor,
                shape = buttonShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize   = if (isEquals) 32.sp else 26.sp,
            color      = if (isPressed) Color.White else textColor
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

@Preview(showBackground = true, backgroundColor = 0xFF0A0F0A)
@Composable
fun MainScreenPreview() {
    CalculadorafatecTheme {
        CalculatorContent(
            state   = CalculatorState(currentInput = "1984"),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0F0A)
@Composable
fun MainScreenErrorPreview() {
    CalculadorafatecTheme {
        CalculatorContent(
            state   = CalculatorState(currentInput = "Erro", isError = true),
            onEvent = {}
        )
    }
}
