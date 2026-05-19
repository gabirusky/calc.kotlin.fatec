package com.fatec.calculadorasimples.ui

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
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
import kotlinx.coroutines.isActive
import kotlin.random.Random

// Constantes do rain — conservadoras para performance
private const val RAIN_CHARS   = "01234789ACEF@#%"
private const val RAIN_COL_GAP = 32f   // espaçamento largo = menos colunas
private const val RAIN_CHAR_H  = 18f
private const val RAIN_DELAY   = 80L   // ~12fps (leve)

private class RainCol(
    val x: Float, var y: Float, var speed: Float,
    val chars: CharArray, val len: Int
)

// ─────────────────────────────────────────────────────────────────────────────
// Layout de botões
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
// Tela principal (stateful)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MainScreen(viewModel: CalculatorViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CalculatorContent(state = uiState, onEvent = viewModel::onButtonClick)
}

// ─────────────────────────────────────────────────────────────────────────────
// Conteúdo da tela (stateless)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CalculatorContent(
    state: CalculatorState,
    onEvent: (String) -> Unit
) {
    Scaffold(containerColor = CalcBackground) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CalcBackground)
        ) {
            // Camada 1: rain atrás de tudo
            MatrixRain()

            // Camada 2: conteúdo com overlay semitransparente
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC0A0F0A)),  // 80% opaco → rain sutil por baixo
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TitleSection()

                Column {
                    DisplaySection(
                        expression   = state.expression,
                        currentInput = state.currentInput,
                        isError      = state.isError
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (row in buttonRows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
}

// ─────────────────────────────────────────────────────────────────────────────
// Matrix Rain — otimizado com cache de TextLayoutResult
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MatrixRain() {
    val measurer = rememberTextMeasurer()
    val style = remember { TextStyle(fontSize = 12.sp, fontFamily = FontFamily.Monospace) }
    val cache = remember(measurer) {
        RAIN_CHARS.toSet().associateWith { measurer.measure(it.toString(), style) }
    }

    val cols = remember { mutableListOf<RainCol>() }
    var w by remember { mutableStateOf(0f) }
    var h by remember { mutableStateOf(0f) }
    var tick by remember { mutableStateOf(0) }

    LaunchedEffect(w, h) {
        if (w <= 0f || h <= 0f || cols.isNotEmpty()) return@LaunchedEffect
        val n = (w / RAIN_COL_GAP).toInt().coerceAtLeast(1)
        repeat(n) { i ->
            val len = Random.nextInt(4, 10)
            cols += RainCol(
                x = i * RAIN_COL_GAP, y = Random.nextFloat() * h,
                speed = Random.nextFloat() * 2f + 1f,
                chars = CharArray(len) { RAIN_CHARS.random() }, len = len
            )
        }
    }

    LaunchedEffect(Unit) {
        var t = 0
        while (isActive) {
            delay(RAIN_DELAY)
            cols.forEachIndexed { i, c ->
                c.y += c.speed
                if (t and 7 == 0) c.chars[Random.nextInt(c.chars.size)] = RAIN_CHARS.random()
                if (c.y > h + c.len * RAIN_CHAR_H) {
                    val len = Random.nextInt(4, 10)
                    cols[i] = RainCol(
                        x = c.x, y = -(len * RAIN_CHAR_H),
                        speed = Random.nextFloat() * 2f + 1f,
                        chars = CharArray(len) { RAIN_CHARS.random() }, len = len
                    )
                }
            }
            tick = ++t
        }
    }

    @Suppress("UNUSED_EXPRESSION") tick

    Canvas(
        modifier = Modifier.fillMaxSize().onSizeChanged { w = it.width.toFloat(); h = it.height.toFloat() }
    ) {
        val green = Color(0xFF00FF41)
        cols.forEach { c ->
            c.chars.forEachIndexed { i, ch ->
                val cy = c.y - i * RAIN_CHAR_H
                if (cy < -RAIN_CHAR_H || cy > size.height) return@forEachIndexed
                val a = if (i == 0) 0.9f else ((1f - i.toFloat() / c.len) * 0.4f)
                if (a < 0.05f) return@forEachIndexed
                cache[ch]?.let { drawText(it, color = if (i == 0) Color.White else green, topLeft = Offset(c.x, cy), alpha = a) }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Título — ocupa o espaço vazio superior
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TitleSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text       = "Calculadora Kotlin",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize   = 30.sp,
            color      = MatrixGreen,
            letterSpacing = 2.sp
        )
        // Linha decorativa verde abaixo do título
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MatrixGreen.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Visor com estilo Matrix
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DisplaySection(
    expression: String,
    currentInput: String,
    isError: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CalcBackground, CalcDisplayBg)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MatrixGreen.copy(alpha = 0.35f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            // Expressão acumulada
            Text(
                text       = expression,
                fontFamily = FontFamily.Monospace,
                fontSize   = 24.sp,
                color      = CalcDisplaySubText.copy(alpha = 0.7f),
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Número principal
            Text(
                text       = if (isError) "[ ERRO ]" else currentInput,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize   = 56.sp,
                color      = if (isError) Color(0xFFFF0040) else CalcDisplayText,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
                textAlign  = TextAlign.End,
                modifier   = Modifier.fillMaxWidth()
            )

            // Cursor estático verde
            Text(
                text       = "_",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
                color      = MatrixGreen.copy(alpha = 0.6f),
                modifier   = Modifier.fillMaxWidth().padding(top = 2.dp),
                textAlign  = TextAlign.End
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Botão com estilo Matrix — borda neon + escala
// ─────────────────────────────────────────────────────────────────────────────

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
        targetValue   = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 100),
        label         = "btnScale"
    )

    val buttonShape = remember(isWide) {
        if (isWide) RoundedCornerShape(50) else CircleShape
    }

    // Borda: "=" sempre verde brilhante; pressionado = verde claro; padrão = verde dim
    val borderColor = when {
        isEquals  -> MatrixGreen
        isPressed -> MatrixGreen.copy(alpha = 0.8f)
        else      -> MatrixGreenDim.copy(alpha = 0.4f)
    }

    Box(
        modifier = modifier
            .aspectRatio(if (isWide) 2f else 1f)
            .scale(scale)
            .clip(buttonShape)
            .background(backgroundColor)
            .border(
                width = if (isEquals) 2.dp else 1.dp,
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
            fontSize   = if (isEquals) 30.sp else 24.sp,
            color      = if (isPressed) Color.White else textColor
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Utilitários
// ─────────────────────────────────────────────────────────────────────────────

private fun ButtonDef.buttonColors(): Pair<Color, Color> = when (type) {
    BtnType.FUNC     -> CalcFuncBtn     to CalcFuncText
    BtnType.NUMERIC  -> CalcNumericBtn  to CalcNumericText
    BtnType.OPERATOR -> CalcOperatorBtn to CalcOperatorText
    BtnType.EQUALS   -> CalcEqualsBtn   to CalcEqualsText
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF0A0F0A)
@Composable
fun MainScreenPreview() {
    CalculadorafatecTheme {
        CalculatorContent(state = CalculatorState(currentInput = "1984"), onEvent = {})
    }
}
