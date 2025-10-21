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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import eu.antoinepurnelle.jobapplication.Route
import eu.antoinepurnelle.jobapplication.Route.AiRoute
import eu.antoinepurnelle.jobapplication.Route.ExperienceDetailRoute
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel.MainUiState.Error
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel.MainUiState.Loaded
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel.MainUiState.Loading
import eu.antoinepurnelle.jobapplication.mainscreen.model.MainUiModel
import eu.antoinepurnelle.jobapplication.mainscreen.model.MainUiModel.ShareMenuItem
import eu.antoinepurnelle.jobapplication.util.LaunchType
import eu.antoinepurnelle.jobapplication.util.companyFallbackPictureUrl
import eu.antoinepurnelle.jobapplication.util.educationFallbackPictureUrl
import eu.antoinepurnelle.jobapplication.util.getContext
import eu.antoinepurnelle.jobapplication.util.getShareIconRes
import eu.antoinepurnelle.jobapplication.util.launch
import eu.antoinepurnelle.jobapplication.util.projectFallbackPictureUrl
import eu.antoinepurnelle.jobapplication.util.shareUrl
import eu.antoinepurnelle.ui.components.atoms.HorizontalDiv
import eu.antoinepurnelle.ui.components.atoms.HorizontalSpacer
import eu.antoinepurnelle.ui.components.atoms.RoundedCornerShapeDefault
import eu.antoinepurnelle.ui.components.atoms.UrlImage
import eu.antoinepurnelle.ui.components.atoms.VerticalSpacer
import eu.antoinepurnelle.ui.components.atoms.VerticalSpacerSmall
import eu.antoinepurnelle.ui.components.molecules.PillsGroup
import eu.antoinepurnelle.ui.components.molecules.QuickAction
import eu.antoinepurnelle.ui.components.molecules.SectionCard
import eu.antoinepurnelle.ui.components.molecules.SubSectionsCard
import eu.antoinepurnelle.ui.components.molecules.cardDecoration
import eu.antoinepurnelle.ui.components.organisms.BottomSheet
import eu.antoinepurnelle.ui.components.organisms.ErrorView
import eu.antoinepurnelle.ui.components.organisms.LoadingView
import eu.antoinepurnelle.ui.components.organisms.model.SectionCardItemModel
import eu.antoinepurnelle.ui.components.organisms.model.SubSectionModel
import eu.antoinepurnelle.ui.theme.Dimens.Padding
import eu.antoinepurnelle.ui.theme.Dimens.Padding.SpacerDefault
import eu.antoinepurnelle.ui.theme.Dimens.Padding.SpacerLarge
import eu.antoinepurnelle.ui.theme.Dimens.Padding.SpacerSmall
import eu.antoinepurnelle.ui.theme.Dimens.Size
import eu.antoinepurnelle.ui.theme.colors
import jobapplication.composeapp.generated.resources.Res
import jobapplication.composeapp.generated.resources.ic_chat
import jobapplication.composeapp.generated.resources.ic_chevron_right
import jobapplication.composeapp.generated.resources.ic_email
import jobapplication.composeapp.generated.resources.ic_github
import jobapplication.composeapp.generated.resources.ic_linkedin
import jobapplication.composeapp.generated.resources.ic_phone
import jobapplication.composeapp.generated.resources.ic_sparkle
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    onNavigate: (Route) -> Unit,
    viewModel: MainScreenViewModel = koinViewModel(),
) {
    when (val state = viewModel.uiState.collectAsState().value) {
        is Error -> ErrorView(state.message)
        is Loading -> LoadingView()
        is Loaded -> MainView(state, onNavigate, viewModel)
    }
}

@Composable
private fun MainView(
    uiState: Loaded,
    onNavigate: (Route) -> Unit,
    callback: MainCallback,
) {
    val scrollState = rememberScrollState()
    var previousScroll by remember { mutableStateOf(0) }
    var fabVisible by remember { mutableStateOf(true) }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }.collect { currentScroll ->
            fabVisible = currentScroll <= previousScroll || currentScroll <= 0
            previousScroll = currentScroll
        }
    }

    Scaffold(
        containerColor = Transparent,
        floatingActionButton = { Fab(fabVisible, onNavigate) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(Padding.Screen),
        ) {
            HeaderView(uiState.uiModel, callback)
            ExperienceView(uiState.uiModel.experiences, onNavigate)
            ProjectsView(uiState.uiModel.projects)
            EducationView(uiState.uiModel.education)
            OtherView(uiState.uiModel.other)
            Spacer(modifier = Modifier.navigationBarsPadding())
        }

        if (uiState.showBottomSheet) {
            ShareBottomSheet(uiState.uiModel.shareMenuItems, callback)
        }
    }
}

@Composable
private fun Fab(
    fabVisible: Boolean,
    onNavigate: (Route) -> Unit,
) = AnimatedVisibility(
    visible = fabVisible,
    enter = fadeIn() + scaleIn(),
    exit = fadeOut() + scaleOut(),
) {
    val shape = RoundedCornerShapeDefault
    val iconSize = Size.IconDefault
    val padding = SpacerDefault
    val elevation = Size.ElevationFab
    val totalSize = iconSize + padding * 2 + elevation * 2
    Box(
        modifier = Modifier.defaultMinSize(totalSize, totalSize),
        contentAlignment = Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_sparkle),
            contentDescription = "AI", // TODO #9 i18l
            modifier = Modifier
                .dropShadow(
                    shape = shape,
                    shadow = Shadow(
                        color = colors.decor.card.shadow,
                        radius = elevation,
                        offset = DpOffset(x = 0.dp, y = 4.dp),
                    ),
                )
                .background(colors.decor.buttonBackground, shape)
                .clip(shape)
                .clickable { onNavigate(AiRoute) }
                .padding(padding)
                .size(iconSize),
        )
    }
}

@Composable
private fun ShareBottomSheet(
    shareMenuItems: List<ShareMenuItem>,
    callback: MainCallback,
) = BottomSheet(
    onDismissRequest = callback::onBottomSheetDismiss,
) {
    shareMenuItems.forEach { item: ShareMenuItem ->
        val context = getContext()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    shareUrl(item.url, context)
                    callback.onBottomSheetDismiss()
                }
                .padding(vertical = SpacerSmall, horizontal = SpacerLarge),
        ) {
            val iconModifier = Modifier.size(Size.IconDefault)
            if (item.pictureUrl != null) {
                UrlImage(
                    url = item.pictureUrl,
                    contentDescription = item.title,
                    modifier = iconModifier,
                )
            } else {
                Image(
                    painter = painterResource(getShareIconRes()),
                    contentDescription = item.title,
                    colorFilter = ColorFilter.tint(colors.text.main),
                    modifier = iconModifier,
                )
            }

            HorizontalSpacer()

            Text(
                text = item.title,
                style = typography.titleMedium,
                color = colors.text.main,
            )
        }
    }
}

@Composable
internal fun HeaderView(
    uiModel: MainUiModel,
    callback: MainCallback,
) = Column(
    modifier = Modifier.cardDecoration().fillMaxWidth(),
    horizontalAlignment = CenterHorizontally,
) {
    val header = uiModel.header

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
        modifier = Modifier.fillMaxWidth(),
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
        val uriHandler = LocalUriHandler.current
        QuickAction(
            iconRes = Res.drawable.ic_phone,
            contentDescription = "Call ${header.phoneNumber}", // TODO #9 i18l
            onclick = { uriHandler.launch(LaunchType.Phone(header.phoneNumber)) },
        )
        QuickAction(
            iconRes = Res.drawable.ic_chat,
            contentDescription = "Chat with ${header.phoneNumber}", // TODO #9 i18l
            onclick = { uriHandler.launch(LaunchType.Chat(header.phoneNumber)) },
        )
        QuickAction(
            iconRes = Res.drawable.ic_email,
            contentDescription = "Email ${header.emailAddress}", // TODO #9 i18l
            onclick = { uriHandler.launch(LaunchType.Email(header.emailAddress)) },
        )
        header.linkedIn?.let {
            QuickAction(
                iconRes = Res.drawable.ic_linkedin,
                contentDescription = "LinkedIn profile ${header.linkedIn}", // TODO #9 i18l
                onclick = {
                    uriHandler.launch(LaunchType.Url(header.linkedIn))
                },
            )
        }

        header.github?.let {
            QuickAction(
                iconRes = Res.drawable.ic_github,
                contentDescription = "GitHub profile ${header.github}", // TODO #9 i18l
                onclick = { uriHandler.launch(LaunchType.Url(header.github)) },
            )
        }

        if (uiModel.shareMenuItems.isNotEmpty()) {
            QuickAction(
                iconRes = getShareIconRes(),
                contentDescription = "Share", // TODO #9 i18l
                onclick = { callback.onShareClick() },
            )
        }
    }
}

@Composable
private fun ProjectsView(
    projects: List<SectionCardItemModel>,
) {
    val uriHandler = LocalUriHandler.current

    SectionCard(
        title = "Projects", // TODO #9 i18l
        items = projects,
        fallbackPictureUrl = projectFallbackPictureUrl,
        onItemClick = { it.url?.let { url -> uriHandler.launch(LaunchType.Url(url)) } },
        itemTrailingIconRes = Res.drawable.ic_chevron_right,
    )
}

@Composable
private fun ExperienceView(
    experiences: List<SectionCardItemModel>,
    onNavigate: (Route) -> Unit,
) = SectionCard(
    title = "Experience", // TODO #9 i18l
    items = experiences,
    fallbackPictureUrl = companyFallbackPictureUrl,
    onItemClick = { onNavigate(ExperienceDetailRoute(it.id)) },
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
