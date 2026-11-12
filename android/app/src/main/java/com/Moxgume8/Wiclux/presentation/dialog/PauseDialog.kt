package com.Moxgume8.Wiclux.presentation.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.Moxgume8.Wiclux.R
import com.Moxgume8.Wiclux.core.theme.AccentDeep
import com.Moxgume8.Wiclux.core.theme.IceSurface
import com.Moxgume8.Wiclux.core.theme.TextSecondary

@Composable
fun PauseDialog(onResume: () -> Unit, onMenu: () -> Unit) {
    AlertDialog(
        onDismissRequest = onResume,
        shape = RoundedCornerShape(24.dp),
        containerColor = IceSurface,
        title = {
            Text(
                text = stringResource(R.string.pause_title),
                style = MaterialTheme.typography.titleLarge,
                color = AccentDeep
            )
        },
        text = {
            Text(
                text = stringResource(R.string.pause_body),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onResume) {
                Text(
                    text = stringResource(R.string.resume),
                    style = MaterialTheme.typography.titleMedium,
                    color = AccentDeep
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onMenu) {
                Text(
                    text = stringResource(R.string.menu),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
            }
        }
    )
}
