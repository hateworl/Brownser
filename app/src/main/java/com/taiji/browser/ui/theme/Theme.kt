package com.taiji.browser.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val TaijiDarkScheme = darkColorScheme(
    primary = QiAccent,
    secondary = QiAccentSoft,
    background = YinBlack,
    surface = YinCharcoal,
    onBackground = YangPearl,
    onSurface = YangPearl
)

private val TaijiLightScheme = lightColorScheme(
    primary = QiAccent,
    secondary = QiAccentSoft,
    background = YangWhite,
    surface = YangPearl,
    onBackground = YinCharcoal,
    onSurface = YinCharcoal
)

@Composable
fun TaijiBrowserTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Cores dinâmicas do wallpaper (Material You) - encaixa o app na UI do sistema
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> TaijiDarkScheme
        else -> TaijiLightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = TaijiShapes,
        content = content
    )
}
