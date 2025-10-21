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

package eu.antoinepurnelle.jobapplication.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState.AiResponseState.Answer
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState.AiResponseState.ErrorMessage
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState.AiResponseState.Idle
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState.AiResponseState.Loading
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState.InputType
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.QuickPrompt
import eu.antoinepurnelle.ui.components.atoms.MDText
import eu.antoinepurnelle.ui.components.atoms.RoundedCornerShapeDefault
import eu.antoinepurnelle.ui.components.atoms.VerticalSpacer
import eu.antoinepurnelle.ui.components.atoms.VerticalSpacerLarge
import eu.antoinepurnelle.ui.components.molecules.cardDecoration
import eu.antoinepurnelle.ui.components.organisms.LoadingView
import eu.antoinepurnelle.ui.theme.Dimens.Padding
import eu.antoinepurnelle.ui.theme.Dimens.Padding.SpacerDefault
import eu.antoinepurnelle.ui.theme.Dimens.Size
import eu.antoinepurnelle.ui.theme.Dimens.Size.BorderWidth
import eu.antoinepurnelle.ui.theme.colors
import jobapplication.composeapp.generated.resources.Res
import jobapplication.composeapp.generated.resources.ic_arrow_left
import jobapplication.composeapp.generated.resources.ic_sparkle
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AiScreen(
    onBack: () -> Unit,
    viewModel: AiScreenViewModel = koinViewModel(),
) {
    AiContentView(
        uiState = viewModel.uiState.collectAsState().value,
        callback = viewModel,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiContentView(
    uiState: AiUiState,
    callback: AiCallback,
    onBack: () -> Unit,
) = Scaffold(
    containerColor = Transparent,
    contentWindowInsets = WindowInsets(),
    topBar = {
        TopAppBar(
            title = { Text("AI Assistant", style = typography.titleMedium, color = colors.text.main) }, // TODO i18n
            colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.decor.gradientStart),
            navigationIcon = {
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
            },
        )
    },
) { padding ->
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(horizontal = Padding.Screen)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Bottom,
    ) {
        VerticalSpacer()
        uiState.prompt?.let { prompt ->
            Text(
                prompt,
                style = typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = colors.text.secondary,
            )
            VerticalSpacerLarge()
        }
        when (uiState.response) {
            is Idle -> PromptsView(uiState.inputType, callback)
            is Loading -> LoadingView()
            is Answer -> AiAnswerView(uiState.response.response, uiState.inputType, callback)
            is ErrorMessage -> {
                Box(
                    Modifier.fillMaxSize(),
                ) {
                    Text(
                        text = "An error occurred: ${uiState.response.message}", // TODO i18n
                        style = typography.bodyLarge,
                        color = colors.text.main,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
        VerticalSpacer(modifier = Modifier.navigationBarsPadding())
    }
}

@Composable
private fun PromptsView(
    inputType: InputType?,
    callback: AiCallback,
) = Column(
    verticalArrangement = Arrangement.spacedBy(SpacerDefault),
    horizontalAlignment = CenterHorizontally,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(SpacerDefault, alignment = CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(SpacerDefault),
        modifier = Modifier.fillMaxWidth(),
    ) {
        QuickPrompt.entries.forEach { quickPrompt ->
            PromptActionView(
                title = quickPrompt.title,
                onClick = { callback.onQuickPromptSelected(quickPrompt) },
            )
        }
    }

    PromptInput(
        inputType = inputType,
        onSubmit = callback::onPromptInputSubmit,
    )
}

@Composable
private fun PromptInput(
    inputType: InputType?,
    onSubmit: (String, InputType) -> Unit,
) = AnimatedVisibility(
    visible = inputType != null,
) {
    var value by remember { mutableStateOf(TextFieldValue()) }
    val onSubmit: () -> Unit = {
        inputType?.let {
            onSubmit(value.text, it)
            value = TextFieldValue()
        }
    }

    TextField(
        value = value,
        modifier = Modifier.fillMaxWidth(),
        onValueChange = { value = it },
        placeholder = { Text("Paste the URL of the Job Description", style = typography.bodyMedium, color = colors.text.secondary) }, // TODO i18n
        shape = RoundedCornerShapeDefault,
        maxLines = 4,
        trailingIcon = {
            Image(
                painter = painterResource(Res.drawable.ic_sparkle),
                contentDescription = "Submit URL", // TODO i18n
                modifier = Modifier
                    .size(Size.IconDefault)
                    .clip(RoundedCornerShapeDefault)
                    .clickable(onClick = onSubmit),
            )
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = colors.text.main,
            unfocusedTextColor = colors.text.main,
            cursorColor = colors.accent,
            focusedIndicatorColor = Transparent,
            unfocusedIndicatorColor = Transparent,
            focusedContainerColor = colors.decor.inputBackground,
            unfocusedContainerColor = colors.decor.inputBackground,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (inputType == InputType.Url) KeyboardType.Uri else KeyboardType.Text,
            imeAction = ImeAction.Go,
        ),
        keyboardActions = KeyboardActions(onGo = { onSubmit() }),
    )
}

@Composable
private fun PromptActionView(
    title: String,
    onClick: () -> Unit,
) = Text(
    text = title,
    style = typography.bodyLarge,
    color = colors.text.main,
    modifier = Modifier
        .clip(RoundedCornerShapeDefault)
        .clickable(onClick = onClick)
        .border(width = BorderWidth, color = colors.decor.border, shape = RoundedCornerShapeDefault)
        .background(color = colors.decor.pillBackground, shape = RoundedCornerShapeDefault)
        .defaultMinSize(minHeight = 20.dp)
        .padding(horizontal = 8.dp, vertical = 6.dp),
)

@Composable
private fun AiAnswerView(
    answer: String,
    inputType: InputType?,
    callback: AiCallback,
) {
    MDText(
        content = answer,
        modifier = Modifier.cardDecoration(),
    )
    VerticalSpacerLarge()

    PromptsView(inputType, callback)
}
