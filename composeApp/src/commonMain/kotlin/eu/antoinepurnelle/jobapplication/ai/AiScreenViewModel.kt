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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState.AiResponseState.Answer
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState.AiResponseState.ErrorMessage
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState.InputType
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.QuickPrompt
import eu.antoinepurnelle.jobapplication.domain.model.Result
import eu.antoinepurnelle.jobapplication.domain.usecase.PromptUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiScreenViewModel(
    private val prompt: PromptUseCase,
) : ViewModel(), AiCallback {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Sends the prompt to the AI and updates the UI state accordingly.
     */
    private fun onSendPrompt(prompt: String) {
        _uiState.value = AiUiState(
            prompt = prompt,
            response = AiUiState.AiResponseState.Loading,
        )
        viewModelScope.launch {
            when (val result = prompt(prompt)) {
                is Result.Success -> {
                    _uiState.value = AiUiState(
                        prompt = prompt,
                        response = Answer(result.data),
                    )
                }
                is Result.Error -> {
                    _uiState.value = AiUiState(
                        prompt = prompt,
                        response = ErrorMessage(result.error.toString()),
                    )
                }
            }
        }
    }

    /**
     * Handles the selection of a quick prompt. If the quick prompt requires additional input,
     * it updates the UI state to request that input. Otherwise, it sends the prompt directly.
     *
     * @param quickPrompt The selected quick prompt.
     */
    override fun onQuickPromptSelected(quickPrompt: QuickPrompt) {
        when (quickPrompt) {
            QuickPrompt.JobDescriptionMatch -> _uiState.value = _uiState.value.copy(inputType = InputType.Url)
            QuickPrompt.CustomPrompt -> _uiState.value = _uiState.value.copy(inputType = InputType.CustomPrompt)
            else -> onSendPrompt(quickPrompt.prompt)
        }
    }

    /**
     * Handles the submission of additional input for prompts that require it.
     * Constructs the full prompt and sends it to the AI.
     *
     * @param value The additional input provided by the user.
     * @param inputType The type of input provided (URL or custom prompt).
     */
    override fun onPromptInputSubmit(value: String, inputType: InputType) {
        val prompt = when (inputType) {
            InputType.CustomPrompt -> value
            InputType.Url -> "${QuickPrompt.JobDescriptionMatch.prompt}\n$value"
        }

        onSendPrompt(prompt)
        _uiState.value = _uiState.value.copy(inputType = null)
    }

    // TODO i18n
    enum class QuickPrompt(val title: String, val prompt: String) {
        ShortSummary("Short summary", "Write a short summary for this resume in 2-3 short structured paragraphs."),
        Summary(
            "Full summary",
            "Write a summary for this resume. Highlight the career path, the skills and what we can expect from the applicant. " +
                "Structure your answer and use around 500 words.",
        ),
        Skills("Skills", "Extract the skills listed in this resume."),
        Contact("Contact information", "Extract the contact information from this resume."),
        Education("Education", "List the educational background present in this resume."),
        SalaryExpectation(
            "Salary expectation",
            "Based on this resume, what would be a reasonable salary expectation for this applicant? " +
                "Express your answer in a monthly gross + monthly and annual benefits " +
                "(meal vouchers, company car, insurances, net allowance, devices, Internet & phone subscriptions, 13th month, bonus " +
                "and other usual benefits). Take the location and experience in account",
        ),
        JobDescriptionMatch(
            "Match with a job description",
            "How well does this resume match the following job description? " +
                "Be specific about key aspects of the job description that match the resume. " +
                "Structure your answer and use around 300 words.\nJob description URL:",
        ),
        CustomPrompt("Custom prompt", ""),
    }

    data class AiUiState(
        val prompt: String? = null,
        val inputType: InputType? = null,
        val response: AiResponseState = AiResponseState.Idle,
    ) {
        sealed interface AiResponseState {
            data object Idle : AiResponseState
            data object Loading : AiResponseState
            data class Answer(val response: String) : AiResponseState
            data class ErrorMessage(val message: String) : AiResponseState
        }

        enum class InputType {
            Url,
            CustomPrompt,
        }
    }
}

interface AiCallback {
    fun onQuickPromptSelected(quickPrompt: QuickPrompt)
    fun onPromptInputSubmit(value: String, inputType: InputType)
}
