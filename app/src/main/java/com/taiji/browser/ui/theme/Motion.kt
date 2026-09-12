package com.taiji.browser.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween

/**
 * Easing orgânico: aceleração e desaceleração suaves, sem cortes abruptos.
 * Referência ao movimento contínuo e sem esforço (wu wei) do Taiji.
 */
val FluidEasing = CubicBezierEasing(0.33f, 0.0f, 0.15f, 1.0f)

val FluidFast = tween<Float>(durationMillis = 220, easing = FluidEasing)
val FluidMedium = tween<Float>(durationMillis = 380, easing = FluidEasing)
val FluidSlow = tween<Float>(durationMillis = 600, easing = FluidEasing)
