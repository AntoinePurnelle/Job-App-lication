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

package eu.antoinepurnelle.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import eu.antoinepurnelle.ui.components.atoms.HorizontalSpacerSmall
import eu.antoinepurnelle.ui.components.atoms.UrlImage
import eu.antoinepurnelle.ui.components.molecules.model.PillModel
import eu.antoinepurnelle.ui.theme.Dimens.Size.BorderWidth
import eu.antoinepurnelle.ui.theme.Dimens.Size.IconSmall
import eu.antoinepurnelle.ui.theme.colors

@Composable
fun Pill(
    model: PillModel,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(percent = 50)

    Row(
        modifier = modifier
            .border(width = BorderWidth, color = colors.decor.border, shape = shape)
            .clip(shape)
            .background(color = colors.decor.pillBackground, shape = shape)
            .defaultMinSize(minHeight = 20.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        model.iconUrl?.let { iconUrl ->
            UrlImage(
                url = iconUrl,
                contentDescription = model.label,
                modifier = Modifier.size(IconSmall),
            )
            HorizontalSpacerSmall()
        }

        Text(model.label, style = typography.bodySmall, color = colors.text.main)
    }
}
