package com.fatec.calculadorasimples.ui.theme

import androidx.compose.ui.graphics.Color

// ── Fundo geral da tela ──────────────────────────────────────
val CalcBackground      = Color(0xFF1C1C1E)  // Quase preto (iOS-style)

// ── Visor ────────────────────────────────────────────────────
val CalcDisplayBg       = Color(0xFF2C2C2E)  // Cinza muito escuro
val CalcDisplayText     = Color(0xFFFFFFFF)  // Branco puro
val CalcDisplaySubText  = Color(0xFFAEAEB2)  // Cinza claro (expressão acumulada)

// ── Botões numéricos ─────────────────────────────────────────
val CalcNumericBtn      = Color(0xFF3A3A3C)  // Cinza escuro
val CalcNumericText     = Color(0xFFFFFFFF)

// ── Botões de função (C, ±, %) ───────────────────────────────
val CalcFuncBtn         = Color(0xFF636366)  // Cinza médio
val CalcFuncText        = Color(0xFFFFFFFF)

// ── Botões de operador (+, −, ×, ÷) ─────────────────────────
val CalcOperatorBtn     = Color(0xFFFF9F0A)  // Âmbar/laranja Apple
val CalcOperatorText    = Color(0xFFFFFFFF)

// ── Botão de igual (=) ──────────────────────────────────────
val CalcEqualsBtn       = Color(0xFFFF9F0A)  // Mesmo âmbar
val CalcEqualsText      = Color(0xFFFFFFFF)

// ── Compatibilidade com Material3 (DarkColorScheme) ──────────
val Primary             = Color(0xFFFF9F0A)
val OnPrimary           = Color(0xFF000000)
val Surface             = Color(0xFF2C2C2E)
val OnSurface           = Color(0xFFFFFFFF)
val SurfaceVariant      = Color(0xFF3A3A3C)
val OnSurfaceVariant    = Color(0xFFAEAEB2)