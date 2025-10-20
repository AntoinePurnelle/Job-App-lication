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

import eu.antoinepurnelle.jobapplication.Pilot
import eu.antoinepurnelle.jobapplication.Route.ExperienceDetailRoute
import eu.antoinepurnelle.jobapplication.domain.model.Failure
import eu.antoinepurnelle.jobapplication.domain.model.Result
import eu.antoinepurnelle.jobapplication.domain.usecase.FetchMainPageUseCase
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel.MainUiState
import eu.antoinepurnelle.jobapplication.mainscreen.MainScreenViewModel.MainUiState.Loaded
import eu.antoinepurnelle.jobapplication.mainscreen.model.MainUiModel
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
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
class MainScreenViewModelTest {

    @MockK private lateinit var fetchMainPageUseCase: FetchMainPageUseCase
    @MockK private lateinit var pilot: Pilot
    @MockK private lateinit var uiModel: MainUiModel

    private lateinit var viewModel: MainScreenViewModel
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setup() {
        fetchMainPageUseCase = mockk()
        pilot = mockk()
        uiModel = mockk()

        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        confirmVerified(fetchMainPageUseCase, pilot)
        Dispatchers.resetMain()
    }

    @Test
    fun `init - fetch success - should call fetchMainPageUseCase and set Loaded UiState`() {
        // GIVEN
        // THIS DATA
        val fetchResult = Result.Success(uiModel)
        val expectedUiState = Loaded(uiModel)

        // THIS BEHAVIOR
        coEvery { fetchMainPageUseCase<MainUiModel>() } returns fetchResult

        // WHEN
        viewModel = MainScreenViewModel(pilot, fetchMainPageUseCase)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify {
            fetchMainPageUseCase<MainUiModel>()
        }

        // THIS SHOULD BE
        viewModel shouldHaveState expectedUiState
    }

    @Test
    fun `init - fetch failed - should call fetchMainPageUseCase and set Error UiState`() {
        // GIVEN
        // THIS DATA
        val failureMessage = "failure message"
        val error = object : Failure {
            override fun toString(): String = failureMessage
        }
        val fetchResult = Result.Error(error)
        val expectedUiState = MainUiState.Error(failureMessage)

        // THIS BEHAVIOR
        coEvery { fetchMainPageUseCase<MainUiModel>() } returns fetchResult

        // WHEN
        viewModel = MainScreenViewModel(pilot, fetchMainPageUseCase)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerifySequence {
            fetchMainPageUseCase<MainUiModel>()
        }

        // THIS SHOULD BE
        viewModel shouldHaveState expectedUiState
    }

    private fun createViewModelAndDisregardInit() {
        val fetchResult = Result.Success(uiModel)
        coEvery { fetchMainPageUseCase<MainUiModel>() } returns fetchResult

        viewModel = MainScreenViewModel(pilot, fetchMainPageUseCase)

        clearAllMocks()
    }

    @Test
    fun `onExperienceClick - should call pilot navigateTo with ExperienceDetailRoute`() {
        // GIVEN
        // THIS SETUP
        createViewModelAndDisregardInit()

        // THIS DATA
        val id = "experience-id"

        // THIS BEHAVIOR
        coEvery { pilot.navigateTo(ExperienceDetailRoute(id)) } just Runs

        // WHEN
        viewModel.onExperienceClick(id)

        // THEN
        // THIS SHOULD HAVE HAPPENED
        verify {
            pilot.navigateTo(ExperienceDetailRoute(id))
        }
    }

    @Test
    fun `onShareClick - when in Loaded state - should set showBottomSheet to true`() {
        // GIVEN
        // THIS SETUP
        createViewModelAndDisregardInit()

        // THIS DATA
        val expectedState = Loaded(uiModel, showBottomSheet = true)

        // WHEN
        viewModel.onShareClick()

        // THEN
        // THIS SHOULD BE
        viewModel shouldHaveState expectedState
    }

    @Test
    fun `onBottomSheetDismiss - when in Loaded state - should set showBottomSheet to false`() {
        // GIVEN
        // THIS SETUP
        createViewModelAndDisregardInit()
        viewModel.onShareClick()

        // THIS DATA
        val expectedState = Loaded(uiModel, showBottomSheet = false)

        // WHEN
        viewModel.onBottomSheetDismiss()

        // THEN
        // THIS SHOULD BE
        viewModel shouldHaveState expectedState
    }

    private infix fun MainScreenViewModel.shouldHaveState(state: MainUiState) {
        this.uiState.value shouldBe state
    }

}
