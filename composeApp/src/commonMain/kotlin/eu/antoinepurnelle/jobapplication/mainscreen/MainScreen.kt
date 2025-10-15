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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel.MainUiState
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel.MainUiState.Loaded
import eu.antoinepurnelle.jobapplication.mainscreen.model.MainPageUiModel.Header
import eu.antoinepurnelle.jobapplication.util.LaunchType
import eu.antoinepurnelle.jobapplication.util.getContext
import eu.antoinepurnelle.jobapplication.util.launch
import eu.antoinepurnelle.ui.components.atoms.RoundedCornerShapeDefault
import eu.antoinepurnelle.ui.components.atoms.UrlImage
import eu.antoinepurnelle.ui.components.atoms.gradientBackground
import eu.antoinepurnelle.ui.components.molecules.QuickAction
import eu.antoinepurnelle.ui.components.molecules.SectionCard
import eu.antoinepurnelle.ui.components.molecules.SubSectionsCard
import eu.antoinepurnelle.ui.components.molecules.cardDecoration
import eu.antoinepurnelle.ui.components.organisms.model.SectionCardItemModel
import eu.antoinepurnelle.ui.components.organisms.model.SubSectionModel
import eu.antoinepurnelle.ui.theme.Dimens.Padding
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

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = koinViewModel(),
) {
    when (val state = viewModel.uiState.collectAsState().value) {
        is MainUiState.Error -> ErrorView(state.message)
        is MainUiState.Loading -> LoadingView()
        is Loaded -> MainView(uiState = state)
    }
}

@Composable
private fun LoadingView() = Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = CenterHorizontally,
    modifier = Modifier
        .fillMaxSize()
        .gradientBackground(),
) {
    CircularProgressIndicator()
    Spacer(modifier = Modifier.height(Padding.SpacerLarge))
    Text(text = "Loading...", style = typography.bodyLarge, color = colors.text.main)
}

@Composable
private fun ErrorView(message: String) = Box(
    modifier = Modifier
        .fillMaxSize()
        .gradientBackground(),
    contentAlignment = Alignment.Center,
) {
    Text(text = "An error occurred: $message", style = typography.bodyLarge, color = colors.text.main)
}

@Composable
private fun MainView(
    uiState: Loaded,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .gradientBackground()
        .verticalScroll(rememberScrollState())
        .statusBarsPadding()
        .padding(Padding.Screen),
) {
    HeaderView(uiState.data.header)
    ExperienceView(uiState.data.experiences)
    EducationView(uiState.data.education)
    Spacer(modifier = Modifier.navigationBarsPadding())
}

@Composable
internal fun HeaderView(
    header: Header,
) = Column(
    modifier = Modifier.cardDecoration().fillMaxWidth(),
    verticalArrangement = spacedBy(Padding.SpacerDefault),
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
    Text(text = header.name, style = typography.headlineSmall, color = colors.text.main)
    Text(text = header.headline, style = typography.titleLarge, color = colors.text.main)
    header.location?.let { Text(text = header.location, style = typography.titleMedium, color = colors.text.main) }

    FlowRow(
        horizontalArrangement = spacedBy(Padding.SpacerDefault, alignment = CenterHorizontally),
        verticalArrangement = spacedBy(Padding.SpacerDefault),
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
private fun ExperienceView(
    experiences: List<SectionCardItemModel>,
) = SectionCard(
    title = "Experience", // TODO #9 i18l
    items = experiences,
    fallbackPictureUrl = companyFallbackPictureUrl,
    onItemClick = {}, // TODO #14
    itemTrailingIconRes = Res.drawable.ic_chevron_right,
)

@Composable
private fun EducationView(
    educations: List<SubSectionModel>,
) = SubSectionsCard(
    subsections = educations,
    title = "Education", // TODO #9 i18l
    fallbackPictureUrl = educationFallbackPictureUrl,
)

@Suppress("MaxLineLength", "ktlint:standard:max-line-length")
private val companyFallbackPictureUrl =
    "https://lh3.googleusercontent.com/pw/AP1GczPflsoWG1nGNDb9eZTUWjTqn5T7b5bawH56HFUCccparCrBaLerr6XcptYZJjQbOSM-pRbA-criKuzPdgbo8tB9P7HT54oZdhnyUILOzBm8ogdYE-PN_zn1GE1ABHYIotTzr_Z4IDefMIUbFWOzBI3iWQ=w512-h512-s-no"
@Suppress("MaxLineLength", "ktlint:standard:max-line-length")
private val educationFallbackPictureUrl =
    "https://lh3.googleusercontent.com/pw/AP1GczNFcByjtiixp31Mzd5cK9HFC7Z-RLlSEpXD2FXWQd8LtGnmicdCpA2CAyUfRsuIvKGkhmxmAlug8HN-nGTd10yDhwXGieso7tnEPpR_yboOUImOIigkbnKyxVffp1y1w_PCatCtv6rM0GH-iNH_eVfhPA=w512-h512-s-no"
