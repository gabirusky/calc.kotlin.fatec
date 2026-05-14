package com.fatec.calculadorasimples.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Esquema de cores escuro fixo da Calculadora FATEC.
 *
 * Calculadoras adotam convencionalmente dark mode; desabilitamos dynamic color
 * para manter a identidade visual consistente em qualquer dispositivo.
 */
private val CalcDarkColorScheme = darkColorScheme(
    primary         = Primary,
    onPrimary       = OnPrimary,
    surface         = Surface,
    onSurface       = OnSurface,
    surfaceVariant  = SurfaceVariant,
    onSurfaceVariant= OnSurfaceVariant,
    background      = CalcBackground,
    onBackground    = CalcDisplayText
)

/**
 * Tema principal do aplicativo.
 *
 * Sempre usa [CalcDarkColorScheme] — dynamic color desabilitado intencionalmente.
 */
@Composable
fun CalculadorafatecTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CalcDarkColorScheme,
        typography  = Typography,
        content     = content
    )
}