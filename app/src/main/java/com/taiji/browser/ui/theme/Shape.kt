package com.taiji.browser.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Cantos assimétricos: referência à curva em "S" que divide o Yin-Yang.
// Em vez de raios iguais nos 4 cantos, um lado é mais "cheio" e o outro mais "vazio".
val TaijiShapes = Shapes(
    extraSmall = RoundedCornerShape(topStart = 4.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 4.dp),
    small = RoundedCornerShape(topStart = 8.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 8.dp),
    medium = RoundedCornerShape(topStart = 12.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 12.dp),
    large = RoundedCornerShape(topStart = 16.dp, topEnd = 40.dp, bottomStart = 40.dp, bottomEnd = 16.dp),
    extraLarge = RoundedCornerShape(topStart = 24.dp, topEnd = 56.dp, bottomStart = 56.dp, bottomEnd = 24.dp)
)
