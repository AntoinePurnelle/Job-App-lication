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

import eu.antoinepurnelle.jobapplication.Pilot
import eu.antoinepurnelle.jobapplication.domain.model.Failure
import eu.antoinepurnelle.jobapplication.domain.model.Result
import eu.antoinepurnelle.jobapplication.domain.usecase.GetExperiencePageUseCase
import eu.antoinepurnelle.jobapplication.experience.ExperienceViewModel.ExperienceUiState
import eu.antoinepurnelle.jobapplication.experience.model.ExperienceUiModel
import eu.antoinepurnelle.jobapplication.mainscreen.model.MainUiModel
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExperienceViewModelTest {

    @MockK private lateinit var getExperiencePageUseCase: GetExperiencePageUseCase
    @MockK private lateinit var pilot: Pilot

    private lateinit var viewModel: ExperienceViewModel
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        getExperiencePageUseCase = mockk()
        pilot = mockk()

        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        confirmVerified(getExperiencePageUseCase, pilot)
        Dispatchers.resetMain()
    }

    @Test
    fun `init - get success - should call getExperiencePageUseCase and set Loaded UiState`() {
        // GIVEN
        // THIS DATA
        val experienceId = "experienceId"
        val uiModel = mockk<ExperienceUiModel>()
        val getResult = Result.Success(uiModel)
        val expectedUiState = ExperienceUiState.Loaded(uiModel)

        // THIS BEHAVIOR
        coEvery { getExperiencePageUseCase<ExperienceUiModel>(experienceId) } returns getResult

        // WHEN
        viewModel = ExperienceViewModel(
            id = experienceId,
            pilot = pilot,
            getExperiencePage = getExperiencePageUseCase,
        )

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify {
            getExperiencePageUseCase<MainUiModel>(experienceId)
        }

        // THIS SHOULD BE
        viewModel shouldHaveState expectedUiState
    }

    @Test
    fun `init - get failed - should call getExperiencePageUseCase and set Error UiState`() {
        // GIVEN
        // THIS DATA
        val experienceId = "experienceId"
        val failureMessage = "failure message"
        val error = object : Failure {
            override fun toString(): String = failureMessage
        }
        val getResult = Result.Error(error)
        val expectedUiState = ExperienceUiState.Error(failureMessage)

        // THIS BEHAVIOR
        coEvery { getExperiencePageUseCase<ExperienceUiModel>(experienceId) } returns getResult

        // WHEN
        viewModel = ExperienceViewModel(
            id = experienceId,
            pilot = pilot,
            getExperiencePage = getExperiencePageUseCase,
        )

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify {
            getExperiencePageUseCase<ExperienceUiModel>(experienceId)
        }

        // THIS SHOULD BE
        viewModel shouldHaveState expectedUiState
    }

    private fun createViewModelAndDisregardInit() {
        val experienceId = "experienceId"
        val uiModel = mockk<ExperienceUiModel>()
        val getResult = Result.Success(uiModel)
        coEvery { getExperiencePageUseCase<ExperienceUiModel>(experienceId) } returns getResult

        viewModel = ExperienceViewModel(
            id = experienceId,
            pilot = pilot,
            getExperiencePage = getExperiencePageUseCase,
        )

        clearAllMocks()
    }

    @Test
    fun `onBackPressed - should call pilot back`() {
        // GIVEN
        // THIS SETUP
        createViewModelAndDisregardInit()

        // THIS BEHAVIOR
        every { pilot.back() } just Runs

        // WHEN
        viewModel.onBackPressed()

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verify {
            pilot.back()
        }
    }

    private infix fun ExperienceViewModel.shouldHaveState(state: ExperienceUiState) {
        this.uiState.value shouldBe state
    }
}
