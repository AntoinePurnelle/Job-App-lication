package eu.antoinepurnelle.jobapplication

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

interface Colors {
    val gradientStart: Color
    val gradientEnd: Color
}

val colors: Colors
    @Composable
    get() = if (isSystemInDarkTheme()) DarkColors else LightColors

data object LightColors : Colors {
    override val gradientStart = Color(0xFF26D0CE)
    override val gradientEnd = Color(0xFF1A2980)
}

data object DarkColors : Colors {
    override val gradientEnd = Color(0xFF131A56)
    override val gradientStart = Color(0xFF1E8381)
}
