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

import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState.AiResponseState.Answer
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState.AiResponseState.ErrorMessage
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.AiUiState.InputType
import eu.antoinepurnelle.jobapplication.ai.AiScreenViewModel.QuickPrompt
import eu.antoinepurnelle.jobapplication.domain.model.Failure
import eu.antoinepurnelle.jobapplication.domain.model.NetworkError
import eu.antoinepurnelle.jobapplication.domain.model.Result
import eu.antoinepurnelle.jobapplication.domain.usecase.PromptUseCase
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnitParamsRunner::class)
class AiScreenViewModelTest {

    @MockK private lateinit var promptUseCase: PromptUseCase

    private lateinit var viewModel: AiScreenViewModel
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private val aiAnswer = "This is a sample AI answer."

    @BeforeTest
    fun setup() {
        promptUseCase = mockk()

        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        confirmVerified(promptUseCase)
        Dispatchers.resetMain()
    }

    @Test
    fun `init - sets initial UiState`() {
        // GIVEN
        // THIS DATA
        val expectedState = AiUiState()

        // WHEN
        viewModel = AiScreenViewModel(promptUseCase)

        // THEN
        // THIS SHOULD BE
        viewModel shouldHaveState expectedState
    }

    private fun createViewModelAndDisregardInit() {
        viewModel = AiScreenViewModel(promptUseCase)
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun getQuickPromptParams(): List<Array<Any>> = listOf(
        QuickPrompt.ShortSummary,
        QuickPrompt.Summary,
        QuickPrompt.Skills,
        QuickPrompt.Contact,
        QuickPrompt.Education,
        QuickPrompt.SalaryExpectation,
    ).flatMap {
        listOf(arrayOf(it, Result.Success(aiAnswer)), arrayOf(it, Result.Error(NetworkError.UNKNOWN)))
    }

    @Test
    @Parameters(method = "getQuickPromptParams")
    fun `onQuickPromptSelected - standard quick prompts - should send prompt and update uiState`(
        quickPrompt: QuickPrompt,
        promptResult: Result<String, Failure>,
    ) {
        // GIVEN
        // THIS SETUP
        createViewModelAndDisregardInit()

        // THIS DATA
        val response = when (promptResult) {
            is Result.Success -> Answer(promptResult.data)
            is Result.Error -> ErrorMessage(promptResult.error.toString())
        }
        val expectedState = AiUiState(prompt = quickPrompt.prompt, inputType = null, response = response)

        // THIS BEHAVIOR
        coEvery { promptUseCase(quickPrompt.prompt) } returns promptResult

        // WHEN
        viewModel.onQuickPromptSelected(quickPrompt)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify {
            promptUseCase(quickPrompt.prompt)
        }
        // THIS SHOULD BE
        viewModel shouldHaveState expectedState
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun getInputPromptParams(): List<QuickPrompt> = listOf(
        QuickPrompt.JobDescriptionMatch,
        QuickPrompt.CustomPrompt,
    )

    @Test
    @Parameters(method = "getInputPromptParams")
    fun `onQuickPromptSelected - prompt with input - should update UiState to show input`(quickPrompt: QuickPrompt) {
        // GIVEN
        // THIS SETUP
        createViewModelAndDisregardInit()

        // THIS DATA
        val expectedState = when (quickPrompt) {
            QuickPrompt.JobDescriptionMatch -> AiUiState(inputType = InputType.Url)
            QuickPrompt.CustomPrompt -> AiUiState(inputType = InputType.CustomPrompt)
            else -> throw IllegalArgumentException("Invalid quick prompt for this test: $quickPrompt")
        }

        // WHEN
        viewModel.onQuickPromptSelected(quickPrompt)

        // THEN
        // THIS SHOULD BE
        viewModel shouldHaveState expectedState
    }

    @Suppress("unused", "UnusedPrivateMember")
    private fun getInputPromptWithResultsParams(): List<Array<Any>> = getInputPromptParams().flatMap {
        listOf(arrayOf(it, Result.Success(aiAnswer)), arrayOf(it, Result.Error(NetworkError.UNKNOWN)))
    }

    @Test
    @Parameters(method = "getInputPromptWithResultsParams")
    fun `onPromptInputSubmit - should send constructed prompt and update uiState`(
        quickPrompt: QuickPrompt,
        promptResult: Result<String, Failure>,
    ) {
        // GIVEN
        // THIS SETUP
        createViewModelAndDisregardInit()

        val inputValue = "Additional input for the prompt."
        val constructedPrompt = when (quickPrompt) {
            QuickPrompt.JobDescriptionMatch -> "${quickPrompt.prompt}\n$inputValue"
            QuickPrompt.CustomPrompt -> inputValue
            else -> throw IllegalArgumentException("Invalid quick prompt for this test: $quickPrompt")
        }

        // THIS DATA
        val response = when (promptResult) {
            is Result.Success -> Answer(promptResult.data)
            is Result.Error -> ErrorMessage(promptResult.error.toString())
        }
        val expectedState = AiUiState(prompt = constructedPrompt, inputType = null, response = response)

        // THIS BEHAVIOR
        coEvery { promptUseCase(constructedPrompt) } returns promptResult

        // WHEN
        val inputType = when (quickPrompt) {
            QuickPrompt.JobDescriptionMatch -> InputType.Url
            QuickPrompt.CustomPrompt -> InputType.CustomPrompt
            else -> throw IllegalArgumentException("Invalid quick prompt for this test: $quickPrompt")
        }
        viewModel.onPromptInputSubmit(inputValue, inputType)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify {
            promptUseCase(constructedPrompt)
        }
        // THIS SHOULD BE
        viewModel shouldHaveState expectedState
    }

    private infix fun AiScreenViewModel.shouldHaveState(state: AiUiState) {
        this.uiState.value shouldBe state
    }

}
