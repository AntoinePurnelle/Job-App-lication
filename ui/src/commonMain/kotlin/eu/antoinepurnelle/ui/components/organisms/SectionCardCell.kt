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

package eu.antoinepurnelle.ui.components.organisms

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import eu.antoinepurnelle.ui.components.atoms.HorizontalDiv
import eu.antoinepurnelle.ui.components.atoms.RoundedCornerShapeDefault
import eu.antoinepurnelle.ui.components.atoms.RoundedCornerShapeSmall
import eu.antoinepurnelle.ui.components.atoms.UrlImage
import eu.antoinepurnelle.ui.components.atoms.VerticalSpacer
import eu.antoinepurnelle.ui.components.molecules.PillsGroup
import eu.antoinepurnelle.ui.components.organisms.model.SectionCardItemModel
import eu.antoinepurnelle.ui.onClick
import eu.antoinepurnelle.ui.theme.Dimens.Padding
import eu.antoinepurnelle.ui.theme.Dimens.Size
import eu.antoinepurnelle.ui.theme.colors
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun SectionCardCell(
    item: SectionCardItemModel,
    isFirst: Boolean,
    isLast: Boolean,
    fallbackPictureUrl: String? = null,
    trailingIconRes: DrawableResource? = null,
    onClick: (() -> Unit)? = null,
) {
    val (top, bottom, shape) = when {
        isFirst && isLast -> Triple(Padding.CardVertical, Padding.CardVertical, RoundedCornerShapeDefault)
        isFirst -> Triple(
            Padding.CardVertical,
            Padding.CardVerticalHalf,
            RoundedCornerShape(topStart = Size.RoundedCornerSizeDefault, topEnd = Size.RoundedCornerSizeDefault),
        )
        isLast -> Triple(
            Padding.CardVerticalHalf,
            Padding.CardVertical,
            RoundedCornerShape(bottomStart = Size.RoundedCornerSizeDefault, bottomEnd = Size.RoundedCornerSizeDefault),
        )
        else -> Triple(Padding.CardVerticalHalf, Padding.CardVerticalHalf, RectangleShape)
    }
    val padding = PaddingValues(top = top, bottom = bottom, start = Padding.CardHorizontal, end = Padding.CardHorizontal)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .onClick(onClick)
            .padding(padding),
    ) {
        Row(
            verticalAlignment = CenterVertically,
            horizontalArrangement = spacedBy(Padding.SpacerDefault),
        ) {
            (item.pictureUrl ?: fallbackPictureUrl)?.let {
                UrlImage(
                    url = it,
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(Size.CompanyAvatar)
                        .clip(RoundedCornerShapeSmall),
                )
            }

            CellContent(item)

            if (trailingIconRes != null) {
                Icon(
                    painter = painterResource(trailingIconRes),
                    contentDescription = "See details for ${item.title} at ${item.subtitle}", // TODO #9 i18l
                    tint = colors.text.secondary,
                )
            }
        }

        if (item.pills.isNotEmpty()) {
            VerticalSpacer()
            PillsGroup(item.pills)
        }
    }

    if (!isLast) {
        HorizontalDiv()
    }
}

@Composable
private fun RowScope.CellContent(item: SectionCardItemModel) = Column(
    modifier = Modifier.weight(1f),
    horizontalAlignment = Start,
    verticalArrangement = spacedBy(Padding.SpacerContent),
) {
    Text(
        text = item.title,
        style = typography.bodyLarge,
        color = colors.text.main,
    )
    item.subtitle?.let {
        Text(
            text = it,
            style = typography.bodyMedium,
            color = colors.text.main,
        )
    }
    item.description?.let {
        Text(
            text = it,
            style = typography.bodySmall,
            color = colors.text.main,
        )
    }
    item.date?.let {
        Text(
            text = it,
            style = typography.bodySmall,
            color = colors.text.secondary,
        )
    }
}
