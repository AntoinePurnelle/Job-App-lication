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

package eu.antoinepurnelle.jobapplication.domain.usecase

import eu.antoinepurnelle.jobapplication.domain.model.Failure
import eu.antoinepurnelle.jobapplication.domain.model.Result
import eu.antoinepurnelle.jobapplication.domain.model.Resume
import eu.antoinepurnelle.jobapplication.domain.model.UiModel
import eu.antoinepurnelle.jobapplication.domain.repository.ResumeRepository
import eu.antoinepurnelle.jobapplication.domain.transformer.MainPageUiTransformer
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class FetchMainPageUseCaseTest {

    @MockK private lateinit var repository: ResumeRepository
    @MockK private lateinit var transformer: MainPageUiTransformer

    private lateinit var useCase: FetchMainPageUseCase

    @BeforeTest
    fun setup() {
        repository = mockk()
        transformer = mockk()

        useCase = FetchMainPageUseCaseImpl(repository, transformer)
    }

    @AfterTest
    fun tearDown() {
        confirmVerified(repository, transformer)
    }

    @Test
    fun `invoke - repo and transform success - should return success`(): Unit = runTest {
        // GIVEN
        // THIS DATA
        val resume = mockk<Resume>()
        val repoResult = Result.Success(resume)
        val uiModel = mockk<UiModel>()
        val expected = Result.Success(uiModel)

        // THIS BEHAVIOR
        coEvery { repository.getResume() } returns repoResult
        every { transformer.transform<UiModel>(repoResult.data) } returns uiModel

        // WHEN
        val result = useCase<UiModel>()

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerifySequence {
            repository.getResume()
            transformer.transform<UiModel>(repoResult.data)
        }

        // THIS SHOULD BE
        result shouldBe expected
    }

    @Test
    fun `invoke - repo error - should return error`(): Unit = runTest {
        // GIVEN
        // THIS DATA
        val repoError = mockk<Failure>()
        val repoResult: Result<Resume, Failure> = Result.Error(repoError)
        val expected: Result<UiModel, Failure> = Result.Error(repoError)

        // THIS BEHAVIOR
        coEvery { repository.getResume() } returns repoResult

        // WHEN
        val result = useCase<UiModel>()

        // THEN
        // THIS SHOULD HAVE HAPPENED
        coVerify {
            repository.getResume()
        }

        // THIS SHOULD BE
        result shouldBe expected
    }

}
