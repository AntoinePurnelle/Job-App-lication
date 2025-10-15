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

package eu.antoinepurnelle.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import eu.antoinepurnelle.ui.theme.colors

/**
 * Apply a gradient background from bottom-left to top-right
 */
@Composable
fun Modifier.gradientBackground() = this then Modifier.background(
    brush = Brush.linearGradient(
        colors = listOf(colors.decor.gradientStart, colors.decor.gradientEnd),
        start = Offset(0f, Float.POSITIVE_INFINITY), // bottom left
        end = Offset(Float.POSITIVE_INFINITY, 0f), // top right
    ),
)
