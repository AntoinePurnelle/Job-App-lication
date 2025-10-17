/*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package eu.antoinepurnelle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import eu.antoinepurnelle.ui.theme.Colors.DecorColors
import eu.antoinepurnelle.ui.theme.Colors.DecorColors.CardsColors
import eu.antoinepurnelle.ui.theme.Colors.TextColors

data class Colors(
    val decor: DecorColors,
    val text: TextColors,
) {
    data class DecorColors(
        val gradientStart: Color,
        val gradientEnd: Color,

        val divider: Color,
        val border: Color = divider,

        val buttonBackground: Color,
        val pillBackground: Color,

        val card: CardsColors,
    ) {
        data class CardsColors(
            val background: Color,
            val shadow: Color,
            val inset: Color,
        )
    }

    data class TextColors(
        val main: Color,
        val secondary: Color,
    )
}

val colors: Colors
    @Composable
    get() = if (isSystemInDarkTheme()) DarkColors else LightColors

val LightColors = Colors(
    decor = DecorColors(
        gradientStart = Color(0xFF6EC6FF), // lighter blue
        gradientEnd = Color(0xFFB2EBF2), // lighter cyan
        divider = Color(0xFFE9EDF2), // light grey
        buttonBackground = Color(0xFFFFDAB0), // Light apricot/peach orange
        pillBackground = Color(0xFFF5F7FA), // Very light grey
        card = CardsColors(
            background = Color.White,
            shadow = Color(0x1A101828),
            inset = Color(0x1018280D),
        ),
    ),
    text = TextColors(
        main = Color(0xFF001830), // dark grey
        secondary = Color(0xFF3E4C59), // medium grey
    ),
)

val DarkColors = Colors(
    decor = DecorColors(
        gradientStart = Color(0xFF1E8381), // Dark cyan
        gradientEnd = Color(0xFF131A56), // Dark blue
        divider = Color(0xFF2D333B), // Darker grey
        buttonBackground = Color(0xFFCF7F53), // Deeper orange-brown
        pillBackground = Color(0xFF2D333B), // Darker grey
        card = CardsColors(
            background = Color(0xFF1B1E26), // Dark grey
            shadow = Color.Transparent,
            inset = Color(0x1018280D),
        ),
    ),
    text = TextColors(
        main = Color.White,
        secondary = Color(0xFFB0B8C1), // Light grey
    ),
)
