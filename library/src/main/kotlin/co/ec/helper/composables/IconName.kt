package co.ec.helper.composables


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun IconName(
    name: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = "",
    tint: Color = LocalContentColor.current
) {
    var iconClassName = name
    if (!name.contains(".")) {
        iconClassName = "filled.$name"
    }
    val icon: ImageVector? = remember(name) { getIconByName(iconClassName) }
    icon?.let {
        Icon(
            icon, contentDescription = contentDescription ?: name,
            modifier = modifier,
            tint = tint
        )
    }
}


private fun getIconByName(name: String): ImageVector? {
    var iconClassName = name
    if (!name.contains(".")) {
        iconClassName = "filled.$name"
    }
    val icon = try {
        val cl = Class.forName("androidx.compose.material.icons.${iconClassName}Kt")
        val method = cl.declaredMethods.first()
        val obj: Any = when (iconClassName.split(".")[0].lowercase()) {
            "filled" -> Icons.Filled
            "twotone" -> Icons.TwoTone
            "outlined" -> Icons.Outlined
            "rounded" -> Icons.Rounded
            "sharp" -> Icons.Sharp
            else -> Icons.Filled
        }
        method.invoke(null, obj) as ImageVector
    } catch (_: Throwable) {
        null
    }
    return icon
}


@Preview(showBackground = true)
@Composable
fun IconNamePreview() {
    MaterialTheme {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconName(name = "Mic")
            IconName(name = "twotone.ContentCopy")
            IconName(name = "sharp.Add")
        }
    }
}
