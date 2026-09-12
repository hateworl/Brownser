package com.taiji.browser.browser

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.taiji.browser.ui.theme.FluidMedium

/**
 * Barra de navegação minimalista: campo de URL + indicador de bloqueio + ação de compartilhar.
 * O fundo faz uma transição de cor suave (FluidMedium) entre estado normal e "carregando",
 * como uma respiração — referência à ideia de fluidez do Taiji.
 */
@Composable
fun BrowserBar(
    url: String,
    isLoading: Boolean,
    blockedCount: Int,
    onUrlSubmit: (String) -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editing by remember { mutableStateOf(false) }
    var draft by remember(url) { mutableStateOf(url) }

    val backgroundColor by animateColorAsState(
        targetValue = if (isLoading)
            MaterialTheme.colorScheme.surfaceVariant
        else
            MaterialTheme.colorScheme.surface,
        label = "browserBarBackground"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 8.dp),
        color = backgroundColor,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = "Anúncios bloqueados: $blockedCount",
                tint = if (blockedCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )

            if (editing) {
                TextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                        focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent
                    )
                )
            } else {
                Text(
                    text = url,
                    modifier = Modifier
                        .weight(1f)
                        .clickableNoRipple { editing = true },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(onClick = onShare) {
                Icon(Icons.Filled.Share, contentDescription = "Compartilhar")
            }
        }
    }

    if (editing) {
        LaunchedEffect(editing) {
            // submit ao perder foco/enter tratado no TextField em versão futura
        }
    }
}

private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        indication = null,
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
        onClick = onClick
    )
)

// import necessário para o clickable customizado acima
private fun Modifier.clickable(
    indication: androidx.compose.foundation.Indication?,
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource,
    onClick: () -> Unit
): Modifier = androidx.compose.foundation.clickable(
    interactionSource = interactionSource,
    indication = indication,
    onClick = onClick
)
