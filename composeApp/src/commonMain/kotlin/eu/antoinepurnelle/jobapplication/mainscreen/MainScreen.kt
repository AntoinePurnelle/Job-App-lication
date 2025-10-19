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

package eu.antoinepurnelle.jobapplication.mainscreen

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import eu.antoinepurnelle.jobapplication.Pilot
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel.MainUiState.Error
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel.MainUiState.Loaded
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel.MainUiState.Loading
import eu.antoinepurnelle.jobapplication.mainscreen.model.MainUiModel
import eu.antoinepurnelle.jobapplication.mainscreen.model.MainUiModel.Header
import eu.antoinepurnelle.jobapplication.util.LaunchType
import eu.antoinepurnelle.jobapplication.util.companyFallbackPictureUrl
import eu.antoinepurnelle.jobapplication.util.educationFallbackPictureUrl
import eu.antoinepurnelle.jobapplication.util.getContext
import eu.antoinepurnelle.jobapplication.util.launch
import eu.antoinepurnelle.jobapplication.util.projectFallbackPictureUrl
import eu.antoinepurnelle.ui.components.atoms.HorizontalDiv
import eu.antoinepurnelle.ui.components.atoms.RoundedCornerShapeDefault
import eu.antoinepurnelle.ui.components.atoms.UrlImage
import eu.antoinepurnelle.ui.components.atoms.VerticalSpacer
import eu.antoinepurnelle.ui.components.atoms.VerticalSpacerSmall
import eu.antoinepurnelle.ui.components.molecules.PillsGroup
import eu.antoinepurnelle.ui.components.molecules.QuickAction
import eu.antoinepurnelle.ui.components.molecules.SectionCard
import eu.antoinepurnelle.ui.components.molecules.SubSectionsCard
import eu.antoinepurnelle.ui.components.molecules.cardDecoration
import eu.antoinepurnelle.ui.components.organisms.ErrorView
import eu.antoinepurnelle.ui.components.organisms.LoadingView
import eu.antoinepurnelle.ui.components.organisms.model.SectionCardItemModel
import eu.antoinepurnelle.ui.components.organisms.model.SubSectionModel
import eu.antoinepurnelle.ui.theme.Dimens.Padding
import eu.antoinepurnelle.ui.theme.Dimens.Padding.SpacerDefault
import eu.antoinepurnelle.ui.theme.Dimens.Size
import eu.antoinepurnelle.ui.theme.colors
import jobapplication.composeapp.generated.resources.Res
import jobapplication.composeapp.generated.resources.ic_chat
import jobapplication.composeapp.generated.resources.ic_chevron_right
import jobapplication.composeapp.generated.resources.ic_email
import jobapplication.composeapp.generated.resources.ic_github
import jobapplication.composeapp.generated.resources.ic_linkedin
import jobapplication.composeapp.generated.resources.ic_phone
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MainScreen(
    pilot: Pilot,
    viewModel: MainScreenViewModel = koinViewModel(parameters = { parametersOf(pilot) }),
) {
    when (val state = viewModel.uiState.collectAsState().value) {
        is Error -> ErrorView(state.message)
        is Loading -> LoadingView()
        is Loaded -> MainView(state.data, viewModel)
    }
}

@Composable
private fun MainView(
    uiModel: MainUiModel,
    callback: MainCallback,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .statusBarsPadding()
        .padding(Padding.Screen),
) {
    HeaderView(uiModel.header)
    ExperienceView(uiModel.experiences, callback)
    ProjectsView(uiModel.projects)
    EducationView(uiModel.education)
    OtherView(uiModel.other)
    Spacer(modifier = Modifier.navigationBarsPadding())
}

@Composable
internal fun HeaderView(
    header: Header,
) = Column(
    modifier = Modifier.cardDecoration().fillMaxWidth(),
    horizontalAlignment = CenterHorizontally,
) {
    val context = getContext()

    header.pictureUrl?.let {
        UrlImage(
            url = it,
            contentDescription = "Profile picture", // TODO #9 i18l
            modifier = Modifier
                .size(Size.Avatar)
                .clip(RoundedCornerShapeDefault),
        )
    }

    VerticalSpacer()

    Text(text = header.name, style = typography.headlineSmall, color = colors.text.main)
    FlowRow(
        horizontalArrangement = spacedBy(SpacerDefault, alignment = CenterHorizontally),
        verticalArrangement = spacedBy(SpacerDefault),
    ) {
        header.location?.let {
            Text(text = header.location, style = typography.titleSmall, color = colors.text.main)
        }
        header.dateOfBirth?.let {
            if (header.location != null) {
                Text(text = "•", style = typography.titleSmall, color = colors.text.main) // TODO #9 i18l
            }
            Text(text = it, style = typography.titleSmall, color = colors.text.main)
        }
    }

    HorizontalDiv(modifier = Modifier.padding(vertical = SpacerDefault))

    Text(text = header.headline, style = typography.titleLarge, color = colors.text.main)
    if (header.mainSkills.isNotEmpty()) {
        VerticalSpacerSmall()
        PillsGroup(header.mainSkills)
    }

    HorizontalDiv(modifier = Modifier.padding(vertical = SpacerDefault))

    FlowRow(
        horizontalArrangement = spacedBy(SpacerDefault, alignment = CenterHorizontally),
        verticalArrangement = spacedBy(SpacerDefault),
        modifier = Modifier.fillMaxWidth(),
    ) {
        QuickAction(
            iconRes = Res.drawable.ic_phone,
            contentDescription = "Call ${header.phoneNumber}", // TODO #9 i18l
            onclick = { launch(LaunchType.Phone(header.phoneNumber), context) },
        )
        QuickAction(
            iconRes = Res.drawable.ic_chat,
            contentDescription = "Chat with ${header.phoneNumber}", // TODO #9 i18l
            onclick = { launch(LaunchType.Chat(header.phoneNumber), context) },
        )
        QuickAction(
            iconRes = Res.drawable.ic_email,
            contentDescription = "Email ${header.emailAddress}", // TODO #9 i18l
            onclick = { launch(LaunchType.Email(header.emailAddress), context) },
        )
        header.linkedIn?.let {
            QuickAction(
                iconRes = Res.drawable.ic_linkedin,
                contentDescription = "LinkedIn profile ${header.linkedIn}", // TODO #9 i18l
                onclick = { launch(LaunchType.Url(header.linkedIn), context) },
            )
        }

        header.github?.let {
            QuickAction(
                iconRes = Res.drawable.ic_github,
                contentDescription = "GitHub profile ${header.github}", // TODO #9 i18l
                onclick = { launch(LaunchType.Url(header.github), context) },
            )
        }
    }
}

@Composable
private fun ProjectsView(
    projects: List<SectionCardItemModel>,
) {
    val context = getContext()

    SectionCard(
        title = "Projects", // TODO #9 i18l
        items = projects,
        fallbackPictureUrl = projectFallbackPictureUrl,
        onItemClick = { it.url?.let { url -> launch(LaunchType.Url(url), context) } },
        itemTrailingIconRes = Res.drawable.ic_chevron_right,
    )
}

@Composable
private fun ExperienceView(
    experiences: List<SectionCardItemModel>,
    callback: MainCallback,
) = SectionCard(
    title = "Experience", // TODO #9 i18l
    items = experiences,
    fallbackPictureUrl = companyFallbackPictureUrl,
    onItemClick = { callback.onExperienceClick(it.id) },
    itemTrailingIconRes = Res.drawable.ic_chevron_right,
)

@Composable
private fun EducationView(
    educations: List<SubSectionModel>,
) = SubSectionsCard(
    subSections = educations,
    title = "Education", // TODO #9 i18l
    fallbackPictureUrl = educationFallbackPictureUrl,
)

@Composable
private fun OtherView(
    other: List<SectionCardItemModel>,
) = SectionCard(
    title = "Hobbies, Activities & Other", // TODO #9 i18l
    items = other,
)
