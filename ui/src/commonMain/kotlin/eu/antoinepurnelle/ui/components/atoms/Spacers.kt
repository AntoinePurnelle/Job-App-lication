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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import eu.antoinepurnelle.ui.theme.Dimens.Padding.SpacerDefault
import eu.antoinepurnelle.ui.theme.Dimens.Padding.SpacerLarge
import eu.antoinepurnelle.ui.theme.Dimens.Padding.SpacerXLarge
import eu.antoinepurnelle.ui.theme.Dimens.Padding.SpacerXSmall

/**
 * A vertical spacer with a customizable height. (default 16.dp)
 */
@Composable
fun VerticalSpacer(height: Dp = SpacerDefault, modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(height))
}

/**
 * A 8.dp vertical spacer.
 */
@Composable
fun VerticalSpacerSmall(modifier: Modifier = Modifier) {
    VerticalSpacer(height = SpacerXSmall, modifier = modifier)
}

/**
 * A 24.dp vertical spacer.
 */
@Composable
fun VerticalSpacerLarge(modifier: Modifier = Modifier) {
    VerticalSpacer(height = SpacerLarge, modifier = modifier)
}

/**
 * A horizontal spacer with a customizable width. (default 16.dp)
 */
@Composable
fun HorizontalSpacer(width: Dp = SpacerDefault, modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.width(width))
}

/**
 * A 8.dp horizontal spacer.
 */
@Composable
fun HorizontalSpacerSmall(modifier: Modifier = Modifier) {
    HorizontalSpacer(width = SpacerXSmall, modifier = modifier)
}

/**
 * A 32.dp horizontal spacer.
 */
@Composable
fun HorizontalSpacerXLarge(modifier: Modifier = Modifier) {
    HorizontalSpacer(width = SpacerXLarge, modifier = modifier)
}
