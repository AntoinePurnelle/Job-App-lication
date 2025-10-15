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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import eu.antoinepurnelle.ui.components.atoms.RoundedCornerShapeDefault
import eu.antoinepurnelle.ui.components.organisms.SectionCardCell
import eu.antoinepurnelle.ui.components.organisms.model.SectionCardItemModel
import eu.antoinepurnelle.ui.components.organisms.model.SubSectionModel
import eu.antoinepurnelle.ui.theme.Dimens.Padding
import eu.antoinepurnelle.ui.theme.Dimens.Size.ElevationDefault
import eu.antoinepurnelle.ui.theme.colors
import org.jetbrains.compose.resources.DrawableResource

/**
 * Apply a card decoration to any Composable
 * It will add a shadow, a background color, rounded corners, an inner shadow and padding
 *
 * @param padding Padding to apply inside the card
 */
@Composable
fun Modifier.cardDecoration(
    padding: PaddingValues = PaddingValues(horizontal = Padding.CardHorizontal, vertical = Padding.CardVertical),
): Modifier {
    val shape = RoundedCornerShapeDefault
    return this then Modifier
        .dropShadow(
            shape = shape,
            shadow = Shadow(
                color = colors.decor.card.shadow,
                radius = ElevationDefault,
                offset = DpOffset(x = 0.dp, y = 1.dp),
            ),
        )
        .background(color = colors.decor.card.background, shape = shape)
        .clip(shape)
        .innerShadow(
            shape = shape,
            shadow = Shadow(
                color = colors.decor.card.inset,
                radius = 0.dp,
                offset = DpOffset(x = 0.dp, y = (-2).dp),
            ),
        )
        .padding(padding)
}

/**
 * A card with a title and content inside
 */
@Composable
fun SectionCard(
    title: String,
    padding: PaddingValues = PaddingValues(horizontal = Padding.CardHorizontal, vertical = Padding.CardVertical),
    content: @Composable BoxScope.() -> Unit,
) {
    Column {
        Text(
            text = title.uppercase(),
            style = typography.labelLarge,
            color = colors.text.main,
            modifier = Modifier.padding(vertical = Padding.SectionTitleVertical, horizontal = Padding.CardHorizontal),
        )
        Box(
            modifier = Modifier.cardDecoration(padding),
            content = content,
        )

    }
}

/**
 * A card with a title and a list of items inside
 * If the list is empty, nothing is displayed
 */
@Composable
fun SectionCard(
    title: String,
    padding: PaddingValues = PaddingValues(),
    items: List<SectionCardItemModel>,
    fallbackPictureUrl: String,
    itemTrailingIconRes: DrawableResource? = null,
    onItemClick: ((SectionCardItemModel) -> Unit)? = null,
) {
    if (items.isNotEmpty()) {
        SectionCard(
            title = title,
            padding = padding,
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    SectionCardCell(
                        item = item,
                        isFirst = index == 0,
                        isLast = index == items.lastIndex,
                        fallbackPictureUrl = fallbackPictureUrl,
                        trailingIconRes = itemTrailingIconRes,
                        onClick = onItemClick?.let { { it(item) } },
                    )
                }
            }
        }
    }
}

/**
 * A card with a title and a list of subsections inside
 * If all subsections are empty, nothing is displayed
 */
@Composable
fun SubSectionsCard(
    title: String,
    padding: PaddingValues = PaddingValues(),
    subsections: List<SubSectionModel>,
    fallbackPictureUrl: String,
    itemTrailingIconRes: DrawableResource? = null,
    onItemClick: ((SectionCardItemModel) -> Unit)? = null,
) {
    if (subsections.any { it.items.isNotEmpty() }) {
        SectionCard(
            title = title,
            padding = padding,
        ) {
            Column {
                subsections.forEach { subsection ->
                    SubSectionView(
                        title = subsection.title,
                        items = subsection.items,
                        fallbackPictureUrl = fallbackPictureUrl,
                        itemTrailingIconRes = itemTrailingIconRes,
                        onClick = onItemClick,
                    )
                }
            }
        }
    }
}

/**
 * A subsection with a title and a list of items inside
 * If the list is empty, nothing is displayed
 */
@Composable
private fun SubSectionView(
    title: String,
    items: List<SectionCardItemModel>,
    fallbackPictureUrl: String,
    itemTrailingIconRes: DrawableResource? = null,
    onClick: ((SectionCardItemModel) -> Unit)?,
) {
    if (items.isNotEmpty()) {
        Column(
            horizontalAlignment = CenterHorizontally,
        ) {
            Text(
                text = title,
                style = typography.labelLarge,
                color = colors.text.main,
                modifier = Modifier.padding(vertical = Padding.SectionTitleVertical, horizontal = Padding.CardHorizontal),
            )
            items.forEachIndexed { index, item ->
                SectionCardCell(
                    item = item,
                    isFirst = false,
                    isLast = index == items.lastIndex,
                    fallbackPictureUrl = fallbackPictureUrl,
                    trailingIconRes = itemTrailingIconRes,
                    onClick = onClick?.let { { it(item) } },
                )
            }
        }
    }
}
