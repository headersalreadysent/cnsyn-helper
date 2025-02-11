package co.ec.helper.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity

@Composable
fun rememberKeyboardVisibleState(): State<Boolean> {
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    return remember {
        derivedStateOf { imeInsets.getBottom(density) > 0 }
    }
}
