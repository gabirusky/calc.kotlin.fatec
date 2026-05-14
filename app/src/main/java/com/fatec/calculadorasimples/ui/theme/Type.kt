package com.fatec.calculadorasimples.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Tipografia customizada para a Calculadora FATEC.
 *
 * - [displayLarge]: visor principal — números grandes, peso leve (estilo iOS Calculator)
 * - [displayMedium]: expressão acumulada acima do número
 * - [headlineLarge]: fallback para títulos
 * - [labelLarge]: texto dos botões
 */
val Typography = Typography(
    // Número principal no visor
    displayLarge = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Light,
        fontSize     = 72.sp,
        lineHeight   = 80.sp,
        letterSpacing = (-2).sp
    ),
    // Expressão acumulada (ex: "8 ×")
    displayMedium = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Light,
        fontSize     = 28.sp,
        lineHeight   = 36.sp,
        letterSpacing = (-0.5).sp
    ),
    // Texto dos botões numéricos
    titleLarge = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Normal,
        fontSize     = 32.sp,
        lineHeight   = 40.sp,
        letterSpacing = 0.sp
    ),
    // Texto dos botões de operador
    headlineMedium = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Normal,
        fontSize     = 32.sp,
        lineHeight   = 40.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily   = FontFamily.Default,
        fontWeight   = FontWeight.Normal,
        fontSize     = 16.sp,
        lineHeight   = 24.sp,
        letterSpacing = 0.5.sp
    )
)