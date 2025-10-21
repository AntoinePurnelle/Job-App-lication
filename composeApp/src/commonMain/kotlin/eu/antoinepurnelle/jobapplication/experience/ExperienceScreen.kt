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

package eu.antoinepurnelle.jobapplication.experience

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import eu.antoinepurnelle.jobapplication.experience.ExperienceViewModel.ExperienceUiState.Error
import eu.antoinepurnelle.jobapplication.experience.ExperienceViewModel.ExperienceUiState.Loaded
import eu.antoinepurnelle.jobapplication.experience.ExperienceViewModel.ExperienceUiState.Loading
import eu.antoinepurnelle.jobapplication.experience.model.ExperienceUiModel
import eu.antoinepurnelle.jobapplication.experience.model.ExperienceUiModel.ExperienceHeader
import eu.antoinepurnelle.jobapplication.util.companyFallbackPictureUrl
import eu.antoinepurnelle.ui.components.atoms.RoundedCornerShapeDefault
import eu.antoinepurnelle.ui.components.atoms.RoundedCornerShapeSmall
import eu.antoinepurnelle.ui.components.atoms.UrlImage
import eu.antoinepurnelle.ui.components.molecules.SubSectionsCard
import eu.antoinepurnelle.ui.components.molecules.cardDecoration
import eu.antoinepurnelle.ui.components.organisms.ErrorView
import eu.antoinepurnelle.ui.components.organisms.LoadingView
import eu.antoinepurnelle.ui.components.organisms.model.SubSectionModel
import eu.antoinepurnelle.ui.theme.Dimens.Padding
import eu.antoinepurnelle.ui.theme.Dimens.Padding.SpacerDefault
import eu.antoinepurnelle.ui.theme.Dimens.Size
import eu.antoinepurnelle.ui.theme.colors
import jobapplication.composeapp.generated.resources.Res
import jobapplication.composeapp.generated.resources.ic_arrow_left
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ExperienceScreen(
    id: String,
    onBack: () -> Unit,
    viewModel: ExperienceViewModel = koinViewModel { parametersOf(id) },
) {
    when (val state = viewModel.uiState.collectAsState().value) {
        is Loading -> LoadingView()
        is Error -> ErrorView(state.message)
        is Loaded -> ExperienceView(state.data, onBack)
    }
}

@Composable
private fun ExperienceView(
    uiModel: ExperienceUiModel,
    onBack: () -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()
        .padding(Padding.Screen),
) {
    HeaderView(uiModel.header, onBack)
    PositionsView(uiModel.positions)
}

@Composable
private fun HeaderView(
    header: ExperienceHeader,
    onBack: () -> Unit,
) = Column(
    modifier = Modifier.cardDecoration(PaddingValues()).fillMaxWidth(),
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_left),
            contentDescription = "Back", // TODO i18n
            tint = colors.text.main,
            modifier = Modifier
                .clip(RoundedCornerShapeDefault)
                .clickable { onBack() }
                .padding(horizontal = Padding.CardHorizontal, vertical = Padding.CardVertical)
                .size(Size.IconSmall),
        )

        Text(
            text = header.title,
            style = typography.titleLarge,
            color = colors.text.main,
            modifier = Modifier.padding(horizontal = Padding.CardHorizontal, vertical = Padding.CardVertical),
        )
    }

    Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = spacedBy(SpacerDefault),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Padding.CardHorizontal, end = Padding.CardHorizontal, bottom = Padding.CardVertical),
    ) {
        UrlImage(
            url = header.pictureUrl ?: companyFallbackPictureUrl,
            contentDescription = header.company,
            modifier = Modifier
                .size(Size.CompanyAvatar)
                .clip(RoundedCornerShapeSmall),
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Start,
            verticalArrangement = spacedBy(Padding.SpacerContent),
        ) {
            Text(
                text = header.company,
                style = typography.bodyLarge,
                color = colors.text.main,
            )
            Text(
                text = header.date,
                style = typography.bodyMedium,
                color = colors.text.main,
            )
        }
    }
}

@Composable
private fun PositionsView(
    positions: List<SubSectionModel>,
) {
    if (positions.isNotEmpty()) {
        SubSectionsCard(
            title = "Positions", // TODO i18n
            subSections = positions,
            scrollable = true,
        )
    }
}
