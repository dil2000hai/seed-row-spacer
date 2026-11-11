package com.seedrow.spacer.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seedrow.spacer.core.theme.InkPrimary
import com.seedrow.spacer.core.theme.InkSecondary

@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    homePainter: Painter? = null,
    homeDescription: String? = null,
    onHome: (() -> Unit)? = null,
    actionPainter: Painter? = null,
    actionDescription: String? = null,
    actionState: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            if (homePainter != null && onHome != null) {
                IconButton(onClick = onHome, modifier = Modifier.size(48.dp)) {
                    Image(
                        painter = homePainter,
                        contentDescription = homeDescription,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = InkPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = InkSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            if (actionPainter != null && onAction != null) {
                IconButton(
                    onClick = onAction,
                    modifier = Modifier
                        .size(48.dp)
                        .semantics {
                            if (actionState != null) stateDescription = actionState
                        }
                ) {
                    Image(
                        painter = actionPainter,
                        contentDescription = actionDescription,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
