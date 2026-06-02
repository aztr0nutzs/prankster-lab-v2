package com.pranksterlab.components.reactor.ultimate

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun UltimateReactorBottomPanel(
    state: UltimateReactorState,
    modifier: Modifier = Modifier,
    onStateChange: (UltimateReactorState) -> Unit,
    onDeploy: () -> Unit,
    onPowerToggle: () -> Unit
) {
    UltimateReactorControls(
        state = state,
        modifier = modifier,
        onStateChange = onStateChange,
        onDeploy = onDeploy,
        onPowerToggle = onPowerToggle
    )
}
