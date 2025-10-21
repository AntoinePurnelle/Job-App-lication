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

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLinkStyles
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import eu.antoinepurnelle.ui.theme.colors

@Composable
fun MDText(
    content: String,
    modifier: Modifier = Modifier,
) = Markdown(
    content = content,
    modifier = modifier,
    colors = DefaultMarkdownColors(
        text = colors.text.main,
        codeBackground = colors.decor.pillBackground,
        inlineCodeBackground = colors.decor.pillBackground,
        dividerColor = colors.decor.divider,
        tableBackground = colors.decor.card.background,
    ),
    typography = DefaultMarkdownTypography(
        h1 = MaterialTheme.typography.headlineLarge,
        h2 = MaterialTheme.typography.headlineMedium,
        h3 = MaterialTheme.typography.headlineSmall,
        h4 = MaterialTheme.typography.titleLarge,
        h5 = MaterialTheme.typography.titleMedium,
        h6 = MaterialTheme.typography.titleSmall,
        text = MaterialTheme.typography.bodyLarge,
        code = MaterialTheme.typography.bodyLarge,
        inlineCode = MaterialTheme.typography.bodyLarge,
        quote = MaterialTheme.typography.bodyLarge,
        paragraph = MaterialTheme.typography.bodyLarge,
        ordered = MaterialTheme.typography.bodyLarge,
        bullet = MaterialTheme.typography.bodyLarge,
        list = MaterialTheme.typography.bodyLarge,
        textLink = TextLinkStyles(),
        table = MaterialTheme.typography.bodyLarge,
    ),
)
