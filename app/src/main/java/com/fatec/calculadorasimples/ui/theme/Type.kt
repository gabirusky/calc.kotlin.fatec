package com.fatec.calculadorasimples.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Tipografia estilo Matrix — fonte monoespaçada Monospace com peso Bold.
 *
 * - [displayLarge]: visor principal — números grandes em estilo "terminal"
 * - [displayMedium]: expressão acumulada acima do número
 * - [titleLarge]: texto dos botões da calculadora
 */
val Typography = Typography(
    // Número principal no visor (grande, monoespaçado, Bold)
    displayLarge = TextStyle(
        fontFamily    = FontFamily.Monospace,
        fontWeight    = FontWeight.Bold,
        fontSize      = 68.sp,
        lineHeight    = 76.sp,
        letterSpacing = (-1).sp
    ),
    // Expressão acumulada (ex: "8 ×")
    displayMedium = TextStyle(
        fontFamily    = FontFamily.Monospace,
        fontWeight    = FontWeight.Normal,
        fontSize      = 26.sp,
        lineHeight    = 34.sp,
        letterSpacing = 0.sp
    ),
    // Texto dos botões numéricos e de função
    titleLarge = TextStyle(
        fontFamily    = FontFamily.Monospace,
        fontWeight    = FontWeight.Bold,
        fontSize      = 28.sp,
        lineHeight    = 36.sp,
        letterSpacing = 0.sp
    ),
    // Texto dos botões de operador
    headlineMedium = TextStyle(
        fontFamily    = FontFamily.Monospace,
        fontWeight    = FontWeight.Bold,
        fontSize      = 28.sp,
        lineHeight    = 36.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily    = FontFamily.Monospace,
        fontWeight    = FontWeight.Normal,
        fontSize      = 16.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.5.sp
    )
)